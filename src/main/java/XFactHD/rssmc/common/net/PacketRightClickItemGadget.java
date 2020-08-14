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

package XFactHD.rssmc.common.net;

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.common.data.EnumGadget;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketRightClickItemGadget implements IMessage
{
    private EnumGadget gadget;

    public PacketRightClickItemGadget() {}

    public PacketRightClickItemGadget(EnumGadget gadget)
    {
        this.gadget = gadget;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(gadget.ordinal());
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        gadget = EnumGadget.values()[buf.readInt()];
    }

    public static class Handler implements IMessageHandler<PacketRightClickItemGadget, IMessage>
    {
        @Override
        public IMessage onMessage(PacketRightClickItemGadget message, MessageContext ctx)
        {
            RainbowSixSiegeMC.proxy.addScheduledTask(new Runnable()
            {
                @Override
                public void run()
                {
                    EntityPlayer player = ctx.getServerHandler().playerEntity;
                    ItemStack stack = player.getHeldItemMainhand();
                    if (stack != null && stack.getItem() == message.gadget.getGadgetItem())
                    {
                        stack.getItem().onItemRightClick(stack, player.world, player, EnumHand.MAIN_HAND);
                    }
                }
            });
            return null;
        }
    }
}