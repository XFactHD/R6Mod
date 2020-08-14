package xfacthd.r6mod.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.client.gui.overlay.info.KillfeedEntry;
import xfacthd.r6mod.client.gui.overlay.info.PointsEntry;
import xfacthd.r6mod.common.data.DeathReason;
import xfacthd.r6mod.common.data.PointContext;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.util.data.ExtraPointsEntry;

import java.util.*;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = R6Mod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientPointHandler
{
    private static final int ENTRY_LIFETIME = 100;
    private static final int KILLFEED_LIFETIME = 100;

    private static final Map<Long, PointsEntry> entries = new TreeMap<>();
    private static final Map<Long, KillfeedEntry> killfeed = new HashMap<>();

    @SubscribeEvent
    public static void onClientTick(final TickEvent.ClientTickEvent event)
    {
        if (world() == null || event.phase != TickEvent.Phase.START) { return; }

        List<Long> toRemove = new ArrayList<>();
        for (Long addTime : entries.keySet())
        {
            if (world().getGameTime() - addTime > ENTRY_LIFETIME)
            {
                toRemove.add(addTime);
            }
        }
        toRemove.forEach(entries::remove);

        toRemove.clear();
        for (Long addTime : killfeed.keySet())
        {
            if (world().getGameTime() - addTime > KILLFEED_LIFETIME)
            {
                toRemove.add(addTime);
            }
        }
        toRemove.forEach(killfeed::remove);
    }



    public static void onPacketPointInfo(PointContext context, int points, List<ExtraPointsEntry> extraPoints, EnumGadget gadget)
    {
        entries.put(world().getGameTime(), new PointsEntry(context, points, extraPoints, gadget));
    }

    public static void onPacketKillfeed(String killer, boolean killerMyTeam, String victim, boolean victimMyTeam, DeathReason reason, int enumIdx, boolean headshot)
    {
        killfeed.put(world().getGameTime(), new KillfeedEntry(killer, killerMyTeam, victim, victimMyTeam, reason, enumIdx, headshot));
    }

    public static Map<Long, PointsEntry> getPointEntries() { return entries; }

    public static Map<Long, KillfeedEntry> getKillfeed() { return killfeed; }



    private static Minecraft mc() { return Minecraft.getInstance(); }

    private static World world() { return mc().world; }
}