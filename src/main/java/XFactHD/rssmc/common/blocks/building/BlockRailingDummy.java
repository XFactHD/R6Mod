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

import XFactHD.rssmc.common.blocks.BlockBase;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("deprecation")
public class BlockRailingDummy extends BlockBase
{
    public BlockRailingDummy()
    {
        super("block_railing_dummy", Material.WOOD, null, null, null);
        setDefaultState(super.getDefaultState().withProperty(PropertyHolder.CORNER, false));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PropertyHolder.FACING_CARDINAL, PropertyHolder.CORNER);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        boolean corner = false;
        if (meta >= 7)
        {
            corner = true;
            meta -= 5;
        }
        return getDefaultState().withProperty(PropertyHolder.CORNER, corner).withProperty(PropertyHolder.FACING_CARDINAL, EnumFacing.getFront(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        int facing = state.getValue(PropertyHolder.FACING_CARDINAL).getIndex();
        if (state.getValue(PropertyHolder.CORNER)) { facing += 5; }
        return facing;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity)
    {
        if (state.getValue(PropertyHolder.CORNER))
        {
            switch (state.getValue(PropertyHolder.FACING_CARDINAL))
            {
                case NORTH:
                {
                    addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0, 0, 1F / 16F, 1, 7.6F/16F, 3F / 16F));
                    addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(1F / 16F, 0, 0, 3F / 16F, 7.6F/16F, 1));
                    break;
                }
                case EAST:
                {
                    addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(13F / 16F, 0, 0, 15F / 16F, 7.6F/16F, 1));
                    addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0, 0, 1F / 16F, 1, 7.6F/16F, 3F / 16F));
                    break;
                }
                case SOUTH:
                {
                    addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0, 0, 13F / 16F, 1, 7.6F/16F, 15F / 16F));
                    addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(13F / 16F, 0, 0, 15F / 16F, 7.6F/16F, 1));
                    break;
                }
                case WEST:
                {
                    addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(1F / 16F, 0, 0, 3F / 16F, 7.6F/16F, 1));
                    addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0, 0, 13F / 16F, 1, 7.6F/16F, 15F / 16F));
                    break;
                }
            }
        }
        else
        {
            switch (state.getValue(PropertyHolder.FACING_CARDINAL))
            {
                case NORTH:  addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0, 0, 1F/16F, 1, 7.6F/16F, 3F/16F)); break;
                case EAST:   addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(13F/16F, 0, 0, 15F/16F, 7.6F/16F, 1)); break;
                case SOUTH:  addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0, 0, 13F/16F, 1, 7.6F/16F, 15F/16F)); break;
                case WEST:   addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(1F/16F, 0, 0, 3F/16F, 7.6F/16F, 1)); break;
            }
        }
    }

    @Nullable
    @Override
    public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end)
    {
        if (state.getValue(PropertyHolder.CORNER))
        {
            AxisAlignedBB aabb1 = FULL_BLOCK_AABB;
            AxisAlignedBB aabb2 = FULL_BLOCK_AABB;
            switch (state.getValue(PropertyHolder.FACING_CARDINAL))
            {
                case NORTH:
                {
                    aabb1 = new AxisAlignedBB(0, 0, 1F / 16F, 1, 7.6F/16F, 3F / 16F);
                    aabb2 = new AxisAlignedBB(1F / 16F, 0, 0, 3F / 16F, 7.6F/16F, 1);
                    break;
                }
                case EAST:
                {
                    aabb1 = new AxisAlignedBB(13F / 16F, 0, 0, 15F / 16F, 7.6F/16F, 1);
                    aabb2 = new AxisAlignedBB(0, 0, 1F / 16F, 1, 7.6F/16F, 3F / 16F);
                    break;
                }
                case SOUTH:
                {
                    aabb1 = new AxisAlignedBB(0, 0, 13F / 16F, 1, 7.6F/16F, 15F / 16F);
                    aabb2 = new AxisAlignedBB(13F / 16F, 0, 0, 15F / 16F, 7.6F/16F, 1);
                    break;
                }
                case WEST:
                {
                    aabb1 = new AxisAlignedBB(1F / 16F, 0, 0, 3F / 16F, 7.6F/16F, 1);
                    aabb2 = new AxisAlignedBB(0, 0, 13F / 16F, 1, 7.6F/16F, 15F / 16F);
                    break;
                }
            }
            RayTraceResult result = rayTrace(pos, start, end, aabb1);
            if (result != null) { return result; }
            return rayTrace(pos, start, end, aabb2);
        }
        else
        {
            switch (state.getValue(PropertyHolder.FACING_CARDINAL))
            {
                case NORTH:  return rayTrace(pos, start, end, new AxisAlignedBB(0, 0, 1F/16F, 1, 7.6F/16F, 3F/16F));
                case EAST:   return rayTrace(pos, start, end, new AxisAlignedBB(13F/16F, 0, 0, 15F/16F, 7.6F/16F, 1));
                case SOUTH:  return rayTrace(pos, start, end, new AxisAlignedBB(0, 0, 13F/16F, 1, 7.6F/16F, 15F/16F));
                case WEST:   return rayTrace(pos, start, end, new AxisAlignedBB(1F/16F, 0, 0, 3F/16F, 7.6F/16F, 1));
                default:     return rayTrace(pos, start, end, FULL_BLOCK_AABB);
            }
        }
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos)
    {
        if (state.getValue(PropertyHolder.CORNER))
        {
            return FULL_BLOCK_AABB.offset(pos);
        }
        else
        {
            switch (state.getValue(PropertyHolder.FACING_CARDINAL))
            {
                case NORTH:  return new AxisAlignedBB(0, 0, 1F/16F, 1, 7.6F/16F, 3F/16F).offset(pos);
                case EAST:   return new AxisAlignedBB(13F/16F, 0, 0, 15F/16F, 7.6F/16F, 1).offset(pos);
                case SOUTH:  return new AxisAlignedBB(0, 0, 13F/16F, 1, 7.6F/16F, 15F/16F).offset(pos);
                case WEST:   return new AxisAlignedBB(1F/16F, 0, 0, 3F/16F, 7.6F/16F, 1).offset(pos);
                default:     return FULL_BLOCK_AABB.offset(pos);
            }
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        super.breakBlock(world, pos, state);
        world.setBlockToAir(pos.down());
    }

    @Override
    public boolean isCompleteBlock(IBlockState state)
    {
        return false;
    }
}