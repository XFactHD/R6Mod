/*  Copyright (C) <2020>  <XFactHD>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see http://www.gnu.org/licenses. */

package xfacthd.r6mod.common.data.itemsubtypes;

import com.google.gson.*;
import net.minecraft.util.ResourceLocation;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.common.data.gun_data.*;

import javax.annotation.Nonnull;
import java.io.*;
import java.net.URL;
import java.util.*;

public enum EnumGun
{
    //ASSAULT_RIFLE
    L85A2,
    AR33,
    G36_C,
    R4_C,
    SIG_556xi,
    FAMAS_F2 ,
    AK12,
    AUG_A2,
    SIG_552_C,
    HK_416_C,
    C8_SFW,
    MK17_CQB,
    PARA_308,
    TYPE_89,
    C7E,
    M762,
    V308,
    SPEAR_308,
    COLT_M4,
    AK_74M,
    ARX200,
    F90,

    //SNIPER_RIFLE
    HK417,
    OTS_03,
    CAMRS,
    SR25,
    MK14_EBR,
    AR15_50,
    CSRX_300,

    //MACHINE_GUN
    PKP_6P41,
    G8A1,
    M249,
    T_95_LSW,
    LMG_E,
    ALDA_556,
    M249_SAW,

    //SHOTGUN
    M590A1,
    M1014,
    SG_CQB,
    SASG_12,
    M870,
    SUPER_90,
    SPAS_12,
    SPAS_15,
    SUPERNOVA,
    ITA12L,
    ITA12S,
    SIX12,
    SIX12SD,
    FO_12,
    BOSG_12_2,
    ACS12,
    TCSG12,
    SUPER_SHORTY,

    //SUB_MACHINE_GUN
    FMG9,
    MP5K,
    SMG11,
    UMP45,
    MP5,
    P90,
    K9X19VSN,
    MP7,
    STEN_9MM_C1,
    MPX,
    M12,
    MP5SD,
    BEARING_9,
    PDW9,
    VECTOR_45,
    T5_SMG,
    SKORPION_EVO,
    K1A,
    C75_AUTO,
    SMG12,
    MX4_STORM,
    SPSMG9,
    AUG_A3,
    P10_RONI,
    COMMANDO9,

    //PISTOL
    P226_MK25,
    M45_M,
    FN_57_USG,
    P9,
    LFP586,
    GSH_18,
    PMM,
    P12,
    MK1_9MM,
    D_50,
    PRB92,
    LUISON,
    P229,
    USP40,
    Q_929,
    RG15,
    BAILIFF_410,
    KERATOS_357,
    M1911,
    P10_C,
    AMP_44_MAG,
    SDP_9;

    private Type type;
    private EnumMagazine magazine;
    private EnumBullet ammoType;
    private int rpm;
    private int ticksPerShot;
    private float damage;
    private float damageSilenced;
    private float spread;
    private float recoil;
    private int adsTime;
    private int magCap;
    private boolean automatic;
    private int additionalAmmo;
    private Map<ReloadState, Integer> reloadTimes; //TODO: measure reload time and add to weapons, split into different states
    private List<EnumAttachment> attachments;
    private List<EnumAttachment.Type> attachmentSlots;
    private Firemode[] firemodes;
    private int burstCount;
    private int shotgunPelletCount;
    private int chargeTime;
    private boolean chamberAdditional;
    private boolean pumpAction;
    private int maxPenetration;
    private ResourceLocation symbol;

    @Nonnull
    public Type getGunType() { return type; }

    @Nonnull
    public EnumMagazine getMagazine() { return magazine; }

    @Nonnull
    public EnumBullet getAmmoType() { return ammoType; }

    public int getRoundsPerMinute() { return rpm; }

    public int getTicksBetweenRounds() { return ticksPerShot; }

    /** returns 0 if the gun is integrally silenced */
    public float getGunDamageBase() { return damage; }

    public float getGunDamageSilenced() { return damageSilenced; }

    public float getBaseSpread() { return spread; }

    public float getBaseRecoil() { return recoil; }

