package xfacthd.r6mod.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.common.container.ContainerMagFiller;

public class ScreenMagFiller extends ContainerScreen<ContainerMagFiller>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(R6Mod.MODID, "textures/gui/gui_mag_filler.png");

    public ScreenMagFiller(ContainerMagFiller container, PlayerInventory inv, ITextComponent title)
    {
        super(container, inv, title);
        xSize = 176;
        ySize = 183;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bindTexture(TEXTURE);
        int texX = (width - xSize) / 2;
        int texY = (height - ySize) / 2;
        blit(texX, texY, 0, 0, xSize, ySize);
    }
}