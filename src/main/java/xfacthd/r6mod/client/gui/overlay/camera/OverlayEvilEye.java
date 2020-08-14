package xfacthd.r6mod.client.gui.overlay.camera;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.client.util.render.*;
import xfacthd.r6mod.common.entities.camera.EntityEvilEyeCamera;

import java.util.Random;

public class OverlayEvilEye extends AbstractCameraOverlay<EntityEvilEyeCamera>
{
    private static final ResourceLocation JAMMED = new ResourceLocation(R6Mod.MODID, "textures/gui/overlay/jammer_filter.png");
    private static final ResourceLocation DOOR_STATE = new ResourceLocation(R6Mod.MODID, "textures/gui/overlay/evil_eye_door.png");
    private static final ResourceLocation CROSSHAIR = new ResourceLocation(R6Mod.MODID, "textures/gui/overlay/evil_eye_crosshair.png");
    private static final ResourceLocation WARNING = new ResourceLocation(R6Mod.MODID, "textures/gui/overlay/evil_eye_warn.png");
    private static final Random RAND = new Random(System.currentTimeMillis());

    @Override
    public void drawOverlay(EntityEvilEyeCamera camera, MatrixStack matrix)
    {
        int w = window().getScaledWidth();
        int h = window().getScaledHeight();

        float xCenter = w / 2F;
        float yCenter = h / 2F;

        float door = camera.getDoorState();
        boolean overheat = camera.isOverheated();

        if (camera.isJammed())
        {
            drawJammed(matrix, w, h);
        }
        else if (door == 0F || overheat)
        {
            float ammo = camera.getAmmo();

            drawCrosshair(matrix, xCenter, yCenter, overheat);
            drawStaticArc(matrix, xCenter, yCenter);
            drawAmmoArc(matrix, xCenter, yCenter, overheat, ammo);
        }
        else
        {
            drawDoorState(matrix, xCenter, yCenter, door);
        }
    }

    private void drawJammed(MatrixStack matrix, int w, int h)
    {
        Minecraft.getInstance().getTextureManager().bindTexture(JAMMED);

        TextureDrawer.startColored();

        float qw = w / 4F;
        float hh = h / 2F;
        for (int xMul = 0; xMul < 4; xMul++)
        {
            for (int yMul = 0; yMul < 4; yMul++)
            {
                float x = qw * (float) xMul;
                float y = hh * (float) yMul;

                float uOff = .5F * (RAND.nextInt(32) / 32F);
                float vOff = .5F * (RAND.nextInt(32) / 32F);
                TextureDrawer.fillBuffer(matrix, x, y, qw, hh, 0F + uOff, .5F + uOff, 0F + vOff, .5F + vOff, 0xFFFFFFBF);
                TextureDrawer.fillBuffer(matrix, x, y, qw, hh, 0F + uOff, .5F + uOff, 0F + vOff, .5F + vOff, 0xFFFFFFBF);
            }
        }

        TextureDrawer.end();
    }

    private void drawDoorState(MatrixStack matrix, float xCenter, float yCenter, float door)
    {
        float xOff = 5F * (1F - door);

        float size = 8;
        float xLeft = xCenter - 5F - xOff - size;
        float xRight = xCenter + 4F + xOff;
        float yTop = yCenter - (size / 2F);

        mc().getTextureManager().bindTexture(DOOR_STATE);

        TextureDrawer.start();
        TextureDrawer.fillBuffer(matrix, xLeft,  yTop, size, size, 0, 1, 0, 1);
        TextureDrawer.fillBuffer(matrix, xRight, yTop, size, size, 1, 0, 0, 1);
        TextureDrawer.end();
    }

    private void drawCrosshair(MatrixStack matrix, float xCenter, float yCenter, boolean overheated)
    {
        mc().getTextureManager().bindTexture(overheated ? WARNING : CROSSHAIR);
        TextureDrawer.drawTexture(matrix, xCenter - 8.5F, yCenter - 8F, 16, 16, 0, 1, 0, 1);
    }

    private void drawStaticArc(MatrixStack matrix, float xCenter, float yCenter)
    {
        RenderSystem.disableTexture();
        UIRenderHelper.drawArc(matrix, xCenter, yCenter, 40, .5F, 90, -180, 40, Color4i.WHITE);
        RenderSystem.enableTexture();
    }

    private void drawAmmoArc(MatrixStack matrix, float xCenter, float yCenter, boolean overheated, float ammo)
    {
        RenderSystem.disableTexture();

        float startAngle = 90F;
        float arcAngle = -180F * ammo;

        if (overheated)
        {
            arcAngle = (-180F * (1F - ammo));
            startAngle = 270F - arcAngle;
        }

        Color4i color = (overheated || ammo < .4F) ? Color4i.RED : Color4i.WHITE;

        UIRenderHelper.drawArc(matrix, xCenter, yCenter, 44, 2, startAngle, arcAngle, 80, color);

        RenderSystem.enableTexture();
    }

    @Override
    public boolean hideCrosshair() { return true; }
}