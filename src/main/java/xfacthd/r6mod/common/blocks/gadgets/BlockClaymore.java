package xfacthd.r6mod.common.blocks.gadgets;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;
import xfacthd.r6mod.common.blocks.BlockGadget;
import xfacthd.r6mod.common.data.PropertyHolder;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.tileentities.gadgets.TileEntityClaymore;

import java.util.HashMap;

public class BlockClaymore extends BlockGadget
{
    private static final HashMap<Direction, VoxelShape> VOXEL_SHAPES = createVoxelShapes();

    public BlockClaymore()
    {
        super("block_claymore",
                Properties.create(Material.IRON)
                .notSolid()
                .hardnessAndResistance(5.0F, 12.0F)
                .harvestTool(ToolType.PICKAXE)
                .sound(SoundType.METAL),
                EnumGadget.CLAYMORE);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR);
    }

    @Override
    public BlockState getStateForPlacementOwnable(BlockItemUseContext context)
    {
        if (context.getFace() != Direction.UP) { return null; }
        if (!context.getWorld().getBlockState(context.getPos().offset(Direction.DOWN)).isSolid()) { return null; }
        return getDefaultState().with(PropertyHolder.FACING_HOR, context.getPlacementHorizontalFacing());
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new TileEntityClaymore(); }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
    {
        return VOXEL_SHAPES.getOrDefault(state.get(PropertyHolder.FACING_HOR), VoxelShapes.fullCube());
    }

    private static HashMap<Direction, VoxelShape> createVoxelShapes()
    {
        HashMap<Direction, VoxelShape> shapes = new HashMap<>();

        shapes.put(Direction.NORTH, VoxelShapes.create(5D/16D, 1D/16D, 9D/16D, 11D/16D, 4D/16D, 10D/16D));
        shapes.put(Direction.EAST,  VoxelShapes.create(6D/16D, 1D/16D, 5D/16D,  7D/16D, 4D/16D, 11D/16D));
        shapes.put(Direction.SOUTH, VoxelShapes.create(5D/16D, 1D/16D, 6D/16D, 11D/16D, 4D/16D,  7D/16D));
        shapes.put(Direction.WEST,  VoxelShapes.create(9D/16D, 1D/16D, 5D/16D, 10D/16D, 4D/16D, 11D/16D));

        return shapes;
    }
}