package xfacthd.r6mod.client.gui.overlay.camera;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import xfacthd.r6mod.api.entity.ICameraEntity;

public class OverlayBlackEye extends AbstractCameraOverlay //TODO: implement properly
{
    @Override
    public void drawOverlay(ICameraEntity camera)
    {
        int w = window().getScaledWidth();
        int h = window().getScaledHeight();

        //INFO: blue filter
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(0, h, 0).color(0x00, 0x90, 0xFF, 0x44).endVertex();
        bufferbuilder.pos(w, h, 0).color(0x00, 0x90, 0xFF, 0x44).endVertex();
        bufferbuilder.pos(w, 0, 0).color(0x00, 0x90, 0xFF, 0x44).endVertex();
        bufferbuilder.pos(0, 0, 0).color(0x00, 0x90, 0xFF, 0x44).endVertex();
        bufferbuilder.finishDrawing();
        RenderSystem.enableAlphaTest();
        WorldVertexBufferUploader.draw(bufferbuilder);
    }
}