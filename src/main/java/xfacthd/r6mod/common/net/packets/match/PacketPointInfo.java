package xfacthd.r6mod.common.net.packets.match;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.r6mod.client.event.ClientPointHandler;
import xfacthd.r6mod.common.data.PointContext;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.net.packets.AbstractPacket;
import xfacthd.r6mod.common.util.data.ExtraPointsEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PacketPointInfo extends AbstractPacket
{
    private PointContext context;
    private int points;
    private List<ExtraPointsEntry> extraPoints;
    private EnumGadget gadget;

    public PacketPointInfo(PacketBuffer buffer) { decode(buffer); }

    public PacketPointInfo(PointContext context, EnumGadget gadget, int points, List<ExtraPointsEntry> extraPoints)
    {
        this.context = context;
        this.gadget = gadget;
        this.points = points;
        this.extraPoints = extraPoints;
    }

    public PacketPointInfo(PointContext context, int points, List<ExtraPointsEntry> extraPoints)
    {
        this.context = context;
        this.points = points;
        this.extraPoints = extraPoints;
    }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeInt(context.ordinal());

        buffer.writeInt(points);

        buffer.writeInt(extraPoints != null ? extraPoints.size() : 0);
        if (extraPoints != null)
        {
            for (ExtraPointsEntry entry : extraPoints)
            {
                buffer.writeInt(entry.getContext().ordinal());
                buffer.writeInt(entry.getPoints());
            }
        }

        buffer.writeBoolean(gadget != null);
        if (gadget != null)
        {
            buffer.writeInt(gadget.ordinal());
        }
    }

    @Override
    public void decode(PacketBuffer buffer)
    {
        context = PointContext.values()[buffer.readInt()];

        points = buffer.readInt();

        int extraCount = buffer.readInt();
        if (extraCount > 0)
        {
            extraPoints = new ArrayList<>();
            for (int i = 0; i < extraCount; i++)
            {
                PointContext extraContext = PointContext.values()[buffer.readInt()];
                extraPoints.add(new ExtraPointsEntry(extraContext, buffer.readInt()));
            }
        }

        boolean hasGadget = buffer.readBoolean();
        if (hasGadget)
        {
           gadget = EnumGadget.values()[buffer.readInt()];
        }
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> ClientPointHandler.onPacketPointInfo(context, points, extraPoints, gadget));
        ctx.get().setPacketHandled(true);
    }
}