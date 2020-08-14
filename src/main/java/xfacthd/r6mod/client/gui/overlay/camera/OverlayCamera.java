package xfacthd.r6mod.client.gui.overlay.camera;

import com.mojang.blaze3d.systems.RenderSystem;
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
    public void drawOverlay(EntityCamera camera)
    {
        int w = window().getScaledWidth();
        int h = window().getScaledHeight();

        if (camera.isDestroyed()) { drawDestroyed(w, h); }
        else if (camera.isJammed()) { drawJammed(w, h); }

        //TODO: draw foreground
    }

    private void drawDestroyed(int w, int h)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        Minecraft.getInstance().getTextureManager().bindTexture(NOSIGNAL);

        int y = (int)((float)(world().getGameTime() % h) + mc().getRenderPartialTicks());

        double qw = w / 4D;
        double hh = h / 2D;
        for (int xMul = 0; xMul < 4; xMul++)
        {
            for (int yMul = 0; yMul < 4; yMul++)
            {
                double x = qw * (double)xMul;
                double yActual = y + hh * (double)yMul - h;

                TextureDrawer.drawTexture(x, yActual, qw, hh, 0F, 1F, 0F, 1F);
                TextureDrawer.drawTexture(x, yActual, qw, hh, 0F, 1F, 0F, 1F);
            }
        }
    }

    private void drawJammed(int w, int h)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 0.75F);

        Minecraft.getInstance().getTextureManager().bindTexture(JAMMED);

        double qw = w / 4D;
        double hh = h / 2D;
        for (int xMul = 0; xMul < 4; xMul++)
        {
            for (int yMul = 0; yMul < 4; yMul++)
            {
                double x = qw * (double)xMul;
                double y = hh * (double)yMul;

                float uOff = .5F * (RAND.nextInt(32) / 32F);
                float vOff = .5F * (RAND.nextInt(32) / 32F);
                TextureDrawer.drawTexture(x, y, qw, hh, 0F + uOff, .5F + uOff, 0F + vOff, .5F + vOff);
                TextureDrawer.drawTexture(x, y, qw, hh, 0F + uOff, .5F + uOff, 0F + vOff, .5F + vOff);
            }
        }
    }
}