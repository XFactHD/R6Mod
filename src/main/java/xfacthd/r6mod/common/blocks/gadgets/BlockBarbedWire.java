package xfacthd.r6mod.common.blocks.gadgets;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import xfacthd.r6mod.common.R6Content;
import xfacthd.r6mod.common.blocks.BlockGadget;
import xfacthd.r6mod.common.data.PropertyHolder;
import xfacthd.r6mod.common.data.blockdata.WallSegment;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.tileentities.gadgets.TileEntityBarbedWire;

import javax.annotation.Nullable;
import java.util.*;

@SuppressWarnings("deprecation")
public class BlockBarbedWire extends BlockGadget
{
    private static final Map<WallSegment, VoxelShape> SHAPES = createShapes();
    private static final Vec3d MOTION_MULT = new Vec3d(.65, .65, .65);

    public BlockBarbedWire()
    {
        super("block_barbed_wire",
                Properties.create(Material.IRON)
                        .doesNotBlockMovement()
                        .notSolid()
                        .hardnessAndResistance(4f, 4F)
                        .noDrops(),
                EnumGadget.BARBED_WIRE);

        setDefaultState(getDefaultState().with(PropertyHolder.ELECTRIFIED, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.SQUARE_SEGMENT, PropertyHolder.ELECTRIFIED);
    }

    @Override
    protected BlockState getStateForPlacementOwnable(BlockItemUseContext context)
    {
        if (context.getFace() != Direction.UP) { return null; }

        World world = context.getWorld();
        BlockPos pos = context.getPos();

        Vec3d hitVec = context.getHitVec();
        Vec3d subHit = hitVec.subtract(Math.floor(hitVec.getX()), Math.floor(hitVec.getY()), Math.floor(hitVec.getZ()));
        boolean right = subHit.getX() < .5D;
        boolean top = subHit.getZ() > .5D;

        boolean space = true;
        Direction xOff = right ? Direction.WEST : Direction.EAST;
        Direction zOff = top ? Direction.SOUTH : Direction.NORTH;
        if (!world.isAirBlock(pos.offset(xOff))) { space = false; }
        if (!world.isAirBlock(pos.offset(zOff))) { space = false; }
        if (!world.isAirBlock(pos.offset(xOff).offset(zOff))) { space = false; }

        if (!space) { return null; }

        WallSegment segment = WallSegment.squareFromBools(top, right);
        return getDefaultState().with(PropertyHolder.SQUARE_SEGMENT, segment);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
    {
        return SHAPES.get(state.get(PropertyHolder.SQUARE_SEGMENT));
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        super.onBlockPlacedBy(world, pos, state, placer, stack);

        if (!(placer instanceof PlayerEntity)) { return; }

        WallSegment segment = state.get(PropertyHolder.SQUARE_SEGMENT);
        Direction xOff = segment.isRight() ? Direction.WEST : Direction.EAST;
        Direction zOff = segment.isTop() ? Direction.SOUTH : Direction.NORTH;

        WallSegment segX = segment.offsetSide(!segment.isRight());
        WallSegment segZ = segment.squareOffsetHeight(!segment.isTop());
        WallSegment segXZ = segX.squareOffsetHeight(!segment.isTop());

        placeSegment(world, pos.offset(xOff), state, segX, (PlayerEntity)placer);
        placeSegment(world, pos.offset(zOff), state, segZ, (PlayerEntity)placer);
        placeSegment(world, pos.offset(xOff).offset(zOff), state, segXZ, (PlayerEntity)placer);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityBarbedWire)
        {
            if (((TileEntityBarbedWire)te).shouldSlow(entity))
            {
                entity.setMotionMultiplier(state, MOTION_MULT);
                entity.setSprinting(false);
            }
        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult raytrace)
    {
        if (hand == Hand.OFF_HAND) { return ActionResultType.FAIL; }

        ItemStack stack = player.getHeldItemMainhand();
        if (stack.getItem() == R6Content.blockShockWire.asItem())
        {
            if (state.get(PropertyHolder.ELECTRIFIED)) { return ActionResultType.FAIL; }

            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityBarbedWire)
            {
                return ((TileEntityBarbedWire)te).placeShockWire(player);
            }
            return ActionResultType.FAIL;
        }
        else if (stack.isEmpty() && state.get(PropertyHolder.ELECTRIFIED) && player.isSneaking())
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityBarbedWire && ((TileEntityBarbedWire)te).isShockWireOwner(player))
            {
                return ((TileEntityBarbedWire)te).removeShockWire(player);
            }
        }
        return super.onBlockActivated(state, world, pos, player, hand, raytrace);
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (state.getBlock() != newState.getBlock())
        {
            if (!world.isRemote() && state.get(PropertyHolder.ELECTRIFIED))
            {
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof TileEntityBarbedWire)
                {
                    UUID shockWireOwner = ((TileEntityBarbedWire)te).getShockWireOwner();
                    if (shockWireOwner != null)
                    {
                        PlayerEntity player = world.getPlayerByUuid(shockWireOwner);
                        if (player != null)
                        {
                            player.addItemStackToInventory(new ItemStack(R6Content.blockShockWire));
                        }
                    }
                }
            }

            destroy(world, pos, state, null, null, null);
        }

        super.onReplaced(state, world, pos, newState, isMoving);
    }

    @Override
    public void destroy(World world, BlockPos pos, BlockState state, PlayerEntity player, EnumGadget source, Direction side)
    {
        WallSegment segment = state.get(PropertyHolder.SQUARE_SEGMENT);
        Direction xOff = segment.isRight() ? Direction.WEST : Direction.EAST;
        Direction zOff = segment.isTop() ? Direction.SOUTH : Direction.NORTH;

        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityBarbedWire) { ((TileEntityBarbedWire)te).onDestroyed(); }

        world.destroyBlock(pos.offset(xOff), false);
        world.destroyBlock(pos.offset(zOff), false);
        world.destroyBlock(pos.offset(xOff).offset(zOff), false);

        super.destroy(world, pos, state, player, source, side);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new TileEntityBarbedWire(); }

    private void placeSegment(World world, BlockPos pos, BlockState baseState, WallSegment segment, PlayerEntity player)
    {
        BlockState state = baseState.with(PropertyHolder.SQUARE_SEGMENT, segment);
        world.setBlockState(pos, state);

        //Call super to avoid trying to place the additional blocks again
        super.onBlockPlacedBy(world, pos, state, player, ItemStack.EMPTY);
    }

    private static Map<WallSegment, VoxelShape> createShapes()
    {
        Map<WallSegment, VoxelShape> shapes = new HashMap<>();

        shapes.put(WallSegment.TOP_LEFT,     makeCuboidShape(3, 0, 3, 16, 12, 16));
        shapes.put(WallSegment.TOP_RIGHT,    makeCuboidShape(0, 0, 3, 13, 12, 16));
        shapes.put(WallSegment.BOTTOM_LEFT,  makeCuboidShape(3, 0, 0, 16, 12, 13));
        shapes.put(WallSegment.BOTTOM_RIGHT, makeCuboidShape(0, 0, 0, 13, 12, 13));

        return shapes;
    }
}