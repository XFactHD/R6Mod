package xfacthd.r6mod.common.net.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.r6mod.client.event.ClientEffectEventHandler;
import xfacthd.r6mod.common.data.effects.EnumEffect;

import java.util.function.Supplier;

public class PacketEffectClear extends AbstractPacket
{
    private EnumEffect effect;

    public PacketEffectClear(PacketBuffer buffer) { decode(buffer); }

    public PacketEffectClear(EnumEffect effect) { this.effect = effect; }

    @Override
    public void encode(PacketBuffer buffer) { buffer.writeInt(effect.ordinal()); }

    @Override
    public void decode(PacketBuffer buffer) { effect = EnumEffect.values()[buffer.readInt()]; }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> ClientEffectEventHandler.removeEffect(effect));
        ctx.get().setPacketHandled(true);
    }
}