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

package XFactHD.rssmc.common.entity.gadget;

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.common.data.EnumParticle;
import XFactHD.rssmc.common.data.team.StatusController;
import XFactHD.rssmc.common.utils.Damage;
import XFactHD.rssmc.common.utils.utilClasses.Position;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityFragGrenade extends AbstractEntityGrenade
{
    public EntityFragGrenade(World world)
    {
        super(world);
    }

    public EntityFragGrenade(World world, EntityPlayer thrower, int timer)
    {
        super(world, thrower, timer);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        if (!world.isRemote && timer > 0)
        {
            timer -= 1;
            if (timer <= 0)
            {
                boom();
            }
        }
    }

    @Override
    public void boom()
    {
        Position pos = new Position(posX, posY, posZ);
        for (EntityLivingBase entity : world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(posX - 3, posY - 3, posZ - 3, posX + 3, posY + 3, posZ + 3)))
        {
            if (StatusController.canSeeEntity(this, entity))
            {
                float amount = 28F * (float)(pos.distanceTo(new Position(entity.posX, entity.posY, entity.posZ)) / 3);
                entity.attackEntityFrom(Damage.causeFragGrenadeDamage((EntityPlayer) getThrower()), amount);
            }
        }
        RainbowSixSiegeMC.proxy.spawnParticle(EnumParticle.BIG_EXPLOSION, world.provider.getDimension(), posX, posY, posZ);
        world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1, 1);
        setDead();
    }
}