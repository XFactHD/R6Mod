package xfacthd.r6mod.client.util.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class UIRenderHelper
{
    public static void drawProgressBar(double x, double y, double frameW, double frameH, double barW, double barH, float progress, Color4i frameColor, Color4i barColor)
    {
        //Setup GL state
        GlStateManager.disableTexture();
        GlStateManager.depthMask(false);

        //Get Tesselator and Draw Buffer
        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder buffer = tessellator.getBuffer();

        //Draw outline
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        float lineWidth = getLineWidthScaled(4F);
        drawRect(buffer, x, y, x + frameW, y + frameH, lineWidth, frameColor.r(), frameColor.g(), frameColor.b(), frameColor.a());
        tessellator.draw();

        //Draw bar
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        double xOff = (frameW / 2.0) - (barW / 2.0);
        double yOff = (frameH / 2.0) - (barH / 2.0);
        double barLen = barW * progress;
        fillRect(buffer, x + xOff, y + yOff, x + xOff + barLen, y + yOff + barH, barColor.r(), barColor.g(), barColor.b(), barColor.a());
        tessellator.draw();

        //Reset GL state
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture();
    }

    /**
     * Draws a progress circle in a square shape
     * @param x The horizontal position
     * @param y The vertical position
     * @param size The resulting width and height on screen
     * @param texSize The width and height of the texture in pixels
     * @param edgeOff The offset between texture edge and the outer edge of the square in the texture
     * @param lineW The width of the square's outline
     * @param progress The progress to show (0F-1F)
     * @param color The color to tint the progress bar with
     * @param texture The texture to draw
     * @todo try to find a simpler way to draw this
     */
    public static void drawProgressCircle(double x, double y, double size, double texSize, double edgeOff, float lineW, float progress, Color4i color, ResourceLocation texture)
    {
        Minecraft.getInstance().getTextureManager().bindTexture(texture);

        double offset = edgeOff / texSize;
        double border = (edgeOff + lineW) / texSize;
        double circleSize = (texSize - (edgeOff * 2D)) / texSize;

        //Top right half
        float partProgress = Math.min(progress * 8F, 1F);
        double partX = x + (size * offset) + ((circleSize / 2D) * size);
        double partY = y;
        double partW = ((circleSize / 2D) * 12D) * partProgress;
        double partH = size * border;
        float minU = .5F;
        float maxU = .5F + (((float)circleSize / 2F) * partProgress);
        float minV = 0;
        float maxV = (float)border;
        TextureDrawer.drawTexture(partX, partY, partW, partH, minU, maxU, minV, maxV, color.packed());

        //Right
        if (progress > .125F)
        {
            partProgress = Math.min((progress - .125F) * 4F, 1F);
            partX = x + ((1F - border) * size);
            partY = y + (size * offset);
            partW = size * border;
            partH = (size * circleSize) * partProgress;
            minU = 1F - (float)border;
            maxU = 1F;
            minV = (float)offset;
            maxV = (float)(offset + circleSize * partProgress);
            TextureDrawer.drawTexture(partX, partY, partW, partH, minU, maxU, minV, maxV, color.packed());
        }

        //Bottom
        if (progress > .375F)
        {
            partProgress = Math.min((progress - .375F) * 4F, 1F);
            partX = x + (offset * size) + ((size * circleSize) * (1F - partProgress));
            partY = y + ((1F - border) * size);
            partW = (circleSize * size) * partProgress;
            partH = border * size;
            minU = (1F - (float)offset) - ((float)circleSize * partProgress);
            maxU = 1F - (float)offset;
            minV = 1F - (float)border;
            TextureDrawer.drawTexture(partX, partY, partW, partH, minU, maxU, minV, 1F, color.packed());
        }

        //Left
        if (progress > .625F)
        {
            partProgress = Math.min((progress - .625F) * 4F, 1F);
            partX = x + (offset * size);
            partY = y + (size * offset) + ((size * circleSize) * (1F - partProgress));
            partW = size * (border - offset);
            partH = (size * circleSize) * partProgress;
            minV = (1F - (float)offset) - ((float)circleSize * partProgress);
            maxV = 1F - (float)offset;
            TextureDrawer.drawTexture(partX, partY, partW, partH, (float)offset, (float)border, minV, maxV, color.packed());
        }

        //Top left half
        if (progress > .875F)
        {
            partProgress = Math.min((progress - .875F) * 8F, 1F);
            partX = x + (size * offset);
            partW = ((circleSize / 2D) * size) * partProgress;
            partH = size * border;
            maxU = (float)offset + (((float)circleSize / 2F) * partProgress);
            TextureDrawer.drawTexture(partX, y, partW, partH, (float)offset, maxU, 0, (float)border, color.packed());
        }
    }

    public static void drawLine(BufferBuilder buffer, double x1, double y1, double x2, double y2, int r, int g, int b, int a)
    {
        buffer.pos(x1, y1, 0).color(r, g, b, a).endVertex();
        buffer.pos(x2, y2, 0).color(r, g, b, a).endVertex();
    }

    public static void drawRect(BufferBuilder buffer, double x1, double y1, double x2, double y2, float lineWidth, int r, int g, int b, int a)
    {
        double off = lineWidth * 0.1F;

        GL11.glLineWidth(lineWidth);
        drawLine(buffer, x1 - off, y1,       x2 + off, y1,       r, g, b, a);
        drawLine(buffer, x2,       y1 + off, x2,       y2 - off, r, g, b, a);
        drawLine(buffer, x1,       y1 + off, x1,       y2 - off, r, g, b, a);
        drawLine(buffer, x1 - off, y2,       x2 + off, y2,       r, g, b, a);
    }

    public static void fillRect(BufferBuilder buffer, double x1, double y1, double x2, double y2, int r, int g, int b, int a)
    {
        buffer.pos(x1, y1, 0).color(r, g, b, a).endVertex();
        buffer.pos(x2, y1, 0).color(r, g, b, a).endVertex();
        buffer.pos(x2, y2, 0).color(r, g, b, a).endVertex();
        buffer.pos(x1, y2, 0).color(r, g, b, a).endVertex();
    }

    public static void drawArc(float x, float y, float radius, float startAngle, float arcAngle, int segments, int r, int g, int b, int a)
    {
        //Setup GL state
        GlStateManager.disableTexture();
        GlStateManager.depthMask(false);

        //Get Tesselator and Draw Buffer
        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder buffer = tessellator.getBuffer();

        //Convert to radians
        startAngle = (float)Math.toRadians(startAngle);
        arcAngle = (float)Math.toRadians(arcAngle);

        float theta = arcAngle / (float)(segments - 1); //theta is now calculated from the arc angle instead, the - 1 bit comes from the fact that the arc is open

        double tangetial_factor = Math.tan(theta);
        double radial_factor = Math.cos(theta);

        double pathX = radius * Math.cos(startAngle); //we now start at the start angle
        double pathY = radius * Math.sin(startAngle);

        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        for(int i = 0; i < segments; i++)
        {
            buffer.pos(x + pathX, y + pathY, 0).color(r, g, b, a).endVertex();

            double tx = -pathY;
            double ty = pathX;

            pathX += tx * tangetial_factor;
            pathY += ty * tangetial_factor;

            pathX *= radial_factor;
            pathY *= radial_factor;
        }

        //Draw
        tessellator.draw();

        //Reset GL state
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture();
    }

    public static void drawCircle(double x, double y, double radius, int color, float value, int sides)
    {
        double TWICE_PI = Math.PI * 2;

        //Setup GL state
         RenderSystem.disableTexture();
        RenderSystem.depthMask(false);
        RenderSystem.disableBlend();

        float[] colors = getRGBAFloatArrayFromHexColor(color);

        //Get Tesselator and Draw Buffer
        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(x, y, 0).color(colors[0], colors[1], colors[2], colors[3]).endVertex();

        for(int i = 0; i <= (int) ((float)sides * value); i++)
        {
            double angle = (TWICE_PI * i / sides) + Math.toRadians(180);
            buffer.pos(x + Math.sin(angle) * radius, y + Math.cos(angle) * radius, 0).color(colors[0], colors[1], colors[2], colors[3]).endVertex();
        }

        //Draw
        tessellator.draw();

        //Reset GL state
        RenderSystem.enableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
    }

    public static float getLineWidthScaled(float width) { return width * ((float)window().getGuiScaleFactor() / 5.0F); }

    public static float[] getRGBAFloatArrayFromHexColor(int color)
    {
        float[] floats = new float[4];
        floats[0] = (color >> 24 & 255) / 255.0F;
        floats[1] = (color >> 16 & 255) / 255.0F;
        floats[2] = (color >>  8 & 255) / 255.0F;
        floats[3] = (color       & 255) / 255.0F;
        return floats;
    }

    public static int[] getRGBAArrayFromHexColor(int color)
    {
        int[] ints = new int[4];
        ints[0] = (color >> 24 & 255);
        ints[1] = (color >> 16 & 255);
        ints[2] = (color >>  8 & 255);
        ints[3] = (color       & 255);
        return ints;
    }

    private static MainWindow window() { return Minecraft.getInstance().getMainWindow(); }
}