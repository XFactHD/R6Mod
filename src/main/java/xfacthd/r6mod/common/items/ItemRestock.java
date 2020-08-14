package xfacthd.r6mod.common.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.world.World;

//Used for items that automatically restock over time
public abstract class ItemRestock extends Item
{
    private final int initialCount;
    private final int maxCount;
    private final long restockTime;
    private final boolean infinite;

    public ItemRestock(Item.Properties props, int initialCount, int maxCount, long restockTime, boolean infinite)
    {
        super(props.maxStackSize(1));
        this.initialCount = initialCount;
        this.maxCount = maxCount;
        this.restockTime = restockTime;
        this.infinite = infinite;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean isSelected)
    {
        if (!stack.hasTag())
        {
            stack.setTag(new CompoundNBT());
            //noinspection ConstantConditions
            stack.getTag().putInt("count", initialCount);
            if (!infinite) { stack.getTag().putInt("available", initialCount); }
        }

        if (!world.isRemote())
        {
            //noinspection ConstantConditions
            int count = stack.getTag().getInt("count");
            long lastRestock = stack.getTag().getLong("lastRestock");
            if (count < maxCount)
            {
                if (lastRestock == 0)
                {
                    stack.getTag().putLong("lastRestock", world.getGameTime());
                }
                else if (world.getGameTime() - lastRestock >= restockTime)
                {
                    stack.getTag().putLong("lastRestock", world.getGameTime());

                    stack.getTag().putInt("count", count + 1);
                    if (!infinite)
                    {
                        stack.getTag().putInt("available", stack.getTag().getInt("available") + 1);
                    }
                }
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
    {
        if (!world.isRemote())
        {
            ItemStack stack = player.getHeldItem(hand);
            if (stack.hasTag())
            {
                //noinspection ConstantConditions
                int count = infinite ? stack.getTag().getInt("count") : stack.getTag().getInt("available");
                if (count > 0)
                {
                    ActionResult<ItemStack> result = useItem(stack, world, player, hand);
                    if (result.getType() == ActionResultType.SUCCESS)
                    {
                        if (infinite) { stack.getTag().putInt("count", stack.getTag().getInt("count") - 1); }
                        else { stack.getTag().putInt("available", stack.getTag().getInt("available") - 1); }
                    }
                    return result;
                }
            }
        }
        return super.onItemRightClick(world, player, hand);
    }

    protected abstract ActionResult<ItemStack> useItem(ItemStack stack, World world, PlayerEntity player, Hand hand);

    public int getCount(ItemStack stack)
    {
        if (!stack.hasTag()) { return -1; }

        //noinspection ConstantConditions
        return infinite ? stack.getTag().getInt("count") : stack.getTag().getInt("available");
    }

    public float getRestockProgress(ItemStack stack, World world)
    {
        if (!stack.hasTag()) { return 0F; }

        //noinspection ConstantConditions
        if (stack.getTag().getInt("count") >= maxCount) { return 0F; }

        long lastRestock = stack.getTag().getLong("lastRestock");
        float diff = (float)(world.getGameTime() - lastRestock);
        return diff / (float)restockTime;
    }

    public int getBarColor(ItemStack stack, World world) { return 0xFFFFFFFF; }
}