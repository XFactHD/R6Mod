package xfacthd.r6mod.common.net.packets.match;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.r6mod.client.event.ClientPointHandler;
import xfacthd.r6mod.common.data.DeathReason;
import xfacthd.r6mod.common.net.packets.AbstractPacket;
import xfacthd.r6mod.common.util.damage.DamageSourceGadget;
import xfacthd.r6mod.common.util.damage.DamageSourceGun;

import java.util.function.Supplier;

public class PacketKillfeedEntry extends AbstractPacket
{
    private ITextComponent killerName;
    private ITextComponent victimName;
    private boolean killerOnRecTeam;
    private boolean victimOnRecTeam;
    private DeathReason reason;
    private int enumIdx = -1;
    private boolean headshot;

    public PacketKillfeedEntry(PacketBuffer buffer) { decode(buffer); }

    public PacketKillfeedEntry(PlayerEntity receiver, PlayerEntity killer, PlayerEntity victim, DamageSource source)
    {
        killerName = killer.getDisplayName();
        victimName = victim.getDisplayName();
        killerOnRecTeam = receiver.getTeam() != null && receiver.getTeam().isSameTeam(killer.getTeam());
        victimOnRecTeam = receiver.getTeam() != null && receiver.getTeam().isSameTeam(victim.getTeam());

        //TODO: add knive when implemented
        if (source instanceof DamageSourceGun)
        {
            reason = DeathReason.GUN;
            enumIdx = ((DamageSourceGun)source).getGun().ordinal();
            headshot = ((DamageSourceGun)source).isHeadshot();
        }
        else if (source instanceof DamageSourceGadget)
        {
            reason = DeathReason.GADGET;
            enumIdx = ((DamageSourceGadget)source).getGadget().ordinal();
        }
        else if (source == DamageSource.FALL)
        {
            reason = DeathReason.FALL;
        }
        else if (source instanceof IndirectEntityDamageSource && source.damageType.equals("arrow"))
        {
            reason = DeathReason.ARROW;
        }
        else
        {
            reason = DeathReason.UNKNOWN;
        }
    }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeTextComponent(killerName);
        buffer.writeTextComponent(victimName);
        buffer.writeBoolean(killerOnRecTeam);
        buffer.writeBoolean(victimOnRecTeam);
        buffer.writeInt(reason.ordinal());
        buffer.writeInt(enumIdx);
        if (reason == DeathReason.GUN) { buffer.writeBoolean(headshot); }
    }

    @Override
    public void decode(PacketBuffer buffer)
    {
        killerName = buffer.readTextComponent();
        victimName = buffer.readTextComponent();
        killerOnRecTeam = buffer.readBoolean();
        victimOnRecTeam = buffer.readBoolean();
        reason = DeathReason.values()[buffer.readInt()];
        enumIdx = buffer.readInt();
        if (reason == DeathReason.GUN) { headshot = buffer.readBoolean(); }
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            String killer = killerName.getString();
            String victim = victimName.getString();
            ClientPointHandler.onPacketKillfeed(killer, killerOnRecTeam, victim, victimOnRecTeam, reason, enumIdx, headshot);
        });
        ctx.get().setPacketHandled(true);
    }
}