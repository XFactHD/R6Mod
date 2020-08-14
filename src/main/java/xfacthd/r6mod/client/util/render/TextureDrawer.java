package xfacthd.r6mod.client.util.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class TextureDrawer
{
    private static BufferBuilder buffer;

    public static void drawTexture(double x, double y, double w, double h, float minU, float maxU, float minV, float maxV)
    {
        start();
        fillBuffer(x, y, w, h, minU, maxU, minV, maxV);
        end();
    }

    public static void drawTexture(double x, double y, double w, double h, float minU, float maxU, float minV, float maxV, int color)
    {
        startColored();
        fillBuffer(x, y, w, h, minU, maxU, minV, maxV, color);
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

    public static void fillBuffer(double x, double y, double w, double h, float minU, float maxU, float minV, float maxV)
    {
        if (buffer == null) { throw new IllegalStateException("Drawing operation not started!"); }

        buffer.pos(x,     y + h, -90).tex(minU, maxV).endVertex();
        buffer.pos(x + w, y + h, -90).tex(maxU, maxV).endVertex();
        buffer.pos(x + w, y,     -90).tex(maxU, minV).endVertex();
        buffer.pos(x,     y,     -90).tex(minU, minV).endVertex();
    }

    public static void fillBuffer(double x, double y, double w, double h, float minU, float maxU, float minV, float maxV, int color)
    {
        if (buffer == null) { throw new IllegalStateException("Drawing operation not started!"); }

        float[] colors = UIRenderHelper.getRGBAFloatArrayFromHexColor(color);
        buffer.pos(x,     y + h, -90).color(colors[0], colors[1], colors[2], colors[3]).tex(minU, maxV).endVertex();
        buffer.pos(x + w, y + h, -90).color(colors[0], colors[1], colors[2], colors[3]).tex(maxU, maxV).endVertex();
        buffer.pos(x + w, y,     -90).color(colors[0], colors[1], colors[2], colors[3]).tex(maxU, minV).endVertex();
        buffer.pos(x,     y,     -90).color(colors[0], colors[1], colors[2], colors[3]).tex(minU, minV).endVertex();
    }

    public static void end()
    {
        if (buffer == null) { throw new IllegalStateException("Drawing operation not started!"); }

        buffer.finishDrawing();
        RenderSystem.enableAlphaTest();
        WorldVertexBufferUploader.draw(buffer);

        buffer = null;
    }
}