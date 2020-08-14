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

package XFactHD.rssmc.common.blocks.building;

import XFactHD.rssmc.common.blocks.TileEntityBase;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import java.util.EnumMap;

@SuppressWarnings("deprecation")
public class TileEntityMultiTexture extends TileEntityBase
{
    private EnumMap<EnumFacing, IBlockState> sideStateMap = new EnumMap<>(EnumFacing.class);

    public IBlockState getStateForSide(EnumFacing side)
    {
        if (sideStateMap.get(side) == null)
        {
            sideStateMap.put(side, Blocks.AIR.getDefaultState());
        }
        return sideStateMap.get(side);
    }

    public void setStateForSide(EnumFacing side, IBlockState state)
    {
        sideStateMap.put(side, state);
        notifyBlockUpdate();
    }

    public void clearSide(EnumFacing side)
    {
        setStateForSide(side, Blocks.AIR.getDefaultState());
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        for (EnumFacing side : EnumFacing.values())
        {
            NBTTagCompound tag = nbt.getCompoundTag(side.getName());
            Block block = Block.REGISTRY.getObject(new ResourceLocation(tag.getString("block")));
            IBlockState state = block.getStateFromMeta(tag.getInteger("meta"));
            sideStateMap.put(side, state);
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        for (EnumFacing side : EnumFacing.values())
        {
            IBlockState state = getStateForSide(side);
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("block", state.getBlock().getRegistryName().toString());
            tag.setInteger("meta", state.getBlock().getMetaFromState(state));
            nbt.setTag(side.getName(), tag);
        }
    }
}