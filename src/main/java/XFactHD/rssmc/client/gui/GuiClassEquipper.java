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
import XFactHD.rssmc.client.gui.controls.GuiButtonOperator;
import XFactHD.rssmc.common.data.EnumGadget;
import XFactHD.rssmc.common.data.EnumGun;
import XFactHD.rssmc.common.data.EnumOperator;
import XFactHD.rssmc.common.gui.ContainerClassEquipper;
import XFactHD.rssmc.common.net.PacketSetOperator;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiCheckBox;

import java.io.IOException;
import java.util.Collections;

@SuppressWarnings("RedundantIfStatement")
public class GuiClassEquipper extends GuiContainerBase
{
    private static final int PAGE_CHOOSE_OPERATOR = 0;
    private static final int PAGE_CHOOSE_EQUIPMENT = 1;

    private EntityPlayer player;
    private int currentScreen = 0;
    private EnumOperator chosenOperator = null;
    private EnumGun[] possiblePrimaries = null;
    private EnumGun[] possibleSecondaries = null;
    private EnumGadget[] possibleGadgets = null;
    private EnumGun chosenPrimary = null;
    private EnumGun chosenSecondary = null;
    private EnumGadget chosenGadget = null;

    public GuiClassEquipper(EntityPlayer player)
    {
        super(new ContainerClassEquipper());
        this.player = player;
        setGuiSize(210, 210);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        addButtons();
        currentScreen = PAGE_CHOOSE_OPERATOR;
    }

