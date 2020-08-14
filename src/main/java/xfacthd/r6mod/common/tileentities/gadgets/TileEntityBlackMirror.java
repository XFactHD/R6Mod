package xfacthd.r6mod.common.tileentities.gadgets;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import xfacthd.r6mod.common.data.PropertyHolder;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.data.types.TileEntityTypes;
import xfacthd.r6mod.common.tileentities.TileEntityGadget;
import xfacthd.r6mod.common.util.R6SoundEvents;
import xfacthd.r6mod.common.util.data.PointManager;

public class TileEntityBlackMirror extends TileEntityGadget
{
    private static final long GLASS_DELAY = 10;

    private boolean waitingGlassFall = false;
    private long destroyTime = 0;

    public TileEntityBlackMirror() { super(TileEntityTypes.tileTypeBlackMirror, EnumGadget.BLACK_MIRROR); }

    @Override
    public void tick()
    {
        super.tick();

        //noinspection ConstantConditions
        if (!world.isRemote() && waitingGlassFall)
        {
            if (world.getGameTime() - destroyTime > GLASS_DELAY)
            {
                waitingGlassFall = false;
                world.setBlockState(pos, getBlockState().with(PropertyHolder.OPEN, true));

                if (!getBlockState().get(PropertyHolder.RIGHT)) //Only play the sound once
                {
                    Direction facing = getBlockState().get(PropertyHolder.FACING_HOR);
                    Vector3d soundPos = getFrontCenteredSound(pos, facing);
                    SoundEvent event = R6SoundEvents.getGadgetSound(EnumGadget.BLACK_MIRROR, "shatter");
                    world.playSound(null, soundPos.getX(), soundPos.getY(), soundPos.getZ(), event, SoundCategory.BLOCKS, 1F, 1F);
                }
            }
        }
    }

    @Override
    public void shoot(PlayerEntity shooter, Vector3d hitVec)
    {
        if (destroyCanister(hitVec))
        {
            PointManager.awardGadgetDestroyed(EnumGadget.BLACK_MIRROR, shooter);
        }
    }

    @Override
    public void shock(PlayerEntity shooter, Vector3d hitVec) { shoot(shooter, hitVec); }

    public boolean destroyCanister(Vector3d hitVec)
    {
        BlockState state = getBlockState();
        if (!state.get(PropertyHolder.DESTROYED))
        {
            Direction facing = state.get(PropertyHolder.FACING_HOR);
            boolean right = state.get(PropertyHolder.RIGHT);

            Vector3d subHit = hitVec.subtract((int)hitVec.getX(), pos.getY(), (int)hitVec.getZ());

            if (isCanisterHit(facing, right, subHit))
            {
                //noinspection ConstantConditions
                if (!world.isRemote())
                {
                    Direction offset = right ? facing.rotateYCCW() : facing.rotateY();

                    BlockPos adjPos = pos.offset(offset);

                    world.setBlockState(pos, state.with(PropertyHolder.DESTROYED, true));
                    world.setBlockState(adjPos, world.getBlockState(adjPos).with(PropertyHolder.DESTROYED, true));

                    Vector3d soundPos = getFrontCenteredSound(!right ? pos : pos.offset(facing.rotateYCCW()), facing);
                    SoundEvent event = R6SoundEvents.getGadgetSound(EnumGadget.BLACK_MIRROR, "open");
                    world.playSound(null, soundPos.getX(), soundPos.getY(), soundPos.getZ(), event, SoundCategory.BLOCKS, 1F, 1F);

                    destroyTime = world.getGameTime();
                    waitingGlassFall = true;

                    TileEntity te = world.getTileEntity(adjPos);
                    if (te instanceof TileEntityBlackMirror)
                    {
                        ((TileEntityBlackMirror)te).destroyTime = destroyTime;
                        ((TileEntityBlackMirror)te).waitingGlassFall = waitingGlassFall;
                    }

                    double smokeX = pos.getX();
                    double smokeY = pos.getY() + 1D/16D;
                    double smokeZ = pos.getZ();
                    switch (facing)
                    {
                        case NORTH:
                        {
                            smokeX += right ? 0 : 1;
                            smokeZ += 17D/16D;
                            break;
                        }
                        case EAST:
                        {
                            smokeX += -1D/16D;
                            smokeZ += right ? 0 : 1;
                            break;
                        }
                        case SOUTH:
                        {
                            smokeX += right ? 1 : 0;
                            smokeZ += -1D/16D;
                            break;
                        }
                        case WEST:
                        {
                            smokeX += 17D/16D;
                            smokeZ += right ? 1 : 0;
                            break;
                        }
                    }
                    ((ServerWorld)world).spawnParticle(ParticleTypes.SMOKE, smokeX, smokeY, smokeZ, 1, 0, 0, 0, 0);
                }
                return true;
            }
        }
        return false;
    }

    private static boolean isCanisterHit(Direction facing, boolean right, Vector3d subHit)
    {
        if (subHit.getY() < .1D/16D || subHit.getY() > 2D/16D) { return false; }

        double x = Math.abs(subHit.getX());
        double z = Math.abs(subHit.getZ());

        if (facing == Direction.SOUTH)
        {
            return z <= .11D && (right ? x >= 0F && x <= 3F/16F : x >= 13F/16F && x <= 1F);
        }
        else if (facing == Direction.NORTH)
        {
            return z <= .11D && (right ? x >= 13F/16F && x <= 1F : x >= 0F && x <= 3F/16F);
        }
        else if (facing == Direction.WEST)
        {
            return x >= .89D && (right ? z >= 13F/16F && z <= 1 : z >= 0F && z <= 3F/16F);
        }
        else if (facing == Direction.EAST)
        {
            return x <= .11D && (right ? z >= 0F && z <= 3F/16F : z >= 13F/16F && z <= 1);
        }
        return false;
    }

    private static Vector3d getFrontCenteredSound(BlockPos pos, Direction facing)
    {
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        switch (facing)
        {
            case NORTH:
            {
                z++;
                x++;
                break;
            }
            case EAST:
            {
                z++;
                break;
            }
            case WEST:
            {
                x++;
                break;
            }
        }
        return new Vector3d(x, y, z);
    }
}