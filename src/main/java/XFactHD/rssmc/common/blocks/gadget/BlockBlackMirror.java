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
import XFactHD.rssmc.common.blocks.BlockGadget;
import XFactHD.rssmc.common.blocks.building.BlockWall;
import XFactHD.rssmc.common.data.EnumGadget;
import XFactHD.rssmc.common.items.itemBlocks.ItemBlockBlackMirror;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import XFactHD.rssmc.common.utils.propertyEnums.EnumMaterial;
import XFactHD.rssmc.common.utils.propertyEnums.MirrorState;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("deprecation")
public class BlockBlackMirror extends BlockGadget implements IDestructable
{
    private static final AxisAlignedBB AABB_DESTROYED_NORTH = new AxisAlignedBB(     0, 0, 4F/16F,       1, 1,       1);
    private static final AxisAlignedBB AABB_DESTROYED_EAST  = new AxisAlignedBB(     0, 0,      0, 12F/16F, 1,       1);
    private static final AxisAlignedBB AABB_DESTROYED_SOUTH = new AxisAlignedBB(     0, 0,      0,       1, 1, 12F/16F);
    private static final AxisAlignedBB AABB_DESTROYED_WEST  = new AxisAlignedBB(4F/16F, 0,      0,       1, 1,       1);

    public BlockBlackMirror()
    {
        super("blockBlackMirror", Material.IRON, RainbowSixSiegeMC.CT.gadgetTab, ItemBlockBlackMirror.class, null);
        //TODO: consider adding placement animation while timer counts
        registerTileEntity(TileEntityBlackMirror.class, "BlackMirror");
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PropertyHolder.FACING_CARDINAL, PropertyHolder.RIGHT, PropertyHolder.MIRROR_STATE, PropertyHolder.ON_REINFORCEMENT, PropertyHolder.WALL_MATERIAL);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        boolean right = false;
        if (meta > 5)
        {
            meta -= 5;
            right = true;
        }
        return getDefaultState().withProperty(PropertyHolder.FACING_CARDINAL, EnumFacing.getFront(meta)).withProperty(PropertyHolder.RIGHT, right);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(PropertyHolder.FACING_CARDINAL).getIndex() + (state.getValue(PropertyHolder.RIGHT) ? 5 : 0);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityBlackMirror)
        {
            IBlockState stateUp = world.getBlockState(pos.up());
            stateUp = stateUp.getActualState(world, pos.up());
            IBlockState stateDown = world.getBlockState(pos.down());
            stateDown = stateDown.getActualState(world, pos.down());
            EnumMaterial mat = /*stateUp.getBlock() instanceof BlockReinforcement ? stateUp.getValue(PropertyHolder.WALL_MATERIAL) :*/ //TODO: enable when reinforcement is material aware
                               stateUp.getBlock() instanceof BlockWall ? stateUp.getValue(PropertyHolder.WALL_MATERIAL) :
                               stateDown.getBlock() instanceof BlockWall ? stateDown.getValue(PropertyHolder.WALL_MATERIAL) :
                               EnumMaterial.OAK;
            return state.withProperty(PropertyHolder.MIRROR_STATE, ((TileEntityBlackMirror)te).getDestroyState())
                   .withProperty(PropertyHolder.ON_REINFORCEMENT, ((TileEntityBlackMirror)te).isOnReinforcement())
                   .withProperty(PropertyHolder.WALL_MATERIAL, mat);
        }
        return state;
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos)
    {
        return true;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityBlackMirror && player.isSneaking() && heldItem == null && side == state.getValue(PropertyHolder.FACING_CARDINAL).getOpposite())
        {
            if (((TileEntityBlackMirror)te).getDestroyState() != MirrorState.INTACT) { return false; }
            if (hitY > 0F && hitY < 2F/16F && isCanisterHit(state.getValue(PropertyHolder.FACING_CARDINAL), state.getValue(PropertyHolder.RIGHT), hitX, hitZ))
            {
                if (!world.isRemote)
                {
                    ((TileEntityBlackMirror)te).destroy();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityBlackMirror();
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos)
    {
        return getFullBlockAABBForFacing(state.getValue(PropertyHolder.FACING_CARDINAL)).offset(pos);
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity)
    {
        TileEntity te = world.getTileEntity(pos);
        EnumFacing facing = state.getValue(PropertyHolder.FACING_CARDINAL);
        if (te instanceof TileEntityBlackMirror && ((TileEntityBlackMirror)te).getDestroyState() == MirrorState.OPEN)
        {
            boolean n = facing == EnumFacing.NORTH;
            boolean e = facing == EnumFacing.EAST;
            boolean s = facing == EnumFacing.SOUTH;
            boolean w = facing == EnumFacing.WEST;
            addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(w ? 4F/16F : 0,       0, n ? 4F/16F : 0, e ? 12F/16F : 1, 2F/16F, s ? 12F/16F : 1));
            addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(w ? 4F/16F : 0, 14F/16F, n ? 4F/16F : 0, e ? 12F/16F : 1,      1, s ? 12F/16F : 1));
            addCollisionBoxToList(pos, entityBox, collidingBoxes, getSideAABBForFacing(facing, state.getValue(PropertyHolder.RIGHT)));
        }
        else
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, getFullBlockAABBForFacing(facing));
        }
    }

    @Nullable
    @Override
    public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end)
    {
        EnumFacing facing = state.getValue(PropertyHolder.FACING_CARDINAL);
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileEntityBlackMirror) || ((TileEntityBlackMirror)te).getDestroyState() != MirrorState.OPEN) { return rayTrace(pos, start, end, getFullBlockAABBForFacing(facing)); }
        boolean n = facing == EnumFacing.NORTH;
        boolean e = facing == EnumFacing.EAST;
        boolean s = facing == EnumFacing.SOUTH;
        boolean w = facing == EnumFacing.WEST;
        AxisAlignedBB aabbOne   = new AxisAlignedBB(w ? 4F/16F : 0,       0, n ? 4F/16F : 0, e ? 12F/16F : 1, 2F/16F, s ? 12F/16F : 1);
        AxisAlignedBB aabbTwo   = new AxisAlignedBB(w ? 4F/16F : 0, 14F/16F, n ? 4F/16F : 0, e ? 12F/16F : 1,      1, s ? 12F/16F : 1);
        AxisAlignedBB aabbThree = getSideAABBForFacing(facing, state.getValue(PropertyHolder.RIGHT));
        RayTraceResult result;
        for (AxisAlignedBB aabb : Arrays.asList(aabbOne, aabbTwo, aabbThree))
        {
            result = rayTrace(pos, start, end, aabb != null ? aabb : FULL_BLOCK_AABB);
            if (result != null) { return result; }
        }
        return null;
    }

    @Override
    public boolean isCompleteBlock(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean onBlockDestroyedByShot(World world, BlockPos pos, IBlockState state, EntityPlayer player, float hitX, float hitY, float hitZ, EnumFacing sideHit)
    {
        if (hitY > 0 && hitY <= 2F/16F && isCanisterHit(state.getValue(PropertyHolder.FACING_CARDINAL), state.getValue(PropertyHolder.RIGHT), hitX, hitZ))
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityBlackMirror && ((TileEntityBlackMirror)te).getDestroyState() == MirrorState.INTACT)
            {
                ((TileEntityBlackMirror)te).destroy();
                return true;
            }
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
        return false;
    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer)
    {
        return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.TRANSLUCENT;
    }

    private boolean isCanisterHit(EnumFacing facing, boolean right, float hitX, float hitZ)
    {
        if (facing == EnumFacing.NORTH)
        {
            return right ? hitX >= 0F && hitX <= 3F/16F : hitX >= 13F/16F && hitX <= 1F;
        }
        else if (facing == EnumFacing.SOUTH)
        {
            return right ? hitX >= 13F/16F && hitX <= 1F : hitX >= 0F && hitX <= 3F/16F;
        }
        else if (facing == EnumFacing.EAST)
        {
            return right ? hitZ >= 0F && hitZ <= 3F/16F : hitZ >= 13F/16F && hitZ <= 1;
        }
        else if (facing == EnumFacing.WEST)
        {
            return right ? hitZ >= 13F/16F && hitZ <= 1 : hitZ >= 0F && hitZ <= 3F/16F;
        }
        return false;
    }
    
    private AxisAlignedBB getSideAABBForFacing(EnumFacing facing, boolean right)
    {
        switch (facing)
        {
            case NORTH:
            {
                float minX = right ? 14F/16F : 0;
                float maxX = right ? 1 : 2F/16F;
                return new AxisAlignedBB(minX, 0, 4F/16F, maxX, 1, 1);
            }
            case EAST:
            {
                float minZ = right ? 14F/16F : 0;
                float maxZ = right ? 1 : 2F/16F;
                return new AxisAlignedBB(0, 0, minZ, 12F/16F, 1, maxZ);
            }
            case SOUTH:
            {
                float minX = right ? 0 : 14F/16F;
                float maxX = right ? 2F/16F : 1;
                return new AxisAlignedBB(minX, 0, 0, maxX, 1, 12F/16F);
            }
            case WEST:
            {
                float minZ = right ? 0 : 14F/16F;
                float maxZ = right ? 2F/16F : 1;
                return new AxisAlignedBB(4F/16F, 0, minZ, 1, 1, maxZ);
            }
            default: return FULL_BLOCK_AABB;
        }
    }

    private AxisAlignedBB getFullBlockAABBForFacing(EnumFacing facing)
    {
        switch (facing)
        {
            case NORTH: return AABB_DESTROYED_NORTH;
            case SOUTH: return AABB_DESTROYED_SOUTH;
            case WEST:  return AABB_DESTROYED_WEST;
            case EAST:  return AABB_DESTROYED_EAST;
        }
        return FULL_BLOCK_AABB;
    }

    @Override
    public boolean canAttachGagdet(World world, BlockPos pos, IBlockState state, EnumGadget gadget, EnumFacing side)
    {
        return gadget == EnumGadget.THERMITE_CHARGE && state.getBlock().getActualState(state, world, pos).getValue(PropertyHolder.ON_REINFORCEMENT);
    }

    @Override
    public void explode(World world, BlockPos pos, IBlockState state, EnumGadget gadget, EnumFacing originatingSide)
    {
        EnumFacing facing = state.getValue(PropertyHolder.FACING_CARDINAL);
        for (BlockPos p : BlockPos.getAllInBox(pos.down(), pos.up().offset(state.getValue(PropertyHolder.RIGHT) ? facing.rotateYCCW() : facing.rotateY())))
        {
            world.setBlockToAir(p);
        }
    }

    @Override
    public boolean canBeShocked(IBlockState state)
    {
        return state.getValue(PropertyHolder.MIRROR_STATE) == MirrorState.INTACT;
    }

    @Override
    public boolean shock(World world, BlockPos pos, IBlockState state, EntityPlayer player, float hitX, float hitY, float hitZ)
    {
        if (state.getValue(PropertyHolder.MIRROR_STATE) != MirrorState.INTACT) { return false; }
        if (hitY > 0F && hitY < 2F/16F && isCanisterHit(state.getValue(PropertyHolder.FACING_CARDINAL), state.getValue(PropertyHolder.RIGHT), hitX, hitZ))
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityBlackMirror)
            {
                ((TileEntityBlackMirror) te).destroy();
                return true;
            }
            return false;
        }
        return false;
    }
}