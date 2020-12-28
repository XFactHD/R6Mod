package xfacthd.r6mod.common.tileentities.gadgets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import xfacthd.r6mod.common.data.PropertyHolder;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.data.types.TileEntityTypes;
import xfacthd.r6mod.common.entities.grenade.EntityEMPGrenade;
import xfacthd.r6mod.common.tileentities.TileEntityGadget;
import xfacthd.r6mod.common.data.types.R6SoundEvents;
import xfacthd.r6mod.common.util.WorldUtils;
import xfacthd.r6mod.common.util.data.*;

import java.util.List;

public class TileEntityBanshee extends TileEntityGadget
{
    private static final long SOUND_LENGTH = 10;
    private static final long DISABLE_TIME = 100;
    private static final double RADIUS = 4;
    private static final double MIN_DIST_SQ = 2 * 2;
    private static final double MAX_DIST_SQ = RADIUS * RADIUS;
    private static final double MIN_DIST_MULT = .5;
    private static final double MAX_DIST_MULT = .75;

    private Vector3d centerVec = null;
    private AxisAlignedBB searchBox = null;
    private boolean active = false;
    private long lastSound = 0;
    private boolean jammed = false;
    private long jamStart = 0;

    public TileEntityBanshee() { super(TileEntityTypes.tileTypeBanshee, EnumGadget.BANSHEE); }

    @Override
    public void tick()
    {
        super.tick();

        //noinspection ConstantConditions
        if (world.isRemote()) { return; }

        if (jammed)
        {
            if (active)
            {
                active = false;
                markFullUpdate();
            }

            if (world.getGameTime() - jamStart > DISABLE_TIME)
            {
                jammed = false;
                markFullUpdate();
            }
        }
        else
        {
            final Vector3d center = getCenterVec();
            final PlayerEntity owner = getOwner();

            AxisAlignedBB aabb = getSearchBox();
            List<Entity> entities = world.getEntitiesInAABBexcluding(getOwner(), aabb, (e) ->
            {
                double dist = e.getDistanceSq(center);
                if (dist > MAX_DIST_SQ) { return false; }
                if (!WorldUtils.canEntityBeSeen(world, center, e)) { return false; }
                if (getTeam().equals("null"))
                {
                    if (owner != null && e == owner) { return false; }
                }
                else
                {
                    if (e.getTeam() != null && e.getTeam().getName().equals(getTeam())) { return false; }
                }
                return e instanceof LivingEntity && e.isAlive();
            });
            active = !entities.isEmpty();
            if (!entities.isEmpty())
            {
                entities.forEach((e) ->
                {
                    double dist = e.getDistanceSq(center);
                    double factor = (dist - MIN_DIST_SQ) / (MAX_DIST_SQ - MIN_DIST_SQ);
                    double mult = MathHelper.clampedLerp(MIN_DIST_MULT, MAX_DIST_MULT, factor);
                    e.setMotionMultiplier(getBlockState(), new Vector3d(mult, mult, mult));
                });
            }
        }

        if (active && world.getGameTime() - lastSound >= SOUND_LENGTH)
        {
            SoundEvent sound = R6SoundEvents.getGadgetSound(EnumGadget.BANSHEE, "noise");
            world.playSound(null, pos, sound, SoundCategory.BLOCKS, 1, 1);
            lastSound = world.getGameTime();
        }
    }

    private AxisAlignedBB getSearchBox()
    {
        if (searchBox == null)
        {
            Direction facing = getBlockState().get(PropertyHolder.FACING_NOT_DOWN);

            Vector3d center = getCenterVec();
            double x = center.getX();
            double y = center.getY();
            double z = center.getZ();

            double minX;
            double minY;
            double minZ;
            double maxX;
            double maxY;
            double maxZ;

            if (facing == Direction.UP)
            {
                minX = x - RADIUS;
                maxX = x + RADIUS;
                minY = y;
                maxY = y + RADIUS;
                minZ = z - RADIUS;
                maxZ = z + RADIUS;
            }
            else
            {
                Direction side = facing.rotateYCCW();

                if (facing.getAxis() == Direction.Axis.X)
                {
                    minX = x;
                    maxX = x + facing.getXOffset() * RADIUS;
                    minY = y - RADIUS;
                    maxY = y + RADIUS;
                    minZ = z - side.getZOffset() * RADIUS;
                    maxZ = z + side.getZOffset() * RADIUS;
                }
                else
                {
                    minX = x - side.getXOffset() * RADIUS;
                    maxX = x + side.getXOffset() * RADIUS;
                    minY = y - RADIUS;
                    maxY = y + RADIUS;
                    minZ = z;
                    maxZ = z + facing.getZOffset() * RADIUS;
                }
            }

            searchBox = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
        }
        return searchBox;
    }
    
    private Vector3d getCenterVec()
    {
        if (centerVec == null)
        {
            Direction facing = getBlockState().get(PropertyHolder.FACING_NOT_DOWN);

            double x = pos.getX();
            double y = pos.getY();
            double z = pos.getZ();

            if (facing == Direction.UP)
            {
                x += .5D;
                z += .5D;
            }
            else
            {
                y += .5D;

                if (facing.getAxis() == Direction.Axis.X)
                {
                    z += .5D;

                    if (facing == Direction.WEST)
                    {
                        x += 1;
                    }
                }
                else if (facing.getAxis() == Direction.Axis.Z)
                {
                    x += .5D;

                    if (facing == Direction.NORTH)
                    {
                        z += 1;
                    }
                }
            }

            centerVec = new Vector3d(x, y, z);
        }
        return centerVec;
    }

    @Override //Banshee is bulletproof
    public void shoot(PlayerEntity shooter, Vector3d hitVec) { }

    @Override
    public void shock(PlayerEntity shooter, Vector3d hitVec)
    {
        //noinspection ConstantConditions
        jamStart = world.getGameTime();
        jammed = true;
        markFullUpdate();

        PointManager.awardGadgetDisabled(EnumGadget.BANSHEE, shooter, getTeam());
    }

    @Override
    public void empPulse(EntityEMPGrenade emp)
    {
        if (!getTeam().equals("null") && !emp.getTeamName().equals(getTeam()))
        {
            //noinspection ConstantConditions
            jamStart = world.getGameTime();
            jammed = true;
            markFullUpdate();

            PlayerEntity thrower = emp.getThrowerEntity();
            if (thrower != null)
            {
                PointManager.awardGadgetDisabled(EnumGadget.BANSHEE, thrower, getTeam());
            }
        }
    }
}