package xfacthd.r6mod.common.blocks.building;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import xfacthd.r6mod.api.interaction.IDestructable;
import xfacthd.r6mod.common.blocks.BlockBase;
import xfacthd.r6mod.common.data.ItemGroups;
import xfacthd.r6mod.common.data.PropertyHolder;
import xfacthd.r6mod.common.data.blockdata.WallSegment;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;

import javax.annotation.Nullable;
import java.util.Map;

@SuppressWarnings("deprecation")
public class BlockDropHatch extends BlockBase implements IDestructable
{
    private static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public BlockDropHatch()
    {
        super("block_drop_hatch",
                Properties.create(Material.WOOD)
                        .notSolid()
                        .hardnessAndResistance(2F, 3F)
                        .harvestTool(ToolType.AXE),
                ItemGroups.BUILDING);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.SQUARE_SEGMENT);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        Vector3d hitVec = context.getHitVec();
        Vector3d subHit = hitVec.subtract(Math.floor(hitVec.getX()), Math.floor(hitVec.getY()), Math.floor(hitVec.getZ()));

        Direction face = context.getFace();
        boolean top;
        boolean right;
        if (face.getAxis() == Direction.Axis.Y)
        {
            top = subHit.getZ() > .5D;
            right = subHit.getX() < .5D;
        }
        else
        {
            if (face.getAxis() == Direction.Axis.X)
            {
                top = subHit.getZ() > .5D;
                right = face == Direction.WEST;
            }
            else
            {
                top = face == Direction.SOUTH;
                right = subHit.getX() < .5D;
            }
        }

        WallSegment segment = WallSegment.squareFromBools(top, right);
        Map<WallSegment, BlockPos> posMap = segment.squarePositions(context.getPos());

        World world = context.getWorld();
        for (BlockPos pos : posMap.values())
        {
            if (!world.isAirBlock(pos)) { return null; }
        }

        return getDefaultState().with(PropertyHolder.SQUARE_SEGMENT, segment);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        WallSegment segment = state.get(PropertyHolder.SQUARE_SEGMENT);
        Map<WallSegment, BlockPos> posMap = segment.squarePositions(pos);

        for (WallSegment seg : posMap.keySet())
        {
            if (seg != segment)
            {
                world.setBlockState(posMap.get(seg), getDefaultState().with(PropertyHolder.SQUARE_SEGMENT, seg));
            }
        }
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
}