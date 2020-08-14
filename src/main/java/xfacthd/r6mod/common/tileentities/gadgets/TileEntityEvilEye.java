package xfacthd.r6mod.common.tileentities.gadgets;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import xfacthd.r6mod.common.data.types.TileEntityTypes;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.entities.camera.EntityEvilEyeCamera;
import xfacthd.r6mod.common.tileentities.TileEntityGadget;
import xfacthd.r6mod.common.util.data.PointManager;

import java.lang.ref.WeakReference;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class TileEntityEvilEye extends TileEntityGadget
{
    private Direction facing;
    private WeakReference<EntityEvilEyeCamera> camera;

    public TileEntityEvilEye() { super(TileEntityTypes.tileTypeEvilEye, EnumGadget.EVIL_EYE); }

    public TileEntityEvilEye(Direction facing)
    {
        this();
        this.facing = facing;
    }

    @Override
    public void tick()
    {
        super.tick();

        if (camera == null)
        {
            AxisAlignedBB bb = new AxisAlignedBB(pos.getX() + 0.2D, pos.getY() + 0.2D, pos.getZ() + 0.2D, pos.getX() + 0.8D, pos.getY() + 0.8D, pos.getZ() + 0.8D);

            List<EntityEvilEyeCamera> entities = world.getEntitiesWithinAABB(EntityEvilEyeCamera.class, bb);
            if (!entities.isEmpty())
            {
                camera = new WeakReference<>(entities.get(0));
            }
        }
    }

    public Direction getFacing() { return facing; }

    public float getRotationYaw(float partialTicks)
    {
        if (getCamera() == null) { return 0; }

        float yaw = getCamera().getYaw(partialTicks);
        if (facing != Direction.UP) { yaw -= facing.getHorizontalAngle(); }
        if (facing == Direction.SOUTH || facing == Direction.WEST || facing == Direction.UP) { yaw *= -1; }
        return yaw;
    }

    public float getRotationPitch(float partialTicks)
    {
        if (getCamera() == null) { return 0; }

        float pitch = getCamera().getPitch(partialTicks);
        if (facing == Direction.UP) { pitch += 90F; }
        if (facing == Direction.SOUTH || facing == Direction.WEST) { pitch *= -1; }
        return pitch;
    }

    public float getDoorState()
    {
        if (getCamera() == null) { return 1F; } //Fully closed

        return getCamera().getDoorState();
    }

    public boolean isFriendly(PlayerEntity player) { return getCamera() != null && getCamera().isFriendly(player); }

    public EntityEvilEyeCamera getCamera() { return camera != null ? camera.get() : null; }

    public void removeCamera() { if (camera != null) { camera.get().remove(); } }

    /*
     * IShootable
     */

    @Override
    public void shoot(PlayerEntity shooter, Vec3d hitVec)
    {
        if (getDoorState() == 1F) { return; }
        if (!internalsHit(shooter, hitVec)) { return; }

        PointManager.awardGadgetDestroyed(EnumGadget.EVIL_EYE, shooter, getTeam());
        world.destroyBlock(pos, false);
    }

    @Override
    public void shock(PlayerEntity shooter, Vec3d hitVec)
    {
        EntityEvilEyeCamera camera = getCamera();
        if (camera == null) { return; }

        if (getDoorState() != 1F) //Door open
        {
            if (internalsHit(shooter, hitVec))
            {
                PointManager.awardGadgetDestroyed(EnumGadget.EVIL_EYE, shooter, getTeam());
                world.destroyBlock(pos, false);
                return;
            }
        }

        camera.shock(shooter);
    }

    private boolean internalsHit(PlayerEntity shooter, Vec3d hitVec)
    {
        //TODO: check that hit vec has actually hit camera's internals
        return false;
    }

    /*
     * NBT stuff
     */

    @Override
    public void writeNetworkNBT(CompoundNBT nbt)
    {
        nbt.putInt("facing", facing.getIndex());
        super.writeNetworkNBT(nbt);
    }

    @Override
    public void readNetworkNBT(CompoundNBT nbt)
    {
        super.readNetworkNBT(nbt);
        facing = Direction.byIndex(nbt.getInt("facing"));
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        nbt.putInt("facing", facing.getIndex());
        return super.write(nbt);
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        super.read(nbt);
        facing = Direction.byIndex(nbt.getInt("facing"));
    }
}