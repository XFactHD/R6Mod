package xfacthd.r6mod.common.tileentities.gadgets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import xfacthd.r6mod.api.IExplosionParticleSpawner;
import xfacthd.r6mod.api.interaction.IEMPInteract;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.data.types.TileEntityTypes;
import xfacthd.r6mod.common.entities.grenade.EntityEMPGrenade;
import xfacthd.r6mod.common.net.NetworkHandler;
import xfacthd.r6mod.common.net.packets.PacketExplosionParticles;
import xfacthd.r6mod.common.tileentities.TileEntityGadget;
import xfacthd.r6mod.common.util.RayTraceHelper;
import xfacthd.r6mod.common.util.WorldUtils;
import xfacthd.r6mod.common.util.damage.DamageSourceClaymore;

import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public class TileEntityClaymore extends TileEntityGadget implements IExplosionParticleSpawner, IEMPInteract
{
    private static final long ACTIVATE_DELAY = 30; //INFO: 30 ticks == 1.5 seconds
    private static final float FULL_DAMAGE = 142.0F / 5.0F; //Damage scaled to 20HP max health
    private static final double FULL_DAMAGE_DISTANCE = 2;
    private static final double FULL_DAMAGE_DISTANCE_SQ = FULL_DAMAGE_DISTANCE * FULL_DAMAGE_DISTANCE;
    private static final double ZERO_DAMAGE_DISTANCE = 6;
    private static final double ZERO_DAMAGE_DISTANCE_SQ = ZERO_DAMAGE_DISTANCE * ZERO_DAMAGE_DISTANCE;
    private static final Predicate<? super Entity> DAMAGE_PREDICATE = entity -> entity instanceof LivingEntity && entity.canBeCollidedWith() && entity.isAlive();
    private static final HashMap<Direction, Vec3d> ORIGIN_VECTORS = new HashMap<>();
    private static final HashMap<Direction, ImmutableTriple<Vec3d, Vec3d, Vec3d>> LASER_VECTORS = new HashMap<>();

    static
    {
        double angle = Math.toRadians(30);
        for (Direction dir : Direction.Plane.HORIZONTAL)
        {
            Vec3d centerVec = new Vec3d(dir.getXOffset(), 0, dir.getZOffset()).normalize().scale(3);
            Vec3d leftVec   = centerVec.rotateYaw((float)-angle).normalize().scale(3D + (1D - angle));
            Vec3d rightVec  = centerVec.rotateYaw((float) angle).normalize().scale(3D + (1D - angle));

            LASER_VECTORS.put(dir, ImmutableTriple.of(leftVec, centerVec, rightVec));
        }

        ORIGIN_VECTORS.put(Direction.NORTH, new Vec3d(0.5D, 3.635D/16D, 8D/16D));
        ORIGIN_VECTORS.put(Direction.EAST,  new Vec3d(8D/16D, 3.635D/16D, 0.5D));
        ORIGIN_VECTORS.put(Direction.SOUTH, new Vec3d(0.5D, 3.635D/16D, 8D/16D));
        ORIGIN_VECTORS.put(Direction.WEST,  new Vec3d(8D/16D, 3.635D/16D, 0.5D));
    }

    private Direction facing = Direction.NORTH;
    private boolean active = false;
    private Triple<Vec3d, Vec3d, Vec3d> lasers = Triple.of(Vec3d.ZERO, Vec3d.ZERO, Vec3d.ZERO);

    private boolean firstTick = true;
    private long startTime = 0;

    public TileEntityClaymore() { super(TileEntityTypes.tileTypeClaymore, EnumGadget.CLAYMORE); }

    public TileEntityClaymore(Direction facing)
    {
        this();
        this.facing = facing;
    }

    @Override
    public void tick()
    {
        super.tick();

        if (world == null || getOwner() == null) { return; }

        if (firstTick)
        {
            if (!active) { startTime = world.getGameTime(); }
            firstTick = false;
            markFullUpdate();
        }

        if (active)
        {
            if (!world.isRemote) { detectTripAndExplode(); }
            else { raytraceRenderVectors(); }
        }
        else if (world.getGameTime() - startTime >= ACTIVATE_DELAY)
        {
            active = true;
            world.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK, SoundCategory.BLOCKS, .25F, 2F);
            markFullUpdate();
        }
    }

    /*
     * Getters and setters
     */

    public Direction getFacing() { return facing; }

    public boolean isActive() { return active; }

    public Triple<Vec3d, Vec3d, Vec3d> getLasers() { return lasers; }

    /*
     * Private helpers
     */

    private void detectTripAndExplode()
    {
        Vec3d start =  new Vec3d(pos).add(ORIGIN_VECTORS.get(facing));
        Vec3d left =   new Vec3d(pos).add(ORIGIN_VECTORS.get(facing)).add(LASER_VECTORS.get(facing).left);
        Vec3d middle = new Vec3d(pos).add(ORIGIN_VECTORS.get(facing)).add(LASER_VECTORS.get(facing).middle);
        Vec3d right =  new Vec3d(pos).add(ORIGIN_VECTORS.get(facing)).add(LASER_VECTORS.get(facing).right);

        boolean tripped = RayTraceHelper.raytraceClaymore(this, start, left, middle, right);
        if (tripped)
        {
            findAndDamageEntities(start);
            //noinspection ConstantConditions
            world.removeBlock(pos, false);
            world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1, WorldUtils.getRandomSoundPitch(world));

            NetworkHandler.sendToPlayersTrackingChunk(new PacketExplosionParticles(pos), world.getChunkAt(pos));
        }
    }

    private void findAndDamageEntities(Vec3d origin)
    {
        double halfDist = ZERO_DAMAGE_DISTANCE / 2;
        Vec3d bbLeft  = origin.add(halfDist * facing.rotateYCCW().getXOffset(), 0, halfDist * facing.rotateYCCW().getZOffset()); //Offset to left of object
        Vec3d bbRight = origin.
                add(halfDist * facing.rotateY().getXOffset(), 0, halfDist * facing.rotateY().getZOffset()). //Offset to right of object
                add(ZERO_DAMAGE_DISTANCE * facing.getXOffset(), 0, ZERO_DAMAGE_DISTANCE * facing.getZOffset()); //Offset in front of object

        AxisAlignedBB bb = new AxisAlignedBB(bbLeft, bbRight);
        //noinspection ConstantConditions
        List<Entity> victims = world.getEntitiesInAABBexcluding(null, bb, DAMAGE_PREDICATE);
        for (Entity victim : victims)
        {
            if (!WorldUtils.canEntityBeSeen(world, new Vec3d(pos), victim)) { continue; }

            double distance = victim.getPositionVec().squareDistanceTo(origin);
            float damage = FULL_DAMAGE;
            if (distance > FULL_DAMAGE_DISTANCE_SQ)
            {
                double maxDist = ZERO_DAMAGE_DISTANCE_SQ - FULL_DAMAGE_DISTANCE_SQ;
                double dist = Math.min(maxDist, distance - FULL_DAMAGE_DISTANCE_SQ);
                damage *= 1.0 - (dist / maxDist);
            }

            if (damage > 0) { victim.attackEntityFrom(new DamageSourceClaymore(getOwner()), damage); }
        }
    }

    @Override
    public void spawnParticles()
    {
        //noinspection ConstantConditions
        world.addParticle(ParticleTypes.EXPLOSION, pos.getX() + .5D, pos.getY() + .5D, pos.getZ() + .5D, 0, 0, 0);
    }

    private void raytraceRenderVectors()
    {
        if (!Direction.Plane.HORIZONTAL.test(facing)) { return; }

        Vec3d start = new Vec3d(pos).add(ORIGIN_VECTORS.get(facing));
        Vec3d left = new Vec3d(pos).add(ORIGIN_VECTORS.get(facing)).add(LASER_VECTORS.get(facing).left);
        Vec3d middle = new Vec3d(pos).add(ORIGIN_VECTORS.get(facing)).add(LASER_VECTORS.get(facing).middle);
        Vec3d right = new Vec3d(pos).add(ORIGIN_VECTORS.get(facing)).add(LASER_VECTORS.get(facing).right);
        lasers = Triple.of(
                RayTraceHelper.raytraceRange(world, getOwner(), start, left).subtract(start),
                RayTraceHelper.raytraceRange(world, getOwner(), start, middle).subtract(start),
                RayTraceHelper.raytraceRange(world, getOwner(), start, right).subtract(start)
        );
    }

    @Override
    public void empPulse(EntityEMPGrenade emp)
    {
        if (!getTeam().equals("null") && !emp.getTeamName().equals(getTeam()))
        {
            //noinspection ConstantConditions
            world.destroyBlock(pos, false);
        }
    }

    /*
     * NBT stuff
     */

    @Override
    public void writeNetworkNBT(CompoundNBT nbt)
    {
        nbt.putInt("facing", facing.getIndex());
        nbt.putBoolean("active", active);
        super.writeNetworkNBT(nbt);
    }

    @Override
    public void readNetworkNBT(CompoundNBT nbt)
    {
        super.readNetworkNBT(nbt);
        facing = Direction.byIndex(nbt.getInt("facing"));
        active = nbt.getBoolean("active");
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        nbt.putInt("facing", facing.getIndex());
        nbt.putBoolean("active", active);
        return super.write(nbt);
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        super.read(nbt);
        facing = Direction.byIndex(nbt.getInt("facing"));
        active = nbt.getBoolean("active");
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() { return INFINITE_EXTENT_AABB; }
}