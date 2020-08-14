package xfacthd.r6mod.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.client.util.render.TextureDrawer;
import xfacthd.r6mod.common.container.ContainerMagFiller;

public class ScreenMagFiller extends ContainerScreen<ContainerMagFiller>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(R6Mod.MODID, "textures/gui/gui_mag_filler.png");
    private static final int SIZE_X = 176;
    private static final int SIZE_Y = 183;

    public ScreenMagFiller(ContainerMagFiller container, PlayerInventory inv, ITextComponent title)
    {
        super(container, inv, title);
        xSize = SIZE_X;
        ySize = SIZE_Y;
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks)
    {
        renderBackground(matrix);
        super.render(matrix, mouseX, mouseY, partialTicks);
    }

    @Override //TODO: set titleX/Y and playerInventoryTitleX/Y properly instead of hiding the titles
    protected void drawGuiContainerForegroundLayer(MatrixStack stack, int x, int y) { /*NOOP to hide titles*/ }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrix, float partialTicks, int mouseX, int mouseY)
    {
        //noinspection ConstantConditions
        minecraft.getTextureManager().bindTexture(TEXTURE);
        int texX = (width - xSize) / 2;
        int texY = (height - ySize) / 2;
        TextureDrawer.drawGuiTexture(matrix, texX, texY, 0, 0, xSize, ySize);
    }
}