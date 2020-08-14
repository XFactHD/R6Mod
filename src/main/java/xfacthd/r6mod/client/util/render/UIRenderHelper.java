package xfacthd.r6mod.client.util.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import xfacthd.r6mod.R6Mod;

public class UIRenderHelper
{
    private static final ResourceLocation BAR_LOCATION = new ResourceLocation(R6Mod.MODID, "textures/gui/overlay/progress_bar.png");
    private static final float FRAME_WIDTH = 50;
    private static final float FRAME_HEIGHT = 8;
    private static final float FRAME_MAX_U = FRAME_WIDTH / 64F;
    private static final float BAR_WIDTH = 46;
    private static final float BAR_OFFSET = (FRAME_WIDTH - BAR_WIDTH) / 2F;
    private static final float BAR_MIN_U = BAR_OFFSET / 64F;
    private static final float BAR_U_RANGE = BAR_WIDTH / 64F;
    private static final double TWICE_PI = Math.PI * 2D;

    /**
     * Draws a progress bar
     * @param matrix The MatrixStack used for transformations
     * @param x The horizontal position
     * @param y The vertical position
     * @param progress The progress to show (0F-1F)
     * @param frameColor The color to tint the frame with
     * @param barColor The color to tint the bar with
     * @param centerX Wether the x coordinate should be the center or the left edge of the progress bar
     */
    public static void drawProgressBar(MatrixStack matrix, float x, float y, float progress, Color4i frameColor, Color4i barColor, boolean centerX)
    {
        mc().getTextureManager().bindTexture(BAR_LOCATION);

        if (centerX) { x -= FRAME_WIDTH / 2F; }

        TextureDrawer.startColored();

        TextureDrawer.fillBuffer(matrix, x, y, FRAME_WIDTH, FRAME_HEIGHT, 0, FRAME_MAX_U, 0, .5F, frameColor.packed());

        float width = BAR_WIDTH * progress;
        float maxU = BAR_MIN_U + (BAR_U_RANGE * progress);
        TextureDrawer.fillBuffer(matrix, x + BAR_OFFSET, y, width, FRAME_HEIGHT, BAR_MIN_U, maxU, .5F, 1, barColor.packed());

        TextureDrawer.end();
    }

