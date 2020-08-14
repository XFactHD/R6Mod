package xfacthd.r6mod.api.interaction;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface IReloadable
{
    //Used by the reload packet
    void reload(ItemStack stack, PlayerEntity player);

    //Used by the ammo box
    void restock(ItemStack stack, PlayerEntity player, int slot);
}