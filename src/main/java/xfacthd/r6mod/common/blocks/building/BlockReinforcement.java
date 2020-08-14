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
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import xfacthd.r6mod.api.interaction.IHardDestructable;
import xfacthd.r6mod.common.R6Content;
import xfacthd.r6mod.common.blocks.BlockBase;
import xfacthd.r6mod.common.data.*;
import xfacthd.r6mod.common.data.blockdata.WallSegment;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.util.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@SuppressWarnings("deprecation")
public class BlockReinforcement extends BlockBase implements IHardDestructable
{
    private static final Map<Direction, VoxelShape> SHAPES = new HashMap<>();
    private static final Map<Direction, VoxelShape> SHAPES_TOP = new HashMap<>();

    static { createShapes(); }

    public BlockReinforcement()
    {
        super("block_reinforcement", Properties.create(Material.IRON)
                .hardnessAndResistance(-1.0F, 3600000.0F)
                .notSolid(),
                ItemGroups.BUILDING
        );

        setDefaultState(getDefaultState()
                .with(PropertyHolder.WALL_SEGMENT, WallSegment.BOTTOM_LEFT)
                .with(PropertyHolder.ELECTRIFIED, false)
        );
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.WALL_SEGMENT, PropertyHolder.MATERIAL, PropertyHolder.ELECTRIFIED);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
    {
        if (state.get(PropertyHolder.WALL_SEGMENT).isTop())
        {
            return SHAPES_TOP.get(state.get(PropertyHolder.FACING_HOR));
        }
        else
        {
            return SHAPES.get(state.get(PropertyHolder.FACING_HOR));
        }
    }

    @Override
    public void destroy(World world, BlockPos pos, BlockState state, PlayerEntity player, EnumGadget source, Direction side)
    {
        world.destroyBlock(pos, false);
    }

    @Override
    public boolean isSideSolid(World world, BlockState state, BlockPos pos, Direction side)
    {
        Direction facing = state.get(PropertyHolder.FACING_HOR);
        return side == facing || side == facing.getOpposite();
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
    {
        return new ItemStack(R6Content.itemReinforcement);
    }

    private static void createShapes()
    {
        VoxelShape shape = Stream.of(
                Block.makeCuboidShape( 0.0,  0.0, 12.0, 16.0, 16.0, 16.0),
                Block.makeCuboidShape( 3.5,  2.5,  3.5,  4.5,  5.5,  4.5),
                Block.makeCuboidShape( 2.5,  3.5,  3.5,  5.5,  4.5,  4.5),
                Block.makeCuboidShape(11.5,  2.5,  3.5, 12.5,  5.5,  4.5),
                Block.makeCuboidShape(10.5,  3.5,  3.5, 13.5,  4.5,  4.5),
                Block.makeCuboidShape( 7.5,  6.5,  3.5,  8.5,  9.5,  4.5),
                Block.makeCuboidShape( 7.5,  7.5,  4.5,  8.5,  8.5, 12.0),
                Block.makeCuboidShape( 3.5, 10.5,  3.5,  4.5, 13.5,  4.5),
                Block.makeCuboidShape( 2.5, 11.5,  3.5,  5.5, 12.5,  4.5),
                Block.makeCuboidShape(11.5, 10.5,  3.5, 12.5, 13.5,  4.5),
                Block.makeCuboidShape(10.5, 11.5,  3.5, 13.5, 12.5,  4.5),
                Block.makeCuboidShape( 6.5,  7.5,  3.5,  9.5,  8.5,  4.5),
                Block.makeCuboidShape( 2.5,  0.0,  5.0,  5.5, 16.0, 11.0),
                Block.makeCuboidShape(10.5,  0.0,  5.0, 13.5, 16.0, 11.0),
                Block.makeCuboidShape( 0.0,  0.0,  4.0, 16.0, 16.0,  5.0),
                Block.makeCuboidShape( 0.0,  0.0, 11.0, 16.0, 16.0, 12.0)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

        VoxelShape shapeTop = Stream.of(
                Block.makeCuboidShape( 0.0,  0.0, 12.0, 16.0, 16.0, 16.0),
                Block.makeCuboidShape( 0.0, 13.0, 16.0, 16.0, 16.0, 19.0),
                Block.makeCuboidShape( 3.5,  2.5,  3.5,  4.5,  5.5,  4.5),
                Block.makeCuboidShape( 2.5,  3.5,  3.5,  5.5,  4.5,  4.5),
                Block.makeCuboidShape(11.5,  2.5,  3.5, 12.5,  5.5,  4.5),
                Block.makeCuboidShape(10.5,  3.5,  3.5, 13.5,  4.5,  4.5),
                Block.makeCuboidShape( 7.5,  6.5,  3.5,  8.5,  9.5,  4.5),
                Block.makeCuboidShape( 6.5,  7.5,  3.5,  9.5,  8.5,  4.5),
                Block.makeCuboidShape( 3.5, 10.5,  3.5,  4.5, 13.5,  4.5),
                Block.makeCuboidShape( 2.5, 11.5,  3.5,  5.5, 12.5,  4.5),
                Block.makeCuboidShape(11.5, 10.5,  3.5, 12.5, 13.5,  4.5),
                Block.makeCuboidShape(10.5, 11.5,  3.5, 13.5, 12.5,  4.5),
                Block.makeCuboidShape( 7.5,  7.5,  4.5,  8.5,  8.5, 12.0),
                Block.makeCuboidShape( 2.5,  0.0,  5.0,  5.5, 16.0, 11.0),
                Block.makeCuboidShape(10.5,  0.0,  5.0, 13.5, 16.0, 11.0),
                Block.makeCuboidShape( 0.0,  0.0,  4.0, 16.0, 16.0,  5.0),
                Block.makeCuboidShape( 0.0,  0.0, 11.0, 16.0, 16.0, 12.0)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

        for (Direction facing : Direction.Plane.HORIZONTAL)
        {
            SHAPES.put(facing, Utils.rotateShape(Direction.NORTH, facing, shape));
            SHAPES_TOP.put(facing, Utils.rotateShape(Direction.NORTH, facing, shapeTop));
        }
    }
}