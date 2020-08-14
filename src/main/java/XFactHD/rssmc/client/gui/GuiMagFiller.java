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

import XFactHD.rssmc.common.blocks.survival.TileEntityMagFiller;
import XFactHD.rssmc.common.gui.ContainerMagFiller;
import net.minecraft.entity.player.EntityPlayer;

public class GuiMagFiller extends GuiContainerBase
{
    private ContainerMagFiller container;

    public GuiMagFiller(TileEntityMagFiller te, EntityPlayer player)
    {
        super(new ContainerMagFiller(te, player));
        container = (ContainerMagFiller)inventorySlots;
        setGuiSize(176, 183);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        bindGuiTexture("gui_mag_filler");
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, 176, 183);
        float stored = (float)container.getEnergyStored() / 10000F;
        int offset = (int)(52F * (1F - stored));
        drawTexturedModalRect(guiLeft + 12, guiTop + 25 + offset, 176, 15 + offset, 8, 52 - offset);
        if (container.isActive())
        {
            drawTexturedModalRect(guiLeft + 83, guiTop + 44, 176, 0, 10, 15);
        }
        //TODO: draw stack being processed
    }
}