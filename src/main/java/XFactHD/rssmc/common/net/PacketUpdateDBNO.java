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
import XFactHD.rssmc.api.capability.IDBNOHandler;
import XFactHD.rssmc.common.capability.dbnoHandler.DBNOEventHandler;
import XFactHD.rssmc.common.capability.dbnoHandler.DBNOHandlerStorage;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketUpdateDBNO implements IMessage
{
    private NBTTagCompound nbt;

    public PacketUpdateDBNO(NBTTagCompound nbt)
    {
        this.nbt = nbt;
    }

    public PacketUpdateDBNO(){}

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeTag(buf, nbt);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        nbt = ByteBufUtils.readTag(buf);
    }

    public static class Handler implements IMessageHandler<PacketUpdateDBNO, IMessage>
    {
        @Override
        public IMessage onMessage(PacketUpdateDBNO message, MessageContext ctx)
        {
            RainbowSixSiegeMC.proxy.addScheduledTask(new Runnable()
            {
                @Override
                @SuppressWarnings("ConstantConditions")
                public void run()
                {
                    IDBNOHandler handler = Minecraft.getMinecraft().player.getCapability(DBNOHandlerStorage.DBNO_HANDLER_CAPABILITY, null);
                    if (handler != null)
                    {
                        handler.deserializeNBT(message.nbt);
                    }
                    else
                    {
                        DBNOEventHandler.setQueuedCapData(message.nbt);
                    }
                }
            });
            return null;
        }
    }
}
