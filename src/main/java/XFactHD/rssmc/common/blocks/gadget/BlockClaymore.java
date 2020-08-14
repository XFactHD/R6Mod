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
import XFactHD.rssmc.common.blocks.BlockGadget;
import XFactHD.rssmc.common.items.itemBlocks.ItemBlockGadget;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class BlockClaymore extends BlockGadget
{
    public BlockClaymore()
    {
        super("blockClaymore", Material.IRON, RainbowSixSiegeMC.CT.gadgetTab, null, null);
        setDefaultState(super.getDefaultState().withProperty(PropertyHolder.FACING_CARDINAL, EnumFacing.NORTH));
        registerSpecialItemBlock(new ItemBlockGadget(this, 50));
        registerTileEntity(TileEntityClaymore.class, "claymore");
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PropertyHolder.FACING_CARDINAL);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing facing = EnumFacing.NORTH;
        if (EnumFacing.getFront(meta).getAxis().isHorizontal())
        {
            facing = EnumFacing.getFront(meta);
        }
        return getDefaultState().withProperty(PropertyHolder.FACING_CARDINAL, facing);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(PropertyHolder.FACING_CARDINAL).getIndex();
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return getDefaultState().withProperty(PropertyHolder.FACING_CARDINAL, placer.getHorizontalFacing());
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityClaymore)
        {
            ((TileEntityClaymore)te).setFacing(state.getValue(PropertyHolder.FACING_CARDINAL));
        }
    }

    @Override
    public boolean needsSpecialDestructionHandling()
    {
        return false;
    }

    @Override
    public boolean canBePickedUp(IBlockState state)
    {
        return true;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state)
    {
        return false;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos)
    {
        switch (state.getValue(PropertyHolder.FACING_CARDINAL))
        {
            case NORTH: return new AxisAlignedBB(  .3, 0,   .5,   .7, .26, .675);
            case EAST:  return new AxisAlignedBB(.325, 0,   .3,   .5, .26,   .7);
            case SOUTH: return new AxisAlignedBB(  .3, 0, .325,   .7, .26,   .5);
            case WEST:  return new AxisAlignedBB(  .5, 0,   .3, .675, .26,   .7);
            default: return super.getCollisionBoundingBox(state, world, pos);
        }
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityClaymore();
    }

    @Override
    public boolean canBeShocked(IBlockState state)
    {
        return true;
    }
}