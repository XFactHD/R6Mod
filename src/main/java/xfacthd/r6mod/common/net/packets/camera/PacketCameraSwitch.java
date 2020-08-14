package xfacthd.r6mod.common.net.packets.camera;

import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.r6mod.common.net.packets.AbstractPacket;
import xfacthd.r6mod.common.util.data.CameraManager;
import xfacthd.r6mod.common.util.data.R6WorldSavedData;

import java.util.function.Supplier;

public class PacketCameraSwitch extends AbstractPacket
{
    private boolean forward;

    public PacketCameraSwitch(boolean forward) { this.forward = forward; }

    public PacketCameraSwitch(PacketBuffer buffer) { decode(buffer); }

    @Override
    public void encode(PacketBuffer buffer) { buffer.writeBoolean(forward); }

    @Override
    public void decode(PacketBuffer buffer) { forward = buffer.readBoolean(); }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            ServerWorld world = (ServerWorld) ctx.get().getSender().world;
            CameraManager cameraManager = R6WorldSavedData.get(world).getCameraManager();
            cameraManager.changeCamera(ctx.get().getSender(), forward);
        });
        ctx.get().setPacketHandled(true);
    }
}