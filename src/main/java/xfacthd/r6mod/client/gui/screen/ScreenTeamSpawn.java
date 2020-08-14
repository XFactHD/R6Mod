package xfacthd.r6mod.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.client.gui.screen.widgets.TeamListWidget;
import xfacthd.r6mod.common.container.ContainerTeamSpawn;
import xfacthd.r6mod.common.net.NetworkHandler;
import xfacthd.r6mod.common.net.packets.match.PacketTeamSpawnGuiResult;

import java.util.Collection;

public class ScreenTeamSpawn extends ContainerScreen<ContainerTeamSpawn> implements Button.IPressable
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(R6Mod.MODID, "textures/gui/gui_camera.png");
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
    @SuppressWarnings("ConstantConditions")
    protected void init()
    {
        int listWidth = Math.max(50, font.getStringWidth(TeamListWidget.NO_TEAM) + 30);
        Collection<String> teams = minecraft.world.getScoreboard().getTeamNames();
        for (String team : teams) { listWidth = Math.max(listWidth, font.getStringWidth(team) + 30); }
        xSize = listWidth + 12;
        super.init();

        int listY = guiTop + BUTTON_HEIGHT + (PADDING * 2);
        listWidget = new TeamListWidget(guiLeft + PADDING, listY, listWidth, LIST_HEIGHT, teams, font);

        int buttonY = listY + LIST_HEIGHT + PADDING;
        buttonOk = new Button(guiLeft + PADDING, buttonY, listWidth, BUTTON_HEIGHT, "Confirm", this);
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

        listWidget.render(mouseX, mouseY, partialTicks);

        minecraft.getTextureManager().bindTexture(TEXTURE);

        int texX = (width - xSize) / 2;
        int texY = (height - ySize) / 2;

        //Left border
        blit(texX, texY, 0, 0, 6, ySize);

        //Center body
        int maxX = guiLeft + xSize - 6;
        for (texX += 6; texX < maxX; texX += 56)
        {
            int w = Math.min(56, maxX - texX);
            blit(texX, texY, 6, 0, w, ySize);
        }

        //Right border
        texX = maxX;
        blit(texX, texY, 62, 0, 6, ySize);

        drawCenteredString(font, HEADER, width / 2, guiTop + 12, 0xFFFFFFFF);

        buttonOk.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (mouseX >= listWidget.getLeft() && mouseX <= listWidget.getRight() && mouseY >= listWidget.getTop() && mouseY <= listWidget.getBottom())
        {
            return listWidget.mouseClicked(mouseX, mouseY, button);
        }
        else if (mouseX >= buttonOk.x && mouseX <= buttonOk.x + buttonOk.getWidth() && mouseY >= buttonOk.y && mouseY <= buttonOk.y + buttonOk.getHeight())
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