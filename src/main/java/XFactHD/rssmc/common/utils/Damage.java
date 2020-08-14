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

package XFactHD.rssmc.common.utils;

import XFactHD.rssmc.common.data.EnumGadget;
import XFactHD.rssmc.common.data.EnumGun;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;

public class Damage
{
    public static DamageSourceNitroCell causeNitroDamage(EntityPlayer killer)
    {
        return new DamageSourceNitroCell(killer);
    }

    public static DamageSourceClaymore causeClaymoreDamage(EntityPlayer killer)
    {
        return new DamageSourceClaymore(killer);
    }

    public static DamageSourceImpactGrenade causeImpactGrenadeDamage(EntityPlayer killer)
    {
        return new DamageSourceImpactGrenade(killer);
    }

    public static DamageSourceFragGrenade causeFragGrenadeDamage(EntityPlayer killer)
    {
        return new DamageSourceFragGrenade(killer);
    }

    public static DamageSourceBullet causeBulletDamage(EntityPlayer killer, EnumGun gun)
    {
        return new DamageSourceBullet(killer, gun);
    }

    public static DamageSourceHitByGadget causeHitByGadgetDamage(EntityPlayer killer, EnumGadget gadget)
    {
        return new DamageSourceHitByGadget(killer, gadget);
    }

    public static DamageSourceKapkanTrap causeKapkanTrapDamage(EntityPlayer killer)
    {
        return new DamageSourceKapkanTrap(killer);
    }

    public static DamageSourceShockWire causeShockWireDamage(EntityPlayer killer)
    {
        return new DamageSourceShockWire(killer);
    }

    public static DamageSourceBreachCharge causeBreachChargeDamage(EntityPlayer killer)
    {
        return new DamageSourceBreachCharge(killer);
    }

    public static DamageSourceThermiteCharge causeThermiteChargeDamage(EntityPlayer killer)
    {
        return new DamageSourceThermiteCharge(killer);
    }

    public static DamageSource causeBomberDamage(EntityPlayer killer)
    {
        return new DamageSourceBomber(killer);
    }

    public static class DamageSourceNitroCell extends CustomDamageSource
    {
        public DamageSourceNitroCell(EntityPlayer killer)
        {
            super("c4", killer);
            setDamageIsAbsolute();
            setExplosion();
            setDamageBypassesArmor();
        }
    }

    public static class DamageSourceClaymore extends CustomDamageSource
    {
        public DamageSourceClaymore(EntityPlayer killer)
        {
            super("claymore", killer);
            setExplosion();
            setDamageBypassesArmor();
            setDamageIsAbsolute();
        }
    }

    public static class DamageSourceImpactGrenade extends CustomDamageSource
    {
        public DamageSourceImpactGrenade(EntityPlayer killer)
        {
            super("impactGrenade", killer);
            setExplosion();
            setDamageBypassesArmor();
            setDamageIsAbsolute();
        }
    }

    public static class DamageSourceFragGrenade extends CustomDamageSource
    {
        public DamageSourceFragGrenade(EntityPlayer killer)
        {
            super("fragGrenade", killer);
            setExplosion();
            setDamageBypassesArmor();
            setDamageIsAbsolute();
        }
    }

    public static class DamageSourceBullet extends CustomDamageSource
    {
        public DamageSourceBullet(EntityPlayer killer, EnumGun gun)
        {
            super("bullet", killer);
            setExplosion();
            setDamageBypassesArmor();
            setDamageIsAbsolute();
        }
    }

    public static class DamageSourceHitByGadget extends CustomDamageSource
    {
        public DamageSourceHitByGadget(EntityPlayer killer, EnumGadget gadget)
        {
            super("hitByGadget", killer);
            setExplosion();
            setDamageBypassesArmor();
            setDamageIsAbsolute();
        }
    }

    public static class DamageSourceKapkanTrap extends CustomDamageSource
    {
        private EntityPlayer killer;

        public DamageSourceKapkanTrap(EntityPlayer killer)
        {
            super("kapkanTrap", killer);
        }
    }

    public static class DamageSourceShockWire extends CustomDamageSource
    {
        public DamageSourceShockWire(EntityPlayer killer)
        {
            super("shockWire", killer);
        }
    }

    public static class DamageSourceBreachCharge extends CustomDamageSource
    {
        public DamageSourceBreachCharge(EntityPlayer killer)
        {
            super("breach", killer);
            setDamageIsAbsolute();
            setExplosion();
            setDamageBypassesArmor();
        }
    }

    public static class DamageSourceThermiteCharge extends CustomDamageSource
    {
        public DamageSourceThermiteCharge(EntityPlayer killer)
        {
            super("thermite", killer);
            setDamageIsAbsolute();
            setExplosion();
            setDamageBypassesArmor();
        }
    }

    public static class DamageSourceBomber extends CustomDamageSource
    {
        public DamageSourceBomber(EntityPlayer killer)
        {
            super("bomber", killer);
            setDamageIsAbsolute();
            setExplosion();
            setDamageBypassesArmor();
        }
    }

    public static abstract class CustomDamageSource extends DamageSource
    {
        private EntityPlayer killer;

        private CustomDamageSource(String damageType, EntityPlayer killer)
        {
            super(damageType);
            this.killer = killer;
        }

        @Override
        public Entity getEntity()
        {
            return killer;
        }
    }
}