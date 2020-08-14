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
import XFactHD.rssmc.common.Content;
import XFactHD.rssmc.common.blocks.building.TileEntityReinforcement;
import XFactHD.rssmc.common.utils.utilClasses.DataCallable;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import XFactHD.rssmc.common.utils.propertyEnums.Connection;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

@SuppressWarnings("unused")
public class PacketSetBlockToReinforce implements IMessage
{
    private BlockPos pos;
    private EnumFacing side;
    private Connection con;
    private NBTTagCompound data;

    public PacketSetBlockToReinforce(){}

    public PacketSetBlockToReinforce(BlockPos pos, IBlockState state, EnumFacing side, Connection con)
    {
        data = new NBTTagCompound();
        data.setInteger("x", pos.getX());
        data.setInteger("y", pos.getY());
        data.setInteger("z", pos.getZ());
        data.setString("blockName", state.getBlock().getRegistryName().toString());
        data.setInteger("blockMeta", state.getBlock().getMetaFromState(state));
        data.setInteger("side", side.getIndex());
        data.setInteger("con", con.ordinal());
    }

    @Override //FIXME: gets spammed when in a camera
    public void fromBytes(ByteBuf buf)
    {
        try
        {
            data = ByteBufUtils.readTag(buf);
            pos = new BlockPos(data.getInteger("x"), data.getInteger("y"), data.getInteger("z"));
            side = EnumFacing.getFront(data.getInteger("side"));
            con = Connection.valueOf(data.getInteger("con"));
        }
        catch (Exception e) {}
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeTag(buf, data);
    }

    public static class Handler implements IMessageHandler<PacketSetBlockToReinforce, IMessage>
    {
        @Override
        public IMessage onMessage(final PacketSetBlockToReinforce message, final MessageContext ctx)
        {
            RainbowSixSiegeMC.proxy.addScheduledTask(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        setBlockToReinforce(message, FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld());
                    }
                    catch (Exception e) {}
                }
            });
            return null;
        }

        //FIXME: gets spammed when in a camera
        private void setBlockToReinforce(PacketSetBlockToReinforce message, World world)
        {
            IBlockState state = Content.blockReinforcement.getDefaultState().
                    withProperty(PropertyHolder.FACING_NOT_UP, message.side.getOpposite()).
                    withProperty(PropertyHolder.REINFORCEMENT_CONNECTION, message.con);

            world.setBlockState(message.pos, state, 3);
            TileEntityReinforcement.dataGetters.put(message.pos, new DataCallable(message.data));
        }
    }
}
