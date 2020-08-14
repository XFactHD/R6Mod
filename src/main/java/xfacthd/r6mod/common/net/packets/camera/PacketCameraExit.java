package xfacthd.r6mod.common.net.packets.camera;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.r6mod.common.net.packets.AbstractPacket;
import xfacthd.r6mod.common.util.data.R6WorldSavedData;

import java.util.function.Supplier;

public class PacketCameraExit extends AbstractPacket
{
    public PacketCameraExit() { }

    @SuppressWarnings("unused")
    public PacketCameraExit(PacketBuffer buffer) { decode(buffer); }

    @Override
    public void encode(PacketBuffer buffer) { }

    @Override
    public void decode(PacketBuffer buffer) { }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player == null) { return; }

            ServerWorld world = (ServerWorld)player.world;
            R6WorldSavedData.get(world).getCameraManager().leaveCamera(player);
        });
        ctx.get().setPacketHandled(true);
    }
}