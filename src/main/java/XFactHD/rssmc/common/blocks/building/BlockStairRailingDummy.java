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

import XFactHD.rssmc.common.blocks.BlockBase;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class BlockStairRailingDummy extends BlockBase
{
    public BlockStairRailingDummy()
    {
        super("block_stair_railing_dummy", Material.WOOD, null, null, null);
        setDefaultState(super.getDefaultState().withProperty(PropertyHolder.RIGHT, false));
        setSoundType(SoundType.WOOD);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PropertyHolder.RIGHT, PropertyHolder.FACING_CARDINAL, PropertyHolder.RAILING_TYPE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        boolean right = false;
        if (meta >= 7)
        {
            right = true;
            meta -= 5;
        }
        return getDefaultState().withProperty(PropertyHolder.RIGHT, right).withProperty(PropertyHolder.FACING_CARDINAL, EnumFacing.getFront(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        int facing = state.getValue(PropertyHolder.FACING_CARDINAL).getIndex();
        return state.getValue(PropertyHolder.RIGHT) ? facing + 5 : facing;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos)
    {
        boolean right = state.getValue(PropertyHolder.RIGHT);
        switch (state.getValue(PropertyHolder.FACING_CARDINAL))
        {
            case NORTH: return right ? new AxisAlignedBB(13F/16F, 0, 0, 15F/16F, 7F/16F, 7F/16F) : new AxisAlignedBB(1F/16F, 0, 0, 3F/16F, 7F/16F, 7F/16F);
            case SOUTH: return right ? new AxisAlignedBB(1F/16F, 0, 9F/16F, 3F/16F, 7F/16F, 1) : new AxisAlignedBB(13F/16F, 0, 9F/16F, 15F/16F, 7F/16F, 1);
            case WEST:  return right ? new AxisAlignedBB(0, 0, 1F/16F,  7F/16F, 7F/16F, 3F/16F) : new AxisAlignedBB(0, 0, 13F/16F, 7F/16F, 7F/16F, 15F/16F);
            case EAST:  return right ? new AxisAlignedBB(9F/16F, 0, 13F/16F, 1, 7F/16F, 15F/16F) : new AxisAlignedBB(9F/16F, 0, 1F/16F, 1, 7F/16F, 3F/16F);
        }
        return super.getCollisionBoundingBox(state, world, pos);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        super.breakBlock(world, pos, state);
        world.setBlockToAir(pos.down());
    }

    @Override
    public boolean isCompleteBlock(IBlockState state)
    {
        return false;
    }
}