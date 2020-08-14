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
import xfacthd.r6mod.common.util.damage.DamageSourceGuMine;

import java.util.UUID;

public class EffectGuMine extends AbstractEffect
{
    private static final int DAMAGE_INTERVAL = 40;
    private static final float DAMAGE = 4F / 5F;
    private static TextureAtlasSprite sprite;

    private final UUID mineOwner;

    public EffectGuMine(ServerPlayerEntity player, UUID mineOwner, int effectTime, long effectStart)
    {
        super(player, EnumEffect.GU_MINE, effectTime, effectStart);
        this.mineOwner = mineOwner;
    }

    @Override
    protected void handleEffect(int runTime)
    {
        player.setSprinting(false);
        if (runTime % DAMAGE_INTERVAL == 0)
        {
            player.attackEntityFrom(new DamageSourceGuMine(player.world.getPlayerByUuid(mineOwner)), DAMAGE);
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

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
        buffer.pos(matrix.getLast().getMatrix(), 0, h, 0).color(0xFF, 0xFF, 0xFF, alpha).tex(sprite.getMinU(), sprite.getMaxV()).endVertex();
        buffer.pos(matrix.getLast().getMatrix(), w, h, 0).color(0xFF, 0xFF, 0xFF, alpha).tex(sprite.getMaxU(), sprite.getMaxV()).endVertex();
        buffer.pos(matrix.getLast().getMatrix(), w, 0, 0).color(0xFF, 0xFF, 0xFF, alpha).tex(sprite.getMaxU(), sprite.getMinV()).endVertex();
        buffer.pos(matrix.getLast().getMatrix(), 0, 0, 0).color(0xFF, 0xFF, 0xFF, alpha).tex(sprite.getMinU(), sprite.getMinV()).endVertex();
        buffer.finishDrawing();
        //noinspection deprecation
        RenderSystem.enableAlphaTest();
        WorldVertexBufferUploader.draw(buffer);
    }

    @Override
    public float getEffectProgress(long worldTime)
    {
        float diff = (float)((worldTime - effectStart) % DAMAGE_INTERVAL);
        return diff / (float)DAMAGE_INTERVAL;
    }

    public static void registerTextures(final TextureStitchEvent.Pre event)
    {
        event.addSprite(new ResourceLocation(R6Mod.MODID, "gui/overlay/effect_gu"));
    }

    public static void loadTextures(AtlasTexture map)
    {
        sprite = map.getSprite(new ResourceLocation(R6Mod.MODID, "gui/overlay/effect_gu"));
    }

    @Override
    protected boolean showProgress() { return true; }

    @Override
    protected boolean isPositive() { return false; }
}