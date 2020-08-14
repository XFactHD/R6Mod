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

import XFactHD.rssmc.common.utils.Reference;
import XFactHD.rssmc.common.utils.helper.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public abstract class GuiContainerBase extends GuiContainer
{
    public GuiContainerBase(Container container)
    {
        super(container);
    }

    protected void bindGuiTexture(String name)
    {
        bindTexture(new ResourceLocation(Reference.MOD_ID, "textures/gui/" + name + ".png"));
    }

    protected void bindTexture(ResourceLocation resLoc)
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(resLoc);
    }

    //WARNING: can be a performance penalty
    protected void renderStackSafe(ItemStack stack, int x, int y)
    {
        if (stack == null) { return; }
        try
        {
            renderStack(stack, x, y);
        }
        catch (NullPointerException e)
        {
            LogHelper.error("Failed to render ItemStack at X=" + x + ", Y=" + y + "! This is an error!");
            e.printStackTrace();
        }
    }

    protected void renderStack(ItemStack stack, int x, int y)
    {
        Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(stack, x, y);
    }
}