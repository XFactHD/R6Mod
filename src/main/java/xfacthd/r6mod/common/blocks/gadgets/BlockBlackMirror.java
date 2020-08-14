package xfacthd.r6mod.common.blocks.gadgets;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import xfacthd.r6mod.api.interaction.IDestructable;
import xfacthd.r6mod.common.blocks.BlockGadget;
import xfacthd.r6mod.common.data.PropertyHolder;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.items.gadgets.BlockItemBlackMirror;
import xfacthd.r6mod.common.tileentities.gadgets.TileEntityBlackMirror;
import xfacthd.r6mod.common.util.Utils;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Stream;

@SuppressWarnings("deprecation")
public class BlockBlackMirror extends BlockGadget implements IDestructable
{
    private static final Map<Direction, VoxelShape> SHAPES_LEFT = new HashMap<>();
    private static final Map<Direction, VoxelShape> SHAPES_RIGHT = new HashMap<>();
    private static final Map<Direction, VoxelShape> SHAPES_BROKEN_LEFT = new HashMap<>();
    private static final Map<Direction, VoxelShape> SHAPES_BROKEN_RIGHT = new HashMap<>();

    static { createShapes(); }

    public BlockBlackMirror()
    {
        super("block_black_mirror",
                Properties.create(Material.IRON)
                        .hardnessAndResistance(-1.0F, 3600000.0F)
                        .notSolid(),
                EnumGadget.BLACK_MIRROR);

        setDefaultState(getDefaultState()
                .with(PropertyHolder.RIGHT, false)
                .with(PropertyHolder.DESTROYED, false)
                .with(PropertyHolder.OPEN, false)
                .with(PropertyHolder.REINFORCED, false)
        );
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.RIGHT, PropertyHolder.DESTROYED, PropertyHolder.OPEN, PropertyHolder.REINFORCED, PropertyHolder.MATERIAL);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult raytrace)
    {
        //if (state.get(PropertyHolder.OPEN)) //For testing stuff without rebuilding the mirror
        //{
        //    world.setBlockState(pos, state.with(PropertyHolder.OPEN, false).with(PropertyHolder.DESTROYED, false));
        //    return ActionResultType.SUCCESS;
        //}

        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityBlackMirror)
        {
            return ((TileEntityBlackMirror)te).destroyCanister(raytrace.getHitVec()) ? ActionResultType.SUCCESS : ActionResultType.FAIL;
        }
        return ActionResultType.FAIL;
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (state.getBlock() != newState.getBlock())
        {
            Direction facing = state.get(PropertyHolder.FACING_HOR);
            Direction offset = state.get(PropertyHolder.RIGHT) ? facing.rotateYCCW() : facing.rotateY();
            BlockPos adjPos = pos.offset(offset);
            world.destroyBlock(adjPos, false);
        }
        super.onReplaced(state, world, pos, newState, isMoving);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
    {
        Direction facing = state.get(PropertyHolder.FACING_HOR);
        if (state.get(PropertyHolder.RIGHT))
        {
            return state.get(PropertyHolder.OPEN) ? SHAPES_BROKEN_RIGHT.get(facing) : SHAPES_RIGHT.get(facing);
        }
        else
        {
            return state.get(PropertyHolder.OPEN) ? SHAPES_BROKEN_LEFT.get(facing) : SHAPES_LEFT.get(facing);
        }
    }

    @Override
    public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        Direction facing = state.get(PropertyHolder.FACING_HOR);
        return state.get(PropertyHolder.RIGHT) ? SHAPES_BROKEN_RIGHT.get(facing) : SHAPES_BROKEN_LEFT.get(facing);
    }

    @Override
    protected BlockItem createBlockItem(Item.Properties props) { return new BlockItemBlackMirror(this, props); }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new TileEntityBlackMirror(); }

    @Override
    public void destroy(World world, BlockPos pos, BlockState state, PlayerEntity player, EnumGadget source, @Nullable Direction side)
    {
        if (!state.get(PropertyHolder.REINFORCED) || source.canHardDestruct())
        {
            Direction facing = state.get(PropertyHolder.FACING_HOR);
            Direction offset = state.get(PropertyHolder.RIGHT) ? facing.rotateYCCW() : facing.rotateY();
            BlockPos adjPos = pos.offset(offset);
            world.destroyBlock(adjPos, false);

            super.destroy(world, pos, state, player, source, side);
        }
    }

    @Override
    public boolean isSideSolid(World world, BlockState state, BlockPos pos, Direction side)
    {
        Direction facing = state.get(PropertyHolder.FACING_HOR);
        return side == facing || side == facing.getOpposite();
    }

    private static void createShapes()
    {
        Optional<VoxelShape> shapeLeft = Stream.of(
                Block.makeCuboidShape( 2.0,  2.0, 13.5, 16.0, 14.0, 14.5),
                Block.makeCuboidShape( 2.5,  0.0,  5.0,  5.5,  2.0, 11.0),
                Block.makeCuboidShape(10.5,  0.0,  5.0, 13.5,  2.0, 11.0),
                Block.makeCuboidShape( 2.5, 14.0,  5.0,  5.5, 16.0, 11.0),
                Block.makeCuboidShape(10.5, 14.0,  5.0, 13.5, 16.0, 11.0),
                Block.makeCuboidShape( 0.0, 14.0,  4.0, 16.0, 16.0,  5.0),
                Block.makeCuboidShape( 0.0, 14.0, 11.0, 16.0, 16.0, 12.0),
                Block.makeCuboidShape( 0.0,  0.0,  4.0, 16.0,  2.0,  5.0),
                Block.makeCuboidShape( 0.0,  0.0, 11.0, 16.0,  2.0, 12.0),
                Block.makeCuboidShape( 0.0,  2.0,  4.0,  2.0, 14.0,  5.0),
                Block.makeCuboidShape( 0.0,  2.0, 11.0,  2.0, 14.0, 12.0),
                Block.makeCuboidShape( 0.0,  0.0, 12.0, 16.0,  2.0, 16.0),
                Block.makeCuboidShape( 0.0, 14.0, 12.0, 16.0, 16.0, 16.0),
                Block.makeCuboidShape( 0.0,  2.0, 12.0,  2.0, 14.0, 16.0),
                Block.makeCuboidShape( 1.6, 14.0, 16.0, 16.0, 14.4, 16.4),
                Block.makeCuboidShape( 1.6,  1.6, 16.0, 13.0,  2.0, 16.4),
                Block.makeCuboidShape( 1.6,  2.0, 16.0,  2.0, 14.0, 16.4),
                Block.makeCuboidShape( 0.9,  1.7, 16.1,  1.1,  4.6, 16.3),
                Block.makeCuboidShape( 1.1,  1.7, 16.1,  1.6,  1.9, 16.3),
                Block.makeCuboidShape(13.0,  0.2, 16.0, 16.0,  1.8, 17.6),
                Block.makeCuboidShape(10.9,  0.7, 16.5, 13.0,  1.3, 17.1),
                Block.makeCuboidShape(11.0,  0.2, 17.0, 12.6,  1.8, 17.6),
                Block.makeCuboidShape(10.5,  0.9, 16.6, 11.0,  1.1, 16.8),
                Block.makeCuboidShape(10.3,  0.9, 16.1, 10.5,  1.1, 16.8),
                Block.makeCuboidShape(10.3,  1.1, 16.1, 10.5,  1.6, 16.3),
                Block.makeCuboidShape(14.5, 14.4, 16.0, 16.0, 16.0, 16.2)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR));

        Optional<VoxelShape> shapeRight = Stream.of(
                Block.makeCuboidShape( 0.0,  2.0, 13.5, 14.0, 14.0, 14.5),
                Block.makeCuboidShape( 2.5,  0.0,  5.0,  5.5,  2.0, 11.0),
                Block.makeCuboidShape(10.5,  0.0,  5.0, 13.5,  2.0, 11.0),
                Block.makeCuboidShape( 2.5, 14.0,  5.0,  5.5, 16.0, 11.0),
                Block.makeCuboidShape(10.5, 14.0,  5.0, 13.5, 16.0, 11.0),
                Block.makeCuboidShape( 0.0, 14.0,  4.0, 16.0, 16.0,  5.0),
                Block.makeCuboidShape( 0.0, 14.0, 11.0, 16.0, 16.0, 12.0),
                Block.makeCuboidShape( 0.0,  0.0,  4.0, 16.0,  2.0,  5.0),
                Block.makeCuboidShape( 0.0,  0.0, 11.0, 16.0,  2.0, 12.0),
                Block.makeCuboidShape(14.0,  2.0,  4.0, 16.0, 14.0,  5.0),
                Block.makeCuboidShape(14.0,  2.0, 11.0, 16.0, 14.0, 12.0),
                Block.makeCuboidShape( 0.0,  0.0, 12.0, 16.0,  2.0, 16.0),
                Block.makeCuboidShape( 0.0, 14.0, 12.0, 16.0, 16.0, 16.0),
                Block.makeCuboidShape(14.0,  2.0, 12.0, 16.0, 14.0, 16.0),
                Block.makeCuboidShape( 0.0, 14.0, 16.0, 14.4, 14.4, 16.4),
                Block.makeCuboidShape( 3.8,  1.6, 16.0, 14.4,  2.0, 16.4),
                Block.makeCuboidShape(14.0,  2.0, 16.0, 14.4, 14.0, 16.4),
                Block.makeCuboidShape( 0.0,  0.2, 16.0,  3.0,  1.8, 17.6),
                Block.makeCuboidShape( 0.0, 14.4, 16.0,  1.5, 16.0, 16.2)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR));

        Optional<VoxelShape> shapeLeftBroken = Stream.of(
                Block.makeCuboidShape( 2.5,  0.0,  5.0,  5.5,  2.0, 11.0),
                Block.makeCuboidShape(10.5,  0.0,  5.0, 13.5,  2.0, 11.0),
                Block.makeCuboidShape( 2.5, 14.0,  5.0,  5.5, 16.0, 11.0),
                Block.makeCuboidShape(10.5, 14.0,  5.0, 13.5, 16.0, 11.0),
                Block.makeCuboidShape( 0.0, 14.0,  4.0, 16.0, 16.0,  5.0),
                Block.makeCuboidShape( 0.0, 14.0, 11.0, 16.0, 16.0, 12.0),
                Block.makeCuboidShape( 0.0,  0.0,  4.0, 16.0,  2.0,  5.0),
                Block.makeCuboidShape( 0.0,  0.0, 11.0, 16.0,  2.0, 12.0),
                Block.makeCuboidShape( 0.0,  2.0,  4.0,  2.0, 14.0,  5.0),
                Block.makeCuboidShape( 0.0,  2.0, 11.0,  2.0, 14.0, 12.0),
                Block.makeCuboidShape( 0.0,  0.0, 12.0, 16.0,  2.0, 16.0),
                Block.makeCuboidShape( 0.0, 14.0, 12.0, 16.0, 16.0, 16.0),
                Block.makeCuboidShape( 0.0,  2.0, 12.0,  2.0, 14.0, 16.0),
                //Block.makeCuboidShape( 0.6,  3.7, 16.1,  1.4,  4.5, 17.8),
                Block.makeCuboidShape( 1.6, 14.0, 16.0, 16.0, 14.4, 16.4),
                Block.makeCuboidShape( 1.6,  1.6, 16.0, 13.0,  2.0, 16.4),
                Block.makeCuboidShape( 1.6,  2.0, 16.0,  2.0, 14.0, 16.4),
                Block.makeCuboidShape( 0.9,  1.7, 16.1,  1.1,  4.6, 16.3),
                Block.makeCuboidShape( 1.1,  1.7, 16.1,  1.6,  1.9, 16.3),
                Block.makeCuboidShape(13.0,  0.2, 16.0, 16.0,  1.8, 17.6),
                Block.makeCuboidShape(10.9,  0.7, 16.5, 13.0,  1.3, 17.1),
                Block.makeCuboidShape(11.0,  0.2, 17.0, 12.6,  1.8, 17.6),
                Block.makeCuboidShape(10.5,  0.9, 16.6, 11.0,  1.1, 16.8),
                Block.makeCuboidShape(10.3,  0.9, 16.1, 10.5,  1.1, 16.8),
                Block.makeCuboidShape(10.3,  1.1, 16.1, 10.5,  1.6, 16.3),
                Block.makeCuboidShape(14.5, 14.4, 16.0, 16.0, 16.0, 16.2)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR));

        Optional<VoxelShape> shapeRightBroken = Stream.of(
                Block.makeCuboidShape( 2.5,  0.0,  5.0,  5.5,  2.0, 11.0),
                Block.makeCuboidShape(10.5,  0.0,  5.0, 13.5,  2.0, 11.0),
                Block.makeCuboidShape( 2.5, 14.0,  5.0,  5.5, 16.0, 11.0),
                Block.makeCuboidShape(10.5, 14.0,  5.0, 13.5, 16.0, 11.0),
                Block.makeCuboidShape( 0.0, 14.0,  4.0, 16.0, 16.0,  5.0),
                Block.makeCuboidShape( 0.0, 14.0, 11.0, 16.0, 16.0, 12.0),
                Block.makeCuboidShape( 0.0,  0.0,  4.0, 16.0,  2.0,  5.0),
                Block.makeCuboidShape( 0.0,  0.0, 11.0, 16.0,  2.0, 12.0),
                Block.makeCuboidShape(14.0,  2.0,  4.0, 16.0, 14.0,  5.0),
                Block.makeCuboidShape(14.0,  2.0, 11.0, 16.0, 14.0, 12.0),
                Block.makeCuboidShape( 0.0,  0.0, 12.0, 16.0,  2.0, 16.0),
                Block.makeCuboidShape( 0.0, 14.0, 12.0, 16.0, 16.0, 16.0),
                Block.makeCuboidShape(14.0,  2.0, 12.0, 16.0, 14.0, 16.0),
                Block.makeCuboidShape( 0.0, 14.0, 16.0, 14.4, 14.4, 16.4),
                Block.makeCuboidShape( 3.8,  1.6, 16.0, 14.4,  2.0, 16.4),
                Block.makeCuboidShape(14.0,  2.0, 16.0, 14.4, 14.0, 16.4),
                Block.makeCuboidShape( 0.0,  0.2, 16.0,  3.0,  1.8, 17.6),
                Block.makeCuboidShape( 0.0, 14.4, 16.0,  1.5, 16.0, 16.2)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR));

        for (Direction facing : Direction.Plane.HORIZONTAL)
        {
            SHAPES_LEFT.put(facing, Utils.rotateShape(Direction.NORTH, facing, shapeLeft.get()));
            SHAPES_RIGHT.put(facing, Utils.rotateShape(Direction.NORTH, facing, shapeRight.get()));
            SHAPES_BROKEN_LEFT.put(facing, Utils.rotateShape(Direction.NORTH, facing, shapeLeftBroken.get()));
            SHAPES_BROKEN_RIGHT.put(facing, Utils.rotateShape(Direction.NORTH, facing, shapeRightBroken.get()));
        }
    }
}