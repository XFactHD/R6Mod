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

import XFactHD.rssmc.api.capability.IGunHandler;
import XFactHD.rssmc.common.Content;
import XFactHD.rssmc.common.capability.gunHandler.GunHandlerStorage;
import XFactHD.rssmc.common.crafting.Crafting;
import XFactHD.rssmc.common.crafting.recipes.RecipeGunCrafting;
import XFactHD.rssmc.common.items.gun.ItemRiotShield;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.*;

public enum EnumGun
{
    //ASSAULT_RIFLE
    C8_SFW   (EnumGunType.ASSAULT_RIFLE, EnumMagazine.MAG_C8_SFW,   0,  8.4F,   0F, 0, 0, 5, true),
    L85A2    (EnumGunType.ASSAULT_RIFLE, EnumMagazine.MAG_L85A2,    0,    9F, 7.6F, 0, 0, -1, true),
    AR33     (EnumGunType.ASSAULT_RIFLE, EnumMagazine.MAG_AR33,     0, 10.4F,   7F, 0, 0, -1, true),
    SIG_556xi(EnumGunType.ASSAULT_RIFLE, EnumMagazine.MAG_SIG_556xi,0,  9.2F, 7.8F, 0, 0, -1, true),
    G36_C    (EnumGunType.ASSAULT_RIFLE, EnumMagazine.MAG_G36_C,    0,  7.6F, 6.4F, 0, 0, -1, true),
    R4_C     (EnumGunType.ASSAULT_RIFLE, EnumMagazine.MAG_R4_C,     0,  8.2F, 6.8F, 0, 0, -1, true),
    FAMAS_F2 (EnumGunType.ASSAULT_RIFLE, EnumMagazine.MAG_FAMAS_F2, 0,  7.8F, 6.6F, 0, 0, -1, true),
    AK12     (EnumGunType.ASSAULT_RIFLE, EnumMagazine.MAG_AK12,     0,  8.8F, 7.4F, 0, 0, -1, true),
    HK_416_C (EnumGunType.ASSAULT_RIFLE, EnumMagazine.MAG_HK_416_C, 0,  8.4F,   7F, 0, 0, -1, true),
    SIG_552_C(EnumGunType.ASSAULT_RIFLE, EnumMagazine.MAG_SIG_552_C,0,  9.4F, 7.8F, 0, 0, -1, true),
    AUG_A2   (EnumGunType.ASSAULT_RIFLE, EnumMagazine.MAG_AUG_A2,   0,  8.2F, 6.8F, 0, 0, -1, true),
    Mk17_CQB (EnumGunType.ASSAULT_RIFLE, EnumMagazine.MAG_Mk17_CQB, 0, 10.4F, 8.8F, 0, 0, -1, true),
    PARA_308 (EnumGunType.ASSAULT_RIFLE, EnumMagazine.MAG_PARA_308, 0, 10.4F, 8.8F, 0, 0, -1, true),
    TYPE_89  (EnumGunType.ASSAULT_RIFLE, EnumMagazine.MAG_TYPE_89,  0,  8.2F, 6.8F, 0, 0, -1, true),
    C7E      (EnumGunType.ASSAULT_RIFLE, EnumMagazine.MAG_C7E,      0,  0.0F, 0.0F, 0, 0, -1, true),

    //SNIPER_RIFLE
    HK417 (EnumGunType.SNIPER_RIFLE, EnumMagazine.MAG_HK417,  0,  13.2F, 11.2F, 0, 0, -1, false),
    OTS_03(EnumGunType.SNIPER_RIFLE, EnumMagazine.MAG_OTS_03, 0,  30.4F, 25.8F, 0, 0, -1, false),
    CAMRS (EnumGunType.SNIPER_RIFLE, EnumMagazine.MAG_CAMRS,  0,  13.6F, 11.4F, 0, 0, -1, false),
    SR25  (EnumGunType.SNIPER_RIFLE, EnumMagazine.MAG_SR25,   0,  14.2F,   12F, 0, 0, -1, false),

    //MACHINE_GUN
    G8A1     (EnumGunType.MACHINE_GUN, EnumMagazine.MAG_G8A1,     0, 7.4F, 6.2F, 0, 0, -1, true),
    PKP_6P41 (EnumGunType.MACHINE_GUN, EnumMagazine.MAG_PKP_6P41, 0, 9.8F, 8.2F, 0, 0, -1, true),
    M249     (EnumGunType.MACHINE_GUN, EnumMagazine.MAG_M249,     0, 6.6F,   0F, 0, 0, -1, true),
    T_95_LSW (EnumGunType.MACHINE_GUN, EnumMagazine.MAG_T_95_LSW, 0,   -1,   -1, 0, 0, -1, true),

