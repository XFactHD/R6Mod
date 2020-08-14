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

import XFactHD.rssmc.common.Content;
import XFactHD.rssmc.common.blocks.TileEntityBase;
import XFactHD.rssmc.common.capability.energyStorage.EnergyStorageSerializable;
import XFactHD.rssmc.common.data.EnumMagazine;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;

public class TileEntityMagFiller extends TileEntityBase implements ITickable
{
    private EnergyStorageSerializable energyStorage = new EnergyStorageSerializable(10000, 500, 0);
    //TODO: add item handlers (one for input, one for process, one for output, one for bullets)
    private boolean active = false;
    private float tankFillState = 0.3F;

    @Override
    public void update()
    {

    }

    public float getTankFillState()
    {
        return tankFillState;
    }

    public ItemStack getInputStack()
    {
        return new ItemStack(Content.itemMagazine, 5, EnumMagazine.MAG_Mk17_CQB.ordinal());
    }

    public ItemStack getProcessStack()
    {
        return new ItemStack(Content.itemMagazine, 1, EnumMagazine.MAG_Mk17_CQB.ordinal());
    }

    public ItemStack getOutputStack()
    {
        return new ItemStack(Content.itemMagazine, 3, EnumMagazine.MAG_Mk17_CQB.ordinal());
    }

    public int getEnergyStored()
    {
        return energyStorage.getEnergyStored();
    }

    public boolean isActive()
    {
        return active;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing side)
    {
        return super.hasCapability(capability, side);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing side)
    {
        return super.getCapability(capability, side);
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        energyStorage.deserializeNBT(nbt.getCompoundTag("energy"));
        active = nbt.getBoolean("active");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        nbt.setTag("energy", energyStorage.serializeNBT());
        nbt.setBoolean("active", active);
    }
}