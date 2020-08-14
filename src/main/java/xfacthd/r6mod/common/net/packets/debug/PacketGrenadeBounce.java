package xfacthd.r6mod.common.net.packets.debug;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.r6mod.client.event.DebugRenderHandler;
import xfacthd.r6mod.common.net.packets.AbstractPacket;

import java.util.UUID;
import java.util.function.Supplier;

public class PacketGrenadeBounce extends AbstractPacket
{
    private UUID entity;
    private Vector3d bounce;

    public PacketGrenadeBounce(UUID entity, Vector3d bounce)
    {
        this.entity = entity;
        this.bounce = bounce;
    }

    public PacketGrenadeBounce(PacketBuffer buffer) { decode(buffer); }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeUniqueId(entity);
        buffer.writeDouble(bounce.x);
        buffer.writeDouble(bounce.y);
        buffer.writeDouble(bounce.z);
    }

    @Override
    public void decode(PacketBuffer buffer)
    {
        entity = buffer.readUniqueId();
        bounce = new Vector3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> DebugRenderHandler.addGrenadeBounce(entity, bounce));
        ctx.get().setPacketHandled(true);
    }
}