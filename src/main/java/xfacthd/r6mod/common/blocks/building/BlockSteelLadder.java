package xfacthd.r6mod.common.blocks.building;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.*;
import net.minecraftforge.common.ToolType;
import xfacthd.r6mod.common.blocks.BlockBase;
import xfacthd.r6mod.common.data.ItemGroups;
import xfacthd.r6mod.common.data.PropertyHolder;
import xfacthd.r6mod.common.util.Utils;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("deprecation")
public class BlockSteelLadder extends BlockBase
{
    private final Map<BlockState, VoxelShape> SHAPES = new HashMap<>();

    public BlockSteelLadder()
    {
        super("block_steel_ladder",
                Properties.create(Material.IRON)
                        .notSolid()
                        .hardnessAndResistance(5F, 6F)
                        .harvestTool(ToolType.PICKAXE),
                ItemGroups.BUILDING);

        setDefaultState(getDefaultState().with(PropertyHolder.UP, false).with(PropertyHolder.DOWN, false));

        createShapes();
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.UP, PropertyHolder.DOWN);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        Direction facing = context.getPlacementHorizontalFacing();

        BlockState stateUp = world.getBlockState(pos.up());
        BlockState stateDown = world.getBlockState(pos.down());

        BlockState state = getDefaultState().with(PropertyHolder.FACING_HOR, facing);
        state = state.with(PropertyHolder.UP, stateUp.getBlock() == this && stateUp.get(PropertyHolder.FACING_HOR) == facing);
        state = state.with(PropertyHolder.DOWN, stateDown.getBlock() == this && stateDown.get(PropertyHolder.FACING_HOR) == facing);
        return state;
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving)
    {
        if (fromPos.equals(pos.up()))
        {
            BlockState stateUp = world.getBlockState(fromPos);
            boolean up = stateUp.getBlock() == this && stateUp.get(PropertyHolder.FACING_HOR) == state.get(PropertyHolder.FACING_HOR);

            if (up != state.get(PropertyHolder.UP))
            {
                world.setBlockState(pos, state.with(PropertyHolder.UP, up));
            }
        }
        else if (fromPos.equals(pos.down()))
        {
            BlockState stateDown = world.getBlockState(fromPos);
            boolean down = stateDown.getBlock() == this && stateDown.get(PropertyHolder.FACING_HOR) == state.get(PropertyHolder.FACING_HOR);

            if (down != state.get(PropertyHolder.DOWN))
            {
                world.setBlockState(pos, state.with(PropertyHolder.DOWN, down));
            }
        }
    }

    @Override
    public boolean isLadder(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity) { return true; }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
    {
        return SHAPES.get(state);
    }

    private void createShapes()
    {
        VoxelShape shapeLadder = makeCuboidShape(0, 0, 7, 16, 16, 9);
        VoxelShape shapeTop = VoxelShapes.combineAndSimplify(
                shapeLadder,
                makeCuboidShape(0, 15, 0, 16, 16, 9),
                IBooleanFunction.OR
        );

        stateContainer.getValidStates().forEach((state) ->
        {
            Direction facing = state.get(PropertyHolder.FACING_HOR);
            boolean top = !state.get(PropertyHolder.UP);
            SHAPES.put(state, Utils.rotateShape(Direction.NORTH, facing, top ? shapeTop : shapeLadder));
        });
    }
}