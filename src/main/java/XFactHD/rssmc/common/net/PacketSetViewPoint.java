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
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class PacketSetViewPoint implements IMessage
{
    public UUID uuid;

    public PacketSetViewPoint() {}

    public PacketSetViewPoint(UUID uuid)
    {
        this.uuid = uuid;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, uuid != null ? uuid.toString() : "-");
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        String s = ByteBufUtils.readUTF8String(buf);
        uuid = s.equals("-") ? null : UUID.fromString(s);
    }

    public static class Handler implements IMessageHandler<PacketSetViewPoint, IMessage>
    {
        @Override
        public IMessage onMessage(PacketSetViewPoint message, MessageContext ctx)
        {
            RainbowSixSiegeMC.proxy.handleClientPacket(message, ctx);
            return null;
        }
    }
}