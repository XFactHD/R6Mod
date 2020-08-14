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
import XFactHD.rssmc.api.block.HitType;
import XFactHD.rssmc.api.block.IDestructable;
import XFactHD.rssmc.common.blocks.BlockBase;
import XFactHD.rssmc.common.data.EnumGadget;
import XFactHD.rssmc.common.items.itemBlocks.ItemBlockBase;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import XFactHD.rssmc.common.utils.propertyEnums.Connection;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class BlockDropHatch extends BlockBase implements IDestructable //TODO: when exploded, don't remove the block and leave the metal frame
{
    public BlockDropHatch()
    {
        super("blockDropHatch", Material.WOOD, RainbowSixSiegeMC.CT.buildingTab, ItemBlockBase.class, null);
        setDefaultState(getDefaultState().withProperty(PropertyHolder.DROP_HATCH_CONNECTION, Connection.UR));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PropertyHolder.DROP_HATCH_CONNECTION);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        if (meta == 0) { meta = 2; }
        return getDefaultState().withProperty(PropertyHolder.DROP_HATCH_CONNECTION, Connection.valueOf(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(PropertyHolder.DROP_HATCH_CONNECTION).ordinal();
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos)
    {
        if (world.getBlockState(pos.north()).getBlock() != Blocks.AIR) { return false; }
        if (world.getBlockState(pos.east()).getBlock() != Blocks.AIR) { return false; }
        if (world.getBlockState(pos.north().east()).getBlock() != Blocks.AIR) { return false; }
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        world.setBlockState(pos.north(), state.withProperty(PropertyHolder.DROP_HATCH_CONNECTION, Connection.DR));
        world.setBlockState(pos.east(), state.withProperty(PropertyHolder.DROP_HATCH_CONNECTION, Connection.UL));
        world.setBlockState(pos.north().east(), state.withProperty(PropertyHolder.DROP_HATCH_CONNECTION, Connection.DL));
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos)
    {
        return new AxisAlignedBB(0, .5F, 0, 1, 1, 1);
    }

    @Override
    public boolean isCompleteBlock(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean canAttachGagdet(World world, BlockPos pos, IBlockState state, EnumGadget gadget, EnumFacing side)
    {
        return gadget == EnumGadget.BREACH_CHARGE && side == EnumFacing.UP;
    }

    @Override
    public void explode(World world, BlockPos pos, IBlockState state, EnumGadget gadget, EnumFacing originatingSide)
    {
        switch (state.getValue(PropertyHolder.DROP_HATCH_CONNECTION))
        {
            case UR:
            {
                BlockPos pos1 = pos.north();
                BlockPos pos2 = pos.east();
                BlockPos pos3 = pos.north().east();
                world.setBlockToAir(pos);
                world.setBlockToAir(pos1);
                world.setBlockToAir(pos2);
                world.setBlockToAir(pos3);
            }
            case UL:
            {
                BlockPos pos1 = pos.north();
                BlockPos pos2 = pos.west();
                BlockPos pos3 = pos.north().west();
                world.setBlockToAir(pos);
                world.setBlockToAir(pos1);
                world.setBlockToAir(pos2);
                world.setBlockToAir(pos3);
            }
            case DR:
            {
                BlockPos pos1 = pos.south();
                BlockPos pos2 = pos.east();
                BlockPos pos3 = pos.south().east();
                world.setBlockToAir(pos);
                world.setBlockToAir(pos1);
                world.setBlockToAir(pos2);
                world.setBlockToAir(pos3);
            }
            case DL:
            {
                BlockPos pos1 = pos.south();
                BlockPos pos2 = pos.west();
                BlockPos pos3 = pos.south().west();
                world.setBlockToAir(pos);
                world.setBlockToAir(pos1);
                world.setBlockToAir(pos2);
                world.setBlockToAir(pos3);
            }
        }
    }

    @Override
    public boolean destruct(World world, BlockPos pos, IBlockState state, HitType type, EnumFacing originatingSide)
    {
        if (type == HitType.HAMMER)
        {
            if (originatingSide == EnumFacing.UP)
            {
                explode(world, pos, state, null, originatingSide);
                return true;
            }
            return false;
        }
        explode(world, pos, state, null, originatingSide);
        return true;
    }
}