package xfacthd.r6mod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import xfacthd.r6mod.api.ISidedHelper;
import xfacthd.r6mod.client.util.ClientConfig;
import xfacthd.r6mod.client.util.ClientHelper;
import xfacthd.r6mod.common.capability.CapabilityHandler;
import xfacthd.r6mod.common.entities.camera.EntityYokaiDrone;
import xfacthd.r6mod.common.net.NetworkHandler;
import xfacthd.r6mod.common.util.*;
import xfacthd.r6mod.common.util.data.R6Command;

@Mod(R6Mod.MODID)
public class R6Mod
{
    public static final String MODID = "r6mod";

    private static final ISidedHelper SIDED_HELPER = DistExecutor.safeRunForDist(() -> ClientHelper::new, () -> ServerHelper::new);

    public R6Mod()
    {
        LogHelper.setLogger(LogManager.getLogger());

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModConfig);

        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }

    public void setup(final FMLCommonSetupEvent event)
    {
        NetworkHandler.initPackets();
        CapabilityHandler.registerCapabilities();
        EntityYokaiDrone.transformEyeHeightField();
    }

    public void onModConfig(final ModConfig.ModConfigEvent event)
    {
        final ModConfig config = event.getConfig();
        if (config.getSpec() == Config.SPEC) { Config.INSTANCE.rebake(); }
        else if (config.getSpec() == ClientConfig.SPEC) { ClientConfig.INSTANCE.rebake(); }
    }

    @SubscribeEvent
    public void onRegisterCommands(final RegisterCommandsEvent event) { R6Command.register(event.getDispatcher()); }

    public static ISidedHelper getSidedHelper() { return SIDED_HELPER; }
}