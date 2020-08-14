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

package XFactHD.rssmc.client;

import XFactHD.rssmc.client.util.wrappers.RSSPlayerControllerMP;
import XFactHD.rssmc.common.capability.gunHandler.GunHandlerStorage;
import XFactHD.rssmc.common.items.gun.ItemGun;
import XFactHD.rssmc.common.utils.logic.GadgetHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ClientEventHandler
{
    public static boolean viewBobbing = false;

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START && player() != null)
        {
            if (!(mc().playerController instanceof RSSPlayerControllerMP))
            {
                mc().playerController = new RSSPlayerControllerMP(mc(), mc().getConnection());
            }

            EntityPlayer player = player();
            NBTTagCompound nbt = player.getEntityData();
            if (nbt.getBoolean("tracked"))
            {
                if (world().getTotalWorldTime() - nbt.getLong("timestamp") >= 100)
                {
                    int trackingsLeft = nbt.getInteger("trackingsLeft");
                    nbt.setInteger("trackingsLeft", trackingsLeft -1);
                }
            }

            ItemStack stack = player.inventory.getCurrentItem();
            if (stack != null && stack.getItem() instanceof ItemGun && stack.getCapability(GunHandlerStorage.GUN_HANDLER_CAPABILITY, null).isAiming() && mc().gameSettings.viewBobbing)
            {
                viewBobbing = mc().gameSettings.viewBobbing;
                mc().gameSettings.setOptionValue(GameSettings.Options.VIEW_BOBBING, 0);
            }
            else if (viewBobbing && (stack == null || !(stack.getItem() instanceof ItemGun) || !stack.getCapability(GunHandlerStorage.GUN_HANDLER_CAPABILITY, null).isAiming()))
            {
                mc().gameSettings.setOptionValue(GameSettings.Options.VIEW_BOBBING, 0);
                viewBobbing = false;
            }
        }
    }

    @SubscribeEvent
    public void clientTickPlayer(TickEvent.PlayerTickEvent event)
    {
        if (event.player.world.isRemote)
        {
            GadgetHandler.tickPlayersHandler(event.player);
        }
    }

    private Minecraft mc()
    {
        return Minecraft.getMinecraft();
    }

    private World world()
    {
        return mc().world;
    }

    private EntityPlayer player()
    {
        return mc().player;
    }
}