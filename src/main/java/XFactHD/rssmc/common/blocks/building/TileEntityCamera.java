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

package XFactHD.rssmc.common.blocks.building;

import XFactHD.rssmc.common.blocks.TileEntityBase;
import XFactHD.rssmc.common.entity.camera.EntityCamera;
import XFactHD.rssmc.common.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;

import java.util.UUID;

public class TileEntityCamera extends TileEntityBase implements ITickable
{
    private boolean active = false;
    private String camUUID = "";
    private EntityCamera camera = null;

    public void setActive(boolean active)
    {
        this.active = active;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setEntity(EntityCamera camera)
    {
        this.camera = camera;
        camUUID = camera.getUniqueID().toString();
        notifyBlockUpdate();
    }

    public EntityCamera getCamera()
    {
        if (camera == null)
        {
            camera = getEntity(camUUID);
        }
        return camera;
    }

    @Override
    public void update()
    {
        if (camera == null && !camUUID.equals(""))
        {
            camera = getEntity(camUUID);
        }
    }

    public void killEntity()
    {
        if (camera != null)
        {
            camera.setDead();
            camera = null;
        }
    }

    private EntityCamera getEntity(String uuid)
    {
        Entity entity = Utils.getEntityByUUID(UUID.fromString(uuid));
        return entity instanceof EntityCamera ? (EntityCamera)entity : null;
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        nbt.setBoolean("active", active);
        nbt.setString("camera", camera != null ? camera.getPersistentID().toString() : camUUID);
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        active = nbt.getBoolean("active");
        camUUID = nbt.getString("camera");
    }
}