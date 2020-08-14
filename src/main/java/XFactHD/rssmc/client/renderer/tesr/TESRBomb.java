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

import XFactHD.rssmc.client.util.ClientReference;
import XFactHD.rssmc.client.util.ClientUtils;
import XFactHD.rssmc.common.blocks.objective.TileEntityBomb;
import XFactHD.rssmc.common.data.EnumSide;
import XFactHD.rssmc.common.data.team.StatusController;
import XFactHD.rssmc.common.utils.Utils;
import XFactHD.rssmc.common.utils.helper.ConfigHandler;
import XFactHD.rssmc.common.utils.utilClasses.Position;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

public class TESRBomb extends TileEntitySpecialRenderer<TileEntityBomb>
{
    @Override
    public void renderTileEntityAt(TileEntityBomb te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        if (StatusController.canShowBombInfo(player()))
        {
            boolean defusing = te.isDefusing();
            int maxDefuseTime = TileEntityBomb.MAX_DEFUSE_TIME;
            int defuseTime = te.getDefuseTime();

            String text = StatusController.getPlayersSide(player()) == EnumSide.DEFFENDER ? ClientReference.DEFEND : ClientReference.PLANT;
            if (defusing) { text = ClientReference.DEFUSING; } //TODO: Check if this is correct
            this.setLightmapDisabled(true);
            ClientUtils.drawNameplateOnTE(te, text + "\n" + Integer.toString(getDistance(te)) + " M", (float) x, (float) y - .25F, (float) z, 60);
            this.setLightmapDisabled(false);

            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(x + .5, y + 1.25, z + .5);
                GlStateManager.rotate(player().getRotationYawHead(), 0, -1, 0);
                GlStateManager.rotate(player().rotationPitch, 1, 0, 0);
                GlStateManager.scale(.65, .65, .65);
                GlStateManager.disableDepth();
                GlStateManager.enableAlpha();

                RenderHelper.disableStandardItemLighting();

                Tessellator tess = Tessellator.getInstance();
                VertexBuffer buffer = tess.getBuffer();

                String location = te.getLocation() == 0 ? "a" : "b";
                TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("rssmc:gui/overlay/gui_bomb_location_" + location);

                buffer.begin(GL11.GL_QUADS, ClientReference.POSITION_TEX_LMAP);
                buffer.pos( .5, 0, 0).tex(sprite.getInterpolatedU( 0), sprite.getInterpolatedV(16)).lightmap(240, 240).endVertex();
                buffer.pos(-.5, 0, 0).tex(sprite.getInterpolatedU(16), sprite.getInterpolatedV(16)).lightmap(240, 240).endVertex();
                buffer.pos(-.5, 1, 0).tex(sprite.getInterpolatedU(16), sprite.getInterpolatedV( 0)).lightmap(240, 240).endVertex();
                buffer.pos( .5, 1, 0).tex(sprite.getInterpolatedU( 0), sprite.getInterpolatedV( 0)).lightmap(240, 240).endVertex();
                tess.draw();

                if (defuseTime > 0)
                {
                    int index = Utils.map(defuseTime, maxDefuseTime, 124);
                    sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("rssmc:gui/overlay/secure_status/gui_secure_status_" + index);

                    buffer.begin(GL11.GL_QUADS, ClientReference.POSITION_TEX_LMAP);
                    buffer.pos( .5, 0, 0).tex(sprite.getInterpolatedU( 0), sprite.getInterpolatedV(16)).lightmap(240, 240).endVertex();
                    buffer.pos(-.5, 0, 0).tex(sprite.getInterpolatedU(16), sprite.getInterpolatedV(16)).lightmap(240, 240).endVertex();
                    buffer.pos(-.5, 1, 0).tex(sprite.getInterpolatedU(16), sprite.getInterpolatedV( 0)).lightmap(240, 240).endVertex();
                    buffer.pos( .5, 1, 0).tex(sprite.getInterpolatedU( 0), sprite.getInterpolatedV( 0)).lightmap(240, 240).endVertex();
                    tess.draw();
                }

                GlStateManager.disableAlpha();
                GlStateManager.enableDepth();
            }
            GlStateManager.popMatrix();
        }

        //Debug Render
        if (player().capabilities.isCreativeMode && ConfigHandler.debugRenderObjective)
        {
            GlStateManager.pushMatrix();
            {
                AxisAlignedBB aabb = te.getAABB();
                //noinspection ConstantConditions
                BlockPos pos = te == null || te.getPos() == null ? BlockPos.ORIGIN : te.getPos();
                double xMin = aabb.minX - pos.getX();
                double yMin = aabb.minY - pos.getY();
                double zMin = aabb.minZ - pos.getZ();
                double xMax = aabb.maxX - pos.getX();
                double yMax = aabb.maxY - pos.getY();
                double zMax = aabb.maxZ - pos.getZ();

                GlStateManager.translate(x, y, z);

                GlStateManager.glLineWidth(10F);

                Tessellator tess = Tessellator.getInstance();
                VertexBuffer buffer = tess.getBuffer();

                buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
                buffer.pos(xMin, yMin, zMin).color(255, 0, 0, 255).endVertex();
                buffer.pos(xMin, yMax, zMin).color(255, 0, 0, 255).endVertex();
                buffer.pos(xMin, yMax, zMax).color(255, 0, 0, 255).endVertex();
                buffer.pos(xMin, yMin, zMax).color(255, 0, 0, 255).endVertex();
                buffer.pos(xMax, yMin, zMax).color(255, 0, 0, 255).endVertex();
                buffer.pos(xMax, yMax, zMax).color(255, 0, 0, 255).endVertex();
                buffer.pos(xMin, yMax, zMax).color(255, 0, 0, 255).endVertex();
                buffer.pos(xMin, yMin, zMax).color(255, 0, 0, 255).endVertex();
                buffer.pos(xMin, yMin, zMin).color(255, 0, 0, 255).endVertex();
                buffer.pos(xMax, yMin, zMin).color(255, 0, 0, 255).endVertex();
                buffer.pos(xMax, yMin, zMax).color(255, 0, 0, 255).endVertex();
                buffer.pos(xMax, yMax, zMax).color(255, 0, 0, 255).endVertex();
                buffer.pos(xMax, yMax, zMin).color(255, 0, 0, 255).endVertex();
                buffer.pos(xMin, yMax, zMin).color(255, 0, 0, 255).endVertex();
                buffer.pos(xMin, yMin, zMin).color(255, 0, 0, 255).endVertex();
                buffer.pos(xMax, yMin, zMin).color(255, 0, 0, 255).endVertex();
                buffer.pos(xMax, yMax, zMin).color(255, 0, 0, 255).endVertex();
                tess.draw();
            }
            GlStateManager.popMatrix();
        }
    }

    private static EntityPlayer player()
    {
        return Minecraft.getMinecraft().player;
    }

    private static int getDistance(TileEntityBomb te)
    {
        return (int)Utils.getPlayerPosition(player()).distanceTo(Position.fromBlockPos(te.getPos()));
    }
}