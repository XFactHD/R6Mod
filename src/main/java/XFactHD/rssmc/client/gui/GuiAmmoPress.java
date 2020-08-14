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

import XFactHD.rssmc.common.blocks.survival.TileEntityAmmoPress;
import XFactHD.rssmc.common.gui.ContainerAmmoPress;
import XFactHD.rssmc.common.utils.Reference;
import XFactHD.rssmc.common.utils.helper.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.List;

public class GuiAmmoPress extends GuiContainerBase
{
    private ContainerAmmoPress container;
    private ResourceLocation texLoc = null;

    public GuiAmmoPress(TileEntityAmmoPress te, EntityPlayer player)
    {
        super(new ContainerAmmoPress(te, player.inventory));
        setGuiSize(176, 184);
        container = (ContainerAmmoPress)inventorySlots;
        texLoc = new ResourceLocation(Reference.MOD_ID, "textures/gui/gui_ammo_press" + (true ? "_energy" : "") + ".png");
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        bindTexture(texLoc);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, 176, 184);

        int scaledProgress = (int)(38 * container.getProgressScaled());
        drawTexturedModalRect(guiLeft + 65, guiTop + 21, 176, 0, scaledProgress, 61);


        {
            int scaledHeight = (int)(70 * (1 - container.getEnergyStoredScaled()));
            drawTexturedModalRect(guiLeft + 10, guiTop + 17 + scaledHeight, 215, scaledHeight + 1, 12, 70 - scaledHeight);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        if (mouseX >= guiLeft + 10 && mouseX <= guiLeft + 22 && mouseY >= guiTop + 17 && mouseY <= guiTop + 87)
        {
            List<String> strings = Collections.singletonList(I18n.format("desc.rssmc:energyStored.name") + ": " + container.getEnergyStored() + "/" + container.getMaxEnergy() + " FE");
            drawHoveringText(strings, mouseX - guiLeft, mouseY - guiTop);
        }
    }
}