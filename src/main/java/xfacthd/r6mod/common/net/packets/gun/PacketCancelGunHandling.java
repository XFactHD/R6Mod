package xfacthd.r6mod.common.net.packets.gun;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.r6mod.common.capability.CapabilityGun;
import xfacthd.r6mod.common.items.gun.ItemGun;
import xfacthd.r6mod.common.net.packets.AbstractPacket;

import java.util.function.Supplier;

public class PacketCancelGunHandling extends AbstractPacket
{
    public PacketCancelGunHandling() { }

    public PacketCancelGunHandling(PacketBuffer buffer) { decode(buffer); }

    @Override
    public void encode(PacketBuffer buffer) { }

    @Override
    public void decode(PacketBuffer buffer) { }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            PlayerEntity player = ctx.get().getSender();
            if (player == null) { return; }

            //INFO: this works as long as the packet is not used for handling slot switching
            ItemStack stack = player.getHeldItemMainhand();

            if (stack.getItem() instanceof ItemGun)
            {
                CapabilityGun.getFrom(stack).handleCancelPacket(player);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}