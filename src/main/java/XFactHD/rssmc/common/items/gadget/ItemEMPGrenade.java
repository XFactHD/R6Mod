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
import XFactHD.rssmc.common.entity.gadget.EntityEMPGrenade;
import XFactHD.rssmc.common.items.ItemBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemEMPGrenade extends ItemBase
{
    public ItemEMPGrenade()
    {
        super("itemEMPGrenade", 3, RainbowSixSiegeMC.CT.gadgetTab, null);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
    {
        if (!world.isRemote)
        {
            EntityEMPGrenade grenade = new EntityEMPGrenade(world, player);
            grenade.setHeadingFromThrower(player, player.rotationPitch, player.rotationYaw, 0, 1, 0);
            if (world.spawnEntity(grenade))
            {
                stack.stackSize -= 1;
                player.inventory.markDirty();
            }
        }
        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }
}
