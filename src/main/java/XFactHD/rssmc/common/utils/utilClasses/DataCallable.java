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

import net.minecraft.nbt.NBTTagCompound;

import java.util.concurrent.Callable;

public class DataCallable implements Callable<NBTTagCompound>
{
    private NBTTagCompound data;

    public DataCallable(NBTTagCompound data)
    {
        this.data = data;
    }

    @Override
    public NBTTagCompound call() throws Exception
    {
        return data;
    }
}