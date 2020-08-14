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

package XFactHD.rssmc.common.utils;

import XFactHD.rssmc.common.Content;
import XFactHD.rssmc.common.data.EnumSide;
import XFactHD.rssmc.common.data.team.StatusController;
import XFactHD.rssmc.common.utils.helper.ConfigHandler;
import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class DeathEventHandler
{
    @SubscribeEvent
    public void itemDropped(PlayerDropsEvent event)
    {
        if (!ConfigHandler.battleMode || event.getEntityPlayer().world.isRemote) { return; }
        EnumSide side = StatusController.getPlayersSide(event.getEntityPlayer());
        if (side == EnumSide.DEFFENDER)
        {
            EntityItem phone = null;
            for (EntityItem item : event.getDrops())
            {
                if (item.getEntityItem().getItem() == Content.itemPhone)
                {
                    phone = item;
                    break;
                }
            }
            if (phone != null)
            {
                event.getEntityPlayer().world.spawnEntity(phone);
            }
        }
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void itemPickedUp(PlayerEvent.ItemPickupEvent event)
    {
        if (ConfigHandler.battleMode)
        {
            event.setCanceled(true);
        }
    }
}