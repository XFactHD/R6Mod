package xfacthd.r6mod.api.tileentity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import xfacthd.r6mod.common.data.InteractState;

public interface IUsageTimeTile
{
    InteractState interact(PlayerEntity player);

    int getUsageTime();

    int getCurrentTime(PlayerEntity player);

    void applySonicBurst(PlayerEntity player, int cooldown);

    TranslationTextComponent getUseMessage();
}