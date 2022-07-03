package xfacthd.r6mod.common.capability;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.api.capability.*;

@Mod.EventBusSubscriber(modid = R6Mod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CapabilityHandler
{
    public static void registerCapabilities()
    {
        CapabilityManager.INSTANCE.register(ICapabilityEffect.class, new CapabilityEffect.Storage(), CapabilityEffect.Empty::new);
        CapabilityManager.INSTANCE.register(ICapabilityGun.class, new CapabilityGun.Storage(), CapabilityGun.Empty::new);
        CapabilityManager.INSTANCE.register(ICapabilityDBNO.class, new CapabilityDBNO.Storage(), CapabilityDBNO.Empty::new);
        CapabilityManager.INSTANCE.register(ICapabilityGarraHook.class, new CapabilityGarraHook.Storage(), CapabilityGarraHook.Empty::new);
    }

    @SubscribeEvent
    public static void onAttachEntityCapabilities(final AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof PlayerEntity)
        {
            event.addCapability(CapabilityEffect.KEY, new CapabilityEffect.Provider((PlayerEntity)event.getObject()));
            event.addCapability(CapabilityDBNO.KEY, new CapabilityDBNO.Provider((PlayerEntity)event.getObject()));
        }
    }
}