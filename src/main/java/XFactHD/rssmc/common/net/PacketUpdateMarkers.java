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
import XFactHD.rssmc.client.renderer.world.InWorldRenderHandler;
import XFactHD.rssmc.common.data.EnumSide;
import XFactHD.rssmc.common.utils.utilClasses.Marker;
import XFactHD.rssmc.common.utils.utilClasses.MarkerType;
import XFactHD.rssmc.common.utils.utilClasses.Position;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.HashMap;

public class PacketUpdateMarkers implements IMessage
{
    private NBTTagCompound nbt;

    public PacketUpdateMarkers() {}

    public PacketUpdateMarkers(NBTTagCompound nbt)
    {
        this.nbt = nbt;
    }

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

    public static class Handler implements IMessageHandler<PacketUpdateMarkers, IMessage>
    {
        @Override
        public IMessage onMessage(PacketUpdateMarkers message, MessageContext ctx)
        {
            RainbowSixSiegeMC.proxy.addScheduledTask(new Runnable()
            {
                @Override
                public void run()
                {
                    HashMap<Position, Marker> markers = new HashMap<>();
                    NBTTagList list = message.nbt.getTagList("markers", Constants.NBT.TAG_COMPOUND);
                    for (int i = 0; i < list.tagCount(); i++)
                    {
                        NBTTagCompound tag = list.getCompoundTagAt(i);
                        Position pos = new Position(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"));
                        EnumSide side = tag.getInteger("side") == -1 ? null : EnumSide.values()[tag.getInteger("side")];
                        markers.put(pos, new Marker(0, side, MarkerType.values()[tag.getInteger("type")], null));
                    }
                    InWorldRenderHandler.updateMarkers(markers);
                }
            });
            return null;
        }
    }
}