    //SHOTGUN
    M590A1   (                          0,    9F, 0.0F, 0, 0, 7, 29, false),
    M1014    (                          0,  6.4F, 0.0F, 0, 0, 8, 25, true),
    M870     (                          0, 11.4F, 0.0F, 0, 0, 7, 29, false),
    SG_CQB   (                          0,   10F, 0.0F, 0, 0, 7, 29, false),
    SASG_12  (EnumMagazine.MAG_SASG_12, 0,  9.4F, 7.8F, 0, 0, 3, true),
    SUPER_90 (                          0,    6F, 0.0F, 0, 0, 8, 25, true),
    SPAS_12  (                          0,  6.6F, 0.0F, 0, 0, 7, 29, true),
    SPAS_15  (EnumMagazine.MAG_SPAS_15, 0,  5.6F, 0.0F, 0, 0, 4, true),
    SUPERNOVA(                          0,  6.4F, 0.0F, 0, 0, 7, 36, false),
    IPS12L   (                          0,  0.0F, 0.0F, 0, 0, 8, 33, false),
    IPS12S   (                          0,  0.0F, 0.0F, 0, 0, 5, 21, false),
    SIX12    (EnumMagazine.MAG_SIX12,   0,  0.0F, 0.0F, 0, 0, 6, true),
    SIX12SD  (EnumMagazine.MAG_SIX12,   0,  0.0F, 0.0F, 0, 0, 6, true),
    F0_12    (EnumMagazine.MAG_F0_12,   0,  0.0F, 0.0F, 0, 0, 0, true),

    //SUB_MACHINE_GUN
    K9X19VSN     (EnumGunType.SUB_MACHINE_GUN, EnumMagazine.MAG_K9X19VSN,      0, 6.8F, 5.6F, 0, 0, -1, true),
    FMG9         (EnumGunType.SUB_MACHINE_GUN, EnumMagazine.MAG_FMG9,          0, 5.8F, 4.8F, 0, 0, -1, true),
    SMG11        (EnumGunType.SUB_MACHINE_GUN, EnumMagazine.MAG_SMG11,         0, 6.4F, 5.4F, 0, 0, -1, true),
    MP5K         (EnumGunType.SUB_MACHINE_GUN, EnumMagazine.MAG_MP5K,          0, 5.8F, 4.8F, 0, 0, -1, true),
    MP5          (EnumGunType.SUB_MACHINE_GUN, EnumMagazine.MAG_MP5,           0, 5.8F, 4.8F, 0, 0, -1, true),
    MP7          (EnumGunType.SUB_MACHINE_GUN, EnumMagazine.MAG_MP7,           0, 6.8F, 5.6F, 0, 0, -1, true),
    P90          (EnumGunType.SUB_MACHINE_GUN, EnumMagazine.MAG_P90,           0, 4.2F, 3.4F, 0, 0, -1, true),
    STEN_9MM_C1  (EnumGunType.SUB_MACHINE_GUN, EnumMagazine.MAG_STEN_9MM_C1,   0, 8.4F,   7F, 0, 0, -1, true),
    UMP45        (EnumGunType.SUB_MACHINE_GUN, EnumMagazine.MAG_UMP45,         0, 7.6F, 6.4F, 0, 0, -1, true),
    MPX          (EnumGunType.SUB_MACHINE_GUN, EnumMagazine.MAG_MPX,           0,   5F, 4.2F, 0, 0, -1, true),
    M12          (EnumGunType.SUB_MACHINE_GUN, EnumMagazine.MAG_M12,           0, 7.2F,   6F, 0, 0, -1, true),
    MP5SD        (EnumGunType.SUB_MACHINE_GUN, EnumMagazine.MAG_MP5SD,         0, 0.0F, 4.6F, 0, 0, -1, true),
    BEARING_9    (EnumGunType.SUB_MACHINE_GUN, EnumMagazine.MAG_BEARING_9,     0, 0.0F, 6.4F, 0, 0, -1, true),
    VECTOR_45    (EnumGunType.SUB_MACHINE_GUN, EnumMagazine.MAG_VECTOR_45,     0, 0.0F, 0.0F, 0, 0,  6, true),
    PDW9         (EnumGunType.SUB_MACHINE_GUN, EnumMagazine.MAG_PDW9,          0, 0.0F, 0.0F, 0, 0, -1, true),
    T5_SMG       (EnumGunType.SUB_MACHINE_GUN, EnumMagazine.MAG_T5_SMG,        0, 0.0F, 0.0F, 0, 0, -1, true),
    SKORPION_EVO (EnumGunType.SUB_MACHINE_GUN, EnumMagazine.MAG_SKORPION_EVO,  0, 0.0F, 0.0F, 0, 0, -1, true),

