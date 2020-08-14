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

package XFactHD.rssmc.common.utils.helper;

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.common.blocks.gadget.TileEntityActiveDefenseSystem;
import XFactHD.rssmc.common.entity.gadget.AbstractEntityGrenade;
import XFactHD.rssmc.common.net.PacketAddBulletTrace;
import XFactHD.rssmc.common.utils.Utils;
import XFactHD.rssmc.common.utils.utilClasses.HitData;
import XFactHD.rssmc.common.utils.utilClasses.Position;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RayTraceUtils
{
    private static Random random = new Random();
    private static final Predicate<? super Entity> entityPredicate = Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>() {
        public boolean apply(@Nullable Entity entity) { return entity != null && entity.canBeCollidedWith() && entity instanceof EntityLivingBase; }
    });

    //spreadPercent must not be higher than 10
    public static ArrayList<HitData> rayTraceEntitiesWithSpread(World world, EntityPlayer shooter, Position startPos, Vec3d dirVecNormal, int length, int maxHitCount, int spreadPercent)
    {
        if (spreadPercent == 0) { spreadPercent = 1; }
        double randomX = ((double) random.nextInt(spreadPercent) * 0.1) * (random.nextBoolean() ? -1 : 1);
        double randomY = ((double) random.nextInt(spreadPercent) * 0.1) * (random.nextBoolean() ? -1 : 1);
        double randomZ = ((double) random.nextInt(spreadPercent) * 0.1) * (random.nextBoolean() ? -1 : 1);
        dirVecNormal.addVector(randomX, randomY, randomZ).normalize();
        return rayTraceEntities(world, shooter, startPos, dirVecNormal, length, maxHitCount);
    }

    public static ArrayList<HitData> rayTraceEntities(World world, EntityPlayer shooter, Position startPos, Vec3d dirVecNormal, int length, int maxHitCount)
    {
        Vec3d startVec = new Vec3d(startPos.getX(), startPos.getY() + shooter.getEyeHeight(), startPos.getZ());
        Vec3d endVec   = startVec.add(dirVecNormal.scale(length));
        if (ConfigHandler.debugRenderBullet && shooter.isCreative())
        {
            RainbowSixSiegeMC.NET.sendMessageToClient(new PacketAddBulletTrace(Utils.getPlayerPosition(shooter), startVec, endVec), shooter);
        }

        RayTraceResult result = world.rayTraceBlocks(startVec, endVec, false, true, false);

        Vec3d aabbVec1 = startVec.add(new Vec3d(0, -1, 0));
        Vec3d aabbVec2 = endVec.add(new Vec3d(0, 1, 0));

        AxisAlignedBB bb = new AxisAlignedBB(aabbVec1, result != null ? result.hitVec.add(new Vec3d(0, 1, 0)) : aabbVec2);
        List<Entity> entities = world.getEntitiesInAABBexcluding(shooter, bb, entityPredicate);

        ArrayList<HitData> entitiesInVector = new ArrayList<>();
        Vec3d endVecEntityCheck = result != null ? result.hitVec : endVec;

        for (Entity entity : entities)
        {
            EntityLivingBase victim = (EntityLivingBase)entity;
            if (!victim.noClip && victim.getEntityBoundingBox() != null)
            {
                RayTraceResult intercept = victim.getEntityBoundingBox().calculateIntercept(startVec, endVecEntityCheck);
                if (intercept != null)
                {
                    intercept.entityHit = entity;
                    entitiesInVector.add(new HitData(victim, intercept, shooter, isHeadshot(intercept.hitVec, intercept.entityHit)));
                    if (entitiesInVector.size() >= maxHitCount) { break; }
                }
            }
        }

        return entitiesInVector;
    }

    public static AbstractEntityGrenade rayTraceGrenades(World world, TileEntityActiveDefenseSystem te, BlockPos ads, int radius)
    {
        AxisAlignedBB aabb;
        switch (te.getFacing())
        {
            case UP:
                aabb = new AxisAlignedBB(ads.getX() - radius, ads.getY(), ads.getZ() - radius, ads.getX() + radius + 1, ads.getY() + radius + 1, ads.getZ() + radius + 1);
                break;
            case NORTH:
                aabb = new AxisAlignedBB(ads.getX() - radius, ads.getY() - radius, ads.getZ() - radius, ads.getX() + radius + 1, ads.getY() + radius + 1, ads.getZ() + 1);
                break;
            case SOUTH:
                aabb = new AxisAlignedBB(ads.getX() - radius, ads.getY() - radius, ads.getZ(), ads.getX() + radius + 1, ads.getY() + radius + 1, ads.getZ() + radius + 1);
                break;
            case WEST:
                aabb = new AxisAlignedBB(ads.getX() - radius, ads.getY() - radius, ads.getZ() - radius, ads.getX() + 1, ads.getY() + radius + 1, ads.getZ() + radius + 1);
                break;
            case EAST:
                aabb = new AxisAlignedBB(ads.getX(), ads.getY() - radius, ads.getZ() - radius, ads.getX() + radius + 1, ads.getY() + radius + 1, ads.getZ() + radius + 1);
                break;
            default: throw new UnsupportedOperationException();
        }
        List<AbstractEntityGrenade> grenades = world.getEntitiesWithinAABB(AbstractEntityGrenade.class, aabb, EntitySelectors.IS_ALIVE);
        return !grenades.isEmpty() ? grenades.get(0) : null;
    }

    public static Position rayTraceMarker(World world, EntityPlayer tracer)
    {
        Vec3d startVec = new Vec3d(tracer.posX, tracer.posY + tracer.getEyeHeight(), tracer.posZ);
        Vec3d endVec = startVec.add(tracer.getLookVec().normalize().scale(ConfigHandler.maxMarkRange));
        RayTraceResult result = world.rayTraceBlocks(startVec, endVec);
        if (result == null || result.getBlockPos().equals(BlockPos.ORIGIN))
        {
            return null;
        }
        Vec3d hit = result.hitVec;
        double yOff = result.sideHit == EnumFacing.UP ? .2 : result.sideHit == EnumFacing.DOWN ? -.2 : 0;
        return new Position(hit.xCoord, hit.yCoord + yOff, hit.zCoord);
    }

    private static boolean isHeadshot(Vec3d hitVec, Entity entityHit)
    {
        double hit = hitVec.yCoord;
        double eyeHeight = entityHit.posY + (double)entityHit.getEyeHeight();
        double dif = hit - eyeHeight;
        return (dif >= -.15 && hit < entityHit.height);
    }
}