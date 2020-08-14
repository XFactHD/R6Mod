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
import XFactHD.rssmc.common.blocks.gadget.TileEntityArmorBag;
import XFactHD.rssmc.common.data.EnumSide;
import XFactHD.rssmc.common.data.team.StatusController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

public class TESRArmorBag extends TileEntitySpecialRenderer<TileEntityArmorBag>
{
    @Override
    public void renderTileEntityAt(TileEntityArmorBag te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        int armorLeft = te.getArmorLeft();
        if (armorLeft > 0 && StatusController.getPlayersSide(player()) == EnumSide.DEFFENDER)
        {
            int maxDistance = 8;
            Entity entity = getRenderViewEntitySafe();
            double distance = te.getDistanceSq(entity.posX, entity.posY, entity.posZ);
            if (distance <= (double)(maxDistance * maxDistance))
            {
                GlStateManager.pushMatrix();
                {
                    GlStateManager.translate(x + .5, y + .7, z + .5);
                    float yaw = getRenderViewEntitySafe() instanceof EntityPlayer ? player().getRotationYawHead() : getRenderViewEntitySafe().rotationYaw;
                    GlStateManager.rotate(yaw, 0, -1, 0);
                    GlStateManager.rotate(getRenderViewEntitySafe().rotationPitch, 1, 0, 0);
                    GlStateManager.scale(.25, .25, .25);
                    GlStateManager.disableDepth();

                    RenderHelper.disableStandardItemLighting();

                    Tessellator tess = Tessellator.getInstance();
                    VertexBuffer buffer = tess.getBuffer();

                    bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                    TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("rssmc:gui/overlay/armor_bag_symbol");

                    buffer.begin(GL11.GL_QUADS, ClientReference.POSITION_TEX_LMAP);
                    buffer.pos(.5, 0, 0).tex(sprite.getInterpolatedU(16), sprite.getInterpolatedV(16)).lightmap(240, 240).endVertex();
                    buffer.pos(-.5, 0, 0).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(16)).lightmap(240, 240).endVertex();
                    buffer.pos(-.5, 1, 0).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(0)).lightmap(240, 240).endVertex();
                    buffer.pos(.5, 1, 0).tex(sprite.getInterpolatedU(16), sprite.getInterpolatedV(0)).lightmap(240, 240).endVertex();
                    tess.draw();

                    GlStateManager.disableAlpha();
                    GlStateManager.enableDepth();
                }
                GlStateManager.popMatrix();
            }

            this.setLightmapDisabled(true);
            ClientUtils.drawNameplateOnTE(te, Integer.toString(armorLeft), (float) x, (float) y - .8F, (float) z, maxDistance);
            this.setLightmapDisabled(false);
        }
    }

    private static Minecraft mc()
    {
        return Minecraft.getMinecraft();
    }

    private static EntityPlayer player()
    {
        return mc().player;
    }

    private Entity getRenderViewEntitySafe()
    {
        return mc().getRenderViewEntity() != null ? mc().getRenderViewEntity() : player();
    }
}