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

package XFactHD.rssmc.common.utils.helper;

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.common.data.EnumParticle;
import XFactHD.rssmc.common.data.team.StatusController;
import XFactHD.rssmc.common.entity.gadget.EntityImpactGrenade;
import XFactHD.rssmc.common.entity.gadget.EntityNitroCell;
import XFactHD.rssmc.common.utils.Damage;
import XFactHD.rssmc.common.utils.utilClasses.Position;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

public class ExplosionHelper
{
    @SuppressWarnings("ConstantConditions")
    public static void causeExplosionAtEntity(World world, EntityThrowable throwable, float size, float maxDmg)
    {
        AxisAlignedBB aabb = new AxisAlignedBB(throwable.posX - size, throwable.posY - size, throwable.posZ - size, throwable.posX + size, throwable.posY + size, throwable.posZ + size);
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(throwable, aabb);
        for (Entity entity : list)
        {
            if (!(entity instanceof EntityLivingBase)) { continue; }
            EntityLivingBase victim = (EntityLivingBase)entity;
            DamageSource source = getDamageSourceFromThrowable(throwable);
            float dist = victim.getDistanceToEntity(throwable);
            float dmg = maxDmg * (1 - ((dist > size ? size : dist) / size));
            if (StatusController.isShieldHolder(victim))
            {
                dmg = StatusController.getDmgModified(victim, dmg, new Position(throwable.posX, throwable.posY, throwable.posZ));
            }
            if (!victim.isEntityInvulnerable(source) && StatusController.canSeeEntity(throwable, victim))
            {
                victim.attackEntityFrom(source, dmg);
            }
        }
        RainbowSixSiegeMC.proxy.spawnParticle(EnumParticle.BIG_EXPLOSION, world.provider.getDimension(), throwable.posX, throwable.posY, throwable.posZ);
    }

    private static DamageSource getDamageSourceFromThrowable(EntityThrowable throwable)
    {
        EntityPlayer thrower = (EntityPlayer) throwable.getThrower();
        if (throwable instanceof EntityNitroCell)
        {
            return Damage.causeNitroDamage(thrower);
        }
        else if (throwable instanceof EntityImpactGrenade)
        {
            return Damage.causeImpactGrenadeDamage(thrower);
        }
        //else if (throwable instanceof EntityFragGrenade)
        //{
        //    return Damage.causeFragGrenadeDamage(thrower);
        //}
        return null;
    }
}