package xfacthd.r6mod.common.net.packets.dbno;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.r6mod.common.capability.CapabilityDBNO;
import xfacthd.r6mod.common.net.packets.AbstractPacket;

import java.util.UUID;
import java.util.function.Supplier;

public class PacketHoldWound extends AbstractPacket
{
    private UUID playerId;
    private boolean holding;

    public PacketHoldWound(PlayerEntity player, boolean holding)
    {
        this.playerId = player.getUniqueID();
        this.holding = holding;
    }

    public PacketHoldWound(PacketBuffer buffer) { decode(buffer); }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeUniqueId(playerId);
        buffer.writeBoolean(holding);
    }

    @Override
    public void decode(PacketBuffer buffer)
    {
        playerId = buffer.readUniqueId();
        holding = buffer.readBoolean();
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            PlayerEntity player = ctx.get().getSender();
            if (player != null)
            {
                CapabilityDBNO.getFrom(player).setHoldingWound(holding);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}