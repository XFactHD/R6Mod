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
import XFactHD.rssmc.common.blocks.building.BlockReinforcement;
import XFactHD.rssmc.common.blocks.building.TileEntityReinforcement;
import XFactHD.rssmc.common.items.itemBlocks.ItemBlockGadget;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class BlockShockWire extends BlockGadget
{
    public BlockShockWire()
    {
        super("blockShockWire", Material.IRON, RainbowSixSiegeMC.CT.gadgetTab, null, null);
        registerSpecialItemBlock(new  ItemBlockGadget(this, 50));
        registerTileEntity(TileEntityShockWire.class, "ShockWire");
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PropertyHolder.FACING_NOT_UP);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        if (meta == 0 || meta == 1)
        {
            meta = EnumFacing.NORTH.getIndex();
        }
        return getDefaultState().withProperty(PropertyHolder.FACING_NOT_UP, EnumFacing.getFront(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(PropertyHolder.FACING_NOT_UP).getIndex();
    }

    @Override
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side)
    {
        if (side == EnumFacing.DOWN) { return false; }
        boolean onGround = !world.isAirBlock(pos.down());
        IBlockState state = world.getBlockState(pos.offset(side.getOpposite()));
        if (state.getBlock() instanceof BlockReinforcement)
        {
            boolean electrified = false;
            TileEntity te = world.getTileEntity(pos.offset(side.getOpposite()));
            if (te instanceof TileEntityReinforcement)
            {
                electrified = ((TileEntityReinforcement)te).isElectrified();
            }
            return !electrified && onGround;
        }
        else if (state.getBlock() instanceof BlockDeployableShield)
        {
            boolean electrified = false;
            TileEntity te = world.getTileEntity(pos.offset(side.getOpposite()));
            if (te instanceof TileEntityDeployableShield)
            {
                electrified = ((TileEntityDeployableShield)te).isElectrified();
            }
            return !electrified && onGround && state.getValue(PropertyHolder.FACING_CARDINAL) == side.getOpposite();
        }
        return false;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return getDefaultState().withProperty(PropertyHolder.FACING_NOT_UP, side.getOpposite());
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        TileEntity te = world.getTileEntity(pos.offset(state.getValue(PropertyHolder.FACING_NOT_UP)));
        if (te instanceof TileEntityReinforcement)
        {
            ((TileEntityReinforcement)te).setElectrified(true, true);
        }
        else if (te instanceof TileEntityDeployableShield)
        {
            ((TileEntityDeployableShield)te).setElectrified(true);
        }
    }

    @Override
    public void onBlockDestroyedByPlayer(World world, BlockPos pos, IBlockState state)
    {
        TileEntity te = world.getTileEntity(pos.offset(state.getValue(PropertyHolder.FACING_NOT_UP)));
        if (te instanceof TileEntityReinforcement)
        {
            ((TileEntityReinforcement)te).setElectrified(false, true);
        }
        else if (te instanceof TileEntityDeployableShield)
        {
            ((TileEntityDeployableShield)te).setElectrified(false);
        }
    }

    @Override
    public boolean needsSpecialDestructionHandling()
    {
        return true;
    }

    @Override
    public boolean canBePickedUp(IBlockState state)
    {
        return true;
    }

    @Override
    public void onBlockDestroyed(World world, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        TileEntity te = world.getTileEntity(pos.offset(state.getValue(PropertyHolder.FACING_NOT_UP)));
        if (te instanceof TileEntityReinforcement)
        {
            ((TileEntityReinforcement)te).setElectrified(false, true);
        }
        else if (te instanceof TileEntityDeployableShield)
        {
            ((TileEntityDeployableShield)te).setElectrified(false);
        }
    }

    @Override
    public boolean onBlockDestroyedByShot(World world, BlockPos pos, IBlockState state, EntityPlayer player, float hitX, float hitY, float hitZ, EnumFacing sideHit)
    {
        TileEntity te = world.getTileEntity(pos.offset(state.getValue(PropertyHolder.FACING_NOT_UP)));
        if (te instanceof TileEntityReinforcement)
        {
            ((TileEntityReinforcement)te).setElectrified(false, true);
        }
        else if (te instanceof TileEntityDeployableShield)
        {
            ((TileEntityDeployableShield)te).setElectrified(false);
        }
        return super.onBlockDestroyedByShot(world, pos, state, player, hitX, hitY, hitZ, sideHit);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos)
    {
        switch (state.getValue(PropertyHolder.FACING_NOT_UP))
        {
            case NORTH: return new AxisAlignedBB( .125, 0, .0625,  .875, .4375,  .625);
            case EAST:  return new AxisAlignedBB( .375, 0,  .125, .9375, .4375,  .875);
            case SOUTH: return new AxisAlignedBB( .125, 0,  .375,  .875, .4375, .9375);
            case WEST:  return new AxisAlignedBB(.0625, 0,  .125,  .625, .4375,  .875);
            default: return FULL_BLOCK_AABB;
        }
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityShockWire();
    }

    @Override
    public boolean isCompleteBlock(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean onBlockShot(World world, BlockPos pos, IBlockState state, EntityPlayer player, float hitX, float hitY, float hitZ, EnumFacing sideHit)
    {
        TileEntity te = world.getTileEntity(pos.offset(state.getValue(PropertyHolder.FACING_NOT_UP)));
        if (te instanceof TileEntityReinforcement)
        {
            ((TileEntityReinforcement)te).setElectrified(false, true);
        }
        world.setBlockToAir(pos);
        return true;
    }

    @Override
    public boolean canBeShocked(IBlockState state)
    {
        return true;
    }
}