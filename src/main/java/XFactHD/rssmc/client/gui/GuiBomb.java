/*  Copyright (C) <2017>  <XFactHD>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see http://www.gnu.org/licenses. */

package XFactHD.rssmc.client.gui;

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.client.gui.controls.GuiButtonTextured;
import XFactHD.rssmc.common.gui.ContainerBomb;
import XFactHD.rssmc.common.net.PacketSetBombRange;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import java.io.IOException;

public class GuiBomb extends GuiContainerBase
{
    private static final String TEXTURE_MINUS = "gui/widgets/minus.png";
    private static final String TEXTURE_PLUS = "gui/widgets/plus.png";
    private String northLocalized = I18n.format("gui.rssmc:north.name");
    private String eastLocalized = I18n.format("gui.rssmc:east.name");
    private String southLocalized = I18n.format("gui.rssmc:south.name");
    private String westLocalized = I18n.format("gui.rssmc:west.name");
    private String locationLocalized = I18n.format("gui.rssmc:location.name");
    private String confirmLocalized = I18n.format("gui.rssmc:confirm.name");

    private BlockPos pos;
    private int rangeNorth = 0;
    private int rangeEast = 0;
    private int rangeSouth = 0;
    private int rangeWest = 0;
    private int location = 0;

    public GuiBomb(BlockPos pos)
    {
        super(new ContainerBomb());
        setGuiSize(130, 200);
        this.pos = pos;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        buttonList.add(new GuiButtonTextured(0, guiLeft + 15, guiTop +  15, 20, 20, "", TEXTURE_MINUS, 2, 2, 16, 16));
        buttonList.add(new GuiButtonTextured(1, guiLeft + 95, guiTop +  15, 20, 20, "", TEXTURE_PLUS,  2, 2, 16, 16));
        buttonList.add(new GuiButtonTextured(2, guiLeft + 15, guiTop +  45, 20, 20, "", TEXTURE_MINUS, 2, 2, 16, 16));
        buttonList.add(new GuiButtonTextured(3, guiLeft + 95, guiTop +  45, 20, 20, "", TEXTURE_PLUS,  2, 2, 16, 16));
        buttonList.add(new GuiButtonTextured(4, guiLeft + 15, guiTop +  75, 20, 20, "", TEXTURE_MINUS, 2, 2, 16, 16));
        buttonList.add(new GuiButtonTextured(5, guiLeft + 95, guiTop +  75, 20, 20, "", TEXTURE_PLUS,  2, 2, 16, 16));
        buttonList.add(new GuiButtonTextured(6, guiLeft + 15, guiTop + 105, 20, 20, "", TEXTURE_MINUS, 2, 2, 16, 16));
        buttonList.add(new GuiButtonTextured(7, guiLeft + 95, guiTop + 105, 20, 20, "", TEXTURE_PLUS,  2, 2, 16, 16));
        buttonList.add(new GuiButtonTextured(8, guiLeft + 15, guiTop + 135, 20, 20, "", TEXTURE_MINUS, 2, 2, 16, 16));
        buttonList.add(new GuiButtonTextured(9, guiLeft + 95, guiTop + 135, 20, 20, "", TEXTURE_PLUS,  2, 2, 16, 16));
        buttonList.add(new GuiButton(10, guiLeft + 40, guiTop + 165, 50, 20, confirmLocalized));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        bindGuiTexture("gui_set_obj_range");
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, 130, 200);
        drawCenteredString(fontRendererObj, northLocalized     + ": " + Integer.toString(rangeNorth), guiLeft + 65, guiTop +  21, 16777215);
        drawCenteredString(fontRendererObj, eastLocalized      + ": " + Integer.toString(rangeEast),  guiLeft + 65, guiTop +  51, 16777215);
        drawCenteredString(fontRendererObj, southLocalized     + ": " + Integer.toString(rangeSouth), guiLeft + 65, guiTop +  81, 16777215);
        drawCenteredString(fontRendererObj, westLocalized      + ": " + Integer.toString(rangeWest),  guiLeft + 65, guiTop + 111, 16777215);
        drawCenteredString(fontRendererObj, locationLocalized  + ": " + (location == 0 ? "A" : "B"),  guiLeft + 65, guiTop + 141, 16777215);
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        buttonList.get( 0).enabled = rangeNorth > 0;
        buttonList.get( 1).enabled = rangeNorth + rangeSouth < 16;
        buttonList.get( 2).enabled = rangeEast > 0;
        buttonList.get( 3).enabled = rangeEast + rangeWest < 16;
        buttonList.get( 4).enabled = rangeSouth > 0;
        buttonList.get( 5).enabled = rangeNorth + rangeSouth < 16;
        buttonList.get( 6).enabled = rangeWest > 0;
        buttonList.get( 7).enabled = rangeEast + rangeWest < 16;
        buttonList.get( 8).enabled = location == 1;
        buttonList.get( 9).enabled = location == 0;
        buttonList.get(10).enabled = (rangeEast + rangeWest) * (rangeNorth + rangeSouth) > 0;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0) { rangeNorth -= 1; }
        if (button.id == 1) { rangeNorth += 1; }
        if (button.id == 2) { rangeEast  -= 1; }
        if (button.id == 3) { rangeEast  += 1; }
        if (button.id == 4) { rangeSouth -= 1; }
        if (button.id == 5) { rangeSouth += 1; }
        if (button.id == 6) { rangeWest  -= 1; }
        if (button.id == 7) { rangeWest  += 1; }
        if (button.id == 8) { location   -= 1; }
        if (button.id == 9) { location   += 1; }
        if (button.id == 10)
        {
            RainbowSixSiegeMC.NET.sendMessageToServer(new PacketSetBombRange(pos, rangeNorth, rangeEast, rangeSouth, rangeWest, location));
            Minecraft.getMinecraft().player.closeScreen();
        }
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();
        if ((rangeEast + rangeWest) * (rangeNorth + rangeSouth) == 0)
        {
            RainbowSixSiegeMC.NET.sendMessageToServer(new PacketSetBombRange(pos, 0, 0, 0, 0, 0));
            Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("chat.rssmc:bomb_setup_cancled.name"));
        }
    }
}