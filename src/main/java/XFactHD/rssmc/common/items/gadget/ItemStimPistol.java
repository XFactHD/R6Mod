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
import XFactHD.rssmc.api.item.IAiming;
import XFactHD.rssmc.common.Content;
import XFactHD.rssmc.common.items.ItemBase;
import XFactHD.rssmc.common.net.fx.PacketHealFX;
import XFactHD.rssmc.common.utils.Utils;
import XFactHD.rssmc.common.utils.helper.ConfigHandler;
import XFactHD.rssmc.common.utils.helper.RayTraceUtils;
import XFactHD.rssmc.common.utils.utilClasses.HitData;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

//TODO: implement reload timer, maybe rework aiming system to use methods ItemGun uses to aim and shoot
@SuppressWarnings("ConstantConditions")
public class ItemStimPistol extends ItemBase implements IAiming
{
    public ItemStimPistol()
    {
        super("item_stim_pistol", 1, RainbowSixSiegeMC.CT.gadgetTab, null);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
    {
        if (hand != EnumHand.MAIN_HAND) { return ActionResult.newResult(EnumActionResult.PASS, stack); }
        if (!world.isRemote)
        {
            if (!ConfigHandler.holdToAim)
            {
                if (!stack.hasTagCompound()) { stack.setTagCompound(new NBTTagCompound()); player.inventory.markDirty(); }
                stack.getTagCompound().setBoolean("aiming", !stack.getTagCompound().getBoolean("aiming"));
                player.inventory.markDirty();
            }
        }
        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public boolean onItemLeftClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (!stack.hasTagCompound()) { stack.setTagCompound(new NBTTagCompound()); }
        player.inventory.markDirty();
        if (!stack.getTagCompound().getBoolean("loaded"))
        {
            if (!world.isRemote)
            {
                int slot = Utils.getSlotFor(player.inventory.mainInventory, new ItemStack(Content.itemStimDart));
                stack.getTagCompound().setBoolean("loaded", slot != -1);
                if (slot != -1)
                {
                    player.inventory.decrStackSize(slot, 1);
                    player.inventory.markDirty();
                }
            }
            return true;
        }
        if (!world.isRemote)
        {
            stack.getTagCompound().setBoolean("loaded", false);
            ArrayList<HitData> hits = RayTraceUtils.rayTraceEntities(world, player, Utils.getPlayerPosition(player), player.getLookVec().normalize(), ConfigHandler.maxShootRange, 1);
            if (hits.size() == 1)
            {
                EntityLivingBase entity = hits.get(0).getVictim();
                entity.heal(8);
                if (entity instanceof EntityPlayer)
                {
                    RainbowSixSiegeMC.NET.sendMessageToClient(new PacketHealFX(), (EntityPlayer)entity);
                }
            }
        }
        return true;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        if (oldStack == null || newStack == null || oldStack.getItem() != newStack.getItem()) { return true; }
        if (!oldStack.hasTagCompound() || !newStack.hasTagCompound()) { return false; }
        return oldStack.getTagCompound().getBoolean("loaded") != newStack.getTagCompound().getBoolean("loaded") ||
               oldStack.getTagCompound().getBoolean("aiming") != newStack.getTagCompound().getBoolean("aiming");
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> subItems)
    {
        if (item == this)
        {
            ItemStack stack = new ItemStack(item);
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setBoolean("loaded", false);
            nbt.setBoolean("aiming", false);
            stack.setTagCompound(nbt);
            subItems.add(stack);
        }
    }

    @Override
    protected boolean hasLeftClickAction()
    {
        return true;
    }

    @Override
    public void setAiming(ItemStack stack, EntityPlayer player, World world, boolean aiming)
    {
        if (!stack.hasTagCompound()) { stack.setTagCompound(new NBTTagCompound()); }
        stack.getTagCompound().setBoolean("aiming", aiming);
        player.inventory.markDirty();
    }

    @Override
    public boolean isAiming(ItemStack stack, EntityPlayer player, World world)
    {
        return stack.hasTagCompound() && stack.getTagCompound().getBoolean("aiming");
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
        super.addInformation(stack, player, tooltip, advanced);
        //tooltip.add("Aiming: " + (stack.hasTagCompound() && stack.getTagCompound().getBoolean("aiming")));
        //tooltip.add("Loaded: " + (stack.hasTagCompound() && stack.getTagCompound().getBoolean("loaded")));
    }

    public boolean isLoaded(ItemStack stack)
    {
        return stack.hasTagCompound() && stack.getTagCompound().getBoolean("loaded");
    }

    public void doSelfHeal(ItemStack stack, EntityPlayer player)
    {
        if (!isLoaded(stack)) { return; }
        player.heal(8);
        stack.getTagCompound().setBoolean("loaded", false);
        player.inventory.markDirty();
        RainbowSixSiegeMC.NET.sendMessageToClient(new PacketHealFX(), player);
    }
}