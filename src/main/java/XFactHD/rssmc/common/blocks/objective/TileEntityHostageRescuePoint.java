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

import XFactHD.rssmc.common.blocks.TileEntityBase;
import XFactHD.rssmc.common.entity.EntityHostage;
import XFactHD.rssmc.common.utils.RSSWorldData;
import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public class TileEntityHostageRescuePoint extends TileEntityBase implements ITickable
{
    private static final Predicate<? super Entity> SEARCH_PREDICATE = entity -> (entity instanceof EntityPlayer && entity.getRidingEntity() instanceof EntityHostage) || entity instanceof EntityHostage;
    private AxisAlignedBB aabb = null;

    @Override
    public void update()
    {
        if (!world.isRemote)
        {
            if (aabb == null) { aabb = new AxisAlignedBB(pos.north().west(), pos.south(2).east(2)); }
            List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, aabb, SEARCH_PREDICATE);
            for (Entity entity : entities)
            {
                if (entity instanceof EntityPlayer)
                {
                    entity.dismountRidingEntity();
                }
                RSSWorldData.get(world).getGameManager().getRunningHostageGame().rescueHostage();
            }
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {

    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {

    }
}