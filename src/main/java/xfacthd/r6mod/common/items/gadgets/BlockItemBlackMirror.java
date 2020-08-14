package xfacthd.r6mod.common.items.gadgets;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import xfacthd.r6mod.common.blocks.building.BlockReinforcement;
import xfacthd.r6mod.common.blocks.building.BlockWall;
import xfacthd.r6mod.common.blocks.gadgets.BlockBlackMirror;
import xfacthd.r6mod.common.data.PropertyHolder;
import xfacthd.r6mod.common.data.blockdata.WallMaterial;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.items.BlockItemGadget;

public class BlockItemBlackMirror extends BlockItemGadget
{
    public BlockItemBlackMirror(BlockBlackMirror block, Properties props)
    {
        super(block, props, EnumGadget.BLACK_MIRROR);
    }

    @Override
    protected boolean canPlace(BlockItemUseContext context, BlockState state)
    {
        return getPlacePositions(context) != null;
    }

    @Override
    public ActionResultType tryPlace(BlockItemUseContext context)
    {
        PlayerEntity player = context.getPlayer();
        Tuple<BlockPos, BlockPos> posTuple = getPlacePositions(context);
        if (player == null || posTuple == null) { return ActionResultType.FAIL; }

        Direction facing = context.getFace().getOpposite();
        boolean leftFirst = isLeft(getSubHit(context.getHitVec()), facing);

        BlockState state = getBlock().getDefaultState();
        state = state.with(PropertyHolder.FACING_HOR, facing);

        World world = context.getWorld();
        if (!world.isRemote())
        {
            BlockState wallLeft = world.getBlockState(posTuple.getA());
            BlockState wallRight = world.getBlockState(posTuple.getB());

            boolean reinforced = isReinforcement(wallLeft, wallRight);
            state = state.with(PropertyHolder.REINFORCED, reinforced);

            WallMaterial matLeft = reinforced ? wallLeft.get(PropertyHolder.MATERIAL) : ((BlockWall)wallLeft.getBlock()).getMaterial();
            WallMaterial matRight = reinforced ? wallRight.get(PropertyHolder.MATERIAL) : ((BlockWall)wallRight.getBlock()).getMaterial();

            BlockState stateLeft = state.with(PropertyHolder.RIGHT, !leftFirst).with(PropertyHolder.MATERIAL, matLeft);
            BlockState stateRight = state.with(PropertyHolder.RIGHT, leftFirst).with(PropertyHolder.MATERIAL, matRight);

            world.setBlockState(posTuple.getA(), stateLeft);
            world.setBlockState(posTuple.getB(), stateRight);

            stateLeft.getBlock().onBlockPlacedBy(world, posTuple.getA(), stateLeft, player, context.getItem());
            stateRight.getBlock().onBlockPlacedBy(world, posTuple.getB(), stateRight, player, context.getItem());

            context.getItem().shrink(1);
            player.inventory.markDirty();
        }

        SoundType type = state.getSoundType(world, posTuple.getA(), context.getPlayer());
        SoundEvent event = getPlaceSound(state, world, posTuple.getA(), player);
        Vec3d soundPos = getFrontCenteredSound(posTuple.getA(), facing);
        world.playSound(player, soundPos.getX(), soundPos.getY(), soundPos.getZ(), event, SoundCategory.BLOCKS, (type.getVolume() + 1.0F) / 2.0F, type.getPitch() * 0.8F);

        return ActionResultType.SUCCESS;
    }

    private static Tuple<BlockPos, BlockPos> getPlacePositions(BlockItemUseContext context)
    {
        Vec3d subHit = getSubHit(context.getHitVec());

        Direction facing = context.getFace().getOpposite();
        boolean left = isLeft(subHit, facing);
        Direction offset = getOffset(facing, subHit);

        //Offset is null if the face is not on the horizontal plane
        if (offset == null) { return null; }

        World world = context.getWorld();
        //Position needs to be offset in 'facing' direction because the BlockPos in 'context' is the normal placement position, not the clicked position
        BlockPos pos = context.getPos().offset(facing);
        BlockPos adjPos = pos.offset(offset);

        BlockState state = world.getBlockState(pos);
        BlockState adjState = world.getBlockState(adjPos);

        if (areBlocksValid(state, adjState, facing) && surrounded(world, left ? pos : adjPos, left ? adjPos : pos))
        {
            if (frontBackClear(world, pos, adjPos, facing))
            {
                return new Tuple<>(pos, adjPos);
            }
        }

        return null;
    }

