/*  Copyright (C) <2016>  <XFactHD, DrakoAlcarus>

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
import XFactHD.rssmc.api.block.IExplosive;
import XFactHD.rssmc.common.Content;
import XFactHD.rssmc.common.blocks.BlockGadget;
import XFactHD.rssmc.common.blocks.building.BlockBarricade;
import XFactHD.rssmc.common.blocks.building.BlockFloorPanel;
import XFactHD.rssmc.common.items.itemBlocks.ItemBlockGadget;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class BlockClusterCharge extends BlockGadget implements IExplosive
{
    public BlockClusterCharge()
    {
        super("blockClusterCharge", Material.IRON, RainbowSixSiegeMC.CT.gadgetTab, null, null);
        registerTileEntity(TileEntityClusterCharge.class, "ClusterCharge");
        registerSpecialItemBlock(new ItemBlockGadget(this, 50)); //TODO: consider adding placement animation while timer counts
        setDefaultState(super.getDefaultState().withProperty(PropertyHolder.ACTIVATED, false));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PropertyHolder.FACING_NOT_UP, PropertyHolder.ACTIVATED);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        boolean active = false;
        if (meta > 5)
        {
            active = true;
            meta -= 10;
        }
        return getDefaultState().withProperty(PropertyHolder.FACING_NOT_UP, meta == 1 ? EnumFacing.NORTH : EnumFacing.getFront(meta)).withProperty(PropertyHolder.ACTIVATED, active);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(PropertyHolder.FACING_NOT_UP).getIndex() + (state.getValue(PropertyHolder.ACTIVATED) ? 10 : 0);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityClusterCharge)
        {
            return state.withProperty(PropertyHolder.ACTIVATED, ((TileEntityClusterCharge)te).isActivated());
        }
        return state;
    }

    @Override
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side)
    {
        IBlockState state = world.getBlockState(pos.offset(side.getOpposite()));
        boolean canPlace = state.getBlock() instanceof BlockBarricade || (state.getBlock() instanceof BlockFloorPanel && !state.getValue(PropertyHolder.SOLID));
        return super.canPlaceBlockOnSide(world, pos, side) && side != EnumFacing.DOWN && canPlace;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, ItemStack stack)
    {
        return !canPlayerPlaceBlock((EntityPlayer) placer) ? Blocks.AIR.getDefaultState() : getDefaultState().withProperty(PropertyHolder.FACING_NOT_UP, side.getOpposite());
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        ItemStack activator = new ItemStack(Content.itemActivator);
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("object", getObjectName());
        nbt.setLong("pos", pos.toLong());
        activator.setTagCompound(nbt);
        ((EntityPlayer) placer).inventory.addItemStackToInventory(activator);
    }

    @Override
    public boolean needsSpecialDestructionHandling()
    {
        return false;
    }

    @Override
    public boolean canBePickedUp(IBlockState state)
    {
        return !state.getValue(PropertyHolder.ACTIVATED);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos)
    {
        switch (state.getValue(PropertyHolder.FACING_NOT_UP))
        {
            case NORTH: return new AxisAlignedBB(2F/16F, 2F/16F,      0, 14F/16F, 14F/16F,  7F/16F);
            case EAST:  return new AxisAlignedBB(9F/16F, 2F/16F, 2F/16F,       1, 14F/16F, 14F/16F);
            case SOUTH: return new AxisAlignedBB(2F/16F, 2F/16F, 9F/16F, 14F/16F, 14F/16F,       1);
            case WEST:  return new AxisAlignedBB(     0, 2F/16F, 2F/16F,  7F/16F, 14F/16F, 14F/16F);
            case DOWN:  return new AxisAlignedBB(2F/16F,      0, 2F/16F, 14F/16F,  7F/16F, 14F/16F);
            default: return FULL_BLOCK_AABB;
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
        return new TileEntityClusterCharge();
    }

    @Override
    public boolean onlyOnePerPlayer()
    {
        return true;
    }

    @Override
    public String getObjectName()
    {
        return "cluster_charge";
    }

    @Override
    public void activate(World world, BlockPos pos, IBlockState state)
    {
        explode(world, pos, state);
    }

    @Override
    public void explode(World world, BlockPos pos, IBlockState state)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityClusterCharge)
        {
            ((TileEntityClusterCharge)te).activate();
        }
    }

    @Override
    public boolean canBeShocked(IBlockState state)
    {
        return true;
    }
}