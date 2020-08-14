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

package XFactHD.rssmc.common.items.ammo;

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.common.data.EnumMagazine;
import XFactHD.rssmc.common.items.ItemBase;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public class ItemMagazine extends ItemBase
{
    public ItemMagazine()
    {
        super("itemMagazine", 16, RainbowSixSiegeMC.CT.ammoTab, EnumMagazine.getAsStringArray());
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> subItems)
    {
        super.getSubItems(item, tab, subItems);
        if (item != this) { return; }
        for (ItemStack stack : subItems)
        {
            if (stack.getItem() != this) { continue; }
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setInteger("maxAmmo", EnumMagazine.values()[stack.getMetadata()].getMagCap());
            nbt.setInteger("currentAmmo", 0/*EnumMagazine.values()[stack.getMetadata()].getMagCap()*/); //TODO: change back, when speedloader and mag filler work
            stack.setTagCompound(nbt);
        }
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
        super.addInformation(stack, player, tooltip, advanced);
        String s = I18n.format("desc.rssmc:ammoLeft.name");
        s += (" " + stack.getTagCompound().getInteger("currentAmmo") + "/" + stack.getTagCompound().getInteger("maxAmmo"));
        tooltip.add(s);
    }
}