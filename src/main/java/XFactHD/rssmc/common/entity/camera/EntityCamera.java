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

import XFactHD.rssmc.common.data.EnumSide;
import XFactHD.rssmc.common.data.team.StatusController;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import XFactHD.rssmc.common.utils.utilClasses.Position;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.UUID;

public class EntityCamera extends AbstractEntityCamera
{
    private Position pos = null;

    public EntityCamera(World world)
    {
        super(world);
    }

    public EntityCamera(World world, UUID owner)
    {
        super(world, owner);
    }

    @Override
    public void onUpdate()
    {
        if (world.isAirBlock(new Position(posX, posY, posZ).toBlockPos())) { setDead(); }
        super.onUpdate();
    }

    @Override
    public boolean canBeViewedBy(EntityPlayer player)
    {
        return StatusController.getPlayersSide(player) != EnumSide.ATTACKER;
    }

    @Override
    public boolean canBeUsedBy(EntityPlayer player)
    {
        return canBeViewedBy(player) && (user == null || user.equals(player.getUniqueID()) || !player.isSpectator());
    }

    @Override
    public boolean canBeControlledBy(EntityPlayer player)
    {
        return canBeUsedBy(player);
    }

    public boolean isDestroyed()
    {
        if (pos == null) { pos = new Position(posX, posY, posZ); }
        return world.getBlockState(pos.toBlockPos()).getValue(PropertyHolder.DESTROYED);
    }

    @Override
    public String getType()
    {
        return "camera";
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        setRotation(0, 90);
    }

    @Override
    public void applyMouseMovement(int dx, int dy)
    {
        float pitch = getPitchYaw().x;
        float yaw = getPitchYaw().y;
        pitch -= (double) dy * .5;
        yaw += (double) dx * .5;
        if (pitch < 0) { pitch = 0; }
        if (pitch > 90) { pitch = 90; }
        if (yaw < 0) { yaw += 360; }
        if (yaw > 360) { yaw -= 360; }
        setRotation(yaw, pitch);
    }
}