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

package XFactHD.rssmc.common.entity.gadget;

import XFactHD.rssmc.api.block.HitType;
import XFactHD.rssmc.api.block.IDestructable;
import XFactHD.rssmc.common.utils.helper.ExplosionHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityImpactGrenade extends AbstractEntityGrenade
{
    public EntityImpactGrenade(World world)
    {
        super(world);
        setSize(.2F, .2F);
    }

    public EntityImpactGrenade(World world, EntityPlayer thrower)
    {
        super(world, thrower);
        setSize(.2F, .2F);
    }

    @Override
    protected void onImpact(RayTraceResult result)
    {
        if (world.isRemote) { return; }
        boom();
        if (result.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            EnumFacing up = isAxisY(result.sideHit) ? EnumFacing.NORTH : EnumFacing.UP;
            EnumFacing down = isAxisY(result.sideHit) ? EnumFacing.SOUTH : EnumFacing.DOWN;
            EnumFacing right = isAxisY(result.sideHit) ? EnumFacing.EAST : result.sideHit.rotateYCCW();
            EnumFacing left = isAxisY(result.sideHit) ? EnumFacing.WEST : result.sideHit.rotateY();
            for (BlockPos pos : BlockPos.getAllInBox(result.getBlockPos().offset(up).offset(right), result.getBlockPos().offset(down).offset(left)))
            {
                IBlockState state = world.getBlockState(pos);
                if (state.getBlock() instanceof IDestructable)
                {
                    ((IDestructable) state.getBlock()).destruct(world, pos, state, HitType.IMPACT_GRENADE, result.sideHit);
                }
            }
        }
    }

    private boolean isAxisY(EnumFacing facing)
    {
        return facing == EnumFacing.UP || facing == EnumFacing.DOWN;
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        if (ticksExisted >= 20 && !world.isRemote)
        {
            boom();
        }
    }

    public void boom()
    {
        ExplosionHelper.causeExplosionAtEntity(world, this, 2, 10F);
        setDead();
    }
}