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
import XFactHD.rssmc.common.utils.propertyEnums.EnumMaterial;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityFloorPanel extends TileEntityBase
{
    private EnumMaterial material = EnumMaterial.OAK;
    private boolean destroyed = false;

    public void setMaterial(EnumMaterial material)
    {
        this.material = material;
        notifyBlockUpdate();
    }

    public EnumMaterial getMaterial()
    {
        return material;
    }

    public void setDestroyed(boolean destroyed)
    {
        this.destroyed = destroyed;
        notifyBlockUpdate();
    }

    public boolean isDestroyed()
    {
        return destroyed;
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        material = EnumMaterial.valueOf(nbt.getString("camo"));
        destroyed = nbt.getBoolean("destroyed");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        nbt.setString("camo", material.toString());
        nbt.setBoolean("destroyed", destroyed);
    }
}