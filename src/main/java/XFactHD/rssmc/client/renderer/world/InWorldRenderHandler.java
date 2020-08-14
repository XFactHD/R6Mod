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

package XFactHD.rssmc.client.renderer.world;

import XFactHD.rssmc.client.fx.ParticleFX;
import XFactHD.rssmc.client.util.ClientReference;
import XFactHD.rssmc.client.util.ClientUtils;
import XFactHD.rssmc.common.data.EnumOperator;
import XFactHD.rssmc.common.data.EnumParticle;
import XFactHD.rssmc.common.data.EnumSide;
import XFactHD.rssmc.common.data.team.StatusController;
import XFactHD.rssmc.common.utils.logic.FootStepHandler;
import XFactHD.rssmc.common.utils.logic.GadgetHandler;
import XFactHD.rssmc.common.utils.utilClasses.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class InWorldRenderHandler
{
    private static ArrayList<Position> collisionPoints = new ArrayList<>();
    private static HashMap<Position, Pair<Vec3d, Vec3d>> traces = new HashMap<>();
    private static HashMap<Position, Marker> markers = new HashMap<>();
    private static ArrayList<ParticleFX> particles = new ArrayList<>();
    private static final boolean IGNORE_SIDE = false;

    @SubscribeEvent
    public void renderWorld(RenderWorldLastEvent event)
    {
        renderMarkers(event);
        renderFootsteps(event);
        renderParticles(event);
        renderGrenadeThreatIndicators(event);
        renderGunTraces(event);
        renderCollisionTraces(event);
    }

    //Marker renderer
    private void renderMarkers(RenderWorldLastEvent event)
    {
        EnumSide side = StatusController.getPlayersSide(player());
        if (!markers.isEmpty())
        {
            Tessellator tess = Tessellator.getInstance();
            VertexBuffer buffer = tess.getBuffer();

            GlStateManager.pushMatrix();
            {
                GlStateManager.disableDepth();
                RenderHelper.disableStandardItemLighting();

                for (Position pos : markers.keySet())
                {
                    GlStateManager.pushMatrix();
                    if (markers.get(pos).getSide() != side && !IGNORE_SIDE) { continue; }
                    String type = markers.get(pos).getType().toString().toLowerCase(Locale.ENGLISH);

                    TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("rssmc:gui/overlay/" + type + "_marker");

                    GlStateManager.translate(getX(pos.getX(), event.getPartialTicks()), getY(pos.getY(), event.getPartialTicks()), getZ(pos.getZ(), event.getPartialTicks()));
                    GlStateManager.rotate(player().getRotationYawHead(), 0, -1, 0);
                    GlStateManager.rotate(player().rotationPitch, 1, 0, 0);
                    GlStateManager.scale(.5, .5, .5);

                    if (markers.get(pos).getType() == MarkerType.JACKAL)
                    {
                        float[] colors = ClientUtils.getRGBAFloatArrayFromHexColor(0x570000);
                        GlStateManager.color(colors[0], colors[1], colors[2], 1);
                    }

                    buffer.begin(GL11.GL_QUADS, ClientReference.POSITION_TEX_LMAP);
                    buffer.pos( .5,  .5, 0).tex(sprite.getInterpolatedU(16), sprite.getInterpolatedV(0)).lightmap(240, 240).endVertex();
                    buffer.pos(-.5,  .5, 0).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(0)).lightmap(240, 240).endVertex();
                    buffer.pos(-.5, -.5, 0).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(16)).lightmap(240, 240).endVertex();
                    buffer.pos( .5, -.5, 0).tex(sprite.getInterpolatedU(16), sprite.getInterpolatedV(16)).lightmap(240, 240).endVertex();

                    buffer.pos( .5, -.5, 0).tex(sprite.getInterpolatedU(16), sprite.getInterpolatedV(16)).lightmap(240, 240).endVertex();
                    buffer.pos(-.5, -.5, 0).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(16)).lightmap(240, 240).endVertex();
                    buffer.pos(-.5,  .5, 0).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(0)).lightmap(240, 240).endVertex();
                    buffer.pos( .5,  .5, 0).tex(sprite.getInterpolatedU(16), sprite.getInterpolatedV(0)).lightmap(240, 240).endVertex();
                    tess.draw();

                    GlStateManager.color(1, 1, 1, 1);

                    GlStateManager.popMatrix();
                }

                GlStateManager.enableDepth();
            }
            GlStateManager.popMatrix();
        }
    }

    //Footstep renderer
    private void renderFootsteps(RenderWorldLastEvent event)
    {
        HashMap<Position, FootStep> footSteps = FootStepHandler.getFootSteps();
        if ((isJackalAndUsingGadget() || IGNORE_SIDE) && !footSteps.isEmpty())
        {
            Tessellator tess = Tessellator.getInstance();
            VertexBuffer buffer = tess.getBuffer();

            RenderHelper.disableStandardItemLighting();

            for (Position pos : footSteps.keySet())
            {
                GlStateManager.pushMatrix();
                FootStep step = footSteps.get(pos);

                TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("rssmc:gui/overlay/footprint_" + step.isRight());

                GlStateManager.translate(getX(pos.getX(), event.getPartialTicks()), getY(pos.getY(), event.getPartialTicks()), getZ(pos.getZ(), event.getPartialTicks()));
                GlStateManager.rotate(360 - step.getRotation(), 0, 1, 0);

                int[] color = step.getColor();

                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
                buffer.pos(1, .0001, 0).tex(sprite.getInterpolatedU(16), sprite.getInterpolatedV(16)).lightmap(240, 240).color(color[0], color[1], color[2], color[3]).endVertex();
                buffer.pos(0, .0001, 0).tex(sprite.getInterpolatedU( 0), sprite.getInterpolatedV(16)).lightmap(240, 240).color(color[0], color[1], color[2], color[3]).endVertex();
                buffer.pos(0, .0001, 1).tex(sprite.getInterpolatedU( 0), sprite.getInterpolatedV( 0)).lightmap(240, 240).color(color[0], color[1], color[2], color[3]).endVertex();
                buffer.pos(1, .0001, 1).tex(sprite.getInterpolatedU(16), sprite.getInterpolatedV( 0)).lightmap(240, 240).color(color[0], color[1], color[2], color[3]).endVertex();
                tess.draw();
                GlStateManager.popMatrix();
            }

            RenderHelper.enableStandardItemLighting();
        }
    }

    //Custom particle renderer
    private void renderParticles(RenderWorldLastEvent event)
    {
        Tessellator tess = Tessellator.getInstance();
        VertexBuffer buffer = tess.getBuffer();

        GlStateManager.pushMatrix();
        {
            RenderHelper.disableStandardItemLighting();

            for (ParticleFX fx : particles)
            {
                GlStateManager.pushMatrix();

                if (fx.getParticle().needsDepthDisabled()) { GlStateManager.disableDepth(); }

                Position pos = fx.getPos();
                TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fx.getParticle().getTexture());

                GlStateManager.translate(getX(pos.getX(), event.getPartialTicks()), getY(pos.getY(), event.getPartialTicks()), getZ(pos.getZ(), event.getPartialTicks()));
                GlStateManager.rotate(player().getRotationYawHead(), 0, -1, 0);
                GlStateManager.rotate(player().rotationPitch, 1, 0, 0);
                GlStateManager.scale(.5, .5, .5);

                buffer.begin(GL11.GL_QUADS, ClientReference.POSITION_TEX_LMAP);
                buffer.pos( .5,  .5, 0).tex(sprite.getInterpolatedU(16), sprite.getInterpolatedV(0)).lightmap(240, 240).endVertex();
                buffer.pos(-.5,  .5, 0).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(0)).lightmap(240, 240).endVertex();
                buffer.pos(-.5, -.5, 0).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(16)).lightmap(240, 240).endVertex();
                buffer.pos( .5, -.5, 0).tex(sprite.getInterpolatedU(16), sprite.getInterpolatedV(16)).lightmap(240, 240).endVertex();

                buffer.pos( .5, -.5, 0).tex(sprite.getInterpolatedU(16), sprite.getInterpolatedV(16)).lightmap(240, 240).endVertex();
                buffer.pos(-.5, -.5, 0).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(16)).lightmap(240, 240).endVertex();
                buffer.pos(-.5,  .5, 0).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(0)).lightmap(240, 240).endVertex();
                buffer.pos( .5,  .5, 0).tex(sprite.getInterpolatedU(16), sprite.getInterpolatedV(0)).lightmap(240, 240).endVertex();
                tess.draw();

                if (fx.getParticle().needsDepthDisabled()) { GlStateManager.enableDepth(); }

                GlStateManager.popMatrix();
            }
        }
        GlStateManager.popMatrix();
    }

    //Gun trace debug renderer
    private void renderGunTraces(RenderWorldLastEvent event)
    {
        Tessellator tess = Tessellator.getInstance();
        VertexBuffer buffer = tess.getBuffer();

        for (Position pos : traces.keySet())
        {
            GlStateManager.pushMatrix();
            GlStateManager.disableDepth();
            GlStateManager.glLineWidth(5);

            double x = getX(pos.getX(), event.getPartialTicks());
            double y = getY(pos.getY(), event.getPartialTicks());
            double z = getZ(pos.getZ(), event.getPartialTicks());

            buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
            buffer.setTranslation(x, y, z);
            buffer.pos(0, 0, 0).color(255, 0, 0, 255).endVertex();
            buffer.pos(0, 2, 0).color(255, 0, 0, 255).endVertex();

            Vec3d start = traces.get(pos).getLeft();
            Vec3d end = traces.get(pos).getRight();

            double xStart = x + getX(start.xCoord, event.getPartialTicks());
            double yStart = y + getY(start.yCoord, event.getPartialTicks());
            double zStart = z + getZ(start.zCoord, event.getPartialTicks());
            double xEnd = x + getX(end.xCoord, event.getPartialTicks());
            double yEnd = y + getY(end.yCoord, event.getPartialTicks());
            double zEnd = z + getZ(end.zCoord, event.getPartialTicks());
            buffer.pos(xStart, yStart, zStart).color(255, 0, 0, 255).endVertex();
            buffer.pos(xEnd, yEnd, zEnd).color(255, 0, 0, 255).endVertex();
            tess.draw();
            buffer.setTranslation(0, 0, 0);

            GlStateManager.enableDepth();
            GlStateManager.popMatrix();
        }
    }

    //Grenade fly path debug renderer
    private void renderCollisionTraces(RenderWorldLastEvent event)
    {
        Tessellator tess = Tessellator.getInstance();
        VertexBuffer buffer = tess.getBuffer();

        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GlStateManager.glLineWidth(5);

        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);

        for (Position pos : collisionPoints)
        {
            double x = getX(pos.getX(), event.getPartialTicks());
            double y = getY(pos.getY(), event.getPartialTicks());
            double z = getZ(pos.getZ(), event.getPartialTicks());
            buffer.pos(x, y, z).color(255, 0, 0, 255).endVertex();
        }
        tess.draw();

        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }

    //Grenade threat indicator renderer
    private void renderGrenadeThreatIndicators(RenderWorldLastEvent event) //TODO: implement
    {

    }

    //Helpers
    private static double getX(double marker, float partialTicks)
    {
        double pos = player().posX;
        double lastPos = player().prevPosX;
        return marker - (lastPos + (pos - lastPos) * partialTicks);
    }

    private static double getY(double marker, float partialTicks)
    {
        double pos = player().posY;
        double lastPos = player().prevPosY;
        return marker - (lastPos + (pos - lastPos) * partialTicks);
    }

    private static double getZ(double marker, float partialTicks)
    {
        double pos = player().posZ;
        double lastPos = player().prevPosZ;
        return marker - (lastPos + (pos - lastPos) * partialTicks);
    }

    private static boolean isJackalAndUsingGadget()
    {
        return StatusController.getPlayersOperator(player()) == EnumOperator.JACKAL && GadgetHandler.getHandlerForPlayer(player()).getFootPrintScanner();
    }

    private static EntityPlayer player()
    {
        return Minecraft.getMinecraft().player;
    }

    private static Random random()
    {
        return player().world.rand;
    }

    public static void updateMarkers(HashMap<Position, Marker> markers)
    {
        InWorldRenderHandler.markers = markers;
    }

    public static void addParticle(EnumParticle particle, double x, double y, double z)
    {
        particles.add(new ParticleFX(particle, new Position(x, y, z)));
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event)
    {
        ArrayList<ParticleFX> toRemove = new ArrayList<>();
        if (event.phase == TickEvent.Phase.START)
        {
            for (ParticleFX fx : particles)
            {
                fx.tick();
                if (fx.isDead()) { toRemove.add(fx); }
            }
        }
        particles.removeAll(toRemove);
    }

    //Gun trace debug render
    public static void addGunTrace(Position pos, Vec3d startVec, Vec3d endVec)
    {
        traces.put(pos, Pair.of(startVec, endVec));
    }

    public static void clearGunTraces()
    {
        traces.clear();
    }

    //Grenade fly path debug render
    public static void addCollisionPoint(double x, double y, double z)
    {
        collisionPoints.add(new Position(x, y, z));
    }

    public static void clearCollisions()
    {
        collisionPoints.clear();
    }
}