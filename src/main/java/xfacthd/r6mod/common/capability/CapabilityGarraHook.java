package xfacthd.r6mod.common.capability;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import xfacthd.r6mod.api.block.IHookable;
import xfacthd.r6mod.api.capability.ICapabilityGarraHook;
import xfacthd.r6mod.common.data.GarraHookState;
import xfacthd.r6mod.common.data.HookableType;
import xfacthd.r6mod.common.util.HitData;
import xfacthd.r6mod.common.util.RayTraceHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilityGarraHook implements ICapabilityGarraHook
{
    @CapabilityInject(ICapabilityGarraHook.class) //TODO: make final again when done, breaks reloads
    public static Capability<ICapabilityGarraHook> HOOK_CAPABILITY = null;

    private static final int MAX_RANGE = 15;
    private static final int USE_COUNT = 4;
    private static final int COOLDOWN = 160;
    private static final double HOOK_SPEED = 1D; //RESEARCH: find proper value
    private static final double FLIGHT_SPEED = 1D / .375;

    private final ItemStack stack;
    private int uses = USE_COUNT;
    private long cooldownStamp = 0;
    private long launchStamp = 0;
    private int flightTime = 0;
    private GarraHookState hookState = GarraHookState.IDLE;
    private IHookable hookable = null;
    private BlockPos hookablePos = null;
    private Vector3d target = null;
    private HookableType targetType = null;
    private Vector3d motion = null;

    private CapabilityGarraHook(ItemStack stack) { this.stack = stack; }

    public ActionResult<ItemStack> handleRightClick(World world, PlayerEntity player)
    {
        if (hookState == GarraHookState.HOOKED) //TODO: remove after testing
        {
            player.setMotion(Vector3d.ZERO);
            player.velocityChanged = true;

            hookState = GarraHookState.IDLE;
            cooldownStamp = world.getGameTime();

            player.setNoGravity(false);
            player.sendStatusMessage(new StringTextComponent("Cancelled"), true);

            return ActionResult.resultConsume(stack);
        }

        if (hookState != GarraHookState.IDLE || uses == 0 || (world.getGameTime() - cooldownStamp) < COOLDOWN)
        {
            if (uses == 0)
            {
                player.sendStatusMessage(new StringTextComponent("Empty"), true);
            }
            else if ((world.getGameTime() - cooldownStamp) < COOLDOWN)
            {
                player.sendStatusMessage(new StringTextComponent("Cooldown"), true);
            }
            return ActionResult.resultFail(stack);
        }

        HitData hit = RayTraceHelper.raytraceGarraHook(world, player, MAX_RANGE);
        if (hit == null) { return ActionResult.resultFail(stack); }

        BlockPos pos = hit.getPos();
        Direction face = hit.getFace();

        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof IHookable)
        {
            IHookable hookable = (IHookable) state.getBlock();
            if (hookable.canHook(world, pos, state, face))
            {
                if (!world.isRemote())
                {
                    this.hookable = hookable;
                    hookablePos = pos;
                    target = hookable.getHookTarget(world, pos, state, face);
                    targetType = hookable.getHookableType();
                    hookState = GarraHookState.LAUNCHED;

                    double distance = hit.getHitVec().distanceTo(player.getPositionVec());
                    flightTime = (int)(distance * HOOK_SPEED);

                    uses--;
                    launchStamp = world.getGameTime();

                    player.sendStatusMessage(new StringTextComponent("Launched"), true);
                }
                return ActionResult.func_233538_a_(stack, world.isRemote());
            }
        }

        return ActionResult.resultFail(stack);
    }

    @Override
    public void tick(World world, PlayerEntity player, ItemStack stack, boolean selected)
    {
        if (!world.isRemote())
        {
            if (hookState == GarraHookState.LAUNCHED)
            {
                if (world.getGameTime() - launchStamp >= flightTime)
                {
                    hookState = GarraHookState.HOOKED;
                    player.setNoGravity(true);

                    Vector3d playerPos = targetType.playerPos(player);
                    motion = target.subtract(playerPos).normalize().mul(.75, .75, .75);//.mul(FLIGHT_SPEED, FLIGHT_SPEED, FLIGHT_SPEED);

                    player.sendStatusMessage(new StringTextComponent("Hooked"), true);

                    hookable.onHookImpact(world, hookablePos, world.getBlockState(hookablePos));
                }
            }
            else if (hookState == GarraHookState.HOOKED)
            {
                Vector3d pos = targetType.playerPos(player);

                boolean changed = false;
                double mx = motion.getX();
                double my = motion.getY();
                double mz = motion.getZ();
                double dx = target.getX() - pos.getX();
                double dy = target.getY() - pos.getY();
                double dz = target.getZ() - pos.getZ();

                if (Math.abs(mx) > Math.abs(dx))
                {
                    mx = dx < .001 ? 0 : dx;
                    changed = true;
                }
                if (Math.abs(my) > Math.abs(dy))
                {
                    my = dy < .001 ? 0 : dy;
                    changed = true;
                }
                if (Math.abs(mz) > Math.abs(dz))
                {
                    mz = dz < .001 ? 0 : dz;
                    changed = true;
                }
                if (changed) { motion = new Vector3d(mx, my, mz); }

                player.sendStatusMessage(new StringTextComponent(motion.toString()), true);

                player.setMotion(motion);
                player.velocityChanged = true;

                if (pos.isWithinDistanceOf(target, .5))
                {
                    hookState = GarraHookState.IDLE;
                    cooldownStamp = world.getGameTime();

                    player.setNoGravity(false);

                    player.sendStatusMessage(new StringTextComponent("Reached"), true);

                    hookable.onPlayerImpact(world, hookablePos, world.getBlockState(hookablePos));
                }
            }
        }
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("state", hookState.ordinal());
        nbt.putInt("uses", uses);
        nbt.putBoolean("hasTarget", target != null);
        if (target != null)
        {
            nbt.putDouble("targetX", target.getX());
            nbt.putDouble("targetY", target.getY());
            nbt.putDouble("targetZ", target.getZ());
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        hookState = GarraHookState.values()[nbt.getInt("state")];
        uses = nbt.getInt("uses");
        target = nbt.getBoolean("hasTarget") ? new Vector3d(
                nbt.getDouble("targetX"),
                nbt.getDouble("targetY"),
                nbt.getDouble("targetZ")
        ) : null;
    }

    public static ICapabilityGarraHook get(ItemStack stack)
    {
        //noinspection ConstantConditions
        return stack.getCapability(HOOK_CAPABILITY).orElseThrow(() ->
                new IllegalStateException("CapabilityGarraHook not present or requested from invalid item!")
        );
    }

    public static final class Empty implements ICapabilityGarraHook
    {
        @Override
        public ActionResult<ItemStack> handleRightClick(World world, PlayerEntity player) { return null; }

        @Override
        public void tick(World world, PlayerEntity player, ItemStack stack, boolean selected) { }

        @Override
        public CompoundNBT serializeNBT()
        {
            return null;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) { }
    }

    public static final class Provider implements ICapabilityProvider, INBTSerializable<CompoundNBT>
    {
        private final ICapabilityGarraHook instance;

        public Provider(ItemStack stack) { instance = new CapabilityGarraHook(stack); }

        @Nonnull
        @Override
        @SuppressWarnings({"ConstantConditions", "unchecked"})
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
        {
            //Make sure to only return an instance if the capability is actually registered
            if (HOOK_CAPABILITY == null) { return LazyOptional.empty(); }

            if (cap != HOOK_CAPABILITY) { return LazyOptional.empty(); }
            return LazyOptional.of(() -> (T)instance);
        }

        @Override
        public CompoundNBT serializeNBT() { return instance.serializeNBT(); }

        @Override
        public void deserializeNBT(CompoundNBT nbt) { instance.deserializeNBT(nbt); }
    }

    public static final class Storage implements Capability.IStorage<ICapabilityGarraHook>
    {
        @Nullable
        @Override
        public INBT writeNBT(Capability<ICapabilityGarraHook> capability, ICapabilityGarraHook instance, Direction side)
        {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<ICapabilityGarraHook> capability, ICapabilityGarraHook instance, Direction side, INBT nbt)
        {
            instance.deserializeNBT((CompoundNBT) nbt);
        }
    }
}