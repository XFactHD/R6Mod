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

import XFactHD.rssmc.api.item.ISpecialLeftClick;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketLeftClickItem implements IMessage
{
    private boolean start;

    public PacketLeftClickItem(){}

    public PacketLeftClickItem(boolean start)
    {
        this.start = start;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeBoolean(start);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        start = buf.readBoolean();
    }

    public static class Handler implements IMessageHandler<PacketLeftClickItem, IMessage>
    {
        @Override
        public IMessage onMessage(PacketLeftClickItem message, MessageContext ctx)
        {
            ((WorldServer)ctx.getServerHandler().playerEntity.world).addScheduledTask(new Runnable()
            {
                @Override
                public void run()
                {
                    EntityPlayer player = ctx.getServerHandler().playerEntity;
                    //for (EnumHand hand : EnumHand.values()) //TODO: implement for offhand if needed
                    {
                        ItemStack stack = player.inventory.getStackInSlot(player.inventory.currentItem);
                        if (stack != null && stack.getItem() instanceof ISpecialLeftClick)
                        {
                            if (message.start)
                            {
                                ((ISpecialLeftClick)stack.getItem()).startLeftClick(stack, player, player.world, EnumHand.MAIN_HAND);
                            }
                            else
                            {
                                ((ISpecialLeftClick)stack.getItem()).stopLeftClick(stack, player, player.world, EnumHand.MAIN_HAND);
                            }
                        }
                    }
                }
            });
            return null;
        }
    }
}