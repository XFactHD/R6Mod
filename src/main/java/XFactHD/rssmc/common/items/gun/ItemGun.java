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

package XFactHD.rssmc.common.items.gun;

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.api.item.ISpecialLeftClick;
import XFactHD.rssmc.api.capability.IGunHandler;
import XFactHD.rssmc.api.item.ISpecialRightClick;
import XFactHD.rssmc.common.capability.gunHandler.GunHandler;
import XFactHD.rssmc.common.capability.gunHandler.GunHandlerStorage;
import XFactHD.rssmc.common.data.EnumAttachment;
import XFactHD.rssmc.common.data.EnumGun;
import XFactHD.rssmc.common.items.ItemBase;
import XFactHD.rssmc.common.utils.logic.GadgetHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

@SuppressWarnings("ConstantConditions") //TODO: set player to null when the item is dropped
public class ItemGun extends ItemBase implements ISpecialRightClick, ISpecialLeftClick
{
    public ItemGun()
    {
        super("itemGun", 1, RainbowSixSiegeMC.CT.gunTab, EnumGun.getGunsAsStringArray());
    }

    @Override
    public void startRightClick(ItemStack stack, EntityPlayer player, World world, EnumHand hand)
    {
        IGunHandler gunHandler = stack.getCapability(GunHandlerStorage.GUN_HANDLER_CAPABILITY, null);
        if (!world.isRemote)
        {
            gunHandler.setAiming(true);
        }
    }

    @Override
    public void stopRightClick(ItemStack stack, EntityPlayer player, World world, EnumHand hand)
    {
        IGunHandler gunHandler = stack.getCapability(GunHandlerStorage.GUN_HANDLER_CAPABILITY, null);
        if (!world.isRemote)
        {
            gunHandler.setAiming(false);
        }
    }

    @Override
    public void startLeftClick(ItemStack stack, EntityPlayer player, World world, EnumHand hand)
    {
        IGunHandler gunHandler = stack.getCapability(GunHandlerStorage.GUN_HANDLER_CAPABILITY, null);
        if (!world.isRemote)
        {
            gunHandler.setFiring(true);
        }
    }

    @Override
    public void stopLeftClick(ItemStack stack, EntityPlayer player, World world, EnumHand hand)
    {
        IGunHandler gunHandler = stack.getCapability(GunHandlerStorage.GUN_HANDLER_CAPABILITY, null);
        if (!world.isRemote)
        {
            gunHandler.setFiring(false);
        }
    }

    @Override
    public void scrollOff(ItemStack stack, EntityPlayer player, World world, EnumHand hand)
    {
        IGunHandler gunHandler = stack.getCapability(GunHandlerStorage.GUN_HANDLER_CAPABILITY, null);
        if (!world.isRemote)
        {
            gunHandler.setAiming(false);
            gunHandler.setFiring(false);
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        return slotChanged;
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entity, ItemStack stack)
    {
        return true;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt)
    {
        return new GunHandler(stack, nbt);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected)
    {
        if (!(entity instanceof EntityPlayer)) { return; }
        IGunHandler gunHandler = stack.getCapability(GunHandlerStorage.GUN_HANDLER_CAPABILITY, null);
        if (gunHandler.getPlayer() == null) { gunHandler.setPlayer((EntityPlayer)entity); }
        if (!world.isRemote && entity instanceof EntityPlayer)
        {
            gunHandler.update(world, (EntityPlayer)entity, slot, isSelected);
            if (!isSelected && (gunHandler.isAiming() || gunHandler.isFiring()))
            {
                scrollOff(stack, ((EntityPlayer)entity), world, EnumHand.MAIN_HAND);
            }

            if (stack.getMetadata() == EnumGun.PRB92.ordinal() && !isSelected && GadgetHandler.getHandlerForPlayer((EntityPlayer)entity).getSilentStep())
            {
                GadgetHandler.getHandlerForPlayer((EntityPlayer)entity).setSilentStep(false);
            }
        }
    }

    @Override
    public boolean canHarvestBlock(IBlockState state, ItemStack stack)
    {
        return false;
    }

    @Override
    public float getStrVsBlock(ItemStack stack, IBlockState state)
    {
        return 0;
    }

    public void reload(ItemStack stack, EntityPlayer player)
    {
        stack.getCapability(GunHandlerStorage.GUN_HANDLER_CAPABILITY, null).setReloading(true, player);
    }

    public static boolean hasAttachment(ItemStack stack, EnumAttachment attachment)
    {
        return stack.getCapability(GunHandlerStorage.GUN_HANDLER_CAPABILITY, null).hasAttachment(attachment);
    }
}