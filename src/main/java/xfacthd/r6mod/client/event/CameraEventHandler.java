package xfacthd.r6mod.client.event;

import net.minecraft.client.*;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.lwjgl.glfw.GLFW;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.api.entity.ICameraEntity;
import xfacthd.r6mod.client.R6Client;
import xfacthd.r6mod.client.util.input.CameraMouseHelper;
import xfacthd.r6mod.client.util.input.KeyBindings;
import xfacthd.r6mod.common.net.NetworkHandler;
import xfacthd.r6mod.common.net.packets.camera.PacketCameraExit;
import xfacthd.r6mod.common.net.packets.camera.PacketCameraSwitch;

import java.lang.reflect.*;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = R6Mod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CameraEventHandler
{
    private static Field ActiveRenderInfo_height;
    private static Field ActiveRenderInfo_prevHeight;

    private static CameraMouseHelper camMouseHelper;
    private static boolean firstEvent = false;
    private static boolean wasInGui = false;
    private static byte lastMask = 0;

    @SubscribeEvent
    public static void onRenderHand(final RenderHandEvent event)
    {
        if (Minecraft.getInstance().getRenderViewEntity() instanceof ICameraEntity)
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onClientTick(final TickEvent.ClientTickEvent event)
    {
        if (event.phase != TickEvent.Phase.START) { return; }

        Entity viewEntity = mc().getRenderViewEntity();
        boolean isInGui = inGui();
        if (viewEntity instanceof ICameraEntity && !isInGui && !wasInGui)
        {
            double diffX = camMouseHelper.getDiffMouseX();
            double diffY = camMouseHelper.getDiffMouseY();
            ((ICameraEntity<?>)viewEntity).handleMouseMovement(diffX, diffY);

            sendKeyInputPacket(mc().gameSettings, viewEntity);

            unPressKeybinds(mc().gameSettings);

            if (KeyBindings.KEY_EXIT_CAM.isPressed())
            {
                NetworkHandler.sendToServer(new PacketCameraExit());
            }
            else if (KeyBindings.KEY_PRIOR_CAM.isPressed())
            {
                NetworkHandler.sendToServer(new PacketCameraSwitch(false));
            }
            else if (KeyBindings.KEY_NEXT_CAM.isPressed())
            {
                NetworkHandler.sendToServer(new PacketCameraSwitch(true));
            }
        }
        else if (!(viewEntity instanceof ICameraEntity))
        {
            //Fixes cases where a keybind was pressed accidentally before entering a cam and the keybind being applied when entering a cam
            KeyBindings.KEY_PRIOR_CAM.setPressed(false);
            KeyBindings.KEY_NEXT_CAM.setPressed(false);
            KeyBindings.KEY_EXIT_CAM.setPressed(false);
        }
        wasInGui = isInGui;
    }

    @SubscribeEvent
    public static void onMouseClick(final InputEvent.RawMouseEvent event)
    {
        if (inGui()) { return; }

        Entity viewEntity = mc().getRenderViewEntity();
        if (viewEntity instanceof ICameraEntity)
        {
            if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT)
            {
                ((ICameraEntity<?>)viewEntity).handleLeftClick(event.getAction() == GLFW.GLFW_PRESS);
                event.setCanceled(true);
            }
            else if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT)
            {
                if (firstEvent) { firstEvent = false; }
                else
                {
                    ((ICameraEntity<?>) viewEntity).handleRightClick(event.getAction() == GLFW.GLFW_PRESS);
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onDisconnect(final ClientPlayerNetworkEvent.LoggedOutEvent event)
    {
        R6Client.getCameraManager().onPlayerDisconnect();
    }

    public static void onEnterCamera()
    {
        firstEvent = true;

        //If player is currently in third person, reset it to normal view
        if (mc().gameSettings.func_243230_g() != PointOfView.FIRST_PERSON) { mc().gameSettings.func_243229_a(PointOfView.FIRST_PERSON); }

        //Remove height interpolation when entering camera
        ActiveRenderInfo info = mc().gameRenderer.getActiveRenderInfo();
        try
        {
            //noinspection ConstantConditions
            float height = mc().renderViewEntity.getEyeHeight();
            ActiveRenderInfo_prevHeight.set(info, height);
            ActiveRenderInfo_height.set(info, height);
        }
        catch (IllegalAccessException e) { throw new RuntimeException(e); }
    }

    public static void onSwitchCamera()
    {
        //Remove height interpolation when switching cameras
        ActiveRenderInfo info = mc().gameRenderer.getActiveRenderInfo();
        try
        {
            //noinspection ConstantConditions
            float height = mc().renderViewEntity.getEyeHeight();
            ActiveRenderInfo_prevHeight.set(info, height);
            ActiveRenderInfo_height.set(info, height);
        }
        catch (IllegalAccessException e) { throw new RuntimeException(e); }
    }

    public static void onLeaveCamera()
    {
        //Remove height interpolation when exiting camera
        ActiveRenderInfo info = mc().gameRenderer.getActiveRenderInfo();
        try
        {
            //noinspection ConstantConditions
            float height = mc().player.getEyeHeight();
            ActiveRenderInfo_prevHeight.set(info, height);
            ActiveRenderInfo_height.set(info, height);
        }
        catch (IllegalAccessException e) { throw new RuntimeException(e); }
    }

    public static void transformFunctions()
    {
        ActiveRenderInfo_prevHeight = ObfuscationReflectionHelper.findField(ActiveRenderInfo.class, "field_216802_n");
        ActiveRenderInfo_height = ObfuscationReflectionHelper.findField(ActiveRenderInfo.class, "field_216801_m");

        //ObfuscationReflectionHelper already makes the methods accessible
        Method mouseHelper_cursorPosCallback = ObfuscationReflectionHelper.findMethod(MouseHelper.class, "func_198022_b", long.class, double.class, double.class);
        Method MouseHelper_mouseButtonCallback = ObfuscationReflectionHelper.findMethod(MouseHelper.class, "func_198023_a", long.class, int.class, int.class, int.class);
        Method MouseHelper_scrollCallback = ObfuscationReflectionHelper.findMethod(MouseHelper.class, "func_198020_a", long.class, double.class, double.class);

        camMouseHelper = new CameraMouseHelper(mouseHelper_cursorPosCallback);

        GLFW.glfwSetCursorPosCallback(mc().getMainWindow().getHandle(), (handle, xPos, yPos) -> mc().execute(() -> camMouseHelper.cursorPosCallback(handle, xPos, yPos)));
        //InputMappings.setMouseCallbacks(mc().getMainWindow().getHandle(),
        //        (handle, xPos, yPos) -> mc().execute(() -> camMouseHelper.cursorPosCallback(handle, xPos, yPos)),
        //        (handle, button, action, mods) -> mc().execute(() ->
        //        {
        //            try { MouseHelper_mouseButtonCallback.invoke(mc().mouseHelper, handle, button, action, mods); }
        //            catch (IllegalAccessException | InvocationTargetException e) { throw new RuntimeException(e); }
        //        }),
        //        (handle, xOff, yOff) -> mc().execute(() ->
        //        {
        //            try { MouseHelper_scrollCallback.invoke(mc().mouseHelper, handle, xOff, yOff); }
        //            catch (IllegalAccessException | InvocationTargetException e) { throw new RuntimeException(e); }
        //        }),
        //        ()
        //);
    }

    private static boolean inGui() { return mc().currentScreen != null || mc().isGamePaused(); }

    private static void sendKeyInputPacket(GameSettings settings, Entity entity)
    {
        byte forward = settings.keyBindForward.isKeyDown() ? (byte)1 : (byte)0;
        byte backward = settings.keyBindBack.isKeyDown() ? (byte)1 : (byte)0;
        byte left = settings.keyBindLeft.isKeyDown() ? (byte)1 : (byte)0;
        byte right = settings.keyBindRight.isKeyDown() ? (byte)1 : (byte)0;
        byte jump = settings.keyBindJump.isKeyDown() ? (byte)1 : (byte)0;

        byte mask = buildMask(forward, backward, left, right, jump);
        if (mask != lastMask) { ((ICameraEntity<?>)entity).handleKeyInput(mask); }
        lastMask = mask;
    }

    private static byte buildMask(byte forward, byte backward, byte left, byte right, byte jump)
    {
        byte mask = 0;

        mask |= (forward);
        mask |= (backward << 1);
        mask |= (left << 2);
        mask |= (right << 3);
        mask |= (jump << 4);

        return mask;
    }

    private static void unPressKeybinds(GameSettings settings)
    {
        //Prevent player from toggling perspective in a camera
        settings.keyBindTogglePerspective.unpressKey();

        //Prevent player from moving in a camera
        settings.keyBindSneak.unpressKey();
        settings.keyBindSprint.unpressKey();

        //Prevent player from modifying anything while in a camera
        settings.keyBindAttack.unpressKey();
        settings.keyBindUseItem.unpressKey();
        settings.keyBindPickBlock.unpressKey();
        settings.keyBindDrop.unpressKey();
        settings.keyBindSwapHands.unpressKey();

        //Prevent certain mod actions
        KeyBindings.KEY_RELOAD.unpressKey();
        KeyBindings.KEY_PING.unpressKey();
    }

    private static Minecraft mc() { return Minecraft.getInstance(); }
}