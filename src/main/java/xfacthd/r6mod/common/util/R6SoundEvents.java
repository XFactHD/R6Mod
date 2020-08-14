package xfacthd.r6mod.common.util;

import com.google.gson.*;
import net.minecraft.data.*;
import net.minecraft.util.*;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.common.data.gun_data.GunSoundType;
import xfacthd.r6mod.common.data.itemsubtypes.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class R6SoundEvents
{
    private static final Map<EnumGun, Map<GunSoundType, SoundEvent>> GUN_SOUNDS = new HashMap<>();
    private static final Map<EnumGadget, Map<String, SoundEvent>> GADGET_SOUNDS = new HashMap<>();
    private static final Map<String, SoundEvent> MISC_SOUNDS = new HashMap<>();

    @SubscribeEvent
    public static void registerSounds(final RegistryEvent.Register<SoundEvent> event)
    {
        registerMiscSound(event.getRegistry(), "mag_filler_mag_in");
        registerMiscSound(event.getRegistry(), "mag_filler_mag_out");

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
                event.getRegistry().register(sound);
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
                event.getRegistry().register(soundEvent);
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

    public static class SoundProvider implements IDataProvider
    {
        private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

        private final DataGenerator gen;
        private final String modid;
        private final List<SoundEventBuilder> builders = new ArrayList<>();

        public SoundProvider(DataGenerator gen, String modid)
        {
            this.gen = gen;
            this.modid = modid;
        }

        @Override
        public void act(DirectoryCache cache) throws IOException
        {
            builders.clear();
            generate();
            if (!builders.isEmpty()) { save(cache); }
        }

        private void generate()
        {
            for (EnumGun gun : EnumGun.values())
            {
                Map<GunSoundType, SoundEvent> sounds = GUN_SOUNDS.get(gun);
                for (GunSoundType type : sounds.keySet())
                {
                    ResourceLocation loc = new ResourceLocation(R6Mod.MODID, "gun/" + gun.getName() + "_" + type.getName());

                    SoundEventBuilder eventBuilder = new SoundEventBuilder(sounds.get(type))
                            .subtitle("subtitle.r6mod.gun." + type.getName())
                            .addSound(new SoundBuilder(loc));
                    builders.add(eventBuilder);
                }
            }

            for (EnumGadget gadget : EnumGadget.values())
            {
                Map<String, SoundEvent> sounds = GADGET_SOUNDS.get(gadget);
                for (String sound : sounds.keySet())
                {
                    ResourceLocation loc = new ResourceLocation(R6Mod.MODID, "gadget/" + gadget.getObjectName() + "_" + sound);

                    SoundEventBuilder eventBuilder = new SoundEventBuilder(sounds.get(sound))
                            .subtitle("subtitle.r6mod.gadget." + gadget.getObjectName() + "." + sound)
                            .addSound(new SoundBuilder(loc));
                    builders.add(eventBuilder);
                }
            }

            for (String sound : MISC_SOUNDS.keySet())
            {
                SoundEvent event = MISC_SOUNDS.get(sound);
                SoundEventBuilder eventBuilder = new SoundEventBuilder(event)
                        .subtitle("subtitle.r6mod." + sound)
                        .addSound(new SoundBuilder(event.getRegistryName()));
                builders.add(eventBuilder);
            }
        }

        private void save(DirectoryCache cache) throws IOException
        {
            JsonObject obj = new JsonObject();
            for (SoundEventBuilder builder : builders) { obj.add(builder.getPath(), builder.build()); }
            Path outPath = gen.getOutputFolder().resolve("assets/" + modid + "/sounds.json");
            IDataProvider.save(GSON, cache, obj, outPath);
        }

        @Override
        public String getName() { return "r6mod:sound_generator"; }
    }

    @SuppressWarnings({"unused", "UnusedReturnValue"})
    private static class SoundEventBuilder
    {
        private final SoundEvent event;
        private boolean replace = false;
        private String translationKey;
        private final Map<ResourceLocation, SoundBuilder> sounds = new HashMap<>();

        public SoundEventBuilder(SoundEvent event) { this.event = event; }

        public SoundEventBuilder replace()
        {
            replace = true;
            return this;
        }

        public SoundEventBuilder subtitle(String key)
        {
            translationKey = key;
            return this;
        }

        public SoundEventBuilder addSound(SoundBuilder sound)
        {
            if (sounds.containsKey(sound.getLocation()))
            {
                throw new RuntimeException("SoundEvent '" + event.getName().toString() + "' already contains Sound '" + sound.getLocation().toString() + "'!");
            }
            sounds.put(sound.getLocation(), sound);
            return this;
        }

        public JsonElement build()
        {
            JsonObject obj = new JsonObject();

            if (replace) { obj.addProperty("replace", true); }
            if (translationKey != null) { obj.addProperty("subtitle", translationKey); }
            if (!sounds.isEmpty())
            {
                JsonArray array = new JsonArray();
                for (SoundBuilder sound : sounds.values()) { array.add(sound.build()); }
                obj.add("sounds", array);
            }

            return obj;
        }

        public String getPath() { return event.getName().getPath(); }
    }

    @SuppressWarnings({"unused", "UnusedReturnValue"})
    private static class SoundBuilder
    {
        private final ResourceLocation location;
        private float volume = 1;
        private float pitch = 1;
        private int weight = 1;
        private boolean stream;
        private int attenuationDistance = 16;
        private boolean preload;
        private String type = "sound";

        public SoundBuilder(ResourceLocation loc) { this.location = loc; }

        public SoundBuilder volume(float v)
        {
            if (v < 0F || v > 1F) { throw new RuntimeException("Volume for '" + location.toString() + "'must be 0 < v < 1!"); }
            volume = v;
            return this;
        }

        public SoundBuilder pitch(float p)
        {
            pitch = p;
            return this;
        }

        public SoundBuilder weight(int w)
        {
            if (w < 1) { throw new RuntimeException("Weight for '" + location.toString() + "'must be > 1!"); }
            weight = w;
            return this;
        }

        public SoundBuilder stream()
        {
            stream = true;
            return this;
        }

        public SoundBuilder attenuationDistance(int d)
        {
            if (d < 1) { throw new RuntimeException("Attenuation distance for '" + location.toString() + "'must be > 1!"); }
            attenuationDistance = d;
            return this;
        }

        public SoundBuilder preload()
        {
            preload = true;
            return this;
        }

        public SoundBuilder event()
        {
            type = "event";
            return this;
        }

        public JsonElement build()
        {
            JsonObject obj = new JsonObject();
            obj.addProperty("name", serializeLocation());

            if (volume != 1F) { obj.addProperty("volume", volume); }
            if (pitch != 1F) { obj.addProperty("pitch", pitch); }
            if (weight != 1F) { obj.addProperty("weight", weight); }
            if (stream) { obj.addProperty("stream", true); }
            if (attenuationDistance != 16) { obj.addProperty("attenuation_distance", attenuationDistance); }
            if (preload) { obj.addProperty("preload", true); }
            if (!type.equals("sound")) { obj.addProperty("type", type); }

            return obj;
        }

        private String serializeLocation()
        {
            if (location.getNamespace().equals("minecraft")) { return location.getPath(); }
            return location.toString();
        }

        public ResourceLocation getLocation() { return location; }
    }
}