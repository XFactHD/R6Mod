package xfacthd.r6mod.common.blocks.misc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import xfacthd.r6mod.common.blocks.BlockBase;
import xfacthd.r6mod.common.data.ItemGroups;
import xfacthd.r6mod.common.data.PropertyHolder;
import xfacthd.r6mod.common.items.gun.ItemMagazine;
import xfacthd.r6mod.common.items.material.ItemBullet;
import xfacthd.r6mod.common.tileentities.misc.TileEntityMagFiller;

import javax.annotation.Nullable;
import java.util.HashMap;

public class BlockMagFiller extends BlockBase
{
    private static final HashMap<Direction, VoxelShape> VOXEL_SHAPES = createVoxelShapes();

    public BlockMagFiller()
    {
        super("block_mag_filler",
                Properties.create(Material.IRON)
                .notSolid()
                .hardnessAndResistance(5.0F, 12000.0F)
                .harvestTool(ToolType.PICKAXE)
                .sound(SoundType.METAL),
                ItemGroups.MISC
        );

        setDefaultState(getDefaultState().with(PropertyHolder.ACTIVE, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(BlockStateProperties.HORIZONTAL_FACING, PropertyHolder.ACTIVE);
    }

    @Override
    public BlockState getExtendedState(BlockState state, IBlockReader world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityMagFiller)
        {
            TileEntityMagFiller temf = (TileEntityMagFiller)te;
            return state.with(PropertyHolder.FACING_HOR, temf.getFacing()).with(PropertyHolder.ACTIVE, temf.isActive());
        }
        return state;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return getDefaultState().with(PropertyHolder.FACING_HOR, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult traceResult)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityMagFiller)
        {
            if (player.isCrouching())
            {
                if (!world.isRemote)
                {
                    ItemStack handStack = player.getHeldItemMainhand();
                    final ItemStack[] returnStack = new ItemStack[1];
                    te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((handler) ->
                    {
                        if (handStack.isEmpty())
                        {
                            returnStack[0] = handler.extractItem(TileEntityMagFiller.MAG_SLOT, 1, false);
                        }
                        else if (handStack.getItem() instanceof ItemMagazine)
                        {
                            returnStack[0] = handler.insertItem(TileEntityMagFiller.MAG_SLOT, handStack, false);
                        }
                        else if (handStack.getItem() instanceof ItemBullet)
                        {
                            int slot = 0;
                            returnStack[0] = handStack;
                            while (!returnStack[0].isEmpty() && slot < 3)
                            {
                                returnStack[0] = handler.insertItem(slot, returnStack[0], false);
                                slot++;
                            }
                        }
                    });

                    if (!returnStack[0].equals(handStack))
                    {
                        player.setHeldItem(Hand.MAIN_HAND, returnStack[0]);
                        ((TileEntityMagFiller) te).markFullUpdate();
                        return ActionResultType.SUCCESS;
                    }
                }
            }
            else
            {
                if (!world.isRemote)
                {
                    NetworkHooks.openGui((ServerPlayerEntity) player, (TileEntityMagFiller) te, pos);
                }
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.FAIL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (!world.isRemote && state.getBlock() != newState.getBlock())
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityMagFiller) { ((TileEntityMagFiller)te).dropContents(); }

        }
        super.onReplaced(state, world, pos, newState, isMoving);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
    {
        return VOXEL_SHAPES.getOrDefault(state.get(PropertyHolder.FACING_HOR), VoxelShapes.fullCube());
    }

    @Override
    public boolean hasTileEntity(BlockState state) { return true; }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return new TileEntityMagFiller(state.get(PropertyHolder.FACING_HOR));
    }

    private static HashMap<Direction, VoxelShape> createVoxelShapes()
    {
        HashMap<Direction, VoxelShape> shapes = new HashMap<>();

        VoxelShape baseShape = VoxelShapes.create(0, 0, 0, 1, 1D/16D, 1);

        VoxelShape legShapeNorth = VoxelShapes.create( 6D/16D, 1D/16D, 12D/16D, 10D/16D, 1, 14D/16D);
        VoxelShape legShapeEast  = VoxelShapes.create( 2D/16D, 1D/16D,  6D/16D,  4D/16D, 1, 10D/16D);
        VoxelShape legShapeSouth = VoxelShapes.create( 6D/16D, 1D/16D,  2D/16D, 10D/16D, 1,  4D/16D);
        VoxelShape legShapeWest  = VoxelShapes.create(12D/16D, 1D/16D,  6D/16D, 14D/16D, 1, 10D/16D);

        VoxelShape interShapeNorth = VoxelShapes.combine(baseShape, legShapeNorth, IBooleanFunction.OR);
        VoxelShape interShapeEast  = VoxelShapes.combine(baseShape, legShapeEast , IBooleanFunction.OR);
        VoxelShape interShapeSouth = VoxelShapes.combine(baseShape, legShapeSouth, IBooleanFunction.OR);
        VoxelShape interShapeWest  = VoxelShapes.combine(baseShape, legShapeWest , IBooleanFunction.OR);

        VoxelShape topShapeNorth = VoxelShapes.create( 4D/16D,  9D/16D,  2D/16D, 12D/16D, 1, 12D/16D);
        VoxelShape topShapeEast  = VoxelShapes.create( 4D/16D,  9D/16D,  4D/16D, 14D/16D, 1, 12D/16D);
        VoxelShape topShapeSouth = VoxelShapes.create( 4D/16D,  9D/16D,  4D/16D, 12D/16D, 1, 14D/16D);
        VoxelShape topShapeWest  = VoxelShapes.create( 2D/16D,  9D/16D,  4D/16D, 12D/16D, 1, 12D/16D);

        shapes.put(Direction.NORTH, VoxelShapes.combine(interShapeNorth, topShapeNorth, IBooleanFunction.OR));
        shapes.put(Direction.EAST,  VoxelShapes.combine(interShapeEast , topShapeEast , IBooleanFunction.OR));
        shapes.put(Direction.SOUTH, VoxelShapes.combine(interShapeSouth, topShapeSouth, IBooleanFunction.OR));
        shapes.put(Direction.WEST,  VoxelShapes.combine(interShapeWest , topShapeWest , IBooleanFunction.OR));

        return shapes;
    }
}