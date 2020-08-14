package xfacthd.r6mod.api.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import xfacthd.r6mod.api.entity.ICameraEntity;

public interface ICameraOverlay<T extends ICameraEntity>
{
    void drawOverlay(T camera, MatrixStack matrix);

    default boolean hideCrosshair() { return false; }
}