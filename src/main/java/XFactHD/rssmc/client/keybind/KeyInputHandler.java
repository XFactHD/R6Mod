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

package XFactHD.rssmc.client.keybind;

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.api.capability.IGunHandler;
import XFactHD.rssmc.api.item.ISpecialLeftClick;
import XFactHD.rssmc.api.item.ISpecialRightClick;
import XFactHD.rssmc.common.capability.gunHandler.GunHandlerStorage;
import XFactHD.rssmc.common.entity.camera.*;
import XFactHD.rssmc.common.items.gadget.ItemPhone;
import XFactHD.rssmc.common.items.gun.ItemGun;
import XFactHD.rssmc.common.net.PacketSwitchCamera;
import XFactHD.rssmc.common.net.keybind.*;
import XFactHD.rssmc.common.utils.helper.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;

public class KeyInputHandler
{
    private int camSwitchCooldown = 0;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event)
    {
        if (camSwitchCooldown > 0) { camSwitchCooldown -= 1; }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onKeyInput(InputEvent.KeyInputEvent event)
    {
        ItemStack stack = getCurrentStack();
        if (KeyBindings.activateGadget.isPressed())
        {
            RainbowSixSiegeMC.NET.sendMessageToServer(new PacketActivateGadget());
        }
        if (KeyBindings.setMarker.isPressed())
        {
            RainbowSixSiegeMC.NET.sendMessageToServer(new PacketSetMarker());
        }
        if (KeyBindings.reload.isPressed() && stack != null && stack.getItem() instanceof ItemGun)
        {
            RainbowSixSiegeMC.NET.sendMessageToServer(new PacketReloadGun());
        }
        //if (KeyBindings.sneakUpright.isKeyDown())
        //{
        //    //TODO: handle speed decrease and volume decrease
        //}
        if (KeyBindings.changeFiremode.isPressed() && stack != null && stack.getItem() instanceof ItemGun)
        {
            RainbowSixSiegeMC.NET.sendMessageToServer(new PacketSwitchFiremode());
        }

        if (stack != null && stack.getItem() instanceof ItemGun && stack.getCapability(GunHandlerStorage.GUN_HANDLER_CAPABILITY, null).isAiming())
        {
            KeyBinding.setKeyBindState(mc().gameSettings.keyBindSprint.getKeyCode(), false);
        }

        Entity renderViewEntity = mc().getRenderViewEntity();
        if (renderViewEntity instanceof AbstractEntityCamera && mc().currentScreen == null)
        {
            for (int i = 0; i < 9; ++i)
            {
                mc().gameSettings.keyBindsHotbar[i].isPressed();
            }
            boolean right = mc().gameSettings.keyBindRight.isPressed();
            boolean left = mc().gameSettings.keyBindLeft.isPressed();
            if (isDrone(renderViewEntity) && ((AbstractEntityCamera)renderViewEntity).isControlledBy(mc().player))
            {
                boolean forward = mc().gameSettings.keyBindForward.isPressed();
                boolean back = mc().gameSettings.keyBindBack.isPressed();
                if (forward || back || right || left)
                {
                    ((AbstractEntityCamera)renderViewEntity).applyMovement(forward, back, right, left);
                }
            }
            if ((!isDrone(renderViewEntity) || !((AbstractEntityCamera)renderViewEntity).isControlledBy(mc().player)) && (right || left) && camSwitchCooldown == 0)
            {
                RainbowSixSiegeMC.NET.sendMessageToServer(new PacketSwitchCamera(right, left));
                camSwitchCooldown = 20;
            }
        }
    }

    @SubscribeEvent
    public void onMouseInput(InputEvent.MouseInputEvent event)
    {
        ItemStack stack = getCurrentStack();
        if (Minecraft.getMinecraft().currentScreen != null || (isPlayerInCamera() && !isPhone(stack))) { return; }
        if (stack != null && stack.getItem() instanceof ItemGun)
        {
            if (!rightPressed && isMouseButtonDown(1))
            {
                rightPressed = true;
                if (ConfigHandler.holdToAim)
                {
                    ((ISpecialRightClick)stack.getItem()).startRightClick(stack, mc().player, mc().world, EnumHand.MAIN_HAND);
                    RainbowSixSiegeMC.NET.sendMessageToServer(new PacketRightClickItem(true));
                }
                else
                {
                    if (getGunHandler().isAiming())
                    {
                        ((ISpecialRightClick)stack.getItem()).stopRightClick(stack, mc().player, mc().world, EnumHand.MAIN_HAND);
                        RainbowSixSiegeMC.NET.sendMessageToServer(new PacketRightClickItem(false));
                    }
                    else
                    {
                        ((ISpecialRightClick)stack.getItem()).startRightClick(stack, mc().player, mc().world, EnumHand.MAIN_HAND);
                        RainbowSixSiegeMC.NET.sendMessageToServer(new PacketRightClickItem(true));
                    }
                }
            }
            else if (rightPressed && !isMouseButtonDown(1))
            {
                rightPressed = false;
                ((ISpecialRightClick)stack.getItem()).stopRightClick(stack, mc().player, mc().world, EnumHand.MAIN_HAND);
                RainbowSixSiegeMC.NET.sendMessageToServer(new PacketRightClickItem(false));
            }
        }
        else
        {
            if (!rightPressed && isMouseButtonDown(1) && stack != null && stack.getItem() instanceof ISpecialRightClick)
            {
                rightPressed = true;
                ((ISpecialRightClick)stack.getItem()).startRightClick(stack, mc().player, mc().world, EnumHand.MAIN_HAND);
                RainbowSixSiegeMC.NET.sendMessageToServer(new PacketRightClickItem(true));
            }
            else if (rightPressed && !isMouseButtonDown(1))
            {
                rightPressed = false;
                if (stack != null && stack.getItem() instanceof ISpecialRightClick)
                {
                    ((ISpecialRightClick)stack.getItem()).stopRightClick(stack, mc().player, mc().world, EnumHand.MAIN_HAND);
                }
                RainbowSixSiegeMC.NET.sendMessageToServer(new PacketRightClickItem(false));
            }
        }

        if (!leftPressed && isMouseButtonDown(0) && stack != null && stack.getItem() instanceof ISpecialLeftClick)
        {
            leftPressed = true;
            ((ISpecialLeftClick)stack.getItem()).startLeftClick(stack, mc().player, mc().world, EnumHand.MAIN_HAND);
            RainbowSixSiegeMC.NET.sendMessageToServer(new PacketLeftClickItem(true));
        }
        else if (leftPressed && !isMouseButtonDown(0))
        {
            leftPressed = false;
            if (stack != null && stack.getItem() instanceof ISpecialLeftClick)
            {
                ((ISpecialLeftClick)stack.getItem()).stopLeftClick(stack, mc().player, mc().world, EnumHand.MAIN_HAND);
            }
            RainbowSixSiegeMC.NET.sendMessageToServer(new PacketLeftClickItem(false));
        }
    }

    @SubscribeEvent
    public void onMouseEvent(MouseEvent event)
    {
        Entity renderViewEntity = mc().getRenderViewEntity();
        if (renderViewEntity instanceof AbstractEntityCamera && mc().currentScreen == null)
        {
            if (event.getDwheel() != 0) { event.setCanceled(true); }
            if (event.getButton() == Math.abs(mc().gameSettings.keyBindAttack.getKeyCode()))
            {
                event.setCanceled(true);
                if (renderViewEntity instanceof EntityTwitchDroneCam)
                {
                    ((EntityTwitchDroneCam)renderViewEntity).getDrone().zap();
                }
            }
            if (event.getDx() != 0 || event.getDy() != 0)
            {
                event.setCanceled(true);
                ((AbstractEntityCamera)renderViewEntity).applyMouseMovement(event.getDx(), event.getDy());
            }
        }
    }

    private ItemStack getCurrentStack()
    {
        return Minecraft.getMinecraft().player.inventory.getStackInSlot(Minecraft.getMinecraft().player.inventory.currentItem);
    }

    private Minecraft mc()
    {
        return Minecraft.getMinecraft();
    }

    //0 is attack/left click, 1 is use/right click
    private boolean isMouseButtonDown(int index)
    {
        switch (index)
        {
            case 0: return Mouse.isButtonDown(Minecraft.getMinecraft().gameSettings.keyBindAttack.getKeyCode() + 100);
            case 1: return Mouse.isButtonDown(Minecraft.getMinecraft().gameSettings.keyBindUseItem.getKeyCode() + 100);
        }
        return false;
    }

    private boolean isDrone(Entity entity)
    {
        return entity instanceof EntityTwitchDroneCam || entity instanceof EntityDroneCam || entity instanceof EntityYokaiCam;
    }

    private boolean isPhone(ItemStack stack)
    {
        return stack != null && stack.getItem() instanceof ItemPhone;
    }

    private boolean isPlayerInCamera()
    {
        return mc().getRenderViewEntity() instanceof AbstractEntityCamera;
    }

    private IGunHandler getGunHandler()
    {
        return getCurrentStack().getCapability(GunHandlerStorage.GUN_HANDLER_CAPABILITY, null);
    }
}
