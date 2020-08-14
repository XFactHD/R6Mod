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

package XFactHD.rssmc.common.entity.camera;

import XFactHD.rssmc.common.data.EnumOperator;
import XFactHD.rssmc.common.data.team.StatusController;
import XFactHD.rssmc.common.entity.drone.EntityTwitchDrone;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.UUID;

public class EntityTwitchDroneCam extends AbstractEntityCamera
{
    public EntityTwitchDroneCam(World world)
    {
        super(world);
    }

    public EntityTwitchDroneCam(World world, UUID owner)
    {
        super(world, owner);
    }

    @Override
    public boolean canBeViewedBy(EntityPlayer player)
    {
        return StatusController.getPlayersOperator(player) == EnumOperator.TWITCH;
    }

    @Override
    public boolean canBeUsedBy(EntityPlayer player)
    {
        return canBeViewedBy(player);
    }

    @Override
    public boolean canBeControlledBy(EntityPlayer player)
    {
        return canBeUsedBy(player) && !player.isSpectator();
    }

    public EntityTwitchDrone getDrone()
    {
        return (EntityTwitchDrone)getRidingEntity();
    }

    @Override
    public String getType()
    {
        return "twitch_drone";
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
    }
}