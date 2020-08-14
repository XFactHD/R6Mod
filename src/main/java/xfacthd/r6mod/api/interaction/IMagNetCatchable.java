package xfacthd.r6mod.api.interaction;

import net.minecraft.util.math.Vec3d;

public interface IMagNetCatchable
{
    /**
     * Called when the projectile enters the radius of an active MagNet
     * @param magPos The position of the MagNet that caught the projectile, used for steering the projectile
     */
    void catchObject(Vec3d magPos);

    void neutralize();
}