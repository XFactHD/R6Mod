package xfacthd.r6mod.common.entities.camera;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.*;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xfacthd.r6mod.api.client.ICameraOverlay;
import xfacthd.r6mod.client.gui.overlay.camera.OverlayBulletproofCamera;
import xfacthd.r6mod.common.data.EnumCamera;
import xfacthd.r6mod.common.data.types.EntityTypes;
import xfacthd.r6mod.common.entities.grenade.EntityEMPGrenade;

import java.util.UUID;

public class EntityBulletproofCamera extends AbstractEntityCamera
{
    private static final DataParameter<Boolean> PARAM_JAMMED = EntityDataManager.createKey(EntityBulletproofCamera.class, DataSerializers.BOOLEAN);

    private boolean jammed = false;
    private long jamStart = 0;

    public EntityBulletproofCamera(World world) { super(EntityTypes.entityTypeBulletproofCamera, world); }

    public EntityBulletproofCamera(World world, BlockPos pos, Direction facing, UUID owner, String team)
    {
        super(EntityTypes.entityTypeBulletproofCamera, world, owner, team);

        if (facing == Direction.UP)
        {
            setRotation(0, -90);
            forceSetPosition(pos.getX() + .5D, pos.getY(), pos.getZ() + .5D);
        }
        else
        {
            setRotation(facing.getHorizontalAngle(), 0);
            switch (facing)
            {
                case NORTH:
                {
                    forceSetPosition(pos.getX() + .5D, pos.getY() + .5D, pos.getZ() + .95D);
                    break;
                }
                case SOUTH:
                {
                    forceSetPosition(pos.getX() + .5D, pos.getY() + .5D, pos.getZ() + .05D);
                    break;
                }
                case WEST:
                {
                    forceSetPosition(pos.getX() + .95D, pos.getY() + .5D, pos.getZ() + .5D);
                    break;
                }
                case EAST:
                {
                    forceSetPosition(pos.getX() + .05D, pos.getY() + .5D, pos.getZ() + .5D);
                    break;
                }
            }
        }
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

    /*
     * ICameraEntity
     */

    @Override
    public void handleMouseMovement(double diffX, double diffY) { }

    @Override
    public void handleRotationPacket(PlayerEntity player, double diffX, double diffY) { }

    @Override
    public EnumCamera getCameraType() { return EnumCamera.BULLETPROOF; }

    @Override
    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public ICameraOverlay createOverlayRenderer() { return new OverlayBulletproofCamera(); }

    /*
     * Misc entity stuff
     */

    @Override
    protected void registerData()
    {
        super.registerData();
        dataManager.register(PARAM_JAMMED, false);
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key)
    {
        super.notifyDataManagerChange(key);
        if (key == PARAM_JAMMED) { jammed = dataManager.get(PARAM_JAMMED); }
    }
}