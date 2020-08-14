package xfacthd.r6mod.common.items.building;

import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.api.interaction.IPlacementTime;
import xfacthd.r6mod.common.R6Content;
import xfacthd.r6mod.common.blocks.building.BlockWall;
import xfacthd.r6mod.common.data.*;
import xfacthd.r6mod.common.data.blockdata.WallMaterial;
import xfacthd.r6mod.common.data.blockdata.WallSegment;

import java.util.Map;

public class ItemReinforcement extends Item implements IPlacementTime
{
    private static final TranslationTextComponent PLACE_MSG = new TranslationTextComponent("msg.r6mod.place.reinforcement");

    public ItemReinforcement()
    {
        super(new Properties().group(ItemGroups.BUILDING));
        setRegistryName(R6Mod.MODID, "item_reinforcement");
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context)
    {
        BlockItemUseContext blockContext = new BlockItemUseContext(context);
        if (!canPlace(blockContext)) { return ActionResultType.FAIL; }
        if (!context.getWorld().isAirBlock(blockContext.getPos())) { return ActionResultType.FAIL; }

        ItemStack stack = context.getItem();
        if (!stack.hasTag()) { stack.setTag(new CompoundNBT()); }

        int placeTime = getCurrentTime(context.getWorld(), context.getItem());
        if (placeTime >= getPlacementTime())
        {
            stack.setTag(null); //Remove tag to make the item stackable again
            return tryPlace(new BlockItemUseContext(context));
        }
        else
        {
            if (placeTime == 0)
            {
                //noinspection ConstantConditions
                stack.getTag().putLong("place_start", context.getWorld().getGameTime());
            }
            //noinspection ConstantConditions
            stack.getTag().putLong("place_last", context.getWorld().getGameTime());
            return ActionResultType.FAIL;
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean isSelected)
    {
        if (stack.hasTag())
        {
            //noinspection ConstantConditions
            long lastInteract = stack.getTag().getLong("place_last");
            if (world.getGameTime() - lastInteract > 5)
            {
                stack.setTag(null);
            }
        }
    }

    private boolean canPlace(BlockItemUseContext context)
    {
        return canPlaceWallReinforcement(context) || canPlaceFloorReinforcement(context);
    }

    public ActionResultType tryPlace(BlockItemUseContext context)
    {
        if (canPlaceWallReinforcement(context))
        {
            return tryPlaceWallReinforcement(context);
        }

        if (canPlaceFloorReinforcement(context))
        {
            return tryPlaceFloorReinforcement(context);
        }

        return ActionResultType.FAIL;
    }

    /*
     * Wall Reinforcement helpers
     */

    private boolean canPlaceWallReinforcement(BlockItemUseContext context)
    {
        if (context.getFace().getHorizontalIndex() == -1) { return false; }

        Direction face = context.getFace();
        BlockPos pos = context.getPos().offset(face.getOpposite()); //Must be offset, pos given is the "normal" place position
        BlockState state = context.getWorld().getBlockState(pos);
        if (!(state.getBlock() instanceof BlockWall)) { return false; }

        Tuple<BlockPos, BlockPos> base = getBasePositions(pos, face.getOpposite(), getSubHit(context.getHitVec()));

        World world = context.getWorld();

        //Check for full 2*3 wall
        if (isNoWall(world, base.getA(), base.getB())) { return false; }
        if (isNoWall(world, base.getA().up(), base.getB().up())) { return false; }
        if (isNoWall(world, base.getA().up(2), base.getB().up(2))) { return false; }

        //Block below must not be a wall block => must click on bottom block
        return isNoWall(world, base.getA().down(), base.getB().down());
    }

    private ActionResultType tryPlaceWallReinforcement(BlockItemUseContext context)
    {
        PlayerEntity player = context.getPlayer();
        if (player == null || !canPlaceWallReinforcement(context)) { return ActionResultType.FAIL; }

        World world = context.getWorld();

        Direction face = context.getFace();
        Direction facing = face.getOpposite();
        BlockPos pos = context.getPos().offset(facing);
        Tuple<BlockPos, BlockPos> base = getBasePositions(pos, facing, getSubHit(context.getHitVec()));
        BlockState state = R6Content.blockReinforcement.getDefaultState().with(PropertyHolder.FACING_HOR, facing);

        if (!world.isRemote())
        {
            placeBlock(world, base.getA(), state, WallSegment.BOTTOM_LEFT);
            placeBlock(world, base.getB(), state, WallSegment.BOTTOM_RIGHT);

            placeBlock(world, base.getA().up(), state, WallSegment.CENTER_LEFT);
            placeBlock(world, base.getB().up(), state, WallSegment.CENTER_RIGHT);

            placeBlock(world, base.getA().up(2), state, WallSegment.TOP_LEFT);
            placeBlock(world, base.getB().up(2), state, WallSegment.TOP_RIGHT);
        }

        SoundType type = state.getSoundType(world, base.getA(), player);
        SoundEvent event = state.getSoundType(world, base.getA(), player).getPlaceSound();
        Vec3d soundPos = getFrontCenteredSound(base.getA(), facing);
        world.playSound(player, soundPos.getX(), soundPos.getY(), soundPos.getZ(), event, SoundCategory.BLOCKS, (type.getVolume() + 1.0F) / 2.0F, type.getPitch() * 0.8F);

        return ActionResultType.SUCCESS;
    }

    private static Tuple<BlockPos, BlockPos> getBasePositions(BlockPos pos, Direction facing, Vec3d subHit)
    {
        boolean left = isLeft(subHit, facing);
        BlockPos adjPos = pos.offset(getOffset(facing, subHit));

        return left ? new Tuple<>(pos, adjPos) : new Tuple<>(adjPos, pos);
    }

    private static void placeBlock(World world, BlockPos pos, BlockState state, WallSegment segment)
    {
        WallMaterial material = ((BlockWall)world.getBlockState(pos).getBlock()).getMaterial();
        world.setBlockState(pos, state.with(PropertyHolder.WALL_SEGMENT, segment).with(PropertyHolder.MATERIAL, material));
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
        if (facing.getHorizontalIndex() == -1) { throw new IllegalArgumentException("Facing has to be horizontal"); } //Can only be placed horizontally
        return isLeft(subHit, facing) ? facing.rotateY() : facing.rotateYCCW();
    }

    private static boolean isNoWall(World world, BlockPos pos, BlockPos adjPos)
    {
        return !isWall(world.getBlockState(pos), world.getBlockState(adjPos));
    }

    private static boolean isWall(BlockState state, BlockState adjState)
    {
        if (!(state.getBlock() instanceof BlockWall) || !(adjState.getBlock() instanceof BlockWall)) { return false; }
        return !((BlockWall)state.getBlock()).isBarred() && !((BlockWall)adjState.getBlock()).isBarred();
    }

    /*
     * Floor Reinforcement helpers
     */

    private boolean canPlaceFloorReinforcement(BlockItemUseContext context)
    {
        if (context.getFace() != Direction.UP) { return false; }

        World world = context.getWorld();
        BlockPos pos = context.getPos().down(); //Must be offset, pos given is the "normal" place position
        BlockState state = world.getBlockState(pos);
        return state.getBlock() == R6Content.blockDropHatch;
    }

    public ActionResultType tryPlaceFloorReinforcement(BlockItemUseContext context)
    {
        if (!canPlaceFloorReinforcement(context)) { return ActionResultType.FAIL; }

        World world = context.getWorld();
        BlockPos pos = context.getPos().down(); //Must be offset, pos given is the "normal" place position
        BlockState state = world.getBlockState(pos);
        WallSegment segment = state.get(PropertyHolder.SQUARE_SEGMENT);

        Map<WallSegment, BlockPos> posMap = segment.squarePositions(pos);
        for (WallSegment seg : posMap.keySet())
        {
            BlockPos blockPos = posMap.get(seg);
            BlockState blockState = R6Content.blockFloorReinforcement.getDefaultState().with(PropertyHolder.SQUARE_SEGMENT, seg);
            world.setBlockState(blockPos, blockState);
            R6Content.blockFloorReinforcement.onBlockPlacedBy(world, blockPos, blockState, context.getPlayer(), context.getItem());
        }

        PlayerEntity player = context.getPlayer();
        SoundType type = state.getSoundType(world, pos, player);
        SoundEvent event = state.getSoundType(world, pos, player).getPlaceSound();
        BlockPos soundPos = posMap.get(WallSegment.BOTTOM_RIGHT);
        world.playSound(player, soundPos.getX(), soundPos.getY(), soundPos.getZ(), event, SoundCategory.BLOCKS, (type.getVolume() + 1.0F) / 2.0F, type.getPitch() * 0.8F);

        return ActionResultType.SUCCESS;
    }

    /*
     * Generic helpers
     */

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

    /*
     * IPlacementTime
     */

    @Override
    public int getCurrentTime(World world, ItemStack stack)
    {
        //noinspection ConstantConditions
        long startInteract = stack.hasTag() ? stack.getTag().getLong("place_start") : 0;
        return startInteract != 0 ? (int)(world.getGameTime() - startInteract) : 0;
    }

    @Override
    public int getPlacementTime() { return 20; }

    @Override
    public void applySonicBurst(World world, ItemStack stack, int cooldown)
    {
        if (!stack.hasTag()) { return; } //If the stack has no tag, it definitely isn't being used

        if (getCurrentTime(world, stack) != 0) //Item in use
        {
            //noinspection ConstantConditions
            stack.getTag().putLong("place_start", world.getGameTime() + cooldown);
        }
    }

    @Override
    public TranslationTextComponent getPlaceMessage() { return PLACE_MSG; }
}