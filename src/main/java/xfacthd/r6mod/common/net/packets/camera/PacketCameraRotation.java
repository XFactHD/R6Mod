package xfacthd.r6mod.common.net.packets.camera;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.r6mod.api.entity.ICameraEntity;
import xfacthd.r6mod.common.net.packets.AbstractPacket;

import java.util.function.Supplier;

public class PacketCameraRotation extends AbstractPacket
{
    private int entityId;
    private double diffX;
    private double diffY;

    public PacketCameraRotation(PacketBuffer buffer) { decode(buffer); }

    public PacketCameraRotation(int entityId, double diffX, double diffY)
    {
        this.entityId = entityId;
        this.diffX = diffX;
        this.diffY = diffY;
    }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeInt(entityId);
        buffer.writeDouble(diffX);
        buffer.writeDouble(diffY);
    }

    @Override
    public void decode(PacketBuffer buffer)
    {
        entityId = buffer.readInt();
        diffX = buffer.readDouble();
        diffY = buffer.readDouble();
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            ServerWorld world = ctx.get().getSender().getServerWorld();
            Entity entity = world.getEntityByID(entityId);
            if (entity instanceof ICameraEntity)
            {
                ((ICameraEntity<?>)entity).handleRotationPacket(ctx.get().getSender(), diffX, diffY);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}