package xfacthd.r6mod.common.tileentities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;

import java.lang.ref.WeakReference;
import java.util.UUID;

public abstract class TileEntityOwnable extends TileEntityBase
{
    private UUID ownerUUID;
    private WeakReference<PlayerEntity> owner;

    public TileEntityOwnable(TileEntityType type) { super(type); }

    public final void setOwner(PlayerEntity owner)
    {
        this.ownerUUID = owner.getUniqueID();
        this.owner = new WeakReference<>(owner);
    }

    @SuppressWarnings("ConstantConditions")
    public final PlayerEntity getOwner()
    {
        if (world == null || ownerUUID == null) { return null; }

        if (owner == null || owner.get() == null || !owner.get().getUniqueID().equals(ownerUUID))
        {
            owner = new WeakReference<>(world.getPlayerByUuid(ownerUUID));
        }
        return owner.get();
    }

    @Override
    public void writeNetworkNBT(CompoundNBT nbt)
    {
        if (ownerUUID != null) { nbt.putUniqueId("owner", ownerUUID); }
    }

    @Override
    public void readNetworkNBT(CompoundNBT nbt)
    {
        if (nbt.hasUniqueId("owner"))
        {
            ownerUUID = nbt.getUniqueId("owner");
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        if (ownerUUID != null) { nbt.putUniqueId("owner", ownerUUID); }
        return super.write(nbt);
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        super.read(nbt);
        if (nbt.hasUniqueId("owner"))
        {
            ownerUUID = nbt.getUniqueId("owner");
        }
    }
}