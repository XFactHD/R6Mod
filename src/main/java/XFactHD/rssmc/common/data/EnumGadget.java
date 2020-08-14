/*  Copyright (C) <2017>  <XFactHD>

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

package XFactHD.rssmc.common.data;

import XFactHD.rssmc.common.Content;
import XFactHD.rssmc.common.blocks.BlockBase;
import XFactHD.rssmc.common.utils.Reference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.List;

public enum EnumGadget
{
    SLEDGE_HAMMER        (Content.itemSledgeHammer,        1, false, new String[] { "hit" }),
    EMP_GRENADE          (Content.itemEMPGrenade,          3, false, new String[] { "charge", "explode" }),
    JAMMER               (Content.blockJammer,             4,        new String[] { "place", "work" }),
    GAS_GRENADE          (Content.itemGasCanister,         3, false, new String[] { "explode" }),
    THERMITE_CHARGE      (Content.blockThermiteCharge,     2,        new String[] { "burn" }),
    GRENADE_LAUNCHER     (Content.itemGrenadeLauncher,     1, false, new String[] { "reload", "fire" }, new ItemStack[] {new ItemStack(Content.itemBreachGrenade, 2)}),
    CARDIAC_SENSOR       (Content.itemCardiacSensor,       1, false, new String[] { "deploy", "work", "undeploy" }),
    TOUGH_BARICADE       (Content.blockToughBarricade,     3,        new String[] { "deploy", "hit", "breakdown" }),
    SHOCK_DRONE          (Content.itemShockDrone,          2, false, new String[] { "drive", "zap" }),
    ARMOR_BAG            (Content.blockArmorBag,           1,        new String[] { "use" }),
    STIM_PISTOL          (Content.itemStimPistol,          1, false, new String[] { "fire", "reload" }, new ItemStack[] {new ItemStack(Content.itemStimDart, 4)}),
    INTEROGATION_KNIVE   (Content.itemInterogationKnive,   1, false, new String[] { "swing", "hit" }),
    YOKAI_DRONE          (Content.itemYokaiDrone,          1, false, new String[] { "hover", "move", "jump", "fire" }),
    CLUSTER_CHARGE       (Content.blockClusterCharge,      3,        new String[] { "fire" }),
    MOUNTED_LMG          (Content.blockLMG,                1,        new String[] { "fire", "reload", "charge", "destroy" }, new ItemStack[] {new ItemStack(Content.itemMountedLMGMag, 8)}),
    KAPKAN_TRAP          (Content.blockKapkanTrap,         3,        new String[] { "place" }),
    ELECTRONICS_DETECTOR (Content.itemElectronicsDetector, 1, true,  new String[] { "deploy", "detect", "undeploy" }),
    ACTIVE_DEFENSE_SYSTEM(Content.blockADS,                3,        new String[] { "place", "work", "shoot" }),
    SHOCK_WIRE           (Content.blockShockWire,          3,        new String[] { "work", "zap" }),
    WELCOME_MAT          (Content.blockWelcomeMat,         3,        new String[] { "activate" }),
    STICKY_CAMERA        (Content.itemBlackEyeCamera,      3, false, new String[] { "deploy", "turn" }),
    CROSSBOW             (Content.itemCrossbow,            1, false, new String[] { "fire", "reload", "charge", "change" }, new ItemStack[] {new ItemStack(Content.itemCrossbowMag, 1, 0), new ItemStack(Content.itemCrossbowMag, 1, 1)}),
    X_KAIROS_LAUNCHER    (Content.itemXKairosLauncher,     1, false, new String[] { "fire", "reload" }, new ItemStack[] {new ItemStack(Content.itemXKairosMag, 3)}),
    BLACK_MIRROR         (Content.blockBlackMirror,        2,        new String[] { "deploy", "open" }),
    CANDELA_GRENADE      (Content.itemCandelaGrenade,      3, false, new String[] { "throw", "deploy", "roll", "explode", "crackle" }),
    GU_MINE              (Content.blockGuMine,             1,        new String[] { "deploy", "explode" }),
    GRZMOT_MINE          (Content.itemGrzmotMine,          4, false, new String[] { "deploy", "explode" }),
    KS79_LAUNCHER        (Content.itemKS79Launcher,        1, false, new String[] { "fire", "switch" }),
    LOGIC_BOMB           (Content.itemLogicBomb,           1, false, null),

    BARRICADE            (Content.blockBarricade,          1,        new String[] { "deploy", "hit", "break" }),
    BARBED_WIRE          (Content.blockBarbedWire,         2,        new String[] { "deploy" }),
    NITRO_CELL           (Content.itemNitroCell,           1, false, new String[] { "beep" }),
    DEPLOYABLE_SHIELD    (Content.blockDeployableShield,   1,        new String[] { "deploy", "remove" }),
    IMPACT_GRENADE       (Content.itemImpactGrenade,       2, false, new String[] { "pullRing" }),

    FRAG_GRENADE         (Content.itemFragGrenade,         2, false, new String[] { "pullRing", "bounce" }),
    STUN_GRENADE         (Content.itemStunGrenade,         3, false, new String[] { "pullRing", "bounce", "explode" }),
    SMOKE_GRENADE        (Content.itemSmokeGrenade,        2, false, new String[] { "pullRing", "bounce", "explode" }),
    BREACH_CHARGE        (Content.blockBreachCharge,       3,        null),
    CLAYMORE             (Content.blockClaymore,           1,        new String[] { "place", "activate", "detect" }),

    DRONE                (Content.itemDrone,               1, false, new String[] { "drive" }),
    PHONE                (Content.itemPhone,               1, false, new String[] { "ring" }),
    CAMERA               (Content.blockCamera,             0,        new String[] { "turn" }),
    NITRO_PHONE          (Content.itemNitroPhone,          0, false, new String[] { "call" }),
    CHARGE_ACTIVATOR     (Content.itemActivator,           0, false, new String[] { "activate" });

    private Item gadgetItem;
    private int stackSize;
    private boolean needsAmmo;
    private ItemStack[] ammoStacks;
    private boolean leftHanded;
    private List<String> sounds;

    EnumGadget(Item gadgetItem, int stackSize, boolean leftHanded, String[] sounds, ItemStack[] ammoStacks)
    {
        this.gadgetItem = gadgetItem;
        this.stackSize = stackSize;
        this.needsAmmo = ammoStacks != null;
        this.ammoStacks = ammoStacks;
        this.leftHanded = leftHanded;
        this.sounds = sounds != null && sounds.length != 0 ? Arrays.asList(sounds) : null;
    }

    EnumGadget(Item gadgetItem, int stackSize, boolean leftHanded, String[] sounds)
    {
        this(gadgetItem, stackSize, leftHanded, sounds, null);
    }

    EnumGadget(BlockBase gadgetBlock, int stackSize, String[] sounds, ItemStack[] ammoStacks)
    {
        this(Item.getItemFromBlock(gadgetBlock), stackSize, false, sounds, ammoStacks);
    }

    EnumGadget(BlockBase gadgetBlock, int stackSize, String[] sounds)
    {
        this(gadgetBlock, stackSize, sounds, null);
    }

    public List<String> getSounds()
    {
        return sounds;
    }

    public Item getGadgetItem()
    {
        return gadgetItem;
    }

    public ItemStack getGadgetItemStack()
    {
        return getGadgetItemStack(stackSize);
    }

    public ItemStack getGadgetItemStack(int stackSize)
    {
        return new ItemStack(gadgetItem, stackSize);
    }

    public boolean needsAmmo()
    {
        return needsAmmo;
    }

    public ItemStack[] getAmmoStacks()
    {
        return ammoStacks;
    }

    public boolean isLeftHanded()
    {
        return leftHanded;
    }

    public String getDisplayName()
    {
        return getGadgetItemStack().getDisplayName();
    }

    public ResourceLocation getDeathMessagePictogram()
    {
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/widgets/gadgets/" + toString().toLowerCase() + ".png");
    }
}