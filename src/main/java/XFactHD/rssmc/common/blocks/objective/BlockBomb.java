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

package XFactHD.rssmc.common.blocks.objective;

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.common.blocks.BlockBase;
import XFactHD.rssmc.common.items.itemBlocks.ItemBlockBase;
import XFactHD.rssmc.common.utils.Reference;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class BlockBomb extends BlockBase
{
    public BlockBomb()
    {
        super("blockBomb", Material.IRON, RainbowSixSiegeMC.CT.miscTab, ItemBlockBase.class, null);
        setDefaultState(super.getDefaultState().withProperty(PropertyHolder.TOP, false).withProperty(PropertyHolder.DEFUSING, false).withProperty(PropertyHolder.DEFUSED, false));
        registerTileEntity(TileEntityBomb.class, "Bomb");
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PropertyHolder.TOP, PropertyHolder.DEFUSING, PropertyHolder.DEFUSED);
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos)
    {
        return super.canPlaceBlockAt(world, pos) && world.getBlockState(pos.up()).getBlock().isReplaceable(world, pos.up());
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(PropertyHolder.TOP, meta == 1 || meta == 3).withProperty(PropertyHolder.DEFUSING, meta == 2 || meta == 3);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        boolean top = state.getValue(PropertyHolder.TOP);
        boolean defusing = state.getValue(PropertyHolder.DEFUSING);
        return top && defusing ? 3 : (defusing ? 2 : (top ? 1 : 0));
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        boolean defusing = false;
        boolean defused = false;
        TileEntity te = world.getTileEntity(state.getValue(PropertyHolder.TOP) ? pos.down() : pos);
        if (te instanceof TileEntityBomb)
        {
            defusing = ((TileEntityBomb)te).isDefusing();
            defused = ((TileEntityBomb)te).isDefused();
        }
        return state.withProperty(PropertyHolder.DEFUSING, defusing).withProperty(PropertyHolder.DEFUSED, defused);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        world.setBlockState(pos.up(), state.withProperty(PropertyHolder.TOP, true));
        if (!world.isRemote)
        {
            ((EntityPlayer)placer).openGui(RainbowSixSiegeMC.INSTANCE, Reference.GUI_ID_BOMB, world, pos.getX(), pos.getY(), pos.getZ());
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        TileEntity te = world.getTileEntity(state.getValue(PropertyHolder.TOP) ? pos.down() : pos);
        if (te instanceof TileEntityBomb && player.isCreative() && heldItem == null && player.isSneaking())
        {
            if (!world.isRemote)
            {
                ((TileEntityBomb)te).reset();
            }
            return true;
        }
        return false;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        super.breakBlock(world, pos, state);
        if (state.getValue(PropertyHolder.TOP))
        {
            world.destroyBlock(pos.down(), false);
        }
        else
        {
            world.destroyBlock(pos.up(), false);
        }
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return state.getValue(PropertyHolder.TOP) ? null : new TileEntityBomb();
    }

    @Override
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
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

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isUnbreakableInSurvivalMode(IBlockState state)
    {
        return true;
    }
}