package xfacthd.r6mod.common.net.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public abstract class AbstractPacket
{
    public abstract void encode(PacketBuffer buffer);

    public abstract void decode(PacketBuffer buffer);

    public abstract void handle(Supplier<NetworkEvent.Context> ctx);
}