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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.UUID;

public abstract class AbstractEntityDrone extends EntityThrowable
{
    protected UUID ownerUUID;
    protected EntityPlayer owner;

    public AbstractEntityDrone(World world)
    {
        super(world);
    }

    public AbstractEntityDrone(World world, EntityPlayer player)
    {
        super(world);
        this.owner = player;
        this.ownerUUID = player.getUniqueID();
    }

    public EntityPlayer getOwner()
    {
        if (owner == null)
        {
            owner = world.getPlayerEntityByUUID(ownerUUID);
        }
        return owner;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        nbt.setUniqueId("owner", ownerUUID);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        ownerUUID = nbt.getUniqueId("owner");
    }
}