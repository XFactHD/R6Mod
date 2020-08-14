package xfacthd.r6mod.client.gui.overlay.info;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.client.util.render.TextureDrawer;
import xfacthd.r6mod.common.data.PointContext;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.util.data.ExtraPointsEntry;

import java.util.List;

public class PointsEntry
{
    private static final int SCREEN_BORDER_OFFSET = 10;
    private static final int BASE_HEIGHT = 24;
    private static final int EXTRA_ENTRY_HEIGHT = 10;
    private static final int ENTRY_WIDTH = 128;
    private static final int TEXT_OFFSET = 4;
    private static final int ONE_SPACE = font().getStringWidth(" ");

    private final PointContext context;
    private final int points;
    private final List<ExtraPointsEntry> extraPoints;
    private final EnumGadget gadget;

    public PointsEntry(PointContext context, int points, List<ExtraPointsEntry> extraPoints, EnumGadget gadget)
    {
        this.context = context;
        this.points = points;
        this.extraPoints = extraPoints;
        this.gadget = gadget;
    }

    public int draw(int xRight, int yTop)
    {
        //Bind texture
        mc().getTextureManager().bindTexture(new ResourceLocation(R6Mod.MODID, "textures/gui/overlay/point_info.png"));

        //Enable alpha blending
        RenderSystem.enableBlend();

        //Scale
        RenderSystem.scaled(.5, .5, .5);
        xRight *= 2;
        yTop *= 2;

        //Calculate edge coords
        xRight -= SCREEN_BORDER_OFFSET;
        int xLeft = xRight - ENTRY_WIDTH;

        //Draw texture
        TextureDrawer.drawTexture(xLeft, yTop, ENTRY_WIDTH, BASE_HEIGHT, 0F, 1F, 0F, .75F);

        //Draw main points
        String text = (points >= 0 ? "+" : "") + points;
        int textX = xRight - font().getStringWidth(text) - TEXT_OFFSET;
        int color = points >= 0 ? 0xFFFFF500 : 0xFFFF0000;
        font().drawStringWithShadow(text, textX, yTop + TEXT_OFFSET, color);

        //Draw main point info text
        String key = context.needsGadget() ? context.translation(gadget) : context.translation();
        text = I18n.format(key);
        textX = xRight - font().getStringWidth(text) - TEXT_OFFSET;
        font().drawStringWithShadow(text, textX, yTop + font().FONT_HEIGHT + TEXT_OFFSET, color);

        //"Increment" y coordinate
        yTop += BASE_HEIGHT + TEXT_OFFSET;

        //Draw extra point lines
        if (extraPoints != null)
        {
            for (ExtraPointsEntry entry : extraPoints)
            {
                //Draw extra points
                text = (entry.getPoints() >= 0 ? "+" : "") + entry.getPoints();
                textX = xRight - font().getStringWidth(text) - TEXT_OFFSET;
                font().drawStringWithShadow(text, textX, yTop, 0xFFFFFFFF);

                //Draw extra point info text
                key = entry.getContext().needsGadget() ? entry.getContext().translation(gadget) : entry.getContext().translation();
                text = I18n.format(key);
                textX -= font().getStringWidth(text) + ONE_SPACE;
                font().drawStringWithShadow(text, textX, yTop, 0xFFBBBBBB);
                yTop += EXTRA_ENTRY_HEIGHT;
            }

            yTop += TEXT_OFFSET; //Add an additional small offset to seperate this from the next entry
        }

        //Fix scaling
        RenderSystem.scaled(2, 2, 2);

        //Return y for next entry scaled to normal resolution
        return yTop / 2;
    }

    private static Minecraft mc() { return Minecraft.getInstance(); }

    private static FontRenderer font() { return mc().fontRenderer; }
}