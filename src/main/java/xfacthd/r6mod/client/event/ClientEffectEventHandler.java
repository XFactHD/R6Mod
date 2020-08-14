package xfacthd.r6mod.client.event;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.common.data.effects.EnumEffect;
import xfacthd.r6mod.common.data.effects.AbstractEffect;

import java.util.*;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = R6Mod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientEffectEventHandler
{
    private static final Map<EnumEffect, AbstractEffect> effects = new EnumMap<>(EnumEffect.class);

    @SubscribeEvent
    public static void onClientTick(final TickEvent.ClientTickEvent event)
    {
        if (event.phase != TickEvent.Phase.START || mc().world == null) { return; }

        List<EnumEffect> toRemove = new ArrayList<>();
        for (EnumEffect effect : effects.keySet())
        {
            AbstractEffect instance = effects.get(effect);
            instance.tickClient();
            if (instance.isInvalid()) { toRemove.add(effect); }
        }
        toRemove.forEach(effects::remove);
    }

    public static void addEffect(EnumEffect effect, int time)
    {
        assert mc().world != null;
        AbstractEffect instance = effect.create(time, mc().world.getGameTime());
        effects.put(effect, instance);
    }

    public static void removeEffect(EnumEffect effect)
    {
        if (effects.containsKey(effect))
        {
            effects.remove(effect).invalidateClient();
        }
    }

    public static Collection<AbstractEffect> getEffects() { return effects.values(); }

    private static Minecraft mc() { return Minecraft.getInstance(); }
}