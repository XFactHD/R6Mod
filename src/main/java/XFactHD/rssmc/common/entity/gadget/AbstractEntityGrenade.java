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
import XFactHD.rssmc.common.data.EnumGadget;
import XFactHD.rssmc.common.net.PacketAddCollision;
import XFactHD.rssmc.common.utils.Damage;
import XFactHD.rssmc.common.utils.helper.ConfigHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class AbstractEntityGrenade extends EntityThrowable
{
    private boolean firstTick = false;
    protected boolean still = false;
    protected int timer;

    public AbstractEntityGrenade(World world)
    {
        super(world);
    }

    public AbstractEntityGrenade(World world, EntityPlayer thrower)
    {
        super(world, thrower);
        this.timer = 80;
    }

    public AbstractEntityGrenade(World world, EntityPlayer thrower, int timer)
    {
        super(world, thrower);
        this.timer = timer;
    }

    @Override
    protected void onImpact(RayTraceResult result) //TODO: needs a lot more work, use Processing Robot code for this!
    {
        if (result.typeOfHit == RayTraceResult.Type.ENTITY)
        {
            result.entityHit.attackEntityFrom(Damage.causeHitByGadgetDamage((EntityPlayer)getThrower(), getGadget()), 1);
        }
        else if (result.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            if (!world.isRemote && ConfigHandler.debugRenderGrenadePath)
            {
                RainbowSixSiegeMC.NET.sendMessageToClient(new PacketAddCollision(posX, posY, posZ), (EntityPlayer)getThrower());
            }
            if (result.sideHit.getAxis().isHorizontal())
            {
                BlockPos pos = result.getBlockPos();
                int x = result.sideHit.getAxis() == EnumFacing.Axis.Z ? pos.getX() : 0;
                int z = result.sideHit.getAxis() == EnumFacing.Axis.X ? pos.getZ() : 0;
                Vec3d cross = new Vec3d(0, pos.getY(), 0).crossProduct(new Vec3d(x, 0, z));
                Vec3d dir = new Vec3d(motionX, motionY, motionZ);
                double dot = dir.dotProduct(cross);
                double angle = Math.cos((float)dot);
                dir = dir.rotateYaw((float) angle * 2F);
                setThrowableHeading(dir.xCoord, dir.yCoord, dir.zCoord, 1, 0);
            }
            else
            {
                still = true;
                setPositionAndUpdate(lastTickPosX, lastTickPosY, lastTickPosZ);
                if (!(this instanceof EntityFragGrenade))
                {
                    timer = getTime();
                }
            }
        }
    }

    @Override
    public void onUpdate()
    {
        if (!firstTick && !world.isRemote)
        {
            if (ConfigHandler.debugRenderGrenadePath)
            {
                RainbowSixSiegeMC.NET.sendMessageToClient(new PacketAddCollision(-1, -1, -1), (EntityPlayer)getThrower());
                RainbowSixSiegeMC.NET.sendMessageToClient(new PacketAddCollision(posX, posY, posZ), (EntityPlayer)getThrower());
            }
            firstTick = true;
        }
        if (this instanceof EntityImpactGrenade) { super.onUpdate(); return; }

        if (!(this instanceof EntityFragGrenade))
        {
            if (!world.isRemote && timer > 0)
            {
                timer -= 1;
                if (timer == 0)
                {
                    boom();
                }
            }
        }

        if (!still)
        {
            super.onUpdate();
        }
        else
        {
            super.onEntityUpdate();
        }
    }

    private EnumGadget getGadget()
    {
        Class c = getClass();
        switch (c.getName())
        {
            case "EntityEMPGrenade": return EnumGadget.EMP_GRENADE;
            case "EntityFragGrenade": return EnumGadget.FRAG_GRENADE;
            case "EntitySmokeGrenade": return EnumGadget.SMOKE_GRENADE;
            case "EntityStunGrenade": return EnumGadget.STUN_GRENADE;
        }
        return null;
    }

    protected int getTime() { return 0; }

    protected void boom() {}

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("timer", timer);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        timer = nbt.getInteger("timer");
    }
}