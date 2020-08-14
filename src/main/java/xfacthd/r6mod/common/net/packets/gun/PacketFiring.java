package xfacthd.r6mod.common.net.packets.gun;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.r6mod.common.capability.CapabilityGun;
import xfacthd.r6mod.common.items.gun.ItemGun;
import xfacthd.r6mod.common.net.packets.AbstractPacket;

import java.util.function.Supplier;

public class PacketFiring extends AbstractPacket
{
    boolean mouseDown = false;

    public PacketFiring(boolean mouseDown) { this.mouseDown = mouseDown; }

    public PacketFiring(PacketBuffer buffer) { decode(buffer); }

    @Override
    public void encode(PacketBuffer buffer) { buffer.writeBoolean(mouseDown); }

    @Override
    public void decode(PacketBuffer buffer) { mouseDown = buffer.readBoolean(); }

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
                CapabilityGun.getFrom(stack).handleFiringPacket(player, mouseDown);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}