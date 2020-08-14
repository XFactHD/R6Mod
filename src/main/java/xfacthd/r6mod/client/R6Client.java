package xfacthd.r6mod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.client.event.CameraEventHandler;
import xfacthd.r6mod.client.gui.screen.*;
import xfacthd.r6mod.client.model.baked.MultipartModelWrapper;
import xfacthd.r6mod.client.particles.*;
import xfacthd.r6mod.client.render.entity.*;
import xfacthd.r6mod.client.render.ister.*;
import xfacthd.r6mod.client.render.ter.*;
import xfacthd.r6mod.client.util.*;
import xfacthd.r6mod.client.util.data.ClientCameraManager;
import xfacthd.r6mod.client.util.input.KeyBindings;
import xfacthd.r6mod.common.R6Content;
import xfacthd.r6mod.common.data.effects.*;
import xfacthd.r6mod.common.data.types.*;
import xfacthd.r6mod.common.entities.camera.*;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = R6Mod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class R6Client
{
    private static final ClientCameraManager cameraManager = new ClientCameraManager();

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event)
    {
        MinecraftForge.EVENT_BUS.register(R6Client.class);

        //noinspection deprecation
        DeferredWorkQueue.runLater(CameraEventHandler::transformFunctions);

        ClientRegistry.bindTileEntityRenderer(TileEntityTypes.tileTypeMagFiller, RenderMagFiller::new);
        //ClientRegistry.bindTileEntityRenderer(TileEntityTypes.tileBulletPressType, RenderBulletPress::new);
        ClientRegistry.bindTileEntityRenderer(TileEntityTypes.tileTypeCamera, RenderCamera::new);
        ClientRegistry.bindTileEntityRenderer(TileEntityTypes.tileTypeClaymore, RenderClaymore::new);
        ClientRegistry.bindTileEntityRenderer(TileEntityTypes.tileTypeBulletproofCamera, RenderBulletproofCamera::new);
        ClientRegistry.bindTileEntityRenderer(TileEntityTypes.tileTypeThermiteCharge, RenderThermiteCharge::new);
        ClientRegistry.bindTileEntityRenderer(TileEntityTypes.tileTypeEvilEye, RenderEvilEye::new);

        //noinspection deprecation
        DeferredWorkQueue.runLater(() ->
        {
            EntityRendererManager manager = Minecraft.getInstance().getRenderManager();

            //Building entities
            manager.register(EntityTypes.entityTypeCamera, new RenderEntityEmpty(manager));

            //Generic gadget entities
            //manager.register(EntityTypes.entityTypeFragGrenade, new SpriteRenderer<>(manager, mc().getItemRenderer()));
            //manager.register(EntityTypes.entityTypeStunGrenade, new SpriteRenderer<>(manager, mc().getItemRenderer()));
            //manager.register(EntityTypes.entityTypeSmokeGrenade, new SpriteRenderer<>(manager, mc().getItemRenderer()));
            manager.register(EntityTypes.entityTypeImpactGrenade, new SpriteRenderer<>(manager, mc().getItemRenderer()));
            //manager.register(EntityTypes.entityTypeDrone, new RenderEntityDrone(manager));
            manager.register(EntityTypes.entityTypeBulletproofCamera, new RenderEntityEmpty(manager));

            //Operator specific gadget entities (attack)
            manager.register(EntityTypes.entityTypeEMPGrenade, new SpriteRenderer<>(manager, mc().getItemRenderer()));
            manager.register(EntityTypes.entityTypeCandelaGrenade, new RenderEntityCandela(manager));

            //Operator specific gadget entities (defense)
            manager.register(EntityTypes.entityTypeYokaiDrone, new RenderEntityYokaiDrone(manager));
            manager.register(EntityTypes.entityTypeEvilEye, new RenderEntityEmpty(manager));
        });

        //noinspection deprecation
        DeferredWorkQueue.runLater(() ->
        {
            RenderTypeLookup.setRenderLayer(R6Content.blockFakeFire, RenderType.getCutout());
            RenderTypeLookup.setRenderLayer(R6Content.blockBarricade, RenderType.getCutout());
            RenderTypeLookup.setRenderLayer(R6Content.blockBarbedWire, RenderType.getCutout());
            RenderTypeLookup.setRenderLayer(R6Content.blockBreachCharge, RenderType.getCutout());
            RenderTypeLookup.setRenderLayer(R6Content.blockBulletProofCamera, type -> type == RenderType.getCutout() || type == RenderType.getTranslucent());
            RenderTypeLookup.setRenderLayer(R6Content.blockThermiteCharge, RenderType.getCutout());
            RenderTypeLookup.setRenderLayer(R6Content.blockToughBarricade, RenderType.getCutout());
            RenderTypeLookup.setRenderLayer(R6Content.blockBlackMirror, type -> type == RenderType.getCutout() || type == RenderType.getTranslucent());
        });

        ClientRegistry.registerEntityShader(EntityCamera.class, new ResourceLocation(R6Mod.MODID, "shaders/desaturate_full.json"));
        //ClientRegistry.registerEntityShader(EntityBlackEye.class, new ResourceLocation(R6Mod.MODID, "shaders/tint_blue.json"));
        ClientRegistry.registerEntityShader(EntityYokaiDrone.class, new ResourceLocation(R6Mod.MODID, "shaders/desaturate_yokai.json"));
        //ClientRegistry.registerEntityShader(EntityEvilEye.class, new ResourceLocation("minecraft:shaders/..."));
        //ClientRegistry.registerEntityShader(EntityBulletproofCamera.class, new ResourceLocation("minecraft:shaders/..."));

        //noinspection deprecation
        DeferredWorkQueue.runLater(() ->
        {
            ScreenManager.registerFactory(ContainerTypes.containerTypeMagFiller, ScreenMagFiller::new);
            ScreenManager.registerFactory(ContainerTypes.containerTypeCamera, ScreenCamera::new);
            ScreenManager.registerFactory(ContainerTypes.containerTypeTeamSpawn, ScreenTeamSpawn::new);
        });

        KeyBindings.registerKeyBinds();
    }

    @SubscribeEvent
    public static void onModelRegister(final ModelRegistryEvent event)
    {
        //TERs
        RenderEvilEye.registerModels();

        //ISTERs
        RenderCandela.registerModels();

        //EntityRenderers
        RenderEntityYokaiDrone.registerModels();
    }

    @SubscribeEvent
    public static void onModelBake(final ModelBakeEvent event)
    {
        //TERs
        RenderMagFiller.loadModels(event.getModelRegistry());
        RenderEvilEye.loadModels(event.getModelRegistry());

        //ISTERs
        RenderGun.loadModels(event.getModelRegistry());
        RenderCandela.loadModels(event.getModelRegistry());

        //EntityRenderers
        RenderEntityCandela.loadModels(event.getModelRegistry());
        RenderEntityYokaiDrone.loadModels(event.getModelRegistry());

        //Misc model manipulations
        for (ResourceLocation loc : event.getModelRegistry().keySet())
        {
            if (loc.getNamespace().equals(R6Mod.MODID) && loc.getPath().contains("block_black_mirror"))
            {
                IBakedModel model = event.getModelRegistry().get(loc);
                MultipartModelWrapper wrapper = new MultipartModelWrapper(model);
                event.getModelRegistry().put(loc, wrapper);
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("deprecation")
    public static void onTextureMapReloadPre(final TextureStitchEvent.Pre event)
    {
        if (event.getMap().getTextureLocation() == AtlasTexture.LOCATION_BLOCKS_TEXTURE)
        {
            RenderEvilEye.registerTextures(event);

            EffectOverheal.registerTextures(event);
            EffectYokaiBlast.registerTextures(event);
            EffectGuMine.registerTextures(event);
        }
        else if (event.getMap().getTextureLocation() == AtlasTexture.LOCATION_PARTICLES_TEXTURE)
        {
            ParticleYokaiBlast.registerTexture(event);
            ParticleEvilEyeLaser.registerTexture(event);
        }
    }

    @SubscribeEvent
    @SuppressWarnings("deprecation")
    public static void onTextureMapReloadPost(final TextureStitchEvent.Post event)
    {
        if (event.getMap().getTextureLocation() == AtlasTexture.LOCATION_BLOCKS_TEXTURE)
        {
            RenderEvilEye.loadTextures(event.getMap());

            EffectOverheal.loadTextures(event.getMap());
            EffectYokaiBlast.loadTextures(event.getMap());
            EffectGuMine.loadTextures(event.getMap());
        }
        else if (event.getMap().getTextureLocation() == AtlasTexture.LOCATION_PARTICLES_TEXTURE)
        {
            ParticleYokaiBlast.loadTexture(event.getMap());
            ParticleEvilEyeLaser.loadTexture(event.getMap());
        }

        if (ClientConfig.INSTANCE.debugDumpTextures) { TextureDumper.dumpAtlasTexture(event.getMap()); }
    }

    @SubscribeEvent
    public static void onRegisterParticleFactories(final ParticleFactoryRegisterEvent event)
    {
        mc().particles.registerFactory(ParticleTypes.particleTypeYokaiBlast, new ParticleYokaiBlast.Factory());
        mc().particles.registerFactory(ParticleTypes.particleTypeEvilEyeLaser, new ParticleEvilEyeLaser.Factory());
    }

    public static ClientCameraManager getCameraManager() { return cameraManager; }

    private static Minecraft mc() { return Minecraft.getInstance(); }
}