package xfacthd.r6mod.common.entities.grenade;

import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import xfacthd.r6mod.api.interaction.IMagNetCatchable;
import xfacthd.r6mod.common.net.NetworkHandler;
import xfacthd.r6mod.common.net.packets.debug.PacketGrenadeBounce;
import xfacthd.r6mod.common.util.Config;

import java.util.UUID;

@OnlyIn(value = Dist.CLIENT, _interface = IRendersAsItem.class)
public abstract class AbstractEntityGrenade extends Entity implements IMagNetCatchable, IRendersAsItem
{
    private static final float GRAVITY = 0.03F;

    protected UUID thrower;
    protected String teamName = "null";
    private long timerStart = 0;
    private boolean timerRunning = false;

    private boolean firstTick = true;
    private boolean caught = false;
    private ItemStack renderStack = null;

    public AbstractEntityGrenade(EntityType<? extends AbstractEntityGrenade> type, World world) { super(type, world); }

    //Main constructor
    public AbstractEntityGrenade(EntityType<? extends AbstractEntityGrenade> type, World world, PlayerEntity thrower, String team)
    {
        super(type, world);

        this.thrower = thrower.getUniqueID();
        this.teamName = team;

        forceSetPosition(thrower.getPosX(), thrower.getPosY() + thrower.getEyeHeight(), thrower.getPosZ());
        setHeading(thrower);
    }

    //Constructor for "cookable" grenades or grenades that need to start the timer immediately
    public AbstractEntityGrenade(EntityType<? extends AbstractEntityGrenade> type, World world, PlayerEntity thrower, String team, long timerStart)
    {
        this(type, world, thrower, team);

        this.timerRunning = true;
        this.timerStart = timerStart;
    }

    @Override
    public void tick()
    {
        super.tick();

        if (firstTick) { firstTick(); }

        if (!world.isRemote() && isAlive())
        {
            boolean wasCollided = collided;

            tickMovement();
            tickTimer();

            if (!wasCollided && collided) { onImpact(); }
        }
    }

    protected void firstTick()
    {
        firstTick = false;

        recenterBoundingBox();
        if (!world.isRemote()) { sendGrenadePathDebug(); }
    }

    protected void tickMovement()
    {
        Vec3d motion = getMotion();

        //Move to new position
        move(MoverType.SELF, getMotion());
        Vec3d newMotion = getMotion();

        //Check for collisions
        double motionX = motion.x;
        double motionY = motion.y;
        double motionZ = motion.z;

        if (motionY > 0D && collidedVertically && shouldDeflectVert())
        {
            motionY *= -1D;
        }
        if (collidedHorizontally)
        {
            //Collision in x direction
            if (motionX != 0D && motionX != newMotion.x && shouldDeflectHor())
            {
                motionX *= -1D;
            }
            if (motionZ != 0D && motionZ != newMotion.z && shouldDeflectHor())
            {
                motionZ *= -1D;
            }
        }

        //Calculate new motion
        if (!caught)
        {
            float drag = hasDrag() ? (isInWater() ? 0.8F : 0.99F) : 1F;
            motionX *= drag;
            motionY *= drag;
            motionY -= GRAVITY;
            motionZ *= drag;
        }

        if (onGround && hasDrag())
        {
            motionX *= .8D;
            motionZ *= .8D;
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

    private void tickTimer()
    {
        if (timerRunning && world.getGameTime() - timerStart > getFuseLength())
        {
            timerRunning = false;
            onTimerExpired();
        }
    }

    /*
     * Protected logic stuff
     */

    protected final void startTimer() { startTimer(0); }

    protected final void startTimer(int offset)
    {
        if (!timerRunning)
        {
            timerStart = world.getGameTime() - offset;
            timerRunning = true;
        }
    }

    protected abstract long getFuseLength();

    protected abstract void onTimerExpired();

    protected abstract void onImpact();

    protected boolean hasDrag() { return true; }

    protected boolean shouldDeflectHor() { return true; }

    protected boolean shouldDeflectVert() { return true; }

    protected abstract ItemStack getRenderStack();

    /*
     * Private helpers
     */

    private void setHeading(PlayerEntity thrower)
    {
        float x = -MathHelper.sin(thrower.rotationYaw * ((float)Math.PI / 180F)) * MathHelper.cos(thrower.rotationPitch * ((float)Math.PI / 180F));
        float y = -MathHelper.sin(thrower.rotationPitch * ((float)Math.PI / 180F));
        float z = MathHelper.cos(thrower.rotationYaw * ((float)Math.PI / 180F)) * MathHelper.cos(thrower.rotationPitch * ((float)Math.PI / 180F));
        Vec3d motion = new Vec3d(x, y, z).normalize();
        setMotion(motion);

        this.rotationYaw   = this.prevRotationYaw =   thrower.rotationYaw;
        this.rotationPitch = this.prevRotationPitch = thrower.rotationPitch;
    }

    private void sendGrenadePathDebug()
    {
        if (Config.INSTANCE.debugGrenadePath)
        {
            PlayerEntity thrower = getThrowerEntity();
            if (thrower != null)
            {
                NetworkHandler.sendToPlayer(new PacketGrenadeBounce(getUniqueID(), getPositionVec()), (ServerPlayerEntity)thrower);
            }
        }
    }

    /*
     * Getters
     */

    public UUID getThrower() { return thrower; }

    public PlayerEntity getThrowerEntity() { return world.getPlayerByUuid(thrower); }

    public String getTeamName() { return teamName != null ? teamName : "null"; }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ItemStack getItem()
    {
        if (renderStack == null) { renderStack = getRenderStack(); }
        return renderStack;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isInRangeToRenderDist(double distance)
    {
        double len = this.getBoundingBox().getAverageEdgeLength() * 4.0D;
        if (Double.isNaN(len)) { len = 4.0D; }

        len = len * 64.0D;
        return distance < len * len;
    }

    /*
     * IMagNetCatchable
     */

    @Override
    public void catchObject(Vec3d magPos)
    {
        caught = true;

        //TODO: check if this works or if the calculation is bullshit
        setMotion(magPos.subtract(getPositionVec()).normalize());
    }

    /*
     * Misc entity stuff
     */

    @Override
    protected void registerData() { }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) { super.notifyDataManagerChange(key); }

    @Override
    protected float getEyeHeight(Pose poseIn, EntitySize sizeIn) { return getHeight() / 2F; }

    @Override
    protected boolean canTriggerWalking() { return false; }

    @Override
    protected void readAdditional(CompoundNBT nbt)
    {
        thrower = nbt.getUniqueId("owner");
        teamName = nbt.getString("team");
    }

    @Override
    protected void writeAdditional(CompoundNBT nbt)
    {
        nbt.putUniqueId("owner", thrower);
        nbt.putString("team", teamName);
    }

    @Override
    public IPacket<?> createSpawnPacket() { return NetworkHooks.getEntitySpawningPacket(this); }
}