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
import XFactHD.rssmc.api.block.IDestructable;
import XFactHD.rssmc.api.block.IExplosive;
import XFactHD.rssmc.common.Content;
import XFactHD.rssmc.common.blocks.BlockGadget;
import XFactHD.rssmc.common.blocks.building.BlockReinforcement;
import XFactHD.rssmc.common.data.EnumGadget;
import XFactHD.rssmc.common.items.itemBlocks.ItemBlockGadget;
import XFactHD.rssmc.common.utils.Damage;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import XFactHD.rssmc.common.utils.propertyEnums.Connection;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings({"deprecation", "ConstantConditions"})
public class BlockThermiteCharge extends BlockGadget implements IExplosive
{
    public BlockThermiteCharge()
    {
        super("blockThermiteCharge", Material.CIRCUITS, RainbowSixSiegeMC.CT.gadgetTab, null, null);
        registerSpecialItemBlock(new ItemBlockGadget(this, 50)); //TODO: consider adding placement animation while timer counts
        registerTileEntity(TileEntityThermiteCharge.class, "ThermiteCharge");
        setDefaultState(super.getDefaultState().withProperty(PropertyHolder.ACTIVATED, false));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PropertyHolder.FACING_NOT_UP, PropertyHolder.ACTIVATED);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        if (meta == 1)
        {
            meta = EnumFacing.DOWN.getIndex();
        }
        return getDefaultState().withProperty(PropertyHolder.FACING_NOT_UP, EnumFacing.getFront(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(PropertyHolder.FACING_NOT_UP).getIndex();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        boolean active = false;
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityThermiteCharge)
        {
            active = ((TileEntityThermiteCharge)te).isActive();
        }
        return state.withProperty(PropertyHolder.ACTIVATED, active);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos)
    {
        switch (state.getValue(PropertyHolder.FACING_NOT_UP))
        {
            case DOWN:  return new AxisAlignedBB(1.9375F/16F, 0,           0, 14.0625F/16F, 1F/16F,            1);
            case NORTH: return new AxisAlignedBB(1.9375F/16F, 0,           0, 14.0625F/16F,      1,       1F/16F);
            case SOUTH: return new AxisAlignedBB(1.9375F/16F, 0,     15F/16F, 14.0625F/16F,      1,            1);
            case WEST:  return new AxisAlignedBB(          0, 0, 1.9375F/16F,       1F/16F,      1, 14.0625F/16F);
            case EAST:  return new AxisAlignedBB(    15F/16F, 0, 1.9375F/16F,            1,      1, 14.0625F/16F);
        }
        return super.getCollisionBoundingBox(state, world, pos);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        if (canPlayerPlaceBlock((EntityPlayer) placer))
        {
            return getDefaultState().withProperty(PropertyHolder.FACING_NOT_UP, side.getOpposite());
        }
        return Blocks.AIR.getDefaultState();
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        ItemStack activator = new ItemStack(Content.itemActivator);
        activator.setTagCompound(new NBTTagCompound());
        activator.getTagCompound().setString("object", getObjectName());
        activator.getTagCompound().setLong("pos", pos.toLong());
        ((EntityPlayer)placer).inventory.addItemStackToInventory(activator);
    }

    @Override
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side)
    {
        return canPlaceBlockAt(world, pos) && canBeAttachedOnSide(world, pos, world.getBlockState(pos.offset(side.getOpposite())), side);
    }

    @Override
    public boolean canBeAttachedOnSide(World world, BlockPos pos, IBlockState stateToBePlacedOn, EnumFacing side)
    {
        stateToBePlacedOn = stateToBePlacedOn.getBlock().getActualState(stateToBePlacedOn, world, pos.offset(side.getOpposite()));
        boolean blockConform = stateToBePlacedOn.getBlock() instanceof BlockReinforcement || stateToBePlacedOn.getBlock() instanceof BlockBlackMirror;
        if (!blockConform)
        {
            return false;
        }
        if (stateToBePlacedOn.getBlock() instanceof BlockBlackMirror)
        {
            blockConform = stateToBePlacedOn.getValue(PropertyHolder.ON_REINFORCEMENT);
        }
        else
        {
            if (stateToBePlacedOn.getValue(PropertyHolder.FACING_NOT_UP) == EnumFacing.DOWN)
            {
                blockConform = true;
            }
            else
            {
                blockConform = stateToBePlacedOn.getValue(PropertyHolder.REINFORCEMENT_CONNECTION) == Connection.UDR ||
                        stateToBePlacedOn.getValue(PropertyHolder.REINFORCEMENT_CONNECTION) == Connection.UDL;
            }
        }
        return side != EnumFacing.DOWN && blockConform;
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
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean isCompleteBlock(IBlockState state)
    {
        return false;
    }

    @Override
    public void activate(World world, BlockPos pos, IBlockState state)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityThermiteCharge)
        {
            ((TileEntityThermiteCharge)te).activate();
        }
    }

    @Override
    public void explode(World world, BlockPos pos, IBlockState state)
    {
        EnumFacing facing = state.getValue(PropertyHolder.FACING_NOT_UP);
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, getRandomSoundPitch(world));
        if (facing == EnumFacing.DOWN)
        {
            IBlockState adjState = world.getBlockState(pos.down());
            if (adjState.getBlock() instanceof BlockReinforcement)
            {
                ((BlockReinforcement)adjState.getBlock()).explode(world, pos.down(), adjState, EnumGadget.THERMITE_CHARGE, EnumFacing.UP);
            }
        }
        else
        {
            world.setBlockToAir(pos);
            IBlockState adjState = world.getBlockState(pos.offset(facing));
            if (adjState.getBlock() instanceof IDestructable)
            {
                ((IDestructable)adjState.getBlock()).explode(world, pos.offset(facing), adjState, EnumGadget.THERMITE_CHARGE, facing.getOpposite());
            }
        }

        AxisAlignedBB aabb;
        switch (state.getValue(PropertyHolder.FACING_NOT_UP))
        {
            case DOWN:
                aabb = new AxisAlignedBB(pos.west().north(), pos.east(2).south(2).up());
                break;
            case NORTH:
                aabb = new AxisAlignedBB(pos.north(2).west().down(), pos.east(2).south().up(2));
                break;
            case SOUTH:
                aabb = new AxisAlignedBB(pos.down().east(2), pos.up(2).west().south(3));
                break;
            case WEST:
                aabb = new AxisAlignedBB(pos.down().west(2).south(2), pos.up(2).east().north());
                break;
            case EAST:
                aabb = new AxisAlignedBB(pos.down().north(), pos.up(2).south(2).east(3));
                break;
            default: aabb = new AxisAlignedBB(0, 0, 0, 0, 0, 0); break;
        }
        EntityPlayer killer = null;
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityBreachCharge) { killer = ((TileEntityBreachCharge)te).getOwner(); }
        List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, aabb);
        for (EntityPlayer player : players)
        {
            player.attackEntityFrom(Damage.causeBreachChargeDamage(killer), 24);
        }

        world.setBlockToAir(pos);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityThermiteCharge();
    }

    @Override
    public boolean canBeShocked(IBlockState state)
    {
        return true;
    }
}