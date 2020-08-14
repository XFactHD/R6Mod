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

package XFactHD.rssmc.common.gui;

import XFactHD.rssmc.common.blocks.survival.TileEntityAmmoPress;
import XFactHD.rssmc.common.utils.helper.LogHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;

public class ContainerAmmoPress extends Container
{
    private TileEntityAmmoPress te;
    private static final int MAX_PROGRESS = 80;
    private static final int MAX_ENERGY = 5000;
    private int progress = 0;
    private int energy = 0;

    public ContainerAmmoPress(TileEntityAmmoPress te, InventoryPlayer inv)
    {
        this.te = te;

        for (int y = 0; y < 3; ++y)
        {
            for (int x = 0; x < 9; ++x)
            {
                addSlotToContainer(new Slot(inv, x + y * 9 + 9, 8 + x * 18, 102 + y * 18));
            }
        }
        for (int x = 0; x < 9; ++x)
        {
            addSlotToContainer(new Slot(inv, x, 8 + x * 18, 160));
        }
        addSlotToContainer(new SlotItemHandler(te.getItemHandler(), 0,  48, 17));
        addSlotToContainer(new SlotItemHandler(te.getItemHandler(), 1,  48, 44));
        addSlotToContainer(new SlotItemHandler(te.getItemHandler(), 2,  48, 71));
        addSlotToContainer(new SlotItemHandler(te.getItemHandler(), 3, 108, 44));
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (IContainerListener listener : listeners)
        {
            if (progress != te.getProgress())
            {
                listener.sendProgressBarUpdate(this, 0, te.getProgress());
            }
            if (energy != te.getEnergyStored())
            {
                listener.sendProgressBarUpdate(this, 1, te.getEnergyStored());
            }
        }
        progress = te.getProgress();
        energy = te.getEnergyStored();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data)
    {
        if (id == 0)
        {
            progress = data;
        }
        else if (id == 1)
        {
            energy = data;
        }
    }

    public float getProgressScaled()
    {
        return ((float) progress) / ((float) MAX_PROGRESS);
    }

    public float getEnergyStoredScaled()
    {
        return ((float) energy) / ((float) MAX_ENERGY);
    }

    public int getEnergyStored()
    {
        return energy;
    }

    public int getMaxEnergy()
    {
        return MAX_ENERGY;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return true;
    }

    @Nullable
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index)
    {
        //TODO: implement
        return super.transferStackInSlot(player, index);
    }
}