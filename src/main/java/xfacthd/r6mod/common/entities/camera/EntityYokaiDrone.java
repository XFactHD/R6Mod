package xfacthd.r6mod.common.entities.camera;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.*;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import xfacthd.r6mod.api.client.ICameraOverlay;
import xfacthd.r6mod.api.interaction.*;
import xfacthd.r6mod.api.item.IUsageTimeItem;
import xfacthd.r6mod.client.gui.overlay.camera.OverlayYokaiDrone;
import xfacthd.r6mod.common.R6Content;
import xfacthd.r6mod.common.data.EnumCamera;
import xfacthd.r6mod.common.data.InteractState;
import xfacthd.r6mod.common.data.effects.EnumEffect;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.data.particledata.ParticleDataYokaiBlast;
import xfacthd.r6mod.common.data.types.EntityTypes;
import xfacthd.r6mod.common.entities.grenade.EntityEMPGrenade;
import xfacthd.r6mod.common.event.EffectEventHandler;
import xfacthd.r6mod.common.net.NetworkHandler;
import xfacthd.r6mod.common.net.packets.camera.PacketCameraKeyInput;
import xfacthd.r6mod.common.net.packets.camera.PacketCameraLeftClick;
import xfacthd.r6mod.common.util.*;
import xfacthd.r6mod.common.util.data.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

