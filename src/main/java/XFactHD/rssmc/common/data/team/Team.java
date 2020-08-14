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

package XFactHD.rssmc.common.data.team;

import XFactHD.rssmc.common.data.EnumSide;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.lang.ref.WeakReference;
import java.util.*;

public class Team implements INBTSerializable<NBTTagCompound>
{
    private World world = null;
    private int maxPlayers;
    private EnumSide side;
    private ArrayList<UUID> players = new ArrayList<>();
    private HashMap<UUID, WeakReference<EntityPlayer>> playerEntities = new HashMap<>();

    public Team(EnumSide side, int maxPlayers)
    {
        this.side = side;
        this.maxPlayers = maxPlayers;
    }

    public void initializeWorld(World world)
    {
        this.world = world;
        if (players.size() != playerEntities.size())
        {
            playerEntities.clear();
            ArrayList<UUID> toRemove = new ArrayList<>();
            for (UUID uuid : players)
            {
                EntityPlayer player = world.getPlayerEntityByUUID(uuid);
                if (player == null)
                {
                    toRemove.add(uuid);
                    continue;
                }
                playerEntities.put(uuid, new WeakReference<>(player));
            }
            if (toRemove.size() != 0)
            {
                players.removeAll(toRemove);
            }
        }
    }

    public boolean addPlayer(EntityPlayer player)
    {
        if (players.size() < maxPlayers)
        {
            players.add(player.getUniqueID());
            playerEntities.put(player.getUniqueID(), new WeakReference<>(player));
            return true;
        }
        return false;
    }

    public void removePlayer(EntityPlayer player)
    {
        if (players.contains(player.getUniqueID()))
        {
            players.remove(player.getUniqueID());
            playerEntities.remove(player.getUniqueID());
        }
    }

    public ArrayList<UUID> getPlayers()
    {
        return players;
    }

    public HashMap<UUID, WeakReference<EntityPlayer>> getPlayerEntityMap()
    {
        return playerEntities;
    }

    public ArrayList<EntityPlayer> getScoreboardData()
    {
        ArrayList<EntityPlayer> players = new ArrayList<>();
        for (UUID uuid : getPlayerEntityMap().keySet())
        {
            players.add(getPlayerEntityMap().get(uuid).get());
        }
        players.sort(new Comparator<EntityPlayer>()
        {
            @Override
            public int compare(EntityPlayer o1, EntityPlayer o2)
            {
                return StatusController.getPoints(o2) - StatusController.getPoints(o1);
            }
        });
        return players;
    }

    public EnumSide getSide()
    {
        return side;
    }

    public boolean isPlayerInTeam(EntityPlayer player)
    {
        return players.contains(player.getUniqueID());
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("side", side.ordinal());
        nbt.setInteger("maxPlayers", maxPlayers);
        NBTTagList list = new NBTTagList();
        for (UUID uuid : players)
        {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("uuid", uuid.toString());
            list.appendTag(tag);
        }
        nbt.setTag("players", list);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        side = EnumSide.values()[nbt.getInteger("side")];
        maxPlayers = nbt.getInteger("maxPlayers");
        NBTTagList list = nbt.getTagList("players", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++)
        {
            UUID uuid = UUID.fromString(list.getCompoundTagAt(i).getString("uuid"));
            players.add(uuid);
            if (world != null)
            {
                playerEntities.put(uuid, new WeakReference<>(world.getPlayerEntityByUUID(uuid)));
            }
        }
    }
}