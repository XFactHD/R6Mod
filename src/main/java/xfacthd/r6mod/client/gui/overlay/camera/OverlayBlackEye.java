package xfacthd.r6mod.client.gui.overlay.camera;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.ResourceLocation;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.api.entity.ICameraEntity;
import xfacthd.r6mod.client.util.render.TextureDrawer;

public class OverlayBlackEye extends AbstractCameraOverlay //TODO: implement properly
{
    private static final ResourceLocation BACKGROUND = new ResourceLocation(R6Mod.MODID, "textures/gui/overlay/black_eye_filter.png");

    @Override
    public void drawOverlay(ICameraEntity camera, MatrixStack matrix)
    {
        int w = window().getScaledWidth();
        int h = window().getScaledHeight();

        mc().getTextureManager().bindTexture(BACKGROUND);
        TextureDrawer.drawTexture(matrix, 0, 0, w, h, 0, 1, 0, 1);
    }
}