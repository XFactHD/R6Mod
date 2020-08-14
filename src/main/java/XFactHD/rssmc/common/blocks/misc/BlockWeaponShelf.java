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
import XFactHD.rssmc.common.items.gun.ItemGun;
import XFactHD.rssmc.common.items.itemBlocks.ItemBlockBase;
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
import net.minecraft.world.World;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class BlockWeaponShelf extends BlockBase
{
    public BlockWeaponShelf()
    {
        super("blockWeaponShelf", Material.IRON, RainbowSixSiegeMC.CT.miscTab, ItemBlockBase.class, null);
        registerTileEntity(TileEntityWeaponShelf.class, "WeaponShelf");
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PropertyHolder.FACING_CARDINAL);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        if (meta < 2)
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
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return getDefaultState().withProperty(PropertyHolder.FACING_CARDINAL, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (side != state.getValue(PropertyHolder.FACING_CARDINAL)) { return false; }

        float subHit = 0;

        switch (state.getValue(PropertyHolder.FACING_CARDINAL))
        {
            case NORTH: subHit = hitX; break;
            case EAST:  subHit = hitZ; break;
            case SOUTH: subHit = hitX; break;
            case WEST:  subHit = hitZ; break;
        }

        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityWeaponShelf)
        {
            if (heldItem != null && !(heldItem.getItem() instanceof ItemGun))
            {
                return false;
            }
            else if (heldItem == null)
            {
                if (world.isRemote)
                {
                    return ((TileEntityWeaponShelf)te).removeGun(subHit) != null;
                }
                else
                {
                    ItemStack stack = ((TileEntityWeaponShelf)te).removeGun(subHit);
                    if (stack != null)
                    {
                        if (!player.inventory.addItemStackToInventory(stack))
                        {
                            player.dropItem(stack, false);
                        }
                        player.inventory.markDirty();
                        return true;
                    }
                }
            }
            else if (heldItem.getItem() instanceof ItemGun)
            {
                if (world.isRemote)
                {
                    return ((TileEntityWeaponShelf)te).addGun(heldItem, subHit);
                }
                else
                {
                    if (((TileEntityWeaponShelf)te).addGun(heldItem, subHit))
                    {
                        player.inventory.removeStackFromSlot(player.inventory.currentItem);
                        player.inventory.markDirty();
                        return true;
                    }
                    return false;
                }
            }
        }
        return false;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World worldIn, BlockPos pos)
    {
        switch (state.getValue(PropertyHolder.FACING_CARDINAL))
        {
            case NORTH: return new AxisAlignedBB(   0, 0, .625,    1, 1,    1);
            case EAST:  return new AxisAlignedBB(   0, 0,    0, .375, 1,    1);
            case SOUTH: return new AxisAlignedBB(   0, 0,    0,    1, 1, .375);
            case WEST:  return new AxisAlignedBB(.625, 0,    0,    1, 1,    1);
            default: return super.getCollisionBoundingBox(state, worldIn, pos);
        }
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

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityWeaponShelf();
    }
}