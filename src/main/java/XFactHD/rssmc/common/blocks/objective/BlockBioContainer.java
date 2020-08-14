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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class BlockBioContainer extends BlockBase
{
    public BlockBioContainer()
    {
        super("blockBioContainer", Material.IRON, RainbowSixSiegeMC.CT.miscTab, ItemBlockBase.class, null);
        registerTileEntity(TileEntityBioContainer.class, "BioContainer");
        setDefaultState(super.getDefaultState().withProperty(PropertyHolder.SECURING, false));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PropertyHolder.FACING_CARDINAL, PropertyHolder.SECURING);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(PropertyHolder.FACING_CARDINAL, meta < 2 ? EnumFacing.NORTH : EnumFacing.getFront(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(PropertyHolder.FACING_CARDINAL).getIndex();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityBioContainer)
        {
            return state.withProperty(PropertyHolder.SECURING, ((TileEntityBioContainer)te).isSecuring());
        }
        return state;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, ItemStack stack)
    {
        return getDefaultState().withProperty(PropertyHolder.FACING_CARDINAL, placer.getHorizontalFacing());
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        if (!world.isRemote)
        {
            ((EntityPlayer)placer).openGui(RainbowSixSiegeMC.INSTANCE, Reference.GUI_ID_BIO_CONTAINER, world, pos.getX(), pos.getY(), pos.getZ());
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityBioContainer && player.isCreative() && heldItem == null && player.isSneaking())
        {
            if (!world.isRemote)
            {
                ((TileEntityBioContainer)te).reset();
            }
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos)
    {
        switch (state.getValue(PropertyHolder.FACING_CARDINAL))
        {
            case NORTH:
            case SOUTH: return new AxisAlignedBB(0, 0, 3F/16F, 1, 1, 13F/16F);
            case EAST:
            case WEST:  return new AxisAlignedBB(3F/16F, 0, 0, 13F/16F, 1, 1);
            default: return null;
        }
    }

    @Override
    public boolean isCompleteBlock(IBlockState state)
    {
        return false;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityBioContainer();
    }
}