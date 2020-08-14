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

package XFactHD.rssmc.common.entity.drone;

import XFactHD.rssmc.common.entity.camera.EntityBlackEyeCam;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityBlackEye extends AbstractEntityDrone
{
    private boolean sticked = false;

    public EntityBlackEye(World world)
    {
        super(world);
    }

    public EntityBlackEye(World world, EntityPlayer player)
    {
        super(world, player);
    }

    @Override
    protected void onImpact(RayTraceResult result)
    {
        if (result.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            sticked = true;
            world.spawnEntity(new EntityBlackEyeCam(world, owner.getUniqueID()));
        }
    }

    @Override
    public void onUpdate()
    {
        if (sticked)
        {
            super.onEntityUpdate();
        }
        else
        {
            super.onUpdate();
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("sticked", sticked);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        sticked = nbt.getBoolean("sticked");
    }
}