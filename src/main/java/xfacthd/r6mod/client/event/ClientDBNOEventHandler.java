package xfacthd.r6mod.client.event;

import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.api.capability.ICapabilityDBNO;
import xfacthd.r6mod.client.util.input.KeyBindings;
import xfacthd.r6mod.common.capability.CapabilityDBNO;
import xfacthd.r6mod.common.net.NetworkHandler;
import xfacthd.r6mod.common.net.packets.dbno.PacketHoldWound;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = R6Mod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientDBNOEventHandler
{
    private static boolean helpingPlayer = false;
    private static boolean wasInGui = false;
    private static boolean holdingWound = false;

    @SubscribeEvent
    public static void onClientTick(final TickEvent.ClientTickEvent event)
    {
        if (event.phase != TickEvent.Phase.START || player() == null) { return; }

        boolean isInGui = inGui();
        if (!inCam() && !isInGui && !wasInGui)
        {
            if (CapabilityDBNO.getFrom(player()).isDBNO())
            {
                unPressKeybinds(mc().gameSettings);

                boolean pressed = KeyBindings.KEY_HOLD_WOUND.isPressed();
                if (pressed != holdingWound)
                {
                    NetworkHandler.sendToServer(new PacketHoldWound(player(), pressed));
                    holdingWound = pressed;
                }
            }
            else
            {
                KeyBindings.KEY_HOLD_WOUND.setPressed(false);
                holdingWound = false;
            }
        }
        wasInGui = isInGui;
    }

    @SubscribeEvent
    public static void onOpenGui(final GuiOpenEvent event)
    {
        PlayerEntity player = player();
        if (player != null)
        {
            //Stop DeathScreen from opening when in DBNO state
            ICapabilityDBNO dbno = CapabilityDBNO.getFrom(player);
            if (dbno.isDBNO() && !dbno.isDead() && event.getGui() instanceof DeathScreen)
            {
                if (mc().currentScreen != null)
                {
                    event.setGui(null);
                }
                else
                {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onFogDensity(final EntityViewRenderEvent.FogDensity event)
    {
        Entity entity = mc().renderViewEntity;
        if (!(entity instanceof PlayerEntity)) { return; }

        PlayerEntity player = (PlayerEntity)entity;
        if (event.getType() == FogRenderer.FogType.FOG_TERRAIN)
        {
            ICapabilityDBNO dbno = CapabilityDBNO.getFrom(player);
            if (dbno.isDBNO() && !dbno.isDead())
            {
                float fogFactor = dbno.getTimeLeftFactor();
                event.setDensity(1F - fogFactor);
            }
        }
    }



    public static void setHelpingPlayer(boolean helping) { helpingPlayer = helping; }

    public static boolean isHelpingPlayer() { return helpingPlayer; }



    private static void unPressKeybinds(GameSettings settings)
    {
        //Prevent player from moving in dbno
        settings.keyBindSneak.unpressKey();
        settings.keyBindSprint.unpressKey();

        //Prevent player from modifying anything while dbno
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

    private static PlayerEntity player() { return mc().player; }

    private static boolean inGui() { return mc().currentScreen != null || mc().isGamePaused(); }

    private static boolean inCam() { return R6Mod.getSidedHelper().isUsingCamera(mc().player); }
}