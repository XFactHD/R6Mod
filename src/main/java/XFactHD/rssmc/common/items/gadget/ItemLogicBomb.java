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
import XFactHD.rssmc.api.item.ISpecialRightClick;
import XFactHD.rssmc.common.Content;
import XFactHD.rssmc.common.data.team.ObservationManager;
import XFactHD.rssmc.common.data.team.StatusController;
import XFactHD.rssmc.common.data.team.Team;
import XFactHD.rssmc.common.entity.camera.AbstractEntityCamera;
import XFactHD.rssmc.common.items.ItemBase;
import XFactHD.rssmc.common.net.PacketSetViewPoint;
import XFactHD.rssmc.common.utils.RSSWorldData;
import XFactHD.rssmc.common.utils.Utils;
import XFactHD.rssmc.common.utils.helper.LogHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ItemLogicBomb extends ItemBase implements ISpecialRightClick, IItemUsageTimer, ICooldown //TODO: find a better way to enter into cams with this as it is making the colldown check a pain in the ass
{
    private static final ItemStack PHONE_SAMPLE = new ItemStack(Content.itemPhone);
    private static final int HACKING_DURATION = 140; //TODO: find real values
    public static final int RINGING_DURATION = 140;
    private static final int RINGING_COOLDOWN = 600;

    public ItemLogicBomb()
    {
        super("item_logic_bomb", 1, RainbowSixSiegeMC.CT.gadgetTab, null);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void startRightClick(ItemStack stack, EntityPlayer player, World world, EnumHand hand)
    {
        stack.getTagCompound().setLong("stamp", world.getTotalWorldTime());
        boolean lookingAtPhone = false;
        //TODO: determine if the player is looking at a phone
        if (lookingAtPhone) { stack.getTagCompound().setString("action", "hacking"); }
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void stopRightClick(ItemStack stack, EntityPlayer player, World world, EnumHand hand)
    {
        if (!world.isRemote && stack.getTagCompound().getLong("stamp") != 0)
        {
            if (world.getTotalWorldTime() - stack.getTagCompound().getLong("stamp") < 4)
            {
                ObservationManager manager = RSSWorldData.get(world).getObservationManager();
                if (manager.isInCam(player))
                {
                    manager.removePlayerFromCamFeeds(player);
                    RainbowSixSiegeMC.NET.sendMessageToClient(new PacketSetViewPoint(null), player);
                }
                else
                {
                    ArrayList<AbstractEntityCamera> cameras = manager.getObservableCameras(player);
                    if (!cameras.isEmpty())
                    {
                        AbstractEntityCamera camera = cameras.get(0);
                        camera.addViewer(player.getUniqueID());
                        RainbowSixSiegeMC.NET.sendMessageToClient(new PacketSetViewPoint(camera.getUniqueID()), player);
                    }
                }
            }
            else if (stack.getTagCompound().getString("action").equals("hacking") && world.getTotalWorldTime() - stack.getTagCompound().getLong("stamp") >= HACKING_DURATION)
            {
                ObservationManager manager = RSSWorldData.get(world).getObservationManager();
                manager.hackCams();
            }
            else if (stack.getTagCompound().getString("action").equals("ringing") && world.getTotalWorldTime() - stack.getTagCompound().getLong("stamp") >= RINGING_DURATION)
            {
                Team deffenders = StatusController.getDeffenders(world);
                if (deffenders != null)
                {
                    for (EntityPlayer enemy : deffenders.getPlayersForScoreboard())
                    {
                        int slot = Utils.getSlotFor(enemy.inventory.mainInventory, PHONE_SAMPLE);
                        if (slot != -1)
                        {
                            ItemStack phone = enemy.inventory.getStackInSlot(slot);
                            if (phone != null && phone.getItem() instanceof ItemPhone)
                            {
                                ((ItemPhone)phone.getItem()).hackPhone(phone, world, enemy);
                            }
                        }
                    }
                }
                stack.getTagCompound().setLong("time", world.getTotalWorldTime());
                stack.getTagCompound().setInteger("uses", stack.getTagCompound().getInteger("uses") - 1);
            }
        }
        stack.getTagCompound().setLong("stamp", 0);
        stack.getTagCompound().setInteger("timer", 0);
        stack.getTagCompound().setString("action", "");
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected)
    {
        if (!world.isRemote)
        {
            if (!stack.hasTagCompound()) { stack.setTagCompound(new NBTTagCompound()); stack.getTagCompound().setInteger("uses", 2); }
            if (stack.getTagCompound().getLong("stamp") != 0)
            {
                if (world.getTotalWorldTime() % 4 == 0) { stack.getTagCompound().setLong("timer", world.getTotalWorldTime()); }
                long clickDuration = world.getTotalWorldTime() - stack.getTagCompound().getLong("stamp");
                if (clickDuration > 4 && stack.getTagCompound().getString("action").equals(""))
                {
                    long cooldownTime = world.getTotalWorldTime() - stack.getTagCompound().getLong("time");
                    if (cooldownTime > RINGING_COOLDOWN && stack.getTagCompound().getInteger("uses") > 0)
                    {
                        stack.getTagCompound().setString("action", "ringing");
                    }
                    else
                    {
                        stack.getTagCompound().setLong("stamp", 0);
                        stack.getTagCompound().setInteger("timer", 0);
                        stack.getTagCompound().setString("action", "");
                    }
                }
                else if (world.getTotalWorldTime() - stack.getTagCompound().getLong("stamp") > HACKING_DURATION && stack.getTagCompound().getString("action").equals("ringing"))
                {
                    stopRightClick(stack, (EntityPlayer)entity, world, EnumHand.MAIN_HAND);
                }
                else if (world.getTotalWorldTime() - stack.getTagCompound().getLong("stamp") > RINGING_DURATION && stack.getTagCompound().getString("action").equals("hacking"))
                {
                    stopRightClick(stack, (EntityPlayer)entity, world, EnumHand.MAIN_HAND);
                }
            }
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }

    @Override
    public String getDescription()
    {
        return "desc.rssmc:hacking_phones.name";
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public int getCurrentTime(World world, ItemStack stack, EntityPlayer player)
    {
        return (int)(world.getTotalWorldTime() - stack.getTagCompound().getLong("stamp"));
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public int getMaxTime(ItemStack stack)
    {
        return stack.getTagCompound().getString("action").equals("hacking") ? HACKING_DURATION : RINGING_DURATION;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean isInUse(World world, ItemStack stack, EntityPlayer player)
    {
        return stack.hasTagCompound() && stack.getTagCompound().getLong("stamp") != 0 && !stack.getTagCompound().getString("action").equals("");
    }

    @Override
    public void scrollOff(ItemStack stack, EntityPlayer player, World world, EnumHand hand)
    {
        stopRightClick(stack, player, world, hand);
    }

    @Override
    public int getCooldownTime(ItemStack stack)
    {
        return RINGING_COOLDOWN;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
        super.addInformation(stack, player, tooltip, advanced);
        String s = I18n.format("desc.rssmc:uses_left.name");
        s += " ";
        s += stack.hasTagCompound() ? Integer.toString(stack.getTagCompound().getInteger("uses")) : 2;
        tooltip.add(s);
    }
}