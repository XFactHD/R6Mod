package xfacthd.r6mod.common.blocks.gadgets;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import xfacthd.r6mod.common.blocks.BlockGadget;
import xfacthd.r6mod.common.data.PropertyHolder;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.tileentities.gadgets.TileEntityWelcomeMat;

@SuppressWarnings("deprecation")
public class BlockWelcomeMat extends BlockGadget
{
    private static final VoxelShape SHAPE_N = makeCuboidShape(0, 0, 3, 16, 1, 13);
    private static final VoxelShape SHAPE_E = makeCuboidShape(3, 0, 0, 13, 1, 16);

    public BlockWelcomeMat()
    {
        super("block_welcome_mat",
                Properties.create(Material.IRON)
                        .notSolid()
                        .hardnessAndResistance(2F, 3F),
                EnumGadget.WELCOME_MAT);

        setDefaultState(getDefaultState().with(PropertyHolder.TRIGGERED, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_NE, PropertyHolder.TRIGGERED);
    }

    @Override
    protected BlockState getStateForPlacementOwnable(BlockItemUseContext context)
    {
        Direction facing = context.getPlacementHorizontalFacing();
        if (facing == Direction.SOUTH || facing == Direction.WEST) { facing = facing.getOpposite(); }
        return getDefaultState().with(PropertyHolder.FACING_NE, facing);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
    {
        if (state.get(PropertyHolder.TRIGGERED)) { return; }

        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityWelcomeMat)
        {
            if (((TileEntityWelcomeMat)te).shouldTrap(entity))
            {
                ((TileEntityWelcomeMat)te).trapEntity(entity);
                world.setBlockState(pos, state.with(PropertyHolder.TRIGGERED, true));
            }
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
    {
        return state.get(PropertyHolder.FACING_NE) == Direction.NORTH ? SHAPE_N : SHAPE_E;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new TileEntityWelcomeMat(); }
}