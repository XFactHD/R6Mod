package xfacthd.r6mod.common.blocks.misc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import xfacthd.r6mod.common.blocks.BlockBase;
import xfacthd.r6mod.common.data.ItemGroups;
import xfacthd.r6mod.common.data.PropertyHolder;
import xfacthd.r6mod.common.tileentities.misc.TileEntityAmmoBox;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class BlockAmmoBox extends BlockBase
{
    private static final VoxelShape SHAPE_NORTH = VoxelShapes.create(0, 0, 5D/16D, 1, 4D/16D, 11D/16D);
    private static final VoxelShape SHAPE_EAST  = VoxelShapes.create(5D/16D, 0, 0, 11D/16D, 4D/16D, 1);

    public BlockAmmoBox()
    {
        super("block_ammo_box",
                Properties.create(Material.IRON)
                .notSolid()
                .hardnessAndResistance(5.0F, 12000.0F)
                .harvestTool(ToolType.PICKAXE)
                .sound(SoundType.METAL),
                ItemGroups.MISC);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_NE);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        Direction facing = context.getPlacementHorizontalFacing();
        if (facing == Direction.SOUTH || facing == Direction.WEST) { facing = facing.getOpposite(); }
        return getDefaultState().with(PropertyHolder.FACING_NE, facing);
    }

    @Override
    public BlockState getExtendedState(BlockState state, IBlockReader world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityAmmoBox)
        {
            return state.with(PropertyHolder.FACING_NE, ((TileEntityAmmoBox)te).getFacing());
        }
        return state;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTrace)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityAmmoBox)
        {
            return ((TileEntityAmmoBox)te).interact(player).toActionResultType();
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public boolean hasTileEntity(BlockState state) { return true; }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return new TileEntityAmmoBox(state.get(PropertyHolder.FACING_NE));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
    {
        return state.get(PropertyHolder.FACING_NE) == Direction.EAST ? SHAPE_EAST : SHAPE_NORTH;
    }
}