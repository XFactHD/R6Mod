package xfacthd.r6mod.api.client;

import xfacthd.r6mod.api.entity.ICameraEntity;

public interface ICameraOverlay<T extends ICameraEntity>
{
    void drawOverlay(T camera);

    default boolean hideCrosshair() { return false; }
}