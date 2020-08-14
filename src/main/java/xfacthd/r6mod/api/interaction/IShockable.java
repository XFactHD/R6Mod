package xfacthd.r6mod.api.interaction;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public interface IShockable
{
    void shock(PlayerEntity shooter, Vec3d hitVec);
}