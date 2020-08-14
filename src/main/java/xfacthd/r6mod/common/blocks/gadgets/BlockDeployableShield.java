package xfacthd.r6mod.common.blocks.gadgets;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;
import xfacthd.r6mod.common.blocks.BlockGadget;
import xfacthd.r6mod.common.data.PropertyHolder;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.tileentities.gadgets.TileEntityDeployableShield;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("deprecation")
public class BlockDeployableShield extends BlockGadget
{
    private final Map<Direction, VoxelShape> SHAPES = createShapes();

    public BlockDeployableShield(String name, EnumGadget gadget)
    {
        super(name,
                Properties.create(Material.IRON)
                        .notSolid()
                        .hardnessAndResistance(5F, 6F)
                        .harvestTool(ToolType.PICKAXE)
                        .sound(SoundType.METAL),
                gadget);
    }

    public BlockDeployableShield() { this("block_deployable_shield", EnumGadget.DEPLOYABLE_SHIELD); }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR);
    }

    @Override
    protected BlockState getStateForPlacementOwnable(BlockItemUseContext context)
    {
        if (context.getFace() != Direction.UP) { return null; }
        BlockState stateUnder = context.getWorld().getBlockState(context.getPos().down());
        if (!stateUnder.isSolid()) { return null; }

        return getDefaultState().with(PropertyHolder.FACING_HOR, context.getPlacementHorizontalFacing());
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new TileEntityDeployableShield(); }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
    {
        return SHAPES.get(state.get(PropertyHolder.FACING_HOR));
    }

    protected Map<Direction, VoxelShape> createShapes()
    {
        Map<Direction, VoxelShape> shapes = new HashMap<>();

        shapes.put(Direction.NORTH, makeCuboidShape( 0.0, 0.0,  1.0, 16.0, 16.0,  5.5));
        shapes.put(Direction.EAST,  makeCuboidShape(10.5, 0.0,  0.0, 15.0, 16.0, 16.0));
        shapes.put(Direction.SOUTH, makeCuboidShape( 0.0, 0.0, 10.5, 16.0, 16.0, 15.0));
        shapes.put(Direction.WEST,  makeCuboidShape( 1.0, 0.0,  0.0,  5.5, 16.0, 16.0));

        return shapes;
    }
}