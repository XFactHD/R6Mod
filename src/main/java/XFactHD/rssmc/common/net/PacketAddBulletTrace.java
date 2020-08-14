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
import XFactHD.rssmc.common.utils.utilClasses.Position;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketAddBulletTrace implements IMessage
{
    public Position pos;
    public Vec3d startVec, endVec;

    public PacketAddBulletTrace() {}

    public PacketAddBulletTrace(Position pos, Vec3d startVec, Vec3d endVec)
    {
        this.pos = pos;
        this.startVec = startVec;
        this.endVec = endVec;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeDouble(pos.getX());
        buf.writeDouble(pos.getY());
        buf.writeDouble(pos.getZ());
        buf.writeDouble(startVec.xCoord);
        buf.writeDouble(startVec.yCoord);
        buf.writeDouble(startVec.zCoord);
        buf.writeDouble(endVec.xCoord);
        buf.writeDouble(endVec.yCoord);
        buf.writeDouble(endVec.zCoord);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        pos = new Position(buf.readDouble(), buf.readDouble(), buf.readDouble());
        startVec = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        endVec = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public static class Handler implements IMessageHandler<PacketAddBulletTrace, IMessage>
    {
        @Override
        public IMessage onMessage(PacketAddBulletTrace message, MessageContext ctx)
        {
            RainbowSixSiegeMC.proxy.handleClientPacket(message, ctx);
            return null;
        }
    }
}