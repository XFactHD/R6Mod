package xfacthd.r6mod.client.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.api.capability.ICapabilityDBNO;
import xfacthd.r6mod.api.entity.ICameraEntity;
import xfacthd.r6mod.api.client.ICameraOverlay;
import xfacthd.r6mod.api.entity.IUsageTimeEntity;
import xfacthd.r6mod.api.interaction.*;
import xfacthd.r6mod.api.item.IUsageTimeItem;
import xfacthd.r6mod.api.tileentity.IUsageTimeTile;
import xfacthd.r6mod.client.R6Client;
import xfacthd.r6mod.client.gui.overlay.info.KillfeedEntry;
import xfacthd.r6mod.client.gui.overlay.info.PointsEntry;
import xfacthd.r6mod.client.util.data.ClientCameraManager;
import xfacthd.r6mod.client.util.render.*;
import xfacthd.r6mod.common.capability.*;
import xfacthd.r6mod.common.data.EnumCamera;
import xfacthd.r6mod.common.data.effects.AbstractEffect;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGun;
import xfacthd.r6mod.common.items.ItemRestock;
import xfacthd.r6mod.common.items.gun.ItemGun;
import xfacthd.r6mod.common.items.gun.ItemMagazine;
import xfacthd.r6mod.common.items.material.ItemBullet;
import xfacthd.r6mod.common.util.Config;

