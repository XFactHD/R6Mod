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

package XFactHD.rssmc.common.blocks.survival;

import XFactHD.rssmc.common.blocks.TileEntityBase;
import XFactHD.rssmc.common.capability.itemHandler.ItemHandlerGunCraftingTable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityGunCraftingTable extends TileEntityBase
{
    private ItemHandlerGunCraftingTable itemHandler = new ItemHandlerGunCraftingTable();

    public ItemHandlerGunCraftingTable getItemHandler()
    {
        return itemHandler;
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {

    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {

    }
}