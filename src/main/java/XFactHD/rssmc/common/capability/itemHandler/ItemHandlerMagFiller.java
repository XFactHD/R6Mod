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

package XFactHD.rssmc.common.capability.itemHandler;

import XFactHD.rssmc.common.Content;
import XFactHD.rssmc.common.blocks.survival.TileEntityMagFiller;
import XFactHD.rssmc.common.data.EnumBullet;
import XFactHD.rssmc.common.data.EnumMagazine;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.ItemStackHandler;

public class ItemHandlerMagFiller extends ItemStackHandler
{
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_PROCESS = 1;
    public static final int SLOT_OUTPUT = 2;
    public static final int SLOT_BULLETS_1 = 3;
    public static final int SLOT_BULLETS_2 = 4;
    public static final int SLOT_BULLETS_3 = 5;

    private TileEntityMagFiller te;
    private EnumFacing lastSide = null;

    public ItemHandlerMagFiller(TileEntityMagFiller te)
    {
        super(6);
        this.te = te;
    }

    @Override
    public int getStackLimit(int slot, ItemStack stack)
    {
        return (slot == SLOT_INPUT || slot == SLOT_OUTPUT) ? 5 : slot == SLOT_PROCESS ? 1 : super.getStackLimit(slot, stack);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
    {
        EnumFacing side = lastSide;
        //lastSide = null; //INFO: can't be done here because hoppers (and potentially other item transports) are shit
        if (stack == null) { return stack; }
        if ((slot == SLOT_PROCESS || slot == SLOT_OUTPUT) && side != null) { return stack; }
        if ((slot == SLOT_BULLETS_1 || slot == SLOT_BULLETS_2 || slot == SLOT_BULLETS_3) && ((side != EnumFacing.UP && side != null) || stack.getItem() != Content.itemAmmo || stack.getMetadata() < EnumBullet.CARTRIDGE_357SIG.ordinal())) { return stack; }
        if (slot == SLOT_INPUT)
        {
            if (side != te.getFacing().rotateY() && side != null) { return stack; }
            if (stack.getItem() != Content.itemMagazine || !stack.hasTagCompound()) { return stack; }
            if (stack.getTagCompound().getInteger("currentAmmo") >= stack.getTagCompound().getInteger("maxAmmo")) { return stack; }
        }
        if (!checkAmmoCompatible(slot, stack)) { return stack; }
        return super.insertItem(slot, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        EnumFacing side = lastSide;
        lastSide = null;
        if (slot == SLOT_PROCESS && side != null) { return null; }
        if (slot == SLOT_INPUT && side != te.getFacing().rotateY() && side != null) { return null; }
        if (slot == SLOT_OUTPUT && side != te.getFacing().rotateYCCW() && side != null) { return null; }
        if ((slot == SLOT_BULLETS_1 || slot == SLOT_BULLETS_2 || slot == SLOT_BULLETS_3) && (side != EnumFacing.UP && side != null)) { return null; }
        return super.extractItem(slot, amount, simulate);
    }

    public boolean moveItem(int sourceSlot, int targetSlot, int amount)
    {
        ItemStack sourceStack = getStackInSlot(sourceSlot);
        ItemStack targetStack = getStackInSlot(targetSlot);

        if (sourceStack != null && sourceStack.stackSize >= amount && (targetStack == null || (sourceStack.isItemEqual(targetStack) && targetStack.stackSize + amount <= getStackLimit(targetSlot, targetStack))))
        {
            lastSide = null;
            ItemStack stack = extractItem(sourceSlot, amount, true);
            if (insertItem(targetSlot, stack, true) == null)
            {
                stack = extractItem(sourceSlot, amount, false);
                insertItem(targetSlot, stack, false);
            }
            return true;
        }
        return false;
    }

    public boolean extractBullet(EnumBullet bullet)
    {
        int[] slots = new int[] { SLOT_BULLETS_1, SLOT_BULLETS_2, SLOT_BULLETS_3 };
        ItemStack[] bullets = new ItemStack[] { getStackInSlot(SLOT_BULLETS_1), getStackInSlot(SLOT_BULLETS_2), getStackInSlot(SLOT_BULLETS_3) };
        lastSide = null;
        for (int i = 0; i < 3; i++)
        {
            if (bullets[i] != null && EnumBullet.valueOf(bullets[i]) == bullet)
            {
                if (extractItem(slots[i], 1, false) != null)
                {
                    return true;
                }
            }
        }
        return false;
    }

    public void setLastSide(EnumFacing lastSide)
    {
        this.lastSide = lastSide;
    }

    @Override
    protected void onContentsChanged(int slot)
    {
        if (getStackInSlot(slot) == null && slot != SLOT_OUTPUT && slot != SLOT_INPUT)
        {
            te.resetProgress();
        }
        te.notifyBlockUpdate();
    }

    private boolean checkAmmoCompatible(int slot, ItemStack stack)
    {
        if (stack != null && stack.getItem() == Content.itemAmmo && (slot == SLOT_BULLETS_1 || slot == SLOT_BULLETS_2 || slot == SLOT_BULLETS_3))
        {
            ItemStack mag = getStackInSlot(SLOT_INPUT);
            if (mag != null)
            {
                return EnumMagazine.valueOf(mag).getBullet() == EnumBullet.valueOf(stack);
            }
            else
            {
                int[] slots = new int[] { SLOT_BULLETS_1, SLOT_BULLETS_2, SLOT_BULLETS_3 };
                for (int i = 0; i < 3; i++)
                {
                    if (slots[i] != slot)
                    {
                        ItemStack stackInSlot = getStackInSlot(slots[i]);
                        if (stack.isItemEqual(stackInSlot))
                        {
                            return true;
                        }
                    }
                }
                for (int i = 0; i < 3; i++)
                {
                    if (slots[i] != slot)
                    {
                        ItemStack stackInSlot = getStackInSlot(slots[i]);
                        if (stackInSlot != null)
                        {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        else if (slot == SLOT_INPUT && stack != null && stack.getItem() == Content.itemMagazine)
        {
            ItemStack[] bullets = new ItemStack[] { getStackInSlot(SLOT_BULLETS_1), getStackInSlot(SLOT_BULLETS_2), getStackInSlot(SLOT_BULLETS_3) };
            for (int i = 0; i < 3; i++)
            {
                if (bullets[i] != null && EnumMagazine.valueOf(stack).getBullet() == EnumBullet.valueOf(bullets[i]))
                {
                    return true;
                }
            }
            for (int i = 0; i < 3; i++)
            {
                if (bullets[i] != null)
                {
                    return false;
                }
            }
            return true;
        }
        return true;
    }
}