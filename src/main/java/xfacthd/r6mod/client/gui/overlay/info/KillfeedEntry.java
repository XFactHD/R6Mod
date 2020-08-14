package xfacthd.r6mod.client.gui.overlay.info;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.client.util.render.TextureDrawer;
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
    private static final ResourceLocation TEAM_COLORS = new ResourceLocation(R6Mod.MODID, "textures/gui/overlay/team_colors.png");

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

    public int draw(MatrixStack matrix, int xRight, int yTop)
    {
        //Enable alpha blending
        RenderSystem.enableBlend();

        //Scale
        matrix.push();
        matrix.scale(.5F, .5F, .5F);
        xRight *= 2;
        yTop *= 2;

        //Calculate edge coords
        xRight -= SCREEN_BORDER_OFFSET;

        //Draw victim name
        int len = font().getStringWidth(victimName) + 4;
        xRight -= len;
        drawBackground(matrix, xRight, yTop, len, victimMyTeam);
        font().drawString(matrix, victimName, xRight + 2, yTop + 2, 0xFFFFFFFF);
        xRight -= TEXT_OFFSET;

        //Draw headshot icon
        if (reason == DeathReason.GUN && headshot)
        {
            xRight -= ICON_SIZE;
            mc().getTextureManager().bindTexture(HEADSHOT);
            TextureDrawer.drawTexture(matrix, xRight, yTop, ICON_SIZE, ICON_SIZE, 0F, 1F, 0F, 1F);
            xRight -= TEXT_OFFSET;
        }

        //Draw icon
        int iconWidth = reason == DeathReason.GUN ? ICON_SIZE * 2 : ICON_SIZE;
        xRight -= iconWidth;
        mc().getTextureManager().bindTexture(symbol);
        TextureDrawer.drawTexture(matrix, xRight, yTop, iconWidth, ICON_SIZE, 0F, 1F, 0F, 1F);
        xRight -= TEXT_OFFSET;

        //Draw killer name
        if (!killerName.equals(victimName))
        {
            len = font().getStringWidth(killerName) + 4;
            xRight -= len;
            drawBackground(matrix, xRight, yTop, len, killerMyTeam);
            font().drawString(matrix, killerName, xRight + 2, yTop + 2, 0xFFFFFFFF);
        }

        //Fix scaling
        matrix.pop();

        //Return y for next entry scaled to normal resolution
        return (yTop + ENTRY_HEIGHT + 2) / 2;
    }

    private void drawBackground(MatrixStack matrix, int x, int y, int w, boolean myteam)
    {
        Minecraft.getInstance().getTextureManager().bindTexture(TEAM_COLORS);

        float minV = (myteam == onBlueTeam() ? 0 : .5F);
        float maxV = (myteam == onBlueTeam() ? .5F : 1);
        TextureDrawer.drawTexture(matrix, x, y, w, ENTRY_HEIGHT, 0, 0, minV, maxV);
    }

    private boolean onBlueTeam() { return false; }

    private static Minecraft mc() { return Minecraft.getInstance(); }

    private static FontRenderer font() { return mc().fontRenderer; }
}