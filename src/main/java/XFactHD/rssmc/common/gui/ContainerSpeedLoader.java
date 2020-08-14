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

import XFactHD.rssmc.common.data.EnumBullet;
import XFactHD.rssmc.common.data.EnumMagazine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerSpeedLoader extends Container
{
    private InventoryPlayer inv;
    private ItemStackHandlerSpeedLoader itemHandler;

    public ContainerSpeedLoader(EntityPlayer player)
    {
        inv = player.inventory;
        itemHandler = new ItemStackHandlerSpeedLoader();
        for (int y = 0; y < 3; ++y)
        {
            for (int x = 0; x < 9; ++x)
            {
                addSlotToContainer(new Slot(inv, x + y * 9 + 9, 8 + x * 18, 62 + y * 18));
            }
        }
        for (int x = 0; x < 9; ++x)
        {
            addSlotToContainer(new Slot(inv, x, 8 + x * 18, 120));
        }
        addSlotToContainer(new SlotItemHandlerSpeedLoader(itemHandler, 0,  60, 14));
        addSlotToContainer(new SlotItemHandlerSpeedLoader(itemHandler, 1,  99, 14));
    }

    @Override
    public void onContainerClosed(EntityPlayer player)
    {
        super.onContainerClosed(player);
        player.dropItem(itemHandler.getStackInSlot(0), false);
        player.dropItem(itemHandler.getStackInSlot(1), false);
    }

    public ItemStackHandlerSpeedLoader getItemHandler()
    {
        return itemHandler;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return true;
    }

    @SuppressWarnings("ConstantConditions")
    public void loadMag()
    {
        ItemStack ammoStack = itemHandler.getStackInSlot(1);
        ItemStack magStack = itemHandler.getStackInSlot(0);
        EnumMagazine mag = EnumMagazine.valueOf(magStack);
        if (mag.getBullet() != EnumBullet.valueOf(ammoStack)) { return; }
        int loaded = magStack.getTagCompound().getInteger("currentAmmo");
        int amount = Math.min(mag.getMagCap() - loaded, ammoStack.stackSize);
        itemHandler.extractItem(1, amount, false);
        magStack.getTagCompound().setInteger("currentAmmo", magStack.getTagCompound().getInteger("currentAmmo") + amount);
        inv.markDirty();
        for (IContainerListener listener : listeners)
        {
            listener.sendSlotContents(this, 36, magStack);
            listener.sendSlotContents(this, 37, ammoStack);
        }
    }

    private static class SlotItemHandlerSpeedLoader extends SlotItemHandler
    {
        public SlotItemHandlerSpeedLoader(IItemHandler itemHandler, int index, int xPosition, int yPosition)
        {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public void putStack(ItemStack stack)
        {
            this.getItemHandler().insertItem(getSlotIndex(), stack, false);
            this.onSlotChanged();
        }


        public void putMagStack(ItemStack stack)
        {
            ((ItemStackHandlerSpeedLoader)getItemHandler()).setMagStack(stack);
            this.onSlotChanged();
        }
    }

    public static class ItemStackHandlerSpeedLoader implements IItemHandler, INBTSerializable<NBTTagCompound>
    {
        protected ItemStack[] stacks = new ItemStack[2];

        @Override
        public int getSlots()
        {
            return 2;
        }

        @Override
        public ItemStack getStackInSlot(int slot)
        {
            if (slot > 1) { throw new IndexOutOfBoundsException("Slot index is to high!"); }
            return this.stacks[slot];
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
        {
            if (stack == null || stack.stackSize == 0)
                return null;

            if (slot > 1) { throw new IndexOutOfBoundsException("Slot index is to high!"); }

            ItemStack existing = this.stacks[slot];

            int limit = slot == 0 ? 1 : 64;

            if (existing != null)
            {
                if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                    return stack;

                limit -= existing.stackSize;
            }

            if (limit <= 0)
                return stack;

            boolean reachedLimit = stack.stackSize > limit;

            if (!simulate)
            {
                if (existing == null)
                {
                    this.stacks[slot] = reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack;
                }
                else
                {
                    existing.stackSize += reachedLimit ? limit : stack.stackSize;
                }
            }

            return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.stackSize - limit) : null;
        }

        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            if (amount == 0)
                return null;

            if (slot > 1) { throw new IndexOutOfBoundsException("Slot index is to high!"); }

            ItemStack existing = this.stacks[slot];

            if (existing == null)
                return null;

            int toExtract = Math.min(amount, existing.getMaxStackSize());

            if (existing.stackSize <= toExtract)
            {
                if (!simulate)
                {
                    this.stacks[slot] = null;
                }
                return existing;
            }
            else
            {
                if (!simulate)
                {
                    this.stacks[slot] = ItemHandlerHelper.copyStackWithSize(existing, existing.stackSize - toExtract);
                }

                return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
            }
        }

        public void setMagStack(ItemStack stack)
        {
            stacks[0] = stack;
        }

        @Override
        public NBTTagCompound serializeNBT()
        {
            return new NBTTagCompound();
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {}
    }
}