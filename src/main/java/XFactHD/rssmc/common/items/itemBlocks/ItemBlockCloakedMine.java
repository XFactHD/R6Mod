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

import XFactHD.rssmc.api.item.ICooldown;
import XFactHD.rssmc.common.blocks.BlockBase;
import XFactHD.rssmc.common.data.team.StatusController;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

@SuppressWarnings("ConstantConditions") //TODO: rework stacksize handling, using up last item will destroy the stack, rework to deploy entity that places block on impact
public class ItemBlockCloakedMine extends ItemBlockBase implements ICooldown
{
    public ItemBlockCloakedMine(BlockBase block)
    {
        super(block, 7);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected)
    {
        if (stack.stackSize >= 7 || !(entity instanceof EntityPlayer) || !StatusController.isGameRunning()) { return; }
        if (!stack.hasTagCompound()) { stack.setTagCompound(new NBTTagCompound()); }
        if (stack.getTagCompound().getLong("time") == 0)
        {
            stack.getTagCompound().setLong("time", world.getTotalWorldTime());
        }
        else if (world.getTotalWorldTime() - stack.getTagCompound().getLong("time") >= 700)
        {
            if (!world.isRemote) { stack.stackSize += 1; }
            stack.getTagCompound().setLong("time", world.getTotalWorldTime());
            ((EntityPlayer)entity).inventory.markDirty();
        }
    }

    @Override
    public int getCurrentTime(ItemStack stack, World world)
    {
        if (!stack.hasTagCompound() || stack.stackSize == 8) { return -1; }
        return (int) (world.getTotalWorldTime() - stack.getTagCompound().getLong("time"));
    }

    @Override
    public int getCooldownTime(ItemStack stack)
    {
        return 700;
    }
}