package XFactHD.rssmc.api.item;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@SuppressWarnings("ConstantConditions")
public interface ICooldown
{
    int getCooldownTime(ItemStack stack);

    default int getCurrentTime(ItemStack stack, World world)
    {
        if (!stack.hasTagCompound()) { return 0; }
        return (int) (world.getTotalWorldTime() - stack.getTagCompound().getLong("time"));
    }

    default int getBarColor(ItemStack stack, World world) { return -1; }
}
