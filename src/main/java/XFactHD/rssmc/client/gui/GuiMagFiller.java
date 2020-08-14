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
import net.minecraft.item.ItemStack;
import java.util.Collections;

public class GuiMagFiller extends GuiContainerBase
{
    private ContainerMagFiller container;
    private TileEntityMagFiller te;

    public GuiMagFiller(TileEntityMagFiller te, EntityPlayer player)
    {
        super(new ContainerMagFiller(te, player));
        container = (ContainerMagFiller)inventorySlots;
        this.te = te;
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
        ItemStack stack = te.getProcessStack();
        if (stack != null)
        {
            renderStack(stack, guiLeft + 80, guiTop + 73);
            this.itemRender.renderItemOverlayIntoGUI(fontRendererObj, stack, guiLeft + 80, guiTop + 73, "");
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        ItemStack stack = te.getProcessStack();
        if (mouseX >= guiLeft + 12 && mouseX <= guiLeft + 20 && mouseY >= guiTop + 25 && mouseY <= guiTop + 77)
        {
            String text = Integer.toString(container.getEnergyStored()) + "/10000 RF";
            drawHoveringText(Collections.singletonList(text), mouseX - guiLeft, mouseY - guiTop);
        }
        else if (mouseX >= guiLeft + 80 && mouseX <= guiLeft + 98 && mouseY >= guiTop + 73 && mouseY <= guiTop + 91 && stack != null)
        {
            drawHoveringText(stack.getTooltip(this.mc.player, this.mc.gameSettings.advancedItemTooltips), mouseX - guiLeft, mouseY - guiTop);
        }
    }
}