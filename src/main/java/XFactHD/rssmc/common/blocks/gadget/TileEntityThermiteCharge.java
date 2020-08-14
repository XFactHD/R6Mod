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

import XFactHD.rssmc.api.block.IExplosive;
import XFactHD.rssmc.common.blocks.TileEntityGadget;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;

public class TileEntityThermiteCharge extends TileEntityGadget
{
    private int timer = 0;

    @Override
    public void update()
    {
        if (timer > 0)
        {
            --timer;
            if (timer == 0 && !world.isRemote)
            {
                ((IExplosive)world.getBlockState(pos).getBlock()).explode(world, pos, getState());
            }
        }
    }

    public void activate()
    {
        timer = 105;
        if (!world.isRemote) { notifyBlockUpdate(); }
    }

    public boolean isActive()
    {
        return timer > 0;
    }

    public int getTime()
    {
        return timer;
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        super.writeCustomNBT(nbt);
        nbt.setInteger("timer", timer);
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        super.readCustomNBT(nbt);
        timer = nbt.getInteger("timer");
    }
}