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

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;

public class HitData
{
    private EntityLivingBase victim;
    private RayTraceResult rayTrace;
    private EntityPlayer shooter;
    private boolean headshot;

    public HitData(EntityLivingBase victim, RayTraceResult rayTrace, EntityPlayer shooter, boolean headshot)
    {
        this.victim = victim;
        this.rayTrace = rayTrace;
        this.shooter = shooter;
        this.headshot = headshot;
    }

    public EntityLivingBase getVictim()
    {
        return victim;
    }

    public RayTraceResult getRayTrace()
    {
        return rayTrace;
    }

    public EntityPlayer getShooter()
    {
        return shooter;
    }

    public boolean isHeadshot()
    {
        return headshot;
    }
}