package xfacthd.r6mod.common.net.packets.gun;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.r6mod.common.capability.CapabilityGun;
import xfacthd.r6mod.common.items.gun.ItemGun;
import xfacthd.r6mod.common.net.packets.AbstractPacket;

import java.util.function.Supplier;

public class PacketAiming extends AbstractPacket
{
    boolean mouseDown = false;
    boolean holdToAim = false;

    public PacketAiming(boolean mouseDown, boolean holdToAim)
    {
        this.mouseDown = mouseDown;
        this.holdToAim = holdToAim;
    }

    public PacketAiming(PacketBuffer buffer) { decode(buffer); }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeBoolean(mouseDown);
        buffer.writeBoolean(holdToAim);
    }

    @Override
    public void decode(PacketBuffer buffer)
    {
        mouseDown = buffer.readBoolean();
        holdToAim = buffer.readBoolean();
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            PlayerEntity player = ctx.get().getSender();
            if (player == null) { return; }

            ItemStack stack = player.getHeldItemMainhand();

            if (stack.getItem() instanceof ItemGun)
            {
                CapabilityGun.getFrom(stack).handleAimingPacket(player, mouseDown, holdToAim);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}