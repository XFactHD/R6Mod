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

import XFactHD.rssmc.common.blocks.TileEntityOwnable;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityBarricade extends TileEntityOwnable
{
    private int hits = 0;
    private int shots = 0;
    private boolean window = false;
    private boolean door = false;
    private boolean large = false;
    private boolean right = false;
    private boolean left = false;

    public void hitBarricade()
    {
        hits += 1;
        notifyBlockUpdate();
    }

    public void shootBarricade()
    {
        shots += 1;
        notifyBlockUpdate();
    }

    public int getHits()
    {
        return hits;
    }

    public int getShots()
    {
        return shots;
    }

    public boolean isWindow()
    {
        return window;
    }

    public void setWindow(boolean window)
    {
        this.window = window;
        notifyBlockUpdate();
    }

    public boolean isDoor()
    {
        return door;
    }

    public void setDoor(boolean door)
    {
        this.door = door;
        notifyBlockUpdate();
    }

    public boolean isLarge()
    {
        return large;
    }

    public void setLarge(boolean large)
    {
        this.large = large;
        notifyBlockUpdate();
    }

    public boolean isRight()
    {
        return right;
    }

    public void setRight(boolean right)
    {
        this.right = right;
        notifyBlockUpdate();
    }

    public boolean isLeft()
    {
        return left;
    }

    public void setLeft(boolean left)
    {
        this.left = left;
        notifyBlockUpdate();
    }

    public int getMaxHits()
    {
        return 3;
    }

    public int getMaxShots() { return 12; }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        super.readCustomNBT(nbt);
        hits = nbt.getInteger("hits");
        window = nbt.getBoolean("window");
        door = nbt.getBoolean("door");
        large = nbt.getBoolean("large");
        right = nbt.getBoolean("right");
        left = nbt.getBoolean("left");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        super.writeCustomNBT(nbt);
        nbt.setInteger("hits", hits);
        nbt.setBoolean("window", window);
        nbt.setBoolean("door", door);
        nbt.setBoolean("large", large);
        nbt.setBoolean("right", right);
        nbt.setBoolean("left", left);
    }
}