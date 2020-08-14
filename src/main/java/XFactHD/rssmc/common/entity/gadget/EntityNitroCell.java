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

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.api.block.HitType;
import XFactHD.rssmc.api.block.IDestructable;
import XFactHD.rssmc.common.data.EnumGadget;
import XFactHD.rssmc.common.data.EnumParticle;
import XFactHD.rssmc.common.utils.Damage;
import XFactHD.rssmc.common.utils.helper.ExplosionHelper;
import XFactHD.rssmc.client.util.Sounds;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

@SuppressWarnings({"deprecation", "ConstantConditions"})
public class EntityNitroCell extends EntityThrowable
{
    private boolean sticked = false;
    private EnumFacing stickSide = null;

    public EntityNitroCell(World world)
    {
        super(world);
        setSize(.75F, .75F);
        setEntityBoundingBox(new AxisAlignedBB(.2, 0, .2, .8, .8, .8));
    }

    public EntityNitroCell(World world, EntityPlayer player)
    {
        super(world, player);
        setSize(.75F, .75F);
        setEntityBoundingBox(new AxisAlignedBB(.2, 0, .2, .8, .8, .8));
    }

    @Override
    protected void onImpact(RayTraceResult result)
    {
        if (result.typeOfHit == RayTraceResult.Type.ENTITY)
        {
            result.entityHit.attackEntityFrom(Damage.causeHitByGadgetDamage((EntityPlayer)getThrower(), EnumGadget.NITRO_CELL), 1);
        }
        else if (result.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            sticked = true;
            stickSide = result.sideHit;
            switch (stickSide)
            {
                case UP:
                {
                    rotationYaw = 0;
                    rotationPitch = 0;
                    break;
                }
                case DOWN:
                {
                    rotationYaw = 0;
                    rotationPitch = 180;
                    break;
                }
                case NORTH:
                {
                    rotationYaw = 180;
                    rotationPitch = 90;
                    break;
                }
                case EAST:
                {
                    rotationYaw = -90;
                    rotationPitch = 90;
                    break;
                }
                case SOUTH:
                {
                    rotationYaw = 0;
                    rotationPitch = 90;
                    break;
                }
                case WEST:
                {
                    rotationYaw = 90;
                    rotationPitch = 90;
                    break;
                }
            }
        }
    }

    @Override
    public void onUpdate()
    {
        if (!sticked)
        {
            super.onUpdate();
        }
        else
        {
            onEntityUpdate();
            switch (stickSide)
            {
                case UP:
                {
                    rotationYaw = 0;
                    rotationPitch = 0;
                    break;
                }
                case DOWN:
                {
                    rotationYaw = 0;
                    rotationPitch = 180;
                    break;
                }
                case NORTH:
                {
                    rotationYaw = 180;
                    rotationPitch = 90;
                    break;
                }
                case EAST:
                {
                    rotationYaw = -90;
                    rotationPitch = 90;
                    break;
                }
                case SOUTH:
                {
                    rotationYaw = 0;
                    rotationPitch = 90;
                    break;
                }
                case WEST:
                {
                    rotationYaw = 90;
                    rotationPitch = 90;
                    break;
                }
            }
        }
    }

    public void boom()
    {
        world.playSound(posX, posY, posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1, 1, false);
        ExplosionHelper.causeExplosionAtEntity(world, this, 3, 24);
        for (BlockPos pos : BlockPos.getAllInBox(getPosition().down(3).north(3).west(3), getPosition().up(3).south(3).east(3)))
        {
            IBlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof IDestructable)
            {
                ((IDestructable)state.getBlock()).destruct(world, pos, state, HitType.C4, null);
            }
        }
        setDead();
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        world.playSound(null, posX, posY, posZ, Sounds.soundDestroyElectricDevice, SoundCategory.BLOCKS, 1, 1);
        RainbowSixSiegeMC.proxy.spawnParticle(EnumParticle.DESTROY_ELECTRIC_DEVICE, world.provider.getDimension(), posX, posY, posZ);
        setDead();
        return true;
    }

    public boolean isSticked()
    {
        return sticked;
    }

    public EnumFacing getStickSide()
    {
        return stickSide;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("sticked", sticked);
        if (stickSide != null) { nbt.setInteger("side", stickSide.getIndex()); }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        sticked = nbt.getBoolean("sticked");
        stickSide = nbt.hasKey("side") ? EnumFacing.getFront(nbt.getInteger("side")) : null;
    }
}