/*  Copyright (C) <2017>  <XFactHD>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see http://www.gnu.org/licenses. */

package XFactHD.rssmc.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

public class ClientUtils
{
    public static void drawNameplateOnTE(TileEntity te, String str, float x, float y, float z, int maxDistance)
    {
        TileEntityRendererDispatcher dispatcher = TileEntityRendererDispatcher.instance;
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;

        Entity entity = dispatcher.entity;
        double distance = te.getDistanceSq(entity.posX, entity.posY, entity.posZ);
        if (distance > (double)(maxDistance * maxDistance)) { return; }

        x += .5;
        y += 1.5;
        z += .5;

        float viewerYaw = dispatcher.entityYaw;
        float viewerPitch = dispatcher.entityPitch;

        String[] strings = str.split("\n");

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(viewerPitch, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-0.025F, -0.025F, 0.025F);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        int width = 0;
        for (String s : strings)
        {
            int w = fontRenderer.getStringWidth(s) / 2;
            if (w > width) { width = w; }
        }
        GlStateManager.disableTexture2D();
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vertexbuffer.pos((double)(-width - 1), (double)(-1), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        vertexbuffer.pos((double)(-width - 1), (double)( 8 * strings.length), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        vertexbuffer.pos((double)( width + 1), (double)( 8 * strings.length), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        vertexbuffer.pos((double)( width + 1), (double)(-1), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();

        GlStateManager.depthMask(true);
        int index = 0;
        for (String s : strings)
        {
            fontRenderer.drawString(s, -fontRenderer.getStringWidth(s) / 2, 8 * index, -1);
            index += 1;
        }
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    public static void drawNameplateOnEntity(Entity entity, String str, float x, float y, float z, int maxDistance)
    {
        TileEntityRendererDispatcher dispatcher = TileEntityRendererDispatcher.instance;
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;

        Entity viewEntity = dispatcher.entity;
        double distance = entity.getDistanceSq(viewEntity.posX, viewEntity.posY, viewEntity.posZ);
        if  (distance > (double)(maxDistance * maxDistance)) { return; }

        //x += .5;
        y += 1.5;
        //z += .5;

        float viewerYaw = dispatcher.entityYaw;
        float viewerPitch = dispatcher.entityPitch;

        String[] strings = str.split("\n");

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(viewerPitch, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-0.025F, -0.025F, 0.025F);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        int width = 0;
        for (String s : strings)
        {
            int w = fontRenderer.getStringWidth(s) / 2;
            if (w > width) { width = w; }
        }
        GlStateManager.disableTexture2D();
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vertexbuffer.pos((double)(-width - 1), (double)(-1), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        vertexbuffer.pos((double)(-width - 1), (double)( 8 * strings.length), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        vertexbuffer.pos((double)( width + 1), (double)( 8 * strings.length), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        vertexbuffer.pos((double)( width + 1), (double)(-1), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();

        GlStateManager.depthMask(true);
        int index = 0;
        for (String s : strings)
        {
            fontRenderer.drawString(s, -fontRenderer.getStringWidth(s) / 2, 8 * index, -1);
            index += 1;
        }
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    public static void drawTexturedModalRectFromSprite(double x, double y, TextureAtlasSprite sprite, double w, double h, double maxU, double maxV)
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos(    x, y + h, 0).tex(sprite.getMinU(), sprite.getInterpolatedV(maxV)).endVertex();
        vertexbuffer.pos(x + w, y + h, 0).tex(sprite.getInterpolatedU(maxU), sprite.getInterpolatedV(maxV)).endVertex();
        vertexbuffer.pos(x + w,     y, 0).tex(sprite.getInterpolatedU(maxU), sprite.getMinV()).endVertex();
        vertexbuffer.pos(    x,     y, 0).tex(sprite.getMinU(), sprite.getMinV()).endVertex();
        tessellator.draw();
    }

    public static TextureAtlasSprite getSprite(String name)
    {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(name);
    }

    public static float[] getRGBAFloatArrayFromHexColor(int color)
    {
        float[] floats = new float[4];
        floats[0] = (color >> 16 & 255) / 255.0F;
        floats[1] = (color >> 8 & 255) / 255.0F;
        floats[2] = (color & 255) / 255.0F;
        floats[3] = (color >> 24 & 255) / 255.0F;
        return floats;
    }

    public static int getHexColorFromRGBAFloats(float red, float green, float blue, float alpha)
    {
        return ((int)(alpha * 255F) << 24) | ((int)(red * 255F) << 16) | ((int)(green * 255F) << 8) | ((int)(blue * 255F));
        //return ((int)(red * 255F) << 16) | ((int)(green * 255F) << 8) | ((int)(blue * 255F));
    }
}