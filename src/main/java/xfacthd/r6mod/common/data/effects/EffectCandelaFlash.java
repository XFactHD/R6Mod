package xfacthd.r6mod.common.data.effects;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.common.items.gadgets.ItemYingGlasses;

public class EffectCandelaFlash extends AbstractEffect
{
    private static TextureAtlasSprite sprite;

    public EffectCandelaFlash(ServerPlayerEntity player, int time, long start)
    {
        super(player, EnumEffect.CANDELA_FLASH, time, start);
    }

    @Override
    protected void handleEffect(int runTime)
    {
        if (ItemYingGlasses.isActive(player) /*|| ItemSmartGlasses.isActive(player)*/) //TODO: activate when glance is implemented
        {
            invalidate();
        }
    }

    @Override
    public void drawEffect(MatrixStack matrix)
    {
        //noinspection deprecation
        Minecraft.getInstance().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

        int w = Minecraft.getInstance().getMainWindow().getScaledWidth();
        int h = Minecraft.getInstance().getMainWindow().getScaledHeight();

        //noinspection ConstantConditions
        int diff = (int)(Minecraft.getInstance().world.getGameTime() - effectStart);
        float factor = Math.max((float)(effectTime - diff) / 20F, 0F);
        int alpha = (effectTime - diff > 20) ? 0xFF : (int)((float)0xFF * factor);

        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
        bufferbuilder.pos(matrix.getLast().getMatrix(), 0, h, 0).color(0xFF, 0xFF, 0xFF, alpha).tex(sprite.getMinU(), sprite.getMaxV()).endVertex();
        bufferbuilder.pos(matrix.getLast().getMatrix(), w, h, 0).color(0xFF, 0xFF, 0xFF, alpha).tex(sprite.getMaxU(), sprite.getMaxV()).endVertex();
        bufferbuilder.pos(matrix.getLast().getMatrix(), w, 0, 0).color(0xFF, 0xFF, 0xFF, alpha).tex(sprite.getMaxU(), sprite.getMinV()).endVertex();
        bufferbuilder.pos(matrix.getLast().getMatrix(), 0, 0, 0).color(0xFF, 0xFF, 0xFF, alpha).tex(sprite.getMinU(), sprite.getMinV()).endVertex();
        bufferbuilder.finishDrawing();
        //noinspection deprecation
        RenderSystem.enableAlphaTest();
        WorldVertexBufferUploader.draw(bufferbuilder);
    }

    public static void registerTextures(final TextureStitchEvent.Pre event)
    {
        event.addSprite(new ResourceLocation(R6Mod.MODID, "gui/overlay/effect_flash"));
    }

    public static void loadTextures(AtlasTexture map)
    {
        sprite = map.getSprite(new ResourceLocation(R6Mod.MODID, "gui/overlay/effect_flash"));
    }

    @Override
    protected boolean isPositive() { return false; }

    @Override
    public boolean showIcon() { return false; }
}