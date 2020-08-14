package xfacthd.r6mod.common.capability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.api.capability.ICapabilityEffect;
import xfacthd.r6mod.common.net.NetworkHandler;
import xfacthd.r6mod.common.net.packets.capability.PacketUpdateFinkaBoost;
import xfacthd.r6mod.common.net.packets.capability.PacketUpdateStimState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilityEffect implements ICapabilityEffect
{
    public static final ResourceLocation KEY = new ResourceLocation(R6Mod.MODID, "capability_effect");

    //@CapabilityInject(ICapabilityEffect.class) //TODO: reenable, causes debug class reload to fail
    public static final Capability<ICapabilityEffect> EFFECT_CAPABILITY = null;
    private static final ICapabilityEffect DUMMY = new Empty();

    public static final float BOOST_POOL_MAX = 20F / 5F;
    public static final float STIM_POOL_MAX = 40F / 5F;
    public static final long STIM_DEPLETE_INTERVAL = 20;
    private static final float STIM_DEPLETE_TICK = 1F / 5F;

    private final PlayerEntity player;
    private boolean boostActive = false;
    private float boostPool = 0F; //Health pool from finka boost
    private long stimStart = 0;   //Point in time where player was hit by stim shot
    private float stimPool = 0F;  //Health pool from stim shot

    public CapabilityEffect(PlayerEntity player) { this.player = player; }

    @Override
    public void tick()
    {
        long diff = player.world.getGameTime() - stimStart;
        if (stimPool > 0F && diff > 0 && diff % STIM_DEPLETE_INTERVAL == 0)
        {
            stimPool = Math.max(stimPool - STIM_DEPLETE_TICK, 0F);
            NetworkHandler.sendToPlayer(new PacketUpdateStimState(stimPool), (ServerPlayerEntity)player);
        }
    }

    @Override
    public float onPlayerAttacked(float dmg)
    {
        if (boostActive)
        {
            boolean changed = false;

            if (dmg > boostPool)
            {
                dmg -= boostPool;
                boostPool = 0;
                changed = true;
            }
            else if (dmg > 0F)
            {
                boostPool -= dmg;
                dmg = 0F;
                changed = true;
            }

            if (changed) { NetworkHandler.sendToPlayer(new PacketUpdateFinkaBoost(boostPool), (ServerPlayerEntity)player); }
        }

        if (stimPool > 0F)
        {
            boolean changed = false;

            if (dmg > stimPool)
            {
                dmg -= stimPool;
                stimPool = 0;
                changed = true;
            }
            else if (dmg > 0F)
            {
                stimPool -= dmg;
                dmg = 0F;
                changed = true;
            }

            if (changed) { NetworkHandler.sendToPlayer(new PacketUpdateStimState(stimPool), (ServerPlayerEntity)player); }
        }
        return dmg;
    }

    @Override
    public void addFinkaBoost()
    {
        if (!boostActive)
        {
            boostActive = true;
            boostPool = BOOST_POOL_MAX;

            NetworkHandler.sendToPlayer(new PacketUpdateFinkaBoost(BOOST_POOL_MAX), (ServerPlayerEntity)player);
        }
    }

    @Override
    public void removeFinkaBoost()
    {
        if (boostActive)
        {
            boostActive = false;
            boostPool = 0F;

            NetworkHandler.sendToPlayer(new PacketUpdateFinkaBoost(0F), (ServerPlayerEntity)player);
        }
    }

    @Override
    public void applyStimShot()
    {
        float health = player.getHealth();
        if (health < player.getMaxHealth())
        {
            stimPool = Math.max(STIM_POOL_MAX - (player.getMaxHealth() - health), 0F);
            health += Math.min(STIM_POOL_MAX, player.getMaxHealth() - health);
            player.setHealth(health);
        }
        else
        {
            stimPool = STIM_POOL_MAX;
        }
        stimStart = player.world.getGameTime();

        NetworkHandler.sendToPlayer(new PacketUpdateStimState(stimPool), (ServerPlayerEntity)player);
    }

    @Override
    public void setBoostPoolClient(float pool) { boostPool = pool; }

    @Override
    public void setStimPoolClient(float pool) { stimPool = pool; }

    @Override
    public float getBoostPool() { return boostPool; }

    @Override
    public float getStimPool() { return stimPool; }

    @Override
    public void invalidate()
    {
        boostActive = false;
        boostPool = 0F;

        stimStart = 0;
        stimPool = 0F;
    }

    @SuppressWarnings("ConstantConditions")
    public static ICapabilityEffect getFrom(PlayerEntity player) { return player.getCapability(EFFECT_CAPABILITY).orElse(DUMMY); }

    public static class Empty implements ICapabilityEffect
    {
        @Override
        public void tick() { }

        @Override
        public float onPlayerAttacked(float dmg) { return dmg; }

        @Override
        public void addFinkaBoost() { }

        @Override
        public void removeFinkaBoost() { }

        @Override
        public void applyStimShot() { }

        @Override
        public void setBoostPoolClient(float pool) { }

        @Override
        public void setStimPoolClient(float pool) { }

        @Override
        public float getBoostPool() { return 0; }

        @Override
        public float getStimPool() { return 0; }

        @Override
        public void invalidate() { }
    }

    public static class Provider implements ICapabilityProvider
    {
        private final CapabilityEffect instance;

        public Provider(PlayerEntity player) { instance = new CapabilityEffect(player); }

        @Override
        @SuppressWarnings({"ConstantConditions", "unchecked"})
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
        {
            //INFO: makes sure to only return an instance if the capability is actually registered
            if (EFFECT_CAPABILITY == null) { return LazyOptional.empty(); }

            if (cap != EFFECT_CAPABILITY) { return LazyOptional.empty(); }
            return LazyOptional.of(() -> (T)instance);
        }
    }

    public static class Storage implements Capability.IStorage<ICapabilityEffect>
    {
        @Override
        public INBT writeNBT(Capability<ICapabilityEffect> capability, ICapabilityEffect instance, Direction side) { return null; }

        @Override
        public void readNBT(Capability<ICapabilityEffect> capability, ICapabilityEffect instance, Direction side, INBT nbt) { }
    }
}