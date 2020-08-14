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
import XFactHD.rssmc.common.blocks.gadget.TileEntityActiveDefenseSystem;
import XFactHD.rssmc.common.utils.utilClasses.Position;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketADSAnimation implements IMessage
{
    private BlockPos pos;
    private Position position;

    public PacketADSAnimation() {}

    public PacketADSAnimation(BlockPos pos, Position position)
    {
        this.pos = pos;
        this.position = position;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setLong("pos", pos.toLong());
        position.serialize(nbt);
        ByteBufUtils.writeTag(buf, nbt);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        NBTTagCompound nbt = ByteBufUtils.readTag(buf);
        pos = BlockPos.fromLong(nbt.getLong("pos"));
        position = Position.deserialize(nbt);
    }

    public static class Handler implements IMessageHandler<PacketADSAnimation, IMessage>
    {
        @Override
        public IMessage onMessage(PacketADSAnimation message, MessageContext ctx)
        {
            RainbowSixSiegeMC.proxy.addScheduledTask(new Runnable()
            {
                @Override
                public void run()
                {
                    TileEntity te = Minecraft.getMinecraft().world.getTileEntity(message.pos);
                    if (te instanceof TileEntityActiveDefenseSystem)
                    {
                        ((TileEntityActiveDefenseSystem)te).startAnimation(message.position);
                    }
                }
            });
            return null;
        }
    }
}