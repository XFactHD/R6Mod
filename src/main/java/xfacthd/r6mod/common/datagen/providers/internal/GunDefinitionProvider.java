package xfacthd.r6mod.common.datagen.providers.internal;

import com.google.gson.*;
import net.minecraft.data.*;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.common.data.gun_data.Firemode;
import xfacthd.r6mod.common.data.gun_data.ReloadState;
import xfacthd.r6mod.common.data.itemsubtypes.EnumAttachment;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGun;
import xfacthd.r6mod.common.util.Utils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Locale;

@SuppressWarnings("unused")
public final class GunDefinitionProvider implements IDataProvider
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final DataGenerator gen;
    private final String modid;

    public GunDefinitionProvider(DataGenerator gen)
    {
        this.gen = gen;
        this.modid = R6Mod.MODID;
    }

    @Override
    public void act(DirectoryCache cache) throws IOException
    {
        for (EnumGun gun : EnumGun.values())
        {
            JsonObject obj = new JsonObject();

            obj.addProperty("name", gun.toString().toLowerCase(Locale.ENGLISH));
            obj.addProperty("type", gun.getGunType().toString().toLowerCase(Locale.ENGLISH));
            obj.addProperty("has_mag", gun.hasMag());
            if (!gun.hasMag())
            {
                obj.addProperty("mag_cap", gun.getMagCapacity());
                obj.addProperty("ammo_type", gun.getAmmoType().toString().toLowerCase(Locale.ENGLISH));
            }
            obj.addProperty("rpm", gun.getRoundsPerMinute());
            obj.addProperty("damage", gun.getGunDamageBase() * 5F);
            obj.addProperty("damage_silenced", gun.getGunDamageSilenced() * 5F);
            obj.addProperty("spread", gun.getBaseSpread());
            obj.addProperty("recoil", gun.getBaseRecoil());
            obj.addProperty("ads_time", gun.getAimTime(Collections.emptyList()));
            obj.addProperty("reserve", gun.getAdditionalAmmo());
            obj.addProperty("automatic", gun.isAutomatic());
            if (gun.getGunType() == EnumGun.Type.SHOTGUN) { obj.addProperty("pump_action", false); }
            obj.addProperty("closed_bolt", gun.canChamberAdditionalBullet());
            if (Utils.arrayContains(gun.getFiremodes(), Firemode.BURST))
            {
                obj.addProperty("burst_count", gun.getBurstBulletCount());
            }
            if (gun.getShotgunPelletCount() != 0) { obj.addProperty("pellet_count", gun.getShotgunPelletCount()); }
            obj.addProperty("max_penetration", gun.getMaxPenetrationCount());

            JsonObject reloadTimes = new JsonObject();
            for (ReloadState state : ReloadState.values())
            {
                reloadTimes.addProperty(state.toString().toLowerCase(Locale.ENGLISH), 0);
            }
            obj.add("reload_times", reloadTimes);

            JsonObject attachments = new JsonObject();
            for (EnumAttachment attachment : gun.getCompatibleAttachements())
            {
                attachments.add(attachment.toString().toLowerCase(Locale.ENGLISH), new JsonObject());
            }
            obj.add("attachments", attachments);

            JsonArray firemodes = new JsonArray();
            for (Firemode firemode : gun.getFiremodes())
            {
                firemodes.add(firemode.toString().toLowerCase(Locale.ENGLISH));
            }
            obj.add("firemodes", firemodes);

            if (gun.hasMag()) { obj.add("mag_transform", new JsonObject()); }

            Path outPath = gen.getOutputFolder().resolve("data/" + modid + "/guns/" + gun.toString().toLowerCase(Locale.ENGLISH) + ".json");
            IDataProvider.save(GSON, cache, obj, outPath);
        }
    }

    @Override
    public String getName() { return modid + ":gun_definitions"; }
}