    //PISTOL
    LFP586   (                                                   15.4F,        0, 0,  6, 36),
    MK1_9MM  (EnumGunType.PISTOL, EnumMagazine.MAG_MK1_9MM,   0,  8.6F,  7.2F, 0, 0, -1, false),
    P226_MK25(EnumGunType.PISTOL, EnumMagazine.MAG_P226_MK25, 0, 10.6F,    9F, 0, 0, -1, false),
    M45_M    (EnumGunType.PISTOL, EnumMagazine.MAG_M45_M,     0,  9.4F,  7.8F, 0, 0, -1, false),
    FN_57_USG(EnumGunType.PISTOL, EnumMagazine.MAG_FN_57_USG, 0,  5.4F,  4.4F, 0, 0, -1, false),
    P12      (EnumGunType.PISTOL, EnumMagazine.MAG_P12,       0,  8.6F,  7.2F, 0, 0, -1, false),
    P9       (EnumGunType.PISTOL, EnumMagazine.MAG_P9,        0,    6F,    5F, 0, 0, -1, false),
    PMM      (EnumGunType.PISTOL, EnumMagazine.MAG_PMM,       0, 12.6F, 10.6F, 0, 0, -1, false),
    GSH_18   (EnumGunType.PISTOL, EnumMagazine.MAG_GSH_18,    0,  6.6F,  5.6F, 0, 0, -1, false),
    DEAGLE   (EnumGunType.PISTOL, EnumMagazine.MAG_DEAGLE,    0, 13.6F,  0.0F, 0, 0, -1, false),
    PRB92    (EnumGunType.PISTOL, EnumMagazine.MAG_PRB92,     0,  7.8F,  6.6F, 0, 0, -1, false),
    P229     (EnumGunType.PISTOL, EnumMagazine.MAG_P229,      0,   10F,  8.4F, 0, 0, -1, false),
    USP40    (EnumGunType.PISTOL, EnumMagazine.MAG_USP40,     0,  0.0F,  0.0F, 0, 0, -1, false),
    Q_929    (EnumGunType.PISTOL, EnumMagazine.MAG_Q_929,     0,  0.0F,  0.0F, 0, 0, -1, false),
    RG15     (EnumGunType.PISTOL, EnumMagazine.MAG_RG15,      0,  0.0F,  0.0F, 0, 0, -1, false),

    //SHIELD
    EXTENDABLE_SHIELD(),
    RUSSIAN_SHIELD(),
    FLASH_SHIELD(),
    RIOT_SHIELD();

    private EnumGunType type;
    private EnumMagazine magazine;
    private int ticksPerShot; //TODO: calculate ticks between two shots
    private float baseDamage;
    private float silencerDamage;
    private float baseSpread;
    private float baseRecoil;
    private int magCap = -1;
    private boolean automatic;
    private boolean hasMag = false;
    private int additionalMags; //TODO: find amount of additional mags on rainbow6.wikia.com
    private int reloadTime; //TODO: measure reload time and add to weapons

    //Main constructor
    EnumGun(EnumGunType type, EnumMagazine magazine, int ticksPerShot, float baseDamage, float silencerDamage, float baseSpread, float baseRecoil, int additionalMags, boolean automatic)
    {
        this.type = type;
        this.magazine = magazine;
        this.ticksPerShot = ticksPerShot;
        this.baseDamage = baseDamage;
        this.silencerDamage = silencerDamage;
        this.baseSpread = baseSpread;
        this.baseRecoil = baseRecoil;
        this.additionalMags = additionalMags;
        this.automatic = automatic;
        hasMag = true;
    }

    //Revolver constructor
    EnumGun(float baseDamage, float baseSpread, float baseRecoil, int magCap, int additionalAmmo)
    {
        this(EnumGunType.PISTOL, null, 1, baseDamage, 0, baseSpread, baseRecoil, additionalAmmo, false);
        hasMag = false;
        this.magCap = magCap;
    }

    //Shotgun constructor
    EnumGun(int ticksPerShot, float baseDamage, float silencerDamage, float baseSpread, float baseRecoil, int magCap, int additionalAmmo, boolean semiAuto)
    {
        this(EnumGunType.SHOTGUN, null, ticksPerShot, baseDamage, silencerDamage, baseSpread, baseRecoil, additionalAmmo, semiAuto);
        this.hasMag = false;
        this.magCap = magCap;
    }

    //Mag fed shotgun constructor
    EnumGun(EnumMagazine magazine, int ticksPerShot, float baseDamage, float silencerDamage, float baseSpread, float baseRecoil, int additionalMags, boolean semiAuto)
    {
        this(EnumGunType.SHOTGUN, magazine, ticksPerShot, baseDamage, silencerDamage, baseSpread, baseRecoil, additionalMags, semiAuto);
    }

