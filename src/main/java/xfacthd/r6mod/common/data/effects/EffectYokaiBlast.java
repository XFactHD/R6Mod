package xfacthd.r6mod.common.data.effects;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import xfacthd.r6mod.R6Mod;

public class EffectYokaiBlast extends AbstractEffect
{
    private static TextureAtlasSprite sprite;

    public EffectYokaiBlast(ServerPlayerEntity player, int time, long start) { super(player, EnumEffect.YOKAI_BLAST, time, start); }

    @Override
    protected void handleEffect(int runTime) { player.setSprinting(false); }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawEffect()
    {
        //noinspection deprecation
        Minecraft.getInstance().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

        int w = Minecraft.getInstance().getMainWindow().getScaledWidth();
        int h = Minecraft.getInstance().getMainWindow().getScaledHeight();

        //noinspection ConstantConditions
        int diff = (int)(Minecraft.getInstance().world.getGameTime() - effectStart);
        float factor = (float)(effectTime - diff) / 20F;
        int alpha = (effectTime - diff > 20) ? 0xFF : (int)((float)0xFF * factor);

        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
        bufferbuilder.pos(0, h, 0).tex(sprite.getMinU(), sprite.getMaxV()).color(0xFF, 0xFF, 0xFF, alpha).endVertex();
        bufferbuilder.pos(w, h, 0).tex(sprite.getMaxU(), sprite.getMaxV()).color(0xFF, 0xFF, 0xFF, alpha).endVertex();
        bufferbuilder.pos(w, 0, 0).tex(sprite.getMaxU(), sprite.getMinV()).color(0xFF, 0xFF, 0xFF, alpha).endVertex();
        bufferbuilder.pos(0, 0, 0).tex(sprite.getMinU(), sprite.getMinV()).color(0xFF, 0xFF, 0xFF, alpha).endVertex();
        bufferbuilder.finishDrawing();
        RenderSystem.enableAlphaTest();
        WorldVertexBufferUploader.draw(bufferbuilder);

        //TODO: add light effects
    }

    public static void registerTextures(final TextureStitchEvent.Pre event)
    {
        event.addSprite(new ResourceLocation(R6Mod.MODID, "gui/overlay/effect_yokai"));
    }

    public static void loadTextures(AtlasTexture map)
    {
        sprite = map.getSprite(new ResourceLocation(R6Mod.MODID, "gui/overlay/effect_yokai"));
    }

    @Override
    protected boolean showProgress() { return true; }

    @Override
    protected boolean isPositive() { return false; }
}