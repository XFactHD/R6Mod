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

package XFactHD.rssmc.client.event;

import XFactHD.rssmc.common.capability.dbnoHandler.DBNOHandlerStorage;
import XFactHD.rssmc.common.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Field;

@SideOnly(Side.CLIENT)
@SuppressWarnings("ConstantConditions")
public class DBNOClientEventHandler
{
    private static Field sleeping = ReflectionHelper.findField(EntityPlayer.class, "sleeping", "field_71083_bS");
    private static float fogDistance = 0.5F;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void colorFog(EntityViewRenderEvent.FogColors event)
    {
        if (isPlayerDBNO() && !getLocalPlayer().isDead)
        {
            event.setRed(0.5F + fogDistance / 2.0F);
            event.setGreen(0.5F + fogDistance / 2.0F);
            event.setBlue(0.5F + fogDistance / 2.0F);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderFog(EntityViewRenderEvent.RenderFogEvent event)
    {
        if (getLocalPlayer().isDead) { return; }
        float fog = (float)(Minecraft.getMinecraft().gameSettings.renderDistanceChunks * 16);
        fog *= fogDistance / 2.0F;
        boolean dbno = isPlayerDBNO();
        GlStateManager.setFogStart(fog * (dbno ? fogDistance * 0.25F : fogDistance * 0.75F));
        GlStateManager.setFogEnd(fog);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderGameOverlay(RenderGameOverlayEvent.Pre event)
    {
        if (!isPlayerDBNO() || getLocalPlayer().isDead) { fogDistance = Math.min(fogDistance + 0.0025F, 1.0F); }
        else { fogDistance = Math.max(fogDistance - 0.0025F, Math.max(0.0F, (float) getTimeLeft() / 300F)); }

        if (isPlayerDBNO() && !getLocalPlayer().isDead && event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS)
        {
            event.setCanceled(true);
            ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
            int index = Utils.map(300 - getTimeLeft(), 300, 124);
            int x = res.getScaledWidth() / 2 - 8;
            int y = res.getScaledHeight() / 2 - 8;
            TextureAtlasSprite sprite = getSprite("rssmc:gui/overlay/secure_status/secure_status_" + index);
            GlStateManager.enableAlpha();
            drawTexturedModalRect(x, y, sprite, 16, 16);
            sprite = getSprite("rssmc:gui/overlay/dbno_symbol");
            drawTexturedModalRect(x, y, sprite, 16, 16);
            GlStateManager.disableAlpha();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderPlayerPre(RenderPlayerEvent.Pre event)
    {
        if(isPlayerDBNO(event.getEntityPlayer()) && !getLocalPlayer().isDead)
        {
            event.getEntityPlayer().renderOffsetX = 0;
            event.getEntityPlayer().renderOffsetY = 0;
            event.getEntityPlayer().renderOffsetZ = 0;

            try {
                sleeping.set(event.getEntityPlayer(), true);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderPlayerPost(RenderPlayerEvent.Post event)
    {
        if(isPlayerDBNO() && !getLocalPlayer().isDead)
        {
            try {
                sleeping.set(event.getEntityPlayer(), false);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onCameraSetup(EntityViewRenderEvent.CameraSetup event)
    {
        if(isPlayerDBNO() && !getLocalPlayer().isDead)
        {
            GlStateManager.translate(0, 0, -1.5);
            event.setYaw(0);
            event.setPitch(-90);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if(event.phase == TickEvent.Phase.START) { return; }
        if(isPlayerDBNO(event.player) && event.player != getLocalPlayer() && !event.player.isDead)
        {
            double width = 0.6;
            double height = 1.8;
            event.player.setEntityBoundingBox(new AxisAlignedBB(event.player.posX - height, event.player.posY - width/2D, event.player.posZ - width/2D, event.player.posX, event.player.posY + width/2D, event.player.posZ + width/2D));
        }
    }

    private static boolean isPlayerDBNO()
    {
        return getLocalPlayer().getCapability(DBNOHandlerStorage.DBNO_HANDLER_CAPABILITY, null).isDBNO();
    }

    private static boolean isPlayerDBNO(EntityPlayer player)
    {
        return player.getCapability(DBNOHandlerStorage.DBNO_HANDLER_CAPABILITY, null).isDBNO();
    }

    private static int getTimeLeft()
    {
        return Minecraft.getMinecraft().player.getCapability(DBNOHandlerStorage.DBNO_HANDLER_CAPABILITY, null).getTimeLeft();
    }

    private static EntityPlayer getLocalPlayer()
    {
        return Minecraft.getMinecraft().player;
    }

    private TextureAtlasSprite getSprite(String name)
    {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(name);
    }

    private static void drawTexturedModalRect(double xCoord, double yCoord, TextureAtlasSprite sprite, double width, double height)
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos(          xCoord, yCoord + height, 0).tex(sprite.getMinU(), sprite.getMaxV()).endVertex();
        vertexbuffer.pos(xCoord + width, yCoord + height, 0).tex(sprite.getMaxU(), sprite.getMaxV()).endVertex();
        vertexbuffer.pos(xCoord + width,            yCoord, 0).tex(sprite.getMaxU(), sprite.getMinV()).endVertex();
        vertexbuffer.pos(          xCoord,            yCoord, 0).tex(sprite.getMinU(), sprite.getMinV()).endVertex();
        tessellator.draw();
    }
}
