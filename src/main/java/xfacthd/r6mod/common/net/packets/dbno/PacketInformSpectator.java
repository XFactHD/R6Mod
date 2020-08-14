package xfacthd.r6mod.common.net.packets.dbno;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.common.capability.CapabilityDBNO;
import xfacthd.r6mod.common.net.packets.AbstractPacket;

import java.util.UUID;
import java.util.function.Supplier;

public class PacketInformSpectator extends AbstractPacket
{
    private UUID playerId;
    private boolean dbno;
    private int timeLeft;

    public PacketInformSpectator(PlayerEntity player, boolean dbno, int timeLeft)
    {
        this.playerId = player.getUniqueID();
        this.dbno = dbno;
        this.timeLeft = timeLeft;
    }

    public PacketInformSpectator(PacketBuffer buffer) { decode(buffer); }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeUniqueId(playerId);
        buffer.writeBoolean(dbno);
        buffer.writeInt(timeLeft);
    }

    @Override
    public void decode(PacketBuffer buffer)
    {
        playerId = buffer.readUniqueId();
        dbno = buffer.readBoolean();
        timeLeft = buffer.readInt();
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            PlayerEntity spectator = R6Mod.getSidedHelper().getPlayer();
            PlayerEntity player = spectator.world.getPlayerByUuid(playerId);
            if (player != null)
            {
                CapabilityDBNO.getFrom(player).handleSpectatorPacket(dbno, timeLeft);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}