    //Shield constructor
    EnumGun()
    {
        this(EnumGunType.SHIELD, null, 0, 0, 0, 0, 0, 0, false);
    }

    public EnumGunType getGunType()
    {
        return type;
    }

    public EnumMagazine getMagazine()
    {
        return hasMag ? magazine : null;
    }

    public ItemStack getGunItemStack()
    {
        if (ordinal() >= EXTENDABLE_SHIELD.ordinal())
        {
            return new ItemStack(Content.itemRiotShield, 1, this.ordinal() - EXTENDABLE_SHIELD.ordinal());
        }
        else
        {
            ItemStack stack = new ItemStack(Content.itemGun, 1, ordinal());
            IGunHandler handler = stack.getCapability(GunHandlerStorage.GUN_HANDLER_CAPABILITY, null);
            handler.setGun(this);
            handler.preLoad();
            return stack;
        }
    }

    public int getRoundsPerSecond()
    {
        return ticksPerShot;
    }

    public float getGunBaseDamage()
    {
        return baseDamage;
    }

    /** returns 0 if the gun can't have a silencer */
    public float getGunDamageSilenced()
    {
        return silencerDamage;
    }

    public float getActualDamage(List<EnumAttachment> attachments)
    {
        boolean silenced = attachments.contains(EnumAttachment.SUPPRESSOR) || this == MP5SD;
        return silenced ? getGunDamageSilenced() : getGunBaseDamage();
    }

    public float getBaseSpread()
    {
        return baseSpread;
    }

    public float getBaseRecoil()
    {
        return baseRecoil;
    }

    public float getSpreadModified(ArrayList<EnumAttachment> attachments)
    {
        //TODO: calculate spread for weapons with attachments and their reduce values
        return baseSpread;
    }

    public float getRecoilModified(ArrayList<EnumAttachment> attachments)
    {
        //TODO: calculate recoil for weapons with attachments and their reduce values
        return baseRecoil;
    }

    public int getMagCapacity()
    {
        return magazine != null ? magazine.getMagCap() : magCap;
    }

    public int getAdditionalMags()
    {
        return additionalMags;
    }

    /** If the gun is of EnumGunType shotgun, this returns if the gun is semi automatic or not */
    public boolean isAutomatic()
    {
        return automatic;
    }

    public boolean hasMag()
    {
        return hasMag;
    }

    public ItemStack getMagazineStack(boolean single)
    {
        if (this == LFP586)
        {
            return new ItemStack(Content.itemAmmo, single ? magCap : additionalMags, EnumBullet.ROUND_357MAGNUM.ordinal());
        }
        else if (type == EnumGunType.SHOTGUN && !hasMag)
        {
            return new ItemStack(Content.itemAmmo, single ? magCap : additionalMags, EnumBullet.ROUND_12GAUGE.ordinal());
        }
        else
        {
            ItemStack stack = new ItemStack(Content.itemMagazine, single ? 1 : additionalMags, magazine.ordinal());
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setInteger("currentAmmo", magazine.getMagCap());
            nbt.setInteger("maxAmmo", magazine.getMagCap());
            stack.setTagCompound(nbt);
            return stack;
        }
    }

