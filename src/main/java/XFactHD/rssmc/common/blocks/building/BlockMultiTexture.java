/*  Copyright (C) <2016>  <XFactHD>

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
import XFactHD.rssmc.common.utils.properties.PropertyBlockState;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class BlockMultiTexture extends BlockBase
{
    public BlockMultiTexture()
    {
        super("blockMultiTexture", Material.ROCK, RainbowSixSiegeMC.CT.buildingTab, ItemBlockBase.class, null);
        registerTileEntity(TileEntityMultiTexture.class, "MultiTexture");
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[]
                {
                        PropertyHolder.BLOCK_STATE_U,
                        PropertyHolder.BLOCK_STATE_D,
                        PropertyHolder.BLOCK_STATE_N,
                        PropertyHolder.BLOCK_STATE_E,
                        PropertyHolder.BLOCK_STATE_S,
                        PropertyHolder.BLOCK_STATE_W,
                });
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityMultiTexture)
        {
            IExtendedBlockState extState = (IExtendedBlockState)state;
            for (EnumFacing side : EnumFacing.values())
            {
                extState = extState.withProperty(getStateForSide(side), ((TileEntityMultiTexture)te).getStateForSide(side));
            }
            return extState;
        }
        return state;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (heldItem != null && heldItem.getItem() instanceof ItemBlock)
        {
            Block block = ((ItemBlock)heldItem.getItem()).getBlock();
            IBlockState sideState = block.getStateFromMeta(heldItem.getMetadata());
            TileEntity te = world.getTileEntity(pos);
            if (!block.hasTileEntity(sideState) && te instanceof TileEntityMultiTexture && !isNeitherSameBlockNorAirAndIsOpaque(te, side, sideState))
            {
                if (!world.isRemote)
                {
                    ((TileEntityMultiTexture)te).setStateForSide(side, sideState);
                    if (!player.capabilities.isCreativeMode)
                    {
                        --heldItem.stackSize;
                        player.inventory.markDirty();
                    }
                }
                return true;
            }
        }
        else if (heldItem == null && player.isSneaking())
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityMultiTexture)
            {
                IBlockState sideState = ((TileEntityMultiTexture)te).getStateForSide(side);
                if (sideState.getBlock() != Blocks.AIR)
                {
                    if (!world.isRemote)
                    {
                        ((TileEntityMultiTexture)te).clearSide(side);
                        if (!player.capabilities.isCreativeMode)
                        {
                            ItemStack stack = new ItemStack(sideState.getBlock(), 1, sideState.getBlock().getMetaFromState(sideState));
                            if (!player.inventory.addItemStackToInventory(stack))
                            {
                                player.dropItem(stack, false);
                            }
                            player.inventory.markDirty();
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        List<ItemStack> stacks = new ArrayList<>();
        stacks.add(new ItemStack(this));
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityMultiTexture)
        {
            for (EnumFacing side : EnumFacing.values())
            {
                IBlockState sideState = ((TileEntityMultiTexture)te).getStateForSide(side);
                if (sideState.getBlock() != Blocks.AIR)
                {
                    stacks.add(new ItemStack(sideState.getBlock(), 1, sideState.getBlock().getMetaFromState(sideState)));
                }
            }
        }
        return stacks;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityMultiTexture();
    }

    private boolean isNeitherSameBlockNorAirAndIsOpaque(TileEntity te, EnumFacing side, IBlockState state)
    {
        if (state.getMaterial() == Material.AIR)
        {
            return false;
        }
        if (!state.getBlock().isOpaqueCube(state) || !state.getBlock().isFullBlock(state))
        {
            return false;
        }
        if (te instanceof TileEntityMultiTexture)
        {
            return state == ((TileEntityMultiTexture)te).getStateForSide(side);
        }
        return false;
    }

    private PropertyBlockState getStateForSide(EnumFacing side)
    {
        switch (side)
        {
            case DOWN:  return PropertyHolder.BLOCK_STATE_D;
            case UP:    return PropertyHolder.BLOCK_STATE_U;
            case NORTH: return PropertyHolder.BLOCK_STATE_N;
            case SOUTH: return PropertyHolder.BLOCK_STATE_S;
            case WEST:  return PropertyHolder.BLOCK_STATE_W;
            case EAST:  return PropertyHolder.BLOCK_STATE_E;
            default: return PropertyHolder.BLOCK_STATE_U;
        }
    }
}