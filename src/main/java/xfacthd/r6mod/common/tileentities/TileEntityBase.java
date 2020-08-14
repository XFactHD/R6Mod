package xfacthd.r6mod.common.tileentities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.util.Constants;

public abstract class TileEntityBase extends TileEntity
{
    public TileEntityBase(TileEntityType type) { super(type); }

    @SuppressWarnings("ConstantConditions")
    public final void markFullUpdate()
    {
        markDirty();
        world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
    }

    public abstract void writeNetworkNBT(CompoundNBT nbt);

    public abstract void readNetworkNBT(CompoundNBT nbt);

    @Override
    public CompoundNBT getUpdateTag()
    {
        CompoundNBT nbt = new CompoundNBT();
        writeNetworkNBT(nbt);
        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundNBT nbt) { readNetworkNBT(nbt); }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        CompoundNBT nbt = new CompoundNBT();
        writeNetworkNBT(nbt);
        return new SUpdateTileEntityPacket(getPos(), -1, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
    {
        CompoundNBT nbt = pkt.getNbtCompound();
        readNetworkNBT(nbt);
    }
}