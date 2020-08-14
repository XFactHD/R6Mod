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
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class RecipeBlockFloorPanel implements IRecipe
{
    @Override
    @SuppressWarnings("deprecation")
    public boolean matches(InventoryCrafting inv, World world)
    {
        Item lastItem = null;
        int lastMeta = -1;
        for (int x = 0; x < inv.getWidth(); x++)
        {
            for (int y = 0; y < inv.getHeight(); y++)
            {
                ItemStack stack = inv.getStackInRowAndColumn(y, x);
                if (y == 1 && (stack == null || !(stack.getItem() == Items.IRON_INGOT))) { return false; }
                else
                {
                    if (stack == null) { return false; }
                    if (lastItem == null)
                    {
                        lastItem = stack.getItem();
                        lastMeta = stack.getMetadata();
                    }
                    else
                    {
                        if (lastItem != stack.getItem() || lastMeta != stack.getMetadata()) { return false; }
                        if (!(stack.getItem() instanceof ItemBlock)) { return false; }
                        IBlockState state = ((ItemBlock)stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata());
                        if (((ItemBlock)stack.getItem()).getBlock().hasTileEntity(state)) { return false; }
                        if (((ItemBlock)stack.getItem()).getBlock().getMaterial(state) != Material.WOOD) { return false; }
                    }
                }
            }
        }
        return true;
    }

    @Nullable
    @Override
    @SuppressWarnings({"ConstantConditions", "deprecation"})
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        if (matches(inv, null))
        {
            Block block = ((ItemBlock)inv.getStackInRowAndColumn(0, 0).getItem()).getBlock();
            ItemStack stack = new ItemStack(Content.blockFloorPanel);
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setString("camoBlock", block.getRegistryName().toString());
            IBlockState state = block.getStateFromMeta(stack.getMetadata());
            nbt.setInteger("camoMeta", block.getMetaFromState(state));
            return stack;
        }
        return null;
    }

    @Nullable
    @Override
    public ItemStack getRecipeOutput()
    {
        return new ItemStack(Content.blockFloorPanel);
    }

    @Override
    public int getRecipeSize()
    {
        return 9;
    }

    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting inv)
    {
        return new ItemStack[9];
    }
}