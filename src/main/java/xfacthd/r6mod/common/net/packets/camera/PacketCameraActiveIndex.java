package xfacthd.r6mod.common.net.packets.camera;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.r6mod.client.R6Client;
import xfacthd.r6mod.common.net.packets.AbstractPacket;

import java.util.function.Supplier;

public class PacketCameraActiveIndex extends AbstractPacket
{
    int activeId;

    public PacketCameraActiveIndex(int activeId) { this.activeId = activeId; }

    public PacketCameraActiveIndex(PacketBuffer buffer) { decode(buffer); }

    @Override
    public void encode(PacketBuffer buffer) { buffer.writeInt(activeId); }

    @Override
    public void decode(PacketBuffer buffer) { activeId = buffer.readInt(); }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> R6Client.getCameraManager().handleCameraIndexPacket(activeId));
        ctx.get().setPacketHandled(true);
    }
}