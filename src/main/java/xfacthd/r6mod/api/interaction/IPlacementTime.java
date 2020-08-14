package xfacthd.r6mod.api.interaction;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public interface IPlacementTime
{
    int getPlacementTime();

    int getCurrentTime(World world, ItemStack stack);

    void applySonicBurst(World world, ItemStack stack, int cooldown);

    TranslationTextComponent getPlaceMessage();
}