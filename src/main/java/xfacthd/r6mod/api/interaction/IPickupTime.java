package xfacthd.r6mod.api.interaction;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import xfacthd.r6mod.common.data.InteractState;

import java.util.UUID;

public interface IPickupTime
{
    InteractState pickUp(PlayerEntity player);

    int getPickupTime();

    int getCurrentTime();

    void applySonicBurst(PlayerEntity player, int cooldown);

    UUID getPickupInteractor();

    TranslationTextComponent getPickupMessage();
}