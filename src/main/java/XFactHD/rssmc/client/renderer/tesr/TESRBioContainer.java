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
import XFactHD.rssmc.common.blocks.objective.TileEntityBioContainer;
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
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

public class TESRBioContainer extends TileEntitySpecialRenderer<TileEntityBioContainer>
{
    @Override
    public void renderTileEntityAt(TileEntityBioContainer te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        boolean securing = te.isSecuring();
        boolean contested = te.isContested();
        int maxSecureTime = TileEntityBioContainer.MAX_SECURE_TIME;
        int secureTime = te.getSecureTime();

        if (!StatusController.isPlayerInObjectiveArea(player()) || !(mc().getRenderViewEntity() instanceof EntityPlayer))
        {
            String text = StatusController.getPlayersSide(player()) == EnumSide.DEFFENDER ? ClientReference.DEFEND : ClientReference.ATTACK;
            if (securing) { text = ClientReference.SECURING; }
            if (contested) { text = ClientReference.CONTESTED; }
            this.setLightmapDisabled(true);
            ClientUtils.drawNameplateOnTE(te, text + "\n" + Integer.toString(getDistance(te)) + " M", (float) x, (float) y, (float) z, 60);
            this.setLightmapDisabled(false);

            mc().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(x + .5, y + 1.6, z + .5);
                float yaw = getRenderViewEntitySafe() instanceof EntityPlayer ? player().getRotationYawHead() : getRenderViewEntitySafe().rotationYaw;
                GlStateManager.rotate(yaw, 0, -1, 0);
                GlStateManager.rotate(getRenderViewEntitySafe().rotationPitch, 1, 0, 0);
                GlStateManager.scale(.65, .65, .65);
                GlStateManager.disableDepth();
                GlStateManager.enableAlpha();

                RenderHelper.disableStandardItemLighting();

                Tessellator tess = Tessellator.getInstance();
                VertexBuffer buffer = tess.getBuffer();

                TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("rssmc:gui/overlay/gui_objective_location");

                buffer.begin(GL11.GL_QUADS, ClientReference.POSITION_TEX_LMAP);
                buffer.pos( .5, 0, 0).tex(sprite.getInterpolatedU(16), sprite.getInterpolatedV(16)).lightmap(240, 240).endVertex();
                buffer.pos(-.5, 0, 0).tex(sprite.getInterpolatedU( 0), sprite.getInterpolatedV(16)).lightmap(240, 240).endVertex();
                buffer.pos(-.5, 1, 0).tex(sprite.getInterpolatedU( 0), sprite.getInterpolatedV( 0)).lightmap(240, 240).endVertex();
                buffer.pos( .5, 1, 0).tex(sprite.getInterpolatedU(16), sprite.getInterpolatedV( 0)).lightmap(240, 240).endVertex();
                tess.draw();

                if (secureTime > 0)
                {
                    int index = Utils.map(secureTime, maxSecureTime, 124);
                    sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("rssmc:gui/overlay/secure_status/gui_secure_status_" + index);

                    buffer.begin(GL11.GL_QUADS, ClientReference.POSITION_TEX_LMAP);
                    buffer.pos( .5, 0, 0).tex(sprite.getInterpolatedU(16), sprite.getInterpolatedV(16)).lightmap(240, 240).endVertex();
                    buffer.pos(-.5, 0, 0).tex(sprite.getInterpolatedU( 0), sprite.getInterpolatedV(16)).lightmap(240, 240).endVertex();
                    buffer.pos(-.5, 1, 0).tex(sprite.getInterpolatedU( 0), sprite.getInterpolatedV( 0)).lightmap(240, 240).endVertex();
                    buffer.pos( .5, 1, 0).tex(sprite.getInterpolatedU(16), sprite.getInterpolatedV( 0)).lightmap(240, 240).endVertex();
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

    private static int getDistance(TileEntityBioContainer te)
    {
        return (int)Utils.getPlayerPosition(player()).distanceTo(Position.fromBlockPos(te.getPos()));
    }

    private static Minecraft mc()
    {
        return Minecraft.getMinecraft();
    }

    private Entity getRenderViewEntitySafe()
    {
        return mc().getRenderViewEntity() != null ? mc().getRenderViewEntity() : player();
    }
}