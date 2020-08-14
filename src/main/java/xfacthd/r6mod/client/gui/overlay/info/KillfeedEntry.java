package xfacthd.r6mod.client.gui.overlay.info;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.client.util.render.TextureDrawer;
import xfacthd.r6mod.client.util.render.UIRenderHelper;
import xfacthd.r6mod.common.data.DeathReason;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGun;

public class KillfeedEntry
{
    private static final int SCREEN_BORDER_OFFSET = 10;
    private static final int ENTRY_HEIGHT = font().FONT_HEIGHT + 4;
    private static final int ICON_SIZE = ENTRY_HEIGHT;
    private static final int TEXT_OFFSET = 4;
    private static final ResourceLocation UNKNOWN = new ResourceLocation(R6Mod.MODID, "textures/gui/symbols/unknown.png");
    private static final ResourceLocation FALL = new ResourceLocation(R6Mod.MODID, "textures/gui/symbols/fall.png");
    private static final ResourceLocation ARROW = new ResourceLocation(R6Mod.MODID, "textures/gui/symbols/arrow.png");
    private static final ResourceLocation KNIVE = new ResourceLocation(R6Mod.MODID, "textures/gui/symbols/knive.png");
    private static final ResourceLocation HEADSHOT = new ResourceLocation(R6Mod.MODID, "textures/gui/symbols/headshot.png");

    private final String killerName;
    private final boolean killerMyTeam;
    private final String victimName;
    private final boolean victimMyTeam;
    private final DeathReason reason;
    private final boolean headshot;
    private final ResourceLocation symbol;

    public KillfeedEntry(String killerName, boolean killerMyTeam, String victimName, boolean victimMyTeam, DeathReason reason, int enumIdx, boolean headshot)
    {
        this.killerName = killerName;
        this.killerMyTeam = killerMyTeam;
        this.victimName = victimName;
        this.victimMyTeam = victimMyTeam;
        this.reason = reason;
        this.headshot = headshot;

        if (reason == DeathReason.GUN) { symbol = EnumGun.values()[enumIdx].getDeathMessageSymbol(); }
        else if (reason == DeathReason.GADGET) { symbol = EnumGadget.values()[enumIdx].getDeathMessageSymbol(); }
        else if (reason == DeathReason.FALL) { symbol = FALL; }
        else if (reason == DeathReason.ARROW) { symbol = ARROW; }
        else if (reason == DeathReason.KNIVE) { symbol = KNIVE; }
        else { symbol = UNKNOWN; }
    }

    public int draw(int xRight, int yTop)
    {
        //Enable alpha blending
        RenderSystem.enableBlend();

        //Scale
        RenderSystem.scaled(.5, .5, .5);
        xRight *= 2;
        yTop *= 2;

        //Calculate edge coords
        xRight -= SCREEN_BORDER_OFFSET;

        //Draw victim name
        int len = font().getStringWidth(victimName) + 4;
        xRight -= len;
        drawBackground(xRight, yTop, len, victimMyTeam);
        font().drawStringWithShadow(victimName, xRight + 2, yTop + 2, 0xFFFFFFFF);
        xRight -= TEXT_OFFSET;

        //Draw headshot icon
        if (reason == DeathReason.GUN && headshot)
        {
            xRight -= ICON_SIZE;
            mc().getTextureManager().bindTexture(HEADSHOT);
            TextureDrawer.drawTexture(xRight, yTop, ICON_SIZE, ICON_SIZE, 0F, 1F, 0F, 1F);
            xRight -= TEXT_OFFSET;
        }

        //Draw icon
        int iconWidth = reason == DeathReason.GUN ? ICON_SIZE * 2 : ICON_SIZE;
        xRight -= iconWidth;
        mc().getTextureManager().bindTexture(symbol);
        TextureDrawer.drawTexture(xRight, yTop, iconWidth, ICON_SIZE, 0F, 1F, 0F, 1F);
        xRight -= TEXT_OFFSET;

        //Draw killer name
        if (!killerName.equals(victimName))
        {
            len = font().getStringWidth(killerName) + 4;
            xRight -= len;
            drawBackground(xRight, yTop, len, killerMyTeam);
            font().drawStringWithShadow(killerName, xRight + 2, yTop + 2, 0xFFFFFFFF);
        }

        //Fix scaling
        RenderSystem.scaled(2, 2, 2);

        //Return y for next entry scaled to normal resolution
        return (yTop + ENTRY_HEIGHT + 2) / 2;
    }

    private void drawBackground(int x, int y, int w, boolean myteam)
    {
        RenderSystem.disableTexture();

        int r = (myteam == onBlueTeam()) ?   0 : 255;
        int g = (myteam == onBlueTeam()) ?  54 :  85;
        int b = (myteam == onBlueTeam()) ? 255 :   0;

        //Get Tesselator and Draw Buffer
        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder buffer = tessellator.getBuffer();

        //Draw bar
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        UIRenderHelper.fillRect(buffer, x, y, x + w, y + ENTRY_HEIGHT, r, g, b, 255);

        //Finish drawing
        buffer.finishDrawing();
        RenderSystem.color4f(1F, 1F, 1F, 1F);
        RenderSystem.disableAlphaTest();
        WorldVertexBufferUploader.draw(buffer);

        RenderSystem.enableTexture();
    }

    private boolean onBlueTeam() { return false; }

    private static Minecraft mc() { return Minecraft.getInstance(); }

    private static FontRenderer font() { return mc().fontRenderer; }
}