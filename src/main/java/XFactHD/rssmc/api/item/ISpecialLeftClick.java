package XFactHD.rssmc.api.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public interface ISpecialLeftClick
{
    void startLeftClick(ItemStack stack, EntityPlayer player, World world, EnumHand hand);

    void stopLeftClick(ItemStack stack, EntityPlayer player, World world, EnumHand hand);

    void scrollOff(ItemStack stack, EntityPlayer player, World world, EnumHand hand);
}
