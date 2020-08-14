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
import XFactHD.rssmc.common.capability.energyStorage.EnergyStorageSerializable;
import XFactHD.rssmc.common.capability.itemHandler.ItemHandlerMagFiller;
import XFactHD.rssmc.common.data.EnumMagazine;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileEntityMagFiller extends TileEntityBase implements ITickable //FIXME: when the slots are accessed by process code or a hopper, item numbers are doubled on the client
{
    private static final int RF_PER_TICK = 10;

    private EnumFacing facing = null;
    private EnergyStorageSerializable energyStorage = new EnergyStorageSerializable(10000, 500, 0);
    private ItemHandlerMagFiller itemHandler = new ItemHandlerMagFiller(this);
    private boolean active = false;
    private float tankFillState = 0;
    private int ticks = 0;

    @Override
    @SuppressWarnings("ConstantConditions")
    public void update()
    {
        if (world.isRemote)
        {
            calculateTankFillState();
        }
        else
        {
            energyStorage.receiveEnergy(10, false); //TODO: remove when finished
            checkCanProcess();
            if (active) { process(); }
            ItemStack stack = itemHandler.getStackInSlot(ItemHandlerMagFiller.SLOT_PROCESS);
            if (stack != null && stack.getTagCompound().getInteger("currentAmmo") >= stack.getTagCompound().getInteger("maxAmmo"))
            {
                itemHandler.setLastSide(null);
                if (!itemHandler.moveItem(ItemHandlerMagFiller.SLOT_PROCESS, ItemHandlerMagFiller.SLOT_OUTPUT, 1)) { active = false; }
            }
        }
    }

    private void calculateTankFillState()
    {
        ItemStack stack1 = itemHandler.getStackInSlot(ItemHandlerMagFiller.SLOT_BULLETS_1);
        ItemStack stack2 = itemHandler.getStackInSlot(ItemHandlerMagFiller.SLOT_BULLETS_2);
        ItemStack stack3 = itemHandler.getStackInSlot(ItemHandlerMagFiller.SLOT_BULLETS_3);
        float amount = 0;
        if (stack1 != null) { amount += stack1.stackSize; }
        if (stack2 != null) { amount += stack2.stackSize; }
        if (stack3 != null) { amount += stack3.stackSize; }
        tankFillState = 1F - amount / 192F;
    }

    @SuppressWarnings("ConstantConditions")
    private void checkCanProcess()
    {
        if (energyStorage.getEnergyStored() > RF_PER_TICK)
        {
            ItemStack input = itemHandler.getStackInSlot(ItemHandlerMagFiller.SLOT_INPUT);
            ItemStack processing = itemHandler.getStackInSlot(ItemHandlerMagFiller.SLOT_PROCESS);
            ItemStack output = itemHandler.getStackInSlot(ItemHandlerMagFiller.SLOT_OUTPUT);
            if (processing != null && processing.getTagCompound().getInteger("currentAmmo") >= processing.getTagCompound().getInteger("maxAmmo") && output != null && output.stackSize >= 5)
            {
                active = false;
                ticks = 0;
            }
            else
            {
                ItemStack bullets1 = itemHandler.getStackInSlot(ItemHandlerMagFiller.SLOT_BULLETS_1);
                ItemStack bullets2 = itemHandler.getStackInSlot(ItemHandlerMagFiller.SLOT_BULLETS_2);
                ItemStack bullets3 = itemHandler.getStackInSlot(ItemHandlerMagFiller.SLOT_BULLETS_3);
                active = ((input != null || processing != null) && (bullets1 != null || bullets2 != null || bullets3 != null));
                if (!active) { ticks = 0; }
            }
        }
        else
        {
            active = false;
            ticks = 0;
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void process()
    {
        if (energyStorage.extractEnergyInternal(RF_PER_TICK, true) != RF_PER_TICK) { return; }
        energyStorage.extractEnergyInternal(RF_PER_TICK, false);
        if (ticks == 0)
        {
            if (itemHandler.getStackInSlot(ItemHandlerMagFiller.SLOT_PROCESS) == null)
            {
                if (!itemHandler.moveItem(ItemHandlerMagFiller.SLOT_INPUT, ItemHandlerMagFiller.SLOT_PROCESS, 1)) { return; }
            }
            ItemStack stack = itemHandler.getStackInSlot(ItemHandlerMagFiller.SLOT_PROCESS);
            if (stack != null && stack.getTagCompound().getInteger("currentAmmo") < stack.getTagCompound().getInteger("maxAmmo"))
            {
                if (itemHandler.extractBullet(EnumMagazine.valueOf(stack).getBullet()))
                {
                    stack.getTagCompound().setInteger("currentAmmo", stack.getTagCompound().getInteger("currentAmmo") + 1);
                }
            }
            ticks++;
        }
        else
        {
            ticks++;
            if (ticks > 5) { ticks = 0; }
        }
    }

    public float getTankFillState()
    {
        return tankFillState;
    }

    public ItemStack getInputStack()
    {
        return itemHandler.getStackInSlot(ItemHandlerMagFiller.SLOT_INPUT);
    }

    public ItemStack getProcessStack()
    {
        return itemHandler.getStackInSlot(ItemHandlerMagFiller.SLOT_PROCESS);
    }

    public ItemStack getOutputStack()
    {
        return itemHandler.getStackInSlot(ItemHandlerMagFiller.SLOT_OUTPUT);
    }

    public int getEnergyStored()
    {
        return energyStorage.getEnergyStored();
    }

    public boolean isActive()
    {
        return active;
    }

    public void resetProgress()
    {
        active = false;
        ticks = 0;
    }

    public EnumFacing getFacing()
    {
        if (facing == null)
        {
            facing = world.getBlockState(pos).getValue(PropertyHolder.FACING_CARDINAL);
        }
        return facing;
    }

    public ItemHandlerMagFiller getItemHandler()
    {
        return itemHandler;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing side)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            return side == EnumFacing.UP || side == getFacing().rotateY() || side == getFacing().rotateYCCW();
        }
        if (capability == CapabilityEnergy.ENERGY)
        {
            return side == EnumFacing.DOWN;
        }
        return super.hasCapability(capability, side);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing side)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (side == EnumFacing.UP || side == getFacing().rotateY() || side == getFacing().rotateYCCW()))
        {
            itemHandler.setLastSide(side);
            return (T)itemHandler;
        }
        if (capability == CapabilityEnergy.ENERGY && side == EnumFacing.DOWN)
        {
            return (T)null; //TODO: create energy handler
        }
        return super.getCapability(capability, side);
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        energyStorage.deserializeNBT(nbt.getCompoundTag("energy"));
        itemHandler.deserializeNBT(nbt.getCompoundTag("inv"));
        active = nbt.getBoolean("active");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        nbt.setTag("energy", energyStorage.serializeNBT());
        nbt.setTag("inv", itemHandler.serializeNBT());
        nbt.setBoolean("active", active);
    }
}