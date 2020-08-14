package xfacthd.r6mod.common;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.particles.ParticleType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xfacthd.r6mod.common.blocks.BlockBase;
import xfacthd.r6mod.common.blocks.building.*;
import xfacthd.r6mod.common.blocks.gadgets.*;
import xfacthd.r6mod.common.blocks.misc.*;
import xfacthd.r6mod.common.container.*;
import xfacthd.r6mod.common.data.blockdata.WallMaterial;
import xfacthd.r6mod.common.data.particledata.*;
import xfacthd.r6mod.common.data.types.*;
import xfacthd.r6mod.common.data.ItemGroups;
import xfacthd.r6mod.common.data.itemsubtypes.*;
import xfacthd.r6mod.common.entities.grenade.EntityCandelaGrenade;
import xfacthd.r6mod.common.entities.camera.*;
import xfacthd.r6mod.common.entities.grenade.*;
import xfacthd.r6mod.common.items.building.ItemReinforcement;
import xfacthd.r6mod.common.items.gadgets.*;
import xfacthd.r6mod.common.items.gun.*;
import xfacthd.r6mod.common.items.material.*;
import xfacthd.r6mod.common.items.misc.*;
import xfacthd.r6mod.common.tileentities.building.*;
import xfacthd.r6mod.common.tileentities.gadgets.*;
import xfacthd.r6mod.common.tileentities.misc.*;
import xfacthd.r6mod.common.util.R6DataSerializers;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class R6Content
{
    //INFO: Misc blocks
    public static BlockBase blockGunCraftingTable;                      //STATUS: Not implemented
    public static BlockBase blockGunCustomizer;                         //STATUS: Not implemented
    public static BlockBase blockAmmoBox;                               //STATUS: Complete
    public static BlockBase blockWeaponShelf;                           //STATUS: Not implemented
    public static BlockBase blockMagFiller;                             //STATUS: Missing texture
    public static BlockBase blockBulletPress;                           //STATUS: Not implemented
    public static BlockBase blockTeamSpawn;                             //STATUS: Complete
    public static BlockBase blockClassEquipper;                         //STATUS: Not implemented
    public static BlockBase blockFakeFire;                              //STATUS: Complete

    //INFO: Building blocks
    public static Map<WallMaterial, BlockWall> blockWalls;              //STATUS: Complete
    public static Map<WallMaterial, BlockWall> blockWallsBarred;        //STATUS: Complete
    public static BlockBase blockBarricade;                             //STATUS: Complete
    public static BlockBase blockReinforcement;                         //STATUS: Complete
    public static Map<WallMaterial, BlockFloorPanel> blockFloorPanels;  //STATUS: Complete
    public static BlockBase blockDropHatch;                             //STATUS: Complete
    public static BlockBase blockFloorReinforcement;                    //STATUS: Complete
    public static BlockBase blockSteelLadder;                           //STATUS: Complete
    public static BlockBase blockCamera;                                //STATUS: Complete

    //INFO: Generic gadget blocks
    public static BlockBase blockBreachCharge;                          //STATUS: Complete
    public static BlockBase blockClaymore;                              //STATUS: Complete
    public static BlockBase blockBarbedWire;                            //STATUS: Complete
    public static BlockBase blockDeployableShield;                      //STATUS: Complete
    public static BlockBase blockBulletProofCamera;                     //STATUS: Complete

    //INFO: Operator specific gadget blocks
    public static BlockBase blockThermiteCharge;                        //STATUS: Complete
    public static BlockBase blockClusterCharge;                         //STATUS: Not implemented
    public static BlockBase blockJammer;                                //STATUS: Not implemented
    public static BlockBase blockToughBarricade;                        //STATUS: Complete
    public static BlockBase blockArmorBag;                              //STATUS: Not implemented
    public static BlockBase blockKapkanTrap;                            //STATUS: Not implemented
    public static BlockBase blockADS;                                   //STATUS: Not implemented
    public static BlockBase blockShockWire;                             //STATUS: Not implemented
    public static BlockBase blockWelcomeMat;                            //STATUS: Complete
    public static BlockBase blockBlackMirror;                           //STATUS: Complete
    public static BlockBase blockEvilEye;                               //STATUS: Complete
    public static BlockBase blockVolcanShield;                          //STATUS: Complete
    public static BlockBase blockBanshee;                               //STATUS: Missing textures

    //INFO: Basic items
    public static HashMap<EnumMaterial, Item> itemMaterials;            //STATUS: Missing textures and additional materials
    public static HashMap<EnumBullet, Item> itemBullets;                //STATUS: Missing textures

    //INFO: Gun stuff items
    public static HashMap<EnumGun, Item> itemGuns;                      //STATUS: Missing models and textures
    public static HashMap<EnumAttachment, Item> itemAttachments;        //STATUS: Missing models and textures
    public static HashMap<EnumMagazine, Item> itemMagazines;            //STATUS: Missing textures
    public static Item itemSpeedLoader;                                 //STATUS: Not implemented
    public static Item itemAmmoPouch;                                   //STATUS: Not implemented

    //INFO: Basic gameplay items
    public static Item itemDrone;                                       //STATUS: Not implemented
    public static Item itemPhone;                                       //STATUS: Complete
    public static Item itemCrowbar;                                     //STATUS: Complete
    public static Item itemReinforcement;                               //STATUS: Complete

    //INFO: Generic gadget items
    public static Item itemActivator;                                   //STATUS: Missing proper model and texture
    public static Item itemRiotShield;                                  //STATUS: Not implemented
    public static Item itemImpactGrenade;                               //STATUS: Complete
    public static Item itemFragGrenade;                                 //STATUS: Not implemented
    public static Item itemSmokeGrenade;                                //STATUS: Not implemented
    public static Item itemStunGrenade;                                 //STATUS: Not implemented
    public static Item itemNitroCell;                                   //STATUS: Not implemented
    public static Item itemNitroPhone;                                  //STATUS: Not implemented
    public static Item itemProximityAlarm;                              //STATUS: Not implemented

    //INFO: Operator specific gadget items (attack)
    public static Item itemSledgeHammer;                                //STATUS: Not implemented
    public static Item itemEMPGrenade;                                  //STATUS: Missing texture
    public static Item itemGrenadeLauncher;                             //STATUS: Not implemented
    public static Item itemShockDrone;                                  //STATUS: Not implemented
    public static Item itemExtendableShield;                            //STATUS: Not implemented
    public static Item itemFlashShield;                                 //STATUS: Not implemented
    public static Item itemElectronicsDetector;                         //STATUS: Not implemented
    public static Item itemCrossbow;                                    //STATUS: Not implemented
    public static Item itemXKairosLauncher;                             //STATUS: Not implemented
    public static Item itemCandelaGrenade;                              //STATUS: WIP
    public static Item itemYingGlasses;                                 //STATUS: Not implemented
    public static Item itemLifelineLauncher;                            //STATUS: Not implemented
    public static Item itemLogicBomb;                                   //STATUS: Not implemented
    public static Item itemEEOneD;                                      //STATUS: Not implemented
    public static Item itemAdrenalSurge;                                //STATUS: Not implemented
    public static Item itemBreachingTorch;                              //STATUS: Not implemented
    public static Item itemTraxStingers;                                //STATUS: Not implemented
    public static Item itemHELPresenceReduction;                        //STATUS: Not implemented
    public static Item itemGarraHook;                                   //STATUS: Not implemented
    public static Item itemGeminiReplicator;                            //STATUS: Not implemented
    public static Item itemSELMAAquaBreacher;                           //STATUS: Not implemented

    //INFO: Operator specific gadget items (defense)
    public static Item itemGasCanister;                                 //STATUS: Not implemented
    public static Item itemCardiacSensor;                               //STATUS: Not implemented
    public static Item itemStimPistol;                                  //STATUS: Test implementation
    public static Item itemIncendiaryLauncher;                          //STATUS: Not implemented //INFO: new tachanka gadget, replace with actual name when released
    public static Item itemBlackEyeCamera;                              //STATUS: Not implemented
    public static Item itemInterogationKnive;                           //STATUS: Not implemented
    public static Item itemYokaiDrone;                                  //STATUS: Complete
    public static Item itemGuMine;                                      //STATUS: Not implemented
    public static Item itemGrzmotMine;                                  //STATUS: Not implemented
    public static Item itemERC7;                                        //STATUS: Not implemented
    public static Item itemPrisma;                                      //STATUS: Not implemented
    public static Item itemCCEShield;                                   //STATUS: Not implemented
    public static Item itemElectroclaw;                                 //STATUS: Not implemented
    public static Item itemPestLauncher;                                //STATUS: Not implemented
    public static Item itemGlanceSmartGlasses;                          //STATUS: Not implemented
    public static Item itemMagNet;                                      //STATUS: Not implemented

    //INFO: Gadget consumables
    public static HashMap<EnumGadgetAmmo, Item> itemGadgetAmmos;    //STATUS: Missing models and textures

    @SubscribeEvent
    public static void onRegisterBlocks(final RegistryEvent.Register<Block> event)
    {
        event.getRegistry().register(blockAmmoBox = new BlockAmmoBox());
        event.getRegistry().register(blockMagFiller = new BlockMagFiller());
        event.getRegistry().register(blockTeamSpawn = new BlockTeamSpawn());
        event.getRegistry().register(blockFakeFire = new BlockFakeFire());

        event.getRegistry().registerAll(BlockWall.registerBlocks());
        event.getRegistry().register(blockBarricade = new BlockBarricade());
        event.getRegistry().register(blockReinforcement = new BlockReinforcement());
        event.getRegistry().registerAll(BlockFloorPanel.registerBlocks());
        event.getRegistry().register(blockDropHatch = new BlockDropHatch());
        event.getRegistry().register(blockFloorReinforcement = new BlockFloorReinforcement());
        event.getRegistry().register(blockSteelLadder = new BlockSteelLadder());
        event.getRegistry().register(blockCamera = new BlockCamera());

        event.getRegistry().register(blockBreachCharge = new BlockBreachCharge());
        event.getRegistry().register(blockClaymore = new BlockClaymore());
        event.getRegistry().register(blockBarbedWire = new BlockBarbedWire());
        event.getRegistry().register(blockDeployableShield = new BlockDeployableShield());
        event.getRegistry().register(blockBulletProofCamera = new BlockBulletProofCamera());

        event.getRegistry().register(blockThermiteCharge = new BlockThermiteCharge());
        event.getRegistry().register(blockToughBarricade = new BlockToughBarricade());
        event.getRegistry().register(blockWelcomeMat = new BlockWelcomeMat());
        event.getRegistry().register(blockBlackMirror = new BlockBlackMirror());
        event.getRegistry().register(blockEvilEye = new BlockEvilEye());
        event.getRegistry().register(blockVolcanShield = new BlockVolcanShield());
        event.getRegistry().register(blockBanshee = new BlockBanshee());
    }

    @SubscribeEvent
    public static void onRegisterItems(final RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(itemPhone = new ItemPhone());
        event.getRegistry().register(itemCrowbar = new ItemCrowbar());
        event.getRegistry().register(itemReinforcement = new ItemReinforcement());
        event.getRegistry().register(itemActivator = new ItemActivator());
        event.getRegistry().register(itemImpactGrenade = new ItemImpactGrenade());

        event.getRegistry().register(itemEMPGrenade = new ItemEMPGrenade());
        event.getRegistry().register(itemCandelaGrenade = new ItemCandela());
        event.getRegistry().register(itemYingGlasses = new ItemYingGlasses());

        event.getRegistry().register(itemStimPistol = new ItemStimPistol());
        event.getRegistry().register(itemYokaiDrone = new ItemYokaiDrone());

        onRegisterItemsWithSubtypes(event);
        onRegisterItemBlocks(event);

        ItemGroups.finalizeItemGroups();
    }

    private static void onRegisterItemsWithSubtypes(final RegistryEvent.Register<Item> event)
    {
        itemMaterials = new HashMap<>();
        for (EnumMaterial material : EnumMaterial.values())
        {
            Item item = new ItemMaterial(material);
            event.getRegistry().register(item);
            itemMaterials.put(material, item);
        }

        itemBullets = new HashMap<>();
        for (EnumBullet bullet : EnumBullet.values())
        {
            Item item = new ItemBullet(bullet);
            event.getRegistry().register(item);
            itemBullets.put(bullet, item);
        }

        itemMagazines = new HashMap<>();
        for (EnumMagazine mag : EnumMagazine.values())
        {
            if (mag == EnumMagazine.NONE) { continue; } //EnumMagazine.NONE is a placeholder

            Item item = new ItemMagazine(mag);
            event.getRegistry().register(item);
            itemMagazines.put(mag, item);
        }

        itemGadgetAmmos = new HashMap<>();
        for (EnumGadgetAmmo ammo : EnumGadgetAmmo.values())
        {
            Item item = new ItemGadgetAmmo(ammo);
            event.getRegistry().register(item);
            itemGadgetAmmos.put(ammo, item);
        }

        //Must happen here because ItemGroups are filled next and rely on the data initialized here
        EnumGun.initializeValueDetails();
        itemGuns = new HashMap<>();
        for (EnumGun gun : EnumGun.values())
        {
            Item item = new ItemGun(gun);
            event.getRegistry().register(item);
            itemGuns.put(gun, item);
        }

        itemAttachments = new HashMap<>();
        for (EnumAttachment attachment : EnumAttachment.values())
        {
            Item item = new ItemAttachment(attachment);
            event.getRegistry().register(item);
            itemAttachments.put(attachment, item);
        }
    }

    private static void onRegisterItemBlocks(final RegistryEvent.Register<Item> event)
    {
        blockAmmoBox.registerItemBlock(event.getRegistry());
        blockMagFiller.registerItemBlock(event.getRegistry());

        blockTeamSpawn.registerItemBlock(event.getRegistry());

        blockWalls.forEach((material, block) -> block.registerItemBlock(event.getRegistry()));
        blockWallsBarred.forEach((material, block) -> block.registerItemBlock(event.getRegistry()));
        blockBarricade.registerItemBlock(event.getRegistry());
        blockFloorPanels.forEach((material, block) -> block.registerItemBlock(event.getRegistry()));
        blockDropHatch.registerItemBlock(event.getRegistry());
        blockSteelLadder.registerItemBlock(event.getRegistry());
        blockCamera.registerItemBlock(event.getRegistry());

        blockBreachCharge.registerItemBlock(event.getRegistry());
        blockClaymore.registerItemBlock(event.getRegistry());
        blockBarbedWire.registerItemBlock(event.getRegistry());
        blockDeployableShield.registerItemBlock(event.getRegistry());
        blockBulletProofCamera.registerItemBlock(event.getRegistry());

        blockThermiteCharge.registerItemBlock(event.getRegistry());
        blockToughBarricade.registerItemBlock(event.getRegistry());
        blockWelcomeMat.registerItemBlock(event.getRegistry());
        blockBlackMirror.registerItemBlock(event.getRegistry());
        blockEvilEye.registerItemBlock(event.getRegistry());
        blockVolcanShield.registerItemBlock(event.getRegistry());
        blockBanshee.registerItemBlock(event.getRegistry());
    }

    @SubscribeEvent
    public static void onRegisterTileEntityTypes(final RegistryEvent.Register<TileEntityType<?>> event)
    {
        TileEntityTypes.setRegistry(event.getRegistry());

        TileEntityTypes.tileTypeAmmoBox = TileEntityTypes.create(TileEntityAmmoBox::new, "tile_ammo_box", blockAmmoBox);
        TileEntityTypes.tileTypeMagFiller = TileEntityTypes.create(TileEntityMagFiller::new, "tile_mag_filler", blockMagFiller);
        //TileEntityTypes.tileBulletPressType = TileEntityTypes.create(TileEntityBulletPress::new, "tile_bullet_press", blockBulletPress);
        TileEntityTypes.tileTypeTeamSpawn = TileEntityTypes.create(TileEntityTeamSpawn::new, "tile_team_spawn", blockTeamSpawn);
        TileEntityTypes.tileTypeFakeFire = TileEntityTypes.create(TileEntityFakeFire::new, "tile_fake_fire", blockFakeFire);

        TileEntityTypes.tileTypeCamera = TileEntityTypes.create(TileEntityCamera::new, "tile_camera", blockCamera);

        TileEntityTypes.tileTypeBreachCharge = TileEntityTypes.create(TileEntityBreachCharge::new, "tile_breach_charge", blockBreachCharge);
        TileEntityTypes.tileTypeClaymore = TileEntityTypes.create(TileEntityClaymore::new, "tile_claymore", blockClaymore);
        TileEntityTypes.tileTypeBarbedWire = TileEntityTypes.create(TileEntityBarbedWire::new, "tile_barbed_wire", blockBarbedWire);
        TileEntityTypes.tileTypeDeployableShield = TileEntityTypes.create(TileEntityDeployableShield::new, "tile_shield", blockDeployableShield);
        TileEntityTypes.tileTypeBulletproofCamera = TileEntityTypes.create(TileEntityBulletproofCamera::new, "tile_bulletproof_camera", blockBulletProofCamera);

        TileEntityTypes.tileTypeThermiteCharge = TileEntityTypes.create(TileEntityThermiteCharge::new, "tile_thermite_charge", blockThermiteCharge);
        TileEntityTypes.tileTypeToughBarricade = TileEntityTypes.create(TileEntityToughBarricade::new, "tile_tough_barricade", blockToughBarricade);
        TileEntityTypes.tileTypeWelcomeMat = TileEntityTypes.create(TileEntityWelcomeMat::new, "tile_welcome_mat", blockWelcomeMat);
        TileEntityTypes.tileTypeBlackMirror = TileEntityTypes.create(TileEntityBlackMirror::new, "tile_black_mirror", blockBlackMirror);
        TileEntityTypes.tileTypeEvilEye = TileEntityTypes.create(TileEntityEvilEye::new, "tile_evil_eye", blockEvilEye);
        TileEntityTypes.tileTypeVolcanShield = TileEntityTypes.create(TileEntityVolcanShield::new, "tile_volcan_shield", blockVolcanShield);
        TileEntityTypes.tileTypeBanshee = TileEntityTypes.create(TileEntityBanshee::new, "tile_banshee", blockBanshee);
    }

    @SubscribeEvent
    public static void onRegisterEntityTypes(final RegistryEvent.Register<EntityType<?>> event)
    {
        DataSerializers.registerSerializer(R6DataSerializers.LONG);

        EntityTypes.setRegistry(event.getRegistry());

        EntityTypes.entityTypeCamera = EntityTypes.create((type, world) -> new EntityCamera(world), "entity_camera", 0.1F, 0.1F, 1, true);
        //EntityTypes.entityTypeFragGrenade = EntityTypes.createGrenade((type, world) -> new EntityFragGrenade(world), "entity_frag_grenade");
        EntityTypes.entityTypeImpactGrenade = EntityTypes.createGrenade((type, world) -> new EntityImpactGrenade(world), "entity_impact_grenade");
        EntityTypes.entityTypeBulletproofCamera = EntityTypes.create((type, world) -> new EntityBulletproofCamera(world), "entity_bulletproof_camera", .1F, .1F, 10, true);

        EntityTypes.entityTypeEMPGrenade = EntityTypes.createGrenade((type, world) -> new EntityEMPGrenade(world), "entity_emp_grenade");
        EntityTypes.entityTypeCandelaGrenade = EntityTypes.create((type, world) -> new EntityCandelaGrenade(world), "entity_candela", .25F, .25F, 1, true);

        EntityTypes.entityTypeYokaiDrone = EntityTypes.create((type, world) -> new EntityYokaiDrone(world), "entity_yokai_drone", .7F, .08F, 1);
        EntityTypes.entityTypeEvilEye = EntityTypes.create((type, world) -> new EntityEvilEyeCamera(world), "entity_evil_eye", .1F, .1F, 1, true);
    }

    @SubscribeEvent
    public static void onRegisterContainerTypes(final RegistryEvent.Register<ContainerType<?>> event)
    {
        ContainerTypes.setRegistry(event.getRegistry());

        ContainerTypes.containerTypeMagFiller = ContainerTypes.create("container_mag_filler", (windowId, inv, data) ->
                new ContainerMagFiller(windowId, inv.player.world, data.readBlockPos(), inv.player, inv)
        );

        ContainerTypes.containerTypeCamera = ContainerTypes.create("container_camera", (windowId, inv, data) ->
                new ContainerCamera(windowId, inv.player.world, data.readBlockPos(), inv.player)
        );

        ContainerTypes.containerTypeTeamSpawn = ContainerTypes.create("container_team_spawn", ((windowId, inv, data) ->
                new ContainerTeamSpawn(windowId, inv.player.world, data.readBlockPos()))
        );
    }

    @SubscribeEvent
    public static void onRegisterParticleTypes(final RegistryEvent.Register<ParticleType<?>> event)
    {
        ParticleTypes.setRegistry(event.getRegistry());

        ParticleTypes.particleTypeYokaiBlast = ParticleTypes.create("yokai_blast", false, ParticleDataYokaiBlast.DESERIALIZER);
        ParticleTypes.particleTypeEvilEyeLaser = ParticleTypes.create("evil_eye_laser", false, ParticleDataEvilEyeLaser.DESERIALIZER);
    }
}