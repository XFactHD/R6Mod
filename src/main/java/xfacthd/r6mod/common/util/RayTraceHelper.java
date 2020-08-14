package xfacthd.r6mod.common.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import xfacthd.r6mod.api.interaction.IShockable;
import xfacthd.r6mod.api.interaction.IShootable;
import xfacthd.r6mod.common.entities.camera.EntityEvilEyeCamera;
import xfacthd.r6mod.common.entities.camera.EntityYokaiDrone;
import xfacthd.r6mod.common.net.NetworkHandler;
import xfacthd.r6mod.common.net.packets.debug.PacketRayTraceResult;
import xfacthd.r6mod.common.tileentities.gadgets.TileEntityClaymore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public class RayTraceHelper
{
    private static final Random rand = new Random();
    private static final Predicate<? super Entity> BULLET_PREDICATE = entity ->
    {
        if (entity instanceof IShootable || entity instanceof ItemFrameEntity) { return true; }
        return entity instanceof LivingEntity && entity.isAlive();
    };
    private static final Predicate<? super Entity> SONIC_PREDICATE = entity -> entity instanceof PlayerEntity && entity.isAlive();
    private static final Predicate<? super Entity> EVIL_EYE_PREDICATE = entity ->
    {
        if (entity instanceof IShockable || entity instanceof ItemFrameEntity) { return true; }
        return entity instanceof LivingEntity && entity.isAlive();
    };

    public static List<HitData> raytraceGunShot(World world, PlayerEntity shooter, int range, int maxPenetration, float spread)
    {
        //Spread calculation copied from AbstractArrowEntity::shoot()
        double spreadX = rand.nextGaussian() * (double)0.0075F * (double)spread;
        double spreadY = rand.nextGaussian() * (double)0.0075F * (double)spread;
        double spreadZ = rand.nextGaussian() * (double)0.0075F * (double)spread;

        Vector3d dirVecNormal = shooter.getLookVec().normalize().add(spreadX, spreadY, spreadZ);
        Vector3d startVec = shooter.getPositionVec().add(0, shooter.getEyeHeight(), 0);
        Vector3d endVec = startVec.add(dirVecNormal.scale(range));

        BlockRayTraceResult result = raytraceRangeDetailed(world, shooter, startVec, endVec);
        Vector3d rangeVec = result.getHitVec();

        if (Config.INSTANCE.debugGunShots)
        {
            NetworkHandler.sendToPlayer(new PacketRayTraceResult(startVec, rangeVec), (ServerPlayerEntity) shooter);
        }

        List<HitData> hitData = raytraceEntities(world, shooter, startVec, rangeVec, maxPenetration, true, BULLET_PREDICATE);
        if (maxPenetration == -1 || hitData.size() < maxPenetration)
        {
            if (result.getType() != RayTraceResult.Type.MISS)
            {
                hitData.add(new HitData(shooter, result.getPos(), result.getHitVec()));
            }
        }
        return hitData;
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean raytraceClaymore(TileEntityClaymore te, Vector3d start, Vector3d left, Vector3d middle, Vector3d right)
    {
        if (te.getWorld() == null || te.getOwner() == null) { return false; }

        left = raytraceRange(te.getWorld(), te.getOwner(), start, left);
        middle = raytraceRange(te.getWorld(), te.getOwner(), start, middle);
        right = raytraceRange(te.getWorld(), te.getOwner(), start, right);

        Predicate<? super Entity> TRIP_PREDICATE = entity ->
        {
            if (entity instanceof PlayerEntity)
            {
                return entity.isAlive() && (entity.getTeam() == null || entity.getTeam().isSameTeam(te.getOwner().getTeam()));
            }
            return entity instanceof LivingEntity && entity.canBeCollidedWith() && entity.isAlive();
        };

        ArrayList<HitData> trips = new ArrayList<>();
        trips.addAll(RayTraceHelper.raytraceEntities(te.getWorld(), te.getOwner(), start, start, left,   1, true, TRIP_PREDICATE));
        trips.addAll(RayTraceHelper.raytraceEntities(te.getWorld(), te.getOwner(), start, start, middle, 1, true, TRIP_PREDICATE));
        trips.addAll(RayTraceHelper.raytraceEntities(te.getWorld(), te.getOwner(), start, start, right,  1, true, TRIP_PREDICATE));
        return !trips.isEmpty();
    }

    public static List<HitData> raytraceSonicBurst(EntityYokaiDrone drone, int range)
    {
        PlayerEntity shooter = drone.getPrimaryUser();
        Vector3d dirVecNormal = drone.getLookVec().normalize();
        Vector3d startVec = drone.getPositionVec().add(0, drone.getEyeHeight(), 0).add(dirVecNormal);
        Vector3d endVec = startVec.add(dirVecNormal.scale(range));

        Vector3d rangeVec = raytraceRange(drone.world, shooter, startVec, endVec);
        return raytraceEntities(drone.world, shooter, startVec, startVec, rangeVec, -1, false, SONIC_PREDICATE);
    }

    public static HitData raytraceEvilEyeLaser(EntityEvilEyeCamera camera, int range)
    {
        PlayerEntity shooter = camera.getPrimaryUser();
        Vector3d dirVecNormal = camera.getLookVec().normalize();
        Vector3d startVec = camera.getPositionVec().add(0, camera.getEyeHeight(), 0).add(dirVecNormal);
        Vector3d endVec = startVec.add(dirVecNormal.scale(range));

        BlockRayTraceResult result = raytraceRangeDetailed(camera.world, shooter, startVec, endVec);
        Vector3d rangeVec = result.getHitVec();

        List<HitData> hit = raytraceEntities(camera.world, shooter, startVec, startVec, rangeVec, 1, false, EVIL_EYE_PREDICATE);
        if (!hit.isEmpty()) { return hit.get(0); }

        if (result.getType() != RayTraceResult.Type.MISS)
        {
            return new HitData(shooter, result.getPos(), result.getHitVec());
        }

        return null;
    }

    public static Vector3d raytraceRange(World world, PlayerEntity player, Vector3d start, Vector3d end)
    {
        if (player == null) { return end; }
        return raytraceRangeDetailed(world, player, start, end).getHitVec();
    }

    public static BlockRayTraceResult raytraceRangeDetailed(World world, @Nonnull PlayerEntity player, Vector3d start, Vector3d end)
    {
        return world.rayTraceBlocks(new RayTraceContext(start, end, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, player));
    }

    public static List<HitData> raytraceEntities(World world, PlayerEntity shooter, Vector3d start, Vector3d end, int maxCount, boolean ignoreShooter, Predicate<? super Entity> predicate)
    {
        return raytraceEntities(world, shooter, shooter.getPositionVec(), start, end, maxCount, ignoreShooter, predicate);
    }

    public static List<HitData> raytraceEntities(World world, @Nullable PlayerEntity shooter, Vector3d sourceVec, Vector3d start, Vector3d end, int maxCount, boolean ignoreShooter, Predicate<? super Entity> predicate)
    {
        AxisAlignedBB bb = new AxisAlignedBB(start, end);
        List<Entity> entities = world.getEntitiesInAABBexcluding(ignoreShooter ? shooter : null, bb, predicate);
        entities.sort((e1, e2) ->
        {
            double d1 = e1.getPositionVec().squareDistanceTo(sourceVec);
            double d2 = e2.getPositionVec().squareDistanceTo(sourceVec);
            return Double.compare(d1, d2);
        });

        if (entities.isEmpty()) { return Collections.emptyList(); }

        int count = 0;
        ArrayList<HitData> hitData = new ArrayList<>();
        for (Entity entity : entities)
        {
            if (!entity.noClip)
            {
                Optional<Vector3d> intercept = entity.getBoundingBox().rayTrace(start, end);
                if (intercept.isPresent() && (shooter == null || shooter.canEntityBeSeen(entity)))
                {
                    boolean headshot = entity instanceof LivingEntity && isHeadshot(intercept.get(), (LivingEntity) entity);
                    hitData.add(new HitData(shooter, entity, headshot, intercept.get()));
                    count++;
                }
            }

            if (maxCount != -1 && count >= maxCount) { break; }
        }

        return hitData;
    }

    private static boolean isHeadshot(Vector3d hitVec, LivingEntity entityHit)
    {
        double hit = hitVec.y;
        double eyeHeight = entityHit.getPosY() + (double)entityHit.getEyeHeight();
        double dif = hit - eyeHeight;
        return (dif >= -.15 && dif < (entityHit.getHeight() - entityHit.getEyeHeight()));
    }
}