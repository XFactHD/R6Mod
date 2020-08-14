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
import XFactHD.rssmc.common.blocks.survival.TileEntityAmmoPress;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class ItemHandlerAmmoPress extends ItemStackHandler
{
    private TileEntityAmmoPress te;

    public ItemHandlerAmmoPress(TileEntityAmmoPress te)
    {
        super(4);
        this.te = te;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
    {
        if (slot == 3) { return null; }
        if (slot == 0 && (stack.getItem() != Content.itemAmmo || stack.getMetadata() > 10)) { return null; }
        if (slot == 1 && stack.getItem() != Items.GUNPOWDER) { return null; }
        if (slot == 2 && (stack.getItem() != Content.itemAmmo || stack.getMetadata() < 11 || stack.getMetadata() > 21)) { return null; }
        return super.insertItem(slot, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        if (slot != 3) { return null; }
        return super.extractItem(slot, amount, simulate);
    }

    @Override
    protected void onContentsChanged(int slot)
    {
        te.resetProgress();
        te.notifyBlockUpdate();
    }
}