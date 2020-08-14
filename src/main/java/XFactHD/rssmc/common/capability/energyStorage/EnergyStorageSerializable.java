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

package XFactHD.rssmc.common.capability.energyStorage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.EnergyStorage;

public class EnergyStorageSerializable extends EnergyStorage implements INBTSerializable<NBTTagCompound>
{
    public EnergyStorageSerializable(int capacity, int maxReceive, int maxExtract)
    {
        super(capacity, maxReceive, maxExtract);
    }

    public int receiveEnergyInternal(int toReceive, boolean simulate)
    {
        int energyReceived = Math.min(capacity - energy, toReceive);
        if (!simulate)
        {
            energy += energyReceived;
        }
        return energyReceived;
    }

    public int extractEnergyInternal(int toExtract, boolean simulate)
    {
        int energyExtracted = Math.min(energy, toExtract);
        if (!simulate)
        {
            energy -= energyExtracted;
        }
        return energyExtracted;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("energy", energy);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        energy = nbt.getInteger("energy");
    }
}