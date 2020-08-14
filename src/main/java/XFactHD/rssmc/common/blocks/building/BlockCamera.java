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
import XFactHD.rssmc.api.block.IShockable;
import XFactHD.rssmc.api.block.IShootable;
import XFactHD.rssmc.common.blocks.BlockBase;
import XFactHD.rssmc.common.data.team.StatusController;
import XFactHD.rssmc.common.entity.camera.EntityCamera;
import XFactHD.rssmc.common.items.itemBlocks.ItemBlockBase;
import XFactHD.rssmc.common.utils.helper.LogHelper;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
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
public class BlockCamera extends BlockBase implements IShockable, IShootable
{
    public BlockCamera()
    {
        super("blockCamera", Material.GLASS, RainbowSixSiegeMC.CT.buildingTab, ItemBlockBase.class, null);
        setSoundType(SoundType.GLASS);
        setDefaultState(super.getDefaultState().withProperty(PropertyHolder.ACTIVE, false).withProperty(PropertyHolder.DESTROYED, false));
        registerTileEntity(TileEntityCamera.class, "Camera");
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PropertyHolder.ACTIVE, PropertyHolder.DESTROYED);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(PropertyHolder.DESTROYED, meta == 1);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(PropertyHolder.DESTROYED) ? 1 : 0;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityCamera)
        {
            return state.withProperty(PropertyHolder.ACTIVE, ((TileEntityCamera)te).isActive());
        }
        return state;
    }

    @Override
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side)
    {
        return canPlaceBlockAt(world, pos) && side == EnumFacing.DOWN;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        if (!world.isRemote)
        {
            EntityCamera cam = new EntityCamera(world);
            cam.setPosition((double) pos.getX() + .5, (double) pos.getY() + .9, (double) pos.getZ() + .5);
            //cam.setPositionDescription(StatusController.getPosDescription(world, pos)); //TODO: implement
            if (!world.spawnEntity(cam))
            {
                LogHelper.info("Spawn failed!");
                world.setBlockToAir(pos);
            }
            else
            {
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof TileEntityCamera)
                {
                    ((TileEntityCamera)te).setEntity(cam);
                }
                else
                {
                    cam.setDead();
                }
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote && heldItem != null && player.isCreative() && heldItem.getItem() == Item.getItemFromBlock(Blocks.GLASS) && state.getValue(PropertyHolder.DESTROYED))
        {
            world.setBlockState(pos, state.withProperty(PropertyHolder.DESTROYED, false));
        }
        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityCamera)
        {
            ((TileEntityCamera)te).killEntity();
        }
        super.breakBlock(world, pos, state);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World world, BlockPos pos)
    {
        return new AxisAlignedBB(.3125, .8125, .3125, .6875, 1, .6875);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityCamera();
    }

    @Override
    public boolean onBlockShot(World world, BlockPos pos, IBlockState state, EntityPlayer player, float hitX, float hitY, float hitZ, EnumFacing sideHit)
    {
        if (!state.getValue(PropertyHolder.DESTROYED))
        {
            world.setBlockState(pos, state.withProperty(PropertyHolder.DESTROYED, true).withProperty(PropertyHolder.ACTIVE, false));
            return true;
        }
        return false;
    }

    @Override
    public boolean onBlockDestroyedByShot(World world, BlockPos pos, IBlockState state, EntityPlayer player, float hitX, float hitY, float hitZ, EnumFacing sideHit) { return false; }

    @Override
    public boolean canBeShocked(IBlockState state)
    {
        return true;
    }

    @Override
    public boolean shock(World world, BlockPos pos, IBlockState state, EntityPlayer player, float hitX, float hitY, float hitZ)
    {
        if (!state.getValue(PropertyHolder.DESTROYED))
        {
            world.setBlockState(pos, state.withProperty(PropertyHolder.DESTROYED, true).withProperty(PropertyHolder.ACTIVE, false));
            return true;
        }
        return false;
    }

    @Override
    public boolean isCompleteBlock(IBlockState state)
    {
        return false;
    }
}