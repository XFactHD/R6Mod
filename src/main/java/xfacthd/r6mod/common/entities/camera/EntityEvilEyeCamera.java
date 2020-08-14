package xfacthd.r6mod.common.entities.camera;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.*;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xfacthd.r6mod.api.client.ICameraOverlay;
import xfacthd.r6mod.api.interaction.IShockable;
import xfacthd.r6mod.client.gui.overlay.camera.OverlayEvilEye;
import xfacthd.r6mod.common.data.EnumCamera;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.data.particledata.ParticleDataEvilEyeLaser;
import xfacthd.r6mod.common.data.types.EntityTypes;
import xfacthd.r6mod.common.entities.grenade.EntityEMPGrenade;
import xfacthd.r6mod.common.net.NetworkHandler;
import xfacthd.r6mod.common.net.packets.camera.*;
import xfacthd.r6mod.common.util.HitData;
import xfacthd.r6mod.common.util.RayTraceHelper;
import xfacthd.r6mod.common.util.damage.DamageSourceEvilEye;
import xfacthd.r6mod.common.util.data.PointManager;

import java.util.UUID;

public class EntityEvilEyeCamera extends AbstractEntityCamera
{
    private static final DataParameter<Float> PARAM_DOOR = EntityDataManager.createKey(EntityEvilEyeCamera.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> PARAM_AMMO = EntityDataManager.createKey(EntityEvilEyeCamera.class, DataSerializers.FLOAT);
    private static final DataParameter<Boolean> PARAM_OVERHEAT = EntityDataManager.createKey(EntityEvilEyeCamera.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> PARAM_JAMMED = EntityDataManager.createKey(EntityEvilEyeCamera.class, DataSerializers.BOOLEAN);
    private static final RedstoneParticleData WHITE_DUST = new RedstoneParticleData(1F, 1F, 1F, 1F);
    private static final float LASER_DAMAGE = 0;

    private Direction facing;

    private float initYaw = 0; //The initial yaw when the camera is placed
    private float minYaw;
    private float maxYaw;
    private float minPitch;
    private float maxPitch;
    private boolean limitsInitialized = false;

    private float doorState = 1F;
    private float doorDir = 0.1F;
    private long lastTurretAction = 0;
    private boolean firing = false;
    private float ammo = 1F;
    private boolean overheated = false;
    private boolean jammed = false;
    private long jamStart = 0;

    public EntityEvilEyeCamera(World world) { super(EntityTypes.entityTypeEvilEye, world); }

    public EntityEvilEyeCamera(World world, BlockPos pos, Direction facing, UUID owner, String team)
    {
        super(EntityTypes.entityTypeEvilEye, world, owner, team);

        this.facing = facing;

        double xOff = .5;
        double yOff = .5;
        double zOff = .5;
        switch (facing)
        {
            case UP:
            {
                yOff = .5625;
                break;
            }
            case NORTH:
            {
                zOff = .4375;
                break;
            }
            case SOUTH:
            {
                zOff = .5625;
                break;
            }
            case WEST:
            {
                xOff = .4375;
                break;
            }
            case EAST:
            {
                xOff = .5625;
                break;
            }
        }
        forceSetPosition(pos.getX() + xOff, pos.getY() + yOff, pos.getZ() + zOff);

        initRotationLimits();
        setRotation(initYaw, 0);
    }

    @Override
    public void tick()
    {
        super.tick();

        if (!world.isRemote)
        {
            tickDoor();
            tickTurret();

            if (world.getGameTime() - jamStart > EntityEMPGrenade.EFFECT_TIME)
            {
                jammed = false;
                dataManager.set(PARAM_JAMMED, false);
            }
        }
    }

    @Override
    protected void firstTick()
    {
        super.firstTick();

        //Limits only needed on the server and the client also never gets the "facing"
        if (!world.isRemote && !limitsInitialized) { initRotationLimits(); }
    }

    private void tickDoor()
    {
        //When jammed => DOOR STUCK!
        if (jammed) { return; }

        float newState = MathHelper.clamp(doorState + (overheated ? 0.1F : doorDir), 0, 1);
        if (newState != doorState)
        {
            dataManager.set(PARAM_DOOR, newState);
            doorState = newState;
        }
    }

    private void tickTurret()
    {
        if (jammed) { return; }

        if (doorState <= 0F && firing && !overheated)
        {
            if (world.getGameTime() - lastTurretAction >= 5)
            {
                ammo -= 0.05;
                dataManager.set(PARAM_AMMO, ammo);
                lastTurretAction = world.getGameTime();

                HitData hit = RayTraceHelper.raytraceEvilEyeLaser(this, 64);
                if (hit != null)
                {
                    if (hit.getHitType() == HitData.Type.ENTITY)
                    {
                        Entity entity = hit.getEntityHit();
                        if (entity instanceof IShockable)
                        {
                            ((IShockable)entity).shock(getPrimaryUser(), hit.getHitVec());
                        }
                        else
                        {
                            entity.attackEntityFrom(new DamageSourceEvilEye(getPrimaryUser()), LASER_DAMAGE);
                        }
                    }
                    else
                    {
                        BlockState state = world.getBlockState(hit.getPos());
                        if (state.getBlock() instanceof IShockable)
                        {
                            ((IShockable)state.getBlock()).shock(getPrimaryUser(), hit.getHitVec());
                        }
                    }
                }

                if (ammo <= 0F)
                {
                    overheated = true;
                    dataManager.set(PARAM_OVERHEAT, true);
                }
            }
        }
        else if (ammo < 1F)
        {
            if (world.getGameTime() - lastTurretAction >= 5)
            {
                ammo += 0.05F;
                dataManager.set(PARAM_AMMO, ammo);
                lastTurretAction = world.getGameTime();

                if (ammo >= 1.0F)
                {
                    overheated = false;
                    dataManager.set(PARAM_OVERHEAT, false);
                }
            }
        }
    }

    private void initRotationLimits()
    {
        if (facing == null) { return; }

        if (facing == Direction.UP)
        {
            minYaw = 0;
            maxYaw = 360;
            minPitch = -90;
            maxPitch = 0;
        }
        else
        {
            initYaw = facing.getHorizontalAngle();

            minYaw = initYaw - 90;
            maxYaw = initYaw + 90;
            minPitch = -90;
            maxPitch = 90;
        }

        limitsInitialized = true;
    }

    public void shock(PlayerEntity shooter)
    {
        jammed = true;
        jamStart = world.getGameTime();
        dataManager.set(PARAM_JAMMED, true);

        doorState = .5F;
        dataManager.set(PARAM_DOOR, doorState);

        PointManager.awardGadgetDisabled(EnumGadget.EVIL_EYE, shooter, getTeamName());
    }

    @Override
    public void empPulse(EntityEMPGrenade emp)
    {
        if (getTeam() != null && !emp.getTeamName().equals(getTeamName()))
        {
            jammed = true;
            jamStart = world.getGameTime();
            dataManager.set(PARAM_JAMMED, true);

            doorState = .5F;
            dataManager.set(PARAM_DOOR, doorState);
        }
    }

    /*
     * ICameraEntity
     */

    @Override
    public void handleRotationPacket(PlayerEntity player, double diffX, double diffY)
    {
        if (!canPerformAction(player) || jammed) { return; } //Only the owner can control evil eyes

        diffX *= .15D;
        diffY *= .15D;

        float newYaw = rotationYaw + (float)(diffX);
        float newPitch = rotationPitch + (float)(diffY);

        if (facing == Direction.UP && newYaw < 0F)
        {
            newYaw = 360F + newYaw;
        }
        //Stupid workaround for "rotationYaw = yaw % 360F" in setRotation() in case the upper limit is 360F, fixes camera turning around backwards
        else if (facing == Direction.EAST)
        {
            if (newYaw > 0F && newYaw < 180F && diffX > 0F) { newYaw = 0F; }
            else if (newYaw < 0F) { newYaw = 360F + newYaw; }
            else { newYaw = MathHelper.clamp(newYaw, minYaw, maxYaw); }
        }
        else
        {
            newYaw = MathHelper.clamp(newYaw, minYaw, maxYaw);
        }
        newPitch = MathHelper.clamp(newPitch, minPitch, maxPitch);

        setRotation(newYaw, newPitch);

        //Fixes interpolation issue when the camera crosses the 359°->0° point //FIXME: doesn't fix the same issue at 180°
        if (newYaw >= 360F && rotationYaw < newYaw)
        {
            prevRotationYaw -= 360F;
        }
        else if (newYaw < 0F && rotationYaw > newYaw)
        {
            prevRotationYaw += 360F;
        }
    }

    @Override
    public void handleLeftClickPacket(PlayerEntity player, boolean down)
    {
        if (!canPerformAction(player)) { return; }
        firing = down;
    }

    @Override
    public void handleRightClickPacket(PlayerEntity player, boolean down)
    {
        if (!canPerformAction(player)) { return; }
        doorDir = down ? -0.1F : 0.1F;
    }

    @Override
    public void handleLeftClick(boolean down)
    {
        NetworkHandler.sendToServer(new PacketCameraLeftClick(getEntityId(), down));
    }

    @Override
    public void handleRightClick(boolean down)
    {
        NetworkHandler.sendToServer(new PacketCameraRightClick(getEntityId(), down));
    }

    @Override
    public void stopUsing(PlayerEntity player)
    {
        //Only reset status if the primary user stopped using the camera and he could actually use these functions
        if (player.getUniqueID().equals(users.peek()) && canPerformAction(player))
        {
            firing = false;
            doorDir = 0.1F;
        }
        super.stopUsing(player);
    }

    @Override
    public EnumCamera getCameraType() { return EnumCamera.EVIL_EYE; }

    @Override
    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public ICameraOverlay createOverlayRenderer() { return new OverlayEvilEye(); }

    /*
     * Getters
     */

    public float getDoorState() { return doorState; }

    public float getAmmo() { return ammo; }

    public boolean isOverheated() { return overheated; }

    public boolean isJammed() { return jammed; }

    /*
     * Misc entity stuff
     */

    @Override
    protected void registerData()
    {
        super.registerData();
        dataManager.register(PARAM_DOOR, 1F);
        dataManager.register(PARAM_AMMO, 1F);
        dataManager.register(PARAM_OVERHEAT, false);
        dataManager.register(PARAM_JAMMED, false);
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key)
    {
        super.notifyDataManagerChange(key);
        if (key == PARAM_DOOR) { doorState = dataManager.get(PARAM_DOOR); }
        else if (key == PARAM_AMMO)
        {
            float newAmmo = dataManager.get(PARAM_AMMO);
            if (world.isRemote() && newAmmo < ammo) //Abuse ammo state change for rendering shot
            {
                world.addParticle(WHITE_DUST, getPosX(), getPosY(), getPosZ(), 0, 0, 0);
                world.addParticle(new ParticleDataEvilEyeLaser(rotationYaw, rotationPitch), getPosX(), getPosY(), getPosZ(), 0, 0, 0);
            }
            ammo = newAmmo;
        }
        else if (key == PARAM_OVERHEAT) { overheated = dataManager.get(PARAM_OVERHEAT); }
        else if (key == PARAM_JAMMED) { jammed = dataManager.get(PARAM_JAMMED); }
    }

    @Override
    protected void writeAdditional(CompoundNBT nbt)
    {
        super.writeAdditional(nbt);

        nbt.putInt("facing", facing.getIndex());
        nbt.putFloat("door", doorState);
        nbt.putFloat("ammo", ammo);
    }

    @Override
    protected void readAdditional(CompoundNBT nbt)
    {
        super.readAdditional(nbt);

        facing = Direction.byIndex(nbt.getInt("facing"));
        doorState = nbt.getFloat("door");
        ammo = nbt.getFloat("ammo");
    }
}