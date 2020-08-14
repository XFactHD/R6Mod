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

package XFactHD.rssmc.common.blocks;

import XFactHD.rssmc.api.block.IShockable;
import XFactHD.rssmc.api.block.IShootable;
import XFactHD.rssmc.api.block.IUsageTimer;
import XFactHD.rssmc.common.Content;
import XFactHD.rssmc.common.items.itemBlocks.ItemBlockBase;
import XFactHD.rssmc.common.utils.Utils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;

//TODO: implement destructability by explosives
public abstract class BlockGadget extends BlockBase implements IShootable, IShockable, IUsageTimer
{
    private static final ItemStack TEST_STACK = new ItemStack(Content.itemActivator);

    public BlockGadget(String name, Material material, CreativeTabs creativeTab, Class<? extends ItemBlockBase> itemBlockClass, String[] subnames)
    {
        super(name, material, creativeTab, itemBlockClass, subnames);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, ItemStack stack)
    {
        if (canPlayerPlaceBlock((EntityPlayer) placer))
        {
            return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, stack);
        }
        return Blocks.AIR.getDefaultState();
    }

    @SuppressWarnings("ConstantConditions")
    public boolean canPlayerPlaceBlock(EntityPlayer player)
    {
        if (onlyOnePerPlayer())
        {
            ArrayList<Integer> slots = Utils.getSlotsFor(player.inventory.mainInventory, TEST_STACK);
            if (!slots.isEmpty())
            {
                for (int slot : slots)
                {
                    ItemStack test = player.inventory.getStackInSlot(slot);
                    if (test != null && test.hasTagCompound() && test.getTagCompound().getString("object").equals(getObjectName()))
                    {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean onBlockShot(World world, BlockPos pos, IBlockState state, EntityPlayer player, float hitX, float hitY, float hitZ, EnumFacing sideHit)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityGadget)
        {
            ((TileEntityGadget)te).addHit();
            if (((TileEntityGadget)te).getHits() > getMaxHits())
            {
                return onBlockDestroyedByShot(world, pos, state, player, hitX, hitY, hitZ, sideHit);
            }
        }
        return false;
    }

    @Override
    public boolean onBlockDestroyedByShot(World world, BlockPos pos, IBlockState state, EntityPlayer player, float hitX, float hitY, float hitZ, EnumFacing sideHit)
    {
        world.setBlockToAir(pos);
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityOwnable)
        {
            ((TileEntityOwnable)te).setOwner((EntityPlayer)placer);
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityOwnable && canBePickedUp(state) && player.isSneaking())
        {
            if (((TileEntityOwnable)te).getOwner() == player)
            {
                boolean ready = ((TileEntityGadget)te).click(player);
                if (!world.isRemote && ready)
                {
                    if (needsSpecialDestructionHandling())
                    {
                        onBlockDestroyed(world, pos, state, player);
                    }
                    world.destroyBlock(pos, false);
                    player.inventory.addItemStackToInventory(getDrops(world, pos, state, 0).get(0));
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public String getDescription()
    {
        return "desc.rssmc:pick_up_" + getUnlocalizedName().replace("rssmc:", "").replace("tile.", "") + ".name";
    }

    @Override
    public int getCurrentTime(World world, BlockPos pos, EntityPlayer player)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityGadget)
        {
            return ((TileEntityGadget)te).getTime(player);
        }
        return -1;
    }

    @Override
    public int getMaxTime(World world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityGadget)
        {
            return ((TileEntityGadget)te).getTimeToPickUp();
        }
        return 0;
    }

    public abstract boolean needsSpecialDestructionHandling();

    public abstract boolean canBePickedUp(IBlockState state);

    public void onBlockDestroyed(World world, BlockPos pos, IBlockState state, EntityPlayer player){}

    public boolean onlyOnePerPlayer() { return false; }

    public String getObjectName() { return ""; }

    @Override
    public boolean shock(World world, BlockPos pos, IBlockState state, EntityPlayer player, float hitX, float hitY, float hitZ)
    {
        if (canBeShocked(state))
        {
            if (needsSpecialDestructionHandling())
            {
                onBlockDestroyed(world, pos, state, player);
            }
            world.setBlockToAir(pos);
            return true;
        }
        return false;
    }

    public EntityPlayer getOwner(World world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityGadget)
        {
            return ((TileEntityGadget)te).getOwner();
        }
        return null;
    }
}
