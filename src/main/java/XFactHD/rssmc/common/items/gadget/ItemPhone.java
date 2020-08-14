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
import XFactHD.rssmc.api.item.IItemUsageTimer;
import XFactHD.rssmc.api.item.ISpecialRightClick;
import XFactHD.rssmc.client.util.Sounds;
import XFactHD.rssmc.common.data.EnumGadget;
import XFactHD.rssmc.common.data.team.ObservationManager;
import XFactHD.rssmc.common.entity.camera.AbstractEntityCamera;
import XFactHD.rssmc.common.items.ItemBase;
import XFactHD.rssmc.common.net.PacketSetViewPoint;
import XFactHD.rssmc.common.utils.RSSWorldData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

import java.util.ArrayList;

public class ItemPhone extends ItemBase implements ISpecialRightClick, IItemUsageTimer
{
    public ItemPhone()
    {
        super("item_phone", 1, RainbowSixSiegeMC.CT.gadgetTab, null);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void startRightClick(ItemStack stack, EntityPlayer player, World world, EnumHand hand)
    {
        if (!world.isRemote)
        {
            if (stack.getTagCompound().getBoolean("hacked"))
            {
                //TODO: implement holding to reset with IItemUsageTimer
            }
            else
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
        }
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected)
    {
        if (!stack.hasTagCompound()) { stack.setTagCompound(new NBTTagCompound()); }
        if (stack.getTagCompound().getBoolean("hacked") && !world.isRemote)
        {
            if (world.getTotalWorldTime() - stack.getTagCompound().getLong("ringStamp") > 60) //TODO: replace 60 with the length of the ring sound
            {
                world.playSound(entity.posX, entity.posY, entity.posZ, Sounds.getGadgetSound(EnumGadget.PHONE, "ring"), SoundCategory.PLAYERS, 1, 1, false);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void hackPhone(ItemStack stack, World world, EntityPlayer owner)
    {
        stack.getTagCompound().setBoolean("hacked", true);
        owner.inventory.markDirty();
    }

    @Override
    public void stopRightClick(ItemStack stack, EntityPlayer player, World world, EnumHand hand) {}

    @Override
    public void scrollOff(ItemStack stack, EntityPlayer player, World world, EnumHand hand) {}

    @Override
    public int getCurrentTime(World world, ItemStack stack, EntityPlayer player)
    {
        return 0;
    }

    @Override
    public boolean isInUse(World world, ItemStack stack, EntityPlayer player)
    {
        return false;
    }

    @Override
    public int getMaxTime(ItemStack stack)
    {
        return 0;
    }

    @Override
    public String getDescription()
    {
        return null;
    }
}