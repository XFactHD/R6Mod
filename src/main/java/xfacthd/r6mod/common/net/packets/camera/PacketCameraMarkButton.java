package xfacthd.r6mod.common.net.packets.camera;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.r6mod.api.entity.ICameraEntity;
import xfacthd.r6mod.common.net.packets.AbstractPacket;

import java.util.function.Supplier;

public class PacketCameraMarkButton extends AbstractPacket
{
    private int entityId;
    private boolean down;
    private int fovAngle;

    public PacketCameraMarkButton(int entityId, boolean down, int fovAngle)
    {
        this.entityId = entityId;
        this.down = down;
        this.fovAngle = fovAngle;
    }

    public PacketCameraMarkButton(PacketBuffer buffer) { decode(buffer); }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeInt(entityId);
        buffer.writeBoolean(down);
        buffer.writeInt(fovAngle);
    }

    @Override
    public void decode(PacketBuffer buffer)
    {
        entityId = buffer.readInt();
        down = buffer.readBoolean();
        fovAngle = buffer.readInt();
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
                ((ICameraEntity)entity).handleMarkButtonPacket(ctx.get().getSender(), down, fovAngle);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}