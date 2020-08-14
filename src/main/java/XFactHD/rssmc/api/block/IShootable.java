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

package XFactHD.rssmc.api.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IShootable
{
    default int getMaxHits() { return 1; }

    //returns true if points should be awarded
    boolean onBlockShot(World world, BlockPos pos, IBlockState state, EntityPlayer player, float hitX, float hitY, float hitZ, EnumFacing sideHit);

    //returns true if points should be awarded
    boolean onBlockDestroyedByShot(World world, BlockPos pos, IBlockState state, EntityPlayer player, float hitX, float hitY, float hitZ, EnumFacing sideHit);
}