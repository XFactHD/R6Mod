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

package XFactHD.rssmc.common.blocks.building;

import XFactHD.rssmc.common.blocks.TileEntityBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class TileEntityAmmoBox extends TileEntityBase implements ITickable
{
    private HashMap<UUID, MutablePair<Integer, Integer>> timers = new HashMap<>();

    @Override
    public void update()
    {
        if (!world.isRemote)
        {
            ArrayList<UUID> toRemove = new ArrayList<>();
            for (UUID uuid : timers.keySet())
            {
                if (timers.get(uuid).getRight() > 4 || timers.get(uuid).getLeft() >= getMaxTime())
                {
                    toRemove.add(uuid);
                }
                else
                {
                    MutablePair<Integer, Integer> pair = timers.get(uuid);
                    pair.setLeft(pair.getLeft() + 1);
                    pair.setRight(pair.getRight() + 1);
                    timers.replace(uuid, pair);
                }
            }
            for (UUID uuid : toRemove) { timers.remove(uuid); }
            if (!toRemove.isEmpty()) { notifyBlockUpdate(); }
        }
        else
        {
            for (UUID uuid : timers.keySet())
            {
                MutablePair<Integer, Integer> pair = timers.get(uuid);
                pair.setLeft(pair.getLeft() + 1);
                pair.setRight(pair.getRight() + 1);
                timers.replace(uuid, pair);
            }
        }
    }

    public boolean click(EntityPlayer player)
    {
        if (!timers.containsKey(player.getUniqueID())) { timers.put(player.getUniqueID(), new MutablePair<>(0, 0)); }
        MutablePair<Integer, Integer> pair = timers.get(player.getUniqueID());
        pair.setRight(0);
        timers.replace(player.getUniqueID(), pair);
        notifyBlockUpdate();
        return pair.getLeft() >= getMaxTime();
    }

    public int getMaxTime()
    {
        return 100;
    }

    public int getTime(EntityPlayer player)
    {
        return timers.containsKey(player.getUniqueID()) ? timers.get(player.getUniqueID()).getLeft() : -1;
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        NBTTagList list = new NBTTagList();
        for (UUID uuid : timers.keySet())
        {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setUniqueId("uuid", uuid);
            tag.setInteger("time", timers.get(uuid).getLeft());
            tag.setInteger("timeLastClick", timers.get(uuid).getRight());
            list.appendTag(tag);
        }
        nbt.setTag("timers", list);
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        timers.clear();
        NBTTagList list = nbt.getTagList("timers", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++)
        {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            timers.put(tag.getUniqueId("uuid"), new MutablePair<>(tag.getInteger("time"), tag.getInteger("timeLastClick")));
        }
    }
}