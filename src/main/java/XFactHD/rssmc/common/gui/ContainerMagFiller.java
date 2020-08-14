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

import XFactHD.rssmc.common.blocks.survival.TileEntityMagFiller;
import XFactHD.rssmc.common.capability.itemHandler.ItemHandlerMagFiller;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;

public class ContainerMagFiller extends Container
{
    private TileEntityMagFiller te;
    private int energy = 0;
    private boolean active = false;

    public ContainerMagFiller(TileEntityMagFiller te, EntityPlayer player)
    {
        this.te = te;
        for (int y = 0; y < 3; ++y)
        {
            for (int x = 0; x < 9; ++x)
            {
                addSlotToContainer(new Slot(player.inventory, x + y * 9 + 9, 8 + x * 18, 101 + y * 18));
            }
        }
        for (int x = 0; x < 9; ++x)
        {
            addSlotToContainer(new Slot(player.inventory, x, 8 + x * 18, 159));
        }
        addSlotToContainer(new SlotItemHandlerMagFiller(te.getItemHandler(), 0,  42, 73));
        addSlotToContainer(new SlotItemHandlerMagFiller(te.getItemHandler(), 2,  118, 73));
        addSlotToContainer(new SlotItemHandlerMagFiller(te.getItemHandler(), 3,  60, 16));
        addSlotToContainer(new SlotItemHandlerMagFiller(te.getItemHandler(), 4,  80, 16));
        addSlotToContainer(new SlotItemHandlerMagFiller(te.getItemHandler(), 5,  100, 16));
        te.notifyBlockUpdate();
        detectAndSendChanges();
    }

    @Override
    public void updateProgressBar(int id, int data)
    {
        if (id == 0) { energy = data; }
        if (id == 1) { active = data == 1; }
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (IContainerListener listener : listeners)
        {
            if (energy != te.getEnergyStored())
            {
                listener.sendProgressBarUpdate(this, 0, te.getEnergyStored());
            }
            if (active != te.isActive())
            {
                listener.sendProgressBarUpdate(this, 1, te.isActive() ? 1 : 0);
            }
        }
        energy = te.getEnergyStored();
        active = te.isActive();
    }

    public int getEnergyStored()
    {
        return energy;
    }

    public boolean isActive()
    {
        return active;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return true;
    }

    @Nullable
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int sourceSlotIndex)
    {
        Slot sourceSlot = inventorySlots.get(sourceSlotIndex);
        if (sourceSlot == null || !sourceSlot.getHasStack()) return null;
        ItemStack sourceStack = sourceSlot.getStack();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (sourceSlotIndex >= 0 && sourceSlotIndex < 35)
        {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!mergeItemStack(sourceStack, 36, 40, false))
            {
                return null;
            }
        }
        else if (sourceSlotIndex >= 36 && sourceSlotIndex < 40)
        {
            // This is a TE slot so merge the stack into the players inventory
            if (!mergeItemStack(sourceStack, 0, 35, false))
            {
                return null;
            }
        }
        else
        {
            System.err.print("Invalid slotIndex:" + sourceSlotIndex);
            return null;
        }

        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.stackSize == 0)
        {
            sourceSlot.putStack(null);
        }
        else
        {
            sourceSlot.onSlotChanged();
        }

        sourceSlot.onPickupFromSlot(player, sourceStack);
        return copyOfSourceStack;

        //INFO: 1.11+ code
        //Slot sourceSlot = inventorySlots.get(sourceSlotIndex);
        //if (sourceSlot == null || !sourceSlot.getHasStack()) return ItemStack.EMPTY;
        //ItemStack sourceStack = sourceSlot.getStack();
        //ItemStack copyOfSourceStack = sourceStack.copy();

        //// Check if the slot clicked is one of the vanilla container slots
        //if (sourceSlotIndex >= VANILLA_FIRST_SLOT_INDEX && sourceSlotIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT)
        //{
        //    // This is a vanilla container slot so merge the stack into the tile inventory
        //    if (!mergeItemStack(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT, false))
        //    {
        //        return ItemStack.EMPTY;  // EMPTY_ITEM
        //    }
        //}
        //else if (sourceSlotIndex >= TE_INVENTORY_FIRST_SLOT_INDEX && sourceSlotIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT)
        //{
        //    // This is a TE slot so merge the stack into the players inventory
        //    if (!mergeItemStack(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false))
        //    {
        //        return ItemStack.EMPTY;   // EMPTY_ITEM
        //    }
        //}
        //else
        //{
        //    System.err.print("Invalid slotIndex:" + sourceSlotIndex);
        //    return ItemStack.EMPTY;   // EMPTY_ITEM
        //}

        //// If stack size == 0 (the entire stack was moved) set slot contents to null
        //if (sourceStack.getCount() == 0)
        //{  // getStackSize
        //    sourceSlot.putStack(ItemStack.EMPTY);  // EMPTY_ITEM
        //}
        //else
        //{
        //    sourceSlot.onSlotChanged();
        //}

        //sourceSlot.onTake(player, sourceStack);  //onPickupFromSlot()
        //return copyOfSourceStack;
    }

    private static class SlotItemHandlerMagFiller extends SlotItemHandler
    {
        public SlotItemHandlerMagFiller(IItemHandler itemHandler, int index, int xPosition, int yPosition)
        {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public int getSlotStackLimit()
        {
            return ((ItemHandlerMagFiller)getItemHandler()).getStackLimit(getSlotIndex(), null);
        }

        @Override
        public void putStack(ItemStack stack)
        {
            ((ItemHandlerMagFiller)getItemHandler()).setLastSide(null);
            this.getItemHandler().insertItem(getSlotIndex(), stack, false);
            this.onSlotChanged();
        }

        @Override
        public boolean canTakeStack(EntityPlayer playerIn)
        {
            ((ItemHandlerMagFiller)getItemHandler()).setLastSide(null);
            return super.canTakeStack(playerIn);
        }


    }
}