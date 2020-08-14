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
import XFactHD.rssmc.common.data.EnumGun;
import XFactHD.rssmc.common.gui.ContainerGunCraftingTable;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketGunCrafterConsumeItems implements IMessage
{
    private int gun;

    public PacketGunCrafterConsumeItems(){}

    public PacketGunCrafterConsumeItems(EnumGun gun)
    {
        this.gun = gun.ordinal();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(gun);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        gun = buf.readInt();
    }

    public static class Handler implements IMessageHandler<PacketGunCrafterConsumeItems, IMessage>
    {
        @Override
        public IMessage onMessage(PacketGunCrafterConsumeItems message, MessageContext ctx)
        {
            RainbowSixSiegeMC.proxy.addScheduledTask(new Runnable()
            {
                @Override
                public void run()
                {
                    ((ContainerGunCraftingTable)ctx.getServerHandler().playerEntity.openContainer).consumeRecipeItems(EnumGun.values()[message.gun]);
                }
            });
            return null;
        }
    }
}