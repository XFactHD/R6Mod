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

package XFactHD.rssmc.common.net.keybind;

import XFactHD.rssmc.common.items.gun.ItemGun;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketReloadGun extends AbstractPacketKeyBind
{
    public static class Handler implements IMessageHandler<PacketReloadGun, IMessage>
    {
        @Override
        public IMessage onMessage(PacketReloadGun message, MessageContext ctx)
        {
            ((WorldServer)ctx.getServerHandler().playerEntity.world).addScheduledTask(new Runnable()
            {
                @Override
                public void run()
                {
                    EntityPlayer player = ctx.getServerHandler().playerEntity;
                    ItemStack stack = player.inventory.getCurrentItem();
                    if (stack != null && stack.getItem() instanceof ItemGun)
                    {
                        ((ItemGun)stack.getItem()).reload(stack, player);
                    }
                }
            });
            return null;
        }
    }
}