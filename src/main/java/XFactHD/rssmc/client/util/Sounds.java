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

package XFactHD.rssmc.client.util;

import XFactHD.rssmc.common.data.EnumGadget;
import XFactHD.rssmc.common.data.EnumGun;
import XFactHD.rssmc.common.utils.Reference;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.HashMap;
import java.util.Locale;

public class Sounds
{
    private static HashMap<EnumGadget, HashMap<String, SoundEvent>> gadgetSounds = new HashMap<>();
    private static HashMap<EnumGun, SoundEvent> gunFire = new HashMap<>();
    private static HashMap<EnumGun, SoundEvent> gunCharge = new HashMap<>();
    private static SoundEvent soundGunEmptyClickMG;
    private static SoundEvent soundGunEmptyClickSMG;
    private static SoundEvent soundGunEmptyClickPistol;
    public static SoundEvent soundBulletHitGround;
    public static SoundEvent soundDestroyElectricDevice;

    public static void register()
    {
        soundDestroyElectricDevice = registerSound("destroyElectricDevice");
        soundGunEmptyClickMG = registerSound("emptyGunClickMG");
        soundGunEmptyClickSMG = registerSound("emptyGunClickSMG");
        soundGunEmptyClickPistol = registerSound("emptyGunClickPistol");
        soundBulletHitGround = registerSound("bulletHitGround");
        for (EnumGun gun : EnumGun.values())
        {
            if (gun.ordinal() < EnumGun.EXTENDABLE_SHIELD.ordinal())
            {
                gunFire.put(gun, registerSound(gun.toString().toLowerCase(Locale.ENGLISH) + "_fire"));
                gunCharge.put(gun, registerSound(gun.toString().toLowerCase(Locale.ENGLISH) + "_charge"));
            }
        }
        for (EnumGadget gadget : EnumGadget.values())
        {
            if (gadget.getSounds() != null)
            {
                HashMap<String, SoundEvent> sounds = new HashMap<>();
                for (String name : gadget.getSounds())
                {
                    if (!(gadget.toString().toLowerCase(Locale.ENGLISH).contains("grenade") && name.equals("pull_pin")))
                    {
                        sounds.put(name, registerSound(gadget.toString().toLowerCase(Locale.ENGLISH) + "_" + name));
                    }
                }
                if (!sounds.isEmpty())
                {
                    gadgetSounds.put(gadget, sounds);
                }
            }
        }
        //INFO: Sounds for gadgets that do not have an EnumGadget entry
        HashMap<String, SoundEvent> nullGadgetSounds = new HashMap<>();
        nullGadgetSounds.put("flip_sight", registerSound("flip_sight"));
        nullGadgetSounds.put("scan_footprint", registerSound("scan_foot_print"));
        nullGadgetSounds.put("grenade_pull_pin", registerSound("grenade_pull_pin"));
        gadgetSounds.put(null, nullGadgetSounds);
    }

    private static SoundEvent registerSound(String name)
    {
        SoundEvent event = new SoundEvent(new ResourceLocation(Reference.MOD_ID, name));
        event.setRegistryName(event.getSoundName());
        GameRegistry.register(event);
        return event;
    }

    public static SoundEvent getGunFireSound(EnumGun gun)
    {
        return gunFire.get(gun);
    }

    public static SoundEvent getGunChargeSound(EnumGun gun)
    {
        return gunCharge.get(gun);
    }

    public static SoundEvent getGunEmptyClickSound(EnumGun gun)
    {
        switch (gun.getGunType())
        {
            case ASSAULT_RIFLE:
            case SUB_MACHINE_GUN:
            case SNIPER_RIFLE:
            case SHOTGUN: return soundGunEmptyClickSMG;

            case PISTOL: return soundGunEmptyClickPistol;

            case MACHINE_GUN: return soundGunEmptyClickMG;

            default: return null;
        }
    }

    public static SoundEvent getGadgetSound(EnumGadget gadget, String name)
    {
        switch (name)
        {
            case "grenade_pull_pin": return SoundEvents.BLOCK_TRIPWIRE_CLICK_ON; //TODO: remove when there are proper sounds
            case "switch_scanner": return gadgetSounds.get(EnumGadget.STICKY_CAMERA).get("deploy"); //TODO: remove when there are proper sounds
            default: return gadgetSounds.get(gadget).get(name);
        }
    }
}