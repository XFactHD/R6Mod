package xfacthd.r6mod.common.tileentities.building;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import xfacthd.r6mod.api.interaction.IShootable;
import xfacthd.r6mod.common.container.ContainerCamera;
import xfacthd.r6mod.common.data.PropertyHolder;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.data.types.TileEntityTypes;
import xfacthd.r6mod.common.entities.camera.EntityCamera;
import xfacthd.r6mod.common.tileentities.TileEntityBase;
import xfacthd.r6mod.common.util.data.PointManager;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.UUID;

public class TileEntityCamera extends TileEntityBase implements ITickableTileEntity, IShootable, INamedContainerProvider
{
    private WeakReference<EntityCamera> camera;
    private boolean wasUsed = false;
    private boolean destroyed = false;

    public TileEntityCamera() { super(TileEntityTypes.tileTypeCamera); }

    public EntityCamera getCamera()
    {
        if (camera == null) { return null; }
        return camera.get();
    }

    @Override
    public void tick()
    {
        assert(world != null);

        if (camera == null)
        {
            AxisAlignedBB bb = new AxisAlignedBB(pos.getX() + 0.4D, pos.getY() + 0.8D, pos.getZ() + 0.4D, pos.getX() + 0.6D, pos.getY() + 1D, pos.getZ() + 0.6D);

            List<EntityCamera> entities = world.getEntitiesWithinAABB(EntityCamera.class, bb);
            if (!entities.isEmpty())
            {
                camera = new WeakReference<>(entities.get(0));
            }
        }

        if (!world.isRemote && getCamera() != null)
        {
            boolean isUsed = getCamera().isInUse();
            if (isUsed != wasUsed)
            {
                wasUsed = isUsed;
            }
        }
    }

    public boolean isActive() { return wasUsed; }

    public boolean isDestroyed() { return destroyed; }

    public boolean isFriendly(PlayerEntity player)
    {
        if (getCamera() == null) { return false; }
        if (!hasTeam() || player.getTeam() == null) { return false; }
        return player.getTeam().getName().equals(getCamera().getTeamName());
    }

    public boolean hasTeam()
    {
        if (getCamera() == null) { return false; }
        return !getCamera().getTeamName().equals("null");
    }

    public String getTeam()
    {
        if (getCamera() == null) { return "null"; }
        return getCamera().getTeamName();
    }

    public void addCameraWithTeam(UUID owner, String team)
    {
        EntityCamera camera = new EntityCamera(world, pos, owner, team);
        this.camera = new WeakReference<>(camera);
        //noinspection ConstantConditions
        world.addEntity(camera);
    }

    public void removeCamera() { if (getCamera() != null) { getCamera().remove(); } }

    /*
     * IShootable
     */

    @Override
    public void shoot(PlayerEntity shooter, Vec3d hitVec)
    {
        if (isDestroyed() || world == null) { return; }

        PointManager.awardGadgetDestroyed(EnumGadget.CAMERA, shooter, getTeam());

        //destroyed = true;
        getCamera().setDestroyed();
        world.setBlockState(pos, getBlockState().with(PropertyHolder.DESTROYED, true));

        ((ServerWorld)world).spawnParticle(ParticleTypes.SMOKE, pos.getX() + .5D, pos.getY() + .75D, pos.getZ() + .5D, 1, 0, 0, 0, 0);
    }

    /*
     * INamedContainerProvider
     */

    @Override
    public Container createMenu(int id, PlayerInventory inv, PlayerEntity player)
    {
        //noinspection ConstantConditions
        return new ContainerCamera(id, world, pos, player);
    }

    @Override
    public ITextComponent getDisplayName() { return new TranslationTextComponent("gui.r6mod.camera"); }

    /*
     * NBT stuff
     */

    @Override
    public void writeNetworkNBT(CompoundNBT nbt)
    {
        nbt.putBoolean("used", wasUsed);
        nbt.putBoolean("destroyed", destroyed);
    }

    @Override
    public void readNetworkNBT(CompoundNBT nbt)
    {
        wasUsed = nbt.getBoolean("used");
        destroyed = nbt.getBoolean("destroyed");
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        nbt.putBoolean("destroyed", destroyed);
        return super.write(nbt);
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        super.read(nbt);
        destroyed = nbt.getBoolean("destroyed");
    }
}