package xfacthd.r6mod.common.capability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.api.capability.ICapabilityDBNO;
import xfacthd.r6mod.common.net.NetworkHandler;
import xfacthd.r6mod.common.net.packets.dbno.*;
import xfacthd.r6mod.common.util.data.R6WorldSavedData;

import javax.annotation.Nonnull;

public class CapabilityDBNO implements ICapabilityDBNO
{
    public static final ResourceLocation KEY = new ResourceLocation(R6Mod.MODID, "capability_dbno");
    public static final ITextComponent PICKUP_MSG = new TranslationTextComponent("msg.r6mod.hold_revive");
    private static final int DBNO_TIME = 300;
    private static final float DBNO_POOL = 20F/5F;
    public static final int REVIVE_TIME = 80;
    private static final float REVIVE_POOL = 20F/5F;
    private static final int DEPLETE_NORMAL = 2;
    private static final int DEPLETE_HOLD = 1;
    private static final Vec3d MOTION_MULTIPLIER = new Vec3d(.3, 0, .3);

    //@CapabilityInject(ICapabilityDBNO.class) //TODO: reenable, causes debug class reload to fail
    public static final Capability<ICapabilityDBNO> DBNO_CAPABILITY = null;
    private static final ICapabilityDBNO DUMMY = new Empty();

    private final PlayerEntity player;
    private PlayerEntity helper;
    private boolean dbno = false;
    private boolean trapped = false;
    private boolean previouslyDbno = false;
    private int timeLeft = 0;
    private float hpPool = 0;
    private boolean holdingWound = false;
    private float lastHealth = 0;
    private DamageSource lastDMGSource = null;
    private float lastDmg = 0;
    private long startHelpTime = 0;
    private long lastHelpTime = 0;
    private boolean revived = false;
    private boolean dead = false;

    public CapabilityDBNO(PlayerEntity player) { this.player = player; }

    @Override
    public void tick()
    {
        if (dead || !dbno) { return; }

        if (!player.world.isRemote() && helper != null)
        {
            if (player.world.getGameTime() - lastHelpTime > 5)
            {
                startHelpTime = 0;
                lastHelpTime = 0;
                helper = null;
                informHelper(false);
                sendUpdatePacket();
            }
        }

        if (holdingWound || trapped)
        {
            player.setMotion(Vec3d.ZERO);
        }
        else
        {
            //noinspection ConstantConditions
            player.setMotionMultiplier(null, MOTION_MULTIPLIER);
        }

        //Reset death timer to avoid the root capability provider being invalidated
        player.deathTime = 0;

        if (helper == null) //Helping someone stops the timer
        {
            int timeDeplete = holdingWound ? DEPLETE_HOLD : DEPLETE_NORMAL;
            timeLeft -= timeDeplete;
            if (!player.world.isRemote()) { player.sendStatusMessage(new StringTextComponent("Dead in: " + timeLeft), true); }

            if (!player.world.isRemote() && timeLeft <= 0)
            {
                kill();
            }
        }
    }

    @Override
    public boolean putInDbno()
    {
        if (player.isCreative() || player.isSpectator()) { return false; }
        if (previouslyDbno) { return false; }

        //Can't immediatly die to fall damage
        float dmgBelowZero = lastDMGSource != DamageSource.FALL ? Math.abs(lastHealth - lastDmg) : 0F;
        hpPool = DBNO_POOL - dmgBelowZero;
        if (hpPool <= 0F) { return false; }

        dbno = true;
        previouslyDbno = true;
        timeLeft = DBNO_TIME;

        R6WorldSavedData.get((ServerWorld)player.world).getCameraManager().leaveCamera((ServerPlayerEntity)player);

        sendUpdatePacket();

        return true;
    }

    @Override
    public void putInTrapDbno(DamageSource source, BlockPos trapPos)
    {
        if (player.isCreative() || player.isSpectator()) { return; }

        dbno = true;
        previouslyDbno = true;
        trapped = true;
        lastDMGSource = source;
        timeLeft = DBNO_TIME;
        hpPool = DBNO_POOL;

        R6WorldSavedData.get((ServerWorld)player.world).getCameraManager().leaveCamera((ServerPlayerEntity)player);

        sendUpdatePacket();
    }

    @Override
    public void onAttacked(DamageSource source, float dmg)
    {
        lastHealth = player.getHealth();
        lastDMGSource = source;
        lastDmg = dmg;

        if (dbno)
        {
            hpPool -= dmg;
            if (hpPool <= 0F) { kill(); }
        }
    }

    @Override
    public void setHoldingWound(boolean holding) { this.holdingWound = holding; }

    @Override
    public void tryRevive(PlayerEntity helper)
    {
        if (this.helper != null && this.helper != helper) { return; }

        if (this.helper == null)
        {
            //Can't help if not in the same team
            if (player.getTeam() == null || !player.getTeam().isSameTeam(helper.getTeam())) { return; }

            this.helper = helper;
        }

        lastHelpTime = player.world.getGameTime();
        if (startHelpTime == 0)
        {
            startHelpTime = player.world.getGameTime();
            informHelper(true);
            sendUpdatePacket();
        }
        else if (lastHelpTime - startHelpTime >= REVIVE_TIME)
        {
            player.setHealth(REVIVE_POOL);

            startHelpTime = 0;
            lastHelpTime = 0;
            holdingWound = false;
            informHelper(false);
            this.helper = null;

            revived = true;
            sendUpdatePacket();
        }
    }

