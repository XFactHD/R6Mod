package xfacthd.r6mod.api.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import xfacthd.r6mod.common.data.InteractState;

import java.util.UUID;

public interface IUsageTimeEntity
{
    InteractState interact(PlayerEntity player);

    int getUsageTime();

    int getCurrentTime(UUID interactor);

    void applySonicBurst(UUID interactor, int cooldown);

    TranslationTextComponent getUseMessage();
}