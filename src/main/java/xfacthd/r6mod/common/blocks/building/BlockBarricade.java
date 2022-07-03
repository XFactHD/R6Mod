package xfacthd.r6mod.common.blocks.building;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import xfacthd.r6mod.api.block.IHookable;
import xfacthd.r6mod.api.interaction.IDestructable;
import xfacthd.r6mod.common.blocks.BlockBase;
import xfacthd.r6mod.common.data.*;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.items.building.BlockItemBarricade;
import xfacthd.r6mod.common.util.data.PointManager;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("deprecation")
public class BlockBarricade extends BlockBase implements IDestructable, IHookable
{
    private static final Map<Direction, VoxelShape> SHAPES = new HashMap<>();
    private static final Map<Direction, VoxelShape> SHAPES_GLASS = new HashMap<>();
    private static final Map<Direction, VoxelShape> SHAPES_DOOR = new HashMap<>();

    static { createShapes(); }

    public BlockBarricade(String name, Properties props, ItemGroup group)
    {
        super(name, props, group);

        setDefaultState(getDefaultState()
                .with(PropertyHolder.ON_GLASS, false)
                .with(PropertyHolder.LARGE, false)
                .with(PropertyHolder.LEFT, false)
                .with(PropertyHolder.CENTER, false)
                .with(PropertyHolder.RIGHT, false)
                .with(PropertyHolder.DOOR, false)
        );
    }

    public BlockBarricade()
    {
        this("block_barricade",
                Properties.create(Material.WOOD)
                        .notSolid()
                        .hardnessAndResistance(2.0F, 3.0F)
                        .sound(SoundType.WOOD),
                ItemGroups.BUILDING);
    }

