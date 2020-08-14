package xfacthd.r6mod.client.gui.overlay.camera;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.client.util.render.TextureDrawer;
import xfacthd.r6mod.client.util.render.UIRenderHelper;
import xfacthd.r6mod.common.entities.camera.EntityEvilEyeCamera;

import java.util.Random;

public class OverlayEvilEye extends AbstractCameraOverlay<EntityEvilEyeCamera>
{
    private static final ResourceLocation JAMMED = new ResourceLocation(R6Mod.MODID, "textures/gui/overlay/jammer_filter.png");
    private static final Random RAND = new Random(System.currentTimeMillis());

    @Override
    public void drawOverlay(EntityEvilEyeCamera camera)
    {
        int w = window().getScaledWidth();
        int h = window().getScaledHeight();

        int xCenter = w / 2;
        int yCenter = h / 2;

        //Get Tesselator and Draw Buffer
        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder buffer = tessellator.getBuffer();

        //Setup GL state
        GlStateManager.disableTexture();
        GlStateManager.depthMask(false);

        float door = camera.getDoorState();
        boolean overheat = camera.isOverheated();

        if (camera.isJammed())
        {
            drawJammed(w, h);
        }
        else if (door == 0F || overheat)
        {
            float ammo = camera.getAmmo();

            drawCrosshair(buffer, tessellator, xCenter, yCenter, overheat);
            drawStaticArc(xCenter, yCenter);
            drawAmmoArc(xCenter, yCenter, overheat, ammo);
        }
        else
        {
            drawDoorState(buffer, tessellator, xCenter, yCenter, door);
        }

        //Reset GL state
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture();
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

    private void drawDoorState(BufferBuilder buffer, Tessellator tessellator, int xCenter, int yCenter, float door)
    {
        float lineWidth = UIRenderHelper.getLineWidthScaled(4F);
        GL11.glLineWidth(lineWidth);

        double xOff = 5.0 * (1.0 - door);

        double lineLength = 6;
        double xLeft = xCenter - 4 - xOff - lineLength;
        double xRight = xCenter + 4 + xOff;
        double yTop = yCenter - (lineLength / 2);
        double yBottom = yCenter + (lineLength / 2);
        double off = lineWidth * 0.1F;

        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        UIRenderHelper.drawLine(buffer, xLeft - off, yTop, xLeft + lineLength, yTop, 255, 255, 255, 255);
        UIRenderHelper.drawLine(buffer, xLeft, yTop + off, xLeft, yBottom - off, 255, 255, 255, 255);
        UIRenderHelper.drawLine(buffer, xLeft - off, yBottom, xLeft + lineLength, yBottom, 255, 255, 255, 255);

        UIRenderHelper.drawLine(buffer, xRight, yTop, xRight + lineLength + off, yTop, 255, 255, 255, 255);
        UIRenderHelper.drawLine(buffer, xRight + lineLength, yTop + off, xRight + lineLength, yBottom - off, 255, 255, 255, 255);
        UIRenderHelper.drawLine(buffer, xRight, yBottom, xRight + lineLength + off, yBottom, 255, 255, 255, 255);

        //Draw
        tessellator.draw();
    }

    private void drawCrosshair(BufferBuilder buffer, Tessellator tessellator, int xCenter, int yCenter, boolean overheated)
    {
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        float lineWidth = UIRenderHelper.getLineWidthScaled(4F);
        GL11.glLineWidth(lineWidth);
        if (overheated) //Draw warning sign
        {
            UIRenderHelper.drawLine(buffer, xCenter - 7, yCenter + 4, xCenter + 7, yCenter + 4, 255, 0, 0, 255);
            UIRenderHelper.drawLine(buffer, xCenter, yCenter - 6, xCenter + 7, yCenter + 4, 255, 0, 0, 255);
            UIRenderHelper.drawLine(buffer, xCenter - 7, yCenter + 4, xCenter, yCenter - 6, 255, 0, 0, 255);

            UIRenderHelper.drawLine(buffer, xCenter, yCenter - 3.5, xCenter, yCenter + 0.5, 255, 0, 0, 255);
            UIRenderHelper.drawLine(buffer, xCenter, yCenter + 1.5, xCenter, yCenter + 2.5, 255, 0, 0, 255);
        }
        else //Draw crosshair
        {
            double lineLenHalf = 5;
            UIRenderHelper.drawLine(buffer, xCenter - lineLenHalf, yCenter, xCenter + lineLenHalf, yCenter, 255, 255, 255, 255);
            UIRenderHelper.drawLine(buffer, xCenter, yCenter - lineLenHalf, xCenter, yCenter + lineLenHalf, 255, 255, 255, 255);

            lineWidth = UIRenderHelper.getLineWidthScaled(2F);
            GL11.glLineWidth(lineWidth);

            UIRenderHelper.drawLine(buffer, xCenter - lineLenHalf, yCenter - lineLenHalf, xCenter + lineLenHalf, yCenter + lineLenHalf, 255, 255, 255, 255);
            UIRenderHelper.drawLine(buffer, xCenter - lineLenHalf, yCenter + lineLenHalf, xCenter + lineLenHalf, yCenter - lineLenHalf, 255, 255, 255, 255);
        }

        //Draw
        tessellator.draw();
    }

    private void drawStaticArc(int xCenter, int yCenter)
    {
        float lineWidth = UIRenderHelper.getLineWidthScaled(2F);
        GL11.glLineWidth(lineWidth);
        UIRenderHelper.drawArc(xCenter, yCenter, 40, 0, 180, 20, 255, 255, 255, 255);
    }

    private void drawAmmoArc(int xCenter, int yCenter, boolean overheated, float ammo)
    {
        float startAngle = 180;
        float arcAngle;

        if (overheated)
        {
            arcAngle = -180 * (1F - ammo);
        }
        else
        {
            startAngle -= 180F * (1F - ammo);
            arcAngle = -180F * ammo;
        }

        int g = (overheated || ammo < 0.4F) ? 0 : 255;
        int b = (overheated || ammo < 0.4F) ? 0 : 255;

        float lineWidth = UIRenderHelper.getLineWidthScaled(8F);
        GL11.glLineWidth(lineWidth);
        int segments = (int)(40F * (overheated ? 1F - ammo : ammo));
        UIRenderHelper.drawArc(xCenter, yCenter, 42, startAngle, arcAngle, segments, 255, g, b, 255);
    }
}