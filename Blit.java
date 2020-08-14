package xfacthd.r6mod.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class Blit
{
    private int blitOffset = 0;

    public static void blit(int x, int y, int z, int w, int h, TextureAtlasSprite sprite)
    {
        innerBlit(x, x + w, y, y + h, z, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV());
    }

    public void blit(int x, int y, int spriteX, int spriteY, int spriteW, int spriteH)
    {
        blit(x, y, this.blitOffset, (float)spriteX, (float)spriteY, spriteW, spriteH, 256, 256);
    }

    public static void blit(int x, int y, int z, float spriteX, float spriteY, int spriteW, int spriteH, int texH, int texW)
    {
        innerBlit(x, x + spriteW, y, y + spriteH, z, spriteW, spriteH, spriteX, spriteY, texW, texH);
    }

    public static void blit(int x, int y, int w, int h, float spriteX, float spriteY, int spriteW, int spriteH, int texW, int texH)
    {
        innerBlit(x, x + w, y, y + h, 0, spriteW, spriteH, spriteX, spriteY, texW, texH);
    }

    public static void blit(int x, int y, float spriteX, float spriteY, int w, int h, int texW, int texH)
    {
        blit(x, y, w, h, spriteX, spriteY, w, h, texW, texH);
    }

    private static void innerBlit(int xMin, int xMax, int yMin, int yMax, int z, int spriteW, int spriteH, float spriteX, float spriteY, int texW, int texH)
    {
        innerBlit(xMin, xMax, yMin, yMax, z, spriteX / (float)texW, (spriteX + (float)spriteW) / (float)texW, spriteY / (float)texH, (spriteY + (float)spriteH) / (float)texH);
    }

    protected static void innerBlit(int xMin, int xMax, int yMin, int yMax, int z, float uMin, float uMax, float vMin, float vMax)
    {
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(xMin, yMax, z).tex(uMin, vMax).endVertex();
        bufferbuilder.pos(xMax, yMax, z).tex(uMax, vMax).endVertex();
        bufferbuilder.pos(xMax, yMin, z).tex(uMax, vMin).endVertex();
        bufferbuilder.pos(xMin, yMin, z).tex(uMin, vMin).endVertex();
        bufferbuilder.finishDrawing();
        RenderSystem.enableAlphaTest();
        WorldVertexBufferUploader.draw(bufferbuilder);
    }

    public int getBlitOffset() {
        return this.blitOffset;
    }

    public void setBlitOffset(int offset) {
        this.blitOffset = offset;
    }
}