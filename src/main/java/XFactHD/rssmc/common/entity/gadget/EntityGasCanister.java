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

package XFactHD.rssmc.common.entity.gadget;

import XFactHD.rssmc.common.utils.utilClasses.Position;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityGasCanister extends EntityThrowable
{
    private boolean sticked = false;
    private Position stickPos = null;
    private EnumFacing stickSide = null;

    public EntityGasCanister(World world)
    {
        super(world);
    }

    public EntityGasCanister(World world, EntityPlayer thrower)
    {
        super(world, thrower);
    }

    @Override
    protected void onImpact(RayTraceResult result)
    {
        if (result.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            sticked = true;
            stickPos = new Position(posX, posY, posZ);
            stickSide = result.sideHit;
        }
    }

    @Override
    public void onUpdate()
    {
        if (!sticked)
        {
            super.onUpdate();
        }
    }

    public EnumFacing getStickSide()
    {
        return stickSide;
    }

    public Position getStickPos()
    {
        return stickPos;
    }
}