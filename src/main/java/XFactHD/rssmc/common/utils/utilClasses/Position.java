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

package XFactHD.rssmc.common.utils.utilClasses;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class Position
{
    private double x;
    private double y;
    private double z;
    private BlockPos pos;

    public Position(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        pos = new BlockPos(x, y, z);
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public double getZ()
    {
        return z;
    }

    public Position add(double x, double y, double z)
    {
        if (x != 0 || y != 0 || z != 0)
        {
            return new Position(this.x + x, this.y + y, this.z + z);
        }
        return this;
    }

    public BlockPos toBlockPos()
    {
        return pos;
    }

    public double distanceTo(Position pos)
    {
        double diffX = Math.max(getX(), pos.getX()) - Math.min(getX(), pos.getX());
        double diffY = Math.max(getY(), pos.getY()) - Math.min(getY(), pos.getY());
        double diffZ = Math.max(getZ(), pos.getZ()) - Math.min(getZ(), pos.getZ());
        return Math.sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Position)) { return false; }
        Position pos = (Position)obj;
        return pos.x == x && pos.y == y && pos.z == z;
    }

    @Override
    public String toString()
    {
        return "X: " + x + ", Y: " + y + ", Z: " + z;
    }

    public void serialize(NBTTagCompound nbt)
    {
        nbt.setDouble("x", x);
        nbt.setDouble("y", y);
        nbt.setDouble("z", z);
    }

    public static Position deserialize(NBTTagCompound nbt)
    {
        return new Position(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"));
    }

    public static Position fromBlockPos(BlockPos pos)
    {
        return new Position(pos.getX(), pos.getY(), pos.getZ());
    }

    public static Position fromBlockPosCentered(BlockPos pos, boolean centerY)
    {
        return new Position(pos.getX() + .5, pos.getY() + (centerY ? .5 : 0), pos.getZ() + .5);
    }
}