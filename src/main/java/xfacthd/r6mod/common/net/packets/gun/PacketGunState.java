package xfacthd.r6mod.common.net.packets.gun;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.common.capability.CapabilityGun;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGun;
import xfacthd.r6mod.common.items.gun.ItemGun;
import xfacthd.r6mod.common.net.packets.AbstractPacket;

import java.util.function.Supplier;

public class PacketGunState extends AbstractPacket
{
    private int slot;
    private EnumGun gun;
    private boolean aiming;
    private boolean chambered;
    private boolean charged;
    private long chargeStart;
    private boolean loaded;
    private int bulletsLoaded;

    public PacketGunState(PacketBuffer buffer) { decode(buffer); }

    public PacketGunState(int slot, EnumGun gun, boolean aiming, boolean chambered, boolean charged, long chargeStart, boolean loaded, int bulletsLoaded)
    {
        this.slot = slot;
        this.gun = gun;
        this.aiming = aiming;
        this.chambered = chambered;
        this.charged = charged;
        this.chargeStart = chargeStart;
        this.loaded = loaded;
        this.bulletsLoaded = bulletsLoaded;
    }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeInt(slot);
        buffer.writeInt(gun.ordinal());
        buffer.writeBoolean(aiming);
        buffer.writeBoolean(chambered);
        buffer.writeBoolean(charged);
        buffer.writeLong(chargeStart);
        buffer.writeBoolean(loaded);
        buffer.writeInt(bulletsLoaded);
    }

    @Override
    public void decode(PacketBuffer buffer)
    {
        slot = buffer.readInt();
        gun = EnumGun.values()[buffer.readInt()];
        aiming = buffer.readBoolean();
        chambered = buffer.readBoolean();
        charged = buffer.readBoolean();
        chargeStart = buffer.readLong();
        loaded = buffer.readBoolean();
        bulletsLoaded = buffer.readInt();
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            ItemStack stack = R6Mod.getSidedHelper().getPlayer().inventory.getStackInSlot(slot);
            if (stack.getItem() instanceof ItemGun && ((ItemGun)stack.getItem()).getGun() == gun)
            {
                CapabilityGun.getFrom(stack).handleGunStatePacket(aiming, chambered, charged, chargeStart, loaded, bulletsLoaded);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}