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
import XFactHD.rssmc.common.utils.RSSWorldData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSwitchCamera implements IMessage
{
    private boolean right;
    private boolean left;

    public PacketSwitchCamera() {}

    public PacketSwitchCamera(boolean right, boolean left)
    {
        this.right = right;
        this.left = left;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeBoolean(right);
        buf.writeBoolean(left);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        right = buf.readBoolean();
        left = buf.readBoolean();
    }

    public static class Handler implements IMessageHandler<PacketSwitchCamera, IMessage>
    {
        @Override
        public IMessage onMessage(PacketSwitchCamera message, MessageContext ctx)
        {
            RainbowSixSiegeMC.proxy.addScheduledTask(new Runnable()
            {
                @Override
                public void run()
                {
                    EntityPlayer player = ctx.getServerHandler().playerEntity;
                    if (message.right)
                    {
                        RSSWorldData.get(player.world).getObservationManager().moveToNextCam(player);
                    }
                    else if (message.left)
                    {
                        RSSWorldData.get(player.world).getObservationManager().moveToPriorCam(player);
                    }
                }
            });
            return null;
        }
    }
}