    @Override
    protected BlockItem createBlockItem(Item.Properties props) { return new BlockItemBarricade(this, props); }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR)
                .add(PropertyHolder.TOP)
                .add(PropertyHolder.ON_GLASS)
                .add(PropertyHolder.LARGE)
                .add(PropertyHolder.LEFT)
                .add(PropertyHolder.CENTER)
                .add(PropertyHolder.RIGHT)
                .add(PropertyHolder.DOOR);
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (state.getBlock() != newState.getBlock())
        {
            boolean top = state.get(PropertyHolder.TOP);

            BlockPos other = top ? pos.down() : pos.up();
            world.destroyBlock(other, false);

            if (state.get(PropertyHolder.LARGE))
            {
                Direction facing = state.get(PropertyHolder.FACING_HOR);

                if (state.get(PropertyHolder.LEFT))
                {
                    world.destroyBlock(pos.offset(facing.rotateY()), false);
                    world.destroyBlock(pos.offset(facing.rotateY(), 2), false);
                }
                else if (state.get(PropertyHolder.CENTER))
                {
                    world.destroyBlock(pos.offset(facing.rotateY()), false);
                    world.destroyBlock(pos.offset(facing.rotateYCCW()), false);
                }
                else if (state.get(PropertyHolder.RIGHT))
                {
                    world.destroyBlock(pos.offset(facing.rotateYCCW()), false);
                    world.destroyBlock(pos.offset(facing.rotateYCCW(), 2), false);
                }

                pos = top ? pos.down() : pos.up();

                if (state.get(PropertyHolder.LEFT))
                {
                    world.destroyBlock(pos.offset(facing.rotateY()), false);
                    world.destroyBlock(pos.offset(facing.rotateY(), 2), false);
                }
                else if (state.get(PropertyHolder.CENTER))
                {
                    world.destroyBlock(pos.offset(facing.rotateY()), false);
                    world.destroyBlock(pos.offset(facing.rotateYCCW()), false);
                }
                else if (state.get(PropertyHolder.RIGHT))
                {
                    world.destroyBlock(pos.offset(facing.rotateYCCW()), false);
                    world.destroyBlock(pos.offset(facing.rotateYCCW(), 2), false);
                }
            }
        }
        super.onReplaced(state, world, pos, newState, isMoving);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
    {
        Direction facing = state.get(PropertyHolder.FACING_HOR);
        boolean glass = state.get(PropertyHolder.ON_GLASS);
        boolean door = !state.get(PropertyHolder.TOP) && state.get(PropertyHolder.DOOR);
        return glass ? SHAPES_GLASS.get(facing) : (door ? SHAPES_DOOR.get(facing) : SHAPES.get(facing));
    }

    @Override
    public void destroy(World world, BlockPos pos, BlockState state, PlayerEntity player, EnumGadget source, Direction side)
    {
        PointManager.awardGadgetDestroyed(EnumGadget.BARRICADE, player);
        onReplaced(state, world, pos, Blocks.AIR.getDefaultState(), false);
    }

    @Override
    public boolean isSideSolid(World world, BlockState state, BlockPos pos, Direction side)
    {
        Direction facing = state.get(PropertyHolder.FACING_HOR);
        return side == facing || side == facing.getOpposite();
    }

    @Override
    public boolean canHook(World world, BlockPos pos, BlockState state, Direction side)
    {
        if (state.get(PropertyHolder.DOOR)) { return false; }

        Direction facing = state.get(PropertyHolder.FACING_HOR);
        if (side != facing && side != facing.getOpposite()) { return false; }

        if (state.get(PropertyHolder.LARGE)) { return state.get(PropertyHolder.CENTER); }

        return true;
    }

    @Override
    public Vector3d getHookTarget(World world, BlockPos pos, BlockState state, Direction side)
    {
        if (state.get(PropertyHolder.DOOR)) { return null; }
        Direction facing = state.get(PropertyHolder.FACING_HOR);
        if (side != facing && side != facing.getOpposite()) { return null; }
        if (state.get(PropertyHolder.LARGE) && !state.get(PropertyHolder.CENTER)) { return null; }

        Vector3i sideVec = side.getDirectionVec();
        Vector3d vec = Vector3d.copyCenteredHorizontally(pos).add(new Vector3d(sideVec.getX(), sideVec.getY(), sideVec.getZ()).mul(.5/16D, .5/16D, .5/16D));
        if (!state.get(PropertyHolder.TOP)) { vec = vec.add(0, 1, 0); }

        return vec;
    }

    @Override
    public void onHookImpact(World world, BlockPos pos, BlockState state)
    {
        if (state.get(PropertyHolder.ON_GLASS))
        {
            world.setBlockState(pos, state.with(PropertyHolder.ON_GLASS, false));

            BlockPos otherPos = state.get(PropertyHolder.TOP) ? pos : pos;
            world.setBlockState(otherPos, world.getBlockState(otherPos).with(PropertyHolder.ON_GLASS, false));
        }
    }

    @Override
    public void onPlayerImpact(World world, BlockPos pos, BlockState state)
    {
        world.destroyBlock(pos, false);

        BlockPos otherPos = state.get(PropertyHolder.TOP) ? pos : pos;
        world.destroyBlock(otherPos, false);
    }

    @Override
    public HookableType getHookableType() { return HookableType.WINDOW; }

    public void onRemovedByCrowbar(World world, BlockPos pos) { }

    private static void createShapes()
    {
        SHAPES.put(Direction.NORTH, VoxelShapes.create(    0D, 0D, 7D/16D,     1D, 1D, 9D/16D));
        SHAPES.put(Direction.EAST,  VoxelShapes.create(7D/16D, 0D,     0D, 9D/16D, 1D,     1D));
        SHAPES.put(Direction.SOUTH, VoxelShapes.create(    0D, 0D, 7D/16D,     1D, 1D, 9D/16D));
        SHAPES.put(Direction.WEST,  VoxelShapes.create(7D/16D, 0D,     0D, 9D/16D, 1D,     1D));

        SHAPES_GLASS.put(Direction.NORTH, VoxelShapes.create(    0D, 0D, 7D/16D,      1D, 1D, 11D/16D));
        SHAPES_GLASS.put(Direction.EAST,  VoxelShapes.create(5D/16D, 0D,     0D,  9D/16D, 1D,      1D));
        SHAPES_GLASS.put(Direction.SOUTH, VoxelShapes.create(    0D, 0D, 5D/16D,      1D, 1D,  9D/16D));
        SHAPES_GLASS.put(Direction.WEST,  VoxelShapes.create(7D/16D, 0D,     0D, 11D/16D, 1D,      1D));

        SHAPES_DOOR.put(Direction.NORTH, VoxelShapes.create(    0D, 3D/16D, 7D/16D,     1D, 1D, 9D/16D));
        SHAPES_DOOR.put(Direction.EAST,  VoxelShapes.create(7D/16D, 3D/16D,     0D, 9D/16D, 1D,     1D));
        SHAPES_DOOR.put(Direction.SOUTH, VoxelShapes.create(    0D, 3D/16D, 7D/16D,     1D, 1D, 9D/16D));
        SHAPES_DOOR.put(Direction.WEST,  VoxelShapes.create(7D/16D, 3D/16D,     0D, 9D/16D, 1D,     1D));
    }
}