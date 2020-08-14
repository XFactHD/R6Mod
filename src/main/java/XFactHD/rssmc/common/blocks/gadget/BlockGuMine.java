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
import XFactHD.rssmc.common.items.itemBlocks.ItemBlockCloakedMine;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class BlockGuMine extends BlockGadget
{
    private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(5.5F/16F, 0, 5.5F/16F, 10.5F/16F, 3.5F/16F, 10.5F/16F);

    public BlockGuMine()
    {
        super("block_cloaked_mine", Material.IRON, RainbowSixSiegeMC.CT.gadgetTab, ItemBlockCloakedMine.class, null);
        registerTileEntity(TileEntityCloakedMine.class, "CloakedMine");
    }

    @Override
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side)
    {
        return side == EnumFacing.UP && world.getBlockState(pos.down()).isSideSolid(world, pos.down(), side);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos)
    {
        return BOUNDING_BOX;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity)
    {
        if (BOUNDING_BOX.addCoord(0, pos.up().getY(), 0).offset(pos).intersectsWith(entity.getEntityBoundingBox()))
        {
            if (entity instanceof EntityPlayer)
            {
                EntityPlayer player = (EntityPlayer)entity;
                if (!StatusController.arePlayersTeamMates(player, getOwner(world, pos)))
                {
                    //TODO: check if the enemy is already hit and fire if not!
                }
            }
            else if (entity instanceof EntityLivingBase)
            {
                //TODO: check if the entity is already hit and fire if not!
            }
        }
    }

    @Override
    public boolean isCompleteBlock(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean canBePickedUp(IBlockState state)
    {
        return true;
    }

    @Override
    public boolean needsSpecialDestructionHandling()
    {
        return false;
    }

    @Override
    public boolean canBeShocked(IBlockState state)
    {
        return true;
    }
}