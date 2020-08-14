package xfacthd.r6mod.api.interaction;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IActivatable
{
    void activate(World world, BlockPos pos, String object, PlayerEntity player);

    String getObjectName();
}