    public ArrayList<EnumFiremode> getFiremodes()
    {
        if (type == EnumGunType.PISTOL || type == EnumGunType.SHOTGUN || type == EnumGunType.SNIPER_RIFLE)
        {
            return new ArrayList<>(Collections.singleton(EnumFiremode.SINGLE));
        }
        ArrayList<EnumFiremode> firemodes = new ArrayList<>();
        switch (this)
        {
            case C8_SFW   :
            {
                firemodes.add(EnumFiremode.SINGLE);
                firemodes.add(EnumFiremode.AUTO);
                break;
            }
            case L85A2    :
            {
                firemodes.add(EnumFiremode.SINGLE);
                firemodes.add(EnumFiremode.AUTO);
                break;
            }
            case AR33     :
            {
                firemodes.add(EnumFiremode.SINGLE);
                firemodes.add(EnumFiremode.DOUBLE);
                firemodes.add(EnumFiremode.AUTO);
                break;
            }
            case SIG_556xi:
            {
                firemodes.add(EnumFiremode.SINGLE);
                firemodes.add(EnumFiremode.AUTO);
                break;
            }
            case G36_C    :
            {
                firemodes.add(EnumFiremode.SINGLE);
                firemodes.add(EnumFiremode.AUTO);
                break;
            }
            case R4_C     :
            {
                firemodes.add(EnumFiremode.SINGLE);
                firemodes.add(EnumFiremode.AUTO);
                break;
            }
            case FAMAS_F2 :
            {
                firemodes.add(EnumFiremode.SINGLE);
                firemodes.add(EnumFiremode.TRIPLE);
                firemodes.add(EnumFiremode.AUTO);
                break;
            }
            case AK12     :
            {
                firemodes.add(EnumFiremode.SINGLE);
                firemodes.add(EnumFiremode.TRIPLE);
                firemodes.add(EnumFiremode.AUTO);
                break;
            }
            case HK_416_C :
            {
                firemodes.add(EnumFiremode.AUTO);
                firemodes.add(EnumFiremode.SINGLE);
                break;
            }
            case SIG_552_C:
            {
                firemodes.add(EnumFiremode.SINGLE);
                firemodes.add(EnumFiremode.TRIPLE);
                firemodes.add(EnumFiremode.AUTO);
                break;
            }
            case AUG_A2   :
            {
                firemodes.add(EnumFiremode.AUTO);
                break;
            }
            case Mk17_CQB :
            {
                firemodes.add(EnumFiremode.SINGLE);
                firemodes.add(EnumFiremode.AUTO);
                break;
            }
            case PARA_308 :
            {
                firemodes.add(EnumFiremode.SINGLE);
                firemodes.add(EnumFiremode.AUTO);
                break;
            }
            case TYPE_89:
            {
                firemodes.add(EnumFiremode.AUTO);
                firemodes.add(EnumFiremode.TRIPLE);
                firemodes.add(EnumFiremode.SINGLE);
                break;
            }

            case G8A1    :
            {
                firemodes.add(EnumFiremode.AUTO);
                break;
            }
            case PKP_6P41:
            {
                firemodes.add(EnumFiremode.AUTO);
                break;
            }
            case M249    :
            {
                firemodes.add(EnumFiremode.AUTO);
                break;
            }

            case K9X19VSN   :
            {
                firemodes.add(EnumFiremode.SINGLE);
                firemodes.add(EnumFiremode.AUTO);
                break;
            }
            case FMG9       :
            {
                firemodes.add(EnumFiremode.SINGLE);
                firemodes.add(EnumFiremode.AUTO);
                break;
            }
            case MP5K       :
            {
                firemodes.add(EnumFiremode.SINGLE);
                firemodes.add(EnumFiremode.TRIPLE);
                firemodes.add(EnumFiremode.AUTO);
                break;
            }
            case MP5        :
            {
                firemodes.add(EnumFiremode.SINGLE);
                firemodes.add(EnumFiremode.TRIPLE);
                firemodes.add(EnumFiremode.AUTO);
                break;
            }
            case MP7        :
            {
                firemodes.add(EnumFiremode.SINGLE);
                firemodes.add(EnumFiremode.AUTO);
                break;
            }
            case P90        :
            {
                firemodes.add(EnumFiremode.SINGLE);
                firemodes.add(EnumFiremode.AUTO);
                break;
            }
            case STEN_9MM_C1:
            {
                firemodes.add(EnumFiremode.AUTO);
                break;
            }
            case UMP45      :
            {
                firemodes.add(EnumFiremode.SINGLE);
                firemodes.add(EnumFiremode.TRIPLE);
                firemodes.add(EnumFiremode.AUTO);
                break;
            }
            case MPX        :
            {
                firemodes.add(EnumFiremode.SINGLE);
                firemodes.add(EnumFiremode.AUTO);
                break;
            }
            case M12        :
            {
                firemodes.add(EnumFiremode.SINGLE);
                firemodes.add(EnumFiremode.AUTO);
                break;
            }
            case MP5SD:
            {

                break;
            }
            case SMG11:
            {
                firemodes.add(EnumFiremode.AUTO);
                break;
            }
            case BEARING_9:
            {
                firemodes.add(EnumFiremode.AUTO);
                firemodes.add(EnumFiremode.SINGLE);
                break;
            }
        }
        return firemodes;
    }

    public static EnumGun valueOf(ItemStack stack)
    {
        if (stack == null || stack.getMetadata() > values().length) { return null; }
        if (stack.getItem() instanceof ItemRiotShield)
        {
            return values()[EXTENDABLE_SHIELD.ordinal() + stack.getMetadata()];
        }
        else
        {
            return values()[stack.getMetadata()];
        }
    }

    public static EnumGun valueOf(int index)
    {
        return values()[index];
    }

