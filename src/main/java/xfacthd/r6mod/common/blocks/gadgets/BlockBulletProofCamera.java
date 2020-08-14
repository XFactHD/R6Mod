package xfacthd.r6mod.common.blocks.gadgets;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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
import xfacthd.r6mod.common.blocks.BlockGadget;
import xfacthd.r6mod.common.data.PropertyHolder;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.entities.camera.EntityBulletproofCamera;
import xfacthd.r6mod.common.tileentities.gadgets.TileEntityBulletproofCamera;
import xfacthd.r6mod.common.util.Utils;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Stream;

public class BlockBulletProofCamera extends BlockGadget
{
    private static final Map<Direction, VoxelShape> SHAPES = createVoxelShapes();

    public BlockBulletProofCamera()
    {
        super("block_bulletproof_camera",
                Properties.create(Material.IRON)
                .notSolid()
                .hardnessAndResistance(5.0F, 12000.0F)
                .harvestTool(ToolType.PICKAXE)
                .sound(SoundType.METAL),
                EnumGadget.BULLETPROOF_CAMERA);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_NOT_DOWN);
    }

    @Override
    public BlockState getExtendedState(BlockState state, IBlockReader world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityBulletproofCamera)
        {
            TileEntityBulletproofCamera tebc = (TileEntityBulletproofCamera)te;
            return state.with(PropertyHolder.FACING_NOT_DOWN, tebc.getFacing());
        }
        return state;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacementOwnable(BlockItemUseContext context)
    {
        BlockPos adjPos = context.getPos().offset(context.getFace().getOpposite());
        if (context.getFace() == Direction.DOWN
                || context.getPlayer() == null
                || !context.getWorld().getBlockState(adjPos).isSolid()) { return null; }
        return getDefaultState().with(PropertyHolder.FACING_NOT_DOWN, context.getFace());
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        super.onBlockPlacedBy(world, pos, state, placer, stack);

        if (!world.isRemote && placer instanceof PlayerEntity)
        {
            String teamName = placer.getTeam() == null ? "null" : placer.getTeam().getName();

            EntityBulletproofCamera camera = new EntityBulletproofCamera(world, pos, state.get(PropertyHolder.FACING_NOT_DOWN), placer.getUniqueID(), teamName);
            world.addEntity(camera);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (!world.isRemote && newState.getBlock() != state.getBlock())
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityBulletproofCamera)
            {
                TileEntityBulletproofCamera camTe = (TileEntityBulletproofCamera)te;
                camTe.removeCamera();
            }
        }
        super.onReplaced(state, world, pos, newState, isMoving);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return new TileEntityBulletproofCamera(state.get(PropertyHolder.FACING_NOT_DOWN));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
    {
        return SHAPES.getOrDefault(state.get(PropertyHolder.FACING_NOT_DOWN), VoxelShapes.fullCube());
    }

    private static Map<Direction, VoxelShape> createVoxelShapes()
    {
        Map<Direction, VoxelShape> shapes = new HashMap<>();
        Optional<VoxelShape> shape = Stream.of(
                Block.makeCuboidShape(0, 0, 3, 16, 0.5, 13),
                Block.makeCuboidShape(0.5, 0.5, 3.5, 1.5, 2, 4.5),
                Block.makeCuboidShape(0.5, 0.5, 11.5, 1.5, 2, 12.5),
                Block.makeCuboidShape(14.5, 0.5, 3.5, 15.5, 2, 4.5),
                Block.makeCuboidShape(14.5, 0.5, 11.5, 15.5, 2, 12.5),
                Block.makeCuboidShape(1.5, 2, 3.5, 14.5, 2.5, 12.5),
                Block.makeCuboidShape(0.5, 2, 3, 1.5, 2.5, 13),
                Block.makeCuboidShape(14.5, 2, 3, 15.5, 2.5, 13),
                Block.makeCuboidShape(1.5, 2, 3, 14.5, 2.5, 3.5),
                Block.makeCuboidShape(1.5, 2, 12.5, 14.5, 2.5, 13),
                Block.makeCuboidShape(2.25, 0.5, 4, 13.75, 1.5, 12),
                Block.makeCuboidShape(0, 0.5, 7.5, 1, 1.5, 11),
                Block.makeCuboidShape(-0.75, 0.75, 4.25, -0.25, 1.25, 8.25),
                Block.makeCuboidShape(-0.25, 0.75, 7.75, 0, 1.25, 8.25)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR));

        shape.ifPresent(voxelShape -> shapes.put(Direction.UP, voxelShape));

        shape = Stream.of(
                Block.makeCuboidShape(0, 3, 0, 16, 13, 0.5),
                Block.makeCuboidShape(0.5, 11.5, 0.5, 1.5, 12.5, 2),
                Block.makeCuboidShape(0.5, 3.5, 0.5, 1.5, 4.5, 2),
                Block.makeCuboidShape(14.5, 11.5, 0.5, 15.5, 12.5, 2),
                Block.makeCuboidShape(14.5, 3.5, 0.5, 15.5, 4.5, 2),
                Block.makeCuboidShape(1.5, 3.5, 2, 14.5, 12.5, 2.5),
                Block.makeCuboidShape(0.5, 3, 2, 1.5, 13, 2.5),
                Block.makeCuboidShape(14.5, 3, 2, 15.5, 13, 2.5),
                Block.makeCuboidShape(1.5, 12.5, 2, 14.5, 13, 2.5),
                Block.makeCuboidShape(1.5, 3, 2, 14.5, 3.5, 2.5),
                Block.makeCuboidShape(2.25, 4, 0.5, 13.75, 12, 1.5),
                Block.makeCuboidShape(0, 5, 0.5, 1, 8.5, 1.5),
                Block.makeCuboidShape(-0.75, 7.75, 0.75, -0.25, 11.75, 1.25),
                Block.makeCuboidShape(-0.25, 7.75, 0.75, 0, 8.25, 1.25)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR));

        shape.ifPresent(voxelShape ->
        {
            shapes.put(Direction.SOUTH, voxelShape);
            shapes.put(Direction.WEST, Utils.rotateShape(Direction.NORTH, Direction.EAST, voxelShape));
            shapes.put(Direction.NORTH, Utils.rotateShape(Direction.NORTH, Direction.SOUTH, voxelShape));
            shapes.put(Direction.EAST, Utils.rotateShape(Direction.NORTH, Direction.WEST, voxelShape));
        });

        return shapes;
    }
}