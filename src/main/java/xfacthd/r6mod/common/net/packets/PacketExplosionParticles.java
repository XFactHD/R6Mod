package xfacthd.r6mod.common.net.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.api.IExplosionParticleSpawner;

import java.util.function.Supplier;

public class PacketExplosionParticles extends AbstractPacket
{
    private BlockPos pos;

    public PacketExplosionParticles(BlockPos pos) { this.pos = pos; }

    public PacketExplosionParticles(PacketBuffer buffer) { decode(buffer); }

    @Override
    public void encode(PacketBuffer buffer) { buffer.writeBlockPos(pos); }

    @Override
    public void decode(PacketBuffer buffer) { pos = buffer.readBlockPos(); }

    @Override
    @SuppressWarnings("deprecation")
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            World world = R6Mod.getSidedHelper().getWorld();
            if (world != null && world.isBlockLoaded(pos))
            {
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof IExplosionParticleSpawner)
                {
                    ((IExplosionParticleSpawner)te).spawnParticles();
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}