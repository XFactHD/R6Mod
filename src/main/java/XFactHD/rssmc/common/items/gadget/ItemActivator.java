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

package XFactHD.rssmc.common.items.gadget;

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.api.block.IExplosive;
import XFactHD.rssmc.api.util.IJammed;
import XFactHD.rssmc.common.blocks.gadget.TileEntityJammer;
import XFactHD.rssmc.common.data.EnumGadget;
import XFactHD.rssmc.common.items.ItemBase;
import XFactHD.rssmc.common.items.armor.ItemBomberArmor;
import XFactHD.rssmc.client.util.Sounds;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@SuppressWarnings("ConstantConditions")
public class ItemActivator extends ItemBase implements IJammed.Item
{
    public ItemActivator()
    {
        super("itemActivator", 1, RainbowSixSiegeMC.CT.gadgetTab, null);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
    {
        if (!world.isRemote)
        {
            world.playSound(null, player.getPosition(), Sounds.getGadgetSound(EnumGadget.CHARGE_ACTIVATOR, "activate"), SoundCategory.PLAYERS, 1, 1);
            if (!stack.getTagCompound().hasKey("pos"))
            {
                ItemStack armor = player.inventory.armorInventory[2];
                if (armor != null && armor.getItem() instanceof ItemBomberArmor)
                {
                    ((ItemBomberArmor)armor.getItem()).explode(world, player, armor);
                }
            }
            else
            {
                BlockPos pos = BlockPos.fromLong(stack.getTagCompound().getLong("pos"));
                IBlockState state = world.getBlockState(pos);
                if (state.getBlock() instanceof IExplosive && !stack.getTagCompound().getBoolean("jammed"))
                {
                    ((IExplosive)state.getBlock()).activate(world, pos, world.getBlockState(pos));
                }
            }
            player.inventory.decrStackSize(player.inventory.currentItem, 1);
        }
        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof IExplosive && player.isCreative() && player.isSneaking() && (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("pos")))
        {
            if (!world.isRemote)
            {
                stack.setTagCompound(new NBTTagCompound());
                stack.getTagCompound().setLong("pos", pos.toLong());
            }
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.FAIL;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected)
    {
        if (!world.isRemote && entity instanceof EntityPlayer)
        {
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("jammerPos") && stack.getTagCompound().getBoolean("jammed"))
            {
                BlockPos pos = BlockPos.fromLong(stack.getTagCompound().getLong("jammerPos"));
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof TileEntityJammer)
                {
                    AxisAlignedBB searchBox = ((TileEntityJammer)te).getSearchBox();
                    if (!searchBox.intersectsWith(entity.getEntityBoundingBox()))
                    {
                        stack.getTagCompound().setBoolean("jammed", false);
                        ((EntityPlayer)entity).inventory.markDirty();
                    }
                }
                else
                {
                    stack.getTagCompound().setBoolean("jammed", false);
                    ((EntityPlayer)entity).inventory.markDirty();
                }
            }
        }
    }

    @Override
    public void setJammed(EntityPlayer player, ItemStack stack, boolean jammed, BlockPos jammer)
    {
        if (!stack.hasTagCompound()) { stack.setTagCompound(new NBTTagCompound()); }
        stack.getTagCompound().setBoolean("jammed", jammed);
        if (jammer != null) { stack.getTagCompound().setLong("jammerPos", jammer.toLong()); }
        player.inventory.markDirty();
    }

    @Override
    public boolean isJammed(ItemStack stack)
    {
        return stack.hasTagCompound() && stack.getTagCompound().getBoolean("jammed");
    }
}