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
import XFactHD.rssmc.common.data.EnumGadget;
import XFactHD.rssmc.common.entity.gadget.EntityNitroCell;
import XFactHD.rssmc.common.items.ItemBase;
import XFactHD.rssmc.client.util.Sounds;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.List;
import java.util.UUID;

public class ItemNitroPhone extends ItemBase
{
    public ItemNitroPhone()
    {
        super("itemNitroPhone", 1, RainbowSixSiegeMC.CT.gadgetTab, null);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> subItems)
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("nitroUUID", "");
        nbt.setBoolean("active", false);
        ItemStack stack = new ItemStack(item);
        stack.setTagCompound(nbt);
        subItems.add(stack);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
    {
        boolean hadTagCompund = stack.hasTagCompound() && stack.getTagCompound().hasKey("nitroUUID");
        if (!world.isRemote && hadTagCompund)
        {
            stack.getTagCompound().setBoolean("active", true);
            stack.getTagCompound().setInteger("wait", 10);
            Entity entity = FMLCommonHandler.instance().getMinecraftServerInstance().getEntityFromUuid(UUID.fromString(stack.getTagCompound().getString("nitroUUID")));
            if (entity instanceof EntityNitroCell)
            {
                ((EntityNitroCell)entity).boom();
            }
            world.playSound(null, player.posX, player.posY, player.posZ, Sounds.getGadgetSound(EnumGadget.NITRO_PHONE, "call"), SoundCategory.PLAYERS, 1, 1);
        }
        return stack.hasTagCompound() || hadTagCompund ? ActionResult.newResult(EnumActionResult.SUCCESS, stack) : ActionResult.newResult(EnumActionResult.PASS, stack);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected)
    {
        if (!stack.hasTagCompound() || stack.getTagCompound().getBoolean("active"))
        {
            if (stack.hasTagCompound() && stack.getTagCompound().getInteger("wait") > 0)
            {
                stack.getTagCompound().setInteger("wait", stack.getTagCompound().getInteger("wait") - 1);
            }
            else
            {
                entity.replaceItemInInventory(itemSlot, null);
            }
        }
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
        super.addInformation(stack, player, tooltip, advanced);
        if (!stack.hasTagCompound() || stack.getTagCompound().getString("nitroUUID").equals(""))
        {
            tooltip.add(new TextComponentTranslation("desc.rssmc:useless.name", TextFormatting.RED).getFormattedText());
        }
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        if (!slotChanged)
        {
            if (oldStack.getItem() == newStack.getItem())
            {
                return false;
            }
        }
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }
}