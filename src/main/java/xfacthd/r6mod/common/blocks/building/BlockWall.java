package xfacthd.r6mod.common.blocks.building;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.*;
import net.minecraftforge.common.ToolType;
import xfacthd.r6mod.api.interaction.IDestructable;
import xfacthd.r6mod.common.R6Content;
import xfacthd.r6mod.common.blocks.BlockBase;
import xfacthd.r6mod.common.data.*;
import xfacthd.r6mod.common.data.blockdata.WallMaterial;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.items.building.BlockItemWall;
import xfacthd.r6mod.common.util.Utils;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Stream;

@SuppressWarnings("deprecation")
public class BlockWall extends BlockBase implements IDestructable
{
    private static final VoxelShape SHAPE_NORTH;
    private static final VoxelShape SHAPE_EAST;
    private static final VoxelShape SHAPE_NORTH_DESTROYED;
    private static final VoxelShape SHAPE_EAST_DESTROYED;

    private final WallMaterial material;
    private final boolean barred;

    public BlockWall(WallMaterial material, boolean barred)
    {
        super("block_wall_" + (barred ? "barred_" : "soft_") + material.getString(),
                Properties.create(barred ? Material.IRON : Material.WOOD)
                        .notSolid()
                        .harvestTool(barred ? ToolType.PICKAXE : ToolType.AXE),
                ItemGroups.BUILDING);

        this.material = material;
        this.barred = barred;

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
        PlayerEntity player = context.getPlayer();
        if (player == null) { return null; }

        Direction facing = player.getHorizontalFacing();
        if (facing == Direction.SOUTH || facing == Direction.WEST) { facing = facing.getOpposite(); }
        return getDefaultState().with(PropertyHolder.FACING_NE, facing);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        if (state.get(PropertyHolder.DESTROYED))
        {
            return state.get(PropertyHolder.FACING_NE) == Direction.NORTH ? SHAPE_NORTH_DESTROYED : SHAPE_EAST_DESTROYED;
        }
        else
        {
            return state.get(PropertyHolder.FACING_NE) == Direction.NORTH ? SHAPE_NORTH : SHAPE_EAST;
        }
    }

    @Override
    protected BlockItem createBlockItem(Item.Properties props)
    {
        return new BlockItemWall(this, props, material, barred);
    }

    @Override
    public SoundType getSoundType(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity entity)
    {
        return barred ? SoundType.METAL : SoundType.WOOD;
    }

    //@Override //FIXME: PR to Forge (replace hardness in AbstractBlock.Properties with a ToIntFunction)
    //public float getBlockHardness(IBlockReader world, BlockPos pos) { return barred ? 5 : 2; }

    @Override
    public float getExplosionResistance(BlockState state, IBlockReader world, BlockPos pos, Explosion explosion)
    {
        return barred ? 6 : 2;
    }

    @Override
    public void destroy(World world, BlockPos pos, BlockState state, PlayerEntity player, EnumGadget source, Direction side)
    {
        if (barred && !state.get(PropertyHolder.DESTROYED)) { world.setBlockState(pos, state.with(PropertyHolder.DESTROYED, true)); }
        else if (!barred) { world.destroyBlock(pos, false); }
    }

    @Override
    public boolean isSideSolid(World world, BlockState state, BlockPos pos, Direction side)
    {
        if (barred && state.get(PropertyHolder.DESTROYED)) { return false; }

        Direction facing = state.get(PropertyHolder.FACING_NE);
        return side == facing || side == facing.getOpposite();
    }

    public WallMaterial getMaterial() { return material; }

    public boolean isBarred() { return barred; }

    public static BlockWall[] registerBlocks()
    {
        R6Content.blockWalls = new HashMap<>();
        R6Content.blockWallsBarred = new HashMap<>();

        List<BlockWall> blocks = new ArrayList<>();

        for (WallMaterial material : WallMaterial.values())
        {
            BlockWall blockWall = new BlockWall(material, false);
            blocks.add(blockWall);
            R6Content.blockWalls.put(material, blockWall);

            blockWall = new BlockWall(material, true);
            blocks.add(blockWall);
            R6Content.blockWallsBarred.put(material, blockWall);
        }

        return blocks.toArray(new BlockWall[0]);
    }

    static
    {
        SHAPE_NORTH = Stream.of(
                Block.makeCuboidShape( 2.5,  0.0,  5.0,  5.5, 16.0, 11.0),
                Block.makeCuboidShape(10.5,  0.0,  5.0, 13.5, 16.0, 11.0),
                Block.makeCuboidShape( 0.0,  0.0,  4.0, 16.0, 16.0,  5.0),
                Block.makeCuboidShape( 0.0,  0.0, 11.0, 16.0, 16.0, 12.0)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();
        SHAPE_EAST = Utils.rotateShape(Direction.NORTH, Direction.EAST, SHAPE_NORTH);

        SHAPE_NORTH_DESTROYED = VoxelShapes.combineAndSimplify(
                Block.makeCuboidShape( 2.5,  0.0,  5.0,  5.5, 16.0, 11.0),
                Block.makeCuboidShape(10.5,  0.0,  5.0, 13.5, 16.0, 11.0),
                IBooleanFunction.OR);
        SHAPE_EAST_DESTROYED = Utils.rotateShape(Direction.NORTH, Direction.EAST, SHAPE_NORTH_DESTROYED);
    }
}