    public ArrayList<EnumAttachment> getAttachements()
    {
        ArrayList<EnumAttachment> attach = new ArrayList<EnumAttachment>();
        switch (this)
        {
            //ASSAULT_RIFLE
            case C8_SFW:
            {
                attach.add(EnumAttachment.ACOG_SIGHT);
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.VERTICAL_GRIP);
                attach.add(EnumAttachment.FLASH_HIDER);
                attach.add(EnumAttachment.MUZZLE_BREAK);
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.COMPENSATOR);
                attach.add(EnumAttachment.HEAVY_BARREL);
                attach.add(EnumAttachment.LASER);
                attach.add(EnumAttachment.SHOTGUN);
                break;
            }
            case L85A2:
            {
                attach.add(EnumAttachment.ACOG_SIGHT);
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.VERTICAL_GRIP);
                attach.add(EnumAttachment.FLASH_HIDER);
                attach.add(EnumAttachment.MUZZLE_BREAK);
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.COMPENSATOR);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case AR33:
            {
                attach.add(EnumAttachment.ACOG_SIGHT);
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.VERTICAL_GRIP);
                attach.add(EnumAttachment.ANGLED_GRIP);
                attach.add(EnumAttachment.FLASH_HIDER);
                attach.add(EnumAttachment.MUZZLE_BREAK);
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.COMPENSATOR);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case SIG_556xi:
            {
                attach.add(EnumAttachment.ACOG_SIGHT);
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.VERTICAL_GRIP);
                attach.add(EnumAttachment.ANGLED_GRIP);
                attach.add(EnumAttachment.FLASH_HIDER);
                attach.add(EnumAttachment.MUZZLE_BREAK);
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.COMPENSATOR);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case G36_C:
            {
                attach.add(EnumAttachment.ACOG_SIGHT);
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.VERTICAL_GRIP);
                attach.add(EnumAttachment.ANGLED_GRIP);
                attach.add(EnumAttachment.FLASH_HIDER);
                attach.add(EnumAttachment.MUZZLE_BREAK);
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.COMPENSATOR);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case R4_C:
            {
                attach.add(EnumAttachment.ACOG_SIGHT);
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.VERTICAL_GRIP);
                attach.add(EnumAttachment.MUZZLE_BREAK);
                attach.add(EnumAttachment.FLASH_HIDER);
                attach.add(EnumAttachment.HEAVY_BARREL);
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.COMPENSATOR);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case FAMAS_F2:
            {
                attach.add(EnumAttachment.ACOG_SIGHT);
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.VERTICAL_GRIP);
                attach.add(EnumAttachment.FLASH_HIDER);
                attach.add(EnumAttachment.MUZZLE_BREAK);
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.COMPENSATOR);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case AK12:
            {
                attach.add(EnumAttachment.ACOG_SIGHT);
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.FLASH_HIDER);
                attach.add(EnumAttachment.COMPENSATOR);
                attach.add(EnumAttachment.MUZZLE_BREAK);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case HK_416_C:
            {
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.VERTICAL_GRIP);
                attach.add(EnumAttachment.FLASH_HIDER);
                attach.add(EnumAttachment.MUZZLE_BREAK);
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.COMPENSATOR);
                attach.add(EnumAttachment.HEAVY_BARREL);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case SIG_552_C:
            {
                attach.add(EnumAttachment.ACOG_SIGHT);
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.VERTICAL_GRIP);
                attach.add(EnumAttachment.ANGLED_GRIP);
                attach.add(EnumAttachment.FLASH_HIDER);
                attach.add(EnumAttachment.MUZZLE_BREAK);
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.COMPENSATOR);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case AUG_A2:
            {
                attach.add(EnumAttachment.ACOG_SIGHT);
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.FLASH_HIDER);
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.COMPENSATOR);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case Mk17_CQB:
            {
                attach.add(EnumAttachment.ACOG_SIGHT);
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.VERTICAL_GRIP);
                attach.add(EnumAttachment.ANGLED_GRIP);
                attach.add(EnumAttachment.FLASH_HIDER);
                attach.add(EnumAttachment.MUZZLE_BREAK);
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.COMPENSATOR);
                attach.add(EnumAttachment.LASER);
                attach.add(EnumAttachment.RIFLE_SHIELD);
                break;
            }
            case PARA_308:
            {
                attach.add(EnumAttachment.ACOG_SIGHT);
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.VERTICAL_GRIP);
                attach.add(EnumAttachment.ANGLED_GRIP);
                attach.add(EnumAttachment.FLASH_HIDER);
                attach.add(EnumAttachment.MUZZLE_BREAK);
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.COMPENSATOR);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case TYPE_89:
            {
                attach.add(EnumAttachment.ACOG_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.VERTICAL_GRIP);
                attach.add(EnumAttachment.ANGLED_GRIP);
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.FLASH_HIDER);
                attach.add(EnumAttachment.COMPENSATOR);
                attach.add(EnumAttachment.MUZZLE_BREAK);
                attach.add(EnumAttachment.LASER);
                break;
            }

            //SNIPER_RIFLE
            case HK417:
            {
                attach.add(EnumAttachment.ACOG_SIGHT);
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.VERTICAL_GRIP);
                attach.add(EnumAttachment.FLASH_HIDER);
                attach.add(EnumAttachment.MUZZLE_BREAK);
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.COMPENSATOR);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case OTS_03:
            {
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.FLASH_HIDER);
                attach.add(EnumAttachment.MUZZLE_BREAK);
                attach.add(EnumAttachment.FLIP_SIGHT);
                break;
            }
            case CAMRS:
            {
                attach.add(EnumAttachment.ACOG_SIGHT);
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.VERTICAL_GRIP);
                attach.add(EnumAttachment.ANGLED_GRIP);
                attach.add(EnumAttachment.FLASH_HIDER);
                attach.add(EnumAttachment.MUZZLE_BREAK);
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.COMPENSATOR);
                attach.add(EnumAttachment.LASER);
                attach.add(EnumAttachment.SHOTGUN);
                break;
            }
            case SR25:
            {
                attach.add(EnumAttachment.ACOG_SIGHT);
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.VERTICAL_GRIP);
                attach.add(EnumAttachment.FLASH_HIDER);
                attach.add(EnumAttachment.MUZZLE_BREAK);
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.COMPENSATOR);
                attach.add(EnumAttachment.LASER);
                attach.add(EnumAttachment.RIFLE_SHIELD);
                break;
            }

            //MACHINE_GUN
            case G8A1:
            {
                attach.add(EnumAttachment.ACOG_SIGHT);
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.VERTICAL_GRIP);
                attach.add(EnumAttachment.FLASH_HIDER);
                attach.add(EnumAttachment.SUPPRESSOR);
                break;
            }
            case PKP_6P41:
            {
                attach.add(EnumAttachment.ACOG_SIGHT);
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.FLASH_HIDER);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case M249:
            {
                attach.add(EnumAttachment.ACOG_SIGHT);
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.VERTICAL_GRIP);
                attach.add(EnumAttachment.FLASH_HIDER);
                attach.add(EnumAttachment.COMPENSATOR);
                break;
            }

            //SHOTGUN
            case M590A1:
            {
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case M1014:
            {
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case M870:
            {
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case SG_CQB:
            {
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.VERTICAL_GRIP);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case SASG_12:
            {
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.VERTICAL_GRIP);
                attach.add(EnumAttachment.ANGLED_GRIP);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case SUPER_90:
            {
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case SPAS_12:
            {
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case SPAS_15:
            {
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case SUPERNOVA:
            {
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.LASER);
                break;
            }

            //SUB_MACHINE_GUN
            case K9X19VSN:
            {
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.COMPENSATOR);
                attach.add(EnumAttachment.FLASH_HIDER);
                attach.add(EnumAttachment.MUZZLE_BREAK);
                attach.add(EnumAttachment.VERTICAL_GRIP);
                attach.add(EnumAttachment.ANGLED_GRIP);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case FMG9:
            {
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.FLASH_HIDER);
                attach.add(EnumAttachment.MUZZLE_BREAK);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case MP5K:
            {
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.COMPENSATOR);
                attach.add(EnumAttachment.FLASH_HIDER);
                attach.add(EnumAttachment.MUZZLE_BREAK);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case MP5:
            {
                attach.add(EnumAttachment.ACOG_SIGHT);
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.FLASH_HIDER);
                attach.add(EnumAttachment.MUZZLE_BREAK);
                attach.add(EnumAttachment.VERTICAL_GRIP);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case MP7:
            {
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.COMPENSATOR);
                attach.add(EnumAttachment.FLASH_HIDER);
                attach.add(EnumAttachment.MUZZLE_BREAK);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case P90:
            {
                attach.add(EnumAttachment.ACOG_SIGHT);
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.FLASH_HIDER);
                attach.add(EnumAttachment.MUZZLE_BREAK);
                attach.add(EnumAttachment.HEAVY_BARREL);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case STEN_9MM_C1:
            {
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.HEAVY_BARREL);
                attach.add(EnumAttachment.ANGLED_GRIP);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case UMP45:
            {
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.COMPENSATOR);
                attach.add(EnumAttachment.FLASH_HIDER);
                attach.add(EnumAttachment.MUZZLE_BREAK);
                attach.add(EnumAttachment.HEAVY_BARREL);
                attach.add(EnumAttachment.VERTICAL_GRIP);
                attach.add(EnumAttachment.ANGLED_GRIP);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case MPX:
            {
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.COMPENSATOR);
                attach.add(EnumAttachment.FLASH_HIDER);
                attach.add(EnumAttachment.MUZZLE_BREAK);
                attach.add(EnumAttachment.VERTICAL_GRIP);
                attach.add(EnumAttachment.ANGLED_GRIP);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case M12:
            {
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.FLASH_HIDER);
                attach.add(EnumAttachment.MUZZLE_BREAK);
                attach.add(EnumAttachment.HEAVY_BARREL);
                attach.add(EnumAttachment.LASER);
                break;
            }

            case SMG11:
            {
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.VERTICAL_GRIP);
                attach.add(EnumAttachment.FLASH_HIDER);
                attach.add(EnumAttachment.HEAVY_BARREL);
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.COMPENSATOR);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case MP5SD:
            {
                attach.add(EnumAttachment.ACOG_SIGHT);
                attach.add(EnumAttachment.HOLO_SIGHT);
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.VERTICAL_GRIP);
                attach.add(EnumAttachment.ANGLED_GRIP);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case BEARING_9:
            {
                attach.add(EnumAttachment.RED_DOT_SIGHT);
                attach.add(EnumAttachment.REFLEX_SIGHT);
                attach.add(EnumAttachment.COMPENSATOR);
                attach.add(EnumAttachment.FLASH_HIDER);
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.LASER);
            }

            //PISTOL
            case LFP586:
            {
                attach.add(EnumAttachment.LASER);
                break;
            }
            case MK1_9MM:
            {
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.MUZZLE_BREAK);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case P226_MK25:
            {
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.MUZZLE_BREAK);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case M45_M:
            {
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.MUZZLE_BREAK);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case FN_57_USG:
            {
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.MUZZLE_BREAK);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case P12:
            {
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.MUZZLE_BREAK);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case P9:
            {
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.MUZZLE_BREAK);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case PMM:
            {
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.MUZZLE_BREAK);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case GSH_18:
            {
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.MUZZLE_BREAK);
                attach.add(EnumAttachment.COMPENSATOR);
                break;
            }
            case DEAGLE:
            {
                attach.add(EnumAttachment.LASER);
                break;
            }
            case PRB92:
            {
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.MUZZLE_BREAK);
                attach.add(EnumAttachment.LASER);
                break;
            }
            case P229:
            {
                attach.add(EnumAttachment.SUPPRESSOR);
                attach.add(EnumAttachment.MUZZLE_BREAK);
                attach.add(EnumAttachment.LASER);
                break;
            }
        }
        return attach;
    }

    public RecipeGunCrafting getRecipe()
    {
        return Crafting.getGunRecipe(this);
    }

    public static String[] getGunsAsStringArray()
    {
        String[] strings = new String[values().length - 3];
        for (EnumGun gun : EnumGun.values())
        {
            if (gun.type != EnumGunType.SHIELD)
            {
                strings[gun.ordinal()] = gun.toString().toLowerCase(Locale.ENGLISH);
            }
        }
        return strings;
    }

    public static String[] getShieldsAsStringArray()
    {
        return new String[]{EXTENDABLE_SHIELD.toString().toLowerCase(Locale.ENGLISH),
                            RUSSIAN_SHIELD.toString().toLowerCase(Locale.ENGLISH),
                            FLASH_SHIELD.toString().toLowerCase(Locale.ENGLISH)};
    }

    public int getMaxPenetrationCount()
    {
        if (this == OTS_03) { return 3; }
        else if (type == EnumGunType.SHOTGUN || type == EnumGunType.PISTOL) { return 1; }
        else if (type == EnumGunType.ASSAULT_RIFLE || type == EnumGunType.MACHINE_GUN || type == EnumGunType.SUB_MACHINE_GUN || type == EnumGunType.SNIPER_RIFLE)
        {
            return 2;
        }
        else return 0;
    }

    public int getTicksBetweenRounds()
    {
        if (ticksPerShot != 0) { return ticksPerShot; }
        if (type == EnumGunType.PISTOL)
        {
            return this == DEAGLE || this == LFP586 ? 15 : 10;
        }
        else if (type == EnumGunType.SNIPER_RIFLE)
        {
            return this == OTS_03 ? 20 : 10;
        }
        else if (type == EnumGunType.SHOTGUN)
        {
            return automatic ? 10 : 20;
        }
        return 0;
    }

    public int getReloadTime()
    {
        return reloadTime;
    }

    public String getDisplayName()
    {
        return I18n.format("item.rssmc:itemGun_" + toString().toLowerCase(Locale.ENGLISH) + ".name");
    }

    public enum EnumGunType
    {
        ASSAULT_RIFLE,
        SNIPER_RIFLE,
        MACHINE_GUN,
        SHOTGUN,
        SUB_MACHINE_GUN,
        PISTOL,
        SHIELD
    }
}