package xfacthd.r6mod.common.net.packets.debug;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.r6mod.client.event.DebugRenderHandler;
import xfacthd.r6mod.common.net.packets.AbstractPacket;

import java.util.function.Supplier;

public class PacketRayTraceResult extends AbstractPacket
{
    private Vector3d start;
    private Vector3d end;

    public PacketRayTraceResult(Vector3d start, Vector3d end)
    {
        this.start = start;
        this.end = end;
    }

    public PacketRayTraceResult(AxisAlignedBB bb)
    {
        this.start = new Vector3d(bb.minX, bb.minY, bb.minZ);
        this.end = new Vector3d(bb.maxX, bb.maxY, bb.maxZ);
    }

    public PacketRayTraceResult(PacketBuffer buffer) { decode(buffer); }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeDouble(start.x);
        buffer.writeDouble(start.y);
        buffer.writeDouble(start.z);

        buffer.writeDouble(end.x);
        buffer.writeDouble(end.y);
        buffer.writeDouble(end.z);
    }

    @Override
    public void decode(PacketBuffer buffer)
    {
        start = new Vector3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        end = new Vector3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> DebugRenderHandler.addShotTracer(start, end));
        ctx.get().setPacketHandled(true);
    }
}