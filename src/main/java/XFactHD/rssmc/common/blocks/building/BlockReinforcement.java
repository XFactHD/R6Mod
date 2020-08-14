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

package XFactHD.rssmc.common.blocks.building;

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.api.block.HitType;
import XFactHD.rssmc.api.block.IDestructable;
import XFactHD.rssmc.common.Content;
import XFactHD.rssmc.common.blocks.BlockBase;
import XFactHD.rssmc.common.data.EnumGadget;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import XFactHD.rssmc.common.utils.propertyEnums.Connection;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation") //TODO: Rework to make this simpler as this can only be placed on BlockWall anyway and make it material aware
public class BlockReinforcement extends BlockBase implements IDestructable
{
    private static final AxisAlignedBB AABB_DESTROYED_NORTH = new AxisAlignedBB(     0, 0, 4F/16F,       1, 1,       1);
    private static final AxisAlignedBB AABB_DESTROYED_EAST  = new AxisAlignedBB(     0, 0,      0, 12F/16F, 1,       1);
    private static final AxisAlignedBB AABB_DESTROYED_SOUTH = new AxisAlignedBB(     0, 0,      0,       1, 1, 12F/16F);
    private static final AxisAlignedBB AABB_DESTROYED_WEST  = new AxisAlignedBB(4F/16F, 0,      0,       1, 1,       1);

