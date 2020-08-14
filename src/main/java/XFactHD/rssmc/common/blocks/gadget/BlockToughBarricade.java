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

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.common.blocks.building.BlockBarricade;
import XFactHD.rssmc.common.items.itemBlocks.ItemBlockToughBarricade;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

public class BlockToughBarricade extends BlockBarricade
{
    public BlockToughBarricade()
    {
        super("blockToughBarricade", Material.IRON, RainbowSixSiegeMC.CT.gadgetTab, ItemBlockToughBarricade.class);
        setDefaultState(super.getDefaultState().withProperty(PropertyHolder.TOP, false).withProperty(PropertyHolder.WINDOW, false)
                .withProperty(PropertyHolder.FACING_CARDINAL, EnumFacing.NORTH).withProperty(PropertyHolder.DOOR, false).withProperty(PropertyHolder.LARGE, false)
                .withProperty(PropertyHolder.RIGHT, false).withProperty(PropertyHolder.LEFT, false));
        registerTileEntity(TileEntityToughBarricade.class, "ToughBarricade");
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PropertyHolder.TOP, PropertyHolder.FACING_CARDINAL, PropertyHolder.WINDOW, PropertyHolder.DOOR,
                PropertyHolder.LARGE, PropertyHolder.RIGHT, PropertyHolder.LEFT);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(PropertyHolder.FACING_CARDINAL).getIndex() + (state.getValue(PropertyHolder.TOP) ? 10 : 0);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        boolean top = false;
        if (meta > 5)
        {
            meta -= 10;
            top = true;
        }
        if (meta == 0 || meta == 1)
        {
            meta = EnumFacing.NORTH.getIndex();
        }
        return getDefaultState().withProperty(PropertyHolder.FACING_CARDINAL, EnumFacing.getFront(meta)).withProperty(PropertyHolder.TOP, top);
    }
}