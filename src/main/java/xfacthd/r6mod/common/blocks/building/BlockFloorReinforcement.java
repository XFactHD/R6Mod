package xfacthd.r6mod.common.blocks.building;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import xfacthd.r6mod.api.interaction.IHardDestructable;
import xfacthd.r6mod.common.R6Content;
import xfacthd.r6mod.common.blocks.BlockBase;
import xfacthd.r6mod.common.data.ItemGroups;
import xfacthd.r6mod.common.data.PropertyHolder;
import xfacthd.r6mod.common.data.blockdata.WallSegment;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;

import java.util.Map;

@SuppressWarnings("deprecation")
public class BlockFloorReinforcement extends BlockBase implements IHardDestructable
{
    private static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public BlockFloorReinforcement()
    {
        super("block_floor_reinforcement",
                Properties.create(Material.IRON),
                ItemGroups.BUILDING);
        setDefaultState(getDefaultState().with(PropertyHolder.ELECTRIFIED, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.SQUARE_SEGMENT, PropertyHolder.ELECTRIFIED);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
    {
        return SHAPE;
    }

    @Override
    public void destroy(World world, BlockPos pos, BlockState state, PlayerEntity player, EnumGadget source, Direction side)
    {
        WallSegment segment = state.get(PropertyHolder.SQUARE_SEGMENT);
        Map<WallSegment, BlockPos> posMap = segment.squarePositions(pos);

        for (BlockPos blockPos : posMap.values())
        {
            world.destroyBlock(blockPos, false);
        }
    }

    @Override
    public boolean isSideSolid(World world, BlockState state, BlockPos pos, Direction side)
    {
        return side == Direction.UP;
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
    {
        return new ItemStack(R6Content.itemReinforcement);
    }
}