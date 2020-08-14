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

public class EntityTwitchDrone extends AbstractEntityDrone //TODO: implement
{
    public EntityTwitchDrone(World world)
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

    public int getAmmoLoaded()
    {
        return 0;
    }

    public int getAmmoLeft()
    {
        return 15;
    }

    @Override
    protected void entityInit()
    {

    }

    public void zap()
    {

    }
}