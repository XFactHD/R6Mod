package xfacthd.r6mod.common.tileentities.gadgets;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import xfacthd.r6mod.api.interaction.*;
import xfacthd.r6mod.api.IExplosionParticleSpawner;
import xfacthd.r6mod.common.data.PropertyHolder;
import xfacthd.r6mod.common.data.types.TileEntityTypes;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.entities.grenade.EntityEMPGrenade;
import xfacthd.r6mod.common.net.NetworkHandler;
import xfacthd.r6mod.common.net.packets.PacketExplosionParticles;
import xfacthd.r6mod.common.tileentities.TileEntityGadget;
import xfacthd.r6mod.common.util.*;
import xfacthd.r6mod.common.util.damage.DamageSourceExplosive;

import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public class TileEntityThermiteCharge extends TileEntityGadget implements IActivatable, IExplosionParticleSpawner, IEMPInteract
{
    private static final int BURN_DURATION = 60; //60 ticks => 3 seconds
    private static final float MAX_DAMAGE = 142F / 5F;
    private static final float MAX_DIST_SQ = 2.5F * 2.5F;
    private static final Predicate<? super Entity> DAMAGE_PREDICATE = (entity) -> entity instanceof LivingEntity && entity.canBeCollidedWith() && entity.isAlive();
    private static final HashMap<Direction, Vec3d> ORIGIN_VECS = new HashMap<>();

    static
    {
        ORIGIN_VECS.put(Direction.NORTH, new Vec3d(0.5, 0.5,   0));
        ORIGIN_VECS.put(Direction.EAST,  new Vec3d(  1, 0.5, 0.5));
        ORIGIN_VECS.put(Direction.SOUTH, new Vec3d(0.5, 0.5,   1));
        ORIGIN_VECS.put(Direction.WEST,  new Vec3d(  0, 0.5, 0.5));
        ORIGIN_VECS.put(Direction.DOWN,  new Vec3d(0.5,   0, 0.5));
    }

    private boolean active = false;
    private long activateTime = 0;

    public TileEntityThermiteCharge() { super(TileEntityTypes.tileTypeThermiteCharge, EnumGadget.THERMITE_CHARGE); }

    @Override
    public void tick()
    {
        super.tick();
        if (world != null && !world.isRemote && active && getProgress() >= BURN_DURATION) { explode(); }
    }

    @Override
    public void activate(World world, BlockPos pos, String object, PlayerEntity player)
    {
        if (world.isRemote) { return; }

        if (object.equals(getObjectName()))
        {
            active = true;
            activateTime = world.getGameTime();

            world.playSound(null, pos, SoundEvents.ENTITY_CREEPER_PRIMED, SoundCategory.BLOCKS, 1, 1);

            markFullUpdate();
        }
    }

    private void explode()
    {
        Direction facing = getBlockState().get(PropertyHolder.FACING_NOT_UP);

        //noinspection ConstantConditions
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, WorldUtils.getRandomSoundPitch(world));

        BlockPos upLeftPos    = facing == Direction.DOWN ? pos.down().north().west() : pos.offset(facing).offset(facing.rotateY()).up();
        BlockPos downRightPos = facing == Direction.DOWN ? pos.down().south().east() : pos.offset(facing).offset(facing.rotateYCCW()).down();
        BlockPos.getAllInBox(upLeftPos, downRightPos).forEach((adjPos) ->
        {
           BlockState adjState = world.getBlockState(adjPos);
           TileEntity te = world.getTileEntity(adjPos);
           if (adjState.getBlock() instanceof IDestructable)
           {
               ((IDestructable) adjState.getBlock()).destroy(world, adjPos, adjState, getOwner(), EnumGadget.THERMITE_CHARGE, facing.getOpposite());
           }
           else if (adjState.getBlock() instanceof IHardDestructable)
           {
               ((IHardDestructable) adjState.getBlock()).destroy(world, adjPos, adjState, getOwner(), EnumGadget.THERMITE_CHARGE, facing.getOpposite());
           }
           else if (te instanceof IDestructable)
           {
               ((IDestructable) te).destroy(world, adjPos, adjState, getOwner(), EnumGadget.THERMITE_CHARGE, facing.getOpposite());
           }
           else if (te instanceof IHardDestructable)
           {
               ((IHardDestructable) te).destroy(world, adjPos, adjState, getOwner(), EnumGadget.THERMITE_CHARGE, facing.getOpposite());
           }
           else if (Config.INSTANCE.destroyWood && adjState.getMaterial() == Material.WOOD)
           {
               world.destroyBlock(adjPos, false);
           }
        });

        damageEntities();

        NetworkHandler.sendToPlayersTrackingChunk(new PacketExplosionParticles(pos), world.getChunkAt(pos));

        world.removeBlock(pos, false);
    }

    private void damageEntities()
    {
        Direction facing = getBlockState().get(PropertyHolder.FACING_NOT_UP);

        //Create bounding boxes to search for enemies to damage
        AxisAlignedBB frontBB;
        AxisAlignedBB rearBB;
        if (facing == Direction.DOWN)
        {
            frontBB = new AxisAlignedBB(pos.getX() - 0.5, pos.getY(), pos.getZ() - 0.5, pos.getX() + 1.5, pos.getY() + 0.5, pos.getZ() + 1.5);

            //noinspection ConstantConditions
            double xNeg = pos.getX() - (world.isAirBlock(pos.add(-1, -1,  0)) ? 1 : 0);
            double zNeg = pos.getZ() - (world.isAirBlock(pos.add( 0, -1, -1)) ? 1 : 0);
            double xPos = pos.getX() + (world.isAirBlock(pos.add( 1, -1,  0)) ? 2 : 1);
            double zPos = pos.getZ() + (world.isAirBlock(pos.add( 0, -1,  1)) ? 2 : 1);
            rearBB = new AxisAlignedBB(xNeg, pos.getY() - 0.5, zNeg, xPos, pos.getY() - 2, zPos);
        }
        else if (facing == Direction.NORTH)
        {
            frontBB = new AxisAlignedBB(pos.getX() - 0.5, pos.getY() - 0.5, pos.getZ(), pos.getX() + 1.5, pos.getY() + 1.5, pos.getZ() + 0.5);

            //noinspection ConstantConditions
            double xNeg = pos.getX() - (world.isAirBlock(pos.add(-1,  0, -1)) ? 1 : 0);
            double yNeg = pos.getY() - (world.isAirBlock(pos.add( 0, -1, -1)) ? 1 : 0);
            double xPos = pos.getX() + (world.isAirBlock(pos.add( 1,  0, -1)) ? 2 : 1);
            double yPos = pos.getY() + (world.isAirBlock(pos.add( 0,  1, -1)) ? 2 : 1);
            rearBB = new AxisAlignedBB(xNeg, yNeg, pos.getZ() - 2, xPos, yPos, pos.getZ() - 0.5);
        }
        else if (facing == Direction.EAST)
        {
            frontBB = new AxisAlignedBB(pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() - 0.5, pos.getX() + 1, pos.getY() + 1.5, pos.getZ() + 1.5);

            //noinspection ConstantConditions
            double yNeg = pos.getY() - (world.isAirBlock(pos.add(1, -1,  0)) ? 1 : 0);
            double zNeg = pos.getZ() - (world.isAirBlock(pos.add(1,  0, -1)) ? 1 : 0);
            double yPos = pos.getY() + (world.isAirBlock(pos.add(1,  1,  0)) ? 2 : 1);
            double zPos = pos.getZ() + (world.isAirBlock(pos.add(1,  0,  1)) ? 2 : 1);
            rearBB = new AxisAlignedBB(pos.getX() + 1.5, yNeg, zNeg, pos.getX() + 3, yPos, zPos);
        }
        else if (facing == Direction.SOUTH)
        {
            frontBB = new AxisAlignedBB(pos.getX() - 0.5, pos.getY() - 0.5, pos.getZ() + 0.5, pos.getX() + 1.5, pos.getY() + 1.5, pos.getZ() + 1);

            //noinspection ConstantConditions
            double xNeg = pos.getX() - (world.isAirBlock(pos.add(-1,  0, 1)) ? 1 : 0);
            double yNeg = pos.getY() - (world.isAirBlock(pos.add( 0, -1, 1)) ? 1 : 0);
            double xPos = pos.getX() + (world.isAirBlock(pos.add( 1,  0, 1)) ? 2 : 1);
            double yPos = pos.getY() + (world.isAirBlock(pos.add( 0,  1, 1)) ? 2 : 1);
            rearBB = new AxisAlignedBB(xNeg, yNeg, pos.getZ() + 1.5, xPos, yPos, pos.getZ() + 3);
        }
        else if (facing == Direction.WEST)
        {
            frontBB = new AxisAlignedBB(pos.getX(), pos.getY() - 0.5, pos.getZ() - 0.5, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 1.5);

            //noinspection ConstantConditions
            double yNeg = pos.getY() - (world.isAirBlock(pos.add(-1, -1,  0)) ? 1 : 0);
            double zNeg = pos.getZ() - (world.isAirBlock(pos.add(-1,  0, -1)) ? 1 : 0);
            double yPos = pos.getY() + (world.isAirBlock(pos.add(-1,  1,  0)) ? 2 : 1);
            double zPos = pos.getZ() + (world.isAirBlock(pos.add(-1,  0,  1)) ? 2 : 1);
            rearBB = new AxisAlignedBB(pos.getX() - 2, yNeg, zNeg, pos.getX() - 0.5, yPos, zPos);
        }
        else { throw new RuntimeException("Invalid facing direction, can't face up!"); }

        //Find and damage entities on the side of the wall the charge is on
        Vec3d origin = new Vec3d(pos).add(ORIGIN_VECS.get(facing));
        List<Entity> entities = world.getEntitiesInAABBexcluding(null, frontBB, DAMAGE_PREDICATE);
        for (Entity entity : entities)
        {
            Vec3d entityVec = entity.getPositionVec().add(0, entity.getHeight() / 2D, 0);
            double distance = entityVec.squareDistanceTo(origin);

            float damage = MAX_DAMAGE * (1.0F - (Math.min((float) distance, MAX_DIST_SQ) / MAX_DIST_SQ));
            entity.attackEntityFrom(new DamageSourceExplosive(getOwner(), EnumGadget.THERMITE_CHARGE), damage);

            //NetworkHandler.sendToPlayer(new PacketRayTraceResult(origin, entityVec), (ServerPlayerEntity) getOwner());
        }

        //Find and damage entities on the back side of the wall
        origin = origin.add(facing.getXOffset(), facing.getYOffset(), facing.getZOffset());
        entities = world.getEntitiesInAABBexcluding(null, rearBB, DAMAGE_PREDICATE);
        for (Entity entity : entities)
        {
            Vec3d entityVec = entity.getPositionVec().add(0, entity.getHeight() / 2D, 0);
            double distance = entityVec.squareDistanceTo(origin);

            float damage = MAX_DAMAGE * (1.0F - (Math.min((float) distance, MAX_DIST_SQ) / MAX_DIST_SQ));
            entity.attackEntityFrom(new DamageSourceExplosive(getOwner(), EnumGadget.THERMITE_CHARGE), damage);
        }
    }

    @Override
    public void spawnParticles()
    {
        Direction facing = getBlockState().get(PropertyHolder.FACING_NOT_UP);

        BlockPos upLeftPos    = facing == Direction.DOWN ? pos.down().north().west() : pos.offset(facing).offset(facing.rotateY()).up();
        BlockPos downRightPos = facing == Direction.DOWN ? pos.down().south().east() : pos.offset(facing).offset(facing.rotateYCCW()).down();
        BlockPos.getAllInBox(upLeftPos, downRightPos).forEach((adjPos) ->
        {
            BlockPos pPosF = adjPos.offset(facing.getOpposite());
            BlockPos pPosB = adjPos.offset(facing);

            //noinspection ConstantConditions
            world.addParticle(ParticleTypes.EXPLOSION, pPosF.getX() + .5D, pPosF.getY() + .5D, pPosF.getZ() + .5D, 0, 0, 0);
            world.addParticle(ParticleTypes.EXPLOSION, pPosB.getX() + .5D, pPosB.getY() + .5D, pPosB.getZ() + .5D, 0, 0, 0);
        });
    }

    @Override
    public String getObjectName() { return "block_thermite_charge"; }

    @Override
    public void empPulse(EntityEMPGrenade emp)
    {
        if (!getTeam().equals("null") && !emp.getTeamName().equals(getTeam()))
        {
            //noinspection ConstantConditions
            world.destroyBlock(pos, false);
        }
    }

    public Direction getFacing() { return getBlockState().get(PropertyHolder.FACING_NOT_UP); }

    public boolean isActive() { return active; }

    public int getProgress() { return world == null ? 0 : (int)(world.getGameTime() - activateTime); }

    public int getBurnDuration() { return BURN_DURATION; }

    @Override
    public void writeNetworkNBT(CompoundNBT nbt)
    {
        nbt.putBoolean("active", active);
        nbt.putLong("time", activateTime);
        super.writeNetworkNBT(nbt);
    }

    @Override
    public void readNetworkNBT(CompoundNBT nbt)
    {
        super.readNetworkNBT(nbt);
        active = nbt.getBoolean("active");
        activateTime = nbt.getLong("time");
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        nbt.putBoolean("active", active);
        nbt.putLong("time", activateTime);
        return super.write(nbt);
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        super.read(nbt);
        active = nbt.getBoolean("active");
        activateTime = nbt.getLong("time");
    }
}