    private static Vec3d getSubHit(Vec3d hitVec)
    {
        return hitVec.subtract((int)hitVec.getX(), (int)hitVec.getY(), (int)hitVec.getZ());
    }

    private static boolean isLeft(Vec3d subHit, Direction facing)
    {
        if (facing.getAxis() == Direction.Axis.X)
        {
            double z = Math.abs(subHit.getZ());
            return facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE ? z > .5 : z < .5;
        }
        else
        {
            return facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE ? subHit.getX() > .5 : subHit.getX() < .5;
        }
    }

    private static Direction getOffset(Direction facing, Vec3d subHit)
    {
        if (facing.getHorizontalIndex() == -1) { return null; } //Can only be placed horizontally
        return isLeft(subHit, facing) ? facing.rotateY() : facing.rotateYCCW();
    }

    private static boolean areBlocksValid(BlockState state, BlockState adjState, Direction facing)
    {
        boolean wall = isWall(state, adjState);
        boolean reinforcement = isReinforcement(state, adjState);
        if (!wall && !reinforcement) { return false; }

        if (reinforcement)
        {
            if (!state.get(PropertyHolder.WALL_SEGMENT).isCenter() || !adjState.get(PropertyHolder.WALL_SEGMENT).isCenter()) { return false; }

            return state.get(PropertyHolder.FACING_HOR) == facing && adjState.get(PropertyHolder.FACING_HOR) == facing;
        }
        else
        {
            if (((BlockWall)state.getBlock()).isBarred() || ((BlockWall)adjState.getBlock()).isBarred()) { return false; }

            return state.get(PropertyHolder.FACING_NE).getAxis() == facing.getAxis() &&
                    adjState.get(PropertyHolder.FACING_NE).getAxis() == facing.getAxis();
        }
    }

    private static boolean isReinforcement(BlockState state, BlockState adjState)
    {
        return state.getBlock() instanceof BlockReinforcement && adjState.getBlock() instanceof BlockReinforcement;
    }

    private static boolean isWall(BlockState state, BlockState adjState)
    {
        return state.getBlock() instanceof BlockWall && adjState.getBlock() instanceof BlockWall;
    }

    private static boolean surrounded(World world, BlockPos pos, BlockPos adjPos)
    {
        boolean reinforced = isReinforcement(world.getBlockState(pos), world.getBlockState(adjPos));
        if (reinforced)
        {
            return isReinforcement(world.getBlockState(pos.down()), world.getBlockState(adjPos.down()))
                    && isReinforcement(world.getBlockState(pos.up()), world.getBlockState(adjPos.up()));
        }
        else
        {
            return isWall(world.getBlockState(pos.down()), world.getBlockState(adjPos.down()))
                    && isWall(world.getBlockState(pos.up()), world.getBlockState(adjPos.up()));
        }
    }

    private static boolean frontBackClear(World world, BlockPos pos, BlockPos adjPos, Direction facing)
    {
        BlockPos[] posArr =
                {
                        pos.offset(facing),
                        pos.offset(facing.getOpposite()),
                        adjPos.offset(facing),
                        adjPos.offset(facing.getOpposite())
                };
        for (BlockPos posArround : posArr)
        {
            if (!world.getBlockState(posArround).isAir(world, posArround)) { return false; }
        }
        return true;
    }

    private static Vec3d getFrontCenteredSound(BlockPos pos, Direction facing)
    {
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        switch (facing)
        {
            case NORTH:
            {
                z++;
                x++;
                break;
            }
            case EAST:
            {
                z++;
                break;
            }
            case WEST:
            {
                x++;
                break;
            }
        }
        return new Vec3d(x, y, z);
    }
}