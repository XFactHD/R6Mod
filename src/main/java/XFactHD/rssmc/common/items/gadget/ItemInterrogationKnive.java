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
import XFactHD.rssmc.api.item.ICooldown;
import XFactHD.rssmc.api.item.IItemUsageTimer;
import XFactHD.rssmc.common.items.ItemBase;
import XFactHD.rssmc.common.utils.logic.GadgetHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.UUID;

@SuppressWarnings("ConstantConditions")
public class ItemInterrogationKnive extends ItemBase implements ICooldown, IItemUsageTimer //TODO: rework to be a normal knive and special case players using caveira
{
    public ItemInterrogationKnive()
    {
        super("item_interrogation_knive", 1, RainbowSixSiegeMC.CT.gadgetTab, null);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected)
    {
        if (!(entity instanceof EntityPlayer) || !world.isRemote) { return; }
        if (!stack.hasTagCompound()) { stack.setTagCompound(new NBTTagCompound()); }
        if (!stack.getTagCompound().getString("uuid").equals(entity.getUniqueID().toString()))
        {
            stack.getTagCompound().setString("uuid", entity.getUniqueID().toString());
        }
    }

    @Override
    public float getStrVsBlock(ItemStack stack, IBlockState state)
    {
        return 0;
    }

    @Override
    public String getDescription()
    {
        return "desc.rssmc:interrogation.name";
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand)
    {
        //TODO: handle interrogation
        return true;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) { return true; }

    @Override
    public int getCurrentTime(World world, ItemStack stack, EntityPlayer player)
    {
        return 0;
    }

    @Override
    public int getMaxTime(ItemStack stack)
    {
        return 0;
    }

    @Override
    public boolean isInUse(World world, ItemStack stack, EntityPlayer player)
    {
        return false;
    }

    @Override
    public int getCurrentTime(ItemStack stack, World world)
    {
        if (!stack.hasTagCompound()) { return -1; }
        GadgetHandler handler = GadgetHandler.getHandlerForPlayer(UUID.fromString(stack.getTagCompound().getString("uuid")));
        return handler.getSilentStepTimer() == GadgetHandler.SILENT_STEP_TICKS ? -1 : handler.getSilentStepTimer();
    }

    @Override
    public int getCooldownTime(ItemStack stack)
    {
        return GadgetHandler.SILENT_STEP_TICKS;
    }

    @Override
    public int getBarColor(ItemStack stack, World world)
    {
        if (!stack.hasTagCompound()) { return -1; }
        GadgetHandler handler = GadgetHandler.getHandlerForPlayer(UUID.fromString(stack.getTagCompound().getString("uuid")));
        return handler.getSilentStepTimer() < ((double)GadgetHandler.SILENT_STEP_TICKS * .3) ? 0xFF0000 : -1;
    }
}