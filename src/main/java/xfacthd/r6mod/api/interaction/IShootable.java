package xfacthd.r6mod.api.interaction;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public interface IShootable
{
    void shoot(PlayerEntity shooter, Vec3d hitVec);
}