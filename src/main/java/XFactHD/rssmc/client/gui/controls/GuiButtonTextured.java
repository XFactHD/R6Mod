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

package XFactHD.rssmc.client.gui.controls;

import XFactHD.rssmc.client.util.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;

public class GuiButtonTextured extends GuiButton
{
    protected TextureAtlasSprite texture;
    protected int textureHeight;
    protected int textureWidth;
    protected int textureX;
    protected int textureY;
    private double maxU;
    private double maxV;
    public boolean active = false;
    private boolean shouldRenderButtonBase = true;
    private boolean shouldRenderTexture = true;

    public GuiButtonTextured(int ID, int x, int y, int width, int height, String buttonText, String spritePath, int textureOffsetX, int textureOffsetY, int textureWidth, int textureHeight)
    {
        this(ID, x, y, width, height, buttonText, spritePath, textureOffsetX, textureOffsetY, textureWidth, textureHeight, 16, 16);
    }

    public GuiButtonTextured(int ID, int x, int y, int width, int height, String buttonText, String spritePath, int textureOffsetX, int textureOffsetY, int textureWidth, int textureHeight, double maxU, double maxV)
    {
        super(ID, x, y, width, height, buttonText);
        this.texture = "".equals(spritePath) ? null : ClientUtils.getSprite(spritePath.contains("rssmc:") ? spritePath : "rssmc:" + spritePath);
        this.textureHeight = textureHeight;
        this.textureWidth = textureWidth;
        this.textureX = x + textureOffsetX;
        this.textureY = y + textureOffsetY;
        this.maxU = maxU;
        this.maxV = maxV;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible && shouldRenderTexture)
        {
            FontRenderer fontrenderer = mc.fontRendererObj;
            mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int hoverState = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            if (shouldRenderButtonBase)
            {
                this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + hoverState * 20, this.width / 2, this.height);
                this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + hoverState * 20, this.width / 2, this.height);
            }
            this.mouseDragged(mc, mouseX, mouseY);
            int color = 14737632;

            if (packedFGColour != 0)
            {
                color = packedFGColour;
            }
            else if (!this.enabled)
            {
                color = 10526880;
            }
            else if (this.hovered)
            {
                color = 16777120;
            }

            this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, color);

            if (texture != null)
            {
                GlStateManager.color(1F, 1F, 1F, 1F);
                mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                ClientUtils.drawTexturedModalRectFromSprite(textureX, textureY, texture, textureWidth, textureHeight, maxU, maxV);
            }
        }
    }

    public void setShouldRenderButtonBase(boolean shouldRenderButtonBase)
    {
        this.shouldRenderButtonBase = shouldRenderButtonBase;
    }

    public boolean getShouldRenderButtonBase()
    {
        return shouldRenderButtonBase;
    }

    public void setShouldRenderTexture(boolean shouldRenderTexture)
    {
        this.shouldRenderTexture = shouldRenderTexture;
    }

    public boolean getShouldRenderTexture()
    {
        return shouldRenderTexture;
    }
}