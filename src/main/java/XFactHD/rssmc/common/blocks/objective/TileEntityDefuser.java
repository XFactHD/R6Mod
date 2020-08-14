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

import XFactHD.rssmc.common.Content;
import XFactHD.rssmc.common.blocks.TileEntityBase;
import XFactHD.rssmc.common.data.team.StatusController;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;

public class TileEntityDefuser extends TileEntityBase implements ITickable
{
    public static final int MAX_INTERACT_TIME = 120;
    private boolean active = false;
    private boolean destroyed = false;
    private int destroyTime = 0;
    private int plantTime = 0;
    private int objLocation = 0;
    private long lastPlant = 0;
    private long lastHit = 0;
    private EntityPlayer lastPlanter = null;

    @Override
    public void update()
    {
        if (!world.isRemote)
        {
            if (world.getTotalWorldTime() - lastHit <= 4)
            {
                destroyTime += world.getTotalWorldTime() - lastHit;
                notifyBlockUpdate();
            }
            else
            {
                destroyTime = 0;
                notifyBlockUpdate();
            }

            if (destroyTime >= MAX_INTERACT_TIME)
            {
                setDestroyed(true);
            }

            if (!active && world.getTotalWorldTime() - lastPlant <= 4)
            {
                plantTime += world.getTotalWorldTime() - lastPlant;
            }
            else
            {
                world.destroyBlock(pos, false);
                dropDefuser();
            }

            if (plantTime >= MAX_INTERACT_TIME)
            {
                setActive(true);
                lastPlanter = null;
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void setActive(boolean active)
    {
        this.active = active;
        StatusController.getBomb(world, objLocation).setDefusing(true, pos);
        notifyBlockUpdate();
    }

    public boolean isActive()
    {
        return active;
    }

    public void setDestroyed(boolean destroyed)
    {
        if (destroyed)
        {
            //TODO: tell the game manager that the round is over
        }
        this.destroyed = destroyed;
        notifyBlockUpdate();
    }

    public boolean isDestroyed()
    {
        return destroyed;
    }

    public void setObjLocation(int objLocation)
    {
        this.objLocation = objLocation;
        notifyBlockUpdate();
    }

    public int getObjLocation()
    {
        return objLocation;
    }

    public int getDestroyTime()
    {
        return destroyTime;
    }

    public int getPlantTime()
    {
        return plantTime;
    }

    public void plant()
    {
        lastPlant = world.getTotalWorldTime();
    }

    public void hit()
    {
        lastHit = world.getTotalWorldTime();
    }

    private void dropDefuser()
    {
        if (!lastPlanter.isDead && !StatusController.isPlayerDBNO(lastPlanter))
        {
            lastPlanter.inventory.addItemStackToInventory(new ItemStack(Content.blockDefuser));
        }
        else
        {
            Content.blockDefuser.dropBlockAsItem(world, pos, world.getBlockState(pos), 0);
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        active = nbt.getBoolean("active");
        destroyed = nbt.getBoolean("destroyed");
        destroyTime = nbt.getInteger("timer");
        objLocation = nbt.getInteger("obj");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        nbt.setBoolean("active", active);
        nbt.setBoolean("destroyed", destroyed);
        nbt.setInteger("timer", destroyTime);
        nbt.setInteger("obj", objLocation);
    }
}