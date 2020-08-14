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

package XFactHD.rssmc.common.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;

import java.util.UUID;

public class TileEntityGadget extends TileEntityOwnable implements ITickable
{
    private int hits = 0;
    private EntityPlayer clicker = null;
    private int time = -1;
    private int timeSinceLastClick = 0;

    public void addHit()
    {
        hits += 1;
    }

    public int getHits()
    {
        return hits;
    }

    @Override
    public void update()
    {
        if (clicker != null)
        {
            if (!world.isRemote)
            {
                if (timeSinceLastClick > 4)
                {
                    clicker = null;
                    time = -1;
                    timeSinceLastClick = 0;
                    notifyBlockUpdate();
                }
                else
                {
                    time += 1;
                    timeSinceLastClick += 1;
                    notifyBlockUpdate();
                }
            }
            else
            {
                time += 1;
                timeSinceLastClick += 1;
            }
        }
    }

    public boolean click(EntityPlayer player)
    {
        if (clicker != null && clicker != player) { return false; }
        if (world.isRemote) { return getTime(player) >= getTimeToPickUp(); }
        if (clicker == null) { clicker = player; }
        timeSinceLastClick = 0;
        return getTime(player) >= getTimeToPickUp();
    }

    public int getTime(EntityPlayer player)
    {
        if (player == clicker)
        {
            return time;
        }
        return -1;
    }

    public int getTimeToPickUp()
    {
        return 20;
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        super.writeCustomNBT(nbt);
        nbt.setInteger("hits", hits);
        if (clicker != null) { nbt.setUniqueId("clicker", clicker.getUniqueID()); }
        nbt.setInteger("time", time);
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        super.readCustomNBT(nbt);
        hits = nbt.getInteger("hits");
        if (nbt.hasKey("clicker"))
        {
            UUID uuid = nbt.getUniqueId("clicker");
            if (uuid != null)
            {
                clicker = world.getPlayerEntityByUUID(uuid);
            }
        }
        time = nbt.getInteger("time");
    }
}
