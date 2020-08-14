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
import XFactHD.rssmc.api.item.IItemUsageTimer;
import XFactHD.rssmc.common.blocks.building.BlockBarricade;
import XFactHD.rssmc.common.blocks.building.TileEntityBarricade;
import XFactHD.rssmc.common.data.team.StatusController;
import XFactHD.rssmc.common.items.ItemBase;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@SuppressWarnings("ConstantConditions")
public class ItemCrowbar extends ItemBase implements IItemUsageTimer
{
    public ItemCrowbar()
    {
        super("itemCrowbar", 1, RainbowSixSiegeMC.CT.miscTab, null);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        IBlockState state = world.getBlockState(pos);
        TileEntity te = world.getTileEntity(pos);
        if (state.getBlock() instanceof BlockBarricade && te instanceof TileEntityBarricade)
        {
            if (stack.getTagCompound().getLong("time") == 0)
            {
                EntityPlayer owner = ((TileEntityBarricade)te).getOwner();
                if (owner == player || (owner != null && StatusController.arePlayersTeamMates(owner, player)))
                {
                    stack.getTagCompound().setLong("time", world.getTotalWorldTime());
                    stack.getTagCompound().setLong("stamp", world.getTotalWorldTime());
                }
            }
            else
            {
                stack.getTagCompound().setLong("stamp", world.getTotalWorldTime());
                if (!world.isRemote && stack.getTagCompound().getLong("time") != 0 && world.getTotalWorldTime() - stack.getTagCompound().getLong("time") >= getMaxTime(stack))
                {
                    breakBarricade(world, pos, state);
                }
            }
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.FAIL;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected)
    {
        if (!stack.hasTagCompound()) { stack.setTagCompound(new NBTTagCompound()); }
        if (stack.getTagCompound().getLong("time") != 0)
        {
            if (world.getTotalWorldTime() - stack.getTagCompound().getLong("stamp") > 4)
            {
                stack.getTagCompound().setLong("time", 0);
                stack.getTagCompound().setLong("stamp", 0);
            }
        }
    }

    @Override
    public int getCurrentTime(World world, ItemStack stack, EntityPlayer player)
    {
        return (int)(world.getTotalWorldTime() - stack.getTagCompound().getLong("time"));
    }

    @Override
    public int getMaxTime(ItemStack stack)
    {
        return 40;
    }

    @Override
    public boolean isInUse(World world, ItemStack stack, EntityPlayer player)
    {
        return stack.hasTagCompound() && stack.getTagCompound().getLong("time") != 0;
    }

    @Override
    public String getDescription()
    {
        return "desc.rssmc:remove_barricade.name";
    }

    private void breakBarricade(World world, BlockPos pos, IBlockState state)
    {
        EnumFacing facing = state.getValue(PropertyHolder.FACING_CARDINAL);
        if (state.getValue(PropertyHolder.WINDOW))
        {
            world.destroyBlock(pos, false);
            world.destroyBlock(pos.down(), false);
            world.setBlockState(pos, Blocks.GLASS_PANE.getDefaultState());
            world.setBlockState(pos.down(), Blocks.GLASS_PANE.getDefaultState());
            if (state.getValue(PropertyHolder.LARGE))
            {
                world.destroyBlock(pos.offset(facing.rotateY()), false);
                world.destroyBlock(pos.down().offset(facing.rotateY()), false);
                world.destroyBlock(pos.offset(facing.rotateYCCW()), false);
                world.destroyBlock(pos.down().offset(facing.rotateYCCW()), false);
                world.setBlockState(pos.offset(facing.rotateY()), Blocks.GLASS_PANE.getDefaultState());
                world.setBlockState(pos.down().offset(facing.rotateY()), Blocks.GLASS_PANE.getDefaultState());
                world.setBlockState(pos.offset(facing.rotateYCCW()), Blocks.GLASS_PANE.getDefaultState());
                world.setBlockState(pos.down().offset(facing.rotateYCCW()), Blocks.GLASS_PANE.getDefaultState());
            }
        }
        else
        {
            world.destroyBlock(pos, false);
            world.destroyBlock(pos.down(), false);
            if (state.getValue(PropertyHolder.LARGE))
            {
                world.destroyBlock(pos.offset(facing.rotateY()), false);
                world.destroyBlock(pos.down().offset(facing.rotateY()), false);
                world.destroyBlock(pos.offset(facing.rotateYCCW()), false);
                world.destroyBlock(pos.down().offset(facing.rotateYCCW()), false);
            }
        }
    }
}