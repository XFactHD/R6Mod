package xfacthd.r6mod.common.items.building;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import xfacthd.r6mod.api.interaction.IPlacementTime;
import xfacthd.r6mod.common.blocks.building.BlockBarricade;
import xfacthd.r6mod.common.data.PropertyHolder;

import java.util.*;

public class BlockItemBarricade extends BlockItem implements IPlacementTime
{
    private final TranslationTextComponent PLACE_MSG;

    public BlockItemBarricade(BlockBarricade block, Properties props)
    {
        super(block, props);
        PLACE_MSG = new TranslationTextComponent("msg.r6mod.place", new TranslationTextComponent(getTranslationKey()));
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context)
    {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        Direction side = context.getFace();
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getItem();

        //Can only be placed by a player
        if (player == null) { return ActionResultType.FAIL; }

        //Check if placed on glass panes
        BlockState state = world.getBlockState(pos);
        boolean onGlass = side.getHorizontalIndex() != -1 && isGlassPane(state);

        //If not an glass and not interacting with the bottom side or not with a solid block or with no space, cancel
        if (!onGlass && (side != Direction.DOWN || !state.isSolid() || !world.getBlockState(pos.down()).isAir(world, pos.down()))) { return ActionResultType.FAIL; }

        //Get actual top center position and block facing
        if (!onGlass) { pos = pos.down(); }
        Direction facing = onGlass ? side.getOpposite() : player.getHorizontalFacing();

        //Check if the open is 3*2 or 1*2 and check if the barricade would be completely surrounded
        boolean large = isLargeOpening(world, pos, onGlass, facing);
        if (isNotUpperBlockAndSurrounded(world, pos, onGlass, facing, large)) { return ActionResultType.FAIL; }

        //Check if the barricade should be a door
        boolean door = !onGlass && isDoor(world, pos, facing, large);

        //Make sure stack has an NBT tag
        if (!stack.hasTag()) { stack.setTag(new CompoundNBT()); }

        //Check placement time, if too low, don't proceed with placement
        int placeTime = getCurrentTime(world, stack);
        if (placeTime < getPlacementTime())
        {
            if (placeTime == 0)
            {
                //noinspection ConstantConditions
                stack.getTag().putLong("place_start", world.getGameTime());
            }
            //noinspection ConstantConditions
            stack.getTag().putLong("place_last", world.getGameTime());
            return ActionResultType.FAIL;
        }

        //Placement time over, remove tag to make stackable again
        stack.setTag(null);

        //Only place block on the server
        if (!world.isRemote())
        {
            //Get base state
            BlockState newState = getBlock().getDefaultState();
            newState = newState.with(PropertyHolder.FACING_HOR, facing)
                    .with(PropertyHolder.ON_GLASS, onGlass)
                    .with(PropertyHolder.LARGE, large)
                    .with(PropertyHolder.DOOR, door);

            if (large)
            {
                //Place center blocks
                placeBlock(world, pos,        newState.with(PropertyHolder.TOP, true).with(PropertyHolder.CENTER, true), player, stack);
                placeBlock(world, pos.down(), newState.with(PropertyHolder.TOP, false).with(PropertyHolder.CENTER, true), player, stack);

                //Place left blocks
                placeBlock(world,pos.offset(facing.rotateYCCW()),        newState.with(PropertyHolder.TOP, true).with(PropertyHolder.LEFT, true), player, stack);
                placeBlock(world,pos.offset(facing.rotateYCCW()).down(), newState.with(PropertyHolder.TOP, false).with(PropertyHolder.LEFT, true), player, stack);

                //Place right blocks
                placeBlock(world, pos.offset(facing.rotateY()),        newState.with(PropertyHolder.TOP, true).with(PropertyHolder.RIGHT, true), player, stack);
                placeBlock(world, pos.offset(facing.rotateY()).down(), newState.with(PropertyHolder.TOP, false).with(PropertyHolder.RIGHT, true), player, stack);
            }
            else
            {
                //Place blocks
                placeBlock(world, pos,        newState.with(PropertyHolder.TOP, true), player, stack);
                placeBlock(world, pos.down(), newState.with(PropertyHolder.TOP, false), player, stack);
            }
        }

        //Play placement sound
        SoundType type = getBlock().getDefaultState().getSoundType(world, pos, player);
        SoundEvent event = getPlaceSound(getBlock().getDefaultState(), world, pos, player);
        world.playSound(player, pos, event, SoundCategory.BLOCKS, (type.getVolume() + 1.0F) / 2.0F, type.getPitch() * 0.8F);

        return ActionResultType.SUCCESS;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean isSelected)
    {
        if (stack.hasTag())
        {
            //noinspection ConstantConditions
            long lastInteract = stack.getTag().getLong("place_last");
            if (world.getGameTime() - lastInteract > 5)
            {
                stack.setTag(null);
            }
        }
    }

