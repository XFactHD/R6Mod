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

package XFactHD.rssmc.common.blocks.misc;

import XFactHD.rssmc.common.blocks.TileEntityBase;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class TileEntityWeaponShelf extends TileEntityBase
{
    private ItemStackHandler inventory = new ItemStackHandler(3);
    private static final float subHitLeft = 1F/3F;
    private static final float subHitRight = 2F/3F;

    public boolean addGun(ItemStack stack, float subHit)
    {
        int slot = subHit < subHitLeft ? 0 : (subHit >= subHitLeft && subHit <= subHitRight) ? 1 : subHit > subHitRight ? 2 : -1;
        if (inventory.getStackInSlot(slot) != null || inventory.insertItem(slot, stack, true) != null)
        {
            return false;
        }
        inventory.insertItem(slot, stack, world.isRemote);
        notifyBlockUpdate();
        return true;
    }

    @Nullable
    public ItemStack removeGun(float subHit)
    {
        int slot = subHit < subHitLeft ? 0 : (subHit >= subHitLeft && subHit <= subHitRight) ? 1 : subHit > subHitRight ? 2 : -1;
        if (inventory.getStackInSlot(slot) == null)
        {
            return null;
        }
        ItemStack stack = inventory.extractItem(slot, 1, world.isRemote);
        notifyBlockUpdate();
        return stack;
    }

    public ItemStack[] getStacks()
    {
        ItemStack[] stacks = new ItemStack[3];
        stacks[0] = inventory.getStackInSlot(0);
        stacks[1] = inventory.getStackInSlot(1);
        stacks[2] = inventory.getStackInSlot(2);
        return stacks;
    }

    public EnumFacing getFacing()
    {
        return world.getBlockState(pos).getValue(PropertyHolder.FACING_CARDINAL);
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        nbt.setTag("inv", inventory.serializeNBT());
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        inventory.deserializeNBT(nbt.getCompoundTag("inv"));
    }
}