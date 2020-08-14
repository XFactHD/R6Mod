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

package XFactHD.rssmc.common;

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.common.blocks.building.*;
import XFactHD.rssmc.common.blocks.gadget.*;
import XFactHD.rssmc.common.blocks.misc.*;
import XFactHD.rssmc.common.blocks.BlockBase;
import XFactHD.rssmc.common.blocks.objective.*;
import XFactHD.rssmc.common.blocks.survival.*;
import XFactHD.rssmc.common.crafting.Crafting;
import XFactHD.rssmc.common.entity.*;
import XFactHD.rssmc.common.entity.camera.*;
import XFactHD.rssmc.common.entity.drone.EntityBlackEye;
import XFactHD.rssmc.common.entity.gadget.*;
import XFactHD.rssmc.common.items.*;
import XFactHD.rssmc.common.items.armor.*;
import XFactHD.rssmc.common.items.ammo.*;
import XFactHD.rssmc.common.items.gadget.*;
import XFactHD.rssmc.common.items.gun.*;
import XFactHD.rssmc.common.items.material.*;
import XFactHD.rssmc.common.items.misc.*;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class Content
{
    public static BlockBase blockGameManager;
    public static BlockBase blockGunCraftingTable;
    public static BlockBase blockGunCustomizer;
    public static BlockBase blockClassEquipper;
    public static BlockBase blockAmmoBox;
    public static BlockBase blockWeaponShelf;

    public static BlockBase blockPlaster;
    public static BlockBase blockBarricade;
    public static BlockBase blockReinforcement;
    public static BlockBase blockSteelLadder;
    public static BlockBase blockFloorPanel;
    public static BlockBase blockDropHatch;
    public static BlockBase blockWall;
    public static BlockBase blockMultiTexture;
    public static BlockBase blockStairRailing;
    public static BlockBase blockStairRailingDummy;
    public static BlockBase blockRailing;
    public static BlockBase blockRailingDummy;
    public static BlockBase blockBioContainer;
    public static BlockBase blockBomb;
    public static BlockBase blockDefuser;
    public static BlockBase blockHostageRescuePoint;
    public static BlockBase blockMagFiller;

    public static BlockBase blockJammer;
    public static BlockBase blockThermiteCharge;
    public static BlockBase blockToughBarricade;
    public static BlockBase blockArmorBag;
    public static BlockBase blockClusterCharge;
    public static BlockBase blockLMG;
    public static BlockBase blockKapkanTrap;
    public static BlockBase blockADS;
    public static BlockBase blockShockWire;
    public static BlockBase blockWelcomeMat;
    public static BlockBase blockBarbedWire;
    public static BlockBase blockDeployableShield;
    public static BlockBase blockBreachCharge;
    public static BlockBase blockClaymore;
    public static BlockBase blockCamera;
    public static BlockBase blockBlackMirror;
    public static BlockBase blockGuMine;

    public static ItemBase itemMaterial;

    public static ItemBase itemGun;
    public static ItemBase itemAttachment;
    public static ItemBase itemMagazine;
    public static ItemBase itemCrossbowMag;
    public static ItemBase itemXKairosMag;
    public static ItemBase itemMountedLMGMag;
    public static ItemBase itemAmmo;
    public static ItemBase itemRiotShield;
    public static ItemBase itemReinforcement;
    public static ItemBase itemNitroCell;
    public static ItemBase itemNitroPhone;
    public static ItemBase itemActivator;
    public static ItemBase itemSledgeHammer;
    public static ItemBase itemEMPGrenade;
    public static ItemBase itemGasCanister;
    public static ItemBase itemImpactGrenade;
    public static ItemBase itemFragGrenade;
    public static ItemBase itemSmokeGrenade;
    public static ItemBase itemStunGrenade;
    public static ItemBase itemGrenadeLauncher;
    public static ItemBase itemBreachGrenade;
    public static ItemBase itemCardiacSensor;
    public static ItemBase itemShockDrone;
    public static ItemBase itemStimPistol;
    public static ItemBase itemStimDart;
    public static ItemBase itemInterogationKnive;
    public static ItemBase itemElectronicsDetector;
    public static ItemBase itemBlackEyeCamera;
    public static ItemBase itemCrossbow;
    public static ItemBase itemXKairosLauncher;
    public static ItemBase itemYokaiDrone;
    public static ItemBase itemCandelaGrenade;
    public static ItemBase itemGrzmotMine;
    public static ItemBase itemKS79Launcher;
    public static ItemBase itemLogicBomb;

    public static ItemBase itemDrone;
    public static ItemBase itemPhone;
    public static ItemBase itemRookUpgrade;
    public static ItemBase itemCrowbar;
    public static ItemBase itemHostagePlacer;
    public static ItemBase itemSpeedLoader;

    public static ItemOperatorArmor itemOperatorArmor;
    public static ItemBomberArmor itemBomberArmor;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        BlockBase.setRegistry(event.getRegistry());

        //blockGameManager = new BlockGameManager();              //STATUS: Missing functionality, model, textures, currently deactivated
        blockGunCraftingTable = new BlockGunCraftingTable();      //STATUS: Completed, needs recipes and testing
        //blockGunCustomizer;                                     //STATUS: Missing implementation, functionality, model, textures
        blockClassEquipper = new BlockClassEquipper();            //STATUS: Completed
        blockAmmoBox = new BlockAmmoBox();                        //STATUS: Completed
        blockWeaponShelf = new BlockWeaponShelf();                //STATUS: Completed
        blockMultiTexture = new BlockMultiTexture();              //STATUS: Completed
        blockBarricade = new BlockBarricade();                    //STATUS: Completed
        blockReinforcement = new BlockReinforcement();            //STATUS: Completed
        blockSteelLadder = new BlockSteelLadder();                //STATUS: Completed
        blockFloorPanel = new BlockFloorPanel();                  //STATUS: Completed
        blockDropHatch = new BlockDropHatch();                    //STATUS: Completed
        blockWall = new BlockWall();                              //STATUS: Completed
        blockStairRailing = new BlockStairRailing();              //STATUS: Missing textures
        blockStairRailingDummy = new BlockStairRailingDummy();    //STATUS: Completed
        blockRailing = new BlockRailing();                        //STATUS: Missing textures
        blockRailingDummy = new BlockRailingDummy();              //STATUS: Completed, too high

        blockBioContainer = new BlockBioContainer();              //STATUS: Missing model, textures, integration into game manager
        blockBomb = new BlockBomb();                              //STATUS: Missing textures, integration into game manager
        blockDefuser = new BlockDefuser();                        //STATUS: Missing functionality, model, textures
        blockHostageRescuePoint = new BlockHostageRescuePoint();  //STATUS: Missing functionality, model, textures
        blockMagFiller = new BlockMagFiller();                    //STATUS: Missing textures, functionality is bugged

        blockJammer = new BlockJammer();                          //STATUS: Completed, missing textures
        blockThermiteCharge = new BlockThermiteCharge();          //STATUS: Completed
        blockToughBarricade = new BlockToughBarricade();          //STATUS: Completed
        blockArmorBag = new BlockArmorBag();                      //STATUS: Completed, missing textures
        blockClusterCharge = new BlockClusterCharge();            //STATUS: Missing textures, grenade spawning
        //blockLMG;                                               //STATUS: Missing implementation, functionality, model, textures
        blockKapkanTrap = new BlockKapkanTrap();                  //STATUS: Missing functionality, fancy render
        blockADS = new BlockActiveDefenseSystem();                //STATUS: Completed, TESR needs to be completed, needs better model
        blockShockWire = new BlockShockWire();                    //STATUS: Completed
        blockWelcomeMat = new BlockWelcomeMat();                  //STATUS: Completed
        blockBarbedWire = new BlockBarbedWire();                  //STATUS: Completed
        blockDeployableShield = new BlockDeployableShield();      //STATUS: Completed
        blockBreachCharge = new BlockBreachCharge();              //STATUS: Completed
        blockClaymore = new BlockClaymore();                      //STATUS: Missing functionality
        blockCamera = new BlockCamera();                          //STATUS: Missing functionality, textures
        blockBlackMirror = new BlockBlackMirror();                //STATUS: Missing textures
        blockGuMine = new BlockGuMine();                          //STATUS: Missing functionality, textures
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        BlockBase.registerItemBlocks(event.getRegistry());

        ItemBase.setRegistry(event.getRegistry());

        itemMaterial = new ItemMaterial();                              //STATUS: Missing model, textures

        //itemDrone;                                                    //STATUS: Missing implementation, functionality, models, textures, entity model, entity textures
        itemPhone = new ItemPhone();                                    //STATUS: Completed
        itemOperatorArmor = new ItemOperatorArmor(event.getRegistry()); //STATUS: Completed, missing armor models, armor textures
        itemBomberArmor = new ItemBomberArmor(event.getRegistry());     //STATUS: Completed, missing model, texture, armor model, armor texture
        itemRookUpgrade = new ItemRookUpgrade();                        //STATUS: Completed
        itemCrowbar = new ItemCrowbar();                                //STATUS: Completed
        itemHostagePlacer = new ItemHostagePlacer();                    //STATUS: Completed
        itemSpeedLoader = new ItemSpeedLoader();                        //STATUS: Missing model, texture, gui texture, slots don't update, shift click craches

        itemGun = new ItemGun();                                        //STATUS: Missing functionality, models, textures
        itemAttachment = new ItemAttachment();                          //STATUS: Missing models, textures
        itemMagazine = new ItemMagazine();                              //STATUS: Missing textures
        //itemCrossbowMag;                                              //STATUS: Missing implementation, model, texture
        //itemXKairosMag;                                               //STATUS: Missing implementation, model, texture
        //itemMountedLMGMag;                                            //STATUS: Missing implementation, model, texture
        itemAmmo = new ItemAmmo();                                      //STATUS: Completed, will replace all textures
        itemRiotShield = new ItemRiotShield();                          //STATUS: Missing functionality, models, textures
        itemReinforcement = new ItemReinforcement();                    //STATUS: Completed
        itemNitroCell = new ItemNitroCell();                            //STATUS: Completed
        itemNitroPhone = new ItemNitroPhone();                          //STATUS: Completed
        itemActivator = new ItemActivator();                            //STATUS: Completed, needs better model
        //itemSledgeHammer = new ItemSledgeHammer();                    //STATUS: Missing implementation, functionality, model, textures
        itemEMPGrenade = new ItemEMPGrenade();                          //STATUS: Missing functionality, model, textures
        itemGasCanister = new ItemGasCanister();                        //STATUS: Missing functionality, model, textures
        itemImpactGrenade = new ItemImpactGrenade();                    //STATUS: Completed
        itemFragGrenade = new ItemFragGrenade();                        //STATUS: Completed
        itemSmokeGrenade = new ItemSmokeGrenade();                      //STATUS: Missing functionality, model, textures
        itemStunGrenade = new ItemStunGrenade();                        //STATUS: Missing functionality, model, textures
        //itemGrenadeLauncher;                                          //STATUS: Missing implementation, functionality, model, textures, entity, entity model, entity textures
        //itemBreachGrenade;                                            //STATUS: Missing implementation, model, texture
        //itemCardiacSensor;                                            //STATUS: Missing implementation, functionality, model, textures
        //itemShockDrone;                                               //STATUS: Missing implementation, functionality, model, textures, entity, entity model, entity textures
        itemStimPistol = new ItemStimPistol();                          //STATUS: Completed
        itemStimDart = new ItemStimDart();                              //STATUS: Completed
        itemInterogationKnive = new ItemInterrogationKnive();           //STATUS: Missing functionality, texture needs to be reworked
        //itemElectronicsDetector;                                      //STATUS: Missing implementation, functionality, models, textures
        itemBlackEyeCamera = new ItemBlackEyeCamera();                  //STATUS: Missing implementation, functionality, models, textures, entity, entity model, entity textures
        //itemCrossbow;                                                 //STATUS: Missing implementation, functionality, models, textures, entity, entity model, entity textures
        //itemXKairosLauncher;                                          //STATUS: Missing implementation, functionality, models, textures, entity, entity model, entity textures
        //itemYokaiDrone;                                               //STATUS: Missing implementation, functionality, models, textures, entity, entity model, entity textures
        itemCandelaGrenade = new ItemCandelaGrenade();                  //STATUS: Missing functionality, entity, entity model
        //itemKS79Launcher;                                             //STATUS: Missing implementation, functionality, model, texture
        itemLogicBomb = new ItemLogicBomb();                            //STATUS: Completed, needs some polishing
    }

    public static void preInit()
    {
        EntityRegistry.registerModEntity(EntityNitroCell.class,      "entityNitroCell",       0, RainbowSixSiegeMC.INSTANCE, 128, 10, true);
        EntityRegistry.registerModEntity(EntityImpactGrenade.class,  "entityImpactGrenade",   1, RainbowSixSiegeMC.INSTANCE, 128, 10, true);
        EntityRegistry.registerModEntity(EntityEMPGrenade.class,     "entityEMPGrenade",      2, RainbowSixSiegeMC.INSTANCE, 128, 10, true);
        EntityRegistry.registerModEntity(EntityFragGrenade.class,    "entityFragGrenade",     3, RainbowSixSiegeMC.INSTANCE, 128, 10, true);
        EntityRegistry.registerModEntity(EntityStunGrenade.class,    "entityStunGrenade",     4, RainbowSixSiegeMC.INSTANCE, 128, 10, true);
        EntityRegistry.registerModEntity(EntitySmokeGrenade.class,   "entitySmokeGrenade",    5, RainbowSixSiegeMC.INSTANCE, 128, 10, true);
        EntityRegistry.registerModEntity(EntityGasCanister.class,    "entityGasCanister",     6, RainbowSixSiegeMC.INSTANCE, 128, 10, true);
        EntityRegistry.registerModEntity(EntityHostage.class,        "entityHostage",         7, RainbowSixSiegeMC.INSTANCE, 128, 40, true);
        //EntityRegistry.registerModEntity(EntityFuzeGrenade.class,    "entityFuzeGrenade",     8, RainbowSixSiegeMC.INSTANCE, 128, 10, true);
        EntityRegistry.registerModEntity(EntityCamera.class,         "entityCamera",          9, RainbowSixSiegeMC.INSTANCE, 128, 40, false);
        EntityRegistry.registerModEntity(EntityBlackEye.class,       "entityBlackEye",       10, RainbowSixSiegeMC.INSTANCE, 128, 10, true);
        //INFO: Secondary entity to not rotate everything while looking through the camera
        EntityRegistry.registerModEntity(EntityBlackEyeCam.class,    "entityBlackEyeCam",    11, RainbowSixSiegeMC.INSTANCE, 128, 40, true);
        //EntityRegistry.registerModEntity(EntityCrossbowArrow.class,  "entityCrossbowArrow",  12, RainbowSixSiegeMC.INSTANCE, 128, 10, true);
        //EntityRegistry.registerModEntity(EntityDrone.class,          "entityDrone",          13, RainbowSixSiegeMC.INSTANCE, 128, 10, true);
        EntityRegistry.registerModEntity(EntityDroneCam.class,       "entityDroneCam",       14, RainbowSixSiegeMC.INSTANCE, 128, 10, true);
        //EntityRegistry.registerModEntity(EntityTwitchDrone.class,    "entityTwitchDrone",    15, RainbowSixSiegeMC.INSTANCE, 128, 10, true);
        EntityRegistry.registerModEntity(EntityTwitchDroneCam.class, "entityTwitchDroneCam", 16, RainbowSixSiegeMC.INSTANCE, 128, 40, true);
        //EntityRegistry.registerModEntity(EntityAshGrenade.class,     "entityAshGrenade",     17, RainbowSixSiegeMC.INSTANCE, 128, 10, true);
        //EntityRegistry.registerModEntity(EntityXKairosPellet.class,  "entityXKairosPellet",  18, RainbowSixSiegeMC.INSTANCE, 128, 10, true);
        //EntityRegistry.registerModEntity(EntityYokaiDrone.class,     "entityYokaiDrone",     19, RainbowSixSiegeMC.INSTANCE, 128, 10, true);
        EntityRegistry.registerModEntity(EntityYokaiCam.class,       "entityYokaiCam",       20, RainbowSixSiegeMC.INSTANCE, 128, 40, true);
        //EntityRegistry.registerModEntity(EntityCandelaGrenade.class, "entityCandela",        21, RainbowSixSiegeMC.INSTANCE, 128, 10, true);
        //EntityRegistry.registerModEntity(EntityTerrorist.class,      "entityTerrorist",      22, RainbowSixSiegeMC.INSTANCE, 128, 10, true);
        //EntityRegistry.registerModEntity(EntityBomber.class,         "entityBomber",         23, RainbowSixSiegeMC.INSTANCE, 128, 10, true);
    }

    public static void init()
    {
        Crafting.initItemStacks();
        Crafting.initVanillaCrafting();
        Crafting.initGunCrafting();
        Crafting.initAmmoCrafting();
    }

    public static void postInit()
    {
        TileEntityGameManager.registerGameMode(TileEntityGameManager.GameHandlerBomb.NAME, TileEntityGameManager.GameHandlerBomb.class);
        TileEntityGameManager.registerGameMode(TileEntityGameManager.GameHandlerBioContainer.NAME, TileEntityGameManager.GameHandlerBioContainer.class);
        TileEntityGameManager.registerGameMode(TileEntityGameManager.GameHandlerHostage.NAME, TileEntityGameManager.GameHandlerHostage.class);
    }
}