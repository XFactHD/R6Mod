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
import XFactHD.rssmc.common.Content;
import XFactHD.rssmc.common.blocks.BlockBase;
import XFactHD.rssmc.common.items.itemBlocks.ItemBlockBase;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import XFactHD.rssmc.common.utils.propertyEnums.RailingType;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class BlockStairRailing extends BlockBase
{
    public BlockStairRailing()
    {
        super("block_stair_railing", Material.WOOD, RainbowSixSiegeMC.CT.buildingTab, ItemBlockBase.class, null);
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

    @Override
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side)
    {
        if (side != EnumFacing.UP) { return false; }
        IBlockState state = world.getBlockState(pos.down());
        return state.getBlock() instanceof BlockStairs;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, ItemStack stack)
    {
        IBlockState stairState = world.getBlockState(pos.down());
        EnumFacing facing = stairState.getValue(BlockStairs.FACING);
        boolean right = false;
        switch (facing)
        {
            case NORTH: right = (hitX > .5F); break;
            case SOUTH: right = (hitX < .5F); break;
            case WEST:  right = (hitZ < .5F); break;
            case EAST:  right = (hitZ > .5F); break;
        }
        return getDefaultState().withProperty(PropertyHolder.RIGHT, right).withProperty(PropertyHolder.FACING_CARDINAL, facing);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        EnumFacing facing = state.getValue(PropertyHolder.FACING_CARDINAL);
        IBlockState otherState = world.getBlockState(pos.up().offset(state.getValue(PropertyHolder.RIGHT) ? facing.rotateY() : facing.rotateYCCW()));
        if (otherState.getBlock() == this)
        {
            if (facing.getOpposite() == otherState.getValue(PropertyHolder.FACING_CARDINAL) && state.getValue(PropertyHolder.RIGHT) == otherState.getValue(PropertyHolder.RIGHT))
            {
                return state.withProperty(PropertyHolder.RAILING_TYPE, RailingType.ROUND);
            }
        }
        otherState = world.getBlockState(pos.offset(facing).offset(state.getValue(PropertyHolder.RIGHT) ? facing.rotateY() : facing.rotateYCCW()));
        if (otherState.getBlock() == this)
        {
            EnumFacing otherFacing = otherState.getValue(PropertyHolder.FACING_CARDINAL);
            if (otherFacing == (state.getValue(PropertyHolder.RIGHT) ? facing.rotateYCCW() : facing.rotateY()) && state.getValue(PropertyHolder.RIGHT) != otherState.getValue(PropertyHolder.RIGHT))
            {
                return state.withProperty(PropertyHolder.RAILING_TYPE, RailingType.CORNER);
            }
        }
        otherState = world.getBlockState(pos.down().offset(state.getValue(PropertyHolder.RIGHT) ? facing.rotateY() : facing.rotateYCCW()));
        if (otherState.getBlock() == this)
        {
            return state.withProperty(PropertyHolder.RAILING_TYPE, RailingType.NORMAL);
        }
        otherState = world.getBlockState(pos.up().offset(facing));
        if (otherState.getBlock() != this && world.getBlockState(pos.down().offset(facing.getOpposite())).getBlock() != this)
        {
            return state.withProperty(PropertyHolder.RAILING_TYPE, RailingType.TB);
        }
        otherState = world.getBlockState(pos.up().offset(facing));
        if (otherState.getBlock() != this)
        {
            return state.withProperty(PropertyHolder.RAILING_TYPE, RailingType.TOP);
        }
        otherState = world.getBlockState(pos.down().offset(facing.getOpposite()));
        if (otherState.getBlock() != this)
        {
            return state.withProperty(PropertyHolder.RAILING_TYPE, RailingType.BOTTOM);
        }
        return state.withProperty(PropertyHolder.RAILING_TYPE, RailingType.NORMAL);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        world.setBlockState(pos.up(), Content.blockStairRailingDummy.getStateFromMeta(getMetaFromState(state)));
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        super.breakBlock(world, pos, state);
        world.setBlockToAir(pos.up());
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos)
    {
        boolean right = state.getValue(PropertyHolder.RIGHT);
        switch (state.getValue(PropertyHolder.FACING_CARDINAL))
        {
            case NORTH: return right ? new AxisAlignedBB(13F/16F, 0, 0, 15F/16F, 1, 1) : new AxisAlignedBB(1F/16F, 0, 0, 3F/16F, 1, 1);
            case SOUTH: return right ? new AxisAlignedBB(1F/16F, 0, 0, 3F/16F, 1, 1) : new AxisAlignedBB(13F/16F, 0, 0, 15F/16F, 1, 1);
            case WEST:  return right ? new AxisAlignedBB(0, 0, 1F/16F, 1, 1, 3F/16F) : new AxisAlignedBB(0, 0, 13F/16F, 1, 1, 15F/16F);
            case EAST:  return right ? new AxisAlignedBB(0, 0, 13F/16F, 1, 1, 15F/16F) : new AxisAlignedBB(0, 0, 1F/16F, 1, 1, 3F/16F);
        }
        return super.getCollisionBoundingBox(state, world, pos);
    }

    //@Override //TODO: implement this if we add metal railing
    //public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity)
    //{
    //    return super.getSoundType(state, world, pos, entity);
    //}

    @Override
    public boolean isCompleteBlock(IBlockState state)
    {
        return false;
    }
}