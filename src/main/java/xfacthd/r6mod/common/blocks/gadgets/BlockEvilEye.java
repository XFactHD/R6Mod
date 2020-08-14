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
import xfacthd.r6mod.common.entities.camera.EntityEvilEyeCamera;
import xfacthd.r6mod.common.tileentities.gadgets.TileEntityEvilEye;
import xfacthd.r6mod.common.util.Utils;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Stream;

public class BlockEvilEye extends BlockGadget
{
    private static final Map<Direction, VoxelShape> SHAPES = createVoxelShapes();

    public BlockEvilEye()
    {
        super("block_evil_eye",
                Properties.create(Material.IRON)
                .notSolid()
                .hardnessAndResistance(5.0F, 12000.0F)
                .harvestTool(ToolType.PICKAXE)
                .sound(SoundType.METAL),
                EnumGadget.EVIL_EYE);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_NOT_DOWN);
    }

    @Override
    protected BlockState getStateForPlacementOwnable(BlockItemUseContext context)
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

            EntityEvilEyeCamera camera = new EntityEvilEyeCamera(world, pos, state.get(PropertyHolder.FACING_NOT_DOWN), placer.getUniqueID(), teamName);
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
            if (te instanceof TileEntityEvilEye)
            {
                TileEntityEvilEye camTe = (TileEntityEvilEye)te;
                camTe.removeCamera();
            }
        }
        super.onReplaced(state, world, pos, newState, isMoving);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
    {
        return SHAPES.getOrDefault(state.get(PropertyHolder.FACING_NOT_DOWN), VoxelShapes.fullCube());
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return new TileEntityEvilEye(state.get(PropertyHolder.FACING_NOT_DOWN));
    }

    private static Map<Direction, VoxelShape> createVoxelShapes()
    {
        Map<Direction, VoxelShape> shapes = new HashMap<>();
        Optional<VoxelShape> shape = Stream.of(
                Block.makeCuboidShape(2, 0, 2, 14, 2, 14),
                Block.makeCuboidShape(0, 0, 0, 3, 2, 3),
                Block.makeCuboidShape(13, 0, 0, 16, 2, 3),
                Block.makeCuboidShape(0, 0, 13, 3, 2, 16),
                Block.makeCuboidShape(13, 0, 13, 16, 2, 16),
                Block.makeCuboidShape(-1, 1, 4.6, 2, 1.4, 5),
                Block.makeCuboidShape(-1, 1, 11, 2, 1.4, 11.4),
                Block.makeCuboidShape(-1.4, 1, 4.6, -1, 1.4, 11.4),
                Block.makeCuboidShape(14, 1, 4.6, 17, 1.4, 5),
                Block.makeCuboidShape(14, 1, 11, 17, 1.4, 11.4),
                Block.makeCuboidShape(17, 1, 4.6, 17.4, 1.4, 11.4),
                Block.makeCuboidShape(2.5, 2, 2.5, 13.5, 14, 13.5)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR));

        shape.ifPresent(voxelShape -> shapes.put(Direction.UP, voxelShape));

        shape = Stream.of(
                Block.makeCuboidShape(2, 2, 14, 14, 14, 16),
                Block.makeCuboidShape(0, 0, 14, 3, 3, 16),
                Block.makeCuboidShape(13, 0, 14, 16, 3, 16),
                Block.makeCuboidShape(0, 13, 14, 3, 16, 16),
                Block.makeCuboidShape(13, 13, 14, 16, 16, 16),
                Block.makeCuboidShape(-1, 4.6, 14.6, 2, 5, 15),
                Block.makeCuboidShape(-1, 11, 14.6, 2, 11.4, 15),
                Block.makeCuboidShape(-1.4, 4.6, 14.6, -1, 11.4, 15),
                Block.makeCuboidShape(14, 4.6, 14.6, 17, 5, 15),
                Block.makeCuboidShape(14, 11, 14.6, 17, 11.4, 15),
                Block.makeCuboidShape(17, 4.6, 14.6, 17.4, 11.4, 15),
                Block.makeCuboidShape(2.5, 2.5, 2, 13.5, 13.5, 14)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR));

        shape.ifPresent(voxelShape ->
        {
            shapes.put(Direction.NORTH, voxelShape);
            shapes.put(Direction.EAST, Utils.rotateShape(Direction.NORTH, Direction.EAST, voxelShape));
            shapes.put(Direction.SOUTH, Utils.rotateShape(Direction.NORTH, Direction.SOUTH, voxelShape));
            shapes.put(Direction.WEST, Utils.rotateShape(Direction.NORTH, Direction.WEST, voxelShape));
        });

        return shapes;
    }
}