    /**
     * Draws a progress circle in a square shape
     * @param matrix The MatrixStack used for transformations
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
    public static void drawProgressCircle(MatrixStack matrix, float x, float y, float size, float texSize, float edgeOff, float lineW, float progress, Color4i color, ResourceLocation texture)
    {
        Minecraft.getInstance().getTextureManager().bindTexture(texture);

        float offset = edgeOff / texSize;
        float border = (edgeOff + lineW) / texSize;
        float circleSize = (texSize - (edgeOff * 2F)) / texSize;

        //Top right half
        float partProgress = Math.min(progress * 8F, 1F);
        float partX = x + (size * offset) + ((circleSize / 2F) * size);
        float partY = y;
        float partW = ((circleSize / 2F) * 12F) * partProgress;
        float partH = size * border;
        float minU = .5F;
        float maxU = .5F + ((circleSize / 2F) * partProgress);
        float minV = 0;
        float maxV = border;
        TextureDrawer.drawTexture(matrix, partX, partY, partW, partH, minU, maxU, minV, maxV, color.packed());

        //Right
        if (progress > .125F)
        {
            partProgress = Math.min((progress - .125F) * 4F, 1F);
            partX = x + ((1F - border) * size);
            partY = y + (size * offset);
            partW = size * border;
            partH = (size * circleSize) * partProgress;
            minU = 1F - border;
            maxU = 1F;
            minV = offset;
            maxV = offset + circleSize * partProgress;
            TextureDrawer.drawTexture(matrix, partX, partY, partW, partH, minU, maxU, minV, maxV, color.packed());
        }

        //Bottom
        if (progress > .375F)
        {
            partProgress = Math.min((progress - .375F) * 4F, 1F);
            partX = x + (offset * size) + ((size * circleSize) * (1F - partProgress));
            partY = y + ((1F - border) * size);
            partW = (circleSize * size) * partProgress;
            partH = border * size;
            minU = (1F - offset) - (circleSize * partProgress);
            maxU = 1F - offset;
            minV = 1F - border;
            TextureDrawer.drawTexture(matrix, partX, partY, partW, partH, minU, maxU, minV, 1F, color.packed());
        }

        //Left
        if (progress > .625F)
        {
            partProgress = Math.min((progress - .625F) * 4F, 1F);
            partX = x + (offset * size);
            partY = y + (size * offset) + ((size * circleSize) * (1F - partProgress));
            partW = size * (border - offset);
            partH = (size * circleSize) * partProgress;
            minV = (1F - offset) - (circleSize * partProgress);
            maxV = 1F - offset;
            TextureDrawer.drawTexture(matrix, partX, partY, partW, partH, offset, border, minV, maxV, color.packed());
        }

        //Top left half
        if (progress > .875F)
        {
            partProgress = Math.min((progress - .875F) * 8F, 1F);
            partX = x + (size * offset);
            partW = ((circleSize / 2F) * size) * partProgress;
            partH = size * border;
            maxU = offset + ((circleSize / 2F) * partProgress);
            TextureDrawer.drawTexture(matrix, partX, y, partW, partH, offset, maxU, 0, border, color.packed());
        }
    }

    public static void drawArc(MatrixStack matrix, float x, float y, float radius, float lineWidth, float startAngle, float arcAngle, int segments, Color4i color)
    {
        int numSegs = (int)((float)segments * Math.abs(arcAngle / 360F));
        if (numSegs <= 1) { return; }

        //Get Tesselator and Draw Buffer
        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);

        double startAngleRad = Math.toRadians(startAngle);
        double arcAngleRad = Math.toRadians(arcAngle);

        double min_radius = radius - lineWidth;
        double max_radius = radius + lineWidth;

        for (int i = 0; i < numSegs; i++)
        {
            double angle = startAngleRad + ((arcAngleRad * (double) i) / (double) (numSegs - 1));

            double sin_angle = Math.sin(angle);
            double cos_angle = Math.cos(angle);

            float maxX = x + (float)(sin_angle * max_radius);
            float maxY = y + (float)(cos_angle * max_radius);
            float minX = x + (float)(sin_angle * min_radius);
            float minY = y + (float)(cos_angle * min_radius);

            buffer.pos(matrix.getLast().getMatrix(), maxX, maxY, 0).color(color.r(), color.g(), color.b(), color.a()).endVertex();
            buffer.pos(matrix.getLast().getMatrix(), minX, minY, 0).color(color.r(), color.g(), color.b(), color.a()).endVertex();
        }

        buffer.finishDrawing();
        //noinspection deprecation
        RenderSystem.enableAlphaTest();
        WorldVertexBufferUploader.draw(buffer);
    }

    public static void drawCircle(MatrixStack matrix, float x, float y, float radius, int color, float percent, int sides)
    {
        //Setup GL state
        RenderSystem.disableTexture();
        RenderSystem.depthMask(false);
        RenderSystem.disableBlend();

        int[] colors = getRGBAArrayFromHexColor(color);

        //Get Tesselator and Draw Buffer
        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(matrix.getLast().getMatrix(), x, y, 0).color(colors[0], colors[1], colors[2], colors[3]).endVertex();

        for(int i = 0; i <= (int) ((float)sides * percent); i++)
        {
            float angle = (float)((TWICE_PI * (double)i / (double)sides) + Math.toRadians(180));
            float pX = (float)(x + Math.sin(angle) * radius);
            float pY = (float)(y + Math.cos(angle) * radius);
            buffer.pos(matrix.getLast().getMatrix(), pX, pY, 0).color(colors[0], colors[1], colors[2], colors[3]).endVertex();
        }

        //Draw
        tessellator.draw();

        //Reset GL state
        RenderSystem.enableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
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

    private static Minecraft mc() { return Minecraft.getInstance(); }
}