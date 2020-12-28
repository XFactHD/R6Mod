package xfacthd.r6mod.common.data.types;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.common.data.gun_data.GunSoundType;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGun;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class R6SoundEvents
{
    public static final Map<EnumGun, Map<GunSoundType, SoundEvent>> GUN_SOUNDS = new HashMap<>();
    public static final Map<EnumGadget, Map<String, SoundEvent>> GADGET_SOUNDS = new HashMap<>();
    public static final Map<String, SoundEvent> MISC_SOUNDS = new HashMap<>();

    @SubscribeEvent
    public static void registerSounds(final RegistryEvent.Register<SoundEvent> event)
    {
        final IForgeRegistry<SoundEvent> registry = event.getRegistry();

        registerMiscSound(registry, "mag_filler_mag_in");
        registerMiscSound(registry, "mag_filler_mag_out");

        for (EnumGun gun : EnumGun.values())
        {
            Map<GunSoundType, SoundEvent> sounds = new HashMap<>();
            for (GunSoundType type : gun.getValidSounds())
            {
                String name = gun.toItemName() + "_" + type.getName();
                ResourceLocation regName = new ResourceLocation(R6Mod.MODID, name);
                SoundEvent sound = new SoundEvent(regName);
                sound.setRegistryName(regName);
                sounds.put(type, sound);
                registry.register(sound);
            }
            GUN_SOUNDS.put(gun, sounds);
        }

        for (EnumGadget gadget : EnumGadget.values())
        {
            Map<String, SoundEvent> sounds = new HashMap<>();
            for (String sound : gadget.getSounds())
            {
                String name = gadget.getObjectName() + "_" + sound;
                ResourceLocation regName = new ResourceLocation(R6Mod.MODID, name);
                SoundEvent soundEvent = new SoundEvent(regName);
                soundEvent.setRegistryName(regName);
                sounds.put(sound, soundEvent);
                registry.register(soundEvent);
            }
            GADGET_SOUNDS.put(gadget, sounds);
        }
    }

    public static SoundEvent getGunSound(EnumGun gun, GunSoundType type) { return GUN_SOUNDS.get(gun).get(type); }

    public static SoundEvent getGadgetSound(EnumGadget gadget, String sound) { return GADGET_SOUNDS.get(gadget).get(sound); }

    public static SoundEvent getMiscSound(String sound) { return MISC_SOUNDS.get(sound); }

    private static void registerMiscSound(IForgeRegistry<SoundEvent> registry, String sound)
    {
        ResourceLocation loc = new ResourceLocation(R6Mod.MODID, sound);
        SoundEvent event = new SoundEvent(loc);
        event.setRegistryName(loc);
        registry.register(event);

        MISC_SOUNDS.put(sound, event);
    }
}