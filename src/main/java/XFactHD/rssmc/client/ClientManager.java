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

package XFactHD.rssmc.client;

import XFactHD.rssmc.client.models.baked.*;
import XFactHD.rssmc.client.renderer.entity.*;
import XFactHD.rssmc.client.renderer.tesr.*;
import XFactHD.rssmc.client.util.meshdefinition.*;
import XFactHD.rssmc.client.util.statemapping.*;
import XFactHD.rssmc.common.Content;
import XFactHD.rssmc.common.blocks.gadget.*;
import XFactHD.rssmc.common.blocks.misc.*;
import XFactHD.rssmc.common.blocks.objective.*;
import XFactHD.rssmc.common.blocks.survival.*;
import XFactHD.rssmc.common.data.*;
import XFactHD.rssmc.common.entity.*;
import XFactHD.rssmc.common.entity.gadget.*;
import XFactHD.rssmc.common.utils.*;
import XFactHD.rssmc.common.utils.helper.LogHelper;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import XFactHD.rssmc.common.utils.propertyEnums.*;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.IBakedModel;
//import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.util.vector.Vector3f;

import java.util.Locale;

import static net.minecraft.inventory.EntityEquipmentSlot.*;

@SuppressWarnings({"unused", "ConstantConditions"})
public class ClientManager
{
    private static final boolean[] bools = new boolean[]{true, false};
    private static final EntityEquipmentSlot[] slots = new EntityEquipmentSlot[]{HEAD, CHEST, LEGS, FEET};

