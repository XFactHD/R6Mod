package xfacthd.r6mod.common.util;

import com.google.gson.*;
import net.minecraft.data.*;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.common.R6Content;
import xfacthd.r6mod.common.data.*;
import xfacthd.r6mod.common.data.blockdata.WallMaterial;
import xfacthd.r6mod.common.data.blockdata.WallSegment;
import xfacthd.r6mod.common.data.gun_data.Firemode;
import xfacthd.r6mod.common.data.gun_data.ReloadState;
import xfacthd.r6mod.common.data.itemsubtypes.EnumAttachment;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGun;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Locale;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataCreator
{
    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();

        gen.addProvider(new R6SoundEvents.SoundProvider(gen, R6Mod.MODID));
        //gen.addProvider(new GunDefinitionProvider(gen, R6Mod.MODID)); //DO NOT USE
        gen.addProvider(new BarricadeStateProvider(gen, R6Mod.MODID, fileHelper));
        gen.addProvider(new ToughBarricadeStateProvider(gen, R6Mod.MODID, fileHelper));
        gen.addProvider(new EmptyGunModelProvider(gen, R6Mod.MODID, fileHelper));
        gen.addProvider(new WallStateProvider(gen, R6Mod.MODID, fileHelper));
        gen.addProvider(new BlackMirrorStateProvider(gen, R6Mod.MODID, fileHelper));
        gen.addProvider(new ReinforcementStateProvider(gen, R6Mod.MODID, fileHelper));
        gen.addProvider(new FloorPanelStateProvider(gen, R6Mod.MODID, fileHelper));
    }

    @SuppressWarnings("unused")
    private static final class GunDefinitionProvider implements IDataProvider
    {
        private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

        private final DataGenerator gen;
        private final String modid;

        public GunDefinitionProvider(DataGenerator gen, String modid)
        {
            this.gen = gen;
            this.modid = modid;
        }

        @Override
        public void act(DirectoryCache cache) throws IOException
        {
            for (EnumGun gun : EnumGun.values())
            {
                JsonObject obj = new JsonObject();

                obj.addProperty("name", gun.toString().toLowerCase(Locale.ENGLISH));
                obj.addProperty("type", gun.getGunType().toString().toLowerCase(Locale.ENGLISH));
                obj.addProperty("has_mag", gun.hasMag());
                if (!gun.hasMag())
                {
                    obj.addProperty("mag_cap", gun.getMagCapacity());
                    obj.addProperty("ammo_type", gun.getAmmoType().toString().toLowerCase(Locale.ENGLISH));
                }
                obj.addProperty("rpm", gun.getRoundsPerMinute());
                obj.addProperty("damage", gun.getGunDamageBase() * 5F);
                obj.addProperty("damage_silenced", gun.getGunDamageSilenced() * 5F);
                obj.addProperty("spread", gun.getBaseSpread());
                obj.addProperty("recoil", gun.getBaseRecoil());
                obj.addProperty("ads_time", gun.getAimTime(Collections.emptyList()));
                obj.addProperty("reserve", gun.getAdditionalAmmo());
                obj.addProperty("automatic", gun.isAutomatic());
                if (gun.getGunType() == EnumGun.Type.SHOTGUN) { obj.addProperty("pump_action", false); }
                obj.addProperty("closed_bolt", gun.canChamberAdditionalBullet());
                if (Utils.arrayContains(gun.getFiremodes(), Firemode.BURST)) { obj.addProperty("burst_count", gun.getBurstBulletCount()); }
                if (gun.getShotgunPelletCount() != 0) { obj.addProperty("pellet_count", gun.getShotgunPelletCount()); }
                obj.addProperty("max_penetration", gun.getMaxPenetrationCount());

                JsonObject reloadTimes = new JsonObject();
                for (ReloadState state : ReloadState.values())
                {
                    reloadTimes.addProperty(state.toString().toLowerCase(Locale.ENGLISH), 0);
                }
                obj.add("reload_times", reloadTimes);

                JsonObject attachments = new JsonObject();
                for (EnumAttachment attachment : gun.getCompatibleAttachements())
                {
                    attachments.add(attachment.toString().toLowerCase(Locale.ENGLISH), new JsonObject());
                }
                obj.add("attachments", attachments);

                JsonArray firemodes = new JsonArray();
                for (Firemode firemode : gun.getFiremodes())
                {
                    firemodes.add(firemode.toString().toLowerCase(Locale.ENGLISH));
                }
                obj.add("firemodes", firemodes);

                if (gun.hasMag()) { obj.add("mag_transform", new JsonObject()); }

                Path outPath = gen.getOutputFolder().resolve("data/" + modid + "/guns/" + gun.toString().toLowerCase(Locale.ENGLISH) + ".json");
                IDataProvider.save(GSON, cache, obj, outPath);
            }
        }

        @Override
        public String getName() { return "r6mod:gun_definitions"; }
    }

    private static final class BarricadeStateProvider extends BlockStateProvider
    {
        public BarricadeStateProvider(DataGenerator gen, String modid, ExistingFileHelper fileHelper)
        {
            super(gen, modid, fileHelper);
        }

        @Override
        protected void registerStatesAndModels()
        {
            ModelFile modelGlass =              models().getExistingFile(modLoc("block/building/block_glass_pane"));
            ModelFile modelTop =                models().getExistingFile(modLoc("block/building/block_barricade_top"));
            ModelFile modelBottom =             models().getExistingFile(modLoc("block/building/block_barricade_bottom"));
            ModelFile modelTopWindow =          models().getExistingFile(modLoc("block/building/block_barricade_top_window"));
            ModelFile modelBottomWindow =       models().getExistingFile(modLoc("block/building/block_barricade_bottom_window"));
            ModelFile modelBottomDoor =         models().getExistingFile(modLoc("block/building/block_barricade_bottom_door"));
            ModelFile modelTopLeft =            models().getExistingFile(modLoc("block/building/block_barricade_left_top"));
            ModelFile modelTopRight =           models().getExistingFile(modLoc("block/building/block_barricade_right_top"));
            ModelFile modelTopCenter =          models().getExistingFile(modLoc("block/building/block_barricade_center_top"));
            ModelFile modelBottomLeft =         models().getExistingFile(modLoc("block/building/block_barricade_left_bottom"));
            ModelFile modelBottomRight =        models().getExistingFile(modLoc("block/building/block_barricade_right_bottom"));
            ModelFile modelBottomCenter =       models().getExistingFile(modLoc("block/building/block_barricade_center_bottom"));
            ModelFile modelBottomDoorLeft =     models().getExistingFile(modLoc("block/building/block_barricade_left_bottom_door"));
            ModelFile modelBottomDoorRight =    models().getExistingFile(modLoc("block/building/block_barricade_right_bottom_door"));
            ModelFile modelBottomDoorCenter =   models().getExistingFile(modLoc("block/building/block_barricade_center_bottom_door"));
            ModelFile modelTopWindowLeft =      models().getExistingFile(modLoc("block/building/block_barricade_left_top_window"));
            ModelFile modelTopWindowRight =     models().getExistingFile(modLoc("block/building/block_barricade_right_top_window"));
            ModelFile modelTopWindowCenter =    models().getExistingFile(modLoc("block/building/block_barricade_center_top_window"));
            ModelFile modelBottomWindowLeft =   models().getExistingFile(modLoc("block/building/block_barricade_left_bottom_window"));
            ModelFile modelBottomWindowRight =  models().getExistingFile(modLoc("block/building/block_barricade_right_bottom_window"));
            ModelFile modelBottomWindowCenter = models().getExistingFile(modLoc("block/building/block_barricade_center_bottom_window"));

            MultiPartBlockStateBuilder builder = getMultipartBuilder(R6Content.blockBarricade);

            for (Direction facing : PropertyHolder.FACING_HOR.getAllowedValues())
            {
                //Glass pane model
                {
                    builder.part()
                            .modelFile(modelGlass)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.ON_GLASS, true);
                }

                //Normal model
                {
                    builder.part()
                            .modelFile(modelTop)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, true)
                            .condition(PropertyHolder.ON_GLASS, false)
                            .condition(PropertyHolder.LARGE, false);

                    builder.part()
                            .modelFile(modelBottom)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, false)
                            .condition(PropertyHolder.ON_GLASS, false)
                            .condition(PropertyHolder.LARGE, false)
                            .condition(PropertyHolder.DOOR, false);
                }

                //Window model
                {
                    builder.part()
                            .modelFile(modelTopWindow)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, true)
                            .condition(PropertyHolder.ON_GLASS, true)
                            .condition(PropertyHolder.LARGE, false);

                    builder.part()
                            .modelFile(modelBottomWindow)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, false)
                            .condition(PropertyHolder.ON_GLASS, true)
                            .condition(PropertyHolder.LARGE, false);
                }

                //Large model left
                {
                    builder.part()
                            .modelFile(modelTopLeft)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, true)
                            .condition(PropertyHolder.ON_GLASS, false)
                            .condition(PropertyHolder.LARGE, true)
                            .condition(PropertyHolder.LEFT, true);

                    builder.part()
                            .modelFile(modelBottomLeft)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, false)
                            .condition(PropertyHolder.ON_GLASS, false)
                            .condition(PropertyHolder.LARGE, true)
                            .condition(PropertyHolder.DOOR, false)
                            .condition(PropertyHolder.LEFT, true);
                }

                //Large window model left
                {
                    builder.part()
                            .modelFile(modelTopWindowLeft)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, true)
                            .condition(PropertyHolder.ON_GLASS, true)
                            .condition(PropertyHolder.LARGE, true)
                            .condition(PropertyHolder.LEFT, true);

                    //Bottom model window large left
                    builder.part()
                            .modelFile(modelBottomWindowLeft)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, false)
                            .condition(PropertyHolder.ON_GLASS, true)
                            .condition(PropertyHolder.LARGE, true)
                            .condition(PropertyHolder.LEFT, true);
                }

                //Large model right
                {
                    builder.part()
                            .modelFile(modelTopRight)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, true)
                            .condition(PropertyHolder.ON_GLASS, false)
                            .condition(PropertyHolder.LARGE, true)
                            .condition(PropertyHolder.RIGHT, true);

                    builder.part()
                            .modelFile(modelBottomRight)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, false)
                            .condition(PropertyHolder.ON_GLASS, false)
                            .condition(PropertyHolder.LARGE, true)
                            .condition(PropertyHolder.DOOR, false)
                            .condition(PropertyHolder.RIGHT, true);
                }

                //Large window model right
                {
                    builder.part()
                            .modelFile(modelTopWindowRight)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, true)
                            .condition(PropertyHolder.ON_GLASS, true)
                            .condition(PropertyHolder.LARGE, true)
                            .condition(PropertyHolder.RIGHT, true);

                    //Bottom model window large left
                    builder.part()
                            .modelFile(modelBottomWindowRight)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, false)
                            .condition(PropertyHolder.ON_GLASS, true)
                            .condition(PropertyHolder.LARGE, true)
                            .condition(PropertyHolder.RIGHT, true);
                }

                //Large model center
                {
                    builder.part()
                            .modelFile(modelTopCenter)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, true)
                            .condition(PropertyHolder.ON_GLASS, false)
                            .condition(PropertyHolder.LARGE, true)
                            .condition(PropertyHolder.CENTER, true);

                    builder.part()
                            .modelFile(modelBottomCenter)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, false)
                            .condition(PropertyHolder.ON_GLASS, false)
                            .condition(PropertyHolder.LARGE, true)
                            .condition(PropertyHolder.DOOR, false)
                            .condition(PropertyHolder.CENTER, true);
                }

                //Large window model center
                {
                    builder.part()
                            .modelFile(modelTopWindowCenter)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, true)
                            .condition(PropertyHolder.ON_GLASS, true)
                            .condition(PropertyHolder.LARGE, true)
                            .condition(PropertyHolder.CENTER, true);

                    //Bottom model window large left
                    builder.part()
                            .modelFile(modelBottomWindowCenter)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, false)
                            .condition(PropertyHolder.ON_GLASS, true)
                            .condition(PropertyHolder.LARGE, true)
                            .condition(PropertyHolder.CENTER, true);
                }

                //Door bottom model
                {
                    builder.part()
                            .modelFile(modelBottomDoor)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, false)
                            .condition(PropertyHolder.ON_GLASS, false)
                            .condition(PropertyHolder.LARGE, false)
                            .condition(PropertyHolder.DOOR, true);

                    builder.part()
                            .modelFile(modelBottomDoorLeft)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, false)
                            .condition(PropertyHolder.ON_GLASS, false)
                            .condition(PropertyHolder.LARGE, true)
                            .condition(PropertyHolder.DOOR, true)
                            .condition(PropertyHolder.LEFT, true);

                    builder.part()
                            .modelFile(modelBottomDoorRight)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, false)
                            .condition(PropertyHolder.ON_GLASS, false)
                            .condition(PropertyHolder.LARGE, true)
                            .condition(PropertyHolder.DOOR, true)
                            .condition(PropertyHolder.RIGHT, true);

                    builder.part()
                            .modelFile(modelBottomDoorCenter)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, false)
                            .condition(PropertyHolder.ON_GLASS, false)
                            .condition(PropertyHolder.LARGE, true)
                            .condition(PropertyHolder.DOOR, true)
                            .condition(PropertyHolder.CENTER, true);
                }
            }
        }

        @Override
        public String getName() { return "r6mod:barricade_block_states"; }
    }

    private static final class ToughBarricadeStateProvider extends BlockStateProvider
    {
        public ToughBarricadeStateProvider(DataGenerator gen, String modid, ExistingFileHelper fileHelper)
        {
            super(gen, modid, fileHelper);
        }

        @Override
        protected void registerStatesAndModels()
        {
            ModelFile modelGlass =              models().getExistingFile(modLoc("block/building/block_glass_pane"));
            ModelFile modelTop =                models().getExistingFile(modLoc("block/gadget/block_tough_barricade_top"));
            ModelFile modelBottom =             models().getExistingFile(modLoc("block/gadget/block_tough_barricade_bottom"));
            ModelFile modelTopWindow =          models().getExistingFile(modLoc("block/gadget/block_tough_barricade_top_window"));
            ModelFile modelBottomWindow =       models().getExistingFile(modLoc("block/gadget/block_tough_barricade_bottom_window"));
            ModelFile modelBottomDoor =         models().getExistingFile(modLoc("block/gadget/block_tough_barricade_bottom_door"));
            ModelFile modelTopLeft =            models().getExistingFile(modLoc("block/gadget/block_tough_barricade_left_top"));
            ModelFile modelTopRight =           models().getExistingFile(modLoc("block/gadget/block_tough_barricade_right_top"));
            ModelFile modelTopCenter =          models().getExistingFile(modLoc("block/gadget/block_tough_barricade_center_top"));
            ModelFile modelBottomLeft =         models().getExistingFile(modLoc("block/gadget/block_tough_barricade_left_bottom"));
            ModelFile modelBottomRight =        models().getExistingFile(modLoc("block/gadget/block_tough_barricade_right_bottom"));
            ModelFile modelBottomCenter =       models().getExistingFile(modLoc("block/gadget/block_tough_barricade_center_bottom"));
            ModelFile modelBottomDoorLeft =     models().getExistingFile(modLoc("block/gadget/block_tough_barricade_left_bottom_door"));
            ModelFile modelBottomDoorRight =    models().getExistingFile(modLoc("block/gadget/block_tough_barricade_right_bottom_door"));
            ModelFile modelBottomDoorCenter =   models().getExistingFile(modLoc("block/gadget/block_tough_barricade_center_bottom_door"));
            ModelFile modelTopWindowLeft =      models().getExistingFile(modLoc("block/gadget/block_tough_barricade_left_top_window"));
            ModelFile modelTopWindowRight =     models().getExistingFile(modLoc("block/gadget/block_tough_barricade_right_top_window"));
            ModelFile modelTopWindowCenter =    models().getExistingFile(modLoc("block/gadget/block_tough_barricade_center_top_window"));
            ModelFile modelBottomWindowLeft =   models().getExistingFile(modLoc("block/gadget/block_tough_barricade_left_bottom_window"));
            ModelFile modelBottomWindowRight =  models().getExistingFile(modLoc("block/gadget/block_tough_barricade_right_bottom_window"));
            ModelFile modelBottomWindowCenter = models().getExistingFile(modLoc("block/gadget/block_tough_barricade_center_bottom_window"));

            MultiPartBlockStateBuilder builder = getMultipartBuilder(R6Content.blockToughBarricade);

            for (Direction facing : PropertyHolder.FACING_HOR.getAllowedValues())
            {
                //Glass pane model
                {
                    builder.part()
                            .modelFile(modelGlass)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.ON_GLASS, true);
                }

                //Normal model
                {
                    builder.part()
                            .modelFile(modelTop)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, true)
                            .condition(PropertyHolder.ON_GLASS, false)
                            .condition(PropertyHolder.LARGE, false);

                    builder.part()
                            .modelFile(modelBottom)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, false)
                            .condition(PropertyHolder.ON_GLASS, false)
                            .condition(PropertyHolder.LARGE, false)
                            .condition(PropertyHolder.DOOR, false);
                }

                //Window model
                {
                    builder.part()
                            .modelFile(modelTopWindow)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, true)
                            .condition(PropertyHolder.ON_GLASS, true)
                            .condition(PropertyHolder.LARGE, false);

                    builder.part()
                            .modelFile(modelBottomWindow)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, false)
                            .condition(PropertyHolder.ON_GLASS, true)
                            .condition(PropertyHolder.LARGE, false);
                }

                //Large model left
                {
                    builder.part()
                            .modelFile(modelTopLeft)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, true)
                            .condition(PropertyHolder.ON_GLASS, false)
                            .condition(PropertyHolder.LARGE, true)
                            .condition(PropertyHolder.LEFT, true);

                    builder.part()
                            .modelFile(modelBottomLeft)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, false)
                            .condition(PropertyHolder.ON_GLASS, false)
                            .condition(PropertyHolder.LARGE, true)
                            .condition(PropertyHolder.DOOR, false)
                            .condition(PropertyHolder.LEFT, true);
                }

                //Large window model left
                {
                    builder.part()
                            .modelFile(modelTopWindowLeft)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, true)
                            .condition(PropertyHolder.ON_GLASS, true)
                            .condition(PropertyHolder.LARGE, true)
                            .condition(PropertyHolder.LEFT, true);

                    //Bottom model window large left
                    builder.part()
                            .modelFile(modelBottomWindowLeft)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, false)
                            .condition(PropertyHolder.ON_GLASS, true)
                            .condition(PropertyHolder.LARGE, true)
                            .condition(PropertyHolder.LEFT, true);
                }

                //Large model right
                {
                    builder.part()
                            .modelFile(modelTopRight)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, true)
                            .condition(PropertyHolder.ON_GLASS, false)
                            .condition(PropertyHolder.LARGE, true)
                            .condition(PropertyHolder.RIGHT, true);

                    builder.part()
                            .modelFile(modelBottomRight)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, false)
                            .condition(PropertyHolder.ON_GLASS, false)
                            .condition(PropertyHolder.LARGE, true)
                            .condition(PropertyHolder.DOOR, false)
                            .condition(PropertyHolder.RIGHT, true);
                }

                //Large window model right
                {
                    builder.part()
                            .modelFile(modelTopWindowRight)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, true)
                            .condition(PropertyHolder.ON_GLASS, true)
                            .condition(PropertyHolder.LARGE, true)
                            .condition(PropertyHolder.RIGHT, true);

                    //Bottom model window large left
                    builder.part()
                            .modelFile(modelBottomWindowRight)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, false)
                            .condition(PropertyHolder.ON_GLASS, true)
                            .condition(PropertyHolder.LARGE, true)
                            .condition(PropertyHolder.RIGHT, true);
                }

                //Large model center
                {
                    builder.part()
                            .modelFile(modelTopCenter)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, true)
                            .condition(PropertyHolder.ON_GLASS, false)
                            .condition(PropertyHolder.LARGE, true)
                            .condition(PropertyHolder.CENTER, true);

                    builder.part()
                            .modelFile(modelBottomCenter)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, false)
                            .condition(PropertyHolder.ON_GLASS, false)
                            .condition(PropertyHolder.LARGE, true)
                            .condition(PropertyHolder.DOOR, false)
                            .condition(PropertyHolder.CENTER, true);
                }

                //Large window model center
                {
                    builder.part()
                            .modelFile(modelTopWindowCenter)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, true)
                            .condition(PropertyHolder.ON_GLASS, true)
                            .condition(PropertyHolder.LARGE, true)
                            .condition(PropertyHolder.CENTER, true);

                    //Bottom model window large left
                    builder.part()
                            .modelFile(modelBottomWindowCenter)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, false)
                            .condition(PropertyHolder.ON_GLASS, true)
                            .condition(PropertyHolder.LARGE, true)
                            .condition(PropertyHolder.CENTER, true);
                }

                //Door bottom model
                {
                    builder.part()
                            .modelFile(modelBottomDoor)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, false)
                            .condition(PropertyHolder.ON_GLASS, false)
                            .condition(PropertyHolder.LARGE, false)
                            .condition(PropertyHolder.DOOR, true);

                    builder.part()
                            .modelFile(modelBottomDoorLeft)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, false)
                            .condition(PropertyHolder.ON_GLASS, false)
                            .condition(PropertyHolder.LARGE, true)
                            .condition(PropertyHolder.DOOR, true)
                            .condition(PropertyHolder.LEFT, true);

                    builder.part()
                            .modelFile(modelBottomDoorRight)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, false)
                            .condition(PropertyHolder.ON_GLASS, false)
                            .condition(PropertyHolder.LARGE, true)
                            .condition(PropertyHolder.DOOR, true)
                            .condition(PropertyHolder.RIGHT, true);

                    builder.part()
                            .modelFile(modelBottomDoorCenter)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.TOP, false)
                            .condition(PropertyHolder.ON_GLASS, false)
                            .condition(PropertyHolder.LARGE, true)
                            .condition(PropertyHolder.DOOR, true)
                            .condition(PropertyHolder.CENTER, true);
                }
            }
        }

        @Override
        public String getName() { return "r6mod:tough_barricade_block_states"; }
    }

    private static final class EmptyGunModelProvider extends ItemModelProvider
    {
        public EmptyGunModelProvider(DataGenerator gen, String modid, ExistingFileHelper fileHelper)
        {
            super(gen, modid, fileHelper);
        }

        @Override
        protected void registerModels()
        {
            for (EnumGun gun : EnumGun.values())
            {
                ResourceLocation loc = new ResourceLocation(R6Mod.MODID, gun.toItemName());
                if (!existingFileHelper.exists(loc, ResourcePackType.CLIENT_RESOURCES, ".json", "models/item"))
                {
                    getBuilder(loc.toString());
                }
            }
        }

        @Override
        public String getName() { return "r6mod:empty_gun_models"; }
    }

    private static final class WallStateProvider extends BlockStateProvider
    {
        public static WallStateProvider INSTANCE;

        public WallStateProvider(DataGenerator gen, String modid, ExistingFileHelper fileHelper)
        {
            super(gen, modid, fileHelper);
            INSTANCE = this;
        }

        @Override
        protected void registerStatesAndModels()
        {
            ModelFile wallBarredDestroyed = models().getExistingFile(modLoc("block/building/block_wall_barred_destroyed"));

            for (WallMaterial material : WallMaterial.values())
            {
                //MultiPartBlockStateBuilder builder = getMultipartBuilder(R6Content.blockWalls.get(material));
                VariantBlockStateBuilder builder = getVariantBuilder(R6Content.blockWalls.get(material));

                ModelFile wallSoft = models()
                        .withExistingParent(modLoc("block/building/block_wall_soft_" + material.getString()).toString(), modLoc("block/building/block_wall_soft"))
                        .texture("surface", material.getTexture());

                builder.addModels(builder.partialState().with(PropertyHolder.FACING_NE, Direction.NORTH),
                        ConfiguredModel.builder()
                                .modelFile(wallSoft)
                                .rotationY(getRotation(Direction.NORTH))
                                .build());

                builder.addModels(builder.partialState().with(PropertyHolder.FACING_NE, Direction.EAST),
                        ConfiguredModel.builder()
                                .modelFile(wallSoft)
                                .rotationY(getRotation(Direction.EAST))
                                .build());

                builder = getVariantBuilder(R6Content.blockWallsBarred.get(material));

                ModelFile wallBarred = models()
                        .withExistingParent(modLoc("block/building/block_wall_barred_" + material.getString()).toString(), modLoc("block/building/block_wall_barred"))
                        .texture("surface", material.getTexture());

                builder.addModels(builder.partialState()
                                .with(PropertyHolder.FACING_NE, Direction.NORTH)
                                .with(PropertyHolder.DESTROYED, false),
                        ConfiguredModel.builder()
                                .modelFile(wallBarred)
                                .rotationY(getRotation(Direction.NORTH))
                                .build());

                builder.addModels(builder.partialState()
                                .with(PropertyHolder.FACING_NE, Direction.EAST)
                                .with(PropertyHolder.DESTROYED, false),
                        ConfiguredModel.builder()
                                .modelFile(wallBarred)
                                .rotationY(getRotation(Direction.EAST))
                                .build());

                builder.addModels(builder.partialState()
                                .with(PropertyHolder.FACING_NE, Direction.NORTH)
                                .with(PropertyHolder.DESTROYED, true),
                        ConfiguredModel.builder()
                                .modelFile(wallBarredDestroyed)
                                .rotationY(getRotation(Direction.NORTH))
                                .build());

                builder.addModels(builder.partialState()
                                .with(PropertyHolder.FACING_NE, Direction.EAST)
                                .with(PropertyHolder.DESTROYED, true),
                        ConfiguredModel.builder()
                                .modelFile(wallBarredDestroyed)
                                .rotationY(getRotation(Direction.EAST))
                                .build());
            }
        }

        @Override
        public String getName() { return "r6mod:wall_block_states"; }
    }

    private static final class BlackMirrorStateProvider extends BlockStateProvider
    {
        public BlackMirrorStateProvider(DataGenerator gen, String modid, ExistingFileHelper fileHelper)
        {
            super(gen, modid, fileHelper);
        }

        @Override
        protected void registerStatesAndModels()
        {
            ModelFile windowLeft =  models().getExistingFile(modLoc("block/gadget/block_black_mirror_glass_left"));
            ModelFile windowRight = models().getExistingFile(modLoc("block/gadget/block_black_mirror_glass_right"));

            MultiPartBlockStateBuilder builder = getMultipartBuilder(R6Content.blockBlackMirror);

            for (Direction facing : PropertyHolder.FACING_HOR.getAllowedValues())
            {
                //Glass left
                builder.part()
                        .modelFile(windowLeft)
                        .rotationY(getRotation(facing))
                        .addModel()
                        .condition(PropertyHolder.FACING_HOR, facing)
                        .condition(PropertyHolder.RIGHT, false)
                        .condition(PropertyHolder.OPEN, false);

                //Glass right
                builder.part()
                        .modelFile(windowRight)
                        .rotationY(getRotation(facing))
                        .addModel()
                        .condition(PropertyHolder.FACING_HOR, facing)
                        .condition(PropertyHolder.RIGHT, true)
                        .condition(PropertyHolder.OPEN, false);

                for (WallMaterial material : WallMaterial.values())
                {
                    ModelFile intactLeft = models().getExistingFile(modLoc("block/gadget/block_black_mirror_left_" + material.getString()));
                    ModelFile intactRight = models().getExistingFile(modLoc("block/gadget/block_black_mirror_right_" + material.getString()));
                    ModelFile brokenLeft = models().getExistingFile(modLoc("block/gadget/block_black_mirror_left_destroyed_" + material.getString()));
                    ModelFile brokenRight = models().getExistingFile(modLoc("block/gadget/block_black_mirror_right_destroyed_" + material.getString()));

                    //Frame left canister intact
                    builder.part()
                            .modelFile(intactLeft)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.RIGHT, false)
                            .condition(PropertyHolder.DESTROYED, false)
                            .condition(PropertyHolder.MATERIAL, material);

                    //Frame right canister intact
                    builder.part()
                            .modelFile(intactRight)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.RIGHT, true)
                            .condition(PropertyHolder.DESTROYED, false)
                            .condition(PropertyHolder.MATERIAL, material);

                    //Frame left canister broken
                    builder.part()
                            .modelFile(brokenLeft)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.RIGHT, false)
                            .condition(PropertyHolder.DESTROYED, true)
                            .condition(PropertyHolder.MATERIAL, material);

                    //Frame right canister broken
                    builder.part()
                            .modelFile(brokenRight)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.RIGHT, true)
                            .condition(PropertyHolder.DESTROYED, true)
                            .condition(PropertyHolder.MATERIAL, material);
                }
            }
        }

        @Override
        public String getName() { return "r6mod:black_mirror_block_states"; }
    }

    private static final class ReinforcementStateProvider extends BlockStateProvider
    {
        public ReinforcementStateProvider(DataGenerator gen, String modid, ExistingFileHelper fileHelper)
        {
            super(gen, modid, fileHelper);
        }

        @Override
        protected void registerStatesAndModels()
        {
            //Stupid workaround to be able to use the models generated by the WallStateProvider
            WallStateProvider.INSTANCE.models().generatedModels.forEach((loc, model) ->
            {
                if (loc.getPath().contains("wall_soft_"))
                {
                    models().generatedModels.put(loc, model);
                }
            });

            MultiPartBlockStateBuilder builder = getMultipartBuilder(R6Content.blockReinforcement);

            for (WallSegment segment : WallSegment.values())
            {
                ModelFile model = models().getExistingFile(modLoc("block/building/block_reinforcement_" + segment.getString()));

                for (Direction facing : Direction.Plane.HORIZONTAL)
                {
                    builder.part()
                            .modelFile(model)
                            .rotationY(getRotation(facing))
                            .addModel()
                            .condition(PropertyHolder.FACING_HOR, facing)
                            .condition(PropertyHolder.WALL_SEGMENT, segment);

                    for (WallMaterial material : WallMaterial.values())
                    {
                        ModelFile wallModel = models().getExistingFile(modLoc("block/building/block_wall_soft_" + material.getString()));

                        builder.part()
                                .modelFile(wallModel)
                                .rotationY(getRotation(facing))
                                .addModel()
                                .condition(PropertyHolder.FACING_HOR, facing)
                                .condition(PropertyHolder.MATERIAL, material);
                    }
                }
            }
        }

        @Override
        public String getName()
        {
            return "r6mod:reinforcement_block_states";
        }
    }

    private static final class FloorPanelStateProvider extends BlockStateProvider
    {
        public FloorPanelStateProvider(DataGenerator gen, String modid, ExistingFileHelper fileHelper)
        {
            super(gen, modid, fileHelper);
        }

        @Override
        protected void registerStatesAndModels()
        {
            ModelFile modelDestroyed = models().getExistingFile(modLoc("block/building/block_floor_panel_destroyed"));

            for (WallMaterial material : WallMaterial.values())
            {
                VariantBlockStateBuilder builder = getVariantBuilder(R6Content.blockFloorPanels.get(material));

                ModelFile model = models()
                        .withExistingParent(modLoc("block/building/block_floor_panel_" + material.getString()).toString(), modLoc("block/building/block_floor_panel"))
                        .texture("surface", material.getTexture());

                builder.addModels(builder.partialState()
                                .with(PropertyHolder.FACING_NE, Direction.NORTH)
                                .with(PropertyHolder.DESTROYED, false),
                        ConfiguredModel.builder()
                                .modelFile(model)
                                .rotationY(getRotation(Direction.NORTH))
                                .build());

                builder.addModels(builder.partialState()
                                .with(PropertyHolder.FACING_NE, Direction.EAST)
                                .with(PropertyHolder.DESTROYED, false),
                        ConfiguredModel.builder()
                                .modelFile(model)
                                .rotationY(getRotation(Direction.EAST))
                                .uvLock(true)
                                .build());

                builder.addModels(builder.partialState()
                                .with(PropertyHolder.FACING_NE, Direction.NORTH)
                                .with(PropertyHolder.DESTROYED, true),
                        ConfiguredModel.builder()
                                .modelFile(modelDestroyed)
                                .rotationY(getRotation(Direction.NORTH))
                                .build());

                builder.addModels(builder.partialState()
                                .with(PropertyHolder.FACING_NE, Direction.EAST)
                                .with(PropertyHolder.DESTROYED, true),
                        ConfiguredModel.builder()
                                .modelFile(modelDestroyed)
                                .rotationY(getRotation(Direction.EAST))
                                .build());
            }
        }

        @Override
        public String getName() { return "r6mod:floor_panel_block_states"; }
    }

    private static int getRotation(Direction facing) { return (((int) facing.getHorizontalAngle()) + 180) % 360; }
}