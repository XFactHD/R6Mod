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

package XFactHD.rssmc.common.utils;

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.common.blocks.misc.TileEntityGameManager;
import XFactHD.rssmc.common.data.team.ObservationManager;
import XFactHD.rssmc.common.net.PacketUpdateWorldData;
import XFactHD.rssmc.common.utils.logic.GadgetHandler;
import XFactHD.rssmc.common.data.team.Team;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import org.apache.commons.lang3.tuple.Pair;

@SuppressWarnings("ConstantConditions")
public class RSSWorldData extends WorldSavedData
{
    private static final String NAME = "rssmc:worlddata";

    private World world = null;
    private Team team1 = null;
    private Team team2 = null;
    private BlockPos gameManagerPos = null;
    private TileEntityGameManager manager = null;
    private ObservationManager observManager = null;

    public RSSWorldData()
    {
        super(NAME);
    }

    public static RSSWorldData get(World world)
    {
        MapStorage storage = world.getMapStorage();
        RSSWorldData instance = (RSSWorldData) storage.getOrLoadData(RSSWorldData.class, NAME);

        if (instance == null)
        {
            instance = new RSSWorldData();
            storage.setData(NAME, instance);
        }
        if (instance.world == null)
        {
            instance.world = world;
            if (instance.team1 != null) { instance.team1.initializeWorld(world); }
            if (instance.team2 != null) { instance.team2.initializeWorld(world); }
            if (instance.observManager == null) { instance.observManager = new ObservationManager(); }
            instance.sendToClients();
        }
        return instance;
    }

    public void addTeams(Pair<Team, Team> teams)
    {
        this.team1 = teams.getLeft();
        this.team2 = teams.getRight();
        if (team1 != null && world != null) { team1.initializeWorld(world); }
        if (team2 != null && world != null) { team2.initializeWorld(world); }
        markDirty();
    }

    public void clearTeams()
    {
        team1 = null;
        team2 = null;
        markDirty();
    }

    public Pair<Team, Team> getTeams()
    {
        return Pair.of(team1, team2);
    }

    public void setGameManagerPos(BlockPos gameManagerPos)
    {
        this.gameManagerPos = gameManagerPos;
        markDirty();
    }

    public BlockPos getGameManagerPos()
    {
        return gameManagerPos;
    }

    public TileEntityGameManager getGameManager()
    {
        if (gameManagerPos == null || world == null) { return null; }
        if (manager == null)
        {
            TileEntity te = world.getTileEntity(gameManagerPos);
            if (te instanceof TileEntityGameManager)
            {
                manager = (TileEntityGameManager)te;
            }
        }
        return manager;
    }

    private void sendToClients()
    {
        RainbowSixSiegeMC.NET.sendMessageToAllClients(new PacketUpdateWorldData(serializeNBT()));
    }

    public void sendToClient(EntityPlayer player)
    {
        RainbowSixSiegeMC.NET.sendMessageToClient(new PacketUpdateWorldData(serializeNBT()), player);
    }

    @Override
    public void markDirty()
    {
        super.markDirty();
        sendToClients();
    }

    public ObservationManager getObservationManager()
    {
        return observManager;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        if (nbt.hasKey("team1")) { team1.deserializeNBT(nbt.getCompoundTag("team1")); }
        else { team1 = null; }
        if (nbt.hasKey("team2")) { team2.deserializeNBT(nbt.getCompoundTag("team2")); }
        else { team2 = null; }

        GadgetHandler.readFromNBT(nbt.getCompoundTag("gadgets"));

        gameManagerPos = nbt.hasKey("game_manager") ? BlockPos.fromLong(nbt.getLong("game_manager")) : null;

        if (observManager == null) { observManager = new ObservationManager(); }
        observManager.deserializeNBT(nbt.getCompoundTag("observation"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        if (team1 != null) { nbt.setTag("team1", team1.serializeNBT()); }
        if (team2 != null) { nbt.setTag("team2", team2.serializeNBT()); }

        NBTTagCompound tag = new NBTTagCompound();
        GadgetHandler.writeToNBT(tag);
        nbt.setTag("gadgets", tag);

        if (gameManagerPos != null) { nbt.setLong("game_manager", gameManagerPos.toLong()); }

        nbt.setTag("observation", observManager.serializeNBT());

        return nbt;
    }
}