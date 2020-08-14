package XFactHD.rssmc.client.renderer.tesr;

import XFactHD.rssmc.client.util.ClientReference;
import XFactHD.rssmc.common.blocks.gadget.BlockThermiteCharge;
import XFactHD.rssmc.common.blocks.gadget.TileEntityThermiteCharge;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TESRThermiteCharge extends TileEntitySpecialRenderer<TileEntityThermiteCharge>
{
    @Override
    public void renderTileEntityAt(TileEntityThermiteCharge te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        if (te.hasWorld() && te.getWorld().getBlockState(te.getPos()).getBlock() instanceof BlockThermiteCharge && te.isActive())
        {
            GlStateManager.pushMatrix();

            EnumFacing facing = te.getWorld().getBlockState(te.getPos()).getValue(PropertyHolder.FACING_NOT_UP);

            RenderHelper.disableStandardItemLighting();

            int index = 106 - te.getTime();
            bindTexture(new ResourceLocation("rssmc:textures/blocks/block_thermite_charge_burning/block_thermite_charge_burning_" + index + ".png"));
            Tessellator tess = Tessellator.getInstance();
            VertexBuffer buffer = tess.getBuffer();
            buffer.setTranslation(x, y, z);
            buffer.begin(GL11.GL_QUADS, ClientReference.POSITION_TEX_LMAP);
            switch (facing)
            {
                case NORTH:
                {
                    buffer.pos(1, 1,  1.1F/16F).tex(1, 0).lightmap(240, 240).endVertex();
                    buffer.pos(0, 1,  1.1F/16F).tex(0, 0).lightmap(240, 240).endVertex();
                    buffer.pos(0, 0,  1.1F/16F).tex(0, 1).lightmap(240, 240).endVertex();
                    buffer.pos(1, 0,  1.1F/16F).tex(1, 1).lightmap(240, 240).endVertex();
                    break;
                }
                case EAST:
                {
                    buffer.pos(14.9F/16F, 0, 0).tex(0, 1).lightmap(240, 240).endVertex();
                    buffer.pos(14.9F/16F, 0, 1).tex(1, 1).lightmap(240, 240).endVertex();
                    buffer.pos(14.9F/16F, 1, 1).tex(1, 0).lightmap(240, 240).endVertex();
                    buffer.pos(14.9F/16F, 1, 0).tex(0, 0).lightmap(240, 240).endVertex();
                    break;
                }
                case SOUTH:
                {
                    buffer.pos(0, 1, 14.9F/16F).tex(1, 0).lightmap(240, 240).endVertex();
                    buffer.pos(1, 1, 14.9F/16F).tex(0, 0).lightmap(240, 240).endVertex();
                    buffer.pos(1, 0, 14.9F/16F).tex(0, 1).lightmap(240, 240).endVertex();
                    buffer.pos(0, 0, 14.9F/16F).tex(1, 1).lightmap(240, 240).endVertex();
                    break;
                }
                case WEST:
                {
                    buffer.pos( 1.1F/16F, 1, 0).tex(1, 0).lightmap(240, 240).endVertex();
                    buffer.pos( 1.1F/16F, 1, 1).tex(0, 0).lightmap(240, 240).endVertex();
                    buffer.pos( 1.1F/16F, 0, 1).tex(0, 1).lightmap(240, 240).endVertex();
                    buffer.pos( 1.1F/16F, 0, 0).tex(1, 1).lightmap(240, 240).endVertex();
                    break;
                }
                case DOWN:
                {
                    buffer.pos(0, 1.1F/16F, 0).tex(0, 0).lightmap(240, 240).endVertex();
                    buffer.pos(0, 1.1F/16F, 1).tex(0, 1).lightmap(240, 240).endVertex();
                    buffer.pos(1, 1.1F/16F, 1).tex(1, 1).lightmap(240, 240).endVertex();
                    buffer.pos(1, 1.1F/16F, 0).tex(1, 0).lightmap(240, 240).endVertex();
                    break;
                }
            }
            tess.draw();
            buffer.setTranslation(0, 0, 0);
            RenderHelper.enableStandardItemLighting();

            GlStateManager.popMatrix();
        }
    }
}