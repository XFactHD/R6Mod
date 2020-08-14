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

import XFactHD.rssmc.common.blocks.gadget.TileEntityClaymore;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.lwjgl.opengl.GL11;

public class TESRClaymore extends TileEntitySpecialRenderer<TileEntityClaymore>
{
    @Override
    public void renderTileEntityAt(TileEntityClaymore te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        Vec3i[] lasers = te.getLasers();
        GlStateManager.pushMatrix();

        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();

        if (te.isActive())
        {
            Tessellator tess = Tessellator.getInstance();
            VertexBuffer buffer = tess.getBuffer();
            buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
            buffer.setTranslation(x, y, z);
            GlStateManager.glLineWidth(getLineWidth(te.getPos()));

            EnumFacing facing = te.getWorld().getBlockState(te.getPos()).getValue(PropertyHolder.FACING_CARDINAL);
            double xOff = facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH ? .5 : 0;
            double zOff = facing == EnumFacing.EAST || facing == EnumFacing.WEST ? .5 : 0;

            buffer.pos(.5, .23, .5).color(255, 0, 0, 127).endVertex();
            Vec3i vec1 = lasers[0];
            buffer.pos(vec1.getX() + xOff, vec1.getY() + .23, vec1.getZ() + zOff).color(255, 0, 0, 127).endVertex();
            buffer.pos(.5, .23, .5).color(255, 0, 0, 127).endVertex();
            Vec3i vec2 = lasers[1];
            buffer.pos(vec2.getX() + xOff, vec2.getY() + .23, vec2.getZ() + zOff).color(255, 0, 0, 127).endVertex();
            buffer.pos(.5, .23, .5).color(255, 0, 0, 127).endVertex();
            Vec3i vec3 = lasers[2];
            buffer.pos(vec3.getX() + xOff, vec3.getY() + .23, vec3.getZ() + zOff).color(255, 0, 0, 127).endVertex();
            tess.draw();
            buffer.setTranslation(0, 0, 0);
        }

        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();

        GlStateManager.popMatrix();
    }

    //TODO: find a better way to do this
    private float getLineWidth(BlockPos pos)
    {
        double d0 = ((double)pos.getX()) - Minecraft.getMinecraft().player.posX;
        double d1 = ((double)pos.getY()) - Minecraft.getMinecraft().player.posY;
        double d2 = ((double)pos.getZ()) - Minecraft.getMinecraft().player.posZ;
        float distance = (float) Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
        float factor = (1 - (distance / 128F));
        float width = 2F * (factor * factor * factor * factor);
        return width > 0 ? width : 0;
    }
}