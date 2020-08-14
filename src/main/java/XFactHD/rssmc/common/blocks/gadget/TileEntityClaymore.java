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
import XFactHD.rssmc.common.data.EnumGadget;
import XFactHD.rssmc.common.data.team.StatusController;
import XFactHD.rssmc.common.utils.Damage;
import XFactHD.rssmc.client.util.Sounds;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.List;

public class TileEntityClaymore extends TileEntityGadget
{
    private static Vec3i[][] laserVecs = new Vec3i[4][3];
    private static final AxisAlignedBB ZERO_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
    private int ticksUntilActivation = 20;
    private boolean active = false;
    private EnumFacing facing = EnumFacing.NORTH;
    private Vec3i[] lasers = null;

    static
    {
        laserVecs[0] = new Vec3i[]
                {
                        new Vec3i(-1, 0, 3),
                        new Vec3i(0, 0, 3),
                        new Vec3i(1, 0, 3)
                };
        laserVecs[1] = new Vec3i[]
                {
                        new Vec3i(-2, 0, -1),
                        new Vec3i(-2, 0, 0),
                        new Vec3i(-2, 0, 1)
                };
        laserVecs[2] = new Vec3i[]
                {
                        new Vec3i( 1, 0, -2),
                        new Vec3i( 0, 0, -2),
                        new Vec3i(-1, 0, -2)
                };
        laserVecs[3] = new Vec3i[]
                {
                        new Vec3i(3, 0, 1),
                        new Vec3i(3, 0, 0),
                        new Vec3i(3, 0, -1)
                };


    }

    @Override
    public void update()
    {
        if (lasers != laserVecs[facing.getHorizontalIndex()])
        {
            lasers = laserVecs[facing.getHorizontalIndex()];
            notifyBlockUpdate();
        }
        if (!world.isRemote)
        {
            if (ticksUntilActivation > 0)
            {
                ticksUntilActivation -= 1;
                if (ticksUntilActivation == 0)
                {
                    active = true;
                    world.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK /*Sounds.getGadgetSound(EnumGadget.CLAYMORE, "activate")*/, SoundCategory.BLOCKS, 1, 1);
                    notifyBlockUpdate();
                }
            }
            if (active)
            {
                checkEntityCrossingLaser();
            }
        }
    }

    public void setFacing(EnumFacing facing)
    {
        this.facing = facing;
    }

    private void checkEntityCrossingLaser()
    {
        AxisAlignedBB searchAABB = new AxisAlignedBB(0, 0, 0, 0, 0, 0);
        Vec3i[] lasers = laserVecs[facing.getIndex()-2];
        switch (facing)
        {
            case NORTH: searchAABB = new AxisAlignedBB(pos.west(),  pos.east().north(3).up()); break;
            case EAST:  searchAABB = new AxisAlignedBB(pos.north(), pos.south().east(3).up()); break;
            case SOUTH: searchAABB = new AxisAlignedBB(pos.east(),  pos.west().south(3).up()); break;
            case WEST:  searchAABB = new AxisAlignedBB(pos.south(), pos.north().west(3).up()); break;
        }
        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, searchAABB);
        if (entities.isEmpty())
        {
            return;
        }
        List<EntityLivingBase> victims = new ArrayList<>();
        for (EntityLivingBase entity : entities)
        {
            if ((entity instanceof EntityPlayer && getOwner() != null && (entity == getOwner() || StatusController.arePlayersTeamMates(getOwner(), (EntityPlayer)entity))))
            {
                continue;
            }
            for (Vec3i laser : lasers)
            {
                Vec3d currentLaser = new Vec3d(laser);
                if (entity.getEntityBoundingBox() != ZERO_AABB && entity.getEntityBoundingBox().isVecInside(currentLaser) && !victims.contains(entity))
                {
                    victims.add(entity);
                }
            }
        }
        if (victims.isEmpty())
        {
            return;
        }
        boom(victims);
    }

    private void boom(List<EntityLivingBase> victims)
    {
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), Sounds.getGadgetSound(EnumGadget.CLAYMORE, "detect"), SoundCategory.BLOCKS, 1, 1);
        float pitch = (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F;
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, pitch);
        for (EntityLivingBase victim : victims)
        {
            victim.attackEntityFrom(Damage.causeClaymoreDamage(getOwner()), 20);
        }
        world.destroyBlock(pos, false);
    }

    public Vec3i[] getLasers()
    {
        return lasers;
    }

    public boolean isActive()
    {
        return active;
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        super.writeCustomNBT(nbt);
        nbt.setInteger("ticksUntilActivation", ticksUntilActivation);
        nbt.setBoolean("active", active);
        nbt.setInteger("facing", facing.getIndex());
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        super.readCustomNBT(nbt);
        ticksUntilActivation = nbt.getInteger("ticksUntilActivation");
        active = nbt.getBoolean("active");
        facing = EnumFacing.getFront(nbt.getInteger("facing"));
    }
}