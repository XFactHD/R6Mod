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

package XFactHD.rssmc.common.blocks.gadget;

import XFactHD.rssmc.common.blocks.TileEntityGadget;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;

import java.lang.ref.WeakReference;
import java.util.UUID;

//FIXME: the entity caught by the trap can't turn smoothly
public class TileEntityWelcomeMat extends TileEntityGadget
{
    private UUID uuidEntityCaught = null;
    private WeakReference<Entity> entityCaught = null;
    private double entityX = 0;
    private double entityY = 0;
    private double entityZ = 0;
    private int ticksSinceLastClick = 0;
    private int clicksLastTick = 0;
    private int clicks = 0;

    public void click()
    {
        ticksSinceLastClick = 0;
        clicksLastTick = clicks;
        clicks += 1;
    }

    public void setEntityCaught(Entity entityCaught)
    {
        this.entityCaught = new WeakReference<>(entityCaught);
        entityX = entityCaught.posX;
        entityY = ((double) pos.getY()) + 2D/16D;
        entityZ = entityCaught.posZ;
        notifyBlockUpdate();
    }

    public Entity getEntityCaught()
    {
        if (entityCaught == null && uuidEntityCaught != null)
        {
            entityCaught = new WeakReference<>(((WorldServer)world).getEntityFromUuid(uuidEntityCaught));
        }
        return entityCaught != null ? entityCaught.get() : null;
    }

    @Override
    public void update()
    {
        Entity entity = getEntityCaught();
        if (!world.isRemote && entity != null)
        {
            entity.motionX = 0;
            entity.motionY = 0;
            entity.motionZ = 0;
            entity.setPositionAndUpdate(entityX, entityY, entityZ);
            if (entity.isDead)
            {
                entityCaught = null;
                entityX = 0;
                entityY = 0;
                entityZ = 0;
                world.setBlockToAir(pos);
            }
        }
        if (!world.isRemote)
        {
            if (clicksLastTick == clicks-1)
            {
                ticksSinceLastClick += 1;
            }
            if (ticksSinceLastClick > 4)
            {
                clicks = 0;
                clicksLastTick = 0;
            }
            if (clicks >= 10)
            {
                world.destroyBlock(pos, false);
            }
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        super.readCustomNBT(nbt);
        uuidEntityCaught = nbt.getString("entityCaught").equals("") ? null : UUID.fromString(nbt.getString("entityCaught"));
        entityX = nbt.getDouble("entX");
        entityY = nbt.getDouble("entY");
        entityZ = nbt.getDouble("entZ");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        super.writeCustomNBT(nbt);
        nbt.setString("entityCaught", uuidEntityCaught != null ?  uuidEntityCaught.toString() : "");
        nbt.setDouble("entX", entityX);
        nbt.setDouble("entY", entityY);
        nbt.setDouble("entZ", entityZ);
    }
}