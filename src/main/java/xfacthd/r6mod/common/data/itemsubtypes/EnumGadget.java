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

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import xfacthd.r6mod.R6Mod;

import java.util.*;

public enum EnumGadget
{
    SLEDGE_HAMMER        (false, "sledge_hammer",      -1, -1, new String[] { "hit" }),
    EMP_GRENADE          (false, "emp_grenade",        -1, -1, new String[] { "charge", "explode" }),
    JAMMER               (false, "jammer",             -1, -1, new String[] { "work" }),
    GAS_GRENADE          ( true, "gas_grenade",        -1, 12, new String[] { "explode" }),
    THERMITE_CHARGE      ( true, "thermite_charge",    12, 12, new String[] { "burn" }),
    GRENADE_LAUNCHER     (false, "grenade_launcher",   -1, -1, new String[] { "reload", "fire" }),
    CARDIAC_SENSOR       (false, "cardiac_sensor",     -1, -1, new String[] { "deploy", "work", "undeploy" }),
    TOUGH_BARICADE       (false, "armor_panel",        12, 12, new String[] { }),
    SHOCK_DRONE          (false, "shock_drone",        -1, 12, new String[] { "drive", "zap" }),
    EXTENDABLE_SHIELD    (false, "extendable_shield",  -1, -1, new String[] { "extend", "contract" }),
    ARMOR_BAG            (false, "armor_bag",          12, -1, new String[] { "use" }),
    STIM_PISTOL          (false, "stim_pistol",        -1, -1, new String[] { "fire", "reload" }),
    CLUSTER_CHARGE       ( true, "cluster_charge",     12, 12, new String[] { "fire" }),
    KAPKAN_TRAP          (false, "kapkan_trap",        12, 12, new String[] { }),
    SHUMIKHA_LAUNCHER    (false, "shumikha_launcher",  -1, -1, new String[] { "fire", "reload" }),
    FLASH_SHIELD         (false, "flash_shield",       -1, -1, new String[] { "flash" }),
    ELECTRONICS_DETECTOR (false, "electro_detector",   -1, -1, new String[] { "deploy", "detect", "undeploy" }),
    ACTIVE_DEFENSE_SYSTEM(false, "ads",                12, 12, new String[] { "work", "shoot" }),
    SHOCK_WIRE           (false, "shock_wire",         12, 12, new String[] { "work", "zap" }),
    WELCOME_MAT          (false, "welcome_mat",        12, 12, new String[] { "trigger" }),
    BLACK_EYE            (false, "black_eye",          -1, 12, new String[] { "deploy", "turn" }),
    CROSSBOW             (false, "crossbow",           -1, -1, new String[] { "fire", "reload", "charge", "change" }),
    INTEROGATION_KNIVE   (false, "cav_knive",          -1, -1, new String[] { }),
    X_KAIROS_LAUNCHER    (false, "x_kairos_launcher",  -1, -1, new String[] { "fire", "reload" }),
    YOKAI_DRONE          (false, "yokai_drone",        -1, 12, new String[] { "hover", "move", "jump", "fire" }),
    BLACK_MIRROR         (false, "black_mirror",       12, -1, new String[] { "open", "shatter" }),
    CANDELA_GRENADE      (false, "candela",            -1, -1, new String[] { "place", "roll", "fire", "crackle" }),
    GU_MINE              (false, "gu_mine",            -1, 12, new String[] { "deploy", "trigger" }),
    GRZMOT_MINE          (false, "grzmot_mine",        -1, 12, new String[] { "deploy", "trigger" }),
    LOGIC_BOMB           (false, "logic_bomb",         -1, -1, new String[] { "hack", "call" }),
    ERC7                 (false, "erc7",               -1, -1, new String[] { "activate", "deactivate" }),
    KS79_LIFELINE        (false, "lifeline",           -1, -1, new String[] { "switch", "fire" }),
    EEONED               (false, "eeoned",             -1, -1, new String[] { "count", "noise" }),
    ADRENAL_SURGE        (false, "adrenal_surge",      -1, -1, new String[] { "activate", "beat" }),
    PRISMA               (false, "prisma",             -1, 12, new String[] { "deploy", "switch" }),
    EVIL_EYE             (false, "evil_eye",           12, 12, new String[] { "open", "close", "turn", "fire", "overheat", "beep" }),
    BREACHING_TORCH      (false, "breaching_torch",    -1, -1, new String[] { "burn", "recharge" }),
    CCE_SHIELD           (false, "cce_shield",         -1, -1, new String[] { "extend", "contract", "fire" }),
    AIRJAB               (false, "airjab",             -1, 12, new String[] { "fire", "deploy", "beep", "trigger" }),
    ELECTROCLAW          (false, "electroclaw",        -1, 12, new String[] { "deploy", "work" }),
    TRAX_STINGER         (false, "trax_stinger",       -1, -1, new String[] { "deploy", "step" }),
    PEST_LAUNCHER        (false, "pest_launcher",      -1, 12, new String[] { "fire", "idle", "near", "attack" }),
    HEL_PR               (false, "hel_pr",             -1, -1, new String[] { "activate", "deactivate" }),
    GLANCE_GLASSES       (false, "glance_glasses",     -1, -1, new String[] { "activate", "deactivate" }),
    GARRA_HOOK           (false, "garra_hook",         -1, -1, new String[] { "fire", "hook", "reel" }),
    VOLCAN_SHIELD        (false, "volcan_shield",      12, 12, new String[] { }),
    GEMINI_REPLICATOR    (false, "gemini_replicator",  -1, -1, new String[] { "spawn", "despawn", "destroy" }),
    MAG_NET              (false, "mag_net",            -1, 12, new String[] { "deploy", "activate", "catch" }),
    SELMA_BREACHER       (false, "selma_breacher",     -1, 12, new String[] { "impact", "deploy", "work", "explode" }),
    BANSHEE              (false, "banshee",            12, 12, new String[] { "noise" }),
    SURYA_LASER_GATE     (false, "surya_laser_gate",   12, 12, new String[] { "deploy", "activate", "trigger" }),

