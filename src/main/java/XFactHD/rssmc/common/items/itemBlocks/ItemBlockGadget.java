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

import XFactHD.rssmc.api.item.IItemUsageTimer;
import XFactHD.rssmc.common.blocks.BlockBase;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

//FIXME: sometimes doesn't complete a cycle eventhough the mouse was held
//TODO: measure placement times for all gadgets
@SuppressWarnings("ConstantConditions")
public class ItemBlockGadget extends ItemBlockBase implements IItemUsageTimer
{
    private int placeTime;

    public ItemBlockGadget(BlockBase block, int placeTime)
    {
        super(block);
        this.placeTime = placeTime - (placeTime % 4);
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
    {
        super.getSubItems(itemIn, tab, subItems);
        for (ItemStack stack : subItems)
        {
            if (stack.getItem() == this)
            {
                stack.setTagCompound(new NBTTagCompound());
                stack.getTagCompound().setLong("time", -1);
                stack.getTagCompound().setLong("lastClick", 0);
            }
        }
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        //Determine if the block can even be placed at this position on a best effort basis
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (!block.isReplaceable(world, pos)) { pos = pos.offset(facing); }
        if (!player.canPlayerEdit(pos, facing, stack) || !world.canBlockBePlaced(this.block, pos, false, facing, null, stack)) { return EnumActionResult.FAIL; }

        if (world.isRemote) { return EnumActionResult.SUCCESS; }

        if (!stack.hasTagCompound()) { stack.setTagCompound(new NBTTagCompound()); stack.getTagCompound().setLong("time", -1); }

        if (stack.getTagCompound().getLong("time") == -1)
        {
            stack.getTagCompound().setLong("time", world.getTotalWorldTime());
        }
        stack.getTagCompound().setLong("lastClick", world.getTotalWorldTime());
        player.inventory.markDirty();

        if (getCurrentTime(world, stack, player) < getMaxTime(stack))
        {
            return EnumActionResult.SUCCESS;
        }

        stack.getTagCompound().setLong("time", -1);
        stack.getTagCompound().setLong("lastClick", 0);
        player.inventory.markDirty();
        return super.onItemUse(stack, player, world, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected)
    {
        if (!stack.hasTagCompound() || !(entity instanceof EntityPlayer) || world.isRemote) { return; }
        if (stack.getTagCompound().getLong("time") != -1)
        {
            if (world.getTotalWorldTime() - stack.getTagCompound().getLong("lastClick") > 4)
            {
                //LogHelper.info(world.getTotalWorldTime() + " " + stack.getTagCompound().getLong("lastClick") + " " + stack.getTagCompound().getLong("time"));
                stack.getTagCompound().setLong("time", -1);
                stack.getTagCompound().setLong("lastClick", 0);
                ((EntityPlayer)entity).inventory.markDirty();
            }
        }
    }

    @Override
    public String getDescription()
    {
        return "desc.rssmc:place_" + getUnlocalizedName().replace("rssmc:", "").replace("tile.", "") + ".name";
    }

    @Override
    public int getCurrentTime(World world, ItemStack stack, EntityPlayer player)
    {
        if (!stack.hasTagCompound()) { return -1; }
        long time = stack.getTagCompound().getLong("time");
        if (time == -1) { return -1; }
        long current = world.getTotalWorldTime();
        return (current - time) > placeTime ? -1 : (int)(current - time);
    }

    @Override
    public int getMaxTime(ItemStack stack)
    {
        return placeTime;
    }

    @Override
    public boolean isInUse(World world, ItemStack stack, EntityPlayer player)
    {
        return stack.hasTagCompound() && stack.getTagCompound().getLong("time") != -1;
    }
}