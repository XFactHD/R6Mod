package xfacthd.r6mod.common.blocks.gadgets;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import xfacthd.r6mod.api.interaction.IDestructable;
import xfacthd.r6mod.common.blocks.BlockGadget;
import xfacthd.r6mod.common.data.PropertyHolder;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.items.BlockItemGadget;
import xfacthd.r6mod.common.tileentities.gadgets.TileEntityBreachCharge;
import xfacthd.r6mod.common.util.Config;

import java.util.HashMap;

@SuppressWarnings("deprecation")
public class BlockBreachCharge extends BlockGadget
{
    private static final HashMap<Direction, VoxelShape> VOXEL_SHAPES = createVoxelShapes();

    public BlockBreachCharge()
    {
        super("block_breach_charge",
                Properties.create(Material.IRON)
                .notSolid()
                .hardnessAndResistance(5.0F, 3.0F)
                .harvestTool(ToolType.PICKAXE)
                .sound(SoundType.METAL),
                EnumGadget.BREACH_CHARGE);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_NOT_UP);
    }

    @Override
    public BlockState getStateForPlacementOwnable(BlockItemUseContext context)
    {
        if (context.getFace() == Direction.DOWN) { return null; }

        World world = context.getWorld();
        Direction side = context.getFace();
        BlockPos adjPos = context.getPos().offset(side.getOpposite());

        BlockState stateUnder = world.getBlockState(adjPos);
        TileEntity teUnder = world.getTileEntity(adjPos);

        if (isDestructible(world, adjPos, stateUnder, teUnder, side))
        {
            return getDefaultState().with(PropertyHolder.FACING_NOT_UP, side.getOpposite());
        }
        return null;
    }

    private boolean isDestructible(World world, BlockPos pos, BlockState stateUnder, TileEntity teUnder, Direction side)
    {
        if (stateUnder.getBlock() instanceof IDestructable)
        {
            return ((IDestructable)stateUnder.getBlock()).isSideSolid(world, stateUnder, pos, side);
        }

        if (teUnder instanceof IDestructable)
        {
            return ((IDestructable)teUnder).isSideSolid(world, stateUnder, pos, side);
        }

        return Config.INSTANCE.destroyWood && stateUnder.getMaterial() == Material.WOOD && stateUnder.isSolid();
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new TileEntityBreachCharge(); }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return VOXEL_SHAPES.getOrDefault(state.get(PropertyHolder.FACING_NOT_UP), VoxelShapes.fullCube());
    }

    @Override
    protected Item.Properties createItemProperties() { return super.createItemProperties().maxStackSize(1); }

    @Override
    protected BlockItem createBlockItem(Item.Properties props) { return new BlockItemGadget(this, props, gadget); }

    private static HashMap<Direction, VoxelShape> createVoxelShapes()
    {
        HashMap<Direction, VoxelShape> shapes = new HashMap<>();

        // Center shape
        VoxelShape shapeNorth = VoxelShapes.create( 3D/16D, 1.25D/16D,         0, 13D/16D, 14.75D/16D,     1D/16D);
        VoxelShape shapeEast  = VoxelShapes.create(15D/16D, 1.25D/16D,    3D/16D,       1, 14.75D/16D,    13D/16D);
        VoxelShape shapeSouth = VoxelShapes.create( 3D/16D, 1.25D/16D,   15D/16D, 13D/16D, 14.75D/16D,          1);
        VoxelShape shapeWest  = VoxelShapes.create(      0, 1.25D/16D,    3D/16D,  1D/16D, 14.75D/16D,    13D/16D);
        VoxelShape shapeDown  = VoxelShapes.create( 3D/16D,         0, 1.25D/16D, 13D/16D,     1D/16D, 14.75D/16D);

        // Top end 6.25D/16D 9.75D/16D
        shapeNorth = VoxelShapes.combine(shapeNorth, VoxelShapes.create(6.25D/16D, 14.75D/16D,         0, 9.75D/16D,      1,    1D/16D), IBooleanFunction.OR);
        shapeEast  = VoxelShapes.combine(shapeEast , VoxelShapes.create(  15D/16D, 14.75D/16D, 6.25D/16D,         1,      1, 9.75D/16D), IBooleanFunction.OR);
        shapeSouth = VoxelShapes.combine(shapeSouth, VoxelShapes.create(6.25D/16D, 14.75D/16D,   15D/16D, 9.75D/16D,      1,         1), IBooleanFunction.OR);
        shapeWest  = VoxelShapes.combine(shapeWest , VoxelShapes.create(        0, 14.75D/16D, 6.25D/16D,    1D/16D,      1, 9.75D/16D), IBooleanFunction.OR);
        shapeDown  = VoxelShapes.combine(shapeDown , VoxelShapes.create(6.25D/16D,          0,         0, 9.75D/16D, 1D/16D, 1.25D/16D), IBooleanFunction.OR);

        // Bottom end 5.25D/16D 10.75D/16D
        shapeNorth = VoxelShapes.combine(shapeNorth, VoxelShapes.create(5.25D/16D, 0,          0, 10.75D/16D, 1.25D/16D,     1D/16D), IBooleanFunction.OR);
        shapeEast  = VoxelShapes.combine(shapeEast , VoxelShapes.create(  15D/16D, 0,  5.25D/16D,          1, 1.25D/16D, 10.75D/16D), IBooleanFunction.OR);
        shapeSouth = VoxelShapes.combine(shapeSouth, VoxelShapes.create(5.25D/16D, 0,    15D/16D, 10.75D/16D, 1.25D/16D,          1), IBooleanFunction.OR);
        shapeWest  = VoxelShapes.combine(shapeWest , VoxelShapes.create(        0, 0,  5.25D/16D,     1D/16D, 1.25D/16D, 10.75D/16D), IBooleanFunction.OR);
        shapeDown  = VoxelShapes.combine(shapeDown , VoxelShapes.create(5.25D/16D, 0, 14.75D/16D, 10.75D/16D,    1D/16D,          1), IBooleanFunction.OR);

        shapes.put(Direction.NORTH, shapeNorth);
        shapes.put(Direction.EAST,  shapeEast);
        shapes.put(Direction.SOUTH, shapeSouth);
        shapes.put(Direction.WEST,  shapeWest);
        shapes.put(Direction.DOWN,  shapeDown);

        return shapes;
    }
}