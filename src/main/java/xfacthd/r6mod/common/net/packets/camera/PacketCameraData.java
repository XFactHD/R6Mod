package xfacthd.r6mod.common.net.packets.camera;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.r6mod.client.R6Client;
import xfacthd.r6mod.common.net.packets.AbstractPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PacketCameraData extends AbstractPacket
{
    private List<Integer> cameras;

    public PacketCameraData(List<Integer> cameras) { this.cameras = cameras; }

    public PacketCameraData(PacketBuffer buffer) { decode(buffer); }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeInt(cameras.size());
        for (int id : cameras) { buffer.writeInt(id); }
    }

    @Override
    public void decode(PacketBuffer buffer)
    {
        cameras = new ArrayList<>();

        int size = buffer.readInt();
        for (int i = 0; i < size; i++) { cameras.add(buffer.readInt()); }
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> R6Client.getCameraManager().handleCameraDataPacket(cameras));
        ctx.get().setPacketHandled(true);
    }
}