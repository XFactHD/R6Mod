package xfacthd.r6mod.common.util;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

public class WorldUtils
{
    public static boolean isSideSolid(World world, BlockPos pos, Direction face)
    {
        //FIXME: this doesn't actually check if the face is solid => doesn't work like isSideSolid() did in old versions
        return Block.doesSideFillSquare(world.getBlockState(pos).getShape(world, pos), face);
    }

    public static float getRandomSoundPitch(World world)
    {
        return (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F;
    }

    public static boolean canEntityBeSeen(World world, Vec3d pos, Entity entity)
    {
        if (world == null || entity == null) { return false; }

        return world.rayTraceBlocks(new RayTraceContext(pos, entity.getPositionVec(), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, entity)).getType() == RayTraceResult.Type.MISS;
    }
}