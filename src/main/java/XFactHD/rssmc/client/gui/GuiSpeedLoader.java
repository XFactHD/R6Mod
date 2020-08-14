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
import XFactHD.rssmc.common.gui.ContainerSpeedLoader;
import XFactHD.rssmc.common.net.PacketSpeedLoaderLoad;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

public class GuiSpeedLoader extends GuiContainerBase
{
    private ContainerSpeedLoader container;

    public GuiSpeedLoader(EntityPlayer player)
    {
        super(new ContainerSpeedLoader(player));
        container = (ContainerSpeedLoader)inventorySlots;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        buttonList.add(new GuiButton(0, guiLeft + 59, guiTop + 35, 58, 20, I18n.format("desc.rssmc:load_mag.name")));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        bindGuiTexture("gui_speed_loader");
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, 176, 144);
        //TODO: draw green arrow, when mag can be loaded
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0 && container.getItemHandler().getStackInSlot(0) != null && container.getItemHandler().getStackInSlot(1) != null)
        {
            RainbowSixSiegeMC.NET.sendMessageToServer(new PacketSpeedLoaderLoad());
        }
    }
}