    public static void registerModels()
    {
        //TRSRTransformation trsr = new TRSRTransformation(new ItemTransformVec3f(new Vector3f(0, 180, 0), new Vector3f(-1.25F, -1.5F, -3.25F), new Vector3f(.2F, .2F, .2F)));
        //LogHelper.info(trsr.getTranslation());
        //LogHelper.info(trsr.getLeftRot());
        //LogHelper.info(trsr.getScale());
        OBJLoader.INSTANCE.addDomain("rssmc");
        registerItemModel(Content.itemGun, EnumGun.MP7.ordinal(), "gun=" + EnumGun.MP7.toString().toLowerCase(Locale.ENGLISH));
        //for (EnumGun gun : EnumGun.values()) TODO: reenable when models exist
        //{
        //    registerItemModel(Content.itemGun, gun.ordinal(), "gun=" + gun.toString().toLowerCase(Locale.ENGLISH));
        //}
        for (EnumAttachment attachment : EnumAttachment.values())
        {
            registerItemModel(Content.itemAttachment, attachment.ordinal(), "attachment/item_attachment_" + attachment.toString().toLowerCase(Locale.ENGLISH), "inventory");
        }
        for (EnumBullet bullet : EnumBullet.values())
        {
            registerItemModel(Content.itemAmmo, bullet.ordinal(), "ammo/item_ammo_" +  bullet.toString().toLowerCase(Locale.ENGLISH), "inventory");
        }
        for (EnumMagazine magazine : EnumMagazine.values())
        {
            String mag = magazine.toString().toLowerCase(Locale.ENGLISH).replaceFirst("mag_", "");
            registerItemModel(Content.itemMagazine, magazine.ordinal(), "mag/item_magazine_" + mag, "inventory");
        }

        registerItemModel(Content.itemNitroCell, 0, "inventory");
        registerItemModel(Content.itemReinforcement, 0, "block_reinforcement", "inventory");
        registerItemModel(Content.itemRookUpgrade, 0, "armor/item_rook_upgrade", "inventory");
        registerItemModel(Content.itemCrowbar, 0, "inventory");
        registerItemModel(Content.itemActivator, 0, "inventory");
        registerItemModel(Content.itemImpactGrenade, 0, "inventory");
        registerItemModel(Content.itemEMPGrenade, 0, "item_emp_grenade", "inventory");
        registerItemModel(Content.itemGasCanister, 0, "inventory");
        registerItemModel(Content.itemStimDart, 0, "inventory");
        registerItemModel(Content.itemSmokeGrenade, 0, "inventory");
        registerItemModel(Content.itemStunGrenade, 0, "inventory");
        registerItemModel(Content.itemHostagePlacer, 0, "inventory");
        registerItemModel(Content.itemInterogationKnive, 0, "inventory");
        registerItemModel(Content.itemSpeedLoader, 0, "inventory");
        registerItemBlockModel(Content.blockBarricade, 0, "inventory");
        registerItemBlockModel(Content.blockToughBarricade, 0, "inventory");
        registerItemBlockModel(Content.blockClaymore, 0, "facing=south");
        registerItemBlockModel(Content.blockSteelLadder, 0, "bottom=true,facing=north,top=true");
        registerItemBlockModel(Content.blockAmmoBox, 0, "facing=east");
        registerItemBlockModel(Content.blockWeaponShelf, 0, "facing=south");
        registerItemBlockModel(Content.blockClassEquipper, 0, "normal");
        registerItemBlockModel(Content.blockKapkanTrap, 0, "block_kapkan_trap", "inventory");
        registerItemBlockModel(Content.blockWelcomeMat, 0, "activated=true,facing=north");
        registerItemBlockModel(Content.blockArmorBag, 0, "empty=false,facing=north");
        registerItemBlockModel(Content.blockCamera, 0, "activated=true");
        registerItemBlockModel(Content.blockJammer, 0, "facing=north");
        registerItemBlockModel(Content.blockShockWire, 0, "facing=north");
        registerItemBlockModel(Content.blockBomb, 0, "inventory");
        registerItemBlockModel(Content.blockGunCraftingTable, 0, "normal");
        registerItemBlockModel(Content.blockMultiTexture, 0, "normal");
        registerItemBlockModel(Content.blockBreachCharge, 0, "inventory");
        registerItemBlockModel(Content.blockBarbedWire, 0, "inventory");
        registerItemBlockModel(Content.blockDropHatch, 0, "inventory");
        registerItemBlockModel(Content.blockClusterCharge, 0, "inv");
        registerItemBlockModel(Content.blockThermiteCharge, 0, "inventory");
        registerItemBlockModel(Content.blockDeployableShield, 0, "facing=north");
        registerItemBlockModel(Content.blockADS, 0, "facing=up,loaded=true");
        registerItemBlockModel(Content.blockGuMine, 0, "inventory");
        registerItemBlockModel(Content.blockBlackMirror, 0, "inventory");
        registerItemBlockModel(Content.blockMagFiller, 0, "active=false,facing=north");

        setCustomMeshDefinition(Content.itemStimPistol, new MeshDefinitionStimPistol());
        setCustomMeshDefinition(Content.blockWall, new MeshDefinitionWall());
        setCustomMeshDefinition(Content.blockFloorPanel, new MeshDefinitionFloorPanel());
        setCustomMeshDefinition(Content.itemOperatorArmor, new MeshDefinitionOperatorArmor());
        setCustomMeshDefinition(Content.itemFragGrenade, new MeshDefinitionFragGrenade());
        setCustomMeshDefinition(Content.itemNitroPhone, new MeshDefinitionNitroPhone());
        setCustomMeshDefinition(Content.itemCandelaGrenade, new MeshDefinitionCandela());
        setCustomMeshDefinition(Content.itemPhone, new MeshDefinitionPhone());

        ModelLoader.setCustomStateMapper(Content.blockReinforcement, new StateMapperBlockReinforcement());
        ModelLoader.setCustomStateMapper(Content.blockBarricade, new StateMapperBlockBarricade());
        ModelLoader.setCustomStateMapper(Content.blockToughBarricade, new StateMapperBlockToughBarricade());
        ModelLoader.setCustomStateMapper(Content.blockCamera, new StateMapperBlockCamera());
        ModelLoader.setCustomStateMapper(Content.blockBlackMirror, new StateMapperBlockBlackMirror());
        ModelLoader.setCustomStateMapper(Content.blockBomb, new StateMap.Builder().ignore(PropertyHolder.DEFUSED, PropertyHolder.DEFUSING).build());
        ModelLoader.setCustomStateMapper(Content.blockThermiteCharge, new StateMap.Builder().ignore(PropertyHolder.ACTIVATED).build());
        ModelLoader.setCustomStateMapper(Content.blockRailingDummy, new StateMap.Builder().ignore(PropertyHolder.FACING_CARDINAL, PropertyHolder.CORNER).build());
        ModelLoader.setCustomStateMapper(Content.blockStairRailingDummy, new StateMap.Builder().ignore(PropertyHolder.RIGHT, PropertyHolder.FACING_CARDINAL, PropertyHolder.RAILING_TYPE).build());
    }

