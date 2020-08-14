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

package XFactHD.rssmc.client.fx;

import XFactHD.rssmc.common.data.EnumParticle;
import XFactHD.rssmc.common.utils.utilClasses.Position;

public class ParticleFX
{
    private EnumParticle particle;
    private Position pos;
    private float velX, velY, velZ;
    private int age = 0;
    private boolean dead = false;

    public ParticleFX(EnumParticle particle, Position pos)
    {
        this.particle = particle;
        this.pos = pos;
        this.velX = particle.getDefaultVelocity()[0];
        this.velY = particle.getDefaultVelocity()[1];
        this.velZ = particle.getDefaultVelocity()[2];
    }

    public EnumParticle getParticle()
    {
        return particle;
    }

    public Position getPos()
    {
        return pos;
    }

    public void tick()
    {
        pos = pos.add(velX, velY, velZ);
        age += 1;
        if (age >= particle.getMaxAge()) { dead = true; }
    }

    public boolean isDead()
    {
        return dead;
    }
}