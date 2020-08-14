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
import XFactHD.rssmc.common.items.itemBlocks.ItemBlockBase;
import XFactHD.rssmc.common.items.itemBlocks.ItemBlockGadget;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("deprecation")
public class BlockActiveDefenseSystem extends BlockGadget
{
    public BlockActiveDefenseSystem()
    {
        super("block_ads", Material.IRON, RainbowSixSiegeMC.CT.gadgetTab, null, null);
        registerSpecialItemBlock(new ItemBlockGadget(this, 50));
        registerTileEntity(TileEntityActiveDefenseSystem.class, "ADS");
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PropertyHolder.FACING_NOT_DOWN, PropertyHolder.LOADED);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        if (meta == 0) { meta = 1; }
        return getDefaultState().withProperty(PropertyHolder.FACING_NOT_DOWN, EnumFacing.getFront(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(PropertyHolder.FACING_NOT_DOWN).getIndex();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        boolean loaded = false;
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityActiveDefenseSystem)
        {
            loaded = ((TileEntityActiveDefenseSystem)te).getBullets() > 0;
        }
        return state.withProperty(PropertyHolder.LOADED, loaded);
    }

    @Override
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side)
    {
        return side != EnumFacing.DOWN;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, ItemStack stack)
    {
        return getDefaultState().withProperty(PropertyHolder.FACING_NOT_DOWN, side);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityActiveDefenseSystem)
        {
            int bullets = stack.hasTagCompound() ? stack.getTagCompound().getInteger("bullets") : 2;
            ((TileEntityActiveDefenseSystem) te).setBullets(bullets);
        }
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos)
    {
        switch (state.getValue(PropertyHolder.FACING_NOT_DOWN))
        {
            case UP:    return new AxisAlignedBB(      0, 0,       0,      1, 6F/16F,      1);
            case NORTH: return new AxisAlignedBB(      0, 0, 10F/16F,      1,      1,      1);
            case SOUTH: return new AxisAlignedBB(      0, 0,       0,      1,      1, 6F/16F);
            case WEST:  return new AxisAlignedBB(10F/16F, 0,       0,      1,      1,      1);
            case EAST:  return new AxisAlignedBB(      0, 0,       0, 6F/16F,      1,      1);
            default:    return super.getCollisionBoundingBox(state, world, pos);
        }
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityActiveDefenseSystem)
        {
            ItemStack stack = new ItemStack(this);
            stack.setTagCompound(new NBTTagCompound());
            stack.getTagCompound().setInteger("bullets", ((TileEntityActiveDefenseSystem) te).getBullets());
            return Collections.singletonList(stack);
        }
        return super.getDrops(world, pos, state, fortune);
    }

    @Override
    public boolean isCompleteBlock(IBlockState state)
    {
        return false;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityActiveDefenseSystem();
    }

    @Override
    public boolean canBePickedUp(IBlockState state)
    {
        return true;
    }

    @Override
    public boolean needsSpecialDestructionHandling()
    {
        return false;
    }

    @Override
    public boolean canBeShocked(IBlockState state)
    {
        return true;
    }
}