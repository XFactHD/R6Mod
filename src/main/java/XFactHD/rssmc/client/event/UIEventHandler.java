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

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.api.block.*;
import XFactHD.rssmc.api.item.*;
import XFactHD.rssmc.api.util.*;
import XFactHD.rssmc.client.util.*;
import XFactHD.rssmc.client.util.wrappers.*;
import XFactHD.rssmc.common.blocks.misc.TileEntityGameManager;
import XFactHD.rssmc.common.blocks.objective.*;
import XFactHD.rssmc.common.capability.gunHandler.*;
import XFactHD.rssmc.common.data.*;
import XFactHD.rssmc.common.data.team.*;
import XFactHD.rssmc.common.entity.camera.*;
import XFactHD.rssmc.common.entity.drone.*;
import XFactHD.rssmc.common.items.ammo.ItemAmmo;
import XFactHD.rssmc.common.items.gadget.ItemFragGrenade;
import XFactHD.rssmc.common.items.gun.ItemGun;
import XFactHD.rssmc.common.utils.RSSWorldData;
import XFactHD.rssmc.common.utils.Utils;
import XFactHD.rssmc.common.utils.helper.ConfigHandler;
import XFactHD.rssmc.common.utils.logic.GadgetHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.lang.ref.WeakReference;
import java.util.*;

@SuppressWarnings("ConstantConditions")
public class UIEventHandler
{
    public static UIEventHandler INSTANCE = new UIEventHandler();

