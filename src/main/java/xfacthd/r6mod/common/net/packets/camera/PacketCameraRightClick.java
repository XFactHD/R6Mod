package xfacthd.r6mod.common.net.packets.camera;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.r6mod.api.entity.ICameraEntity;
import xfacthd.r6mod.common.net.packets.AbstractPacket;

import java.util.function.Supplier;

public class PacketCameraRightClick extends AbstractPacket
{
    private int entityId;
    private boolean down;

    public PacketCameraRightClick(int entityId, boolean down)
    {
        this.entityId = entityId;
        this.down = down;
    }

    public PacketCameraRightClick(PacketBuffer buffer) { decode(buffer); }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeInt(entityId);
        buffer.writeBoolean(down);
    }

    @Override
    public void decode(PacketBuffer buffer)
    {
        entityId = buffer.readInt();
        down = buffer.readBoolean();
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
                ((ICameraEntity<?>)entity).handleRightClickPacket(ctx.get().getSender(), down);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}