    BARRICADE            (false, "barricade",          12, 12, new String[] { }),
    BARBED_WIRE          (false, "barbed_wire",        12, 12, new String[] { "deploy", "step" }),
    NITRO_CELL           (false, "nitro_cell",         -1, 12, new String[] { "beep", "explode" }),
    DEPLOYABLE_SHIELD    (false, "deployable_shield",  12, 12, new String[] { }),
    IMPACT_GRENADE       (false, "impact_grenade",     -1, -1, new String[] { "pull_ring" }),

    FRAG_GRENADE         (false, "frag_grenade",       -1, -1, new String[] { "pull_ring" }),
    STUN_GRENADE         (false, "stun_grenade",       -1, -1, new String[] { "pull_ring", "explode" }),
    SMOKE_GRENADE        (false, "smoke_grenade",      -1, -1, new String[] { "pull_ring", "explode" }),
    BREACH_CHARGE        ( true, "breach_charge",      12, 12, new String[] { }),
    CLAYMORE             (false, "claymore",           12, 12, new String[] { "activate", "detect" }),
    BULLETPROOF_CAMERA   (false, "bulletproof_camera", 12, 12, new String[] { }),

    DRONE                (false, "drone",              -1, 12, new String[] { "move" }),
    PHONE                (false, "phone",              -1, -1, new String[] { "ring" }),
    CAMERA               (false, "camera",             -1, -1, new String[] { "turn" }),
    NITRO_PHONE          (false, "nitro_phone",        -1, -1, new String[] { "call" }),
    CHARGE_ACTIVATOR     (false, "activator",          -1, -1, new String[] { "trigger" });

    private final boolean needsActivator;
    private final String objectName;
    private final int placeTime;
    private final int pickupTime;
    private final List<String> sounds;
    private ResourceLocation symbol;
    private TranslationTextComponent placeMsg = null;
    private TranslationTextComponent pickupMsg = null;

    EnumGadget(boolean needsActivator, String objectName, int placeTime, int pickupTime, String[] sounds)
    {
        this.needsActivator = needsActivator;
        this.objectName = objectName;
        this.placeTime = placeTime;
        this.pickupTime = pickupTime;
        this.sounds = sounds != null && sounds.length != 0 ? Arrays.asList(sounds) : Collections.emptyList();
    }

    public boolean getNeedsActivator() { return needsActivator; }

    public String getObjectName() { return objectName; }

    public int getPlaceTime() { return placeTime; }

    public int getPickupTime() { return pickupTime; }

    public boolean canHardDestruct()
    {
        return this == THERMITE_CHARGE || this == X_KAIROS_LAUNCHER || this == BREACHING_TORCH || this == SELMA_BREACHER;
    }

    public List<String> getSounds() { return sounds; }

    public ResourceLocation getDeathMessageSymbol()
    {
        if (symbol == null) { symbol = new ResourceLocation(R6Mod.MODID, "textures/gui/widgets/gadgets/" + toString().toLowerCase(Locale.ENGLISH) + ".png"); }
        return symbol;
    }

    public TranslationTextComponent getPlaceMessage()
    {
        if (placeMsg == null)
        {
            placeMsg = new TranslationTextComponent("msg.r6mod.place", new TranslationTextComponent("gadget.r6mod." + toString().toLowerCase(Locale.ENGLISH)));
        }
        return placeMsg;
    }

    public TranslationTextComponent getPickupMessage()
    {
        if (pickupMsg == null)
        {
            pickupMsg = new TranslationTextComponent("msg.r6mod.pickup", new TranslationTextComponent("gadget.r6mod." + toString().toLowerCase(Locale.ENGLISH)));
        }
        return pickupMsg;
    }
}