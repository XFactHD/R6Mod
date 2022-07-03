package xfacthd.r6mod.api.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import xfacthd.r6mod.common.data.HookableType;

public interface IHookable
{
    boolean canHook(World world, BlockPos pos, BlockState state, Direction side);

    Vector3d getHookTarget(World world, BlockPos pos, BlockState state, Direction side);

    //Called when the hook impacts the block before reeling the player in
    void onHookImpact(World world, BlockPos pos, BlockState state);

    //Called when the player impacts the block after being reeled in
    void onPlayerImpact(World world, BlockPos pos, BlockState state);

    HookableType getHookableType();
}