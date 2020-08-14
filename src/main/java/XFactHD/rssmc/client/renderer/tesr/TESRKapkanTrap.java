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

package XFactHD.rssmc.client.renderer.tesr;

import XFactHD.rssmc.common.blocks.gadget.TileEntityKapkanTrap;
import XFactHD.rssmc.common.utils.helper.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class TESRKapkanTrap extends TileEntitySpecialRenderer<TileEntityKapkanTrap>
{
    @Override
    public void renderTileEntityAt(TileEntityKapkanTrap te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        if (!ConfigHandler.debugRenderKapkan) { return; }
        float lineWidth = getLineWidth(te.getPos());
        if (lineWidth > 0F)
        {
            Vec3d laser = te.getLaser();
            GlStateManager.pushMatrix();

            Tessellator tess = Tessellator.getInstance();
            VertexBuffer buffer = tess.getBuffer();

            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();

            GlStateManager.glLineWidth(lineWidth);
            buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
            buffer.setTranslation(x, y, z);
            switch (te.getFacing())
            {
                case NORTH:
                {
                    buffer.pos(.185, .51, .2).color(255, 0, 0, 127).endVertex();
                    buffer.pos(laser.xCoord + .185, laser.yCoord + .51, laser.zCoord + .2).color(255, 0, 0, 127).endVertex();
                    break;
                }
                case EAST:
                {
                    buffer.pos(.8, .51, .185).color(255, 0, 0, 127).endVertex();
                    buffer.pos(laser.xCoord + .8, laser.yCoord + .51, laser.zCoord + .185).color(255, 0, 0, 127).endVertex();
                    break;
                }
                case SOUTH:
                {
                    buffer.pos(.815, .51, .8).color(255, 0, 0, 127).endVertex();
                    buffer.pos(laser.xCoord + .815, laser.yCoord + .51, laser.zCoord + .8).color(255, 0, 0, 127).endVertex();
                    break;
                }
                case WEST:
                {
                    buffer.pos(.2, .51, .815).color(255, 0, 0, 127).endVertex();
                    buffer.pos(laser.xCoord + .2, laser.yCoord + .51, laser.zCoord + .815).color(255, 0, 0, 127).endVertex();
                    break;
                }
            }
            tess.draw();
            buffer.setTranslation(0, 0, 0);

            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();

            GlStateManager.popMatrix();
        }
    }

    private float getLineWidth(BlockPos pos)
    {
        double d0 = ((double)pos.getX()) - Minecraft.getMinecraft().player.posX;
        double d1 = ((double)pos.getY()) - Minecraft.getMinecraft().player.posY;
        double d2 = ((double)pos.getZ()) - Minecraft.getMinecraft().player.posZ;
        float distance = (float) Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
        if (distance > 5) { return 0; }
        float width = 5F * (1 - (distance / 5F));
        return width > 0 ? width : 0;
    }
}