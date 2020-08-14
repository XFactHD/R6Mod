package xfacthd.r6mod.client.gui.overlay.camera;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.client.util.render.TextureDrawer;
import xfacthd.r6mod.common.entities.camera.EntityCamera;

import java.util.Random;

public class OverlayCamera extends AbstractCameraOverlay<EntityCamera>
{
    private static final ResourceLocation NOSIGNAL = new ResourceLocation(R6Mod.MODID, "textures/gui/overlay/nosignal_filter.png");
    private static final ResourceLocation JAMMED = new ResourceLocation(R6Mod.MODID, "textures/gui/overlay/jammer_filter.png");
    private static final Random RAND = new Random(System.currentTimeMillis());

    @Override
    public void drawOverlay(EntityCamera camera, MatrixStack matrix)
    {
        int w = window().getScaledWidth();
        int h = window().getScaledHeight();

        if (camera.isDestroyed()) { drawDestroyed(matrix, w, h); }
        else if (camera.isJammed()) { drawJammed(matrix, w, h); }

        //TODO: draw foreground
    }

    private void drawDestroyed(MatrixStack matrix, int w, int h)
    {
        Minecraft.getInstance().getTextureManager().bindTexture(NOSIGNAL);

        int y = (int)((float)(world().getGameTime() % h) + mc().getRenderPartialTicks());

        float qw = w / 4F;
        float hh = h / 2F;
        for (int xMul = 0; xMul < 4; xMul++)
        {
            for (int yMul = 0; yMul < 4; yMul++)
            {
                float x = qw * (float) xMul;
                float yActual = y + hh * (float) yMul - h;

                TextureDrawer.drawTexture(matrix, x, yActual, qw, hh, 0F, 1F, 0F, 1F);
                TextureDrawer.drawTexture(matrix, x, yActual, qw, hh, 0F, 1F, 0F, 1F);
            }
        }
    }

    private void drawJammed(MatrixStack matrix, int w, int h)
    {
        Minecraft.getInstance().getTextureManager().bindTexture(JAMMED);

        float qw = w / 4F;
        float hh = h / 2F;
        for (int xMul = 0; xMul < 4; xMul++)
        {
            for (int yMul = 0; yMul < 4; yMul++)
            {
                float x = qw * (float) xMul;
                float y = hh * (float) yMul;

                float uOff = .5F * (RAND.nextInt(32) / 32F);
                float vOff = .5F * (RAND.nextInt(32) / 32F);
                TextureDrawer.drawTexture(matrix, x, y, qw, hh, 0F + uOff, .5F + uOff, 0F + vOff, .5F + vOff, 0xFFFFFFBF);
                TextureDrawer.drawTexture(matrix, x, y, qw, hh, 0F + uOff, .5F + uOff, 0F + vOff, .5F + vOff, 0xFFFFFFBF);
            }
        }
    }
}