    public int getMagCapacity() { return hasMag() ? magazine.getMagCap() : magCap; }

    /** If the gun is of EnumGunType shotgun, this returns if the gun is semi automatic or not */
    public boolean isAutomatic() { return automatic; }

    public int getAdditionalAmmo() { return additionalAmmo; }

    public long getChargeTime() { return chargeTime; }

    public long getReloadStateTime(ReloadState state) { return reloadTimes.getOrDefault(state, 0); }

    public boolean hasMag() { return magazine != EnumMagazine.NONE; }

    public int getBurstBulletCount() { return burstCount; }

    public int getShotgunPelletCount() { return shotgunPelletCount; }

    public boolean canChamberAdditionalBullet() { return chamberAdditional; }

    public boolean isPumpAction() { return pumpAction; }

    @Nonnull
    public List<EnumAttachment> getCompatibleAttachements() { return attachments; }

    @Nonnull
    public List<EnumAttachment.Type> getAttachmentSlots() { return attachmentSlots; }

    @Nonnull
    public Firemode[] getFiremodes() { return firemodes; }



    public float getActualDamage(List<EnumAttachment> attachments)
    {
        return isSilenced(attachments) ? getGunDamageSilenced() : getGunDamageBase();
    }

    public float getSpreadModified(List<EnumAttachment> attachments)
    {
        float spread = this.spread;
        for (EnumAttachment attachment : attachments)
        {
            spread *= attachment.getSpreadMultiplier();
        }
        return spread;
    }

    public float getRecoilModified(List<EnumAttachment> attachments)
    {
        float recoil = this.recoil;
        for (EnumAttachment attachment : attachments)
        {
            recoil *= attachment.getRecoilMultiplier();
        }
        return recoil;
    }

    public boolean isSilenced(List<EnumAttachment> attachments)
    {
        if (this == MP5SD || this == SMG12) { return true; } //Integrally suppressed
        return attachments.contains(EnumAttachment.SUPPRESSOR);
    }

    public int getAimTime(List<EnumAttachment> attachments)
    {
        int time = adsTime;
        for (EnumAttachment attachment : attachments)
        {
            time = (int)((float)time * attachment.getAimTimeMultiplier());
        }
        return time;
    }

    //public RecipeGunCrafting getRecipe() { return Crafting.getGunRecipe(this); }

    public int getMaxPenetrationCount() { return maxPenetration; }

    public GunSoundType[] getValidSounds()
    {
        List<GunSoundType> sounds = new ArrayList<>();

        sounds.add(GunSoundType.FIRE);
        sounds.add(GunSoundType.LOCK_BACK);
        sounds.add(GunSoundType.EMPTY_TRIGGER);

        reloadTimes.keySet().forEach((state) -> sounds.add(state.getSound()));

        return sounds.toArray(new GunSoundType[0]);
    }

    public String toItemName() { return "item_gun_" + getName(); }

    public String getName() { return toString().toLowerCase(Locale.ENGLISH); }

    public ResourceLocation getDeathMessageSymbol()
    {
        if (symbol == null) { symbol = new ResourceLocation(R6Mod.MODID, "textures/gui/widgets/guns/" + toString().toLowerCase(Locale.ENGLISH) + ".png"); }
        return symbol;
    }



