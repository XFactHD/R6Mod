package xfacthd.r6mod.common.net.packets.dbno;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.common.capability.CapabilityDBNO;
import xfacthd.r6mod.common.net.packets.AbstractPacket;

import java.util.function.Supplier;

public class PacketDBNOState extends AbstractPacket
{
    private boolean dbno;
    private boolean trapped;
    private boolean holding;
    private boolean dead;
    private boolean reviving;

    public PacketDBNOState(boolean dbno, boolean trapped, boolean holding, boolean dead, boolean reviving)
    {
        this.dbno = dbno;
        this.trapped = trapped;
        this.holding = holding;
        this.dead = dead;
        this.reviving = reviving;
    }

    public PacketDBNOState(PacketBuffer buffer) { decode(buffer); }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeBoolean(dbno);
        buffer.writeBoolean(trapped);
        buffer.writeBoolean(holding);
        buffer.writeBoolean(dead);
        buffer.writeBoolean(reviving);
    }

    @Override
    public void decode(PacketBuffer buffer)
    {
        dbno = buffer.readBoolean();
        trapped = buffer.readBoolean();
        holding = buffer.readBoolean();
        dead = buffer.readBoolean();
        reviving = buffer.readBoolean();
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            PlayerEntity player = R6Mod.getSidedHelper().getPlayer();
            CapabilityDBNO.getFrom(player).handleUpdatePacket(dbno, trapped, holding, dead, reviving);
        });
        ctx.get().setPacketHandled(true);
    }
}