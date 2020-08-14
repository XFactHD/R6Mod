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

import XFactHD.rssmc.api.util.IJammed;
import XFactHD.rssmc.common.blocks.TileEntityGadget;
import XFactHD.rssmc.common.blocks.TileEntityOwnable;
import XFactHD.rssmc.common.data.EnumSide;
import XFactHD.rssmc.common.data.team.StatusController;
import XFactHD.rssmc.common.data.team.Team;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.Arrays;

public class TileEntityJammer extends TileEntityGadget
{
    private AxisAlignedBB searchBox = null;
    private ArrayList<BlockPos> jammedBlocksAndTiles = new ArrayList<>();

    @Override
    public void update()
    {
        if (!world.isRemote)
        {
            //East and South are expanded by 3 because the BlockPos sits in the lower North West corner of the block
            if (searchBox == null) { searchBox = new AxisAlignedBB(pos.north(2).east(3), pos.south(3).west(2)); }

            checkForJammableBlocks();
            checkForJammableEntities();
            checkForJammableItemsOnPlayers();
        }
    }

    private void checkForJammableBlocks()
    {
        //TODO: check if this fucks up performance
        for (BlockPos.MutableBlockPos pos : BlockPos.getAllInBoxMutable(getPos().north(2).east(2).up(4), getPos().south(2).west(2)))
        {
            if (!canJamBlock(pos)) { continue; }

            Block block = world.getBlockState(pos).getBlock();
            if (block instanceof IJammed.Block && !((IJammed.Block)block).isJammed(world, pos))
            {
                ((IJammed.Block)block).setJammed(world, pos, true);
                jammedBlocksAndTiles.add(pos);
            }
            else
            {
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof IJammed.Block && !((IJammed.Block)te).isJammed(world, pos))
                {
                    ((IJammed.Block)te).setJammed(world , pos, true);
                    jammedBlocksAndTiles.add(pos);
                }
            }
        }
    }

    private void checkForJammableEntities()
    {
        for (Entity entity : world.getEntitiesWithinAABB(Entity.class, searchBox))
        {
            if (!canJamEntity(entity)) { continue; }

            if (entity instanceof IJammed.Entity && !((IJammed.Entity)entity).isJammed())
            {
                ((IJammed.Entity)entity).setJammed(true, pos);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void checkForJammableItemsOnPlayers()
    {
        for (EntityPlayer player : world.getEntitiesWithinAABB(EntityPlayer.class, searchBox))
        {
            if (!canJamItemsOnPlayer(player)) { continue; }

            ArrayList<ItemStack> stacks = new ArrayList<>();
            stacks.addAll(Arrays.asList(player.inventory.armorInventory));
            stacks.addAll(Arrays.asList(player.inventory.mainInventory));
            stacks.addAll(Arrays.asList(player.inventory.offHandInventory));

            for (ItemStack stack : stacks)
            {
                if (stack != null && stack.getItem() instanceof IJammed.Item && !((IJammed.Item)stack.getItem()).isJammed(stack))
                {
                    ((IJammed.Item)stack.getItem()).setJammed(player, stack, true, pos);
                }
            }
        }
    }

    public void doCleanup()
    {
        for (BlockPos pos : jammedBlocksAndTiles)
        {
            Block block = world.getBlockState(pos).getBlock();
            if (block instanceof IJammed.Block && ((IJammed.Block)block).isJammed(world, pos))
            {
                ((IJammed.Block)block).setJammed(world, pos, false);
            }
            else
            {
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof IJammed.Block && ((IJammed.Block)te).isJammed(world, pos))
                {
                    ((IJammed.Block)te).setJammed(world, pos, false);
                }
            }
        }
        jammedBlocksAndTiles.clear();
    }

    public AxisAlignedBB getSearchBox()
    {
        if (searchBox == null)
        {
            searchBox = new AxisAlignedBB(pos.north(2).east(3), pos.south(3).west(2));
        }
        return searchBox;
    }

    private boolean canJamBlock(BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityOwnable)
        {
            EntityPlayer otherOwner = ((TileEntityOwnable)te).getOwner();
            return otherOwner != null && StatusController.getPlayersTeam(otherOwner) != StatusController.getPlayersTeam(getOwner());
        }
        return true;
    }

    private boolean canJamEntity(Entity entity)
    {
        //TODO: implement when there is an owner system for entities
        return true;
    }

    private boolean canJamItemsOnPlayer(EntityPlayer player)
    {
        Team team = StatusController.getPlayersTeam(player);
        return team == null || team.getSide() == EnumSide.ATTACKER;
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        super.writeCustomNBT(nbt);
        NBTTagList list = new NBTTagList();
        for (BlockPos pos : jammedBlocksAndTiles)
        {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setLong("pos", pos.toLong());
            list.appendTag(tag);
        }
        nbt.setTag("positions", list);
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        super.readCustomNBT(nbt);
        NBTTagList list = nbt.getTagList("positions", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++)
        {
            jammedBlocksAndTiles.add(BlockPos.fromLong(((NBTTagCompound)list.get(i)).getLong("pos")));
        }
    }
}