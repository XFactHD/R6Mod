package xfacthd.r6mod.common.entities.camera;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xfacthd.r6mod.api.client.ICameraOverlay;
import xfacthd.r6mod.client.gui.overlay.camera.OverlayCamera;
import xfacthd.r6mod.common.data.EnumCamera;
import xfacthd.r6mod.common.data.types.EntityTypes;
import xfacthd.r6mod.common.entities.grenade.EntityEMPGrenade;

import java.util.UUID;

public class EntityCamera extends AbstractEntityCamera
{
    private static final DataParameter<Boolean> PARAM_DESTROYED = EntityDataManager.createKey(EntityCamera.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> PARAM_JAMMED = EntityDataManager.createKey(EntityCamera.class, DataSerializers.BOOLEAN);

    private boolean destroyed = false;
    private boolean jammed = false;
    private long jamStart = 0;

    public EntityCamera(World world) { super(EntityTypes.entityTypeCamera, world); }

    public EntityCamera(World world, BlockPos pos, UUID owner, String team)
    {
        super(EntityTypes.entityTypeCamera, world, owner, team);

        forceSetPosition(pos.getX() + .5D, pos.getY() + .85D, pos.getZ() + .5D);
    }

    @Override
    public void tick()
    {
        super.tick();

        if (!world.isRemote() && jammed)
        {
            if (world.getGameTime() - jamStart > EntityEMPGrenade.EFFECT_TIME)
            {
                jammed = false;
                dataManager.set(PARAM_JAMMED, false);
            }
        }
    }

    public boolean isJammed() { return jammed; }

    @Override
    public void empPulse(EntityEMPGrenade emp)
    {
        if (getTeam() != null && !emp.getTeamName().equals(getTeamName()))
        {
            jammed = true;
            jamStart = world.getGameTime();

            dataManager.set(PARAM_JAMMED, true);
        }
    }

    public boolean isDestroyed() { return destroyed; }

    public void setDestroyed()
    {
        this.destroyed = true;
        dataManager.set(PARAM_DESTROYED, true);
    }

    /*
     * ICameraEntity
     */

    @Override
    public void handleRotationPacket(PlayerEntity player, double diffX, double diffY)
    {
        if (!player.getUniqueID().equals(users.peek()) || jammed) { return; } //Only the first user can control the camera

        diffX *= .15D;
        diffY *= .15D;

        float newYaw = rotationYaw + (float)(diffX);
        float newPitch = rotationPitch + (float)(diffY);

        newPitch = MathHelper.clamp(newPitch, 0, 90);

        setRotation(newYaw, newPitch); //FIXME: causes render glitch at 180° (probably connected to yaw interpolation)
    }

    @Override
    public EnumCamera getCameraType() { return EnumCamera.STATIC; }

    @Override
    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public ICameraOverlay createOverlayRenderer() { return new OverlayCamera(); }

    /*
     * Misc entity stuff
     */

    @Override
    protected void registerData()
    {
        super.registerData();
        dataManager.register(PARAM_DESTROYED, false);
        dataManager.register(PARAM_JAMMED, false);
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key)
    {
        super.notifyDataManagerChange(key);
        if (key == PARAM_DESTROYED) { destroyed = dataManager.get(PARAM_DESTROYED); }
        if (key == PARAM_JAMMED) { jammed = dataManager.get(PARAM_JAMMED); }
    }

    @Override
    protected void writeAdditional(CompoundNBT nbt)
    {
        super.writeAdditional(nbt);
        nbt.putBoolean("destroyed", destroyed);
    }

    @Override
    protected void readAdditional(CompoundNBT nbt)
    {
        super.readAdditional(nbt);
        destroyed = nbt.getBoolean("destroyed");
    }
}