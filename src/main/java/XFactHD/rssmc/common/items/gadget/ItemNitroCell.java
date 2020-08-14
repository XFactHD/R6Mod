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
import XFactHD.rssmc.common.Content;
import XFactHD.rssmc.common.entity.gadget.EntityNitroCell;
import XFactHD.rssmc.common.items.ItemBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemNitroCell extends ItemBase
{
    public ItemNitroCell()
    {
        super("itemNitroCell", 1, RainbowSixSiegeMC.CT.gadgetTab, null);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
    {
        ItemStack phone = new ItemStack(Content.itemNitroPhone);
        if (!world.isRemote)
        {
            EntityNitroCell nitroCell = new EntityNitroCell(world, player);
            nitroCell.setHeadingFromThrower(player, player.rotationPitch, player.rotationYaw, 0, .8F, 0);
            phone.setTagCompound(new NBTTagCompound());
            phone.getTagCompound().setString("nitroUUID", nitroCell.getPersistentID().toString());
            phone.getTagCompound().setBoolean("active", false);
            if (world.spawnEntity(nitroCell))
            {
                return ActionResult.newResult(EnumActionResult.SUCCESS, phone);
            }
            else
            {
                return ActionResult.newResult(EnumActionResult.FAIL, stack);
            }
        }
        return ActionResult.newResult(EnumActionResult.SUCCESS, phone);
    }
}