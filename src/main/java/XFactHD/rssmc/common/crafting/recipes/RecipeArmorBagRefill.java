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
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class RecipeArmorBagRefill implements IRecipe
{
    @Override
    public boolean matches(InventoryCrafting inv, World world)
    {
        int bags = 0;
        int armors = 0;
        for (int i = 0; i < inv.getHeight()+1; i++)
        {
            for (int j = 0; j < inv.getWidth()+1; j++)
            {
                ItemStack stack = inv.getStackInRowAndColumn(j, i);
                if (stack == null)
                {
                    continue;
                }
                if (stack.getItem() == Item.getItemFromBlock(Content.blockArmorBag))
                {
                    //noinspection ConstantConditions
                    if (stack.getTagCompound().getInteger("armor") >= 20)
                    {
                        return false;
                    }
                    bags += 1;
                }
                else if (stack.getItem() == Content.itemRookUpgrade)
                {
                    armors += 1;
                }
                else
                {
                    return false;
                }
            }
        }
        return bags == 1 && armors > 0;
    }

    @Override
    public int getRecipeSize()
    {
        return 4;
    }

    @Nullable
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        if (matches(inv, null))
        {
            ItemStack bag = null;
            int armor = 0;
            for (int i = 0; i < inv.getHeight()+1; i++)
            {
                for (int j = 0; j < inv.getWidth()+1; j++)
                {
                    ItemStack stack = inv.getStackInRowAndColumn(j, i);
                    if (stack != null && stack.getItem() == Item.getItemFromBlock(Content.blockArmorBag))
                    {
                        bag = stack.copy();
                    }
                    else if (stack != null && stack.getItem() == Content.itemRookUpgrade)
                    {
                        armor += 1;
                    }
                }
            }
            //noinspection ConstantConditions
            int existing = bag.getTagCompound().getInteger("armor");
            bag.getTagCompound().setInteger("armor", existing + armor);
            return bag;
        }
        return null;
    }

    @Nullable
    @Override
    public ItemStack getRecipeOutput()
    {
        return new ItemStack(Content.blockArmorBag);
    }

    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting inv)
    {
        return new ItemStack[9];
    }
}