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

package XFactHD.rssmc.common.capability.gunHandler;

import XFactHD.rssmc.api.capability.IGunHandler;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class GunHandlerStorage implements Capability.IStorage<IGunHandler>
{
    @CapabilityInject(IGunHandler.class)
    public static final Capability<IGunHandler> GUN_HANDLER_CAPABILITY = null;

    @Override
    public NBTBase writeNBT(Capability<IGunHandler> capability, IGunHandler instance, EnumFacing side)
    {
        return capability.writeNBT(instance, side);
    }

    @Override
    public void readNBT(Capability<IGunHandler> capability, IGunHandler instance, EnumFacing side, NBTBase nbt)
    {
        capability.readNBT(instance, side, nbt);
    }
}