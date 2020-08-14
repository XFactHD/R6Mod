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

package XFactHD.rssmc.common.blocks.objective;

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.common.blocks.BlockBase;
import XFactHD.rssmc.common.data.EnumSide;
import XFactHD.rssmc.common.data.team.StatusController;
import XFactHD.rssmc.common.items.itemBlocks.ItemBlockBase;
import XFactHD.rssmc.common.utils.RSSWorldData;
import XFactHD.rssmc.common.utils.Utils;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

@SuppressWarnings("deprecation")
public class BlockDefuser extends BlockBase //TODO: make the defuser replace carpet blocks when for example placed in kids bedroom on house and place the carpet again if the placement is canceled
{
    public BlockDefuser()
    {
        super("block_defuser", Material.IRON, RainbowSixSiegeMC.CT.miscTab, ItemBlockBase.class, null);
        setDefaultState(super.getDefaultState().withProperty(PropertyHolder.ACTIVATED, false).withProperty(PropertyHolder.DESTROYED, false));
        registerTileEntity(TileEntityDefuser.class, "Defuser");
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PropertyHolder.FACING_CARDINAL, PropertyHolder.ACTIVATED, PropertyHolder.DESTROYED);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        if (meta < 2) { meta = 2; }
        return getDefaultState().withProperty(PropertyHolder.FACING_CARDINAL, EnumFacing.getFront(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(PropertyHolder.FACING_CARDINAL).getIndex();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        boolean active = false;
        boolean destroyed = false;
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityDefuser)
        {
            active = ((TileEntityDefuser)te).isActive();
            destroyed = ((TileEntityDefuser)te).isDestroyed();
        }
        return state.withProperty(PropertyHolder.ACTIVATED, active).withProperty(PropertyHolder.DESTROYED, destroyed);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side)
    {
        return side == EnumFacing.UP && (Utils.isPosInArea(StatusController.getBomb(world, 0).getAABB(), pos) ||
               Utils.isPosInArea(StatusController.getBomb(world, 1).getAABB(), pos));
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn)
    {
        world.scheduleUpdate(pos, this, 2);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        int location = Utils.isPosInArea(StatusController.getBomb(world, 0).getAABB(), pos) ? 0 : 1;
        //RSSWorldData.get(world).getGameManager().setDefuserPosition(pos, location); //TODO: activate when the time has come
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (StatusController.getPlayersSide(player) == EnumSide.ATTACKER)
        {
            TileEntity te = world.getTileEntity(pos);
            if (!world.isRemote && te instanceof TileEntityDefuser)
            {
                ((TileEntityDefuser)te).plant();
            }
            return true;
        }
        return false;
    }

    @Override
    @SuppressWarnings({"ConstantConditions", "StatementWithEmptyBody"})
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
    {
        if (!world.isRemote && world.isAirBlock(pos.down()))
        {
            BlockPos bpos;
            for (bpos = pos.down(); (world.isAirBlock(bpos) || canFallThrough(world.getBlockState(bpos))) && bpos.getY() > 0; bpos = bpos.down()) {}
            int location = 0;
            boolean active = false;
            boolean destroyed = false;
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityDefuser)
            {
                location = ((TileEntityDefuser)te).getObjLocation();
                active = ((TileEntityDefuser)te).isActive();
                destroyed = ((TileEntityDefuser)te).isDestroyed();
            }
            if (Utils.isPosInArea(StatusController.getBomb(world, location).getAABB(), bpos))
            {
                world.setBlockToAir(pos);
                world.setBlockState(bpos, state);
                TileEntity tile = world.getTileEntity(bpos);
                if (tile instanceof TileEntityDefuser)
                {
                    ((TileEntityDefuser)tile).setObjLocation(location);
                    ((TileEntityDefuser)tile).setActive(active);
                    ((TileEntityDefuser)tile).setDestroyed(destroyed);
                }
            }
            else
            {
                if (te instanceof TileEntityDefuser)
                {
                    ((TileEntityDefuser)te).setDestroyed(true);
                }
                world.destroyBlock(pos, false);
            }
        }
    }

    @Override
    public void onBlockClicked(World world, BlockPos pos, EntityPlayer player)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityDefuser && StatusController.getPlayersSide(player) == EnumSide.DEFFENDER)
        {
            ((TileEntityDefuser)te).hit();
        }
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityDefuser();
    }

    @Override
    public boolean isCompleteBlock(IBlockState state)
    {
        return false;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos)
    {
        return new AxisAlignedBB(0, 0, 0, 1, .5, 1);
    }

    @Override
    public boolean isUnbreakableInSurvivalMode(IBlockState state)
    {
        return true;
    }

    private static boolean canFallThrough(IBlockState state)
    {
        Block block = state.getBlock();
        Material material = state.getMaterial();
        return block == Blocks.FIRE || material == Material.AIR || material == Material.WATER || material == Material.LAVA;
    }
}