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

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.common.blocks.BlockBase;
import XFactHD.rssmc.common.items.itemBlocks.ItemBlockBase;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
public class BlockSteelLadder extends BlockBase
{
    public BlockSteelLadder()
    {
        super("blockSteelLadder", Material.IRON, RainbowSixSiegeMC.CT.buildingTab, ItemBlockBase.class, null);
        setDefaultState(super.getDefaultState().withProperty(PropertyHolder.FACING_CARDINAL, EnumFacing.NORTH).withProperty(PropertyHolder.TOP, true).withProperty(PropertyHolder.BOTTOM, true));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PropertyHolder.FACING_CARDINAL, PropertyHolder.TOP, PropertyHolder.BOTTOM);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        if (meta == 0 || meta == 1)
        {
            meta = EnumFacing.NORTH.getIndex();
        }
        return getDefaultState().withProperty(PropertyHolder.FACING_CARDINAL, EnumFacing.getFront(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(PropertyHolder.FACING_CARDINAL).getIndex();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        boolean top = world.getBlockState(pos.up()).getBlock() != this || world.getBlockState(pos.up()).getValue(PropertyHolder.FACING_CARDINAL) != state.getValue(PropertyHolder.FACING_CARDINAL);
        boolean bottom = world.getBlockState(pos.down()).getBlock() != this || world.getBlockState(pos.down()).getValue(PropertyHolder.FACING_CARDINAL) != state.getValue(PropertyHolder.FACING_CARDINAL);
        return state.withProperty(PropertyHolder.TOP, top).withProperty(PropertyHolder.BOTTOM, bottom);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return getDefaultState().withProperty(PropertyHolder.FACING_CARDINAL, placer.getHorizontalFacing());
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos)
    {
        boolean top = world.getBlockState(pos.up()).getBlock() != this || world.getBlockState(pos.up()).getValue(PropertyHolder.FACING_CARDINAL) != state.getValue(PropertyHolder.FACING_CARDINAL);
        switch (state.getValue(PropertyHolder.FACING_CARDINAL))
        {
            case NORTH: return top ? new AxisAlignedBB(    0, 0,     0,     1, 1, .5625) : new AxisAlignedBB(    0, 0, .4375,     1, 1, .5625);
            case EAST:  return top ? new AxisAlignedBB(.4375, 0,     0,     1, 1,     1) : new AxisAlignedBB(.4375, 0,     0, .5625, 1,     1);
            case SOUTH: return top ? new AxisAlignedBB(    0, 0, .4375,     1, 1,     1) : new AxisAlignedBB(    0, 0, .4375,     1, 1, .5625);
            case WEST:  return top ? new AxisAlignedBB(    0, 0,     0, .5625, 1, 1) : new AxisAlignedBB(.4375, 0,     0, .5625, 1,     1);
            default: return new AxisAlignedBB(0, 0, 0, 0, 0, 0);
        }
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos)
    {
        //noinspection ConstantConditions
        return getCollisionBoundingBox(state, world, pos).offset(pos);
    }

    @Override
    public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity)
    {
        return true;
    }

    @Override
    public boolean isFullBlock(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return false;
    }
}