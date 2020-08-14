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
import XFactHD.rssmc.api.block.HitType;
import XFactHD.rssmc.api.block.IDestructable;
import XFactHD.rssmc.common.blocks.BlockGadget;
import XFactHD.rssmc.common.data.EnumGadget;
import XFactHD.rssmc.common.items.itemBlocks.ItemBlockGadget;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class BlockDeployableShield extends BlockGadget implements IDestructable
{
    public BlockDeployableShield()
    {
        super("blockDeployableShield", Material.IRON, RainbowSixSiegeMC.CT.gadgetTab, null, null);
        registerSpecialItemBlock(new ItemBlockGadget(this, 50)); //TODO: consider adding placement animation while timer counts
        registerTileEntity(TileEntityDeployableShield.class, "DeployableShield");
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PropertyHolder.FACING_CARDINAL);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(PropertyHolder.FACING_CARDINAL, meta < 2 ? EnumFacing.NORTH : EnumFacing.getFront(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(PropertyHolder.FACING_CARDINAL).getIndex();
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, ItemStack stack)
    {
        return getDefaultState().withProperty(PropertyHolder.FACING_CARDINAL, placer.getHorizontalFacing());
    }

    @Override
    public boolean needsSpecialDestructionHandling()
    {
        return false;
    }

    @Override
    public boolean canBePickedUp(IBlockState state)
    {
        return true;
    }

    @Override
    public boolean onBlockShot(World world, BlockPos pos, IBlockState state, EntityPlayer player, float hitX, float hitY, float hitZ, EnumFacing sideHit)
    {
        world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_METAL_HIT, SoundCategory.BLOCKS, 1, 1, false);
        return false;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos)
    {
        switch (state.getValue(PropertyHolder.FACING_CARDINAL))
        {
            case NORTH: return new AxisAlignedBB(        0, 0,    1F/16F,        1, 1, 5.3F/16F);
            case EAST:  return new AxisAlignedBB(10.7F/16F, 0,         0,  15F/16F, 1,        1);
            case SOUTH: return new AxisAlignedBB(        0, 0, 10.7F/16F,        1, 1,  15F/16F);
            case WEST:  return new AxisAlignedBB(   1F/16F, 0,         0, 5.3F/16F, 1,        1);
            default: return null;
        }
    }

    @Override
    public boolean isCompleteBlock(IBlockState state)
    {
        return false;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityDeployableShield();
    }

    @Override
    public boolean canAttachGagdet(World world, BlockPos pos, IBlockState state, EnumGadget gadget, EnumFacing side)
    {
        return false;
    }

    @Override
    public boolean destruct(World world, BlockPos pos, IBlockState state, HitType type, EnumFacing originatingSide)
    {
        if (type == HitType.IMPACT_GRENADE || type == HitType.C4 || type == HitType.BREACH_GRENADE || type == HitType.CLUSTER_CHARGE || type == HitType.FRAG_GRENADE)
        {
            world.destroyBlock(pos, false);
            return true;
        }
        return false;
    }

    @Override
    public boolean canBeShocked(IBlockState state)
    {
        return false;
    }
}