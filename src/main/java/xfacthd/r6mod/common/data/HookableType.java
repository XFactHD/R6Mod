package xfacthd.r6mod.common.data;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;

public enum HookableType
{
    HATCH,
    LEDGE,
    WINDOW;

    public Vector3d playerPos(PlayerEntity player)
    {
        switch (this)
        {
            case LEDGE:
            case HATCH:
            {
                return player.getPositionVec();
            }
            case WINDOW:
            {
                return player.getPositionVec().add(0, player.getHeight() / 2D + .1D, 0);
            }
        }
        throw new IllegalArgumentException("Invalid HookableType enum constant!");
    }
}