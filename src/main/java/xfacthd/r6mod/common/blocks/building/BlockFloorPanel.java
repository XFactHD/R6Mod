package xfacthd.r6mod.common.blocks.building;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import xfacthd.r6mod.api.interaction.IDestructable;
import xfacthd.r6mod.common.R6Content;
import xfacthd.r6mod.common.blocks.BlockBase;
import xfacthd.r6mod.common.data.ItemGroups;
import xfacthd.r6mod.common.data.PropertyHolder;
import xfacthd.r6mod.common.data.blockdata.WallMaterial;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.items.building.BlockItemFloorPanel;
import xfacthd.r6mod.common.util.Utils;

import java.util.*;
import java.util.stream.Stream;

@SuppressWarnings("deprecation")
public class BlockFloorPanel extends BlockBase implements IDestructable
{
    private static final VoxelShape SHAPE = Block.makeCuboidShape(0, 8, 0, 16, 16, 16); //Top slab bounding box
    private static final VoxelShape SHAPE_DESTROYED_N;
    private static final VoxelShape SHAPE_DESTROYED_E;

    private final WallMaterial material;

    public BlockFloorPanel(WallMaterial material)
    {
        super("block_floor_panel_" + material.getString(),
                Properties.create(Material.IRON)
                .notSolid()
                .hardnessAndResistance(5.0F, 6.0F)
                .harvestTool(ToolType.PICKAXE),
                ItemGroups.BUILDING);

        this.material = material;

        setDefaultState(getDefaultState().with(PropertyHolder.DESTROYED, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_NE, PropertyHolder.DESTROYED);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        if (context.getPlayer() == null) { return null; }

        Direction facing = context.getPlayer().getHorizontalFacing();
        if (facing == Direction.SOUTH || facing == Direction.WEST) { facing = facing.getOpposite(); }
        return getDefaultState().with(PropertyHolder.FACING_NE, facing);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        if (!state.get(PropertyHolder.DESTROYED)) { return SHAPE; }
        return state.get(PropertyHolder.FACING_NE) == Direction.NORTH ? SHAPE_DESTROYED_N : SHAPE_DESTROYED_E;
    }

    @Override
    public void destroy(World world, BlockPos pos, BlockState state, PlayerEntity player, EnumGadget source, Direction side)
    {
        if (!state.get(PropertyHolder.DESTROYED))
        {
            world.setBlockState(pos, state.with(PropertyHolder.DESTROYED, true));
        }
    }

    @Override
    public boolean isSideSolid(World world, BlockState state, BlockPos pos, Direction side)
    {
        return side == Direction.UP;
    }

    @Override
    protected BlockItem createBlockItem(Item.Properties props)
    {
        return new BlockItemFloorPanel(this, props, material);
    }

    public WallMaterial getMaterial() { return material; }

    public static BlockFloorPanel[] registerBlocks()
    {
        R6Content.blockFloorPanels = new HashMap<>();

        List<BlockFloorPanel> blocks = new ArrayList<>();

        for (WallMaterial material : WallMaterial.values())
        {
            BlockFloorPanel block = new BlockFloorPanel(material);
            blocks.add(block);
            R6Content.blockFloorPanels.put(material, block);
        }

        return blocks.toArray(new BlockFloorPanel[0]);
    }

    static
    {
        SHAPE_DESTROYED_N = Stream.of(
                Block.makeCuboidShape(1.2000000178813934, 9.000000014901161, 0, 4.200000017881393, 15.000000014901161, 16),
                Block.makeCuboidShape(6.500000022351742, 9.000000014901161, 0, 9.500000022351742, 15.000000014901161, 16),
                Block.makeCuboidShape(11.80000002682209, 9.000000014901161, 0, 14.80000002682209, 15.000000014901161, 16)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();
        SHAPE_DESTROYED_E = Utils.rotateShape(Direction.NORTH, Direction.EAST, SHAPE_DESTROYED_N);
    }
}