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

package XFactHD.rssmc.common.blocks.misc;

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.common.blocks.BlockBase;
import XFactHD.rssmc.common.items.itemBlocks.ItemBlockBase;
import XFactHD.rssmc.common.utils.RSSWorldData;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
public class BlockGameManager extends BlockBase
{
    public BlockGameManager()
    {
        super("block_game_manager", Material.IRON, RainbowSixSiegeMC.CT.miscTab, ItemBlockBase.class, null);
        //registerTileEntity(TileEntityGameManager.class, "GameManager");
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos)
    {
        return RSSWorldData.get(world).getGameManagerPos() == null;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        RSSWorldData.get(world).setGameManagerPos(pos);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return null; //new TileEntityGameManager();
    }
}