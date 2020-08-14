package xfacthd.r6mod.common.net.packets.dbno;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.client.event.ClientDBNOEventHandler;
import xfacthd.r6mod.common.capability.CapabilityDBNO;
import xfacthd.r6mod.common.net.packets.AbstractPacket;

import java.util.UUID;
import java.util.function.Supplier;

public class PacketInformHelper extends AbstractPacket
{
    private UUID playerId;
    private boolean dbno;
    private boolean reviving;

    public PacketInformHelper(PlayerEntity player, boolean dbno, boolean reviving)
    {
        this.playerId = player.getUniqueID();
        this.dbno = dbno;
        this.reviving = reviving;
    }

    public PacketInformHelper(PacketBuffer buffer) { decode(buffer); }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeUniqueId(playerId);
        buffer.writeBoolean(dbno);
        buffer.writeBoolean(reviving);
    }

    @Override
    public void decode(PacketBuffer buffer)
    {
        playerId = buffer.readUniqueId();
        dbno = buffer.readBoolean();
        reviving = buffer.readBoolean();
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            PlayerEntity helper = R6Mod.getSidedHelper().getPlayer();
            PlayerEntity player = helper.world.getPlayerByUuid(playerId);
            if (player != null)
            {
                CapabilityDBNO.getFrom(player).handleUpdatePacket(dbno, false, false, false, reviving);
                ClientDBNOEventHandler.setHelpingPlayer(reviving);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}