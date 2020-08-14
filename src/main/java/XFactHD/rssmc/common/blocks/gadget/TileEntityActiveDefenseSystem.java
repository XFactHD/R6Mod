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

package XFactHD.rssmc.common.blocks.gadget;

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.common.blocks.TileEntityGadget;
import XFactHD.rssmc.common.entity.gadget.AbstractEntityGrenade;
import XFactHD.rssmc.common.net.PacketADSAnimation;
import XFactHD.rssmc.common.utils.Utils;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import XFactHD.rssmc.common.utils.helper.RayTraceUtils;
import XFactHD.rssmc.common.utils.utilClasses.Position;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class TileEntityActiveDefenseSystem extends TileEntityGadget
{
    private int bullets = 2;
    private int traceTicks = 0;
    private Position grenadePosition = null;
    private EnumFacing facing = null;

    @Override
    public void update()
    {
        if (!world.isRemote && bullets > 0)
        {
            AbstractEntityGrenade grenade = RayTraceUtils.rayTraceGrenades(world, this, pos, 4);
            if (grenade != null)
            {
                grenade.setDead();
                RainbowSixSiegeMC.NET.sendMessageToArea(new PacketADSAnimation(pos, new Position(grenade.posX, grenade.posY, grenade.posZ)), Utils.getTarget(world, pos));
                bullets -= 1;
                notifyBlockUpdate();
            }
        }
        else if (world.isRemote && traceTicks > 0)
        {
            traceTicks -= 1;
            if (traceTicks == 0) { grenadePosition = null; }
        }
    }

    public void setBullets(int bullets)
    {
        this.bullets = bullets;
        notifyBlockUpdate();
    }

    public int getBullets()
    {
        return bullets;
    }

    public void startAnimation(Position grenadePosition)
    {
        this.grenadePosition = grenadePosition;
        traceTicks = 10;
    }

    public Position getGrenadePosition()
    {
        return grenadePosition;
    }

    public boolean isAnimationActive()
    {
        return traceTicks > 0;
    }

    public EnumFacing getFacing()
    {
        if (facing == null)
        {
            facing = world.getBlockState(pos).getValue(PropertyHolder.FACING_NOT_DOWN);
        }
        return facing;
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        super.writeCustomNBT(nbt);
        nbt.setInteger("bullets", bullets);
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        super.readCustomNBT(nbt);
        bullets = nbt.getInteger("bullets");
    }
}