package xfacthd.r6mod.common.entities.grenade;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import xfacthd.r6mod.api.interaction.IShootable;
import xfacthd.r6mod.common.blocks.building.*;
import xfacthd.r6mod.common.data.PropertyHolder;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.data.types.EntityTypes;
import xfacthd.r6mod.common.items.gadgets.ItemCandela;
import xfacthd.r6mod.common.util.R6SoundEvents;
import xfacthd.r6mod.common.util.RayTraceHelper;
import xfacthd.r6mod.common.util.data.PointManager;

public class EntityCandelaGrenade extends AbstractEntityGrenade implements IEntityAdditionalSpawnData, IShootable
{
    private static final long FLASHBANG_INTERVAL = 4;
    private static final int MAX_FLASH_COUNT = 7;
    private static final Vector3d VEC_UP = new Vector3d(0, 1, 0);

    private boolean onBlock = false;
    private Vector3d spawnOffset = null;
    private int timerOffset = 0;
    private boolean spawningFlashbangs = false;
    private int flashbangsSpawned = 0;
    private long lastFlashbang = 0;

    public EntityCandelaGrenade(World world) { super(EntityTypes.entityTypeCandelaGrenade, world); }

    public EntityCandelaGrenade(World world, PlayerEntity thrower, String team, int timerOffset)
    {
        super(EntityTypes.entityTypeCandelaGrenade, world, thrower, team);

        if (thrower.isSneaking())
        {
            float reach = getPlayerReach(thrower);
            RayTraceResult result = thrower.pick(reach, 0, false);

            if (result.getType() == RayTraceResult.Type.BLOCK)
            {
                BlockRayTraceResult blockResult = (BlockRayTraceResult)result;
                onBlock = isValidBlock(world, blockResult.getPos(), blockResult.getFace());

                if (onBlock)
                {
                    startTimer(timerOffset);

                    Vector3d hit = result.getHitVec();
                    Direction side = blockResult.getFace();
                    float yaw = side != Direction.UP ? side.getHorizontalAngle() : 0;
                    float pitch = side == Direction.UP ? -90 : 0;
                    double y = side == Direction.UP ? hit.getY() - (getHeight() / 2D) : hit.getY();
                    setPositionAndRotation(hit.getX(), y, hit.getZ(), yaw, pitch);

                    Vector3d dir = Vector3d.copy(side.getOpposite().getDirectionVec());
                    //Reverse raytrace from opposite side
                    BlockRayTraceResult oppResult = RayTraceHelper.raytraceRangeDetailed(world, thrower, hit.add(dir), hit);
                    spawnOffset = oppResult.getHitVec().subtract(hit);
                }
            }
        }

        if (!onBlock) { this.timerOffset = timerOffset; }
    }

    private float getPlayerReach(PlayerEntity player)
    {
        //noinspection ConstantConditions
        float reach = (float)player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue();
        return player.isCreative() ? reach : reach - 0.5F;
    }

    private boolean isValidBlock(World world, BlockPos pos, Direction side)
    {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof BlockWall)
        {
            Direction facing = state.get(PropertyHolder.FACING_NE);
            return side == facing || side == facing.getOpposite();
        }
        else if (state.getBlock() instanceof BlockBarricade)
        {
            Direction facing = state.get(PropertyHolder.FACING_HOR);
            return side == facing || side == facing.getOpposite();
        }
        else if (state.getBlock() instanceof BlockFloorPanel || state.getBlock() instanceof BlockDropHatch)
        {
            return side == Direction.UP;
        }
        return false;
    }

    @Override
    public void tick()
    {
        super.tick();

        tickFlashbangSpawns();
    }

    @Override
    protected void firstTick()
    {
        super.firstTick();

        if (!world.isRemote() && onBlock)
        {
            SoundEvent event = R6SoundEvents.getGadgetSound(EnumGadget.CANDELA_GRENADE, "place");
            world.playSound(null, getPosX(), getPosY(), getPosZ(), event, SoundCategory.BLOCKS, 1, 1);
        }
    }

    @Override
    protected void tickMovement()
    {
        if (!onBlock)
        {
            super.tickMovement();
        }
    }

    private void tickFlashbangSpawns()
    {
        if (!spawningFlashbangs) { return; }

        if (!world.isRemote() && world.getGameTime() - lastFlashbang >= FLASHBANG_INTERVAL)
        {
            lastFlashbang = world.getGameTime();
            if (flashbangsSpawned >= MAX_FLASH_COUNT)
            {
                spawningFlashbangs = false;

                remove();
                return;
            }

            Vector3d pos = getPositionVec();
            Vector3d motion; //TODO: calculate deviation from straight vector

            if (onBlock)
            {
                pos = getPositionVec().add(spawnOffset);
                motion = spawnOffset.normalize();
            }
            else
            {
                motion = VEC_UP;
            }

            EntityCandelaFlash flash = new EntityCandelaFlash(this, pos, motion, flashbangsSpawned == (MAX_FLASH_COUNT - 1));
            world.addEntity(flash);

            world.playSound(null, getPosX(), getPosY(), getPosZ(), R6SoundEvents.getGadgetSound(EnumGadget.CANDELA_GRENADE, "fire"), SoundCategory.BLOCKS, 1, 1);

            flashbangsSpawned++;
        }
    }

    @Override
    protected void onImpact()
    {
        if (!onBlock)
        {
            startTimer(timerOffset);

            Vector3d motion = getMotion();
            motion = motion.normalize().scale(.75);
            setMotion(motion);
        }
    }

    @Override
    protected void onTimerExpired()
    {
        if (flashbangsSpawned == 0)
        {
            spawningFlashbangs = true;
            setMotion(Vector3d.ZERO);
            PointManager.awardGadgetUse(EnumGadget.CANDELA_GRENADE, getThrowerEntity(), 10);
        }
    }

    @Override
    protected long getFuseLength() { return ItemCandela.FUSE_TIME; }

    @Override
    public void neutralize() { onTimerExpired(); }

    @Override
    protected boolean hasDrag() { return !onGround; }

    @Override
    protected boolean shouldDeflectHor() { return false; }

    public boolean isOnBlock() { return onBlock; }

    @Override
    protected ItemStack getRenderStack() { return ItemStack.EMPTY; }

    @Override
    public void shoot(PlayerEntity shooter, Vector3d hitVec)
    {
        remove();
        PointManager.awardGadgetDestroyed(EnumGadget.CANDELA_GRENADE, shooter, getTeamName());
    }

    @Override
    public boolean canBeCollidedWith() { return true; }

    @Override
    protected void readAdditional(CompoundNBT nbt)
    {
        super.readAdditional(nbt);

        onBlock = nbt.getBoolean("onBlock");
        if (onBlock)
        {
            double offX = nbt.getDouble("offX");
            double offY = nbt.getDouble("offY");
            double offZ = nbt.getDouble("offZ");
            spawnOffset = new Vector3d(offX, offY, offZ);
        }
    }

    @Override
    protected void writeAdditional(CompoundNBT nbt)
    {
        super.writeAdditional(nbt);

        nbt.putBoolean("onBlock", onBlock);
        if (onBlock)
        {
            nbt.putDouble("offX", spawnOffset.getX());
            nbt.putDouble("offY", spawnOffset.getY());
            nbt.putDouble("offZ", spawnOffset.getZ());
        }
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) { buffer.writeBoolean(onBlock); }

    @Override
    public void readSpawnData(PacketBuffer buffer) { onBlock = buffer.readBoolean(); }
}