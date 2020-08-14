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

package XFactHD.rssmc.common.net.fx;

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.common.data.EnumParticle;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSpawnParticle implements IMessage
{
    public EnumParticle particle;
    public double posX;
    public double posY;
    public double posZ;

    public PacketSpawnParticle(){}

    public PacketSpawnParticle(EnumParticle particle, double posX, double posY, double posZ)
    {
        this.particle = particle;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(particle.ordinal());
        buf.writeDouble(posX);
        buf.writeDouble(posY);
        buf.writeDouble(posZ);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        particle = EnumParticle.values()[buf.readInt()];
        posX = buf.readDouble();
        posY = buf.readDouble();
        posZ = buf.readDouble();
    }

    public static class Handler implements IMessageHandler<PacketSpawnParticle, IMessage>
    {
        @Override
        public IMessage onMessage(PacketSpawnParticle message, MessageContext ctx)
        {
            RainbowSixSiegeMC.proxy.handleClientPacket(message, ctx);
            return null;
        }
    }
}