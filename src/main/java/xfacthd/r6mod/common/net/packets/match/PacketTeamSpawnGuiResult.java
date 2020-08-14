package xfacthd.r6mod.common.net.packets.match;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.r6mod.common.container.ContainerTeamSpawn;
import xfacthd.r6mod.common.net.packets.AbstractPacket;

import java.util.function.Supplier;

public class PacketTeamSpawnGuiResult extends AbstractPacket
{
    private String team;

    public PacketTeamSpawnGuiResult(PacketBuffer buffer) { decode(buffer); }

    public PacketTeamSpawnGuiResult(String team) { this.team = team; }

    @Override
    public void encode(PacketBuffer buffer) { buffer.writeString(team); }

    @Override //Need to call this variant of readString() because arg-less variant is client-only
    public void decode(PacketBuffer buffer) { team = buffer.readString(32767); }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player != null)
            {
                Container container = player.openContainer;
                if (container instanceof ContainerTeamSpawn)
                {
                    ((ContainerTeamSpawn)container).onResult(ctx.get().getSender(), team);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}