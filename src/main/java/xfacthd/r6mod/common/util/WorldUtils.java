package xfacthd.r6mod.common.util;

import com.google.common.cache.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class WorldUtils
{
    private static final LoadingCache<VoxelShape, AxisAlignedBB> AABB_CACHE = CacheBuilder.newBuilder().maximumSize(512L).weakKeys().build(new CacheLoader<VoxelShape, AxisAlignedBB>()
    {
        public AxisAlignedBB load(VoxelShape shape) { return shape.getBoundingBox(); }
    });

    public static boolean isBottomSolid(World world, BlockPos pos)
    {
        VoxelShape shape = world.getBlockState(pos).getShape(world, pos);
        AxisAlignedBB aabb = AABB_CACHE.getUnchecked(shape);
        return aabb.minX == 0D && aabb.minZ == 0D && aabb.maxX == 1D && aabb.maxZ == 1D;
    }

    public static float getRandomSoundPitch(World world)
    {
        return (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F;
    }

    public static boolean canEntityBeSeen(World world, Vector3d pos, Entity entity)
    {
        if (world == null || entity == null) { return false; }

        return world.rayTraceBlocks(new RayTraceContext(pos, entity.getPositionVec(), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, entity)).getType() == RayTraceResult.Type.MISS;
    }
}