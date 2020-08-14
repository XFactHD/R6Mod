package xfacthd.r6mod.api.capability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;

public interface ICapabilityDBNO extends INBTSerializable<CompoundNBT>
{
    void tick();

    boolean putInDbno();

    void putInTrapDbno(DamageSource source, BlockPos trapPos);

    void onAttacked(DamageSource source, float dmg);

    void setHoldingWound(boolean holding);

    void tryRevive(PlayerEntity helper);

    boolean wasRevived();

    boolean isDBNO();

    boolean isDead();

    int getTimeLeft();

    float getTimeLeftFactor();

    int getReviveTime();

    float getReviveProgress();

    void handleUpdatePacket(boolean dbno, boolean trapped, boolean holding, boolean dead, boolean reviving);

    void informSpectator(PlayerEntity player);

    void handleSpectatorPacket(boolean dbno, int timeLeft);
}