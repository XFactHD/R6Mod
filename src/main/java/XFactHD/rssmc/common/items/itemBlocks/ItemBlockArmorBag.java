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

package XFactHD.rssmc.common.items.itemBlocks;

import XFactHD.rssmc.common.blocks.BlockBase;
import XFactHD.rssmc.common.items.itemBlocks.ItemBlockBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.*;

import java.util.List;

public class ItemBlockArmorBag extends ItemBlockBase
{
    public ItemBlockArmorBag(BlockBase block)
    {
        super(block);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> subItems)
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("armor", 5);
        ItemStack stack = new ItemStack(item);
        stack.setTagCompound(nbt);
        subItems.add(stack);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
        super.addInformation(stack, player, tooltip, advanced);
        if (stack.hasTagCompound())
        {
            //noinspection ConstantConditions
            int armor = stack.getTagCompound().getInteger("armor");
            TextComponentTranslation text = new TextComponentTranslation("desc.rssmc:armorContained.name");
            TextFormatting formatting = armor > 0 ? TextFormatting.GREEN : TextFormatting.RED;
            text.appendText(formatting.toString() + " " + Integer.toString(armor));
            tooltip.add(text.getFormattedText());
        }
    }
}