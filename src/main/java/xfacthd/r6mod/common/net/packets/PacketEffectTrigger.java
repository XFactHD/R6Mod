package xfacthd.r6mod.common.net.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.r6mod.client.event.ClientEffectEventHandler;
import xfacthd.r6mod.common.data.effects.EnumEffect;

import java.util.function.Supplier;

public class PacketEffectTrigger extends AbstractPacket
{
    private EnumEffect effect;
    private int time;

    public PacketEffectTrigger(PacketBuffer buffer) { decode(buffer); }

    public PacketEffectTrigger(EnumEffect effect, int time)
    {
        this.effect = effect;
        this.time = time;
    }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeInt(effect.ordinal());
        buffer.writeInt(time);
    }

    @Override
    public void decode(PacketBuffer buffer)
    {
        effect = EnumEffect.values()[buffer.readInt()];
        time = buffer.readInt();
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> ClientEffectEventHandler.addEffect(effect, time));
        ctx.get().setPacketHandled(true);
    }
}