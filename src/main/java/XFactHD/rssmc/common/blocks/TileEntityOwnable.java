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

package XFactHD.rssmc.common.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.lang.ref.WeakReference;
import java.util.UUID;

public class TileEntityOwnable extends TileEntityBase
{
    private UUID ownerUUID = null;
    private WeakReference<EntityPlayer> owner = null;

    public void setOwner(EntityPlayer owner)
    {
        this.owner = new WeakReference<>(owner);
        ownerUUID = owner.getPersistentID();
    }

    public EntityPlayer getOwner()
    {
        if ((owner == null || owner.get() == null) && ownerUUID != null)
        {
            owner = new WeakReference<>(world.getPlayerEntityByUUID(ownerUUID));
        }
        return owner != null ? owner.get() : null;
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        nbt.setString("owner", ownerUUID != null ? ownerUUID.toString() : "");
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        ownerUUID = nbt.getString("owner").equals("") ? null : UUID.fromString(nbt.getString("owner"));
    }
}