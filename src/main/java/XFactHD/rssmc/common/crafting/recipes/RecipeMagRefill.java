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

package XFactHD.rssmc.common.crafting.recipes;

import XFactHD.rssmc.common.Content;
import XFactHD.rssmc.common.data.EnumBullet;
import XFactHD.rssmc.common.data.EnumMagazine;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class RecipeMagRefill implements IRecipe
{
    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean matches(InventoryCrafting inv, World world)
    {
        int nullStacks = 0;
        ItemStack stackCenter = inv.getStackInRowAndColumn(1, 1);
        if (stackCenter == null || stackCenter.getItem() != Content.itemMagazine) { return false; }
        for (int x = 0; x < inv.getWidth(); x++)
        {
            for (int y = 0; y < inv.getHeight(); y++)
            {
                if (x != 1 && y != 1)
                {
                    ItemStack stack = inv.getStackInRowAndColumn(y, x);
                    if (stack == null)
                    {
                        nullStacks += 1;
                    }
                    else if (stack.getItem() != Content.itemAmmo) { return false; }
                    else if (EnumMagazine.values()[stackCenter.getMetadata()].getBullet() != EnumBullet.values()[stack.getMetadata()]) { return false; }
                }
            }
        }
        int loaded = stackCenter.getTagCompound().getInteger("currentAmmo");
        int max = stackCenter.getTagCompound().getInteger("maxAmmo");
        if ((8 - nullStacks) > max - loaded) { return false; }
        return nullStacks < 8;
    }

    @Nullable
    @Override
    @SuppressWarnings("ConstantConditions")
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        if (matches(inv, null))
        {
            ItemStack mag = inv.getStackInRowAndColumn(1, 1);
            int bullets = 0;
            for (int x = 0; x < inv.getWidth(); x++)
            {
                for (int y = 0; y < inv.getHeight(); y++)
                {
                    ItemStack stack = inv.getStackInRowAndColumn(y, x);
                    if (x != 1 && y != 1 && stack != null)
                    {
                        bullets += 1;
                    }
                }
            }
            int loaded = mag.getTagCompound().getInteger("currentAmmo");
            int max = mag.getTagCompound().getInteger("maxAmmo");
            int toLoad = Math.min(bullets, max - loaded);
            ItemStack result = mag.copy();
            result.getTagCompound().setInteger("currentAmmo", loaded + toLoad);
            return result;
        }
        return null;
    }

    @Nullable
    @Override
    public ItemStack getRecipeOutput()
    {
        return new ItemStack(Content.itemMagazine);
    }

    @Override
    public int getRecipeSize()
    {
        return 9;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public ItemStack[] getRemainingItems(InventoryCrafting inv)
    {
        return new ItemStack[inv.getSizeInventory()];
    }
}