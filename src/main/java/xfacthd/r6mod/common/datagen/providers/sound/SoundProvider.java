package xfacthd.r6mod.common.datagen.providers.sound;

import com.google.gson.*;
import net.minecraft.data.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.common.data.gun_data.GunSoundType;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGun;
import xfacthd.r6mod.common.data.types.R6SoundEvents;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class SoundProvider implements IDataProvider
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final DataGenerator gen;
    private final String modid;
    private final List<SoundEventBuilder> builders = new ArrayList<>();

    public SoundProvider(DataGenerator gen)
    {
        this.gen = gen;
        this.modid = R6Mod.MODID;
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
            Map<GunSoundType, SoundEvent> sounds = R6SoundEvents.GUN_SOUNDS.get(gun);
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
            Map<String, SoundEvent> sounds = R6SoundEvents.GADGET_SOUNDS.get(gadget);
            for (String sound : sounds.keySet())
            {
                ResourceLocation loc = new ResourceLocation(R6Mod.MODID, "gadget/" + gadget.getObjectName() + "_" + sound);

                SoundEventBuilder eventBuilder = new SoundEventBuilder(sounds.get(sound))
                        .subtitle("subtitle.r6mod.gadget." + gadget.getObjectName() + "." + sound)
                        .addSound(new SoundBuilder(loc));
                builders.add(eventBuilder);
            }
        }

        for (String sound : R6SoundEvents.MISC_SOUNDS.keySet())
        {
            SoundEvent event = R6SoundEvents.MISC_SOUNDS.get(sound);
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
    public String getName() { return modid + ":sound_generator"; }

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