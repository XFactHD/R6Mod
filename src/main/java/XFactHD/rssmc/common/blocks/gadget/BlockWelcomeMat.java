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

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.common.blocks.BlockGadget;
import XFactHD.rssmc.common.data.team.StatusController;
import XFactHD.rssmc.common.items.itemBlocks.ItemBlockGadget;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

@SuppressWarnings({"deprecation", "ConstantConditions"})
public class BlockWelcomeMat extends BlockGadget
{
    public BlockWelcomeMat()
    {
        super("blockWelcomeMat", Material.CARPET, RainbowSixSiegeMC.CT.gadgetTab, null, null);
        registerSpecialItemBlock(new ItemBlockGadget(this, 50)); //TODO: consider adding placement animation while timer counts
        setDefaultState(super.getDefaultState().withProperty(PropertyHolder.ACTIVATED, false));
        registerTileEntity(TileEntityWelcomeMat.class, "WelcomeMat");
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PropertyHolder.FACING_CARDINAL, PropertyHolder.ACTIVATED);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        boolean activated = false;
        if (meta > 5)
        {
            meta -= 10;
            activated = true;
        }
        if (meta == 0 || meta == 1)
        {
            meta = EnumFacing.NORTH.getIndex();
        }
        return getDefaultState().withProperty(PropertyHolder.FACING_CARDINAL, EnumFacing.getFront(meta)).withProperty(PropertyHolder.ACTIVATED, activated);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(PropertyHolder.FACING_CARDINAL).getIndex() + (state.getValue(PropertyHolder.ACTIVATED) ? 10 : 0);
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
    {
        if (player.capabilities.isCreativeMode)
        {
            return super.removedByPlayer(state, world, pos, player, willHarvest);
        }
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityWelcomeMat && !world.isRemote)
        {
            if (((TileEntityWelcomeMat)te).getEntityCaught() == null || !state.getValue(PropertyHolder.ACTIVATED))
            {
                return super.removedByPlayer(state, world, pos, player, willHarvest);
            }
        }
        return !state.getValue(PropertyHolder.ACTIVATED) && super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return getDefaultState().withProperty(PropertyHolder.FACING_CARDINAL, placer.getHorizontalFacing());
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityWelcomeMat && !world.isRemote)
        {
            boolean activated = state.getValue(PropertyHolder.ACTIVATED);
            if (!activated) { return super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ); }
            if (isOwnerOrSameTeam(player, (TileEntityWelcomeMat) te) || player.equals(((TileEntityWelcomeMat)te).getEntityCaught()))
            {
                return false;
            }
            ((TileEntityWelcomeMat)te).click();
            return true;
        }
        return false;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityWelcomeMat && !world.isRemote && ((TileEntityWelcomeMat)te).getEntityCaught() == null && !state.getValue(PropertyHolder.ACTIVATED))
        {
            //noinspection ConstantConditions
            if (isOwnerOrSameTeam(entity, (TileEntityWelcomeMat)te))
            {
                return;
            }
            world.setBlockState(pos, state.withProperty(PropertyHolder.ACTIVATED, true));
            ((TileEntityWelcomeMat)te).setEntityCaught(entity);
        }
    }

    @Override
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean onBlockShot(World world, BlockPos pos, IBlockState state, EntityPlayer player, float hitX, float hitY, float hitZ, EnumFacing sideHit)
    {
        if (!state.getValue(PropertyHolder.ACTIVATED))
        {
            return super.onBlockShot(world, pos, state, player, hitX, hitY, hitZ, sideHit);
        }
        return false;
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

    @Override
    public int getMaxHits()
    {
        return 5;
    }

    @Override
    public boolean canBeShocked(IBlockState state)
    {
        return false;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos)
    {
        switch (state.getValue(PropertyHolder.FACING_CARDINAL))
        {
            case NORTH:
            case SOUTH: return new AxisAlignedBB(0, 0, .1875, 1, .0625, .8125);
            case WEST:
            case EAST:  return new AxisAlignedBB(.1875, 0, 0, .8125, .0625, 1);
        }
        return super.getCollisionBoundingBox(state, world, pos);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityWelcomeMat();
    }

    @Override
    public boolean isCompleteBlock(IBlockState state)
    {
        return false;
    }

    private boolean isOwnerOrSameTeam(Entity entity, TileEntityWelcomeMat te)
    {
        EntityPlayer owner = te.getOwner();
        if (entity == null || entity.getUniqueID() == null || owner == null || !(entity instanceof EntityPlayer))
        {
            return false;
        }
        return entity == owner || StatusController.arePlayersTeamMates(owner, (EntityPlayer)entity);
    }
}