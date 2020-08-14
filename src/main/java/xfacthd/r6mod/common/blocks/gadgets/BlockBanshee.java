package xfacthd.r6mod.common.blocks.gadgets;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.IBlockReader;
import xfacthd.r6mod.common.blocks.BlockGadget;
import xfacthd.r6mod.common.data.PropertyHolder;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.tileentities.gadgets.TileEntityBanshee;
import xfacthd.r6mod.common.util.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@SuppressWarnings("deprecation")
public class BlockBanshee extends BlockGadget
{
    private static final Map<Direction, VoxelShape> SHAPES = createShapes();

    public BlockBanshee()
    {
        super("block_banshee",
                Properties.create(Material.IRON)
                        .notSolid()
                        .hardnessAndResistance(5F, 6F),
                EnumGadget.BANSHEE);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_NOT_DOWN);
    }

    @Override
    protected BlockState getStateForPlacementOwnable(BlockItemUseContext context)
    {
        if (context.getFace() == Direction.DOWN) { return null; }

        BlockPos posUnder = context.getPos().offset(context.getFace().getOpposite());
        if (!context.getWorld().getBlockState(posUnder).isSolid()) { return null; }

        return getDefaultState().with(PropertyHolder.FACING_NOT_DOWN, context.getFace());
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
    {
        return SHAPES.get(state.get(PropertyHolder.FACING_NOT_DOWN));
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new TileEntityBanshee(); }

    private static Map<Direction, VoxelShape> createShapes()
    {
        Map<Direction, VoxelShape> shapes = new HashMap<>();

        VoxelShape shape = Stream.of(
                Block.makeCuboidShape( 1.00, 0.0,  1.00, 15.00, 1.0, 15.00),
                Block.makeCuboidShape( 4.00, 1.0,  2.00, 12.00, 3.5,  3.50),
                Block.makeCuboidShape( 4.00, 1.0, 12.50, 12.00, 3.5, 14.00),
                Block.makeCuboidShape( 2.00, 1.0,  4.00,  3.50, 3.5, 12.00),
                Block.makeCuboidShape(12.50, 1.0,  4.00, 14.00, 3.5, 12.00),
                Block.makeCuboidShape( 7.00, 1.0,  4.00,  9.00, 2.0,  7.00),
                Block.makeCuboidShape( 7.00, 1.0,  9.00,  9.00, 2.0, 12.00),
                Block.makeCuboidShape( 4.00, 1.0,  4.00,  7.00, 2.0, 12.00),
                Block.makeCuboidShape( 9.00, 1.0,  4.00, 12.00, 2.0, 12.00),
                Block.makeCuboidShape( 6.75, 2.0,  4.25,  9.25, 3.0,  6.75),
                Block.makeCuboidShape( 6.75, 2.0,  9.25,  9.25, 3.0, 11.75),
                Block.makeCuboidShape( 4.25, 2.0,  4.25,  6.75, 3.0, 11.75),
                Block.makeCuboidShape( 9.25, 2.0,  4.25, 11.75, 3.0, 11.75),
                Block.makeCuboidShape( 6.50, 3.0,  4.50,  9.50, 4.0,  6.50),
                Block.makeCuboidShape( 6.50, 3.0,  9.50,  9.50, 4.0, 11.50),
                Block.makeCuboidShape( 4.50, 3.0,  4.50,  6.50, 4.0, 11.50),
                Block.makeCuboidShape( 9.50, 3.0,  4.50, 11.50, 4.0, 11.50)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();
        shapes.put(Direction.UP, shape);

        shape = Stream.of(
                Block.makeCuboidShape(1, 1, 0, 15, 15, 1),
                Block.makeCuboidShape(4, 12.5, 1, 12, 14, 3.5),
                Block.makeCuboidShape(4, 2, 1, 12, 3.5, 3.5),
                Block.makeCuboidShape(2, 4, 1, 3.5, 12, 3.5),
                Block.makeCuboidShape(12.5, 4, 1, 14, 12, 3.5),
                Block.makeCuboidShape(7, 9, 1, 9, 12, 2),
                Block.makeCuboidShape(7, 4, 1, 9, 7, 2),
                Block.makeCuboidShape(4, 4, 1, 7, 12, 2),
                Block.makeCuboidShape(9, 4, 1, 12, 12, 2),
                Block.makeCuboidShape(6.75, 9.25, 2, 9.25, 11.75, 3),
                Block.makeCuboidShape(6.75, 4.25, 2, 9.25, 6.75, 3),
                Block.makeCuboidShape(4.25, 4.25, 2, 6.75, 11.75, 3),
                Block.makeCuboidShape(9.25, 4.25, 2, 11.75, 11.75, 3),
                Block.makeCuboidShape(6.5, 9.5, 3, 9.5, 11.5, 4),
                Block.makeCuboidShape(6.5, 4.5, 3, 9.5, 6.5, 4),
                Block.makeCuboidShape(4.5, 4.5, 3, 6.5, 11.5, 4),
                Block.makeCuboidShape(9.5, 4.5, 3, 11.5, 11.5, 4)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();
        shapes.put(Direction.NORTH, Utils.rotateShape(Direction.SOUTH, Direction.NORTH, shape));
        shapes.put(Direction.EAST,  Utils.rotateShape(Direction.SOUTH, Direction.EAST, shape));
        shapes.put(Direction.SOUTH, shape);
        shapes.put(Direction.WEST,  Utils.rotateShape(Direction.SOUTH, Direction.WEST, shape));

        return shapes;
    }
}