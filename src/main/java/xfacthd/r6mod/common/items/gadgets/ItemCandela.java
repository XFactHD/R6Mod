package xfacthd.r6mod.common.items.gadgets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.world.World;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.client.render.ister.RenderCandela;
import xfacthd.r6mod.common.data.ItemGroups;
import xfacthd.r6mod.common.entities.grenade.EntityCandelaGrenade;

public class ItemCandela extends Item
{
    public static final int FUSE_TIME = 120;
    private static final int PHASE_0 = 40;
    private static final int PHASE_1 = 80;
    private static final int PHASE_2 = 120;

    public ItemCandela()
    {
        super(new Properties().group(ItemGroups.GADGETS).setISTER(() -> RenderCandela::new));
        setRegistryName(R6Mod.MODID, "item_candela");
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
    {
        if (stack.hasTag() && entity instanceof PlayerEntity)
        {
            //noinspection ConstantConditions
            long lastInteract = stack.getTag().getLong("use_last");
            if (lastInteract != 0 && world.getGameTime() - lastInteract > 5)
            {
                PlayerEntity player = (PlayerEntity)entity;

                //If the stack is still selected, the player just stopped using it
                if (selected && !world.isRemote())
                {
                    String team = player.getTeam() != null ? player.getTeam().getName() : "null";
                    int time = Math.min(getCurrentTime(stack, world), FUSE_TIME);

                    EntityCandelaGrenade candela = new EntityCandelaGrenade(world, player, team, time);
                    world.addEntity(candela);

                    if (!player.isCreative())
                    {
                        stack.shrink(1);
                        player.inventory.markDirty();
                    }
                }
                else if (!selected && !world.isRemote())
                {
                    ItemYingGlasses.switchGlasses(player, false);
                }
                stack.setTag(null);
            }
        }
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context)
    {
        if (context.getPlayer() == null) { return ActionResultType.FAIL; }
        return handleRightClick(context.getWorld(), context.getItem(), context.getPlayer(), context.getHand()).getType();
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
    {
        return handleRightClick(world, player.getHeldItem(hand), player, hand);
    }

    private ActionResult<ItemStack> handleRightClick(World world, ItemStack stack, PlayerEntity player, Hand hand)
    {
        if (hand == Hand.MAIN_HAND)
        {
            if (!stack.hasTag()) { stack.setTag(new CompoundNBT()); }

            ItemYingGlasses.switchGlasses(player, true);

            int placeTime = getCurrentTime(stack, world);
            if (placeTime == 0)
            {
                //noinspection ConstantConditions
                stack.getTag().putLong("use_start", world.getGameTime());
            }
            //noinspection ConstantConditions
            stack.getTag().putLong("use_last", world.getGameTime());
            return ActionResult.resultPass(stack);
        }
        return ActionResult.resultFail(stack);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        return oldStack.getItem() != newStack.getItem() || slotChanged;
    }

    public int getCurrentTime(ItemStack stack, World world)
    {
        if (stack.hasTag())
        {
            //noinspection ConstantConditions
            long startUse = stack.getTag().getLong("use_start");
            if (startUse != 0)
            {
                return (int)(world.getGameTime() - startUse);
            }
        }
        return 0;
    }

    public enum State
    {
        ZERO,
        ONE_BLINK,
        ONE_SOLID,
        TWO_BLINK,
        TWO_SOLID,
        THREE_BLINK,
        THREE_SOLID;

        public static State fromTime(int time)
        {
            if (time >= PHASE_2)
            {
                return THREE_SOLID;
            }
            else if (time >= PHASE_1)
            {
                return THREE_BLINK;
            }
            else if (time >= PHASE_0)
            {
                return TWO_BLINK;
            }
            else if (time > 0)
            {
                return ONE_BLINK;
            }
            return ZERO;
        }
    }
}