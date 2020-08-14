package xfacthd.r6mod.common.net.packets.gun;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.r6mod.api.interaction.IReloadable;
import xfacthd.r6mod.common.net.packets.AbstractPacket;

import java.util.function.Supplier;

public class PacketReload extends AbstractPacket
{
    public PacketReload() { }

    public PacketReload(PacketBuffer buffer) { decode(buffer); }

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

            ItemStack stack = player.getHeldItemMainhand();
            if (stack.getItem() instanceof IReloadable)
            {
                ((IReloadable)stack.getItem()).reload(stack, player);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}