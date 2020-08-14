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
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import xfacthd.r6mod.api.interaction.IHardDestructable;
import xfacthd.r6mod.common.blocks.BlockGadget;
import xfacthd.r6mod.common.data.PropertyHolder;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.items.BlockItemGadget;
import xfacthd.r6mod.common.tileentities.gadgets.TileEntityThermiteCharge;
import xfacthd.r6mod.common.util.Config;

import java.util.HashMap;

@SuppressWarnings("deprecation")
public class BlockThermiteCharge extends BlockGadget
{
    private static final HashMap<Direction, VoxelShape> VOXEL_SHAPES = createVoxelShapes();

    public BlockThermiteCharge()
    {
        super("block_thermite_charge",
                Properties.create(Material.IRON)
                .notSolid()
                .hardnessAndResistance(5.0F, 12000.0F)
                .harvestTool(ToolType.PICKAXE)
                .sound(SoundType.METAL),
                EnumGadget.THERMITE_CHARGE);
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
        if (stateUnder.getBlock() instanceof IHardDestructable)
        {
            return ((IHardDestructable)stateUnder.getBlock()).isSideSolid(world, stateUnder, pos, side);
        }

        if (teUnder instanceof IHardDestructable)
        {
            return ((IHardDestructable)teUnder).isSideSolid(world, stateUnder, pos, side);
        }

        return Config.INSTANCE.destroyWood && stateUnder.getMaterial() == Material.WOOD && stateUnder.isSolid();
    }

    @Override
    protected Item.Properties createItemProperties() { return super.createItemProperties().maxStackSize(1); }

    @Override
    protected BlockItem createBlockItem(Item.Properties props) { return new BlockItemGadget(this, props, gadget); }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new TileEntityThermiteCharge(); }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return VOXEL_SHAPES.getOrDefault(state.get(PropertyHolder.FACING_NOT_UP), VoxelShapes.fullCube());
    }

    private static HashMap<Direction, VoxelShape> createVoxelShapes()
    {
        HashMap<Direction, VoxelShape> shapes = new HashMap<>();

        shapes.put(Direction.NORTH, VoxelShapes.create( 2D/16D, 0,       0, 14D/16D,      1,  1D/16D));
        shapes.put(Direction.EAST,  VoxelShapes.create(15D/16D, 0,  2D/16D,       1,      1, 14D/16D));
        shapes.put(Direction.SOUTH, VoxelShapes.create( 2D/16D, 0, 15D/16D, 14D/16D,      1,       1));
        shapes.put(Direction.WEST,  VoxelShapes.create(      0, 0,  2D/16D,  1D/16D,      1, 14D/16D));
        shapes.put(Direction.DOWN,  VoxelShapes.create( 2D/16D, 0,       0, 14D/16D, 1D/16D,       1));

        return shapes;
    }
}