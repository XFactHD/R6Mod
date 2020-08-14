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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntitySmokeGrenade extends AbstractEntityGrenade
{
    public EntitySmokeGrenade(World world)
    {
        super(world);
    }

    public EntitySmokeGrenade(World world, EntityPlayer thrower)
    {
        super(world, thrower);
    }

    @Override
    protected int getTime() { return 20; }

    @Override
    public void boom()
    {
        //TODO: make smoke effect
        world.playSound(posX, posY, posZ, SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.PLAYERS, 1, 1, false);
        setDead();
    }
}