    private void addButtons()
    {
        GuiButton mute         = new GuiButtonOperator( 0, guiLeft +   20, guiTop +  20, EnumOperator.MUTE);
        GuiButton smoke        = new GuiButtonOperator( 1, guiLeft +   40, guiTop +  20, EnumOperator.SMOKE);
        GuiButton pulse        = new GuiButtonOperator( 2, guiLeft +   60, guiTop +  20, EnumOperator.PULSE);
        GuiButton castle       = new GuiButtonOperator( 3, guiLeft +   80, guiTop +  20, EnumOperator.CASTLE);
        GuiButton rook         = new GuiButtonOperator( 4, guiLeft +   20, guiTop +  40, EnumOperator.ROOK);
        GuiButton doc          = new GuiButtonOperator( 5, guiLeft +   40, guiTop +  40, EnumOperator.DOC);
        GuiButton tachanka     = new GuiButtonOperator( 6, guiLeft +   60, guiTop +  40, EnumOperator.TACHANKA);
        GuiButton kapkan       = new GuiButtonOperator( 7, guiLeft +   80, guiTop +  40, EnumOperator.KAPKAN);
        GuiButton jaeger       = new GuiButtonOperator( 8, guiLeft +   20, guiTop +  60, EnumOperator.JAEGER);
        GuiButton bandit       = new GuiButtonOperator( 9, guiLeft +   40, guiTop +  60, EnumOperator.BANDIT);
        GuiButton frost        = new GuiButtonOperator(10, guiLeft +   60, guiTop +  60, EnumOperator.FROST);
        GuiButton valkyrie     = new GuiButtonOperator(11, guiLeft +   80, guiTop +  60, EnumOperator.VALKYRIE);
        GuiButton caveira      = new GuiButtonOperator(12, guiLeft +   20, guiTop +  80, EnumOperator.CAVEIRA);
        GuiButton echo         = new GuiButtonOperator(13, guiLeft +   40, guiTop +  80, EnumOperator.ECHO);
        GuiButton mira         = new GuiButtonOperator(14, guiLeft +   60, guiTop +  80, EnumOperator.MIRA);
        GuiButton lesion       = new GuiButtonOperator(15, guiLeft +   80, guiTop +  80, EnumOperator.LESION);
        GuiButton ela          = new GuiButtonOperator(16, guiLeft +   20, guiTop + 100, EnumOperator.ELA);
        GuiButton vigil        = new GuiButtonOperator(17, guiLeft +   40, guiTop + 100, EnumOperator.VIGIL);

        GuiButton sledge       = new GuiButtonOperator(18, guiLeft +  110, guiTop +  20, EnumOperator.SLEDGE);
        GuiButton thatcher     = new GuiButtonOperator(19, guiLeft +  130, guiTop +  20, EnumOperator.THATCHER);
        GuiButton thermite     = new GuiButtonOperator(20, guiLeft +  150, guiTop +  20, EnumOperator.THERMITE);
        GuiButton ash          = new GuiButtonOperator(21, guiLeft +  170, guiTop +  20, EnumOperator.ASH);
        GuiButton montagne     = new GuiButtonOperator(22, guiLeft +  110, guiTop +  40, EnumOperator.MONTAGNE);
        GuiButton twitch       = new GuiButtonOperator(23, guiLeft +  130, guiTop +  40, EnumOperator.TWITCH);
        GuiButton fuze         = new GuiButtonOperator(24, guiLeft +  150, guiTop +  40, EnumOperator.FUZE);
        GuiButton glaz         = new GuiButtonOperator(25, guiLeft +  170, guiTop +  40, EnumOperator.GLAZ);
        GuiButton blitz        = new GuiButtonOperator(26, guiLeft +  110, guiTop +  60, EnumOperator.BLITZ);
        GuiButton iq           = new GuiButtonOperator(27, guiLeft +  130, guiTop +  60, EnumOperator.IQ);
        GuiButton buck         = new GuiButtonOperator(28, guiLeft +  150, guiTop +  60, EnumOperator.BUCK);
        GuiButton blackbeard   = new GuiButtonOperator(29, guiLeft +  170, guiTop +  60, EnumOperator.BLACKBEARD);
        GuiButton capitao      = new GuiButtonOperator(30, guiLeft +  110, guiTop +  80, EnumOperator.CAPITAO);
        GuiButton hibana       = new GuiButtonOperator(31, guiLeft +  130, guiTop +  80, EnumOperator.HIBANA);
        GuiButton jackal       = new GuiButtonOperator(32, guiLeft +  150, guiTop +  80, EnumOperator.JACKAL);
        GuiButton ying         = new GuiButtonOperator(33, guiLeft +  170, guiTop +  80, EnumOperator.YING);
        GuiButton zofia        = new GuiButtonOperator(34, guiLeft +  110, guiTop + 100, EnumOperator.ZOFIA);
        GuiButton dokkaebi     = new GuiButtonOperator(35, guiLeft +  130, guiTop + 100, EnumOperator.DOKKAEBI);

        GuiButton back         = new GuiButton(36, guiLeft +  40, guiTop + 185, 40, 20, I18n.format("gui.rssmc:back.name"));
        GuiButton confirm      = new GuiButton(37, guiLeft +  85, guiTop + 185, 40, 20, I18n.format("gui.rssmc:confirm.name"));
        GuiButton resetLoadout = new GuiButton(38, guiLeft + 130, guiTop + 185, 40, 20, I18n.format("gui.rssmc:reset.name"));

        GuiButton primFirst    = new GuiCheckBox(39, guiLeft + 25, guiTop +  45, "", true);
        GuiButton primSecond   = new GuiCheckBox(40, guiLeft + 48, guiTop +  45, "", false);
        GuiButton primThird    = new GuiCheckBox(41, guiLeft + 71, guiTop +  45, "", false);

        GuiButton secFirst     = new GuiCheckBox(42, guiLeft + 25, guiTop +  79, "", true);
        GuiButton secSecond    = new GuiCheckBox(43, guiLeft + 48, guiTop +  79, "", false);

        GuiButton gadgetFirst  = new GuiCheckBox(44, guiLeft + 25, guiTop + 113, "", true);
        GuiButton gadgetSecond = new GuiCheckBox(45, guiLeft + 48, guiTop + 113, "", false);

        this.buttonList.add(mute);
        this.buttonList.add(smoke);
        this.buttonList.add(pulse);
        this.buttonList.add(castle);
        this.buttonList.add(rook);
        this.buttonList.add(doc);
        this.buttonList.add(tachanka);
        this.buttonList.add(kapkan);
        this.buttonList.add(jaeger);
        this.buttonList.add(bandit);
        this.buttonList.add(frost);
        this.buttonList.add(valkyrie);
        this.buttonList.add(caveira);
        this.buttonList.add(echo);
        this.buttonList.add(mira);
        this.buttonList.add(lesion);
        this.buttonList.add(ela);
        this.buttonList.add(vigil);
        this.buttonList.add(sledge);
        this.buttonList.add(thatcher);
        this.buttonList.add(thermite);
        this.buttonList.add(ash);
        this.buttonList.add(montagne);
        this.buttonList.add(twitch);
        this.buttonList.add(fuze);
        this.buttonList.add(glaz);
        this.buttonList.add(blitz);
        this.buttonList.add(iq);
        this.buttonList.add(buck);
        this.buttonList.add(blackbeard);
        this.buttonList.add(capitao);
        this.buttonList.add(hibana);
        this.buttonList.add(jackal);
        this.buttonList.add(ying);
        this.buttonList.add(zofia);
        this.buttonList.add(dokkaebi);
        this.buttonList.add(confirm);
        this.buttonList.add(back);
        this.buttonList.add(resetLoadout);
        this.buttonList.add(primFirst);
        this.buttonList.add(primSecond);
        this.buttonList.add(primThird);
        this.buttonList.add(secFirst);
        this.buttonList.add(secSecond);
        this.buttonList.add(gadgetFirst);
        this.buttonList.add(gadgetSecond);

        for (GuiButton button : this.buttonList)
        {
            if ((button.id > 16 && button.id < 18) || (button.id > 33 && button.id < 36)) //Season 6-8 ops
            {
                button.enabled = false;
            }
            else if (button.id == 36 || button.id == 37) //Back and Confirm buttons
            {
                button.enabled = false;
            }
            else if (button.id > 38) //Equipment buttons
            {
                button.visible = false;
            }
        }
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        if (currentScreen == PAGE_CHOOSE_EQUIPMENT)
        {
            buttonList.get(37).enabled = (chosenOperator != null && chosenPrimary != null && chosenSecondary != null && chosenGadget != null);

            ((GuiCheckBox)buttonList.get(39)).setIsChecked(chosenPrimary == possiblePrimaries[0]);
            ((GuiCheckBox)buttonList.get(40)).setIsChecked(possiblePrimaries.length > 1 && chosenPrimary == possiblePrimaries[1]);
            ((GuiCheckBox)buttonList.get(41)).setIsChecked(possiblePrimaries.length > 2 && chosenPrimary == possiblePrimaries[2]);

            ((GuiCheckBox)buttonList.get(42)).setIsChecked(chosenSecondary == possibleSecondaries[0]);
            ((GuiCheckBox)buttonList.get(43)).setIsChecked(possibleSecondaries.length > 1 && chosenSecondary == possibleSecondaries[1]);

            ((GuiCheckBox)buttonList.get(44)).setIsChecked(chosenGadget == possibleGadgets[0]);
            ((GuiCheckBox)buttonList.get(45)).setIsChecked(possibleGadgets.length > 1 && chosenGadget == possibleGadgets[1]);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        bindGuiTexture("gui_class_equipper");
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, 210, 210);

        if (currentScreen == PAGE_CHOOSE_EQUIPMENT)
        {
            //Operator name
            drawCenteredString(fontRendererObj, chosenOperator.getDisplayName(), guiLeft + 105, guiTop + 5, -1);

            //Primaries
            if (possiblePrimaries.length >= 1)
            {
                ItemStack primaryOne   = possiblePrimaries[0].getGunItemStack();
                bindGuiTexture("widgets/slot_beweled");
                drawTexturedModalRect(guiLeft + 20, guiTop + 25, 0, 0, 21, 32);
                renderStack(primaryOne, guiLeft + 23, guiTop + 28);
            }
            if (possiblePrimaries.length >= 2)
            {
                ItemStack primaryTwo   = possiblePrimaries[1].getGunItemStack();
                bindGuiTexture("widgets/slot_beweled");
                drawTexturedModalRect(guiLeft + 43, guiTop + 25, 0, 0, 21, 32);
                renderStack(primaryTwo, guiLeft + 46, guiTop + 28);
            }
            if (possiblePrimaries.length == 3)
            {
                ItemStack primaryThree = possiblePrimaries[2].getGunItemStack();
                bindGuiTexture("widgets/slot_beweled");
                drawTexturedModalRect(guiLeft + 66, guiTop + 25, 0, 0, 21, 32);
                renderStack(primaryThree, guiLeft + 69, guiTop + 28);
            }

            //Secondaries
            if (possibleSecondaries.length >= 1)
            {
                ItemStack secondaryOne = possibleSecondaries[0].getGunItemStack();
                bindGuiTexture("widgets/slot_beweled");
                drawTexturedModalRect(guiLeft + 20, guiTop + 59, 0, 0, 21, 32);
                renderStack(secondaryOne, guiLeft + 23, guiTop + 62);
            }
            if (possibleSecondaries.length == 2)
            {
                ItemStack secondaryTwo = possibleSecondaries[1].getGunItemStack();
                bindGuiTexture("widgets/slot_beweled");
                drawTexturedModalRect(guiLeft + 43, guiTop + 59, 0, 0, 21, 32);
                renderStack(secondaryTwo, guiLeft + 46, guiTop + 62);
            }

            //Gadgets
            if (possibleGadgets.length >= 1)
            {
                ItemStack gadgetOne = possibleGadgets[0].getGadgetItemStack();
                bindGuiTexture("widgets/slot_beweled");
                drawTexturedModalRect(guiLeft + 20, guiTop + 93, 0, 0, 21, 32);
                renderStackSafe(gadgetOne, guiLeft + 23, guiTop + 96);
            }
            if (possibleGadgets.length >= 2)
            {
                ItemStack gadgetTwo = possibleGadgets[1].getGadgetItemStack();
                bindGuiTexture("widgets/slot_beweled");
                drawTexturedModalRect(guiLeft + 43, guiTop + 93, 0, 0, 21, 32);
                renderStackSafe(gadgetTwo, guiLeft + 46, guiTop + 96);
            }
            if (possibleGadgets.length == 3)
            {
                ItemStack gadgetThree = possibleGadgets[2].getGadgetItemStack();
                bindGuiTexture("widgets/slot_beweled");
                drawTexturedModalRect(guiLeft + 66, guiTop + 93, 0, 0, 21, 32);
                renderStackSafe(gadgetThree, guiLeft + 69, guiTop + 96);
            }
        }
        else
        {
            //Headline
            drawCenteredString(fontRendererObj, I18n.format("desc.rssmc:choose_operator.name"), guiLeft + 105, guiTop + 5, -1);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        if (currentScreen == PAGE_CHOOSE_OPERATOR)
        {
            for (GuiButton button : buttonList)
            {
                if (button instanceof GuiButtonOperator && button.isMouseOver())
                {
                    drawHoveringText(Collections.singletonList(((GuiButtonOperator)button).getOperator().getDisplayName()), mouseX - guiLeft - 5, mouseY - guiTop + 20);
                }
            }
        }
        else
        {
            mouseX -= guiLeft;
            mouseY -= guiTop;
            if (mouseY >= 25 && mouseY <= 56)
            {
                if (mouseX >= 20 && mouseX <= 40 && possiblePrimaries.length > 0)
                {
                    drawHoveringText(Collections.singletonList(possiblePrimaries[0].getDisplayName()), mouseX - 5, mouseY + 20);
                }
                else if (mouseX >= 43 && mouseX <= 63 && possiblePrimaries.length > 1)
                {
                    drawHoveringText(Collections.singletonList(possiblePrimaries[1].getDisplayName()), mouseX - 5, mouseY + 20);
                }
                else if (mouseX >= 66 && mouseX <= 86 && possiblePrimaries.length > 2)
                {
                    drawHoveringText(Collections.singletonList(possiblePrimaries[2].getDisplayName()), mouseX - 5, mouseY + 20);
                }
            }
            else if (mouseY >= 59 && mouseY <= 90)
            {
                if (mouseX >= 20 && mouseX <= 40 && possibleSecondaries.length > 0)
                {
                    drawHoveringText(Collections.singletonList(possibleSecondaries[0].getDisplayName()), mouseX - 5, mouseY + 20);
                }
                else if (mouseX >= 43 && mouseX <= 63 && possibleSecondaries.length > 1)
                {
                    drawHoveringText(Collections.singletonList(possibleSecondaries[1].getDisplayName()), mouseX - 5, mouseY + 20);
                }
                else if (mouseX >= 66 && mouseX <= 86 && possibleSecondaries.length > 2)
                {
                    drawHoveringText(Collections.singletonList(possibleSecondaries[2].getDisplayName()), mouseX - 5, mouseY + 20);
                }
            }
            else if (mouseY >= 93 && mouseY <= 124)
            {
                if (mouseX >= 20 && mouseX <= 40 && possibleGadgets.length > 0)
                {
                    drawHoveringText(Collections.singletonList(possibleGadgets[0].getDisplayName()), mouseX - 5, mouseY + 20);
                }
                else if (mouseX >= 43 && mouseX <= 63 && possibleGadgets.length > 1)
                {
                    drawHoveringText(Collections.singletonList(possibleGadgets[1].getDisplayName()), mouseX - 5, mouseY + 20);
                }
                else if (mouseX >= 66 && mouseX <= 86 && possibleGadgets.length > 2)
                {
                    drawHoveringText(Collections.singletonList(possibleGadgets[2].getDisplayName()), mouseX - 5, mouseY + 20);
                }
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (currentScreen == PAGE_CHOOSE_OPERATOR && button.id < 36) //Operator
        {
            chosenOperator = ((GuiButtonOperator)button).getOperator();
            prepareGunsAndGadgets();
            prepareButtons(PAGE_CHOOSE_EQUIPMENT);
            chosenPrimary = possiblePrimaries[0];
            chosenSecondary = possibleSecondaries[0];
            chosenGadget = possibleGadgets[0];
            currentScreen = PAGE_CHOOSE_EQUIPMENT;
        }
        else if (currentScreen == PAGE_CHOOSE_EQUIPMENT && button.id == 36) //Back
        {
            chosenOperator = null;
            possiblePrimaries = null;
            possibleSecondaries = null;
            possibleGadgets = null;
            chosenPrimary = null;
            chosenSecondary = null;
            chosenGadget = null;
            prepareButtons(PAGE_CHOOSE_OPERATOR);
            currentScreen = PAGE_CHOOSE_OPERATOR;
        }
        else if (currentScreen == PAGE_CHOOSE_EQUIPMENT && button.id == 37) //Confirm
        {
            finishSetup();
        }
        else if (currentScreen == PAGE_CHOOSE_OPERATOR && button.id == 38) //Reset
        {
            resetPlayerLoadout();
        }
        else if (currentScreen == PAGE_CHOOSE_EQUIPMENT && button.id > 38 && button.id < 42) //Primary weapon
        {
            chosenPrimary = possiblePrimaries[button.id - 39];
        }
        else if (currentScreen == PAGE_CHOOSE_EQUIPMENT && button.id > 41 && button.id < 44) //Secondary weapon
        {
            chosenSecondary = possibleSecondaries[button.id - 42];
        }
        else if (currentScreen == PAGE_CHOOSE_EQUIPMENT && button.id > 43) //Gadget
        {
            chosenGadget = possibleGadgets[button.id - 44];
        }
    }

    private void prepareGunsAndGadgets()
    {
        possiblePrimaries = chosenOperator.getPrimaries();
        possibleSecondaries = chosenOperator.getSecondaries();
        possibleGadgets = chosenOperator.getGadgets();
    }

    private void prepareButtons(int nextScreen)
    {
        if (nextScreen == PAGE_CHOOSE_EQUIPMENT)
        {
            for (GuiButton button : buttonList)
            {
                if (button.id < 36) //Operator buttons
                {
                    button.visible = false;
                }
                else if (button.id > 38) //Equipment buttons
                {
                    switch (button.id)
                    {
                        case 39: button.visible = possiblePrimaries.length >=1; break;
                        case 40: button.visible = possiblePrimaries.length >= 2;break;
                        case 41: button.visible = possiblePrimaries.length == 3; break;

                        case 42: button.visible = possibleSecondaries.length >=1; break;
                        case 43: button.visible = possibleSecondaries.length ==2; break;

                        case 44: button.visible = possibleGadgets.length >=1; break;
                        case 45: button.visible = possibleGadgets.length ==2; break;
                    }
                }
                else if (button.id == 36) //Back
                {
                    button.enabled = true;
                }
                else if (button.id == 37) //Confirm
                {
                    button.enabled = true;
                }
                else //Reset loadout
                {
                    button.enabled = false;
                }
            }
        }
        else if (nextScreen == PAGE_CHOOSE_OPERATOR)
        {
            for (GuiButton button : buttonList)
            {
                if (button.id < 36)
                {
                    button.visible = true;
                }
                else if (button.id > 38)
                {
                    button.visible = false;
                }
                else if (button.id == 36)
                {
                    button.enabled = false;
                }
                else if (button.id == 37)
                {
                    button.enabled = false;
                }
                else
                {
                    button.enabled = true;
                }
            }
        }
    }

    private void finishSetup()
    {
        RainbowSixSiegeMC.NET.sendMessageToServer(new PacketSetOperator(chosenOperator, chosenPrimary, chosenSecondary, chosenGadget));
        player.closeScreen();
    }

    private void resetPlayerLoadout()
    {
        RainbowSixSiegeMC.NET.sendMessageToServer(new PacketSetOperator(null, null, null, null));
        player.closeScreen();
    }
}