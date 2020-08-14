package xfacthd.r6mod.common.tileentities.gadgets;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import xfacthd.r6mod.common.data.PropertyHolder;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.data.types.TileEntityTypes;
import xfacthd.r6mod.common.entities.camera.EntityBulletproofCamera;
import xfacthd.r6mod.common.tileentities.TileEntityGadget;
import xfacthd.r6mod.common.util.data.PointManager;

import java.lang.ref.WeakReference;
import java.util.List;

public class TileEntityBulletproofCamera extends TileEntityGadget implements ITickableTileEntity
{
    private WeakReference<EntityBulletproofCamera> camera;

    public TileEntityBulletproofCamera() { super(TileEntityTypes.tileTypeBulletproofCamera, EnumGadget.BULLETPROOF_CAMERA); }

    public EntityBulletproofCamera getCamera()
    {
        if (camera == null) { return null; }
        return camera.get();
    }

    @Override
    public void tick()
    {
        assert(world != null);

        super.tick();

        if (camera == null)
        {
            AxisAlignedBB bb;
            switch (getBlockState().get(PropertyHolder.FACING_NOT_DOWN))
            {
                case UP:
                {
                    bb = new AxisAlignedBB(pos.getX() + 0.4D, pos.getY(), pos.getZ() + 0.4D, pos.getX() + 0.6D, pos.getY() + 0.2D, pos.getZ() + 0.6D);
                    break;
                }
                case NORTH:
                {
                    bb = new AxisAlignedBB(pos.getX() + 0.4D, pos.getY() + 0.4D, pos.getZ() + .9D, pos.getX() + 0.6D, pos.getY() + 0.6D, pos.getZ() + 1D);
                    break;
                }
                case SOUTH:
                {
                    bb = new AxisAlignedBB(pos.getX() + 0.4D, pos.getY() + 0.4D, pos.getZ(), pos.getX() + 0.6D, pos.getY() + 0.6D, pos.getZ() + 0.2D);
                    break;
                }
                case WEST:
                {
                    bb = new AxisAlignedBB(pos.getX() + .9D, pos.getY() + 0.4D, pos.getZ() + .4D, pos.getX() + 0.6D, pos.getY() + 0.6D, pos.getZ() + .6D);
                    break;
                }
                case EAST:
                {
                    bb = new AxisAlignedBB(pos.getX(), pos.getY() + 0.4D, pos.getZ() + .4D, pos.getX() + 0.2D, pos.getY() + 0.6D, pos.getZ() + .6D);
                    break;
                }
                default: return;
            }

            List<EntityBulletproofCamera> entities = world.getEntitiesWithinAABB(EntityBulletproofCamera.class, bb);
            if (!entities.isEmpty())
            {
                camera = new WeakReference<>(entities.get(0));
            }
        }
    }

    public Direction getFacing() { return getBlockState().get(PropertyHolder.FACING_NOT_DOWN); }

    public boolean isActive() { return getCamera() != null && getCamera().isInUse(); }

    public boolean isFriendly(PlayerEntity player) { return getCamera() != null && getCamera().isFriendly(player); }

    public void removeCamera()
    {
        if (getCamera() != null)
        {
            getCamera().remove();
        }
    }

    /*
     * IShootable
     */

    @Override
    public void shoot(PlayerEntity shooter, Vector3d hitVec)
    {
        if (world == null) { return; }

        //TODO: check that hit vec has actually hit camera's internals

        PointManager.awardGadgetDestroyed(EnumGadget.BULLETPROOF_CAMERA, shooter, getTeam());

        world.destroyBlock(pos, false);
    }
}