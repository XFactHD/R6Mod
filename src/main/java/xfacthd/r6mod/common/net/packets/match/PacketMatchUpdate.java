package xfacthd.r6mod.common.net.packets.match;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.r6mod.common.net.packets.AbstractPacket;

import java.util.function.Supplier;

public class PacketMatchUpdate extends AbstractPacket
{
    private String team1;
    private String team2;
    private Type type;

    public PacketMatchUpdate(String team1, String team2, Type type)
    {
        this.team1 = team1;
        this.team2 = team2;
        this.type = type;
    }

    public PacketMatchUpdate(PacketBuffer buffer) { decode(buffer); }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeString(team1);
        buffer.writeString(team2);
        buffer.writeInt(type.ordinal());
    }

    @Override
    public void decode(PacketBuffer buffer)
    {
        //Need to call this variant of readString() because arg-less variant is client-only
        team1 = buffer.readString(32767);
        team2 = buffer.readString(32767);
        type = Type.values()[buffer.readInt()];
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            //TODO: implement
        });
        ctx.get().setPacketHandled(true);
    }

    public enum Type
    {
        START,
        FINISH
    }
}