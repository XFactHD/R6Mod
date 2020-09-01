package xfacthd.r6mod.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.client.gui.screen.widgets.TeamListWidget;
import xfacthd.r6mod.client.util.render.TextureDrawer;
import xfacthd.r6mod.common.container.ContainerTeamSpawn;
import xfacthd.r6mod.common.net.NetworkHandler;
import xfacthd.r6mod.common.net.packets.match.PacketTeamSpawnGuiResult;

import java.util.Collection;

public class ScreenTeamSpawn extends ContainerScreen<ContainerTeamSpawn> implements Button.IPressable
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(R6Mod.MODID, "textures/gui/gui_camera.png");
    private static final ITextComponent CONFIRM = new TranslationTextComponent("gui.r6mod.camera.confirm");
    private static final String HEADER = I18n.format("gui.r6mod.team_spawn.header");
    private static final int PADDING = 6;
    private static final int LIST_HEIGHT = 120;
    private static final int BUTTON_HEIGHT = 20;

    private TeamListWidget listWidget;
    private Button buttonOk;
    private boolean closedByButton = false;

    public ScreenTeamSpawn(ContainerTeamSpawn container, PlayerInventory inv, ITextComponent title)
    {
        super(container, inv, title);
        ySize = 184;
    }

    @Override
    protected void init()
    {
        int listWidth = Math.max(50, font.getStringWidth(TeamListWidget.NO_TEAM) + 30);
        //noinspection ConstantConditions
        Collection<String> teams = minecraft.world.getScoreboard().getTeamNames();
        for (String team : teams) { listWidth = Math.max(listWidth, font.getStringWidth(team) + 30); }
        xSize = listWidth + 12;
        super.init();

        int listY = guiTop + BUTTON_HEIGHT + (PADDING * 2);
        listWidget = new TeamListWidget(guiLeft + PADDING, listY, listWidth, LIST_HEIGHT, teams, font);

        int buttonY = listY + LIST_HEIGHT + PADDING;
        buttonOk = new Button(guiLeft + PADDING, buttonY, listWidth, BUTTON_HEIGHT, CONFIRM, this);

        buttons.add(buttonOk);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrix, float partialTicks, int mouseX, int mouseY)
    {
        listWidget.render(matrix, mouseX, mouseY, partialTicks);

        //noinspection ConstantConditions
        minecraft.getTextureManager().bindTexture(TEXTURE);

        int texX = (width - xSize) / 2;
        int texY = (height - ySize) / 2;

        TextureDrawer.start();

        //Left border
        TextureDrawer.fillGuiBuffer(matrix, texX, texY, 0, 0, 6, ySize);

        //Center body
        int maxX = guiLeft + xSize - 6;
        for (texX += 6; texX < maxX; texX += 56)
        {
            int w = Math.min(56, maxX - texX);
            TextureDrawer.fillGuiBuffer(matrix, texX, texY, 6, 0, w, ySize);
        }

        //Right border
        texX = maxX;
        TextureDrawer.fillGuiBuffer(matrix, texX, texY, 62, 0, 6, ySize);

        TextureDrawer.end();

        drawCenteredString(matrix, font, HEADER, width / 2, guiTop + 12, 0xFFFFFFFF);
    }

    @Override //TODO: set titleX/Y and playerInventoryTitleX/Y properly instead of hiding the titles
    protected void drawGuiContainerForegroundLayer(MatrixStack stack, int x, int y) { /*NOOP to hide titles*/ }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks)
    {
        renderBackground(matrix);
        super.render(matrix, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (mouseX >= listWidget.getLeft() && mouseX <= listWidget.getRight() && mouseY >= listWidget.getTop() && mouseY <= listWidget.getBottom())
        {
            return listWidget.mouseClicked(mouseX, mouseY, button);
        }
        else if (mouseX >= buttonOk.x && mouseX <= buttonOk.x + buttonOk.getWidth() && mouseY >= buttonOk.y && mouseY <= buttonOk.y + buttonOk.getWidth_CLASH())
        {
            return buttonOk.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double diffX, double diffY)
    {
        if (mouseX >= listWidget.getLeft() && mouseX <= listWidget.getRight() && mouseY >= listWidget.getTop() && mouseY <= listWidget.getBottom())
        {
            return listWidget.mouseDragged(mouseX, mouseY, button, diffX, diffY);
        }
        return super.mouseDragged(mouseX, mouseY, button, diffX, diffY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double wheel)
    {
        if (mouseX >= listWidget.getLeft() && mouseX <= listWidget.getRight() && mouseY >= listWidget.getTop() && mouseY <= listWidget.getBottom())
        {
            return listWidget.mouseScrolled(mouseX, mouseY, wheel);
        }
        return false;
    }

    @Override
    public void onPress(Button button)
    {
        if (button == buttonOk)
        {
            closedByButton = true;
            onClose();
        }
    }

    @Override
    public void onClose()
    {
        super.onClose();
        TeamListWidget.TeamEntry sel = listWidget.getSelected();
        String result = closedByButton && sel != null ? (sel.getTeam()) : "null";
        NetworkHandler.sendToServer(new PacketTeamSpawnGuiResult(result));
    }
}