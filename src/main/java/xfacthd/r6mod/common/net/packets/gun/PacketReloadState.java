package xfacthd.r6mod.common.net.packets.gun;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.common.capability.CapabilityGun;
import xfacthd.r6mod.common.data.gun_data.ReloadState;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGun;
import xfacthd.r6mod.common.items.gun.ItemGun;
import xfacthd.r6mod.common.net.packets.AbstractPacket;

import java.util.function.Supplier;

public class PacketReloadState extends AbstractPacket
{
    private int slot;
    private EnumGun gun;
    private boolean reloading;
    private ReloadState state;
    private long stateStart;

    public PacketReloadState(PacketBuffer buffer) { decode(buffer); }

    public PacketReloadState(int slot, EnumGun gun, boolean reloading, ReloadState state, long stateStart)
    {
        this.slot = slot;
        this.gun = gun;
        this.reloading = reloading;
        this.state = state;
        this.stateStart = stateStart;
    }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeInt(slot);
        buffer.writeInt(gun.ordinal());
        buffer.writeBoolean(reloading);
        buffer.writeInt(state.ordinal());
        buffer.writeLong(stateStart);
    }

    @Override
    public void decode(PacketBuffer buffer)
    {
        slot = buffer.readInt();
        gun = EnumGun.values()[buffer.readInt()];
        reloading = buffer.readBoolean();
        state = ReloadState.values()[buffer.readInt()];
        stateStart = buffer.readLong();
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            ItemStack stack = R6Mod.getSidedHelper().getPlayer().inventory.getStackInSlot(slot);
            if (stack.getItem() instanceof ItemGun && ((ItemGun)stack.getItem()).getGun() == gun)
            {
                CapabilityGun.getFrom(stack).handleReloadStatePacket(reloading, state, stateStart);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}