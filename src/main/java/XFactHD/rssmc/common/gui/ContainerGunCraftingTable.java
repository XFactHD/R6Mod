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

import XFactHD.rssmc.common.blocks.survival.TileEntityGunCraftingTable;
import XFactHD.rssmc.common.crafting.Crafting;
import XFactHD.rssmc.common.crafting.recipes.RecipeGunCrafting;
import XFactHD.rssmc.common.data.EnumGun;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerGunCraftingTable extends Container
{
    private TileEntityGunCraftingTable te;
    private EntityPlayer player;

    public ContainerGunCraftingTable(TileEntityGunCraftingTable te, EntityPlayer player)
    {
        this.te = te;
        this.player = player;
        for (int y = 0; y < 3; ++y)
        {
            for (int x = 0; x < 9; ++x)
            {
                addSlotToContainer(new Slot(player.inventory, x + y * 9 + 9, 8 + x * 18, 110 + y * 18));
            }
        }
        for (int x = 0; x < 9; ++x)
        {
            addSlotToContainer(new Slot(player.inventory, x, 8 + x * 18, 168));
        }
        addSlotToContainer(new SlotItemHandler(te.getItemHandler(),  0, 26, 23));
        addSlotToContainer(new SlotItemHandler(te.getItemHandler(),  1, 44, 23));
        addSlotToContainer(new SlotItemHandler(te.getItemHandler(),  2, 62, 23));
        addSlotToContainer(new SlotItemHandler(te.getItemHandler(),  3, 80, 23));
        addSlotToContainer(new SlotItemHandler(te.getItemHandler(),  4, 26, 41));
        addSlotToContainer(new SlotItemHandler(te.getItemHandler(),  5, 44, 41));
        addSlotToContainer(new SlotItemHandler(te.getItemHandler(),  6, 62, 41));
        addSlotToContainer(new SlotItemHandler(te.getItemHandler(),  7, 80, 41));
        addSlotToContainer(new SlotItemHandler(te.getItemHandler(),  8, 26, 59));
        addSlotToContainer(new SlotItemHandler(te.getItemHandler(),  9, 44, 59));
        addSlotToContainer(new SlotItemHandler(te.getItemHandler(), 10, 62, 59));
        addSlotToContainer(new SlotItemHandler(te.getItemHandler(), 11, 80, 59));
        addSlotToContainer(new SlotItemHandler(te.getItemHandler(), 12, 26, 77));
        addSlotToContainer(new SlotItemHandler(te.getItemHandler(), 13, 44, 77));
        addSlotToContainer(new SlotItemHandler(te.getItemHandler(), 14, 62, 77));
        addSlotToContainer(new SlotItemHandler(te.getItemHandler(), 15, 80, 77));
    }

    @Override
    public void onContainerClosed(EntityPlayer player)
    {
        super.onContainerClosed(player);
        for (int i = 0; i < 16; i++)
        {
            ItemStack stack = te.getItemHandler().getStackInSlot(i);
            if (stack != null)
            {
                player.dropItem(stack, false);
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return true;
    }

    public void consumeRecipeItems(EnumGun gun)
    {
        RecipeGunCrafting recipe = Crafting.getGunRecipe(gun);
        for (ItemStack stack : recipe.getIngredients())
        {
            te.getItemHandler().extractStack(stack);
        }
        player.inventory.addItemStackToInventory(gun.getGunItemStack());
    }
}