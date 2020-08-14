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

import XFactHD.rssmc.common.blocks.TileEntityGadget;
import XFactHD.rssmc.common.data.team.StatusController;
import XFactHD.rssmc.common.utils.Damage;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class TileEntityKapkanTrap extends TileEntityGadget //TODO: make the kapkan trap placeable on both sides of the door
{
    private static final Vec3d[] laserVecs = new Vec3d[] {new Vec3d(0, 0, -1), new Vec3d(0, 0, 1), new Vec3d(-1, 0, 0), new Vec3d(1, 0, 0)};
    private static final AxisAlignedBB ZERO_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
    private boolean firstTick = true;
    private Vec3d laser = null;
    private Vec3d laserStart = null;
    private Vec3d laserEnd = null;
    private EnumFacing facing = EnumFacing.NORTH;
    private AxisAlignedBB searchAABB = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

    @Override
    public void update()
    {
        if (firstTick)
        {
            initLaser();
            firstTick = false;
        }
        if (!world.isRemote)
        {
            checkEntityInLaser();
        }
    }

    private void checkEntityInLaser()
    {
        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, searchAABB);
        List<EntityLivingBase> victims = new ArrayList<>();
        for (EntityLivingBase entity : entities)
        {
            if (entity instanceof EntityPlayer && (entity == getOwner() || StatusController.arePlayersTeamMates(getOwner(), (EntityPlayer)entity)))
            {
                continue;
            }
            RayTraceResult result = entity.getEntityBoundingBox().calculateIntercept(laserStart, laserEnd);
            if (entity.getEntityBoundingBox() != ZERO_AABB && result != null)
            {
                victims.add(entity);
            }
        }
        boom(victims);
    }

    private void boom(List<EntityLivingBase> victims)
    {
        if (victims.isEmpty()) { return; }
        for (EntityLivingBase victim : victims)
        {
            double dist = pos.distanceSq(victim.posX, victim.posY, victim.posZ);
            if (!victim.isImmuneToExplosions())
            {
                victim.attackEntityFrom(Damage.causeKapkanTrapDamage(getOwner()), 30F - (float)dist * .2F);
            }
        }
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
        world.setBlockToAir(pos);
    }

    public void setFacing(EnumFacing facing)
    {
        this.facing = facing;
        firstTick = true;
        if (!world.isRemote) { notifyBlockUpdate(); }
    }

    public EnumFacing getFacing()
    {
        return facing;
    }

    public Vec3d getLaser()
    {
        return laser;
    }

    //FIXME: two orientations do not blow up
    private void initLaser()
    {
        searchAABB = new AxisAlignedBB(pos, pos.offset(facing, 2).offset(facing.rotateY()).up());
        switch (facing)
        {
            case NORTH:
            {
                laserStart = new Vec3d(pos.getX() + .185, pos.getY() + .51, pos.getZ() + .2);
                laser = new Vec3d(0, 0, -1.2);
                break;
            }
            case SOUTH:
            {
                laserStart = new Vec3d(pos.getX() + .815, pos.getY() + .51, pos.getZ() + .8);
                laser = new Vec3d(0, 0, 1.2);
                break;
            }
            case WEST:
            {
                laserStart = new Vec3d(pos.getX() + .2, pos.getY() + .51, pos.getZ() + .815);
                laser = new Vec3d(-1.2, 0, 0);
                break;
            }
            case EAST:
            {
                laserStart = new Vec3d(pos.getX() + .8, pos.getY() + .51, pos.getZ() + .815);
                laser = new Vec3d(1.2, 0, 0);
                break;
            }
        }
        laserEnd = laserStart.add(laser);
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        super.writeCustomNBT(nbt);
        nbt.setInteger("facing", facing.getIndex());
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        super.readCustomNBT(nbt);
        setFacing(EnumFacing.getFront(nbt.getInteger("facing")));
    }
}