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

package XFactHD.rssmc.common.items.gadget;

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.api.item.ISpecialRightClick;
import XFactHD.rssmc.common.items.ItemBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

@SuppressWarnings("ConstantConditions")
public class ItemCandelaGrenade extends ItemBase implements ISpecialRightClick
{
    public ItemCandelaGrenade()
    {
        super("item_candela", 3, RainbowSixSiegeMC.CT.gadgetTab, null);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected)
    {
        if (!stack.hasTagCompound()) { stack.setTagCompound(new NBTTagCompound()); }
        if (stack.getTagCompound().getInteger("timer") < 3 && stack.getTagCompound().getLong("stamp") != 0)
        {
            if (world.getTotalWorldTime() - stack.getTagCompound().getLong("stamp") > 40)
            {
                stack.getTagCompound().setLong("stamp", world.getTotalWorldTime());
                stack.getTagCompound().setInteger("timer", stack.getTagCompound().getInteger("timer") + 1);
                world.playSound(entity.posX, entity.posY, entity.posZ, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.PLAYERS, 1, 3, false);
            }
        }
    }

    @Override
    public void startRightClick(ItemStack stack, EntityPlayer player, World world, EnumHand hand)
    {
        stack.getTagCompound().setLong("stamp", world.getTotalWorldTime());
    }

    @Override
    public void stopRightClick(ItemStack stack, EntityPlayer player, World world, EnumHand hand)
    {
        stack.getTagCompound().setLong("stamp", 0);
        if (!world.isRemote)
        {
            int timer = stack.getTagCompound().getInteger("timer");
            //EntityCandelaGrenade grenade = new EntityCandelaGrenade(world, player);//TODO: spawn entity
            //grenade.setTimer(timer);
            //grenade.setHeadingFromThrower(player, player.rotationPitch, player.rotationYaw, 0, 1, 0);
            //if (world.spawnEntityInWorld(grenade))
            {
                stack.stackSize -= 1;
                player.inventory.markDirty();
            }
        }
        stack.getTagCompound().setInteger("timer", 0);
    }

    @Override
    public void scrollOff(ItemStack stack, EntityPlayer player, World world, EnumHand hand)
    {
        stack.getTagCompound().setLong("stamp", 0);
        stack.getTagCompound().setInteger("timer", 0);
    }
}