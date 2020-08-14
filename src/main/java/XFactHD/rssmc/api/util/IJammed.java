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

package XFactHD.rssmc.api.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IJammed
{
    interface Block
    {
        void setJammed(World world, BlockPos pos, boolean jammed);

        boolean isJammed(World world, BlockPos pos);
    }

    //Items have to handle the "unjamming" themselves
    interface Item
    {
        void setJammed(EntityPlayer player, ItemStack stack, boolean jammed, BlockPos jammer);

        boolean isJammed(ItemStack stack);
    }

    //Entities have to handle the "unjamming" themselves
    interface Entity
    {
        void setJammed(boolean jammed, BlockPos jammer);

        boolean isJammed();
    }
}