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

package XFactHD.rssmc.common.entity.drone;

import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityYokaiDrone extends AbstractEntityDrone //TODO: implement
{
    private boolean disabled;

    public EntityYokaiDrone(World world)
    {
        super(world);
    }

    @Override
    protected void onImpact(RayTraceResult result)
    {

    }

    public boolean isReloading()
    {
        return false;
    }

    public int getCurrentReloadTime()
    {
        return 0;
    }

    //TODO: get real value
    public int getMaxReloadTime()
    {
        return 40;
    }

    public boolean isStationary()
    {
        return false;
    }

    public boolean isDisabled()
    {
        return disabled;
    }

    public int getAmmo()
    {
        return 0;
    }

    @Override
    protected void entityInit()
    {

    }
}