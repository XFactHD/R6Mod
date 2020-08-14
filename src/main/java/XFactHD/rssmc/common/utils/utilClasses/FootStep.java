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

import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

public class FootStep
{
    private long timestamp;
    private int ticks = 0;
    private Position pos;
    private float rotation;
    private UUID player;
    private boolean right;

    public FootStep(long timestamp, Position pos, float rotation, UUID player, boolean right)
    {
        this.timestamp = timestamp;
        this.pos = pos;
        this.rotation = rotation;
        this.player = player;
        this.right = right;
    }

    public FootStep(long timestamp, Position pos, float rotation, EntityPlayer player)
    {
        this.timestamp = timestamp;
        this.pos = pos;
        this.rotation = rotation;
        this.player = player.getUniqueID();
        this.right = player.world.rand.nextBoolean();
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public Position getPos()
    {
        return pos;
    }

    public float getRotation()
    {
        return rotation;
    }

    public UUID getPlayer()
    {
        return player;
    }

    public boolean isRight()
    {
        return right;
    }

    //Client-only
    public void tick()
    {
        ticks += 1;
    }

    //Returns a color depending on the client value of ticks
    public int[] getColor()
    {
        int red = (int)((double)255 * (1 - ((double)ticks / 900D)));
        int green = (int)(ticks < 900 ? (double)255 * ((double) ticks / 900D) : (double)255 * (1 - (((double)ticks - 900D) / 900D)));
        int blue = (int)(ticks < 900 ? 0 : (double)255 * ((double) ticks - 900D) / 900D);
        return new int[]{red, green, blue, 255};
    }
}