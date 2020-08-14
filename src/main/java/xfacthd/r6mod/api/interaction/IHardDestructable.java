package xfacthd.r6mod.api.interaction;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;

public interface IHardDestructable
{
    void destroy(World world, BlockPos pos, BlockState state, PlayerEntity player, EnumGadget source, Direction side);

    default boolean isSideSolid(World world, BlockState state, BlockPos pos, Direction side) { return false; }
}