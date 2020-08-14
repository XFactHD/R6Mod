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

import XFactHD.rssmc.common.blocks.TileEntityOwnable;
import XFactHD.rssmc.common.utils.propertyEnums.Connection;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import java.lang.ref.WeakReference;
import java.util.UUID;

public class TileEntityBarbedWire extends TileEntityOwnable
{
    private UUID shockWireOwnerUUID = null;
    private WeakReference<EntityPlayer> shockWireOwner = null;
    private int hits = 0;

    public void hit(Connection con)
    {
        hits += 1;
        int allHits = hits;
        TileEntity te1 = null;
        TileEntity te2 = null;
        TileEntity te3 = null;
        switch (con)
        {
            case UR:
            {
                te1 = world.getTileEntity(pos.north());
                te2 = world.getTileEntity(pos.east());
                te3 = world.getTileEntity(pos.north().east());
                break;
            }
            case UL:
            {
                te1 = world.getTileEntity(pos.north());
                te2 = world.getTileEntity(pos.west());
                te3 = world.getTileEntity(pos.north().west());
                break;
            }
            case DR:
            {
                te1 = world.getTileEntity(pos.south());
                te2 = world.getTileEntity(pos.east());
                te3 = world.getTileEntity(pos.south().east());
                break;
            }
            case DL:
            {
                te1 = world.getTileEntity(pos.south());
                te2 = world.getTileEntity(pos.west());
                te3 = world.getTileEntity(pos.south().west());
                break;
            }
        }
        if (te1 instanceof TileEntityBarbedWire)
        {
            allHits += ((TileEntityBarbedWire)te1).hits;
        }
        if (te2 instanceof TileEntityBarbedWire)
        {
            allHits += ((TileEntityBarbedWire)te2).hits;
        }
        if (te3 instanceof TileEntityBarbedWire)
        {
            allHits += ((TileEntityBarbedWire)te3).hits;
        }
        if (allHits >= 3)
        {
            world.destroyBlock(pos, false);
            if (te1 != null) world.destroyBlock(te1.getPos(), false);
            if (te2 != null) world.destroyBlock(te2.getPos(), false);
            if (te3 != null) world.destroyBlock(te3.getPos(), false);
        }
    }

    public EntityPlayer getShockWireOwner()
    {
        if (shockWireOwner == null && shockWireOwnerUUID != null)
        {
            shockWireOwner = new WeakReference<>(world.getPlayerEntityByUUID(shockWireOwnerUUID));
        }
        return shockWireOwner != null ? shockWireOwner.get() : null;
    }

    public void setShockWireOwner(EntityPlayer shockWireOwner)
    {
        this.shockWireOwner = new WeakReference<>(shockWireOwner);
        notifyBlockUpdate();
        if (shockWireOwner != null)
        {
            shockWireOwnerUUID = shockWireOwner.getPersistentID();
        }
        else
        {
            shockWireOwnerUUID = null;
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        super.writeCustomNBT(nbt);
        nbt.setString("shockOwner", shockWireOwnerUUID != null ? shockWireOwnerUUID.toString() : "");
        nbt.setInteger("hits", hits);
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        super.readCustomNBT(nbt);
        shockWireOwnerUUID = nbt.getString("shockOwner").equals("") ? null : UUID.fromString(nbt.getString("shockOwner"));
        hits = nbt.getInteger("hits");
    }
}