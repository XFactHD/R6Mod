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

package XFactHD.rssmc.common.utils.utilClasses;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public abstract class CreativeTabRSSMC extends CreativeTabs
{
    public CreativeTabRSSMC(String name)
    {
        super(name);
    }

    public abstract<T extends ItemStack> void prepareTabSorter();
}