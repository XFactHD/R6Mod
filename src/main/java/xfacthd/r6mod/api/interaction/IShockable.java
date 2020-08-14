package xfacthd.r6mod.api.interaction;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;

public interface IShockable
{
    void shock(PlayerEntity shooter, Vector3d hitVec);
}