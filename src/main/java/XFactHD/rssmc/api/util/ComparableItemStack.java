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

package XFactHD.rssmc.api.util;

import net.minecraft.item.ItemStack;

public class ComparableItemStack
{
    private ItemStack stack;

    public ComparableItemStack(ItemStack stack)
    {
        this.stack = stack;
    }

    public ItemStack getStack()
    {
        return stack;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof ComparableItemStack)
        {
            ItemStack stack2 = ((ComparableItemStack)obj).getStack();
            if (!(stack2.getItem() == stack.getItem()))
            {
                return false;
            }
            if (!(stack2.getMetadata() == stack.getMetadata()))
            {
                return false;
            }
            if ((stack2.hasTagCompound() != stack.hasTagCompound()) || (stack.hasTagCompound() && stack2.hasTagCompound() && !(stack2.getTagCompound().equals(stack.getTagCompound()))))
            {
                return false;
            }
            return true;
        }
        return super.equals(obj);
    }
}