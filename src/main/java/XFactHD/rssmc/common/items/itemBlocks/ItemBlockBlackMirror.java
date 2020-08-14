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

package XFactHD.rssmc.common.items.itemBlocks;

import XFactHD.rssmc.common.Content;
import XFactHD.rssmc.common.blocks.BlockBase;
import XFactHD.rssmc.common.blocks.building.BlockReinforcement;
import XFactHD.rssmc.common.blocks.building.BlockWall;
import XFactHD.rssmc.common.blocks.gadget.TileEntityBlackMirror;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import XFactHD.rssmc.common.utils.propertyEnums.Connection;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

//TODO: implement IItemUsageTimer
public class ItemBlockBlackMirror extends ItemBlockBase
{
    public ItemBlockBlackMirror(BlockBase block)
    {
        super(block);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        IBlockState state = world.getBlockState(pos);
        if (side != EnumFacing.UP && side != EnumFacing.DOWN && !world.isRemote)
        {
            if (otherMirrorInRange(world, pos, side)) { return EnumActionResult.FAIL; }
            if (state.getBlock() instanceof BlockWall)
            {
                IBlockState stateRight = world.getBlockState(pos.offset(side.rotateYCCW()));
                if (stateRight == state)
                {
                    IBlockState mirror = Content.blockBlackMirror.getDefaultState().withProperty(PropertyHolder.FACING_CARDINAL, side.getOpposite());
                    --stack.stackSize;
                    world.setBlockState(pos, mirror.withProperty(PropertyHolder.RIGHT, false));
                    world.setBlockState(pos.offset(side.rotateYCCW()), mirror.withProperty(PropertyHolder.RIGHT, true));
                    TileEntity te = world.getTileEntity(pos);
                    TileEntity te2 = world.getTileEntity(pos.offset(side.rotateYCCW()));
                    if (te instanceof TileEntityBlackMirror && te2 instanceof TileEntityBlackMirror)
                    {
                        ((TileEntityBlackMirror)te).setOnReinforcement(false);
                        ((TileEntityBlackMirror)te2).setOnReinforcement(false);
                    }
                    return EnumActionResult.SUCCESS;
                }
            }
            else if (state.getBlock() == Content.blockReinforcement)
            {
                IBlockState stateRight = world.getBlockState(pos.offset(side.rotateYCCW()));
                if (stateRight.getBlock() == state.getBlock() && state.getValue(PropertyHolder.REINFORCEMENT_CONNECTION) == Connection.UDR)
                {
                    IBlockState mirror = Content.blockBlackMirror.getDefaultState().withProperty(PropertyHolder.FACING_CARDINAL, side.getOpposite());
                    --stack.stackSize;
                    world.setBlockState(pos, mirror.withProperty(PropertyHolder.RIGHT, false));
                    world.setBlockState(pos.offset(side.rotateYCCW()), mirror.withProperty(PropertyHolder.RIGHT, true));
                    TileEntity te = world.getTileEntity(pos);
                    TileEntity te2 = world.getTileEntity(pos.offset(side.rotateYCCW()));
                    if (te instanceof TileEntityBlackMirror && te2 instanceof TileEntityBlackMirror)
                    {
                        ((TileEntityBlackMirror)te).setOnReinforcement(true);
                        ((TileEntityBlackMirror)te2).setOnReinforcement(true);
                    }
                    return EnumActionResult.SUCCESS;
                }
            }
        }
        return EnumActionResult.FAIL;
    }

    private boolean otherMirrorInRange(World world, BlockPos pos, EnumFacing side)
    {
        if (world.getBlockState(pos).getBlock() instanceof BlockReinforcement)
        {
            return false;
        }
        else
        {
            int distDown = 0;
            int distUp = 0;
            Block b = world.getBlockState(pos).getBlock();
            for (BlockPos p : BlockPos.getAllInBox(pos.down(8), pos.up(8).offset(side.rotateYCCW())))
            {
                if (p.getY() < pos.getY() && world.getBlockState(p).getBlock() != b && (distDown == 0 || (pos.getY() - p.getY()) > distDown))
                {
                    distDown = pos.getY() - p.getY() - 1;
                }
                else if (p.getY() > pos.getY() && world.getBlockState(p).getBlock() != b && (distUp == 0 || (p.getY() - pos.getY()) > distUp))
                {
                    distUp = p.getY() - pos.getY() - 1;
                }
            }
            for (BlockPos p : BlockPos.getAllInBox(pos.down(distDown), pos.up(distUp)))
            {
                if (world.getBlockState(p).getBlock() == block)
                {
                    return true;
                }
            }
        }
        return false;
    }
}