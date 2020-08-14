package xfacthd.r6mod.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.api.capability.ICapabilityGun;
import xfacthd.r6mod.api.interaction.IReloadable;
import xfacthd.r6mod.client.util.*;
import xfacthd.r6mod.client.util.input.KeyBindings;
import xfacthd.r6mod.common.capability.CapabilityGun;
import xfacthd.r6mod.common.data.itemsubtypes.EnumAttachment;
import xfacthd.r6mod.common.items.gun.ItemGun;
import xfacthd.r6mod.common.net.NetworkHandler;
import xfacthd.r6mod.common.net.packets.gun.*;

import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = R6Mod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GunEventHandler
{
    private static boolean reloadWasPressed = false;
    private static boolean wasViewBobbing = false;
    private static boolean hadGunInHand = false;
    private static boolean wasAiming = false;
    private static boolean wasSprintPressed = false;

    //Lowest to make sure every other handler trying to change the displayed screen is called before
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onOpenGui(final GuiOpenEvent event)
    {
        if (player() == null || world() == null) { return; }

        ItemStack stack = player().getHeldItemMainhand();
        if (!stack.isEmpty() && stack.getItem() instanceof ItemGun && event.getGui() != null)
        {
            NetworkHandler.sendToServer(new PacketCancelGunHandling());
            wasAiming = false;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onClientTick(final TickEvent.ClientTickEvent event)
    {
        if (player() == null || inGui() || inCam()) { return; } //Can't reload while in a camera or gui :D

        if (event.phase == TickEvent.Phase.START)
        {
            ItemStack stack = player().getHeldItemMainhand();

            //Handle reloading
            boolean reloadPressed = KeyBindings.KEY_RELOAD.isKeyDown();
            if (reloadPressed != reloadWasPressed)
            {
                reloadWasPressed = reloadPressed;
                if (reloadPressed && stack.getItem() instanceof IReloadable && !player().isSprinting())
                {
                    NetworkHandler.sendToServer(new PacketReload());
                }
            }

            boolean sprintPressed = mc().gameSettings.keyBindSprint.isKeyDown();

            //Handle disabling and resetting view bobbing when switching aim state and swapping items
            boolean hasGunInHand = stack.getItem() instanceof ItemGun;
            boolean isAiming = hasGunInHand && CapabilityGun.getFrom(stack).isAiming();
            if (hadGunInHand && !hasGunInHand && wasAiming)
            {
                //No cancel packet needed here as the capability handles slot switching on the server
                mc().gameSettings.viewBobbing = wasViewBobbing;
                wasAiming = false;
            }
            else if(hasGunInHand)
            {
                if (isAiming && !wasAiming)
                {
                    wasViewBobbing = mc().gameSettings.viewBobbing;
                    mc().gameSettings.viewBobbing = false;
                }
                else if (!isAiming && wasAiming)
                {
                    mc().gameSettings.viewBobbing = wasViewBobbing;
                }
                wasAiming = isAiming;

                if (isAiming && wasSprintPressed && sprintPressed)
                {
                    mc().gameSettings.keyBindSprint.unpressKey();
                }
            }
            hadGunInHand = hasGunInHand;
            wasSprintPressed = sprintPressed;
        }
    }

    @SubscribeEvent
    public static void onMouseClick(final InputEvent.RawMouseEvent event)
    {
        if (player() == null || inGui() || inCam()) { return; }

        ItemStack stack = player().getHeldItemMainhand();
        if (stack.getItem() instanceof ItemGun)
        {
            if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT)
            {
                event.setCanceled(true);

                NetworkHandler.sendToServer(new PacketFiring(event.getAction() == GLFW.GLFW_PRESS));
            }
            else if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT)
            {
                event.setCanceled(true);

                NetworkHandler.sendToServer(new PacketAiming(event.getAction() == GLFW.GLFW_PRESS, ClientConfig.INSTANCE.holdToAim));
            }
        }
    }

    @SubscribeEvent
    public static void onFovModifier(final EntityViewRenderEvent.FOVModifier event)
    {
        ItemStack stack = player().getHeldItemMainhand();

        if (stack.getItem() instanceof ItemGun)
        {
            ICapabilityGun cap = CapabilityGun.getFrom(stack);
            EnumAttachment sight = findSight(cap);

            double fovMod = sight != null ? sight.getFovMultiplier() : .9D;
            fovMod = MathHelper.lerp(cap.getAimState(world().getGameTime(), (float)event.getRenderPartialTicks()), 1D, fovMod);
            event.setFOV(event.getFOV() * fovMod);
        }
    }

    /*@SubscribeEvent //TODO: activate and set proper values when https://github.com/MinecraftForge/MinecraftForge/pull/6371 is merged and released
    public static void onRenderPlayer(final RenderLivingEvent.RenderModel<AbstractClientPlayerEntity, BipedModel<AbstractClientPlayerEntity>> event)
    {
        Entity entity = event.getEntity();
        if (!(entity instanceof AbstractClientPlayerEntity)) { return; }

        AbstractClientPlayerEntity player = (AbstractClientPlayerEntity)entity;
        boolean rightMain = player.getPrimaryHand() == HandSide.RIGHT;
        BipedModel<AbstractClientPlayerEntity> model = event.getRenderer().getEntityModel();
        if (player.getHeldItem(Hand.MAIN_HAND).getItem() instanceof ItemGun)
        {
            ModelRenderer mainHand = rightMain ? model.bipedRightArm : model.bipedLeftArm;
            mainHand.rotateAngleX = -2F;
            mainHand.rotateAngleY = 1F;
        }
        if (player.getHeldItem(Hand.OFF_HAND).getItem() instanceof ItemGun)
        {
            ModelRenderer offHand = rightMain ? model.bipedLeftArm : model.bipedRightArm;
            offHand.rotateAngleX = -2F;
            offHand.rotateAngleY = 1F;
        }
    }*/

    public static void handleModelRotations(PlayerEntity player, PlayerModel<PlayerEntity> model) //Called from Mixin
    {
        ItemStack mainStack = player.getHeldItemMainhand();
        ItemStack offStack = player.getHeldItemOffhand();

        if (mainStack.getItem() instanceof ItemGun)
        {
            model.bipedRightArm.rotateAngleX = 180;
        }

        if (offStack.getItem() instanceof ItemGun)
        {
            model.bipedLeftArm.rotateAngleX = 180;
        }
    }



    private static EnumAttachment findSight(ICapabilityGun cap)
    {
        List<EnumAttachment> attachments = cap.getAttachments();

        if (attachments.contains(EnumAttachment.FLIP_SIGHT) && cap.isAttachmentActive(EnumAttachment.FLIP_SIGHT))
        {
            return EnumAttachment.FLIP_SIGHT;
        }

        for (EnumAttachment attachment : attachments)
        {
            if (attachment.getType() == EnumAttachment.Type.SIGHT)
            {
                return attachment;
            }
        }
        return null;
    }



    private static Minecraft mc() { return Minecraft.getInstance(); }

    private static PlayerEntity player() { return mc().player; }

    private static World world() { return mc().world; }

    private static boolean inGui() { return mc().isGamePaused() || mc().currentScreen != null; }

    private static boolean inCam() { return R6Mod.getSidedHelper().isUsingCamera(mc().player); }
}