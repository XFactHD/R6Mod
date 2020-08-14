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
import XFactHD.rssmc.common.entity.gadget.EntityGasCanister;
import XFactHD.rssmc.common.items.ItemBase;
import XFactHD.rssmc.common.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.ArrayList;

public class ItemGasCanister extends ItemBase
{
    private static final ItemStack TEST_STACK = new ItemStack(Content.itemActivator);

    public ItemGasCanister()
    {
        super("itemGasCanister", 3, RainbowSixSiegeMC.CT.gadgetTab, null);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
    {
        ArrayList<Integer> slots = Utils.getSlotsFor(player.inventory.mainInventory, TEST_STACK);
        if (!slots.isEmpty())
        {
            for (int slot : slots)
            {
                ItemStack test = player.inventory.getStackInSlot(slot);
                if (test != null && test.hasTagCompound() && test.getTagCompound().getString("object").equals("gas_can"))
                {
                    //Abort early if there is an active gas canister in the world //TODO: add this to other items, too
                    return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
                }
            }
        }
        if (!world.isRemote)
        {
            EntityGasCanister grenade = new EntityGasCanister(world, player);
            grenade.setHeadingFromThrower(player, player.rotationPitch, player.rotationYaw, 0, 1, 0);
            if (world.spawnEntity(grenade))
            {
                stack.stackSize -= 1;
                ItemStack activator = new ItemStack(Content.itemActivator);
                activator.setTagCompound(new NBTTagCompound());
                activator.getTagCompound().setString("object", "gas_can");
                activator.getTagCompound().setString("uuid", grenade.getUniqueID().toString());
                player.inventory.addItemStackToInventory(activator);
                player.inventory.markDirty();
            }
        }
        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }
}