    /*
     * IPlacementTime
     */

    @Override
    public int getCurrentTime(World world, ItemStack stack)
    {
        //noinspection ConstantConditions
        long startInteract = stack.hasTag() ? stack.getTag().getLong("place_start") : 0;
        return startInteract != 0 ? (int)(world.getGameTime() - startInteract) : 0;
    }

    @Override
    public int getPlacementTime() { return 20; }

    @Override
    public void applySonicBurst(World world, ItemStack stack, int cooldown)
    {
        if (!stack.hasTag()) { return; } //If the stack has no tag, it definitely isn't being used

        if (getCurrentTime(world, stack) != 0) //Item in use
        {
            //noinspection ConstantConditions
            stack.getTag().putLong("place_start", world.getGameTime() + cooldown);
        }
    }

    @Override
    public TranslationTextComponent getPlaceMessage() { return PLACE_MSG; }

    /*
     * Placement helpers
     */

    private static boolean isGlassPane(BlockState state) { return state.getBlock() == Blocks.GLASS_PANE; }

    private static boolean isLargeOpening(World world, BlockPos origin, boolean onGlass, Direction facing)
    {
        BlockPos[] posArr = getOpeningPositions(origin, facing);
        for (BlockPos pos : posArr)
        {
            BlockState state = world.getBlockState(pos);
            if (onGlass && !isGlassPane(state)) { return false; }
            if (!onGlass && !state.isAir(world, pos)) { return false; }
        }
        return true;
    }

    private static boolean isNotUpperBlockAndSurrounded(World world, BlockPos origin, boolean onGlass, Direction facing, boolean large)
    {
        BlockState below = world.getBlockState(origin.down());
        if ((!onGlass && below.isAir(world, origin.down())) || (onGlass && isGlassPane(below)))
        {
            BlockPos[] around = getSurroundingPositions(origin, facing, large);
            for (BlockPos pos : around)
            {
                BlockState state = world.getBlockState(pos);
                if (!state.isSolid()) { return true; }
            }
            return false;
        }
        return true;
    }

    @SuppressWarnings("RedundantIfStatement")
    private static boolean isDoor(World world, BlockPos pos, Direction facing, boolean large)
    {
        pos = pos.down(2);

        if (isAir(world, pos.offset(facing))) { return false; }
        if (isAir(world, pos.offset(facing.getOpposite()))) { return false; }

        if (large)
        {
            if (isAir(world, pos.offset(facing.rotateY()).offset(facing))) { return false; }
            if (isAir(world, pos.offset(facing.rotateY()).offset(facing.getOpposite()))) { return false; }

            if (isAir(world, pos.offset(facing.rotateYCCW()).offset(facing))) { return false; }
            if (isAir(world, pos.offset(facing.rotateYCCW()).offset(facing.getOpposite()))) { return false; }
        }

        return true;
    }

    private static boolean isAir(World world, BlockPos pos) { return world.getBlockState(pos).isAir(world, pos); }

    private static BlockPos[] getOpeningPositions(BlockPos origin, Direction facing)
    {
        return new BlockPos[]
                {
                        origin,
                        origin.down(),
                        origin.offset(facing.rotateYCCW()),
                        origin.offset(facing.rotateY()),
                        origin.down().offset(facing.rotateYCCW()),
                        origin.down().offset(facing.rotateY())
                };
    }

    private static BlockPos[] getSurroundingPositions(BlockPos origin, Direction facing, boolean large)
    {
        List<BlockPos> positions = new ArrayList<>(Arrays.asList(
                origin.up(), //Above
                origin.down(2), //Below
                origin.offset(facing.rotateYCCW(), large ? 2 : 1), //Upper left
                origin.offset(facing.rotateY(), large ? 2 : 1), //Upper right
                origin.down().offset(facing.rotateYCCW(), large ? 2 : 1), //Lower left
                origin.down().offset(facing.rotateY(), large ? 2 : 1) //Lower right
        ));

        if (large)
        {
            positions.addAll(Arrays.asList(
                    origin.up().offset(facing.rotateYCCW()), //Left above
                    origin.up().offset(facing.rotateY()), //Right above
                    origin.down(2).offset(facing.rotateYCCW()), //Left below
                    origin.down(2).offset(facing.rotateY()) //Right below
            ));
        }

        return positions.toArray(new BlockPos[0]);
    }

    private static void placeBlock(World world, BlockPos pos, BlockState state, PlayerEntity player, ItemStack stack)
    {
        world.setBlockState(pos, state);
        state.getBlock().onBlockPlacedBy(world, pos, state, player, stack);
    }
}