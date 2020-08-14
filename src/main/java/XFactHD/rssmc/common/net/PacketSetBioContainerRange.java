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
import XFactHD.rssmc.common.blocks.objective.TileEntityBioContainer;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSetBioContainerRange implements IMessage
{
    private BlockPos pos;
    private int rangeNorth;
    private int rangeEast;
    private int rangeSouth;
    private int rangeWest;
    private int rangeUp;

    public PacketSetBioContainerRange() {}

    public PacketSetBioContainerRange(BlockPos pos, int rangeNorth, int rangeEast, int rangeSouth, int rangeWest, int rangeUp)
    {
        this.pos = pos;
        this.rangeNorth = rangeNorth;
        this.rangeEast = rangeEast;
        this.rangeSouth = rangeSouth;
        this.rangeWest = rangeWest;
        this.rangeUp = rangeUp;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeLong(pos.toLong());
        buf.writeInt(rangeNorth);
        buf.writeInt(rangeEast);
        buf.writeInt(rangeSouth);
        buf.writeInt(rangeWest);
        buf.writeInt(rangeUp);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        pos = BlockPos.fromLong(buf.readLong());
        rangeNorth = buf.readInt();
        rangeEast = buf.readInt();
        rangeSouth = buf.readInt();
        rangeWest = buf.readInt();
        rangeUp = buf.readInt();
    }

    public static class Handler implements IMessageHandler<PacketSetBioContainerRange, IMessage>
    {
        @Override
        public IMessage onMessage(PacketSetBioContainerRange message, MessageContext ctx)
        {
            RainbowSixSiegeMC.proxy.addScheduledTask(new Runnable()
            {
                @Override
                public void run()
                {
                    World world = ctx.getServerHandler().playerEntity.world;
                    TileEntity te = world.getTileEntity(message.pos);
                    if (te instanceof TileEntityBioContainer)
                    {
                        if ((message.rangeEast + message.rangeWest) * (message.rangeNorth + message.rangeSouth) < 0 || message.rangeUp < 0)
                        {
                            world.destroyBlock(message.pos, false);
                        }
                        else
                        {
                            ((TileEntityBioContainer)te).setRange(message.rangeNorth, message.rangeEast, message.rangeSouth, message.rangeWest, message.rangeUp);
                        }
                    }
                }
            });
            return null;
        }
    }
}