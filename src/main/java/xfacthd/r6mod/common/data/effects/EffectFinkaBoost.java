package xfacthd.r6mod.common.data.effects;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.api.capability.ICapabilityEffect;
import xfacthd.r6mod.common.capability.CapabilityEffect;

public class EffectFinkaBoost extends AbstractEffect
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(R6Mod.MODID, "textures/gui/overlay/effect_finka.png");
    private boolean added = false;

    public EffectFinkaBoost(ServerPlayerEntity player, int time, long start) { super(player, EnumEffect.FINKA_BOOST, time, start); }

    @Override
    protected void handleEffect(int runTime)
    {
        if (!added)
        {
            added = true;

            //noinspection ConstantConditions
            player.getCapability(CapabilityEffect.EFFECT_CAPABILITY).ifPresent(ICapabilityEffect::addFinkaBoost);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawEffect()
    {
        Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE);

        int w = Minecraft.getInstance().getMainWindow().getScaledWidth();
        int h = Minecraft.getInstance().getMainWindow().getScaledHeight();

        //noinspection ConstantConditions
        int diff = (int)(Minecraft.getInstance().world.getGameTime() - effectStart);
        float factor = (float)(effectTime - diff) / 20F;
        int alpha = (effectTime - diff > 20) ? 0xFF : (int)((float)0xFF * factor);

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
        buffer.pos(0, h, 0).color(0xFF, 0xFF, 0xFF, alpha).tex(0F, 1F).endVertex();
        buffer.pos(w, h, 0).color(0xFF, 0xFF, 0xFF, alpha).tex(1F, 1F).endVertex();
        buffer.pos(w, 0, 0).color(0xFF, 0xFF, 0xFF, alpha).tex(1F, 0F).endVertex();
        buffer.pos(0, 0, 0).color(0xFF, 0xFF, 0xFF, alpha).tex(0F, 0F).endVertex();
        buffer.finishDrawing();
        RenderSystem.enableAlphaTest();
        WorldVertexBufferUploader.draw(buffer);
    }

    @Override
    public void invalidate()
    {
        super.invalidate();

        //noinspection ConstantConditions
        player.getCapability(CapabilityEffect.EFFECT_CAPABILITY).ifPresent(ICapabilityEffect::removeFinkaBoost);
    }

    @Override
    protected boolean showProgress() { return true; }

    @Override
    protected boolean isPositive() { return true; }
}