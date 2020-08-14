package xfacthd.r6mod.common.net.packets.capability;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.common.capability.CapabilityEffect;
import xfacthd.r6mod.common.net.packets.AbstractPacket;

import java.util.function.Supplier;

public class PacketUpdateStimState extends AbstractPacket
{
    private float boostPool;

    public PacketUpdateStimState(PacketBuffer buffer) { decode(buffer); }

    public PacketUpdateStimState(float boostPool) { this.boostPool = boostPool; }

    @Override
    public void encode(PacketBuffer buffer) { buffer.writeFloat(boostPool); }

    @Override
    public void decode(PacketBuffer buffer) { boostPool = buffer.readFloat(); }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            //noinspection ConstantConditions
            R6Mod.getSidedHelper().getPlayer().getCapability(CapabilityEffect.EFFECT_CAPABILITY).ifPresent(
                    (cap) -> cap.setStimPoolClient(boostPool)
            );
        });
        ctx.get().setPacketHandled(true);
    }
}