    public BlockReinforcement()
    {
        super("blockReinforcement", Material.IRON, RainbowSixSiegeMC.CT.buildingTab, null, null);
        registerTileEntity(TileEntityReinforcement.class, "Reinforcement");
        setDefaultState(super.getDefaultState().withProperty(PropertyHolder.ELECTRIFIED, false));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new ExtendedBlockState(this,
                new IProperty[]{PropertyHolder.FACING_NOT_UP, PropertyHolder.REINFORCEMENT_CONNECTION, PropertyHolder.ELECTRIFIED},
                new IUnlistedProperty[]{PropertyHolder.IBLOCKSTATE});
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileEntityReinforcement))
        {
            return state;
        }
        IBlockState stateToReinforce = ((TileEntityReinforcement)te).getStateToReinforce();
        return ((IExtendedBlockState)state).withProperty(PropertyHolder.IBLOCKSTATE, stateToReinforce);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityReinforcement)
        {
            state = state.withProperty(PropertyHolder.ELECTRIFIED, ((TileEntityReinforcement)te).isElectrified());
            return state.withProperty(PropertyHolder.REINFORCEMENT_CONNECTION, ((TileEntityReinforcement)te).getCon());
        }
        return state;
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(PropertyHolder.FACING_NOT_UP, EnumFacing.getFront(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(PropertyHolder.FACING_NOT_UP).getIndex();
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileEntityReinforcement))
        {
            return super.getPickBlock(state, target, world, pos, player);
        }
        IBlockState stateToReinforce = ((TileEntityReinforcement)te).getStateToReinforce();
        EnumFacing side = target.sideHit;
        EnumFacing facing = state.getValue(PropertyHolder.FACING_NOT_UP);
        return side == facing.getOpposite() ? new ItemStack(Content.itemReinforcement) : stateToReinforce.getBlock().getPickBlock(stateToReinforce, target, world, pos, player);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos)
    {
        if (state.getValue(PropertyHolder.FACING_NOT_UP) == EnumFacing.DOWN)
        {
            return new AxisAlignedBB(0, .5F, 0, 1, 1, 1);
        }
        else
        {
            switch (state.getValue(PropertyHolder.FACING_NOT_UP))
            {
                case NORTH: return AABB_DESTROYED_NORTH;
                case SOUTH: return AABB_DESTROYED_SOUTH;
                case WEST:  return AABB_DESTROYED_WEST;
                case EAST:  return AABB_DESTROYED_EAST;
            }
        }
        return super.getCollisionBoundingBox(state, world, pos);
    }

    @Override
    public boolean isCompleteBlock(IBlockState state)
    {
        return false;
    }

    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand)
    {
        state = state.getBlock().getActualState(state, world, pos);
        if (!state.getValue(PropertyHolder.ELECTRIFIED)) { return; }
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        switch (state.getValue(PropertyHolder.FACING_NOT_UP))
        {
            case DOWN:
                x += rand.nextDouble();
                y += 1;
                z += rand.nextDouble();
                break;
            case NORTH:
                x += rand.nextDouble();
                y += rand.nextDouble();
                break;
            case SOUTH:
                x += rand.nextDouble();
                y += rand.nextDouble();
                z += 1;
                break;
            case WEST:
                y += rand.nextDouble();
                z += rand.nextDouble();
                break;
            case EAST:
                x += 1;
                y += rand.nextDouble();
                z += rand.nextDouble();
                break;
        }
        world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0, .1, 0);
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileEntityReinforcement))
        {
            return super.getDrops(world, pos, state, fortune);
        }
        IBlockState stateToReinforce = ((TileEntityReinforcement)te).getStateToReinforce();
        ArrayList<ItemStack> stacks = new ArrayList<>();
        stacks.add(new ItemStack(Content.itemReinforcement));
        stacks.addAll(stateToReinforce.getBlock().getDrops(world, pos, stateToReinforce, 0));
        return stacks;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityReinforcement();
    }

    @Override
    public boolean canAttachGagdet(World world, BlockPos pos, IBlockState state, EnumGadget gadget, EnumFacing side)
    {
        return gadget == EnumGadget.THERMITE_CHARGE;
    }

    @Override
    public void explode(World world, BlockPos pos, IBlockState state, EnumGadget gadget, EnumFacing originatingSide)
    {
        if (state.getValue(PropertyHolder.FACING_NOT_UP) == EnumFacing.DOWN)
        {
            switch (state.getBlock().getActualState(state, world, pos).getValue(PropertyHolder.REINFORCEMENT_CONNECTION))
            {
                case UR:
                {
                    BlockPos pos1 = pos.north();
                    BlockPos pos2 = pos.east();
                    BlockPos pos3 = pos.north().east();
                    world.setBlockToAir(pos);
                    world.setBlockToAir(pos1);
                    world.setBlockToAir(pos2);
                    world.setBlockToAir(pos3);
                    break;
                }
                case UL:
                {
                    BlockPos pos1 = pos.north();
                    BlockPos pos2 = pos.west();
                    BlockPos pos3 = pos.north().west();
                    world.setBlockToAir(pos);
                    world.setBlockToAir(pos1);
                    world.setBlockToAir(pos2);
                    world.setBlockToAir(pos3);
                    break;
                }
                case DR:
                {
                    BlockPos pos1 = pos.south();
                    BlockPos pos2 = pos.east();
                    BlockPos pos3 = pos.south().east();
                    world.setBlockToAir(pos);
                    world.setBlockToAir(pos1);
                    world.setBlockToAir(pos2);
                    world.setBlockToAir(pos3);
                    break;
                }
                case DL:
                {
                    BlockPos pos1 = pos.south();
                    BlockPos pos2 = pos.west();
                    BlockPos pos3 = pos.south().west();
                    world.setBlockToAir(pos);
                    world.setBlockToAir(pos1);
                    world.setBlockToAir(pos2);
                    world.setBlockToAir(pos3);
                    break;
                }
            }
        }
        else
        {
            EnumFacing facing = state.getValue(PropertyHolder.FACING_NOT_UP);
            Connection con = state.getBlock().getActualState(state, world, pos).getValue(PropertyHolder.REINFORCEMENT_CONNECTION);
            for (BlockPos p : BlockPos.getAllInBox(pos.down(), pos.up().offset(con == Connection.UDR ? facing.rotateY() : facing.rotateYCCW())))
            {
                world.setBlockToAir(p);
            }
        }
    }

    @Override
    public boolean destruct(World world, BlockPos pos, IBlockState state, HitType type, EnumFacing originatingSide)
    {
        if (type == HitType.XKAIROS)
        {
            if (state.getValue(PropertyHolder.FACING_NOT_UP) == EnumFacing.DOWN)
            {
                switch (state.getBlock().getActualState(state, world, pos).getValue(PropertyHolder.REINFORCEMENT_CONNECTION))
                {
                    case UR:
                    {
                        BlockPos pos1 = pos.north();
                        BlockPos pos2 = pos.east();
                        BlockPos pos3 = pos.north().east();
                        world.setBlockToAir(pos);
                        world.setBlockToAir(pos1);
                        world.setBlockToAir(pos2);
                        world.setBlockToAir(pos3);
                        break;
                    }
                    case UL:
                    {
                        BlockPos pos1 = pos.north();
                        BlockPos pos2 = pos.west();
                        BlockPos pos3 = pos.north().west();
                        world.setBlockToAir(pos);
                        world.setBlockToAir(pos1);
                        world.setBlockToAir(pos2);
                        world.setBlockToAir(pos3);
                        break;
                    }
                    case DR:
                    {
                        BlockPos pos1 = pos.south();
                        BlockPos pos2 = pos.east();
                        BlockPos pos3 = pos.south().east();
                        world.setBlockToAir(pos);
                        world.setBlockToAir(pos1);
                        world.setBlockToAir(pos2);
                        world.setBlockToAir(pos3);
                        break;
                    }
                    case DL:
                    {
                        BlockPos pos1 = pos.south();
                        BlockPos pos2 = pos.west();
                        BlockPos pos3 = pos.south().west();
                        world.setBlockToAir(pos);
                        world.setBlockToAir(pos1);
                        world.setBlockToAir(pos2);
                        world.setBlockToAir(pos3);
                        break;
                    }
                }
            }
            else
            {
                world.setBlockToAir(pos);
            }
            return true;
        }
        return false;
    }
}