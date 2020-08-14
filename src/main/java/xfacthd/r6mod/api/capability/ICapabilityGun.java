package xfacthd.r6mod.api.capability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import xfacthd.r6mod.common.data.gun_data.ReloadState;
import xfacthd.r6mod.common.data.itemsubtypes.*;

import java.util.List;

public interface ICapabilityGun extends INBTSerializable<CompoundNBT>
{
    void tick(PlayerEntity player, int slot, boolean selected);

    //Used by the reload packet
    void reload(PlayerEntity player);

    //Used by the ammo box
    void restock(PlayerEntity player, int slot);

    boolean isAiming();

    boolean isFiring();

    boolean isCharged();

    boolean isLoaded();

    int getLoadedBullets();

    List<EnumAttachment> getAttachments();

    boolean isAttachmentActive(EnumAttachment attachment);

    float getChargeState(long gameTime);

    float getAimState(long gameTime, float partialTicks);

    ReloadState getReloadState();

    float getReloadStateProgress(long gameTime);

    EnumGun getGun();

    void handleFiringPacket(PlayerEntity player, boolean mouseDown);

    void handleAimingPacket(PlayerEntity player, boolean mouseDown, boolean holdToAim);

    void handleCancelPacket(PlayerEntity player);

    void handleGunStatePacket(boolean aiming, boolean chambered, boolean charged, long chargeStart, boolean loaded, int bulletsLoaded);

    void handleReloadStatePacket(boolean reloading, ReloadState state, long stateStart);
}