    private void parseJson(JsonObject json)
    {
        String name = json.get("name").getAsString();
        type = Type.valueOf(json.get("type").getAsString().toUpperCase(Locale.ENGLISH));
        boolean hasMag = json.get("has_mag").getAsBoolean();
        if (hasMag)
        {
            if (json.has("magazine")) { magazine = EnumMagazine.valueOf(json.get("magazine").getAsString().toUpperCase(Locale.ENGLISH)); }
            else { magazine = EnumMagazine.valueOf(("mag_" + name).toUpperCase(Locale.ENGLISH)); }
            magCap = magazine.getMagCap();
            ammoType = magazine.getBullet();
        }
        else
        {
            magazine = EnumMagazine.NONE;
            magCap = json.get("mag_cap").getAsInt();
            ammoType = EnumBullet.valueOf(json.get("ammo_type").getAsString().toUpperCase(Locale.ENGLISH));
        }
        rpm = json.get("rpm").getAsInt();
        ticksPerShot = (int)Math.ceil(1200.0 / (double)rpm);
        damage = json.get("damage").getAsFloat();
        damageSilenced = json.get("damage_silenced").getAsFloat();
        spread = json.get("spread").getAsFloat();
        recoil = json.get("recoil").getAsFloat();
        adsTime = json.get("ads_time").getAsInt();
        additionalAmmo = json.get("reserve").getAsInt();
        automatic = json.get("automatic").getAsBoolean();
        pumpAction = json.has("pump_action") && json.get("pump_action").getAsBoolean();
        chargeTime = pumpAction ? json.get("charge_time").getAsInt() : 0;
        chamberAdditional = json.get("closed_bolt").getAsBoolean();
        parseReloadTimes(json.getAsJsonObject("reload_times"));
        parseAttachments(json.getAsJsonObject("attachments"));
        parseFiremodes(json.getAsJsonArray("firemodes"));
        burstCount = json.has("burst_count") ? json.get("burst_count").getAsInt() : 0;
        shotgunPelletCount = json.has("pellet_count") ? json.get("pellet_count").getAsInt() : 0;
        maxPenetration = json.has("max_penetration") ? json.get("max_penetration").getAsInt() : 0;
        calculateAttachmentSlots();
        if (hasMag) { R6Mod.getSidedHelper().parseMagazineTransforms(this, json.getAsJsonObject("mag_transform")); }
    }

    private void parseReloadTimes(JsonObject json)
    {
        reloadTimes = new TreeMap<>();

        json.entrySet().forEach((entry) ->
        {
            ReloadState state = ReloadState.valueOf(entry.getKey().toUpperCase(Locale.ENGLISH));
            int time = entry.getValue().getAsInt();
            reloadTimes.put(state, time);
        });
    }

    private void parseAttachments(JsonObject json)
    {
        attachments = new ArrayList<>();

        json.entrySet().forEach((entry) ->
        {
            EnumAttachment attach = EnumAttachment.valueOf(entry.getKey().toUpperCase(Locale.ENGLISH));
            attachments.add(attach);
            R6Mod.getSidedHelper().parseAttachmentTransforms(this, attach, entry.getValue().getAsJsonObject());
        });
    }

    private void parseFiremodes(JsonArray json)
    {
        firemodes = new Firemode[json.size()];
        for (int i = 0; i < json.size(); i++)
        {
            firemodes[i] = Firemode.valueOf(json.get(i).getAsString().toUpperCase(Locale.ENGLISH));
        }
    }

    private void calculateAttachmentSlots()
    {
        List<EnumAttachment.Type> types = new ArrayList<>();
        for (EnumAttachment a : attachments)
        {
            EnumAttachment.Type type = a.getType();
            if (!types.contains(type)) { types.add(type); }
        }
        attachmentSlots = types;
    }



    public static void initializeValueDetails()
    {
        for (EnumGun gun : values())
        {
            JsonObject json = loadFile(gun);
            gun.parseJson(json);
        }
    }

    private static JsonObject loadFile(EnumGun gun)
    {
        URL fileUrl = EnumGun.class.getResource("/data/r6mod/guns/" + gun.toString().toLowerCase(Locale.ENGLISH) + ".json");
        if (fileUrl == null) { throw new IllegalArgumentException(String.format("No json exists for gun %s!", gun.toString())); }

        try
        {
            JsonParser parser = new JsonParser();
            JsonElement elem = parser.parse(new InputStreamReader(fileUrl.openStream()));
            return elem.getAsJsonObject();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to load gun json from the jar!", e);
        }
    }



    public enum Type
    {
        ASSAULT_RIFLE,
        SNIPER_RIFLE,
        MACHINE_GUN,
        SHOTGUN,
        SUB_MACHINE_GUN,
        PISTOL,
        REVOLVER
    }
}