    private int ticks = 0;
    private long lastGrenadeStamp = 0;
    private long lastKillStamp = 0;
    private int jamTicksMove = 0;
    private int jamTicksStretch = 0;
    private boolean jamStretchReverse = false;
    private long lastEffectStamp = 0;
    private long lastFlashTextStamp = 0;
    private boolean renderCrosshair = false;
    private AbstractEntityCamera currentCam = null;
    private EnumScreenEffect currentEffect = null;
    private String currentFlashText = "";
    private List<PointInfo> pointInfo = new ArrayList<>();
    //private int infoIndex = 0; //counter for point info debug code

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event)
    {
        if (player() == null || world() == null) { return; }
        if (event.phase == TickEvent.Phase.START)
        {
            ticks += 1;
            if (ticks >= 40) { ticks = 0; }

            jamTicksMove += 1;
            if (jamTicksMove >= 80) { jamTicksMove = 0; }

            jamTicksStretch += jamStretchReverse ? -1 : 1;
            if (jamTicksStretch >= 30) { jamStretchReverse = true; }
            if (jamTicksStretch <= 0) { jamStretchReverse = false; }

            if (lastEffectStamp != 0 && world().getTotalWorldTime() - lastEffectStamp > currentEffect.getDuration())
            {
                currentEffect = null;
                lastEffectStamp = 0;
            }

            if (lastFlashTextStamp != 0 && world().getTotalWorldTime() - lastFlashTextStamp > 60)
            {
                currentFlashText = "";
                lastFlashTextStamp = 0;
            }

            //PointInfo debug code
            //if (pointInfo.size() < 5 && ticks % 20 == 0)
            //{
            //    pointInfo.add(new PointInfo(world().getTotalWorldTime(), "Test" + infoIndex, world().rand.nextInt(1000)));
            //    infoIndex += 1;
            //    if (infoIndex > 10) { infoIndex = 0; }
            //}

            ArrayList<PointInfo> toRemove = new ArrayList<>();
            for (PointInfo info : pointInfo)
            {
                if (world().getTotalWorldTime() - info.getTimestamp() > 80)
                {
                    toRemove.add(info);
                }
            }
            pointInfo.removeAll(toRemove);

            //Set player glowing if he is behind a wall or other obstacle
            EntityPlayer localPlayer = player(); //TODO: find a way to make the local player glow and render on his screen when using a camera
            localPlayer.setGlowing(mc().getRenderViewEntity() instanceof AbstractEntityCamera);
            Team team = StatusController.getPlayersTeam(localPlayer);
            if (team != null)
            {
                for (UUID uuid : team.getPlayerEntityMap().keySet())
                {
                    EntityPlayer player = team.getPlayerEntityMap().get(uuid).get();
                    if (player != null)
                    {
                        if (player != localPlayer)
                        {
                            if (localPlayer.canEntityBeSeen(player))
                            {
                                player.setGlowing(false);
                            }
                            else
                            {
                                player.setGlowing(true);
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlayPre(RenderGameOverlayEvent.Pre event)
    {
        if (player() == null || world() == null) { return; }

        ItemStack current = player().inventory.getCurrentItem();

        switch (event.getType())
        {
            case HEALTH:
            {
                if (renderHealthBar(event.getResolution())) { event.setCanceled(true); }
            }
            case PLAYER_LIST:
            {
                if (renderScoreboard(event.getResolution())) { event.setCanceled(true); }
            }
            case HELMET:
            {
                renderCameraEffects(event.getResolution(), event.getPartialTicks());
                renderEffectOverlay(event.getResolution(), event.getPartialTicks());
                break;
            }
            case CROSSHAIRS:
            {
                currentCam = mc().getRenderViewEntity() instanceof AbstractEntityCamera ? (AbstractEntityCamera)mc().getRenderViewEntity() : null;
                renderCameraOverlay(event.getResolution(), event.getPartialTicks());
                if (currentCam != null) { event.setCanceled(true); return; }
                if (handleFragGrenadeCrosshair(current)) { event.setCanceled(true); }
                if (renderGunCrosshair(current, event.getResolution())) { event.setCanceled(true); }
                if (renderJackalVisor(event.getResolution())) { event.setCanceled(true); }
                renderUsageTimer(current, Minecraft.getMinecraft().objectMouseOver, event.getResolution(), event.getPartialTicks());
                renderFlashingText(event.getResolution());
                break;
            }
            case HOTBAR:
            {
                //TODO: replace with a better workaround as the amount of issues is insane
                //if (currentCam != null && mc().currentScreen == null) { mc().setRenderViewEntity(player()); swapForHotbar = true; }
            }
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlayPost(RenderGameOverlayEvent.Post event)
    {
        ItemStack current = player().inventory.getCurrentItem();
        switch (event.getType())
        {
            case HOTBAR:
            {
                //TODO: replace with a better workaround as the amount of issues is insane
                //if (swapForHotbar) { mc().setRenderViewEntity(currentCam); swapForHotbar = false; }
                renderCooldownTimers(event.getResolution());
                renderCompass(event.getResolution());
                renderGameStatus(event.getResolution());
                if (currentCam != null) { return; }
                renderBiohazardContainerStatus(event.getResolution());
                renderAmmoCount(current, event.getResolution());
                renderJackalTrackingTimer(event.getResolution(), event.getPartialTicks());
                renderPointInfos(event.getResolution());
                break;
            }
            case CROSSHAIRS:
            {
                renderKillCross(event.getResolution());
            }
        }
    }

    //DESCRIPTION: Dedicated render methods
    private void renderBiohazardContainerStatus(ScaledResolution res)
    {
        if (!StatusController.isPlayerInObjectiveArea(player())) { return; }

        int yText = (int) ((float)res.getScaledHeight() - 34);
        int yIcon = (int) ((float)res.getScaledHeight() - 60);

        TileEntityBioContainer te = StatusController.getBiohazardContainer(world());
        if (te == null) { return; }
        boolean securing = te.isSecuring();
        boolean contested = te.isContested();
        int maxSecureTime = TileEntityBioContainer.MAX_SECURE_TIME;
        int secureTime = te.getSecureTime();

        String text = StatusController.getPlayersSide(player()) == EnumSide.DEFFENDER ? ClientReference.DEFEND : ClientReference.ATTACK;
        if (securing) { text = ClientReference.SECURING; }
        if (contested) { text = ClientReference.CONTESTED; }

        Minecraft.getMinecraft().fontRendererObj.drawString(TextFormatting.WHITE + text, (int)fromCenterX(res, - (text.length() / 2) * 5), yText, 0);

        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        TextureAtlasSprite sprite = getSprite("rssmc:gui/overlay/gui_objective_location");
        GlStateManager.enableAlpha();
        drawTexturedModalRect(fromCenterX(res, -12), yIcon, sprite, 24, 24);

        if (secureTime > 0)
        {
            int index = Utils.map(secureTime, maxSecureTime, 124);
            sprite = getSprite("rssmc:gui/overlay/secure_status/secure_status_" + index);
            drawTexturedModalRect(fromCenterX(res, -12), yIcon, sprite, 24, 24);
        }

        GlStateManager.disableAlpha();
    }

    private boolean renderGunCrosshair(ItemStack current, ScaledResolution res)
    {
        if (current != null && current.getItem() instanceof ItemGun)
        {
            GunHandler handler = (GunHandler) current.getCapability(GunHandlerStorage.GUN_HANDLER_CAPABILITY, null);
            if (handler.getGun() == null) { return false; }
            if (handler.isAiming() || player().isSprinting()) { return false; } //TODO: change to return true, when sights are finished and alligned

            GlStateManager.pushMatrix();

            float spreadRadius = (float) handler.getCurrentSpreadWidth() / 2F;
            double x = res.getScaledWidth_double() / 2D;
            double y = res.getScaledHeight_double() / 2D;
            if (handler.getGun().getGunType() == EnumGun.EnumGunType.SHOTGUN)
            {
                TextureAtlasSprite sprite = getSprite("rssmc:gui/overlay/crosshair_shotgun");
                float spreadFactor = handler.getCurrentSpreadWidth() / handler.getStandardSpreadWidth();
                double width = 12 * spreadFactor;
                spreadRadius *= spreadFactor;
                drawTexturedModalRectCustomSize(x - 6 * spreadFactor, y - 6 - spreadRadius, sprite, width, 3,     3D,     0D,   13D, 1.75D);
                drawTexturedModalRectCustomSize(x + 3 + spreadRadius, y - 6 * spreadFactor, sprite, 3, width, 14.25D,     3D,   16D,   13D);
                drawTexturedModalRectCustomSize(x - 6 * spreadFactor, y + 3 + spreadRadius, sprite, width, 3,     3D, 14.25D,   13D,   16D);
                drawTexturedModalRectCustomSize(x - 6 - spreadRadius, y - 6 * spreadFactor, sprite, 3, width,     0D,     3D, 1.75D,   13D);
            }
            else
            {
                TextureAtlasSprite sprite = getSprite("rssmc:gui/overlay/crosshair_gun");
                drawTexturedModalRectCustomSize(                   x, y - 4 - spreadRadius, sprite, 1, 4, 7.5D, 3.5D,  8.5D,  8.5D);
                drawTexturedModalRectCustomSize(x + 1 + spreadRadius,                    y, sprite, 4, 1, 8.5D, 7.5D, 12.5D,  8.5D);
                drawTexturedModalRectCustomSize(                   x, y + 1 + spreadRadius, sprite, 1, 4, 7.5D, 8.5D,  8.5D, 12.5D);
                drawTexturedModalRectCustomSize(x - 4 - spreadRadius,                    y, sprite, 4, 1, 3.5D, 7.5D,  7.5D,  8.5D);
            }

            GlStateManager.popMatrix();
            return true;
        }
        return false;
    }

    private void renderAmmoCount(ItemStack stack, ScaledResolution res)
    {
        if (stack != null && stack.getItem() instanceof ItemGun)
        {
            GlStateManager.pushMatrix();

            GunHandler handler = (GunHandler) stack.getCapability(GunHandlerStorage.GUN_HANDLER_CAPABILITY, null);
            if (handler.getGun() == null) { return; }

            int slot = player().inventory.currentItem;
            int ammoMax = handler.getGun() != null ? handler.getGun().getMagCapacity() : 0;
            int ammoLeft = handler.getAmmoLeft();
            int ammoInv = getAmmoInInventory(handler.getGun());

            int textX = (int)fromCenterX(res, 156 - font().getStringWidth(Integer.toString(ammoLeft)));
            int textY = res.getScaledHeight() - 15;
            font().drawString(ammoLeft + "/" + ammoInv, textX, textY, -1);

            if (handler.getGun().getFiremodes().size() > 1)
            {
                double x = fromCenterX(res, -88 + (20 * slot));
                double y = res.getScaledHeight() - 9;
                TextureAtlasSprite sprite = getSprite("rssmc:gui/overlay/firemode_" + handler.getFiremode().getName());
                GlStateManager.enableAlpha();
                drawTexturedModalRect(x, y, sprite, 6, 6);
                GlStateManager.disableAlpha();
            }

            if (((float) ammoLeft / (float) ammoMax) < .25)
            {
                String text = I18n.format("desc.rssmc:reload.name");
                textX = (res.getScaledWidth() / 2) - (font().getStringWidth(text) / 2);
                textY = (res.getScaledHeight() / 2) + 12;
                font().drawString(text, textX, textY, getRedFadeHex());
                GlStateManager.color(1, 1, 1, 1);
            }

            GlStateManager.popMatrix();
        }
    }

    private void renderUsageTimer(ItemStack stack, RayTraceResult mouseOver, ScaledResolution res, float partialTicks)
    {
        String text = "";
        int max = -1;
        int current = -1;

        if (stack != null && stack.getItem() instanceof IItemUsageTimer && ((IItemUsageTimer)stack.getItem()).isInUse(world(), stack, player()))
        {
            text = ((IItemUsageTimer)stack.getItem()).getDescription();
            max = ((IItemUsageTimer)stack.getItem()).getMaxTime(stack);
            current = ((IItemUsageTimer)stack.getItem()).getCurrentTime(world(), stack, player());
        }
        else if (mouseOver != null && mouseOver.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            TileEntity te = world().getTileEntity(mouseOver.getBlockPos());
            if (te instanceof IUsageTimer)
            {
                text = ((IUsageTimer)te).getDescription();
                max = ((IUsageTimer)te).getCurrentTime(world(), mouseOver.getBlockPos(), player());
                current = ((IUsageTimer)te).getMaxTime(world(), mouseOver.getBlockPos());
            }
            else
            {
                IBlockState state = world().getBlockState(mouseOver.getBlockPos());
                if (state.getBlock() instanceof IUsageTimer)
                {
                    text = ((IUsageTimer)state.getBlock()).getDescription();
                    max = ((IUsageTimer)state.getBlock()).getMaxTime(world(), mouseOver.getBlockPos());
                    current = ((IUsageTimer)state.getBlock()).getCurrentTime(world(), mouseOver.getBlockPos(), player());
                }
            }
        }

        if (current > -1 && max > -1 && current <= max)
        {
            TextureAtlasSprite sprite = getSprite("rssmc:gui/overlay/health_usage_timer");
            double x = fromCenterX(res, -16);
            double y = fromCenterY(res, 12);
            if (!text.equals(""))
            {
                text = I18n.format(text);
                int textX = (int)fromCenterX(res, -(font().getStringWidth(text) / 2));
                int textY = (int) y + 1;

                font().drawString(text, textX, textY, -1);
            }
            GlStateManager.enableAlpha();
            double factor = ((float)current + partialTicks) / (float) max;
            y += 13;
            drawTexturedModalRectCustomSize(    x,     y, sprite,           32,  5,  0, 0,            16, 2.5);
            drawTexturedModalRectCustomSize(x + 1, y + 1, sprite, 30D * factor,  3, .5, 3, 15.5 * factor, 4.5);
            GlStateManager.disableAlpha();
        }
    }

    private boolean handleFragGrenadeCrosshair(ItemStack current)
    {
        if (current != null && current.getItem() instanceof ItemFragGrenade)
        {
            int time = ((ItemFragGrenade)current.getItem()).getTimeLeft(current, RainbowSixSiegeMC.proxy.getWorld());
            if (time != -1)
            {
                float fraction = time / 50F;
                if (world().getTotalWorldTime() - lastGrenadeStamp >= (5F * fraction))
                {
                    renderCrosshair = !renderCrosshair;
                    lastGrenadeStamp = world().getTotalWorldTime();
                }

                return !renderCrosshair;
            }
        }
        return false;
    }

    private void renderCooldownTimers(ScaledResolution res)
    {
        if (mc().getRenderViewEntity() != player()) { return; }

        TextureAtlasSprite sprite = getSprite("rssmc:gui/overlay/gui_item_cooldown_time");

        for (int slot = 0; slot < 9; slot ++)
        {
            ItemStack stack = player().inventory.getStackInSlot(slot);
            if (stack != null && stack.getItem() instanceof ICooldown && stack.hasTagCompound())
            {
                int max = ((ICooldown)stack.getItem()).getCooldownTime(stack);
                int time = ((ICooldown)stack.getItem()).getCurrentTime(stack, world());
                if (time <= max && time != -1)
                {
                    double offX = 20 * slot;
                    double x = fromCenterX(res, -88) + offX;
                    double offY = 16D - (16D * ((double) time / (double) max));
                    double y = res.getScaledHeight_double() - 18D + offY;
                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    int color = ((ICooldown)stack.getItem()).getBarColor(stack, world());
                    if (color != -1)
                    {
                        float[] colors = ClientUtils.getRGBAFloatArrayFromHexColor(color);
                        GlStateManager.color(colors[0], colors[1], colors[2], 1);
                    }
                    drawTexturedModalRectCustomSize(x, y, sprite, 16, 16D * ((double) time / (double) max), 0, 0, 16, 16 - offY);

                    GlStateManager.color(1, 1, 1, 1);

                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                }
            }
        }
    }

    private void renderKillCross(ScaledResolution res)
    {
        if (world().getTotalWorldTime() - lastKillStamp <= 5)
        {
            GlStateManager.enableAlpha();
            GlStateManager.enableLighting();
            TextureAtlasSprite sprite = getSprite("rssmc:gui/overlay/kill_cross");
            drawTexturedModalRect(fromCenterX(res, -3.5), fromCenterY(res, -3.5), sprite, 8, 8);
            GlStateManager.disableLighting();
            GlStateManager.disableAlpha();
        }
    }

    private void renderCameraOverlay(ScaledResolution res, float partialTicks)
    {
        if (currentCam != null && !currentCam.isDead && !(currentCam instanceof EntityCamera && ((EntityCamera)currentCam).isDestroyed()))
        {
            GlStateManager.pushMatrix();
            if (currentCam instanceof EntityCamera)
            {
                GlStateManager.enableBlend();
                TextureAtlasSprite sprite = getSprite("rssmc:gui/overlay/camera_overlay");
                drawTexturedModalRectCustomSize(fromCenterX(res, -233), fromCenterY(res, -120), sprite, 24, 24, 0, 0, 6, 6); //Outer corners
                drawTexturedModalRectCustomSize(fromCenterX(res, -233), fromCenterY(res,   96), sprite, 24, 24, 0, 6, 6, 0); //Outer corners
                drawTexturedModalRectCustomSize(fromCenterX(res,  209), fromCenterY(res, -120), sprite, 24, 24, 6, 0, 0, 6); //Outer corners
                drawTexturedModalRectCustomSize(fromCenterX(res,  209), fromCenterY(res,   96), sprite, 24, 24, 6, 6, 0, 0); //Outer corners

                drawTexturedModalRectCustomSize(fromCenterX(res, -140), fromCenterY(res, -96), sprite, 24, 24, 0, 0, 6, 6); //Inner corners
                drawTexturedModalRectCustomSize(fromCenterX(res, -140), fromCenterY(res,  72), sprite, 24, 24, 0, 6, 6, 0); //Inner corners
                drawTexturedModalRectCustomSize(fromCenterX(res,  116), fromCenterY(res, -96), sprite, 24, 24, 6, 0, 0, 6); //Inner corners
                drawTexturedModalRectCustomSize(fromCenterX(res,  116), fromCenterY(res,  72), sprite, 24, 24, 6, 6, 0, 0); //Inner corners

                drawTexturedModalRectCustomSize(fromCenterX(res, -209), fromCenterY(res,   -120), sprite, 418, 4, 0, 8, 16, 9); //Top line
                drawTexturedModalRectCustomSize(fromCenterX(res, -209), fromCenterY(res,  115.9), sprite, 418, 4, 0, 8, 16, 9); //Bottom line
                GlStateManager.disableBlend();
            }
            else if (currentCam instanceof EntityYokaiCam)
            {
                GlStateManager.enableBlend();

                EntityYokaiDrone drone = ((EntityYokaiCam)currentCam).getDrone();

                String status = drone.isStationary() ? "stationary" : "floored";
                TextureAtlasSprite sprite = getSprite("rssmc:gui/overlay/yokai_overlay_" + status);
                drawTexturedModalRect(fromCenterX(res, -64), fromCenterY(res, -64), sprite, 128, 128);

                if (drone.isStationary())
                {
                    sprite = getSprite("rssmc:gui/overlay/yokai_widgets");
                    int xOff = /*drone.isReloading() ? 20 : */8;
                    drawTexturedModalRectCustomSize(fromCenterX(res, -74 - xOff), fromCenterY(res, -12), sprite, 8, 24, 0, 0, 4, 12);
                    drawTexturedModalRectCustomSize(fromCenterX(res,  66 + xOff), fromCenterY(res, -12), sprite, 8, 24, 4, 0, 0, 12);

                    float factor = ((float) drone.getCurrentReloadTime() + partialTicks) / (float) drone.getMaxReloadTime();
                    double yOff = 20D - (20D * factor);
                    drawTexturedModalRectCustomSize(fromCenterX(res, -74 - xOff), fromCenterY(res, -10 + yOff), sprite, 6, 20D * factor, 5, 11 - (10F * factor), 8, 11);
                    drawTexturedModalRectCustomSize(fromCenterX(res,  68 + xOff), fromCenterY(res, -10 + yOff), sprite, 6, 20D * factor, 5, 11 - (10F * factor), 8, 11);

                    drawDial(res.getScaledWidth_double() - 68, res.getScaledHeight_double() - 28.5, 8, 0x2D2D2D, 0xFFFFFF, factor, 100, true);

                    font().drawString(I18n.format("desc.rssmc:yokai.name"), res.getScaledWidth() - 56, res.getScaledHeight() - 36, -1);
                    String ammo = ((EntityYokaiCam)currentCam).getDrone().getAmmo() + "/∞";
                    font().drawString(ammo, res.getScaledWidth() - 56, res.getScaledHeight() - 28, -1);
                }
                GlStateManager.disableBlend();
            }
            else if (currentCam instanceof EntityDroneCam)
            {
                TextureAtlasSprite sprite = getSprite("rssmc:gui/overlay/drone_overlay");
                GlStateManager.enableBlend();

                drawTexturedModalRectCustomSize(fromCenterX(res,  221), fromCenterY(res, -120), sprite, 21, 96, 11.5,  0,   15, 16); //Right outline
                drawTexturedModalRectCustomSize(fromCenterX(res,  230), fromCenterY(res, - 24), sprite,  3, 48,   13,  4, 13.5, 16); //Right outline
                drawTexturedModalRectCustomSize(fromCenterX(res,  221), fromCenterY(res,   24), sprite, 21, 96, 11.5, 16,   15,  0); //Right outline

                drawTexturedModalRectCustomSize(fromCenterX(res, -242), fromCenterY(res, -120), sprite, 21, 96, 11.5,  0,   15, 16); //Left outline
                drawTexturedModalRectCustomSize(fromCenterX(res, -233), fromCenterY(res, - 24), sprite,  3, 48,   13,  4, 13.5, 16); //Left outline
                drawTexturedModalRectCustomSize(fromCenterX(res, -242), fromCenterY(res,   24), sprite, 21, 96, 11.5, 16,   15,  0); //Left outline

                drawTexturedModalRectCustomSize(fromCenterX(res, -200), fromCenterY(res, -90), sprite, 15, 15, 3.5, 8.5, 6, 11); //Top left plus
                drawTexturedModalRectCustomSize(fromCenterX(res, -200), fromCenterY(res,  75), sprite, 15, 15, 3.5, 8.5, 6, 11); //Bottom left plus
                drawTexturedModalRectCustomSize(fromCenterX(res,  185), fromCenterY(res, -90), sprite, 15, 15, 3.5, 8.5, 6, 11); //Top right plus
                drawTexturedModalRectCustomSize(fromCenterX(res,  185), fromCenterY(res,  75), sprite, 15, 15, 3.5, 8.5, 6, 11); //Bottom right plus

                GlStateManager.disableBlend();
            }
            else if (currentCam instanceof EntityTwitchDroneCam)
            {
                GlStateManager.enableBlend();

                TextureAtlasSprite sprite = getSprite("rssmc:gui/overlay/drone_overlay");

                drawTexturedModalRectCustomSize(fromCenterX(res, 0), fromCenterY(res, 0), sprite, 1, 1, 0, 0, .5, .5); //Crosshair

                EntityTwitchDrone drone = ((EntityTwitchDroneCam)currentCam).getDrone();
                if (drone.isReloading())
                {
                    float factor = ((float) drone.getCurrentReloadTime() + partialTicks) / (float) drone.getMaxReloadTime();
                    drawTexturedModalRectCustomSize(fromCenterX(res, 193), res.getScaledHeight_double() - 15.5 - (9F * factor), sprite, 7, 9D * factor, 0, 12.75 - (1.25 * factor), 1.75, 12.75);
                }
                else
                {
                    drawTexturedModalRectCustomSize(fromCenterX(res, 0), fromCenterY(res, -5), sprite, 1, 3, .5, 0, 2.5, 6); //Top flash
                    drawTexturedModalRectCustomSize(fromCenterX(res, 0), fromCenterY(res,  3), sprite, 1, 3, .5, 0, 2.5, 6); //Bottom flash

                    drawTexturedModalRectCustomSize(fromCenterX(res,  3), fromCenterY(res, 0), sprite, 3, 1, 0, 6, 6, 8); //Right flash
                    drawTexturedModalRectCustomSize(fromCenterX(res, -5), fromCenterY(res, 0), sprite, 3, 1, 0, 8, 6, 6); //Left flash
                }

                drawTexturedModalRectCustomSize(fromCenterX(res,  230), fromCenterY(res, -120), sprite, 12, 96, 8.5,  0, 6.5, 16); //Right outline
                drawTexturedModalRectCustomSize(fromCenterX(res,  230), fromCenterY(res, - 18), sprite, 12, 36,  11,  0,   9,  6); //Right outline
                drawTexturedModalRectCustomSize(fromCenterX(res,  230), fromCenterY(res,   24), sprite, 12, 96, 8.5, 16, 6.5,  0); //Right outline

                drawTexturedModalRectCustomSize(fromCenterX(res, -242), fromCenterY(res, -120), sprite, 12, 96, 6.5,  0, 8.5, 16); //Left outline
                drawTexturedModalRectCustomSize(fromCenterX(res, -242), fromCenterY(res, - 18), sprite, 12, 36,   9,  0,  11,  6); //Left outline
                drawTexturedModalRectCustomSize(fromCenterX(res, -242), fromCenterY(res,   24), sprite, 12, 96, 6.5, 16, 8.5,  0); //Left outline

                drawTexturedModalRectCustomSize(fromCenterX(res,  -23), fromCenterY(res,  -50), sprite, 3, 12, 0, 8.5, .5, 10.5); //Top crosshair line
                drawTexturedModalRectCustomSize(fromCenterX(res,   20), fromCenterY(res,  -50), sprite, 3, 12, 0, 8.5, .5, 10.5); //Top crosshair line

                drawTexturedModalRectCustomSize(fromCenterX(res,   38), fromCenterY(res, - 23), sprite, 12, 3, 1, 8.5, 3,  9); //Right crosshair line
                drawTexturedModalRectCustomSize(fromCenterX(res,   38), fromCenterY(res, -1.5), sprite,  6, 3, 1, 9.5, 2, 10); //Right crosshair line
                drawTexturedModalRectCustomSize(fromCenterX(res,   38), fromCenterY(res,   20), sprite, 12, 3, 1, 8.5, 3,  9); //Right crosshair line

                drawTexturedModalRectCustomSize(fromCenterX(res,  -23), fromCenterY(res,   38), sprite, 3, 12, 0, 8.5, .5, 10.5); //Bottom crosshair line
                drawTexturedModalRectCustomSize(fromCenterX(res,   20), fromCenterY(res,   38), sprite, 3, 12, 0, 8.5, .5, 10.5); //Bottom crosshair line

                drawTexturedModalRectCustomSize(fromCenterX(res,  -50), fromCenterY(res, - 23), sprite, 12, 3, 1, 8.5, 3,  9); //Left crosshair line
                drawTexturedModalRectCustomSize(fromCenterX(res,  -44), fromCenterY(res, -1.5), sprite,  6, 3, 1, 9.5, 2, 10); //Left crosshair line
                drawTexturedModalRectCustomSize(fromCenterX(res,  -50), fromCenterY(res,   20), sprite, 12, 3, 1, 8.5, 3,  9); //Left crosshair line

                String desc = I18n.format("desc.rssmc:taser.name");
                String ammo = drone.getAmmoLoaded() + "/" + drone.getAmmoLeft();
                font().drawString(desc, res.getScaledWidth() - 62, res.getScaledHeight() - 33, -1);
                font().drawString(ammo, res.getScaledWidth() - 62, res.getScaledHeight() - 24, ammo.equals("0/0") ? 0xFF0000 : -1);
                GlStateManager.color(1, 1, 1, 1);

                GlStateManager.disableBlend();
            }

            if (currentCam instanceof EntityCamera || currentCam instanceof EntityBlackEyeCam || !currentCam.isControlledBy(player()))  //TODO: draw "phone hacked" and "cam hacked" overlay if player is a defender
            {
                TextureAtlasSprite sprite = getSprite("rssmc:gui/overlay/camera_detail");
                GlStateManager.enableBlend();
                drawTexturedModalRect(22, 40, sprite, 96, 96);

                String type = currentCam.getType();
                sprite = getSprite("rssmc:gui/overlay/" + type + "_symbol");
                drawTexturedModalRect(50, 47, sprite, 40, 40);

                String desc = currentCam.getPositionDescription();
                String[] strings = desc.split("\n");
                int line = 0;
                for (String s : strings)
                {
                    int offX = font().getStringWidth(s) / 2;
                    font().drawString(s, 70 - offX, 92 + (line * 9), -1);
                    line += 1;
                }

                String text = I18n.format("desc.rssmc:cam_used_by.name");
                EntityPlayer user = currentCam.getUser();
                int xOffText = font().getStringWidth(text) / 2;
                font().drawString(text, 70 - xOffText, 115, -1);
                String name = user != null ? user.getName() : "Unknown";
                xOffText = (font().getStringWidth(name) / 2) - 5;
                font().drawString(name, 70 - xOffText, 124, -1);
                EnumOperator op = StatusController.getPlayersOperator(user);
                sprite = getSprite("rssmc:gui/operators/" + (op == null ? "unknown" : op.toString().toLowerCase(Locale.ENGLISH)));
                xOffText += 10;
                drawTexturedModalRect(70 - xOffText, 124, sprite, 8, 8);

                sprite = getSprite("rssmc:gui/overlay/camera_selectors");
                TextureAtlasSprite cross = getSprite("rssmc:gui/overlay/kill_cross");
                ArrayList<AbstractEntityCamera> cams = RSSWorldData.get(world()).getObservationManager().getObservableCameras(player());
                int amount = cams.size();
                double x = 70D - (((double) amount / 2D) * 8D) + (containsBlackEye(cams) && containsYokai(cams) ? 8D : containsBlackEye(cams) ? 4D : 0D);
                for (AbstractEntityCamera cam : cams)
                {
                    double uOff = currentCam.equals(cam) ? 4 : 0;
                    double vOff = currentCam instanceof EntityBlackEyeCam ? 0 : currentCam instanceof EntityYokaiCam ? 8 : 4;
                    double xOff = 8D * cams.indexOf(cam) + (containsYokayAndBlackEye(cams) ? 8D : containsBlackEye(cams) || containsTwitchDrone(cams) ? 4D : 0);
                    drawTexturedModalRectCustomSize(x + xOff, 32, sprite, 6, 6, uOff, vOff, 4 + uOff, 4 + vOff);
                    if (cam.isDead || (cam instanceof EntityCamera && ((EntityCamera)cam).isDestroyed()))
                    {
                        drawTexturedModalRect(x - 1 + xOff, 31, cross, 8, 8);
                    }
                }
                GlStateManager.disableBlend();
            }
            GlStateManager.popMatrix();
        }
    }

    private boolean renderJackalVisor(ScaledResolution res)
    {
        //TODO: render overlay
        //TODO: draw hint text for scanning
        return false;
    }

    private void renderCompass(ScaledResolution res)
    {
        if (!ConfigHandler.showCompass) { return; }
        if (!(mc().getRenderViewEntity() instanceof EntityPlayer) && !(mc().getRenderViewEntity() instanceof AbstractEntityCamera)) { return; }

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();

        TextureAtlasSprite sprite = getSprite("rssmc:gui/overlay/compass");
        GlStateManager.translate(fromCenterX(res, 116), res.getScaledHeight_double() - 11D, 0);
        float rotation = mc().getRenderViewEntity() instanceof EntityPlayer ? player().rotationYaw : mc().getRenderViewEntity().rotationYaw;
        GlStateManager.rotate(180 - rotation, 0, 0, 1);
        GlStateManager.translate(-9, -9, 0);
        drawTexturedModalRect(0, 0, sprite, 18, 18);

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void renderJackalTrackingTimer(ScaledResolution res, float partialTicks)
    {
        if (isJackalAndTracking() || isTracked())
        {
            GlStateManager.pushMatrix();

            double x = fromCenterX(res, - 115);
            double y = res.getScaledHeight_double() - 10;
            int colorFront = isJackalAndTracking() ? 0xCACACA : 0x880000;
            int colorBack = isJackalAndTracking() ? 0x5F5F5F : 0x570000;
            float factor = ((float) getTrackTime() + partialTicks) / 80F;
            drawDial(x, y, 5, colorBack, colorFront, factor, 100, true);

            GlStateManager.enableBlend();
            drawTexturedModalRectColored(x - 3.5, y - 3.5, getSprite("rssmc:gui/overlay/jackal_symbol"), 7, 7, colorBack);
            GlStateManager.disableBlend();

            colorBack = isJackalAndTracking() ? 0xCACACA : 0x880000;
            font().drawString(Integer.toString(getTrackingsLeft()), (int)x + 8, (int)y - 3, colorBack);

            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.popMatrix();
        }
    }

    private boolean renderHealthBar(ScaledResolution res)
    {
        if (ConfigHandler.customHealthBar && mc().getRenderViewEntity() instanceof EntityPlayer)
        {
            GlStateManager.pushMatrix();

            drawTexturedModalRect(res.getScaledWidth_double() / 2D - 91, res.getScaledHeight_double() - 38.2, getSprite("rssmc:black"), 81, 1);
            drawTexturedModalRect(res.getScaledWidth_double() / 2D - 91, res.getScaledHeight_double() - 30.2, getSprite("rssmc:black"), 81, 1);
            drawTexturedModalRect(res.getScaledWidth_double() / 2D - 91, res.getScaledHeight_double() - 38.2, getSprite("rssmc:black"),  1, 9);
            drawTexturedModalRect(res.getScaledWidth_double() / 2D - 11, res.getScaledHeight_double() - 38.2, getSprite("rssmc:black"),  1, 9);

            double health = player().getHealth();
            double length = 79D * (Math.min(health, 20D) / 20D);

            if (health < 6D)
            {
                float[] colors = getRedFadeFloat();
                GlStateManager.color(colors[0], colors[1], colors[2]);
            }

            drawTexturedModalRect(res.getScaledWidth_double() / 2D - 90, res.getScaledHeight_double() - 37.2, getSprite("rssmc:white"), length, 7);

            if (health > 20D)
            {
                double overHealth = health - 20D;
                length = 79D * (Math.min(overHealth, 20D) / 20D);
                GlStateManager.color(0, 0, 1);
                drawTexturedModalRect(res.getScaledWidth_double() / 2D - 90, res.getScaledHeight_double() - 37.2, getSprite("rssmc:white"), length, 7);
            }

            int color = health < 6D ? getRedFadeHex() : health > 20D ? 0x0000FF : 0x000000;
            double healthDisp = ConfigHandler.displayHealthBaseTwenty ? health : 100 * (health / 20D);
            String text = Integer.toString((int)(healthDisp + .5D));
            int x = res.getScaledWidth() / 2 - 90 + 40 - (font().getStringWidth(text) / 2);
            font().drawString(text, x, res.getScaledHeight() - 47, color);

            GlStateManager.color(1, 1, 1);

            GlStateManager.popMatrix();

            Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/gui/icons.png"));
            return true;
        }
        return false;
    }

    private void renderGameStatus(ScaledResolution res)
    {
        TileEntityGameManager manager = RSSWorldData.get(world()).getGameManager();
        if (StatusController.isPlayerInATeam(player()) && manager != null && manager.isGameRunning())
        {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();

            Team team = StatusController.getPlayersTeam(player());

            double xCenter = res.getScaledWidth_double() / 2;

            //Draw colored backgrounds
            TextureAtlasSprite sprite = getSprite("rssmc:gui/overlay/team_colors");
            double vMin1 = team.equals(RSSWorldData.get(world()).getTeams().getLeft())  ? 0 :  8;
            double vMax1 = team.equals(RSSWorldData.get(world()).getTeams().getLeft())  ? 8 : 16;
            double vMin2 = team.equals(RSSWorldData.get(world()).getTeams().getRight()) ? 0 :  8;
            double vMax2 = team.equals(RSSWorldData.get(world()).getTeams().getRight()) ? 8 : 16;
            drawTexturedModalRectCustomSize(xCenter - 48, 16, sprite, 32, 16,  0, vMin1, 16, vMax1);
            drawTexturedModalRectCustomSize(xCenter + 16, 16, sprite, 32, 16, 16, vMin2,  0, vMax2);

            //Draw empty player slots
            sprite = getSprite("rssmc:gui/overlay/player_slot");
            double xOff = 0;
            for (int i = 0; i < 5; i++)
            {
                double yOff = i == 0 ? 2 : 0;
                double size = i == 0 ? 20 : 16;
                drawTexturedModalRect(xCenter - 74 - xOff, 16 - yOff, sprite, size, size);
                drawTexturedModalRect(xCenter + 54 + xOff, 16, sprite, 16, 16);
                xOff += 18;
            }

            //Draw time and points
            int time = RSSWorldData.get(world()).getGameManager().getTimeLeftSeconds();
            int minutes = time / 60;
            int seconds = time - (60 * minutes);
            String text = minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
            font().drawString(text, (int) xCenter - (font().getStringWidth(text) / 2), 20, time <= 15 ? (ticks > 20 ? 0xFF0000 : -1) : -1);
            int points = manager.getPoints(team);
            font().drawString(Integer.toString(points), (int) xCenter - (points < 10 ? 26 : 32), 20, -1);
            points = manager.getPoints(StatusController.getEnemyTeam(team, world()));
            font().drawString(Integer.toString(points), (int) xCenter + 20, 20, -1);

            //Draw operator of local player
            EnumOperator op = StatusController.getPlayersOperator(player());
            sprite = op == null ? getSprite("rssmc:operators/unknown") : getSprite("rssmc:operators/" + op.toString().toLowerCase(Locale.ENGLISH));
            drawTexturedModalRectBlurred(xCenter - 74, 14, sprite, 20, 20, !StatusController.isPlayerAlive(player()) ? .75F : 1);

            //Draw health bar
            float health = player().getHealth();
            float healthFactor = Math.min(health / 20F, 1F);
            sprite = getSprite("rssmc:gui/overlay/health_usage_timer");
            if (health < 6F)
            {
                float[] colors = getRedFadeFloat();
                GlStateManager.color(colors[0], colors[1], colors[2]);
            }
            drawTexturedModalRectCustomSize(xCenter - 73, 36, sprite, 18D * healthFactor, 2, 0, 6, 16D * healthFactor, 8);
            if (health > 20F)
            {
                GlStateManager.color(0, 0, 1);
                healthFactor = (health - 20F) / 20F;
                drawTexturedModalRectCustomSize(xCenter - 73, 36, sprite, 18D * healthFactor, 2, 0, 6, 16D * healthFactor, 8);
                GlStateManager.color(1, 1, 1);
            }

            //Draw death indicator
            if (!StatusController.isPlayerAlive(player()))
            {
                sprite = getSprite("rssmc:gui/overlay/kill_cross");
                drawTexturedModalRect(xCenter - 74, 14, sprite, 20, 20);
            }

            //Draw operators of local player's team mates
            HashMap<UUID, WeakReference<EntityPlayer>> players = team.getPlayerEntityMap();
            players.remove(player().getUniqueID());
            xOff = 18;
            for (UUID uuid : players.keySet())
            {
                //Draw operator
                EntityPlayer player = players.get(uuid).get();
                op = StatusController.getPlayersOperator(player);
                sprite = op == null ? getSprite("rssmc:operators/unknown") : getSprite("rssmc:operators/" + op.toString().toLowerCase(Locale.ENGLISH));
                drawTexturedModalRectBlurred(xCenter - 74 - xOff, 16, sprite, 16, 16, !StatusController.isPlayerAlive(player) ? .75F : 1);

                //Draw health bar
                health = player().getHealth();
                healthFactor = Math.min(health / 20F, 1F);
                sprite = getSprite("rssmc:gui/overlay/health_usage_timer");
                if (health < 6F)
                {
                    float[] colors = getRedFadeFloat();
                    GlStateManager.color(colors[0], colors[1], colors[2]);
                }
                drawTexturedModalRectCustomSize(xCenter - 73 - xOff, 34, sprite, 14D * healthFactor, 2, 0, 6, 16D * healthFactor, 8);
                if (health > 20F)
                {
                    GlStateManager.color(0, 0, 1);
                    healthFactor = (health - 20F) / 20F;
                    drawTexturedModalRectCustomSize(xCenter - 73 - xOff, 34, sprite, 14D * healthFactor, 2, 0, 6, 16D * healthFactor, 8);
                    GlStateManager.color(1, 1, 1);
                }

                //Draw death indicator
                if (!StatusController.isPlayerAlive(player))
                {
                    sprite = getSprite("rssmc:gui/overlay/kill_cross");
                    drawTexturedModalRect(xCenter - 74 - xOff, 16, sprite, 16, 16);
                }
                xOff += 18;
            }

            //Draw enemy operators
            team = StatusController.getEnemyTeam(team, world());
            players = team.getPlayerEntityMap();
            xOff = 0;
            for (UUID uuid : players.keySet())
            {
                //Draw operator
                EntityPlayer player = players.get(uuid).get();
                op = StatusController.isOperatorSpotted(player, StatusController.getEnemyTeam(team, world())) ? StatusController.getPlayersOperator(player) : null;
                sprite = op == null ? getSprite("rssmc:operators/unknown") : getSprite("rssmc:operators/" + op.toString().toLowerCase(Locale.ENGLISH));
                drawTexturedModalRectBlurred(xCenter + 54 + xOff, 16, sprite, 16, 16, !StatusController.isPlayerAlive(player) ? .75F : 1);

                //Draw death indicator
                if (!StatusController.isPlayerAlive(player))
                {
                    sprite = getSprite("rssmc:gui/overlay/kill_cross");
                    drawTexturedModalRect(xCenter + 54 + xOff, 16, sprite, 16, 16);
                }
                xOff += 18;
            }

            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    private boolean renderScoreboard(ScaledResolution res)
    {
        if (StatusController.isPlayerInATeam(player()) && StatusController.doTeamsExist(world()))
        {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();

            double xCenter = res.getScaledWidth_double() / 2;
            double yCenter = res.getScaledHeight_double() / 2;
            int textX = (int) xCenter;
            int textY = (int) yCenter;

            boolean blue = StatusController.getPlayersTeam(player()).equals(RSSWorldData.get(world()).getTeams().getLeft());

            //Draw background
            TextureAtlasSprite sprite = getSprite("rssmc:gui/overlay/player_list_" + (blue ? "blue" : "orange"));
            drawTexturedModalRectCustomSize(xCenter - 185.5, yCenter - 104, sprite,  90, 188,      0, 0,  5.625, 11.75);
            drawTexturedModalRectCustomSize(xCenter -  95.5, yCenter - 104, sprite, 140, 188,  5.625, 0, 7.4375, 11.75);
            drawTexturedModalRectCustomSize(xCenter +  44.5, yCenter - 104, sprite, 137, 188, 7.4375, 0,     16, 11.75);

            //Draw team names
            font().drawString(I18n.format("desc.rssmc:team_blue").toUpperCase(Locale.ENGLISH),   textX - 132, textY + (blue ? -77 :   7), 0x0036ff);
            font().drawString(I18n.format("desc.rssmc:team_orange").toUpperCase(Locale.ENGLISH), textX - 132, textY + (blue ?   7 : -77), 0xff5500);

            GlStateManager.scale(.8, .8, .8);

            //Draw game description
            font().drawString("Haus", textX - 164, textY - 93, -1);
            font().drawString("TDM - Bombe", textX - 164, textY - 83, -1);

            GlStateManager.scale(1.25, 1.25, 1.25);

            GlStateManager.scale(.6, .6, .6);

            //Draw table categories
            String text = I18n.format("desc.rssmc:points.name");
            font().drawString(text, textX + 212 - (font().getStringWidth(text)  / 2), textY - 35, -1);
            text = I18n.format("desc.rssmc:kills.name");
            font().drawString(text, textX + 276 - (font().getStringWidth(text)  / 2), textY - 35, -1);
            text = I18n.format("desc.rssmc:assists.name");
            font().drawString(text, textX + 327 - (font().getStringWidth(text)  / 2), textY - 35, -1);
            text = I18n.format("desc.rssmc:deaths.name");
            font().drawString(text, textX + 378 - (font().getStringWidth(text)  / 2), textY - 35, -1);
            text = I18n.format("desc.rssmc:connection.name");
            font().drawString(text, textX + 430 - (font().getStringWidth(text)  / 2), textY - 35, -1);

            text = I18n.format("desc.rssmc:points.name");
            font().drawString(text, textX + 212 - (font().getStringWidth(text)  / 2), textY + 105, -1);
            text = I18n.format("desc.rssmc:kills.name");
            font().drawString(text, textX + 276 - (font().getStringWidth(text)  / 2), textY + 105, -1);
            text = I18n.format("desc.rssmc:assists.name");
            font().drawString(text, textX + 327 - (font().getStringWidth(text)  / 2), textY + 105, -1);
            text = I18n.format("desc.rssmc:deaths.name");
            font().drawString(text, textX + 378 - (font().getStringWidth(text)  / 2), textY + 105, -1);
            text = I18n.format("desc.rssmc:connection.name");
            font().drawString(text, textX + 430 - (font().getStringWidth(text)  / 2), textY + 105, -1);

            GlStateManager.scale(5D/3D, 5D/3D, 5D/3D);

            //Draw operators of local player's team
            Team team = StatusController.getPlayersTeam(player());
            ArrayList<EntityPlayer> players = team.getPlayersForScoreboard();
            int yOff = 18;
            for (EntityPlayer player : players)
            {
                String name = player.getName();
                EnumOperator op = StatusController.getPlayersOperator(player);
                sprite = op == null ? getSprite("rssmc:operators/unknown") : getSprite("rssmc:operators/" + op.toString().toLowerCase(Locale.ENGLISH));
                drawTexturedModalRect(xCenter - 5, yCenter - 82 + yOff, sprite, 10, 10);

                int color = StatusController.isPlayerAlive(player()) ? -1 : 0xC8C8C8;
                font().drawString(name, textX - 131, textY - 81 + yOff, color);

                String s = Integer.toString(StatusController.getPoints(player()));
                font().drawString(s, textX + 26 - font().getStringWidth(s) / 2, textY - 81 + yOff, color);
                s = Integer.toString(StatusController.getKills(player));
                font().drawString(s, textX + 64 - font().getStringWidth(s) / 2, textY - 81 + yOff, color);
                s = Integer.toString(StatusController.getAssists(player));
                font().drawString(s, textX + 95 - font().getStringWidth(s) / 2, textY - 81 + yOff, color);
                s = Integer.toString(StatusController.getDeaths(player));
                font().drawString(s, textX + 126 - font().getStringWidth(s) / 2, textY - 81 + yOff, color);
                s = Integer.toString(Minecraft.getMinecraft().player.connection.getPlayerInfo(name).getResponseTime());
                font().drawString(s, textX + 157 - font().getStringWidth(s) / 2, textY - 81 + yOff, color);
                GlStateManager.color(1, 1, 1, 1);

                yOff += 12;
            }

            //Draw operators of enemies
            team = StatusController.getEnemyTeam(team, world());
            players = team.getPlayersForScoreboard();
            yOff = 0;
            for (EntityPlayer player : players)
            {
                String name = player.getName();
                EnumOperator op = StatusController.isOperatorSpotted(player, StatusController.getEnemyTeam(team, world())) ? StatusController.getPlayersOperator(player) : null;
                sprite = op == null ? getSprite("rssmc:operators/unknown") : getSprite("rssmc:operators/" + op.toString().toLowerCase(Locale.ENGLISH));
                drawTexturedModalRect(xCenter - 5, yCenter + 20 + yOff, sprite, 10, 10);

                int color = StatusController.isPlayerAlive(player()) ? -1 : 0xC8C8C8;
                font().drawString(name, textX - 131, textY + 21 + yOff, color);

                String s = Integer.toString(StatusController.getPoints(player()));
                font().drawString(s, textX + 26 - font().getStringWidth(s) / 2, textY + 21 + yOff, color);
                s = Integer.toString(StatusController.getKills(player));
                font().drawString(s, textX + 64 - font().getStringWidth(s) / 2, textY + 21 + yOff, color);
                s = Integer.toString(StatusController.getAssists(player));
                font().drawString(s, textX + 95 - font().getStringWidth(s) / 2, textY + 21 + yOff, color);
                s = Integer.toString(StatusController.getDeaths(player));
                font().drawString(s, textX + 126 - font().getStringWidth(s) / 2, textY + 21 + yOff, color);
                s = Integer.toString(Minecraft.getMinecraft().player.connection.getPlayerInfo(name).getResponseTime());
                font().drawString(s, textX + 157 - font().getStringWidth(s) / 2, textY + 21 + yOff, color);
                GlStateManager.color(1, 1, 1, 1);

                yOff += 12;
            }

            //Draw team score
            GlStateManager.scale(2.5, 2.5, 2.5);

            TileEntityGameManager manager = RSSWorldData.get(world()).getGameManager();
            team = StatusController.getPlayersTeam(player());
            String s = Integer.toString(manager.getPoints(team));
            font().drawString(s, 36, 37, -1);
            team = StatusController.getEnemyTeam(team, world());
            s = Integer.toString(manager.getPoints(team));
            font().drawString(s, 36, 70, -1);

            GlStateManager.scale(.4, .4, .4);

            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
            return true;
        }
        return false;
    }

    private void renderFlashingText(ScaledResolution res)
    {
        if (currentFlashText == null || currentFlashText.equals("")) { return; }
        GlStateManager.pushMatrix();
        String text = I18n.format(currentFlashText);
        int textX = (int) fromCenterX(res, - (font().getStringWidth(text) / 2));
        int textY = (int) fromCenterY(res, 12);
        font().drawString(text, textX, textY, getRedFadeHex());
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.popMatrix();
    }

    private void renderCameraEffects(ScaledResolution res, float partialTicks)
    {
        if (currentCam != null)
        {
            GlStateManager.pushMatrix();
            GlStateManager.depthMask(false);
            if (currentCam.isDead || isDestroyedCam() || isDisabledYokai() || isJammedDrone())
            {
                TextureAtlasSprite sprite = getSprite("rssmc:gui/overlay/jammer_filter");
                double width = res.getScaledWidth_double();
                double height = res.getScaledHeight_double();
                double vFactor = Math.min(height / width, 1);
                double timeFactor = ((double) jamTicksMove + partialTicks) / 80D;
                double stretchFactor = ((double) jamTicksStretch + (jamStretchReverse ? -partialTicks : partialTicks)) / 30D;
                double minU = 1.5D * stretchFactor;
                double maxU = 16D - (1.5D * stretchFactor);
                double maxV = 16D * vFactor;
                maxV *= (1D - timeFactor);
                double y = height * timeFactor;
                drawTexturedModalRectCustomSize(0, y, sprite, width, height - y, minU,    0, maxU,          maxV);
                drawTexturedModalRectCustomSize(0, 0, sprite, width,          y, minU, maxV, maxU, 16D * vFactor);
            }
            else if (currentCam instanceof EntityBlackEyeCam)
            {
                drawTexturedModalRect(0, 0, getSprite("rssmc:gui/overlay/black_eye_filter"), res.getScaledWidth(), res.getScaledHeight());
            }
            else if (currentCam instanceof EntityYokaiCam)
            {
                drawTexturedModalRectTiled(0, 0, getSprite("rssmc:gui/overlay/yokai_filter"), res.getScaledWidth(), res.getScaledHeight(), 16, 16);
            }
            GlStateManager.depthMask(true);
            GlStateManager.popMatrix();
        }
    }

    private void renderEffectOverlay(ScaledResolution res, float partialTicks)
    {
        if (currentEffect != null)
        {
            GlStateManager.pushMatrix();
            GlStateManager.disableDepth();

            TextureAtlasSprite sprite = getSprite("rssmc:gui/overlay/effect_" + currentEffect.toString().toLowerCase(Locale.ENGLISH));

            if (currentEffect.shouldFade())
            {
                float factor = ((float)(world().getTotalWorldTime() - lastEffectStamp) + partialTicks) / currentEffect.getDuration();
                GlStateManager.color(1, 1, 1, 1F - factor);
            }

            if (currentEffect.shouldTileTexture())
            {
                drawTexturedModalRectTiled(0, 0, sprite, res.getScaledWidth_double(), res.getScaledHeight_double(), currentEffect.getTileSize(), currentEffect.getTileSize());
            }
            else
            {
                drawTexturedModalRect(0, 0, sprite, res.getScaledWidth_double(), res.getScaledHeight_double());
            }

            GlStateManager.color(1, 1, 1, 1);

            GlStateManager.enableDepth();
            GlStateManager.popMatrix();

            Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/gui/icons.png"));
        }
    }

    private void renderPointInfos(ScaledResolution res)
    {
        if (ConfigHandler.battleMode && ConfigHandler.showPointInfo && !pointInfo.isEmpty())
        {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();

            double y = (res.getScaledHeight() / 2) * (1D / .75D);
            TextureAtlasSprite sprite = getSprite("rssmc:gui/overlay/point_info");

            ListIterator<PointInfo> it = pointInfo.listIterator(pointInfo.size());
            while (it.hasPrevious())
            {
                PointInfo info = it.previous();
                GlStateManager.scale(.75, .75, .75);
                double edgeXDouble = res.getScaledWidth_double() * (1D / .75D);
                int edgeX = (int)edgeXDouble;

                float age = (float)(world().getTotalWorldTime() - info.getTimestamp()) + mc().getRenderPartialTicks();
                age = Math.min(age, 80F);
                float color = age < 60F ? 1 : 1F - (Math.min(age - 60F, 20F) / 15F);
                color = MathHelper.clamp(color, .1F, 1);
                GlStateManager.color(1, 1, 1, color);
                drawTexturedModalRectCustomSize(edgeXDouble -  64, y, sprite, 64, 24, 0, 0, 16, 6);
                drawTexturedModalRectCustomSize(edgeXDouble - 128, y, sprite, 64, 24, 0, 0, 16, 6);

                String points = (info.getPoints() >= 0 ? "+" : "") + Integer.toString(info.getPoints());
                int textColor = info.getPoints() >= 0 ? ClientUtils.getHexColorFromRGBAFloats(.7686F, .7686F, 0, color) : ClientUtils.getHexColorFromRGBAFloats(1, 0, 0, color);
                font().drawString(points, edgeX - font().getStringWidth(points), (int)y + 3, textColor/*info.getPoints() > 0 ? 0xC4C400 : 0x0FF0000*/);
                font().drawString(info.getText(), edgeX - font().getStringWidth(info.getText()), (int)y + 14, textColor/*info.getPoints() > 0 ? 0xC4C400 : 0x0FF0000*/);

                GlStateManager.scale(1D / .75D, 1D / .75D, 1D / .75D);
                GlStateManager.scale(.5, .5, .5);

                int yText = (int)((y + 26) * .75 * 2D);
                edgeXDouble = edgeXDouble * .75D * 2D;
                for (int i = 0; i < info.getBonus().length; i++)
                {
                    String text = info.getBonusText()[i];
                    int p = info.getBonus()[i];
                    edgeX = (int)edgeXDouble - 18;
                    points = (p >= 0 ? "+" : "") + Integer.toString(p);
                    font().drawString(points, edgeX, yText, -1);
                    font().drawString(text, edgeX - font().getStringWidth(text) - 2, yText, -1);

                    yText += 10;
                }

                GlStateManager.color(1, 1, 1, 1);
                GlStateManager.scale(2, 2, 2);

                y += 30 + (info.getBonus().length * 7);
            }

            PointInfo info = pointInfo.get(pointInfo.size() - 1);
            if (world().getTotalWorldTime() - info.getTimestamp() < 20)
            {
                int xText = res.getScaledWidth() / 2 + 20;
                int yText = res.getScaledHeight() / 2 - 27;

                int points = info.getPoints();
                for (int i : info.getBonus()) { points += i; }
                String text = (points >= 0 ? "+" : "") + Integer.toString(points);
                font().drawString(text, xText, yText, points >= 0 ? 0xFFFFFF : 0xFF0000);
            }

            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    //DESCRIPTION: Helper methods
    private boolean containsYokayAndBlackEye(ArrayList<AbstractEntityCamera> cams)
    {
        return containsYokai(cams) && containsBlackEye(cams);
    }

    private boolean containsYokai(ArrayList<AbstractEntityCamera> cams)
    {
        for (AbstractEntityCamera cam : cams)
        {
            if (cam instanceof EntityYokaiCam)
            {
                return true;
            }
        }
        return false;
    }

    private boolean containsBlackEye(ArrayList<AbstractEntityCamera> cams)
    {
        for (AbstractEntityCamera cam : cams)
        {
            if (cam instanceof EntityBlackEyeCam)
            {
                return true;
            }
        }
        return false;
    }

    private boolean containsTwitchDrone(ArrayList<AbstractEntityCamera> cams)
    {
        for (AbstractEntityCamera cam : cams)
        {
            if (cam instanceof EntityTwitchDroneCam)
            {
                return true;
            }
        }
        return false;
    }

    private boolean isDestroyedCam()
    {
        return currentCam instanceof EntityCamera && ((EntityCamera)currentCam).isDestroyed();
    }

    private boolean isDisabledYokai()
    {
        return currentCam instanceof EntityYokaiCam && ((EntityYokaiCam)currentCam).getDrone().isDisabled();
    }

    private boolean isJammedDrone()
    {
        return currentCam instanceof IJammed.Entity && ((IJammed.Entity)currentCam).isJammed();
    }

    private int getAmmoInInventory(EnumGun gun)
    {
        int ammoAmount = 0;
        ArrayList<ItemStack> ammo = new ArrayList<>();
        ItemStack reference = gun != null ? gun.getMagazineStack(true) : null;
        if (reference == null) { return 0; }
        for (ItemStack stack : player().inventory.mainInventory)
        {
            if (stack != null && stack.getItem() == reference.getItem() && stack.getMetadata() == reference.getMetadata())
            {
                ammo.add(stack);
            }
        }
        for (ItemStack stack : ammo)
        {
            if (stack.getItem() instanceof ItemAmmo)
            {
                ammoAmount += stack.stackSize;
            }
            else
            {
                ammoAmount += stack.hasTagCompound() ? (stack.getTagCompound().getInteger("currentAmmo") * stack.stackSize) : 0;
            }
        }
        return ammoAmount;
    }

    private boolean isJackalAndTracking()
    {
        return StatusController.getPlayersOperator(player()) == EnumOperator.JACKAL && GadgetHandler.getHandlerForPlayer(player()).isTracking();
    }

    private boolean isTracked()
    {
        return player().getEntityData().getBoolean("tracked") && player().getEntityData().getInteger("trackingsLeft") > 0;
    }

    private int getTrackTime()
    {
        if (isJackalAndTracking())
        {
            return GadgetHandler.getHandlerForPlayer(player()).getTrackTimer();
        }
        return (int) (world().getTotalWorldTime() - player().getEntityData().getLong("timestamp"));
    }

    private int getTrackingsLeft()
    {
        if (isJackalAndTracking())
        {
            return GadgetHandler.getHandlerForPlayer(player()).getTrackingsLeft();
        }
        return 0;
    }

    //DESCRIPTION: Convenience methods
    private Minecraft mc()
    {
        return Minecraft.getMinecraft();
    }

    private EntityPlayer player()
    {
        return mc().player;
    }

    private World world() { return mc().world; }

    private FontRenderer font()
    {
        return mc().fontRendererObj;
    }

    private TextureAtlasSprite getSprite(String name)
    {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(name);
    }

    private double fromCenterX(ScaledResolution res, double offX)
    {
        return res.getScaledWidth_double() / 2D + offX;
    }

    private double fromCenterY(ScaledResolution res, double offY)
    {
        return res.getScaledHeight_double() / 2D + offY;
    }

    private static void drawTexturedModalRect(double x, double y, TextureAtlasSprite sprite, double width, double height)
    {
        drawTexturedModalRectCustomSize(x, y, sprite, width, height, 0, 0, 16, 16);
    }

    private static void drawTexturedModalRectBlurred(double x, double y, TextureAtlasSprite sprite, double width, double height, float alpha)
    {
        drawTexturedModalRectColored(x, y, sprite, width, height, 1, 1, 1, alpha);
    }

    private static void drawTexturedModalRectColored(double x, double y, TextureAtlasSprite sprite, double width, double height, int color)
    {
        float[] colors = ClientUtils.getRGBAFloatArrayFromHexColor(color);
        drawTexturedModalRectColored(x, y, sprite, width, height, colors[0], colors[1], colors[2], 1);
    }

    private static void drawTexturedModalRectColored(double x, double y, TextureAtlasSprite sprite, double width, double height, float red, float green, float blue, float alpha)
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        vertexbuffer.pos(        x, y + height, 0).tex(sprite.getMinU(), sprite.getMaxV()).color(red, green, blue, alpha).endVertex();
        vertexbuffer.pos(x + width, y + height, 0).tex(sprite.getMaxU(), sprite.getMaxV()).color(red, green, blue, alpha).endVertex();
        vertexbuffer.pos(x + width,          y, 0).tex(sprite.getMaxU(), sprite.getMinV()).color(red, green, blue, alpha).endVertex();
        vertexbuffer.pos(        x,          y, 0).tex(sprite.getMinU(), sprite.getMinV()).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
    }

    private static void drawTexturedModalRectCustomSize(double x, double y, TextureAtlasSprite sprite, double w, double h, double minU, double minV, double maxU, double maxV)
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos(    x, y + h, 0).tex(sprite.getInterpolatedU(minU), sprite.getInterpolatedV(maxV)).endVertex();
        vertexbuffer.pos(x + w, y + h, 0).tex(sprite.getInterpolatedU(maxU), sprite.getInterpolatedV(maxV)).endVertex();
        vertexbuffer.pos(x + w,     y, 0).tex(sprite.getInterpolatedU(maxU), sprite.getInterpolatedV(minV)).endVertex();
        vertexbuffer.pos(    x,     y, 0).tex(sprite.getInterpolatedU(minU), sprite.getInterpolatedV(minV)).endVertex();
        tessellator.draw();
    }

    private static void drawTexturedModalRectTiled(double x, double y, TextureAtlasSprite sprite, double width, double height, double tileWidth, double tileHeight)
    {
        double xOrigin = x;
        double yOrigin = y;
        for (x = xOrigin; x < width; x = x + tileWidth)
        {
            for (y = yOrigin; y < height; y += tileHeight)
            {
                drawTexturedModalRect(x, y, sprite, tileWidth, tileHeight);
            }
        }
    }

    private static void drawDial(double x, double y, double radius, int colorBack, int colorFront, float value, int segments, boolean outline)
    {
        if (outline)
        {
            drawCircle(x, y, radius + .5, colorBack, 1, segments);
        }
        drawCircle(x, y, radius, colorFront, 1, segments);
        drawCircle(x, y, radius, colorBack, 1F - value, segments);
        GlStateManager.color(1, 1, 1, 1);
    }

    private static void drawCircle(double x, double y, double radius, int color, float value, int sides)
    {
        double TWICE_PI = Math.PI * 2;

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        float[] colors = ClientUtils.getRGBAFloatArrayFromHexColor(color);
        GlStateManager.color(colors[0], colors[1], colors[2], 1);

        Tessellator tess = Tessellator.getInstance();
        VertexBuffer buffer = tess.getBuffer();

        buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION);
        buffer.pos(x, y, 0).endVertex();

        for(int i = 0; i <= (int) ((float)sides * value); i++)
        {
            double angle = (TWICE_PI * i / sides) + Math.toRadians(180);
            buffer.pos(x + Math.sin(angle) * radius, y + Math.cos(angle) * radius, 0).endVertex();
        }

        tess.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    private float[] getRedFadeFloat()
    {
        float time = ticks < 20 ? (float)(ticks) + mc().getRenderPartialTicks() : (float)(20 - (ticks - 20)) - mc().getRenderPartialTicks();
        time = MathHelper.clamp(time, 0F, 20F);
        float gb = (time + 1) / 20;
        if (gb > 1F) { gb = 1F; }
        return new float[] { 1, gb, gb, 1 };
    }

    private int getRedFadeHex()
    {
        float[] colors = getRedFadeFloat();
        return ClientUtils.getHexColorFromRGBAFloats(colors[0], colors[1], colors[2], colors[3]);
    }

    //DESCRIPTION: Effect setters
    public void setLastKillStamp(long stamp)
    {
        this.lastKillStamp = stamp;
    }

    public void setCurrentScreenEffect(EnumScreenEffect effect)
    {
        this.currentEffect = effect;
        lastEffectStamp = world().getTotalWorldTime();
    }

    public void setCurrentFlashText(String text)
    {
        this.currentFlashText = text;
        lastFlashTextStamp = world().getTotalWorldTime();
    }

    public void addPointInfo(String text, int points, Object... bonus)
    {
        pointInfo.add(new PointInfo(world().getTotalWorldTime(), text, points, bonus));
        if (pointInfo.size() > 5)
        {
            pointInfo.remove(0);
        }
    }
}