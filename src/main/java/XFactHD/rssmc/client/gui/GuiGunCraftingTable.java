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
import XFactHD.rssmc.client.gui.controls.GuiButtonSlot;
import XFactHD.rssmc.client.gui.controls.GuiButtonTextured;
import XFactHD.rssmc.common.blocks.survival.TileEntityGunCraftingTable;
import XFactHD.rssmc.common.capability.itemHandler.ItemHandlerGunCraftingTable;
import XFactHD.rssmc.common.crafting.recipes.RecipeGunCrafting;
import XFactHD.rssmc.common.data.EnumGun;
import XFactHD.rssmc.common.gui.ContainerGunCraftingTable;
import XFactHD.rssmc.common.net.PacketGunCrafterAddAmmo;
import XFactHD.rssmc.common.net.PacketGunCrafterAddGun;
import XFactHD.rssmc.common.net.PacketGunCrafterConsumeItems;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.io.IOException;
import java.util.Collections;
import java.util.Locale;

public class GuiGunCraftingTable extends GuiContainerBase
{
    private TileEntityGunCraftingTable te;
    private EntityPlayer player;
    private boolean infoOpen = false;
    private int currentIndex = 0;
    private EnumGun currentGun = EnumGun.C8_SFW;
    private RecipeGunCrafting recipe = RecipeGunCrafting.EMPTY_RECIPE;
    private String weaponNameLocalized = "";
    private String infoDescLocalized = I18n.format("desc.rssmc:weaponstats.name");
    private String rpmDescLocalized = I18n.format("desc.rssmc:weaponrpm.name");
    private String dmgDescLocalized = I18n.format("desc.rssmc:weapondmg.name");
    private String dmgSDDescLocalized = I18n.format("desc.rssmc:weapondmgsilenced.name");
    private String magCapDescLocalized = I18n.format("desc.rssmc:weaponmagcap.name");
    private String fullAutoDescLocalized = I18n.format("desc.rssmc:weaponfullauto.name");

    public GuiGunCraftingTable(TileEntityGunCraftingTable te, EntityPlayer player)
    {
        super(new ContainerGunCraftingTable(te, player));
        setGuiSize(176, 222);
        this.te = te;
        this.player = player;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        GuiButtonTextured back  = new GuiButtonTextured(0, guiLeft +  30, guiTop - 9, 16, 16, "", "gui/widgets/back",    0, 0, 16, 16);
        GuiButtonTextured forth = new GuiButtonTextured(1, guiLeft + 130, guiTop - 9, 16, 16, "", "gui/widgets/forward", 0, 0, 16, 16);
        GuiButtonSlot getGun    = new GuiButtonSlot(2, guiLeft + 133, guiTop + 29);
        GuiButtonTextured info  = new GuiButtonTextured(3, guiLeft + 173, guiTop - 30, 28, 28,"", "gui/widgets/info", 0, 0, 28, 28, 14, 14);
        GuiButtonSlot getAmmo   = new GuiButtonSlot(4, guiLeft + 133, guiTop + 69);
        info.setShouldRenderButtonBase(false);
        getAmmo.enabled = player.capabilities.isCreativeMode;

        buttonList.add(back);
        buttonList.add(forth);
        buttonList.add(getGun);
        buttonList.add(info);
        buttonList.add(getAmmo);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        bindGuiTexture("gui_gun_crafting_table");
        drawTexturedModalRect(guiLeft, guiTop - 30, 0, 0, 176, 222);
        drawCenteredString(fontRendererObj, weaponNameLocalized, guiLeft + 88, guiTop - 5, 14737632);
        EnumGun gun = EnumGun.values()[currentIndex];
        if (infoOpen)
        {
            bindGuiTexture("widgets/info_big");
            GlStateManager.color(1, 1, 1, 1);
            drawTexturedModalRect(guiLeft + 173, guiTop - 30, 0, 0, 140, 100);
            boolean auto = gun.getGunType() != EnumGun.EnumGunType.SHOTGUN && gun.isAutomatic();
            int rpm = auto ? gun.getRoundsPerSecond() * 60 : 1;
            float dmg = gun.getGunBaseDamage();
            float dmgSD = gun.getGunDamageSilenced();
            int magCap = gun.getMagCapacity();
            drawString(fontRendererObj, infoDescLocalized,                  guiLeft + 190, guiTop - 20, -1);
            drawString(fontRendererObj, rpmDescLocalized + " " + rpm,       guiLeft + 190, guiTop,      -1);
            drawString(fontRendererObj, dmgDescLocalized + " " + dmg,       guiLeft + 190, guiTop + 10, -1);
            drawString(fontRendererObj, dmgSDDescLocalized + " " + dmgSD,   guiLeft + 190, guiTop + 20, -1);
            drawString(fontRendererObj, magCapDescLocalized + " " + magCap, guiLeft + 190, guiTop + 30, -1);
            drawString(fontRendererObj, fullAutoDescLocalized + " " + auto, guiLeft + 190, guiTop + 40, -1);
        }
        GlStateManager.color(1, 1, 1, 1);
        RenderHelper.enableGUIStandardItemLighting();
        renderStack(gun.getGunItemStack(),  guiLeft + 134, guiTop + 30);
        renderStack(gun.getMagazineStack(true), guiLeft + 134, guiTop + 70);
        RenderHelper.disableStandardItemLighting();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        if (!player.capabilities.isCreativeMode)
        {
            bindGuiTexture("widgets/red_cross");
            drawTexturedModalRect(133, 69, 0, 0, 18, 18);
        }
        if (mouseX >= guiLeft + 173 && mouseX <= guiLeft + 192 && mouseY >= guiTop - 30 && mouseY <= guiTop - 2)
        {
            drawHoveringText(Collections.singletonList(infoDescLocalized), mouseX - guiLeft, mouseY - guiTop);
        }
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        ((GuiButtonTextured)buttonList.get(3)).setShouldRenderTexture(!infoOpen);
        buttonList.get(0).enabled = currentIndex > 0;
        buttonList.get(1).enabled = currentIndex < EnumGun.values().length - 4;
        currentGun = EnumGun.values()[currentIndex];
        recipe = currentGun.getRecipe();
        weaponNameLocalized = I18n.format("item.rssmc:itemGun_" + EnumGun.values()[currentIndex].toString().toLowerCase(Locale.ENGLISH) + ".name");
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0) { currentIndex -= 1; }
        else if (button.id == 1) { currentIndex += 1; }
        else if (button.id == 2) { handleGunSlotClick(); }
        else if (button.id == 3) { infoOpen = !infoOpen; }
        else if (button.id == 4) { handleAmmoSlotClick(); }
    }

    private void handleGunSlotClick()
    {
        if (player.capabilities.isCreativeMode)
        {
            RainbowSixSiegeMC.NET.sendMessageToServer(new PacketGunCrafterAddGun(currentGun.getGunItemStack()));
            return;
        }
        ItemHandlerGunCraftingTable itemHandler = te.getItemHandler();
        for (ItemStack stack : recipe.getIngredients())
        {
            if (!itemHandler.containsStack(stack))
            {
                return;
            }
        }
        RainbowSixSiegeMC.NET.sendMessageToServer(new PacketGunCrafterConsumeItems(currentGun));
    }

    private void handleAmmoSlotClick()
    {
        RainbowSixSiegeMC.NET.sendMessageToServer(new PacketGunCrafterAddAmmo(currentGun.getMagazineStack(false)));
    }
}