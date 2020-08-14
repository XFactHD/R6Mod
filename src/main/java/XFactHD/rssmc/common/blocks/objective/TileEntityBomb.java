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

package XFactHD.rssmc.common.blocks.objective;

import XFactHD.rssmc.common.blocks.TileEntityBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class TileEntityBomb extends TileEntityBase implements ITickable
{
    public static final int MAX_DEFUSE_TIME = 900;

    private int rangeNorth = 0;
    private int rangeEast = 0;
    private int rangeSouth = 0;
    private int rangeWest = 0;
    private AxisAlignedBB aabb = null;
    private boolean defusing = false;
    private boolean defused = false;
    private int defuseTimer = 0;
    private int location = 0;

    @Override
    public void update()
    {
        if (!world.isRemote && aabb != null && !defused)
        {
            if (defusing && defuseTimer < MAX_DEFUSE_TIME)
            {
                defuseTimer += 1;
                notifyBlockUpdate();
            }
            if (defuseTimer >= MAX_DEFUSE_TIME)
            {
                setDefused(true);
                setDefusing(false, null);
                notifyBlockUpdate();
            }
        }
    }

    public void setDefusing(boolean defusing, BlockPos defuser)
    {
        if (defusing != this.defusing)
        {
            this.defusing = defusing;
            notifyBlockUpdate();
            if (defuser != null)
            {
                TileEntity te = world.getTileEntity(defuser);
                if (te instanceof TileEntityDefuser)
                {
                    ((TileEntityDefuser)te).setObjLocation(location);
                }
            }
        }
    }

    private void setDefused(boolean defused)
    {
        if (defused != this.defused)
        {
            if (defused) { /*TODO: tell game manager that the round is over*/ }
            this.defusing = defused;
            notifyBlockUpdate();
        }
    }

    public void setRange(int rangeNorth, int rangeEast, int rangeSouth, int rangeWest)
    {
        this.rangeNorth = rangeNorth;
        this.rangeEast = rangeEast;
        this.rangeSouth = rangeSouth;
        this.rangeWest = rangeWest;
        aabb = new AxisAlignedBB(pos.getX() - rangeWest, pos.getY(), pos.getZ() - rangeNorth, pos.getX() + rangeEast + 1, pos.getY() + 2, pos.getZ() + rangeSouth + 1);
        if (world != null && !world.isRemote) { notifyBlockUpdate(); }
    }

    public void setLocation(int location)
    {
        this.location = location;
    }

    public int getLocation()
    {
        return location;
    }

    public boolean isDefusing()
    {
        return defusing;
    }

    public boolean isDefused()
    {
        return defused;
    }

    public int getDefuseTime()
    {
        return defuseTimer;
    }

    public void reset()
    {
        defuseTimer = 0;
        defusing = false;
        defused = false;
        notifyBlockUpdate();
    }

    public AxisAlignedBB getAABB()
    {
        return aabb == null ? new AxisAlignedBB(pos, pos.south().east().up(2)) : aabb;
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        rangeNorth = nbt.getInteger("rangeNorth");
        rangeEast = nbt.getInteger("rangeEast");
        rangeSouth = nbt.getInteger("rangeSouth");
        rangeWest = nbt.getInteger("rangeWest");
        location = nbt.getInteger("location");
        defusing = nbt.getBoolean("defusing");
        defused = nbt.getBoolean("defused");
        defuseTimer = nbt.getInteger("timer");
        setRange(rangeNorth, rangeEast, rangeSouth, rangeWest);
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("rangeNorth", rangeNorth);
        nbt.setInteger("rangeEast", rangeEast);
        nbt.setInteger("rangeSouth", rangeSouth);
        nbt.setInteger("rangeWest", rangeWest);
        nbt.setInteger("location", location);
        nbt.setBoolean("defusing", defusing);
        nbt.setBoolean("defused", defused);
        nbt.setInteger("timer", defuseTimer);
    }

    @Override
    public double getMaxRenderDistanceSquared()
    {
        return Double.MAX_VALUE;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return INFINITE_EXTENT_AABB;
    }
}