    @Override
    public boolean wasRevived()
    {
        if (revived)
        {
            revived = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean isDBNO() { return dbno; }

    @Override
    public boolean isDead() { return dead; }

    @Override
    public int getTimeLeft() { return timeLeft; }

    @Override
    public float getTimeLeftFactor() { return MathHelper.clamp((float)getTimeLeft() / (float)DBNO_TIME, 0F, 1F); }

    @Override
    public int getReviveTime()
    {
        if (startHelpTime == 0) { return 0; }
        return (int)(player.world.getGameTime() - startHelpTime);
    }

    @Override
    public float getReviveProgress() { return (float)getReviveTime() / (float)REVIVE_TIME; }

    private void kill()
    {
        dead = true;
        holdingWound = false;
        player.attackEntityFrom(lastDMGSource, Float.MAX_VALUE);
        sendUpdatePacket();
    }

    private void sendUpdatePacket()
    {
        NetworkHandler.sendToPlayer(new PacketDBNOState(dbno, trapped, holdingWound, dead, startHelpTime != 0), (ServerPlayerEntity)player);
    }

    @Override
    public void handleUpdatePacket(boolean dbno, boolean trapped, boolean holding, boolean dead, boolean reviving)
    {
        this.dbno = dbno;
        this.trapped = trapped;
        this.holdingWound = holding;
        this.dead = dead;

        if (startHelpTime == 0 && reviving)
        {
            startHelpTime = player.world.getGameTime();
        }
        else if (!reviving)
        {
            startHelpTime = 0;
        }
    }

    @Override
    public void informSpectator(PlayerEntity spectator)
    {
        NetworkHandler.sendToPlayer(new PacketInformSpectator(player, dbno, timeLeft), (ServerPlayerEntity)spectator);
    }

    @Override
    public void handleSpectatorPacket(boolean dbno, int timeLeft)
    {
        this.dbno = dbno;
        this.timeLeft = timeLeft;
    }

    private void informHelper(boolean helping)
    {
        NetworkHandler.sendToPlayer(new PacketInformHelper(player, dbno, helping), (ServerPlayerEntity)helper);
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putBoolean("dbno", dbno);
        nbt.putBoolean("trapped", trapped);
        nbt.putBoolean("previouslyDbno", previouslyDbno);
        nbt.putInt("timeLeft", timeLeft);
        nbt.putFloat("hpPool", hpPool);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        dbno = nbt.getBoolean("dbno");
        trapped = nbt.getBoolean("trapped");
        previouslyDbno = nbt.getBoolean("previouslyDbno");
        timeLeft = nbt.getInt("timeLeft");
        hpPool = nbt.getFloat("hpPool");
    }

    @SuppressWarnings("ConstantConditions")
    public static ICapabilityDBNO getFrom(PlayerEntity player) { return player.getCapability(DBNO_CAPABILITY).orElse(DUMMY); }

    public static class Empty implements ICapabilityDBNO
    {
        @Override
        public void tick() { }

        @Override
        public boolean putInDbno() { return false; }

        @Override
        public void putInTrapDbno(DamageSource source, BlockPos trapPos) { }

        @Override
        public void onAttacked(DamageSource source, float dmg) { }

        @Override
        public void setHoldingWound(boolean holding) { }

        @Override
        public void tryRevive(PlayerEntity helper) { }

        @Override
        public boolean wasRevived() { return false; }

        @Override
        public boolean isDBNO() { return false; }

        @Override
        public boolean isDead() { return false; }

        @Override
        public int getTimeLeft() { return 0; }

        @Override
        public float getTimeLeftFactor() { return 0; }

        @Override
        public int getReviveTime() { return 0; }

        @Override
        public float getReviveProgress() { return 0; }

        @Override
        public void handleUpdatePacket(boolean dbno, boolean trapped, boolean holding, boolean dead, boolean reviving) { }

        @Override
        public void informSpectator(PlayerEntity player) { }

        @Override
        public void handleSpectatorPacket(boolean dbno, int timeLeft) { }

        @Override
        public CompoundNBT serializeNBT() { return null; }

        @Override
        public void deserializeNBT(CompoundNBT nbt) { }
    }

    public static class Provider implements ICapabilityProvider
    {
        private final CapabilityDBNO instance;

        public Provider(PlayerEntity player) { instance = new CapabilityDBNO(player); }

        @Override
        @SuppressWarnings({"ConstantConditions", "unchecked"})
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side)
        {
            if (DBNO_CAPABILITY == null) { return LazyOptional.empty(); }

            if (cap != DBNO_CAPABILITY) { return LazyOptional.empty(); }
            return LazyOptional.of(() -> (T)instance);
        }
    }

    public static class Storage implements Capability.IStorage<ICapabilityDBNO>
    {
        @Override
        public INBT writeNBT(Capability<ICapabilityDBNO> capability, ICapabilityDBNO instance, Direction side)
        {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<ICapabilityDBNO> capability, ICapabilityDBNO instance, Direction side, INBT nbt)
        {
            instance.deserializeNBT((CompoundNBT)nbt);
        }
    }
}