package xfacthd.r6mod.client.gui.overlay.camera;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.client.util.render.TextureDrawer;
import xfacthd.r6mod.client.util.render.UIRenderHelper;
import xfacthd.r6mod.common.entities.camera.EntityYokaiDrone;

import java.util.Random;

public class OverlayYokaiDrone extends AbstractCameraOverlay<EntityYokaiDrone>
{
    private static final ResourceLocation BACKGROUND = new ResourceLocation(R6Mod.MODID, "textures/gui/overlay/yokai_filter.png");
    private static final ResourceLocation FOREGROUND_FLOORED = new ResourceLocation(R6Mod.MODID, "textures/gui/overlay/yokai_overlay_floored.png");
    private static final ResourceLocation FOREGROUND_CEILING = new ResourceLocation(R6Mod.MODID, "textures/gui/overlay/yokai_overlay_stationary.png");
    private static final ResourceLocation JAMMED = new ResourceLocation(R6Mod.MODID, "textures/gui/overlay/jammer_filter.png");
    private static final Random RAND = new Random(System.currentTimeMillis());

    private static final double BG_TEX_W = 16; //Background texture width
    private static final double BG_TEX_H = 16; //Background texture height
    private static final double FG_F_TEX_W = 128; //Foreground floored texture width
    private static final double FG_F_TEX_H = 128; //Foreground floored texture height
    private static final double FG_C_TEX_W = 128; //Foreground celing texture width
    private static final double FG_C_TEX_H = 128; //Foreground celing texture height

    @Override
    public void drawOverlay(EntityYokaiDrone camera)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        int w = window().getScaledWidth();
        int h = window().getScaledHeight();

        if (camera.isJammed()) { drawJammed(w, h); }
        else
        {
            drawBackground(w, h);
            drawForeground(w, h, camera.isOnCeiling());
            drawAmmoInfos(w, h, camera.getAmmo(), camera.getReloadStatus(mc().getRenderPartialTicks()));
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

    private void drawBackground(int w, int h)
    {
        mc().getTextureManager().bindTexture(BACKGROUND);
        double hw = w / 2D;
        double hh = h / 2D;

        //Start drawing
        TextureDrawer.start();

        for (double xOff = 0; hw - xOff > 0; xOff += BG_TEX_W)
        {
            for (double yOff = 0; hh - yOff > 0; yOff += BG_TEX_H)
            {
                //Draw top left texture
                double x = hw - xOff - BG_TEX_W - .5D;
                double y = hh - yOff - BG_TEX_H;
                TextureDrawer.fillBuffer(x, y, BG_TEX_W, BG_TEX_H, 0F, 1F, 0F, 1F);

                //Draw top right texture
                x = hw + xOff - .5D;
                y = hh - yOff - BG_TEX_H;
                TextureDrawer.fillBuffer(x, y, BG_TEX_W, BG_TEX_H, 0F, 1F, 0F, 1F);

                //Draw bottom left texture
                x = hw - xOff - BG_TEX_W - .5D;
                y = hh + yOff;
                TextureDrawer.fillBuffer(x, y, BG_TEX_W, BG_TEX_H, 0F, 1F, 0F, 1F);

                //Draw bottom right texture
                x = hw + xOff - .5D;
                y = hh + yOff;// + .5D;
                TextureDrawer.fillBuffer(x, y, BG_TEX_W, BG_TEX_H, 0F, 1F, 0F, 1F);
            }
        }

        //Finish drawing
        TextureDrawer.end();
    }

    private void drawForeground(int w, int h, boolean onCeiling)
    {
        if (onCeiling)
        {
            mc().getTextureManager().bindTexture(FOREGROUND_CEILING);

            double x = (w / 2D) - (FG_C_TEX_W / 2D) - .5D;
            double y = (h / 2D) - (FG_C_TEX_H / 2D);
            TextureDrawer.drawTexture(x, y, FG_C_TEX_W, FG_C_TEX_H, 0F, 1F, 0F, 1F);
        }
        else
        {
            mc().getTextureManager().bindTexture(FOREGROUND_FLOORED);

            double x = (w / 2D) - (FG_F_TEX_W / 2D) - .5D;
            double y = (h / 2D) - (FG_F_TEX_H / 2D);
            TextureDrawer.drawTexture(x, y, FG_F_TEX_W, FG_F_TEX_H, 0F, 1F, 0F, 1F);
        }
    }

    private void drawAmmoInfos(int w, int h, int ammo, float reloadStatus)
    {
        //Draw ammo
        String text = ammo + "/" + EntityYokaiDrone.MAX_AMMO;
        int color = ammo == 0 ? 0xFFFF0000 : 0xFFFFFFFF;
        font().drawStringWithShadow(text, w - 20 - font().getStringWidth(text), h - 15, color);

        //Draw reload indicator
        if (reloadStatus > 0F)
        {
            UIRenderHelper.drawCircle(w - 10, h - 11, 3, 0x808080FF, 1F, 100);
            UIRenderHelper.drawCircle(w - 10, h - 11, 3, 0xFFFFFFFF, reloadStatus, 100);
        }
    }
}