import java.util.*;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = R6Mod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class OverlayEventHandler
{
    private static final ResourceLocation RESTOCK_TIMER = new ResourceLocation(R6Mod.MODID, "textures/gui/overlay/restock_timer.png");
    private static final ResourceLocation BLEEDOUT_TIMER_BG = new ResourceLocation(R6Mod.MODID, "textures/gui/overlay/dbno_timer_empty.png");
    private static final ResourceLocation BLEEDOUT_TIMER = new ResourceLocation(R6Mod.MODID, "textures/gui/overlay/dbno_timer_full.png");
    private static final int MAX_POINT_ENTRIES = 4;
    private static final int MAX_KILLFEED_ENTRIES = 6;

    private static final Map<EnumCamera, ICameraOverlay<ICameraEntity<?>>> camOverlays = new HashMap<>();

    @SubscribeEvent
    public static void onRenderOverlayPre(final RenderGameOverlayEvent.Pre event)
    {
        if (player() == null) { return; }

        RenderSystem.enableTexture();

        if (event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS)
        {
            ItemStack stack = player().getHeldItemMainhand();
            if (stack.getItem() instanceof ItemGun)
            {
                boolean aiming = CapabilityGun.getFrom(stack).isAiming();
                if (aiming) { event.setCanceled(true); }
            }

            if (drawCameraOverlays(event.getMatrixStack())) { event.setCanceled(true); }
            if (drawBleedoutTimer(event.getMatrixStack())) { event.setCanceled(true); }
        }
        else if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR)
        {
            if (inCamera()) { event.setCanceled(true); }
        }
        else if (event.getType() == RenderGameOverlayEvent.ElementType.HELMET)
        {
            if (inLocalPlayer()) { drawEffectOverlays(event.getMatrixStack()); }
        }
    }

    @SubscribeEvent
    public static void onRenderOverlayPost(final RenderGameOverlayEvent.Post event)
    {
        if (player() == null) { return; }

        RenderSystem.enableTexture();

        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR)
        {
            drawGunOverlays(event.getMatrixStack());
            drawDebuffIcons(event.getMatrixStack());
            drawPointInfo(event.getMatrixStack());
            drawKillfeed(event.getMatrixStack());
            drawRestockProgress(event.getMatrixStack());
        }
        else if (event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS)
        {
            RenderSystem.disableBlend();
            if (Config.INSTANCE.usageTime)
            {
                drawTimeOverlaysForItems(event.getMatrixStack());
                drawTimeOverlaysForTiles(event.getMatrixStack());
                drawTimeOverlaysForEntities(event.getMatrixStack());
            }
            drawDbnoPickupTimer(event.getMatrixStack());
        }
        else if (event.getType() == RenderGameOverlayEvent.ElementType.HEALTH)
        {
            drawOverhealHearts(event.getMatrixStack());
        }
    }

    /*
     * Stuff drawn in Pre event
     */

    private static boolean drawCameraOverlays(MatrixStack matrix)
    {
        if (!inCamera()) { return false; }

        Entity viewEntity = mc().getRenderViewEntity();
        if (viewEntity instanceof ICameraEntity)
        {
            ICameraEntity<?> camera = (ICameraEntity<?>) viewEntity;

            if (!camOverlays.containsKey(camera.getCameraType()))
            {
                //noinspection unchecked
                camOverlays.put(camera.getCameraType(), (ICameraOverlay<ICameraEntity<?>>) camera.createOverlayRenderer());
            }
            camOverlays.get(camera.getCameraType()).drawOverlay(camera, matrix);

            drawCameraList(matrix);

            return camOverlays.get(camera.getCameraType()).hideCrosshair();
        }
        return false;
    }

    private static void drawCameraList(MatrixStack matrix)
    {
        //Get ClientCameraManager
        ClientCameraManager cams = R6Client.getCameraManager();

        //Get relevant data
        List<EnumCamera> activeCats = cams.getCategories();
        int catCount = activeCats.size();
        EnumCamera activeCat = cams.getActiveCamera().getCameraType();
        int camCount = cams.getActiveCategorySize();
        int idx = cams.getActiveIndex();

        //Sizes
        final float width = 122; //Width of the whole widget
        final float height = 32; //Height of the whole widget
        final float upperHeight = 20; //Height of the category part
        final float borderOffset = 20; //Offset from the screen border
        final float iconSize = 16; //Size of the category icons
        final float selectSize = 8; //Size of the selection icons
        final float dist = 2; //Distance between icons

        //Base coords
        float x = window().getScaledWidth() - width - borderOffset;
        float y = window().getScaledHeight() - height - borderOffset;

        //Draw background
        mc().getTextureManager().bindTexture(new ResourceLocation(R6Mod.MODID, "textures/gui/overlay/camera_list.png"));
        TextureDrawer.drawTexture(matrix, x, y, 122, 32, 0F, 1F, 0F, 1F);

        //Draw category icons
        float xIcon = (x + (width / 2F)) - (((catCount * iconSize) + ((catCount - 1) * dist)) / 2F);
        mc().getTextureManager().bindTexture(new ResourceLocation(R6Mod.MODID, "textures/gui/overlay/camera_types.png"));
        float uIncr = 1F / (float)EnumCamera.values().length;
        for (int i = 0; i < catCount; i++, xIcon += (iconSize + dist))
        {
            EnumCamera cat = activeCats.get(i);
            float uMin = uIncr * (float)cat.ordinal();
            float vMin = (cat == activeCat) ? .5F : 0F;
            TextureDrawer.drawTexture(matrix, xIcon, y + dist, iconSize, iconSize, uMin, uMin + uIncr, vMin, vMin + .5F);
        }

        //Draw selection icons
        xIcon = (x + (width / 2F)) - (((camCount * selectSize) + ((camCount - 1) * dist)) / 2F);
        mc().getTextureManager().bindTexture(new ResourceLocation(R6Mod.MODID, "textures/gui/overlay/camera_selector.png"));
        for (int i = 0; i < camCount; i++, xIcon += (selectSize + dist))
        {
            float uMin = (i == idx) ? .5F : 0F;
            TextureDrawer.drawTexture(matrix, xIcon, y + upperHeight + dist, selectSize, selectSize, uMin, uMin + .5F, 0F, 1F);
        }
    }

    private static void drawEffectOverlays(MatrixStack matrix)
    {
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        for (AbstractEffect effect : ClientEffectEventHandler.getEffects())
        {
            effect.drawEffect(matrix);
        }
    }

    private static boolean drawBleedoutTimer(MatrixStack matrix)
    {
        Entity entity = mc().renderViewEntity;
        if (!(entity instanceof PlayerEntity)) { return false; }

        PlayerEntity player = (PlayerEntity)entity;
        ICapabilityDBNO dbno = CapabilityDBNO.getFrom(player);
        if (!dbno.isDBNO()) { return false; }

        float progress = dbno.getTimeLeftFactor();

        float x = window().getScaledWidth() / 2F;
        float y = window().getScaledHeight() / 2F;
        float size = 20;

        RenderSystem.enableBlend();
        mc().getTextureManager().bindTexture(BLEEDOUT_TIMER_BG);
        TextureDrawer.drawTexture(matrix, x, y, size, size, 0, 0, 1, 1);
        UIRenderHelper.drawProgressCircle(matrix, x, y, size, 32, 0, 3, progress, Color4i.WHITE, BLEEDOUT_TIMER);

        return true;
    }

    /*
     * Stuff drawn in Post event
     */

    private static void drawGunOverlays(MatrixStack matrix)
    {
        ItemStack stack = player().getHeldItemMainhand();
        if (stack.getItem() instanceof ItemGun)
        {
            int count = CapabilityGun.getFrom(stack).getLoadedBullets();
            int maxCount = CapabilityGun.getFrom(stack).getGun().getMagCapacity();
            int ammoInInv = findAdditionalAmmo(player(), CapabilityGun.getFrom(stack).getGun());

            String text = Integer.toString(ammoInInv);
            int x = window().getScaledWidth() - font().getStringWidth(text) - 10;
            int y = window().getScaledHeight() - font().FONT_HEIGHT - 10;
            int color = ammoInInv == 0 ? 0xFFFF0000 : 0xFFFFFFFF;
            font().drawString(matrix, text, x, y, color);

            x -= font().getStringWidth("/");
            font().drawString(matrix, "/", x, y, color);

            text = Integer.toString(count);
            x -= font().getStringWidth(text);
            color = count < (maxCount * 0.3F) ? 0xFFFF0000 : 0xFFFFFFFF;
            font().drawString(matrix, text, x, y, color);
        }
    }

    private static void drawDebuffIcons(MatrixStack matrix)
    {
        int x = window().getScaledWidth() / 2 + 95;
        int y = window().getScaledHeight() - 17;

        for (AbstractEffect effect : ClientEffectEventHandler.getEffects())
        {
            if (effect.showIcon())
            {
                effect.drawIcon(matrix, x, y);
                x += 16;
            }
        }
    }

    private static void drawPointInfo(MatrixStack matrix)
    {
        int x = window().getScaledWidth();
        int y = window().getScaledHeight() / 2;

        Map<Long, PointsEntry> entries = ClientPointHandler.getPointEntries();
        int start = Math.max(entries.size() - MAX_POINT_ENTRIES, 0); //Only draw the last MAX_POINT_ENTRIES entries
        int i = 0;
        for (PointsEntry entry : entries.values())
        {
            if (i >= start) { y = entry.draw(matrix, x, y); }
            i++;
        }
    }

    private static void drawKillfeed(MatrixStack matrix)
    {
        int x = window().getScaledWidth();
        int y = window().getScaledHeight() / 6;

        Map<Long, KillfeedEntry> entries = ClientPointHandler.getKillfeed();
        int start = Math.max(entries.size() - MAX_KILLFEED_ENTRIES, 0); //Only draw the last MAX_KILLFEED_ENTRIES entries
        int i = 0;
        for (KillfeedEntry entry : entries.values())
        {
            if (i >= start) { y = entry.draw(matrix, x, y); }
            i++;
        }
    }

    private static void drawRestockProgress(MatrixStack matrix)
    {
        int y = window().getScaledHeight() - 16 - 3;
        for (int slot = 0; slot < 9; slot ++)
        {
            int x = (window().getScaledWidth() / 2) - 90 + slot * 20 + 2;
            ItemStack stack = player().inventory.getStackInSlot(slot);
            if (stack.getItem() instanceof ItemRestock)
            {
                ItemRestock item = (ItemRestock)stack.getItem();

                float progress = item.getRestockProgress(stack, world());
                if (progress > 0F)
                {
                    float height = 16F * progress;

                    int color = item.getBarColor(stack, world());

                    mc().getTextureManager().bindTexture(RESTOCK_TIMER);
                    float texY = y + (16F * (1F - progress));
                    RenderSystem.enableBlend();
                    TextureDrawer.drawTexture(matrix, x, texY, 16, height, 0F, 1F, 1F - progress, 1F, color);
                }

                int count = item.getCount(stack);
                String text = Integer.toString(count);
                int textX = x + 19 - 2 - font().getStringWidth(text);
                int textY = y + 6 + 3;
                int color = count > 0 ? 0xFFFFFFFF : 0xFFFF0000;
                font().drawString(matrix, text, textX, textY, color);
            }
        }
    }

    private static void drawTimeOverlaysForItems(MatrixStack matrix)
    {
        ItemStack stack = player().getHeldItemMainhand();
        if (stack.getItem() instanceof IPlacementTime)
        {
            int time = ((IPlacementTime)stack.getItem()).getCurrentTime(world(), stack);
            int maxTime = ((IPlacementTime)stack.getItem()).getPlacementTime();

            if (time > 0)
            {
                int x = window().getScaledWidth() / 2;
                int y = window().getScaledHeight() / 2 + 20;

                float progress = Math.min((float)time / (float)maxTime, 1.0F);
                UIRenderHelper.drawProgressBar(matrix, x, y + 10, progress, Color4i.WHITE, Color4i.WHITE, true);

                String text = ((IPlacementTime)stack.getItem()).getPlaceMessage().getString();
                x = window().getScaledWidth() / 2 - font().getStringWidth(text) / 2;
                y += 20;
                font().drawString(matrix, text, x, y, 0xFFFFFFFF);
            }
        }
        else if (stack.getItem() instanceof IUsageTimeItem)
        {
            int time = ((IUsageTimeItem)stack.getItem()).getCurrentTime(world(), stack, player().getUniqueID());
            int maxTime = ((IUsageTimeItem)stack.getItem()).getUsageTime(stack);

            if (time > 0)
            {
                int x = window().getScaledWidth() / 2;
                int y = window().getScaledHeight() / 2 + 20;

                float progress = Math.min((float)time / (float)maxTime, 1.0F);
                UIRenderHelper.drawProgressBar(matrix, x, y + 10, progress, Color4i.WHITE, Color4i.WHITE, true);

                String text = ((IUsageTimeItem)stack.getItem()).getUseMessage(stack).getString();
                x = window().getScaledWidth() / 2 - font().getStringWidth(text) / 2;
                y += 20;
                font().drawString(matrix, text, x, y, 0xFFFFFFFF);
            }
        }
    }

    private static void drawTimeOverlaysForTiles(MatrixStack matrix)
    {
        RayTraceResult rayTrace = mc().objectMouseOver;
        if (rayTrace != null && rayTrace.getType() == RayTraceResult.Type.BLOCK)
        {
            BlockPos pos = ((BlockRayTraceResult)rayTrace).getPos();
            TileEntity te = world().getTileEntity(pos);

            if (te instanceof IPickupTime && player().getUniqueID().equals(((IPickupTime)te).getPickupInteractor()))
            {
                int time = ((IPickupTime)te).getCurrentTime();
                int maxTime = ((IPickupTime)te).getPickupTime();

                if (time > 0)
                {
                    int x = window().getScaledWidth() / 2;
                    int y = window().getScaledHeight() / 2 + 20;

                    float progress = Math.min((float)time / (float)maxTime, 1.0F);
                    UIRenderHelper.drawProgressBar(matrix, x, y + 10, progress, Color4i.WHITE, Color4i.WHITE, true);

                    String text = ((IPickupTime)te).getPickupMessage().getString();
                    x = window().getScaledWidth() / 2 - font().getStringWidth(text) / 2;
                    y += 20;
                    font().drawString(matrix, text, x, y, 0xFFFFFFFF);
                }
            }
            else if (te instanceof IUsageTimeTile)
            {
                int time = ((IUsageTimeTile)te).getCurrentTime(player());
                int maxTime = ((IUsageTimeTile)te).getUsageTime();

                if (time > 0)
                {
                    int x = window().getScaledWidth() / 2;
                    int y = window().getScaledHeight() / 2 + 20;

                    float progress = Math.min((float)time / (float)maxTime, 1.0F);
                    UIRenderHelper.drawProgressBar(matrix, x, y + 10, progress, Color4i.WHITE, Color4i.WHITE, true);

                    String text = ((IUsageTimeTile)te).getUseMessage().getString();
                    x = window().getScaledWidth() / 2 - font().getStringWidth(text) / 2;
                    y += 20;
                    font().drawString(matrix, text, x, y, 0xFFFFFFFF);
                }
            }
        }
    }

    private static void drawTimeOverlaysForEntities(MatrixStack matrix)
    {
        RayTraceResult rayTrace = mc().objectMouseOver;
        if (rayTrace != null && rayTrace.getType() == RayTraceResult.Type.ENTITY)
        {
            Entity entity = ((EntityRayTraceResult)rayTrace).getEntity();
            if (entity instanceof IUsageTimeEntity)
            {
                int time = ((IUsageTimeEntity)entity).getCurrentTime(player().getUniqueID());
                int maxTime = ((IUsageTimeEntity)entity).getUsageTime();

                if (time > 0)
                {
                    int x = window().getScaledWidth() / 2;
                    int y = window().getScaledHeight() / 2 + 20;

                    float progress = Math.min((float)time / (float)maxTime, 1.0F);
                    UIRenderHelper.drawProgressBar(matrix, x, y + 10, progress, Color4i.WHITE, Color4i.WHITE, true);

                    String text = ((IUsageTimeEntity)entity).getUseMessage().getString();
                    x = window().getScaledWidth() / 2 - font().getStringWidth(text) / 2;
                    y += 20;
                    font().drawString(matrix, text, x, y, 0xFFFFFFFF);
                }
            }
            else if (entity instanceof IPickupTime && player().getUniqueID().equals(((IPickupTime)entity).getPickupInteractor()))
            {
                int time = ((IPickupTime)entity).getCurrentTime();
                int maxTime = ((IPickupTime)entity).getPickupTime();

                if (time > 0)
                {
                    int x = window().getScaledWidth() / 2;
                    int y = window().getScaledHeight() / 2 + 20;

                    float progress = Math.min((float)time / (float)maxTime, 1.0F);
                    UIRenderHelper.drawProgressBar(matrix, x, y + 10, progress, Color4i.WHITE, Color4i.WHITE, true);

                    String text = ((IPickupTime)entity).getPickupMessage().getString();
                    x = window().getScaledWidth() / 2 - font().getStringWidth(text) / 2;
                    y += 20;
                    font().drawString(matrix, text, x, y, 0xFFFFFFFF);
                }
            }
        }
    }

    private static void drawDbnoPickupTimer(MatrixStack matrix)
    {
        RayTraceResult rayTrace = mc().objectMouseOver;
        if (rayTrace != null && rayTrace.getType() == RayTraceResult.Type.ENTITY)
        {
            Entity entity = ((EntityRayTraceResult) rayTrace).getEntity();
            if (entity instanceof PlayerEntity && ClientDBNOEventHandler.isHelpingPlayer())
            {
                int x = window().getScaledWidth() / 2;
                int y = window().getScaledHeight() / 2 + 20;

                PlayerEntity player = (PlayerEntity)entity;
                float progress = CapabilityDBNO.getFrom(player).getReviveProgress();
                UIRenderHelper.drawProgressBar(matrix, x, y + 10, progress, Color4i.WHITE, Color4i.WHITE, true);

                String text = CapabilityDBNO.PICKUP_MSG.getString();
                x = window().getScaledWidth() / 2 - font().getStringWidth(text) / 2;
                y += 20;
                font().drawString(matrix, text, x, y, 0xFFFFFFFF);
            }
        }
    }

    private static void drawOverhealHearts(MatrixStack matrix)
    {
        //noinspection ConstantConditions
        player().getCapability(CapabilityEffect.EFFECT_CAPABILITY).ifPresent((cap) ->
        {
            //Magic numbers from ForgeIngameGui
            int left = window().getScaledWidth() / 2 - 91;
            int top = window().getScaledHeight() - 39;

            float boost = cap.getBoostPool() + cap.getStimPool();
            if (boost == 0F) { return; }

            mc().getTextureManager().bindTexture(new ResourceLocation(R6Mod.MODID, "textures/gui/overlay/overheal_heart.png"));
            RenderSystem.enableBlend();

            BufferBuilder buffer = Tessellator.getInstance().getBuffer();
            buffer.begin(7, DefaultVertexFormats.POSITION_TEX);

            float minU = 0.0F;
            float maxU = 0.5F;
            float minV = 0.0F;
            float maxV = 0.5F;

            int count = (int)Math.ceil(boost);
            for (int i = 0; i < count; i += 2)
            {
                int x = left + (8 * (i / 2));

                float uOff = ((count - i) == 1) ? .5F : 0F;

                buffer.pos(matrix.getLast().getMatrix(), x,     top + 9, -90).tex(minU + uOff, maxV).endVertex();
                buffer.pos(matrix.getLast().getMatrix(), x + 9, top + 9, -90).tex(maxU + uOff, maxV).endVertex();
                buffer.pos(matrix.getLast().getMatrix(), x + 9, top,     -90).tex(maxU + uOff, minV).endVertex();
                buffer.pos(matrix.getLast().getMatrix(), x,     top,     -90).tex(minU + uOff, minV).endVertex();
            }

            buffer.finishDrawing();
            //noinspection deprecation
            RenderSystem.enableAlphaTest();
            WorldVertexBufferUploader.draw(buffer);

            mc().getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
        });
    }

    /*
     * Helpers
     */

    private static int findAdditionalAmmo(PlayerEntity player, EnumGun gun)
    {
        int count = 0;
        for (int i = 0; i < player.inventory.getSizeInventory(); i++)
        {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                if (gun.hasMag() && stack.getItem() instanceof ItemMagazine)
                {
                    if (((ItemMagazine)stack.getItem()).getMagazine() == gun.getMagazine())
                    {
                        count += stack.getDamage();
                    }
                }
                else if (!gun.hasMag() && stack.getItem() instanceof ItemBullet)
                {
                    if (((ItemBullet)stack.getItem()).getBullet() == gun.getAmmoType())
                    {
                        count += stack.getCount();
                    }
                }
            }
        }
        return count;
    }



    private static Minecraft mc() { return Minecraft.getInstance(); }

    private static World world() { return mc().world; }

    private static PlayerEntity player() { return mc().player; }

    private static MainWindow window() { return mc().getMainWindow(); }

    private static FontRenderer font() { return mc().fontRenderer; }

    private static boolean inCamera() { return mc().getRenderViewEntity() instanceof ICameraEntity; }

    private static boolean inLocalPlayer() { return mc().getRenderViewEntity() == player(); }
}