    public static void registerRenderers()
    {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityClaymore.class, new TESRClaymore());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWeaponShelf.class, new TESRWeaponShelf());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityKapkanTrap.class, new TESRKapkanTrap());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityArmorBag.class, new TESRArmorBag());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBioContainer.class, new TESRBioContainer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityThermiteCharge.class, new TESRThermiteCharge());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityActiveDefenseSystem.class, new TESRActiveDefenseSystem());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBomb.class, new TESRBomb());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDefuser.class, new TESRDefuser());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMagFiller.class, new TESRMagFiller());

        RenderingRegistry.registerEntityRenderingHandler(EntityNitroCell.class, new RenderFactoryNitroCell());
        RenderingRegistry.registerEntityRenderingHandler(EntityImpactGrenade.class, new RenderFactoryImpactGrenade());
        RenderingRegistry.registerEntityRenderingHandler(EntityFragGrenade.class, new RenderFactoryFragGrenade());
        RenderingRegistry.registerEntityRenderingHandler(EntityStunGrenade.class, new RenderFactoryStunGrenade());
        RenderingRegistry.registerEntityRenderingHandler(EntitySmokeGrenade.class, new RenderFactorySmokeGrenade());
        RenderingRegistry.registerEntityRenderingHandler(EntityEMPGrenade.class, new RenderFactoryEMPGrenade());
        RenderingRegistry.registerEntityRenderingHandler(EntityGasCanister.class, new RenderFactoryGasCanister());
        RenderingRegistry.registerEntityRenderingHandler(EntityHostage.class, new RenderFactoryEntityHostage());
    }

    private static void registerItemBlockModel(Block block, int meta, String variant)
    {
        if (variant.equals("inventory"))
        {
            registerItemBlockModel(block, meta, Utils.toSnakeCase(block.getRegistryName().getResourcePath()), variant);
        }
        else
        {
            registerItemBlockModel(block, meta, block.getRegistryName().getResourcePath(), variant);
        }
    }

    private static void registerItemBlockModel(Block block, int meta, String location, String variant)
    {
        registerItemModel(Item.getItemFromBlock(block), meta, location, variant);
    }

    private static void registerItemModel(Item item, int meta, String variant)
    {
        registerItemModel(item, meta, Utils.toSnakeCase(item.getRegistryName().getResourcePath()), variant);
    }

    private static void registerItemModel(Item item, int meta, String location, String variant)
    {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(new ResourceLocation(Reference.MOD_ID, location), variant));
    }

    private static void setCustomMeshDefinition(Block block, CustomMeshDefinition definition)
    {
        ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(block), definition);
        definition.registerVariants();
    }

    private static void setCustomMeshDefinition(Item item, CustomMeshDefinition definition)
    {
        ModelLoader.setCustomMeshDefinition(item, definition);
        definition.registerVariants();
    }

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event)
    {
        IRegistry<ModelResourceLocation, IBakedModel> registry = event.getModelRegistry();

        //BlockReinforcement
        for (EnumFacing facing : EnumFacing.values())
        {
            if (facing != EnumFacing.UP && facing != EnumFacing.DOWN)
            {
                for (Connection con : Connection.values())
                {
                    String variant = "facing=" + facing.toString().toLowerCase(Locale.ENGLISH) + ",con=" + con.toString().toLowerCase(Locale.ENGLISH);
                    ModelResourceLocation location = new ModelResourceLocation(Content.blockReinforcement.getRegistryName(), variant);
                    IBakedModel reinforcement = registry.getObject(location);
                    IBakedModel replacement = new ModelBlockReinforcement(reinforcement);
                    registry.putObject(location, replacement);
                }
            }
        }

        //BlockMultiTexture
        IBakedModel multiTexture = registry.getObject(new ModelResourceLocation(Content.blockMultiTexture.getRegistryName(), "normal"));
        IBakedModel multiTextureReplacement = new ModelBlockMultiTexture(multiTexture);
        registry.putObject(new ModelResourceLocation(Content.blockMultiTexture.getRegistryName(), "normal"), multiTextureReplacement);
    }

    @SubscribeEvent
    public void onTextureMapReload(TextureStitchEvent.Pre event)
    {
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/camera_marker"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/player_marker"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/footprint_false"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/footprint_true"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/gui_objective_location"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/gui_bomb_location_a"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/gui_bomb_location_b"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/gui_item_cooldown_time"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/firemode_single"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/firemode_double"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/firemode_triple"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/firemode_auto"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/crosshair_gun"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/crosshair_shotgun"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/kill_cross"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/black_eye_filter"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/drone_overlay"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/camera_selectors"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/camera_detail"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/camera_symbol"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/jammer_filter"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/yokai_filter"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/yokai_overlay_stationary"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/yokai_overlay_floored"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/yokai_widgets"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/camera_overlay"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/player_slot"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/team_colors"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/player_list_blue"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/player_list_orange"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/health_usage_timer"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/dbno_symbol"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/compass"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:white"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:black"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/jackal_symbol"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/point_info"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/widgets/slot"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/widgets/plus"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/widgets/minus"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/widgets/back"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/widgets/forward"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/widgets/info"));
        event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/armor_bag_symbol"));
        for (int i = 0; i < 125; i++)
        {
            event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/secure_status/secure_status_" + i));
        }
        event.getMap().registerSprite(new ResourceLocation("rssmc:operators/unknown"));
        for (EnumOperator op : EnumOperator.values())
        {
            if (!op.toString().toLowerCase(Locale.ENGLISH).contains("unknown"))
            {
                event.getMap().registerSprite(new ResourceLocation("rssmc:operators/" + op.toString().toLowerCase(Locale.ENGLISH)));
            }
        }
        for (EnumScreenEffect effect : EnumScreenEffect.values())
        {
            event.getMap().registerSprite(new ResourceLocation("rssmc:gui/overlay/effect_" + effect.toString().toLowerCase(Locale.ENGLISH)));
        }
    }
}