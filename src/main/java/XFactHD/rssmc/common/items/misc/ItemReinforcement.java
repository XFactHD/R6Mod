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

package XFactHD.rssmc.common.items.misc;

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.common.blocks.building.BlockDropHatch;
import XFactHD.rssmc.common.blocks.building.BlockReinforcement;
import XFactHD.rssmc.common.blocks.building.BlockWall;
import XFactHD.rssmc.common.items.ItemBase;
import XFactHD.rssmc.common.net.PacketSetBlockToReinforce;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import XFactHD.rssmc.common.utils.propertyEnums.Connection;
import XFactHD.rssmc.common.utils.propertyEnums.WallType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

@SuppressWarnings("deprecation") //TODO: rework with IItemUsageTimer and try to remove dependence on network packets
public class ItemReinforcement extends ItemBase
{
    public ItemReinforcement()
    {
        super("itemReinforcement", 2, RainbowSixSiegeMC.CT.buildingTab, null);
    }

    @Override
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
    {
        if (player instanceof FakePlayer)
        {
            return EnumActionResult.PASS;
        }
        if (side == EnumFacing.UP)
        {
            return checkPlacement(world, pos, pos.offset(player.getHorizontalFacing()).offset(player.getHorizontalFacing().rotateY()), EnumFacing.UP, player.getHorizontalFacing());
        }
        else if (side != EnumFacing.DOWN)
        {
            return checkPlacement(world, pos, pos.up(2).offset(side.rotateYCCW()), side, player.getHorizontalFacing());
        }
        return EnumActionResult.PASS;
    }

    private EnumActionResult checkPlacement(World world, BlockPos minPos, BlockPos maxPos, EnumFacing side, EnumFacing rotation)
    {
        IBlockState stateClicked = world.getBlockState(minPos);
        if (isReinforcement(stateClicked) || !isApropriateBlock(stateClicked, side))
        {
            return EnumActionResult.PASS;
        }
        if (!checkSurrounding(world, minPos, side, rotation))
        {
            return EnumActionResult.PASS;
        }
        for (BlockPos pos : BlockPos.getAllInBox(minPos, maxPos))
        {
            IBlockState state = world.getBlockState(pos);
            if (stateClicked.getBlock() != state.getBlock() || (side != EnumFacing.UP && stateClicked.getBlock().getMetaFromState(stateClicked) != state.getBlock().getMetaFromState(state)))
            {
                return EnumActionResult.PASS;
            }
        }
        for (BlockPos pos : BlockPos.getAllInBox(minPos, maxPos))
        {
            Connection con = side == EnumFacing.UP ?
                    getConnectionFromBlockPosAndPlayerFacing(minPos, pos, rotation) :
                    Connection.fromBlockPositions(minPos, pos, side);

            IBlockState state = world.getBlockState(pos);
            state = state.getBlock().getActualState(state, world, pos);
            RainbowSixSiegeMC.NET.sendMessageToServer(new PacketSetBlockToReinforce(pos, state, side, con));
        }
        return EnumActionResult.SUCCESS;
    }

    private boolean isReinforcement(IBlockState state)
    {
        return state.getBlock() instanceof BlockReinforcement;
    }

    private boolean isApropriateBlock(IBlockState state, EnumFacing side)
    {
        if (side == EnumFacing.UP)
        {
            return state.getBlock() instanceof BlockDropHatch;
        }
        return state.getBlock() instanceof BlockWall && state.getValue(PropertyHolder.WALL_TYPE) == WallType.NORMAL;
    }

    private Connection getConnectionFromBlockPosAndPlayerFacing(BlockPos posMin, BlockPos pos, EnumFacing facing)
    {
        BlockPos posUp = posMin.offset(facing);
        BlockPos posRight = posMin.offset(facing.rotateY());
        BlockPos posUpRight = posMin.offset(facing).offset(facing.rotateY());
        if (pos.equals(posMin))
        {
            switch (facing)
            {
                case NORTH: return Connection.UR;
                case EAST:  return Connection.DR;
                case SOUTH: return Connection.DL;
                case WEST:  return Connection.UL;
            }
        }
        else if (pos.equals(posUp))
        {
            switch (facing)
            {
                case NORTH: return Connection.DR;
                case EAST:  return Connection.DL;
                case SOUTH: return Connection.UL;
                case WEST:  return Connection.UR;
            }
        }
        else if (pos.equals(posRight))
        {
            switch (facing)
            {
                case NORTH: return Connection.UL;
                case EAST:  return Connection.UR;
                case SOUTH: return Connection.DR;
                case WEST:  return Connection.DL;
            }
        }
        else if (pos.equals(posUpRight))
        {
            switch (facing)
            {
                case NORTH: return Connection.DL;
                case EAST:  return Connection.UL;
                case SOUTH: return Connection.UR;
                case WEST:  return Connection.DR;
            }
        }
        return null;
    }

    private boolean checkSurrounding(World world, BlockPos pos, EnumFacing side, EnumFacing direction)
    {
        if (side != EnumFacing.UP)
        {
            return true;
        }
        //Check for air below trap door
        for (BlockPos checkPos : BlockPos.getAllInBox(pos.down(), pos.down().offset(direction).offset(direction.rotateY())))
        {
            if (!world.isAirBlock(checkPos))
            {
                return false;
            }
        }
        //Check for other reinforcements adjacent to this one
        BlockPos minPos = pos.offset(direction.getOpposite()).offset(direction.rotateYCCW());
        BlockPos maxPos = pos.offset(direction, 2).offset(direction.rotateY(), 2);
        for (BlockPos checkPos : BlockPos.getAllInBox(minPos, maxPos))
        {
            if (world.getBlockState(checkPos).getBlock() instanceof BlockReinforcement)
            {
                return false;
            }
        }
        return true;
    }
}