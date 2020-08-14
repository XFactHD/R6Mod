package xfacthd.r6mod.common.net.packets.camera;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.r6mod.api.entity.ICameraEntity;
import xfacthd.r6mod.common.net.packets.AbstractPacket;

import java.util.function.Supplier;

public class PacketCameraKeyInput extends AbstractPacket
{
    private int entityId;
    private byte mask;

    public PacketCameraKeyInput(PacketBuffer buffer) { decode(buffer); }

    public PacketCameraKeyInput(int entityId, byte mask)
    {
        this.entityId = entityId;
        this.mask = mask;
    }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeInt(entityId);
        buffer.writeByte(mask);
    }

    @Override
    public void decode(PacketBuffer buffer)
    {
        entityId = buffer.readInt();
        mask = buffer.readByte();
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
                ((ICameraEntity)entity).handleKeyInputPacket(ctx.get().getSender(), mask);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}