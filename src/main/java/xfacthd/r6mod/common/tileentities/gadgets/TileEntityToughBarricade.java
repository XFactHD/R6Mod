package xfacthd.r6mod.common.tileentities.gadgets;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import xfacthd.r6mod.common.R6Content;
import xfacthd.r6mod.common.data.types.TileEntityTypes;
import xfacthd.r6mod.common.tileentities.TileEntityBase;

import java.lang.ref.WeakReference;
import java.util.UUID;

public class TileEntityToughBarricade extends TileEntityBase
{
    private UUID ownerUUID;
    private WeakReference<PlayerEntity> owner;

    public TileEntityToughBarricade() { super(TileEntityTypes.tileTypeToughBarricade); }

    public void returnToOwner()
    {
        if (world == null || world.isRemote() || ownerUUID == null) { return; }

        PlayerEntity player = world.getPlayerByUuid(ownerUUID);
        if (player != null)
        {
            player.addItemStackToInventory(new ItemStack(R6Content.blockToughBarricade));
        }
    }

    public void setOwner(PlayerEntity owner) { this.ownerUUID = owner.getUniqueID(); }

    public PlayerEntity getOwner()
    {
        if (owner == null)
        {
            //noinspection ConstantConditions
            owner = new WeakReference<>(world.getPlayerByUuid(ownerUUID));
        }
        return owner.get();
    }

    @Override
    public void writeNetworkNBT(CompoundNBT nbt) { if (ownerUUID != null) { nbt.putUniqueId("owner", ownerUUID); } }

    @Override
    public void readNetworkNBT(CompoundNBT nbt) { if (nbt.hasUniqueId("owner")) { ownerUUID = nbt.getUniqueId("owner"); } }

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
        if (nbt.hasUniqueId("owner")) { ownerUUID = nbt.getUniqueId("owner"); }
    }
}