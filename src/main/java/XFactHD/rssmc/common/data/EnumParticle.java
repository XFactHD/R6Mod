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

package XFactHD.rssmc.common.data;

import java.util.Locale;

public enum  EnumParticle
{
    DESTROY_ELECTRIC_DEVICE(new float[]{0F, 0F, 0F}, 5, false),
    MUZZLE_FLASH(new float[]{0F, 0F, 0F}, 5, false),
    MUZZLE_SMOKE(new float[]{0F, 0F, 0F}, 5, false),
    OPEN_MIRROR(new float[]{0F, 0F, 0F}, 5, false),
    HEAL(new float[]{0F, 0F, 0F}, 5, false),
    SMALL_EXPLOSION(new float[]{0F, 0F, 0F}, 5, false),
    BIG_EXPLOSION(new float[]{0F, 0F, 0F}, 5, false);

    private float[] velocities;
    private int maxAge;
    private boolean needsNoDepth;

    EnumParticle(float[] velocities, int maxAge, boolean needsNoDepth)
    {
        this.velocities = velocities;
        this.maxAge = maxAge;
        this.needsNoDepth = needsNoDepth;
    }

    public float[] getDefaultVelocity()
    {
        return velocities;
    }

    public int getMaxAge()
    {
        return maxAge;
    }

    public String getTexture()
    {
        return "rssmc:particle/" + toString().toLowerCase(Locale.ENGLISH);
    }

    public boolean needsDepthDisabled()
    {
        return needsNoDepth;
    }
}