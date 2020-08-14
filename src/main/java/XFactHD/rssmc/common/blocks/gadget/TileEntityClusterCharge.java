/*  Copyright (C) <2016>  <XFactHD, DrakoAlcarus>

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

import XFactHD.rssmc.common.blocks.TileEntityGadget;
import XFactHD.rssmc.common.data.EnumGadget;
import XFactHD.rssmc.client.util.Sounds;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3i;

public class TileEntityClusterCharge extends TileEntityGadget
{
    private static final int MAX_DELAY = 20;
    private boolean active = false;
    private int timer = 0;
    private int grenadesLeft = 5;

    @Override
    public void update()
    {
        if (!world.isRemote && active && grenadesLeft > 0)
        {
            timer += 1;
            if (timer >= MAX_DELAY)
            {
                timer = 0;
                fire();
            }
        }
        else if (!world.isRemote && active && grenadesLeft == 0)
        {
            world.setBlockToAir(pos);
        }
    }

    public void activate()
    {
        active = true;
        notifyBlockUpdate();
    }

    private void fire()
    {
        grenadesLeft -= 1;
        world.playSound(null, pos, Sounds.getGadgetSound(EnumGadget.CLUSTER_CHARGE, "fire"), SoundCategory.BLOCKS, 1, 1);
        boolean down = getState().getValue(PropertyHolder.FACING_NOT_UP) == EnumFacing.DOWN;
        Vec3i flightVector = Vec3i.NULL_VECTOR;
        switch (grenadesLeft)
        {
            case 4: flightVector = down ? new Vec3i(0, 0, 0) : new Vec3i(0, 0, 0); //Grenade goes far left
            case 3: flightVector = down ? new Vec3i(0, 0, 0) : new Vec3i(0, 0, 0); //Grenade goes left
            case 2: flightVector = down ? new Vec3i(0, 0, 0) : new Vec3i(0, 0, 0); //Grenade goes center
            case 1: flightVector = down ? new Vec3i(0, 0, 0) : new Vec3i(0, 0, 0); //Grenade goes right
            case 0: flightVector = down ? new Vec3i(0, 0, 0) : new Vec3i(0, 0, 0); //Grenade goes far right
        }
        //TODO: spawn entity and do EntityThrowable#setThrowable() with flightVector
    }

    public boolean isActivated()
    {
        return active;
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        super.readCustomNBT(nbt);
        active = nbt.getBoolean("active");
        timer = nbt.getInteger("timer");
        grenadesLeft = nbt.getInteger("grenadesLeft");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        super.writeCustomNBT(nbt);
        nbt.setBoolean("active", active);
        nbt.setInteger("timer", timer);
        nbt.setInteger("grenadesLeft", grenadesLeft);
    }
}