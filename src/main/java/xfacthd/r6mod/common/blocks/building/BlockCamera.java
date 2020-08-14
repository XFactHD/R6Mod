package xfacthd.r6mod.common.blocks.building;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.NetworkHooks;
import xfacthd.r6mod.common.blocks.BlockBase;
import xfacthd.r6mod.common.data.ItemGroups;
import xfacthd.r6mod.common.data.PropertyHolder;
import xfacthd.r6mod.common.tileentities.building.TileEntityCamera;

@SuppressWarnings("deprecation")
public class BlockCamera extends BlockBase
{
    private static final VoxelShape SHAPE = createVoxelShape();

    public BlockCamera()
    {
        super("block_camera",
                Properties.create(Material.IRON)
                .notSolid()
                .hardnessAndResistance(5.0F, 12000.0F)
                .harvestTool(ToolType.PICKAXE)
                .sound(SoundType.METAL),
                ItemGroups.GADGETS);
        setDefaultState(getDefaultState().with(PropertyHolder.DESTROYED, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.DESTROYED);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        if (context.getFace() != Direction.DOWN ||
                !context.getWorld().getBlockState(context.getPos().up()).isSolid() ||
                context.getPlayer() == null) { return null; }
        return getDefaultState();
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
    {
        super.onBlockPlacedBy(world, pos, state, placer, stack);

        TileEntity te = world.getTileEntity(pos);
        if (!world.isRemote && placer instanceof PlayerEntity && te instanceof TileEntityCamera)
        {
            if (((PlayerEntity) placer).isCreative())
            {
                NetworkHooks.openGui((ServerPlayerEntity) placer, (TileEntityCamera)te, pos);
            }
            else
            {
                String teamName = placer.getTeam() == null ? "null" : placer.getTeam().getName();
                ((TileEntityCamera)te).addCameraWithTeam(placer.getUniqueID(), teamName);
            }
        }
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (!world.isRemote && newState.getBlock() != state.getBlock())
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityCamera)
            {
                TileEntityCamera camTe = (TileEntityCamera) te;
                camTe.removeCamera();
            }
        }
        super.onReplaced(state, world, pos, newState, isMoving);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
    {
        return SHAPE;
    }

    @Override
    public boolean hasTileEntity(BlockState state) { return true; }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new TileEntityCamera(); }

    private static VoxelShape createVoxelShape()
    {
        return VoxelShapes.combineAndSimplify(
                Block.makeCuboidShape(6, 13, 6, 10, 15, 10),
                Block.makeCuboidShape(5, 15, 5, 11, 16, 11),
                IBooleanFunction.OR);
    }
}