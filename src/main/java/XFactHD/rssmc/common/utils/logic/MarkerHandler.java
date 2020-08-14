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

package XFactHD.rssmc.common.utils.logic;

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.common.data.EnumSide;
import XFactHD.rssmc.common.data.team.StatusController;
import XFactHD.rssmc.common.net.PacketUpdateMarkers;
import XFactHD.rssmc.common.utils.Utils;
import XFactHD.rssmc.common.utils.helper.RayTraceUtils;
import XFactHD.rssmc.common.utils.utilClasses.Marker;
import XFactHD.rssmc.common.utils.utilClasses.MarkerType;
import XFactHD.rssmc.common.utils.utilClasses.Position;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.HashMap;

public class MarkerHandler
{
    private static HashMap<Position, Marker> markers = new HashMap<>();
    private static int ticks = 0;

    public static void tick()
    {
        ticks = FMLCommonHandler.instance().getMinecraftServerInstance().getTickCounter();
        for (Position pos : markers.keySet())
        {
            Marker marker = markers.get(pos);
            if (ticks - marker.getTimestamp() >= (marker.getType() == MarkerType.JACKAL ? 100 : 160))
            {
                markers.remove(pos);
                sendUpdateToClients();
            }
        }
    }

    /**
     * @param pos The position of the marker, will be computed of null, method will return early if computed value is null
     * @param side The side of the player who set the marker, will be computed if null
     * @param player The player who set the marker
     */
    public static void addPlayerMarker(Position pos, EnumSide side, EntityPlayer player)
    {
        if (pos == null) { pos = RayTraceUtils.rayTraceMarker(player.world, player); }
        if (pos == null) { return; }
        if (side == null) { side = StatusController.getPlayersSide(player); }
        if (getMarkerForPlayer(player, MarkerType.PLAYER) == null)
        {
            markers.put(pos, new Marker(ticks, side, MarkerType.PLAYER, player.getUniqueID()));
            sendUpdateToClients();
        }
    }

    /**
     * @param pos The position of the marker, will be computed of null
     * @param side The side of the player who marked with a cam or drone, will be computed if null
     * @param player The player who was marked by the cam or drone
     */
    public static void addCameraMarker(Position pos, EnumSide side, EntityPlayer player)
    {
        if (pos == null) { pos = new Position(player.posX, player.posY, player.posZ); }
        if (side == null)
        {
            side = StatusController.getPlayersSide(player);
            if (side != null) { side = side.getOpposite(); }
        }
        Position mark = getMarkerForPlayer(player, MarkerType.CAMERA);
        if (mark != null)
        {
            markers.remove(mark);
        }
        markers.put(pos, new Marker(ticks, side, MarkerType.CAMERA, player.getUniqueID()));
        sendUpdateToClients();
    }

    /**
     * @param pos The position of the marker, will be computed of null
     * @param player The player who was tracked by Jackal
     */
    public static void addJackalMarker(Position pos, EntityPlayer player)
    {
        if (pos == null) { pos = Utils.getPlayerPosition(player); pos.add(0, 1, 0); }
        markers.put(pos, new Marker(ticks, EnumSide.ATTACKER, MarkerType.JACKAL, player.getUniqueID()));
        sendUpdateToClients();
    }

    //Returns the last camera marker of the player or the last marker the player set
    public static Position getMarkerForPlayer(EntityPlayer player, MarkerType type)
    {
        for (Position pos : markers.keySet())
        {
            if (markers.get(pos).getPlayer().equals(player.getUniqueID()) && markers.get(pos).getType() == type)
            {
                return pos;
            }
        }
        return null;
    }

    private static void sendUpdateToClients()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList list = new NBTTagList();
        for (Position pos : markers.keySet())
        {
            Marker marker = markers.get(pos);
            NBTTagCompound tag = new NBTTagCompound();
            tag.setDouble("x", pos.getX());
            tag.setDouble("y", pos.getY());
            tag.setDouble("z", pos.getZ());
            tag.setInteger("side", marker.getSide() == null ? -1 : marker.getSide().ordinal());
            tag.setInteger("type", marker.getType().ordinal());
            list.appendTag(tag);
        }
        nbt.setTag("markers", list);
        RainbowSixSiegeMC.NET.sendMessageToAllClients(new PacketUpdateMarkers(nbt));
    }
}