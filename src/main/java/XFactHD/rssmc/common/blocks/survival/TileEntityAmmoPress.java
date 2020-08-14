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
import XFactHD.rssmc.common.capability.itemHandler.ItemHandlerAmmoPress;
import XFactHD.rssmc.common.utils.helper.ConfigHandler;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class TileEntityAmmoPress extends TileEntityBase implements ITickable
{
    private static final int BULLET_OFFSET = 11;
    private static final int ROUND_OFFSET = 22;
    private static final int MAX_PROGRESS = 80;

    private ItemHandlerAmmoPress itemHandler = new ItemHandlerAmmoPress(this);
    private EnergyStorageSerializable energyStorage = new EnergyStorageSerializable(5000, 250, 0);
    private int progress = 0;
    private boolean proccessingLast = false;
    private boolean processing = false;
    private int toProcess = 0;

    @Override
    public void update()
    {
        if (!world.isRemote)
        {
            //energyStorage.receiveEnergy(50, false);

            if (processing != proccessingLast)
            {
                proccessingLast = processing;
                notifyBlockUpdate();
            }

            if (progress >= MAX_PROGRESS)
            {
                progress = 0;
                processing = false;
                toProcess = 0;
                finishProcess();
            }
            else if (!processing && progress > 0 && energyStorage.getEnergyStored() > .5F * toProcess)
            {
                processing = true;
            }
            else if (processing)
            {
                progressAndConsumeEnergy();
            }

            if (!processing && progress == 0 && canProcess())
            {
                startProcess();
                processing = true;
            }
        }
    }

    private boolean canProcess()
    {
        ItemStack bullet = itemHandler.getStackInSlot(0);
        ItemStack gunpowder = itemHandler.getStackInSlot(1);
        ItemStack casing = itemHandler.getStackInSlot(2);
        ItemStack output = itemHandler.getStackInSlot(3);
        return bullet != null && gunpowder != null && casing != null &&
               (output == null || output.stackSize < 64) &&
               bullet.getMetadata() == casing.getMetadata() + BULLET_OFFSET && (output == null || output.getMetadata() == casing.getMetadata() + ROUND_OFFSET);
    }

    public void resetProgress()
    {
        progress = 0;
        processing = false;
    }

    private void progressAndConsumeEnergy()
    {
        if (true)
        {
            if (energyStorage.getEnergyStored() > .5F * toProcess)
            {
                energyStorage.extractEnergyInternal((int) (.5F * toProcess), false);
                progress += 1;
            }
            else
            {
                processing = false;
            }
        }
        else
        {
            progress += 1;
        }
    }

    private void startProcess()
    {
        ItemStack bullet = itemHandler.getStackInSlot(0);
        ItemStack gunpowder = itemHandler.getStackInSlot(1);
        ItemStack casing = itemHandler.getStackInSlot(2);
        ItemStack output = itemHandler.getStackInSlot(3);
        int smallest = Math.min(Math.min(bullet.stackSize, gunpowder.stackSize), output == null ? 64 : Math.min(casing.stackSize, 64 - output.stackSize));
        toProcess = Math.min(25, smallest);
    }

    private void finishProcess()
    {
        ItemStack bullet = itemHandler.getStackInSlot(0);
        ItemStack gunpowder = itemHandler.getStackInSlot(1);
        ItemStack casing = itemHandler.getStackInSlot(2);
        ItemStack output = itemHandler.getStackInSlot(3);
        itemHandler.setStackInSlot(0, ItemHandlerHelper.copyStackWithSize(bullet, bullet.stackSize - toProcess));
        itemHandler.setStackInSlot(1, ItemHandlerHelper.copyStackWithSize(gunpowder, gunpowder.stackSize - toProcess));
        itemHandler.setStackInSlot(2, ItemHandlerHelper.copyStackWithSize(casing, casing.stackSize - toProcess));
        itemHandler.setStackInSlot(3, new ItemStack(Content.itemAmmo, output == null ? toProcess : output.stackSize + toProcess, casing.getMetadata() + ROUND_OFFSET));
        notifyBlockUpdate();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && canAccessInv(facing)) ||
               (capability == CapabilityEnergy.ENERGY && facing == EnumFacing.DOWN) ||
               super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && canAccessInv(facing))
        {
            return (T)itemHandler;
        }
        else if (capability == CapabilityEnergy.ENERGY && facing == EnumFacing.DOWN)
        {
            return (T) energyStorage;
        }
        return super.getCapability(capability, facing);
    }

    private boolean canAccessInv(EnumFacing side)
    {
        return side != getState().getValue(PropertyHolder.FACING_CARDINAL) && side.getAxis().isHorizontal();
    }

    public ItemHandlerAmmoPress getItemHandler()
    {
        return itemHandler;
    }

    public int getProgress()
    {
        return progress;
    }

    public int getEnergyStored()
    {
        return energyStorage.getEnergyStored();
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        itemHandler.deserializeNBT(nbt.getCompoundTag("inv"));
        energyStorage.deserializeNBT(nbt.getCompoundTag("power"));
        progress = nbt.getInteger("progress");
        processing = nbt.getBoolean("processing");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        nbt.setTag("inv", itemHandler.serializeNBT());
        nbt.setTag("power", energyStorage.serializeNBT());
        nbt.setInteger("progress", progress);
        nbt.setBoolean("processing", processing);
    }
}