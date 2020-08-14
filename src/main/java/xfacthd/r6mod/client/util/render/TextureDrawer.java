package xfacthd.r6mod.client.util.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class TextureDrawer
{
    private static BufferBuilder buffer;

    public static void drawTexture(MatrixStack matrix, float x, float y, float w, float h, float minU, float maxU, float minV, float maxV)
    {
        start();
        fillBuffer(matrix, x, y, w, h, minU, maxU, minV, maxV);
        end();
    }

    public static void drawTexture(MatrixStack matrix, float x, float y, float w, float h, float minU, float maxU, float minV, float maxV, int color)
    {
        startColored();
        fillBuffer(matrix, x, y, w, h, minU, maxU, minV, maxV, color);
        end();
    }

    public static void drawGuiTexture(MatrixStack matrix, float x, float y, float texX, float texY, float w, float h)
    {
        start();
        fillGuiBuffer(matrix, x, y, texX, texY, w, h);
        end();
    }

    public static void drawGuiTexture(MatrixStack matrix, float x, float y, float texX, float texY, float w, float h, int color)
    {
        startColored();
        fillGuiBuffer(matrix, x, y, texX, texY, w, h, color);
        end();
    }

    public static void start()
    {
        if (buffer != null) { throw new IllegalStateException("Last drawing operation not finished!"); }

        buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
    }

    public static void startColored()
    {
        if (buffer != null) { throw new IllegalStateException("Last drawing operation not finished!"); }

        buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
    }

    public static void fillBuffer(MatrixStack matrix, float x, float y, float w, float h, float minU, float maxU, float minV, float maxV)
    {
        if (buffer == null) { throw new IllegalStateException("Drawing operation not started!"); }

        buffer.pos(matrix.getLast().getMatrix(), x,     y + h, -90).tex(minU, maxV).endVertex();
        buffer.pos(matrix.getLast().getMatrix(), x + w, y + h, -90).tex(maxU, maxV).endVertex();
        buffer.pos(matrix.getLast().getMatrix(), x + w, y,     -90).tex(maxU, minV).endVertex();
        buffer.pos(matrix.getLast().getMatrix(), x,     y,     -90).tex(minU, minV).endVertex();
    }

    public static void fillBuffer(MatrixStack matrix, float x, float y, float w, float h, float minU, float maxU, float minV, float maxV, int color)
    {
        if (buffer == null) { throw new IllegalStateException("Drawing operation not started!"); }

        int[] colors = UIRenderHelper.getRGBAArrayFromHexColor(color);
        buffer.pos(matrix.getLast().getMatrix(), x,     y + h, -90).color(colors[0], colors[1], colors[2], colors[3]).tex(minU, maxV).endVertex();
        buffer.pos(matrix.getLast().getMatrix(), x + w, y + h, -90).color(colors[0], colors[1], colors[2], colors[3]).tex(maxU, maxV).endVertex();
        buffer.pos(matrix.getLast().getMatrix(), x + w, y,     -90).color(colors[0], colors[1], colors[2], colors[3]).tex(maxU, minV).endVertex();
        buffer.pos(matrix.getLast().getMatrix(), x,     y,     -90).color(colors[0], colors[1], colors[2], colors[3]).tex(minU, minV).endVertex();
    }

    public static void fillGuiBuffer(MatrixStack matrix, float x, float y, float texX, float texY, float w, float h)
    {
        float minU = texX / 256F;
        float maxU = minU + (w / 256F);
        float minV = texY / 256F;
        float maxV = minV + (h / 256F);
        fillBuffer(matrix, x, y, w, h, minU, maxU, minV, maxV);
    }

    public static void fillGuiBuffer(MatrixStack matrix, float x, float y, float texX, float texY, float w, float h, int color)
    {
        float minU = texX / 256F;
        float maxU = minU + (w / 256F);
        float minV = texY / 256F;
        float maxV = minV + (h / 256F);
        fillBuffer(matrix, x, y, w, h, minU, maxU, minV, maxV, color);
    }

    public static void end()
    {
        if (buffer == null) { throw new IllegalStateException("Drawing operation not started!"); }

        buffer.finishDrawing();
        //noinspection deprecation
        RenderSystem.enableAlphaTest();
        WorldVertexBufferUploader.draw(buffer);

        buffer = null;
    }
}