public class EntityYokaiDrone extends AbstractEntityCamera<EntityYokaiDrone> implements IShootable, IPickupTime
{
    private static final DataParameter<Integer> PARAM_AMMO = EntityDataManager.createKey(EntityYokaiDrone.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> PARAM_CEILING = EntityDataManager.createKey(EntityYokaiDrone.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> PARAM_CLOAK = EntityDataManager.createKey(EntityYokaiDrone.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Long> PARAM_RELOAD = EntityDataManager.createKey(EntityYokaiDrone.class, R6DataSerializers.LONG);
    private static final DataParameter<Boolean> PARAM_JAMMED = EntityDataManager.createKey(EntityYokaiDrone.class, DataSerializers.BOOLEAN);

    private static final float GRAVITY = 0.03F;
    private static final long TIME_TO_CLOAK = 40;
    private static final long TIME_TO_RELOAD = 400;
    private static final long FIRE_COOLDOWN = 10;
    private static final long JUMP_COOLDOWN = 40;
    public static final int MAX_AMMO = 2;
    private static final int EFFECT_DURATION = 200;
    public static final int INTERACT_COOLDOWN = 10;

    private static Field EYE_HEIGHT;

    private boolean onCeiling = false;
    private boolean canMove = false;
    private double motionForward = 0;
    private double motionStrafe = 0;
    private long lastJumpStamp = 0;
    private long lastShotStamp = 0;
    private long reloadTimerStamp = 0;
    private long cloakTimerStamp = 0;
    private boolean cloaked = false;
    private int ammo = MAX_AMMO;
    private byte lastKeyMask = 0; //Mask for key inputs
    private boolean jammed = false;
    private long jamStart = 0;
    private long pickupStart = 0;
    private long lastInteract = 0;

    public EntityYokaiDrone(World world)
    {
        super(EntityTypes.entityTypeYokaiDrone, world);
        stepHeight = .5F;
    }

    public EntityYokaiDrone(World world, PlayerEntity thrower, String team)
    {
        super(EntityTypes.entityTypeYokaiDrone, world, thrower.getUniqueID(), team);

        stepHeight = .5F;

        forceSetPosition(thrower.getPosX(), thrower.getPosY() + thrower.getEyeHeight(), thrower.getPosZ());
        setRotation(thrower.rotationYaw, 0);

        float x = -MathHelper.sin(thrower.rotationYaw * ((float)Math.PI / 180F)) * MathHelper.cos(thrower.rotationPitch * ((float)Math.PI / 180F));
        float y = -MathHelper.sin(thrower.rotationPitch * ((float)Math.PI / 180F));
        float z = MathHelper.cos(thrower.rotationYaw * ((float)Math.PI / 180F)) * MathHelper.cos(thrower.rotationPitch * ((float)Math.PI / 180F));
        setMotion(new Vector3d(x, y, z).normalize().scale(0.5D));
    }

    @Override
    public void tick()
    {
        super.tick();

        //Check and reset interaction timer
        if (lastInteract != 0)
        {
            if (world.getGameTime() - lastInteract > 5)
            {
                pickupStart = 0;
                lastInteract = 0;
            }
        }

        if (world.isRemote()) { return; }

        tickMotion();

        //Tick cloak timer
        if (!cloaked && onCeiling)
        {
            if (world.getGameTime() - cloakTimerStamp > TIME_TO_CLOAK)
            {
                setCloaked(true);
            }
        }

        //Reload
        if (ammo < MAX_AMMO)
        {
            if (reloadTimerStamp == 0)
            {
                reloadTimerStamp = world.getGameTime();
                dataManager.set(PARAM_RELOAD, reloadTimerStamp);
            }
            else if (world.getGameTime() - reloadTimerStamp >= TIME_TO_RELOAD)
            {
                setAmmo(ammo + 1);
                reloadTimerStamp = ammo < MAX_AMMO ? world.getGameTime() : 0;
                dataManager.set(PARAM_RELOAD, reloadTimerStamp);
            }
        }

        //Tick jam timer
        if (world.getGameTime() - jamStart > EntityEMPGrenade.EFFECT_TIME)
        {
            jammed = false;
            dataManager.set(PARAM_JAMMED, false);
        }
    }

    @Override
    protected void firstTick()
    {
        super.firstTick();

        if (!world.isRemote)
        {
            //Force update to make sure the client gets this info
            setOnCeiling(onCeiling);
            setCloaked(cloaked);
            setAmmo(ammo);
        }
    }

    private void tickMotion()
    {
        //If the drone is on the ceiling, no motion is possible
        if (onCeiling)
        {
            setMotion(0, GRAVITY, 0); //The GRAVITY constant is used upwards to get some small motion to cause a collision check
            Vector3d pos = getPositionVec();
            move(MoverType.SELF, getMotion());
            if (!collidedVertically) //Block above the drone was removed
            {
                setLocationAndAngles(pos.x, pos.y, pos.z, rotationYaw, rotationPitch);
                setOnCeiling(false);
                setMotion(0, -.2D, 0);

                setCloaked(false);
            }
            return;
        }

        Vector3d motion = getMotion();

        //Move to new position
        move(MoverType.SELF, getMotion());
        if (motion.y > 0 && collidedVertically && !onCeiling)
        {
            //If the drone is at the "border" to the next block, push the pos up, else use it as is
            BlockPos pos = getPosYHeight(1) < Math.ceil(getPosY()) ? getOnPosition() : getOnPosition().up();
            if (WorldUtils.isBottomSolid(world, pos))
            {
                setOnCeiling(true);
                cloakTimerStamp = world.getGameTime();
            }
            else { setMotion(0, -.2D, 0); } //If the drone can't stick, it falls down
        }

        //Calculate new motion
        double drag = isInWater() ? 0.8D : 0.99D;
        double motionX = (motion.x * drag);
        double motionY = (motion.y * drag) - GRAVITY;
        double motionZ = (motion.z * drag);
        if (onGround)
        {
            canMove = true;
            motionX *= .8D;
            motionY = -GRAVITY; //Can't accelerate downwards if already on the ground => reset to small value to still cause collision checks
            motionZ *= .8D;
        }
        if (motionForward != 0.0 || motionStrafe != 0.0)
        {
            motionX = -MathHelper.sin(rotationYaw * ((float)Math.PI / 180F)) * motionForward - MathHelper.cos(rotationYaw * ((float)Math.PI / 180F)) * motionStrafe;
            motionZ = MathHelper.cos(rotationYaw * ((float)Math.PI / 180F)) * motionForward + MathHelper.sin(rotationYaw * ((float)Math.PI / 180F)) * -motionStrafe;

            //FIXME: diagonal speed increase fix doesn't work
            //double mult = Math.sqrt(motionX * motionX + motionZ * motionZ);
            //motionX /= mult;
            //motionZ /= mult;
        }

        //Reset motion to 0 if too small
        if (Math.abs(motionX) < 0.003D) { motionX = 0; }
        if (Math.abs(motionY) < 0.003D) { motionY = 0; }
        if (Math.abs(motionZ) < 0.003D) { motionZ = 0; }

        //Set new motion
        setMotion(motionX, motionY, motionZ);

        //Inform client of changed motion
        if (!motion.equals(getMotion())) { markVelocityChanged(); }
    }

    private void shootSonicBurst()
    {
        if (ammo <= 0 || world.getGameTime() - lastShotStamp < FIRE_COOLDOWN) { return; }

        setAmmo(ammo - 1);
        lastShotStamp = world.getGameTime();

        setCloaked(false);
        cloakTimerStamp = world.getGameTime();

        world.playSound(null, getPosX(), getPosY(), getPosZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1, 1);
        ((ServerWorld)world).spawnParticle(new ParticleDataYokaiBlast(rotationYaw, rotationPitch), getPosX(), getPosY(), getPosZ(), 1, 0, 0, 0, 0);

        List<HitData> hits = RayTraceHelper.raytraceSonicBurst(this, 14);
        if (hits.isEmpty()) { return; }

        boolean hitFriendly = false;
        boolean hitEnemy = false;
        CameraManager camMgr = R6WorldSavedData.get((ServerWorld)world).getCameraManager();
        for (HitData hit : hits)
        {
            if (hit.getHitType() == HitData.Type.ENTITY && hit.getEntityHit() instanceof ServerPlayerEntity)
            {
                ServerPlayerEntity player = (ServerPlayerEntity)hit.getEntityHit();
                EffectEventHandler.addEffect(player, EnumEffect.YOKAI_BLAST, EFFECT_DURATION);

                //Throw player out of cameras
                if (camMgr.isUsingCamera(player)) { camMgr.leaveCamera(player); }

                //Cancel placement or item use
                ItemStack stack = player.getActiveItemStack();
                if (stack.getItem() instanceof IUsageTimeItem)
                {
                    ((IUsageTimeItem)stack.getItem()).applySonicBurst(world, stack, player.getUniqueID(), INTERACT_COOLDOWN);
                }
                else if (stack.getItem() instanceof IPlacementTime)
                {
                    ((IPlacementTime)stack.getItem()).applySonicBurst(world, stack, INTERACT_COOLDOWN);
                }

                //Cancel block interaction or pickup via PlayerInteractEvent.RightClickBlock
                EffectEventHandler.applySonicBurst(player);

                //Disable finka boost
                EffectEventHandler.removeEffect(player, EnumEffect.FINKA_BOOST);

                //Check team hit for point calculation
                if (player.getTeam() == null || !player.getTeam().isSameTeam(getTeam()))
                {
                    hitEnemy = true;
                }
                else if (player.getTeam() != null && player.getTeam().isSameTeam(getTeam()))
                {
                    hitFriendly = true;
                }
            }
        }

        if (!hits.isEmpty())
        {
            int points = hitFriendly ? -25 : (hitEnemy ? 25 : 0);
            PointManager.awardGadgetUse(EnumGadget.YOKAI_DRONE, getPrimaryUser(), points);
        }
    }

    @Override
    public void empPulse(EntityEMPGrenade emp)
    {
        if (getTeam() == null || !emp.getTeamName().equals(getTeamName()))
        {
            jammed = true;
            jamStart = world.getGameTime();

            dataManager.set(PARAM_JAMMED, true);

            setCloaked(false);
            setOnCeiling(false);
            setMotion(0, -.2D, 0);
        }
    }

    @Override
    public void stopUsing(PlayerEntity player)
    {
        //Only reset status if the primary user stopped using the camera and he could actually use these functions
        if (player.getUniqueID().equals(users.peek()) && canPerformAction(player))
        {
            motionForward = 0;
            motionStrafe = 0;
        }
        super.stopUsing(player);
    }

    /*
     * ICameraEntity
     */

    @Override
    public void handleRotationPacket(PlayerEntity player, double diffX, double diffY)
    {
        if (!player.getUniqueID().equals(users.peek())) { return; } //Only the first user can control the camera

        diffX *= .2D;
        diffY *= .2D;

        float newYaw = rotationYaw + (float)(diffX);
        float newPitch = rotationPitch + (float)(diffY);

        if (onCeiling) { newPitch = MathHelper.clamp(newPitch, 0, 90); }
        else { newPitch = MathHelper.clamp(newPitch, -40, 0); }

        setRotation(newYaw, newPitch); //FIXME: causes render glitch at 180° yaw (probably connected to yaw interpolation)
    }

    @Override
    public void handleLeftClickPacket(PlayerEntity player, boolean down)
    {
        if (!canPerformAction(player)) { return; }
        if (onCeiling && down) { shootSonicBurst(); }
    }

    @Override
    public void handleKeyInputPacket(PlayerEntity player, byte mask)
    {
        if (!canPerformAction(player)) { return; }

        boolean forward = Utils.isBitSet(mask, 0);
        boolean backward = Utils.isBitSet(mask, 1);
        boolean left = Utils.isBitSet(mask, 2);
        boolean right = Utils.isBitSet(mask, 3);
        boolean jump = Utils.isBitSet(mask, 4);
        boolean wasJump = Utils.isBitSet(lastKeyMask, 4);
        lastKeyMask = mask;

        Vector3d motion = getMotion();
        if (canMove)
        {
            motionForward = 0;
            motionStrafe = 0;
            if (forward != backward) { motionForward = forward ? .25D : -.25D; }
            if (left != right) { motionStrafe = left ? -.25D : .25D; }

            if (jump && !wasJump)
            {
                if (world.getGameTime() - lastJumpStamp >= JUMP_COOLDOWN)
                {
                    setMotion(0, .6D, 0);
                    motionForward = 0;
                    motionStrafe = 0;

                    lastJumpStamp = world.getGameTime();
                    canMove = false;
                }
                else
                {
                    player.sendStatusMessage(StatusMessages.JUMP_COOLDOWN, true);
                }
            }
        }
        else if (onCeiling)
        {
            if (jump && !wasJump)
            {
                if (world.getGameTime() - lastJumpStamp >= JUMP_COOLDOWN)
                {
                    setOnCeiling(false);
                    setMotion(0, -.2D, 0);

                    setCloaked(false);

                    lastJumpStamp = world.getGameTime();
                }
                else
                {
                    player.sendStatusMessage(StatusMessages.JUMP_COOLDOWN, true);
                }
            }
        }
        if (!motion.equals(getMotion())) { markVelocityChanged(); }
    }

    @Override
    public void handleLeftClick(boolean down)
    {
        NetworkHandler.sendToServer(new PacketCameraLeftClick(getEntityId(), down));
    }

    @Override
    public void handleKeyInput(byte mask)
    {
        NetworkHandler.sendToServer(new PacketCameraKeyInput(getEntityId(), mask));
    }

    @Override
    public EnumCamera getCameraType() { return EnumCamera.YOKAI_DRONE; }

    @Override
    public ICameraOverlay<EntityYokaiDrone> createOverlayRenderer() { return new OverlayYokaiDrone(); }

    /*
     * IShootable
     */

    @Override
    public void shoot(PlayerEntity shooter, Vector3d hitVec)
    {
        remove();
        PointManager.awardGadgetDestroyed(EnumGadget.YOKAI_DRONE, shooter, getTeamName());
        world.addParticle(ParticleTypes.SMOKE, getPosX(), getPosY(), getPosZ(), 0, 0, 0);
        ((ServerWorld)world).spawnParticle(ParticleTypes.SMOKE, getPosX(), getPosY(), getPosZ(), 1, 0, 0, 0, 0);
    }

    /*
     * Getters
     */

    public int getAmmo() { return ammo; }

    public boolean isCloaked() { return cloaked; }

    public boolean isOnCeiling() { return onCeiling; }

    public float getReloadStatus(float partialTicks)
    {
        if (reloadTimerStamp == 0) { return 0; }
        float diff = (float)(world.getGameTime() - reloadTimerStamp) + partialTicks;
        return diff / (float)TIME_TO_RELOAD;
    }

    public boolean isJammed() { return jammed; }

    /*
     * Private setters
     */

    private void setAmmo(int ammo)
    {
        this.ammo = ammo;
        dataManager.set(PARAM_AMMO, ammo);
    }

    private void setOnCeiling(boolean onCeiling)
    {
        this.onCeiling = onCeiling;
        dataManager.set(PARAM_CEILING, onCeiling);

        if (onCeiling && rotationPitch < 0) { setRotation(rotationYaw, 0); }
        else if (!onCeiling && rotationPitch > 0) { setRotation(rotationYaw, 0); }
    }

    private void setCloaked(boolean cloaked)
    {
        this.cloaked = cloaked;
        dataManager.set(PARAM_CLOAK, cloaked);
    }

    /*
     * IPickupTime
     */

    @Override
    public InteractState pickUp(PlayerEntity player)
    {
        if (player.getUniqueID().equals(owner))
        {
            if (pickupStart == 0) { pickupStart = world.getGameTime(); }
            lastInteract = world.getGameTime();

            if (!Config.INSTANCE.usageTime || getCurrentTime() >= getPickupTime())
            {
                if (!world.isRemote)
                {
                    remove();
                    player.addItemStackToInventory(new ItemStack(R6Content.itemYokaiDrone));
                }
                return InteractState.SUCCESS;
            }
            return InteractState.IN_PROGRESS;
        }
        return InteractState.FAILED;
    }

    @Override
    public int getCurrentTime()
    {
        if (pickupStart == 0) { return 0; }
        return (int)(world.getGameTime() - pickupStart);
    }

    @Override
    public int getPickupTime() { return EnumGadget.YOKAI_DRONE.getPickupTime(); }

    @Override
    public void applySonicBurst(PlayerEntity player, int cooldown)
    {
        //Can be checked against the owner because only the owner can interact with the entity
        if (player.getUniqueID().equals(owner) && pickupStart != 0)
        {
            pickupStart = world.getGameTime() + cooldown;
        }
    }

    @Override
    public UUID getPickupInteractor() { return pickupStart != 0 ? owner : null; }

    @Override
    public TranslationTextComponent getPickupMessage() { return EnumGadget.YOKAI_DRONE.getPickupMessage(); }

    /*
     * Misc entity stuff
     */

    @Override
    protected void registerData()
    {
        super.registerData();
        dataManager.register(PARAM_AMMO, 0);
        dataManager.register(PARAM_CEILING, false);
        dataManager.register(PARAM_CLOAK, false);
        dataManager.register(PARAM_RELOAD, 0L);
        dataManager.register(PARAM_JAMMED, false);
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key)
    {
        super.notifyDataManagerChange(key);
        if (key == PARAM_AMMO) { ammo = dataManager.get(PARAM_AMMO); }
        if (key == PARAM_CEILING)
        {
            onCeiling = dataManager.get(PARAM_CEILING);
            resetEyeHeight();
        }
        if (key == PARAM_CLOAK) { cloaked = dataManager.get(PARAM_CLOAK); }
        if (key == PARAM_RELOAD) { reloadTimerStamp = dataManager.get(PARAM_RELOAD); }
        if (key == PARAM_JAMMED) { jammed = dataManager.get(PARAM_JAMMED); }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        remove();
        return true;
    }

    @Override
    public ActionResultType applyPlayerInteraction(PlayerEntity player, Vector3d vec, Hand hand)
    {
        return pickUp(player).toActionResultType();
    }

    @Override
    public float getEyeHeight(Pose pos) { return .045F; }

    @Override
    protected float getEyeHeight(Pose pose, EntitySize size) { return getEyeHeight(pose); }

    @Override
    public void recalculateSize()
    {
        super.recalculateSize();

        //Cancel out the bullshit of forcing a certain eye height
        resetEyeHeight();
    }

    private void resetEyeHeight()
    {
        try { EYE_HEIGHT.set(this, getEyeHeight(getPose())); }
        catch (IllegalAccessException e) { throw new RuntimeException(e); }
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) { /*NOOP*/ }

    @Override
    protected void writeAdditional(CompoundNBT nbt)
    {
        super.writeAdditional(nbt);
        nbt.putInt("ammo", ammo);
        nbt.putBoolean("onCeiling", onCeiling);
    }

    @Override
    protected void readAdditional(CompoundNBT nbt)
    {
        super.readAdditional(nbt);
        ammo = nbt.getInt("ammo");
        onCeiling = nbt.getBoolean("onCeiling");
    }

    public static void transformEyeHeightField()
    {
        EYE_HEIGHT = ObfuscationReflectionHelper.findField(Entity.class, "field_213326_aJ");
    }
}