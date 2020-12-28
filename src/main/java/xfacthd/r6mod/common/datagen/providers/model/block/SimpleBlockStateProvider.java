package xfacthd.r6mod.common.datagen.providers.model.block;

import net.minecraft.data.DataGenerator;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import xfacthd.r6mod.common.R6Content;
import xfacthd.r6mod.common.data.PropertyHolder;
import xfacthd.r6mod.common.data.blockdata.WallSegment;

public class SimpleBlockStateProvider extends R6BlockStateProvider
{
    public SimpleBlockStateProvider(DataGenerator gen, ExistingFileHelper fileHelper)
    {
        super(gen, fileHelper, "simple_block_states");
    }

    @Override
    protected void registerStatesAndModels()
    {
        //generateGunCraftingTableState();
        //generateGunCustomizerState();
        generateAmmoBoxState();
        //generateWeaponShelfState();
        generateMagFillerState();
        //generateBulletPressState();
        generateTeamSpawnState();
        //generateClassEquipperState();

        generateDropHatchState();
        generateFloorReinforcementState();
        generateSteelLadderState();
        generateCameraState();

        generateBreachChargeState();
        generateClaymoreState();
        generateBarbedWireState();
        generateDeployableShieldState();
        generateBulletproofCameraState();

        generateThermiteChargeState();
        //generateClusterChargeState();
        //generateJammerState();
        //generateArmorBagState();
        //generateBoobyTrapState();
        //generateAdsState();
        //generateShockWireState();
        generateWelcomeMatState();
        generateEvilEyeState();
        generateVolcanShieldState();
        generateBansheeState();
        //generateSuryaGateState();
    }



    private void generateGunCraftingTableState()
    {
        ModelFile model = models().cubeBottomTop(
                "block/misc/block_gun_crafting_table",
                modLoc("block/misc/block_gun_crafting_table_side"),
                modLoc("block/misc/block_gun_crafting_table_bottom"),
                modLoc("block/misc/block_gun_crafting_table_top")
        );

        simpleBlock(R6Content.blockGunCraftingTable, model);
        simpleBlockItem(R6Content.blockGunCraftingTable, model);
    }

    private void generateGunCustomizerState()
    {
        ModelFile model = models().cubeBottomTop(
                "block/misc/block_gun_customizer",
                modLoc("block/misc/block_gun_customizer_side"),
                modLoc("block/misc/block_gun_customizer_bottom"),
                modLoc("block/misc/block_gun_customizer_top")
        );

        simpleBlock(R6Content.blockGunCustomizer, model);
        simpleBlockItem(R6Content.blockGunCustomizer, model);
    }

    private void generateAmmoBoxState()
    {
        ModelFile model = models().getExistingFile(modLoc("block/misc/block_ammo_box"));
        VariantBlockStateBuilder builder = getVariantBuilder(R6Content.blockAmmoBox);

        for (Direction dir : PropertyHolder.FACING_NE.getAllowedValues())
        {
            builder.addModels(
                    builder.partialState().with(PropertyHolder.FACING_NE, dir),
                    ConfiguredModel.builder()
                            .modelFile(model)
                            .rotationY(getRotation(dir))
                            .build()
            );
        }

        simpleBlockItem(R6Content.blockAmmoBox, model);
    }

    private void generateWeaponShelfState()
    {
        ModelFile model = models().getExistingFile(modLoc("block/misc/block_weapon_shelf"));
        simpleHorizontalBlock(R6Content.blockWeaponShelf, model, true);
        simpleBlockItem(R6Content.blockWeaponShelf, model);
    }

    private void generateMagFillerState()
    {
        ModelFile model = models().getExistingFile(modLoc("block/misc/block_mag_filler"));
        simpleHorizontalBlock(R6Content.blockMagFiller, model, true);
        simpleBlockItem(R6Content.blockMagFiller, model);
    }

    private void generateBulletPressState()
    {
        //TODO: implement
    }

    private void generateTeamSpawnState()
    {
        ModelFile model = models().cubeAll(
                "block/misc/block_team_spawn",
                modLoc("block/misc/block_team_spawn")
        );
        simpleBlock(R6Content.blockTeamSpawn, model);
        simpleBlockItem(R6Content.blockTeamSpawn, model);
    }

    private void generateClassEquipperState()
    {
        ModelFile model = models().cubeBottomTop(
                "block/misc/block_class_equipper",
                modLoc("block/misc/block_class_equipper_side"),
                modLoc("block/misc/block_class_equipper_bottom"),
                modLoc("block/misc/block_class_equipper_top")
        );

        simpleBlock(R6Content.blockClassEquipper, model);
        simpleBlockItem(R6Content.blockClassEquipper, model);
    }



    private void generateDropHatchState()
    {
        ModelFile hatchBottomLeft = models().getExistingFile(modLoc("block/building/block_drop_hatch_ur"));
        ModelFile hatchBottomRight = models().getExistingFile(modLoc("block/building/block_drop_hatch_ul"));
        ModelFile hatchTopLeft = models().getExistingFile(modLoc("block/building/block_drop_hatch_dr"));
        ModelFile hatchTopRight = models().getExistingFile(modLoc("block/building/block_drop_hatch_dl"));
        VariantBlockStateBuilder builder = getVariantBuilder(R6Content.blockDropHatch);

        builder.addModels(
                builder.partialState().with(PropertyHolder.SQUARE_SEGMENT, WallSegment.BOTTOM_LEFT),
                ConfiguredModel.builder().modelFile(hatchBottomLeft).build()
        );

        builder.addModels(
                builder.partialState().with(PropertyHolder.SQUARE_SEGMENT, WallSegment.BOTTOM_RIGHT),
                ConfiguredModel.builder().modelFile(hatchBottomRight).build()
        );

        builder.addModels(
                builder.partialState().with(PropertyHolder.SQUARE_SEGMENT, WallSegment.TOP_LEFT),
                ConfiguredModel.builder().modelFile(hatchTopLeft).build()
        );
        builder.addModels(
                builder.partialState().with(PropertyHolder.SQUARE_SEGMENT, WallSegment.TOP_RIGHT),
                ConfiguredModel.builder().modelFile(hatchTopRight).build()
        );
    }

    private void generateFloorReinforcementState()
    {
        ModelFile floorBottomLeft = models().getExistingFile(modLoc("block/building/block_floor_reinforcement_bottom_left"));
        ModelFile floorBottomRight = models().getExistingFile(modLoc("block/building/block_floor_reinforcement_bottom_right"));
        ModelFile floorTopLeft = models().getExistingFile(modLoc("block/building/block_floor_reinforcement_top_left"));
        ModelFile floorTopRight = models().getExistingFile(modLoc("block/building/block_floor_reinforcement_top_right"));
        VariantBlockStateBuilder builder = getVariantBuilder(R6Content.blockFloorReinforcement);

        builder.addModels(
                builder.partialState().with(PropertyHolder.SQUARE_SEGMENT, WallSegment.BOTTOM_LEFT),
                ConfiguredModel.builder().modelFile(floorBottomLeft).build()
        );

        builder.addModels(
                builder.partialState().with(PropertyHolder.SQUARE_SEGMENT, WallSegment.BOTTOM_RIGHT),
                ConfiguredModel.builder().modelFile(floorBottomRight).build()
        );

        builder.addModels(
                builder.partialState().with(PropertyHolder.SQUARE_SEGMENT, WallSegment.TOP_LEFT),
                ConfiguredModel.builder().modelFile(floorTopLeft).build()
        );
        builder.addModels(
                builder.partialState().with(PropertyHolder.SQUARE_SEGMENT, WallSegment.TOP_RIGHT),
                ConfiguredModel.builder().modelFile(floorTopRight).build()
        );
    }

    private void generateSteelLadderState()
    {
        ModelFile ladderNone = models().getExistingFile(modLoc("block/building/block_steel_ladder_none"));
        ModelFile ladderTop = models().getExistingFile(modLoc("block/building/block_steel_ladder_t"));
        ModelFile ladderBottom = models().getExistingFile(modLoc("block/building/block_steel_ladder_b"));
        ModelFile ladderBoth = models().getExistingFile(modLoc("block/building/block_steel_ladder_tb"));
        VariantBlockStateBuilder builder = getVariantBuilder(R6Content.blockSteelLadder);

        for (Direction dir : PropertyHolder.FACING_HOR.getAllowedValues())
        {
            builder.addModels(
                    builder.partialState()
                            .with(PropertyHolder.FACING_HOR, dir)
                            .with(PropertyHolder.UP, false)
                            .with(PropertyHolder.DOWN, false),
                    ConfiguredModel.builder()
                            .modelFile(ladderBoth)
                            .rotationY(getRotation(dir))
                            .build()
            );

            builder.addModels(
                    builder.partialState()
                            .with(PropertyHolder.FACING_HOR, dir)
                            .with(PropertyHolder.UP, true)
                            .with(PropertyHolder.DOWN, false),
                    ConfiguredModel.builder()
                            .modelFile(ladderBottom)
                            .rotationY(getRotation(dir))
                            .build()
            );

            builder.addModels(
                    builder.partialState()
                            .with(PropertyHolder.FACING_HOR, dir)
                            .with(PropertyHolder.UP, false)
                            .with(PropertyHolder.DOWN, true),
                    ConfiguredModel.builder()
                            .modelFile(ladderTop)
                            .rotationY(getRotation(dir))
                            .build()
            );

            builder.addModels(
                    builder.partialState()
                            .with(PropertyHolder.FACING_HOR, dir)
                            .with(PropertyHolder.UP, true)
                            .with(PropertyHolder.DOWN, true),
                    ConfiguredModel.builder()
                            .modelFile(ladderNone)
                            .rotationY(getRotation(dir))
                            .build()
            );
        }

        simpleBlockItem(R6Content.blockSteelLadder, ladderNone);
    }

    private void generateCameraState()
    {
        ModelFile camera = models().getExistingFile(modLoc("block/building/block_camera"));
        ModelFile cameraDestroyed = models().getExistingFile(modLoc("block/building/block_camera_destroyed"));
        VariantBlockStateBuilder builder = getVariantBuilder(R6Content.blockCamera);

        builder.addModels(
                builder.partialState().with(PropertyHolder.DESTROYED, false),
                ConfiguredModel.builder().modelFile(camera).build()
        );

        builder.addModels(
                builder.partialState().with(PropertyHolder.DESTROYED, true),
                ConfiguredModel.builder().modelFile(cameraDestroyed).build()
        );

        simpleBlockItem(R6Content.blockCamera, camera);
    }



    private void generateBreachChargeState()
    {
        ModelFile breachCharge = models().getExistingFile(modLoc("block/gadget/block_breach_charge"));
        VariantBlockStateBuilder builder = getVariantBuilder(R6Content.blockBreachCharge);

        for (Direction dir : PropertyHolder.FACING_NOT_UP.getAllowedValues())
        {
            builder.addModels(
                    builder.partialState().with(PropertyHolder.FACING_NOT_UP, dir),
                    ConfiguredModel.builder()
                            .modelFile(breachCharge)
                            .rotationX(dir == Direction.DOWN ? 90 : 0)
                            .rotationY(dir != Direction.DOWN ? getRotation(dir) : 0)
                            .build()
            );
        }
    }

    private void generateClaymoreState()
    {
        ModelFile model = models().getExistingFile(modLoc("block/gadget/block_claymore"));
        simpleHorizontalBlock(R6Content.blockClaymore, model, true);
        simpleBlockItem(R6Content.blockClaymore, model);
    }

    private void generateBarbedWireState()
    {
        ModelFile barbedWire = models().getExistingFile(modLoc("block/gadget/block_barbed_wire"));
        ModelFile barbedWireElectro = models().getExistingFile(modLoc("block/gadget/block_barbed_wire_electro"));
        VariantBlockStateBuilder builder = getVariantBuilder(R6Content.blockBarbedWire);

        for (WallSegment segment : PropertyHolder.SQUARE_SEGMENT.getAllowedValues())
        {
            builder.addModels(
                    builder.partialState()
                            .with(PropertyHolder.SQUARE_SEGMENT, segment)
                            .with(PropertyHolder.ELECTRIFIED, false),
                    ConfiguredModel.builder()
                            .modelFile(barbedWire)
                            .build()
            );

            builder.addModels(
                    builder.partialState()
                            .with(PropertyHolder.SQUARE_SEGMENT, segment)
                            .with(PropertyHolder.ELECTRIFIED, true),
                    ConfiguredModel.builder()
                            .modelFile(segment == WallSegment.BOTTOM_LEFT ? barbedWireElectro : barbedWire)
                            .build()
            );
        }
    }

    private void generateDeployableShieldState()
    {
        ModelFile model = models().getExistingFile(modLoc("block/gadget/block_deployable_shield"));
        simpleHorizontalBlock(R6Content.blockDeployableShield, model);
        simpleBlockItem(R6Content.blockDeployableShield, model);
    }

    private void generateBulletproofCameraState()
    {
        ModelFile camera = models().getExistingFile(modLoc("block/gadget/block_bulletproof_camera"));
        VariantBlockStateBuilder builder = getVariantBuilder(R6Content.blockBulletProofCamera);

        for (Direction dir : PropertyHolder.FACING_NOT_DOWN.getAllowedValues())
        {
            builder.addModels(
                    builder.partialState().with(PropertyHolder.FACING_NOT_DOWN, dir),
                    ConfiguredModel.builder()
                            .modelFile(camera)
                            .rotationX(dir != Direction.UP ? -90 : 0)
                            .rotationY(dir != Direction.UP ? getRotation(dir) - 180 : 0)
                            .build()
            );
        }
    }



    private void generateThermiteChargeState()
    {
        ModelFile thermiteCharge = models().getExistingFile(modLoc("block/gadget/block_thermite_charge"));
        VariantBlockStateBuilder builder = getVariantBuilder(R6Content.blockThermiteCharge);

        for (Direction dir : PropertyHolder.FACING_NOT_UP.getAllowedValues())
        {
            builder.addModels(
                    builder.partialState().with(PropertyHolder.FACING_NOT_UP, dir),
                    ConfiguredModel.builder()
                            .modelFile(thermiteCharge)
                            .rotationX(dir == Direction.DOWN ? 90 : 0)
                            .rotationY(dir != Direction.DOWN ? getRotation(dir) : 0)
                            .build()
            );
        }
    }

    private void generateClusterChargeState()
    {
        ModelFile charge = models().getExistingFile(modLoc("block/gadget/block_cluster_charge"));
        ModelFile chargeActive = models().getExistingFile(modLoc("block/gadget/block_cluster_charge_activated"));
        VariantBlockStateBuilder builder = getVariantBuilder(R6Content.blockClusterCharge);

        for (Direction dir : PropertyHolder.FACING_NOT_UP.getAllowedValues())
        {
            int rotX = dir == Direction.NORTH || dir == Direction.EAST ? -90 : 90;

            builder.addModels(
                    builder.partialState()
                            .with(PropertyHolder.FACING_NOT_UP, dir)
                            .with(PropertyHolder.ACTIVE, false),
                    ConfiguredModel.builder()
                            .modelFile(charge)
                            .rotationX(dir != Direction.DOWN ? rotX : 0)
                            .rotationY(dir.getAxis() == Direction.Axis.X ? 90 : 0)
                            .build()
            );

            builder.addModels(
                    builder.partialState()
                            .with(PropertyHolder.FACING_NOT_UP, dir)
                            .with(PropertyHolder.ACTIVE, true),
                    ConfiguredModel.builder()
                            .modelFile(chargeActive)
                            .rotationX(dir != Direction.DOWN ? rotX : 0)
                            .rotationY(dir.getAxis() == Direction.Axis.X ? 90 : 0)
                            .build()
            );
        }

        simpleBlockItem(R6Content.blockClusterCharge, charge);
    }

    private void generateJammerState()
    {
        ModelFile model = models().getExistingFile(modLoc("block/gadget/block_jammer"));
        simpleHorizontalBlock(R6Content.blockJammer, model);
        simpleBlockItem(R6Content.blockJammer, model);
    }

    private void generateArmorBagState()
    {
        ModelFile ammoBox = models().getExistingFile(modLoc("block/gadget/block_armor_bag"));
        VariantBlockStateBuilder builder = getVariantBuilder(R6Content.blockArmorBag);

        for (Direction dir : PropertyHolder.FACING_NE.getAllowedValues())
        {
            builder.addModels(
                    builder.partialState().with(PropertyHolder.FACING_NE, dir),
                    ConfiguredModel.builder()
                            .modelFile(ammoBox)
                            .rotationY(getRotation(dir))
                            .build()
            );
        }
    }

    private void generateBoobyTrapState()
    {
        //TODO: implement
    }

    private void generateAdsState()
    {
        //TODO: implement
    }

    private void generateShockWireState()
    {
        ModelFile shockWire = models().getExistingFile(modLoc("block/gadget/block_shock_wire"));
        ModelFile shockWireCenter = models().getExistingFile(modLoc("block/gadget/block_shock_wire_centered"));
        VariantBlockStateBuilder builder = getVariantBuilder(R6Content.blockShockWire);

        for (Direction dir : PropertyHolder.FACING_NOT_UP.getAllowedValues())
        {
            builder.addModels(
                    builder.partialState().with(PropertyHolder.FACING_NOT_UP, dir),
                    ConfiguredModel.builder()
                            .modelFile(dir == Direction.DOWN ? shockWireCenter : shockWire)
                            .rotationY(getRotation(dir))
                            .build()
            );
        }

        simpleBlockItem(R6Content.blockShockWire, shockWireCenter);
    }

    private void generateWelcomeMatState()
    {
        ModelFile mat = models().getExistingFile(modLoc("block/gadget/block_welcome_mat"));
        ModelFile matActive = models().getExistingFile(modLoc("block/gadget/block_welcome_mat_activated"));
        VariantBlockStateBuilder builder = getVariantBuilder(R6Content.blockWelcomeMat);

        for (Direction dir : PropertyHolder.FACING_NE.getAllowedValues())
        {
            builder.addModels(
                    builder.partialState()
                            .with(PropertyHolder.FACING_NE, dir)
                            .with(PropertyHolder.TRIGGERED, false),
                    ConfiguredModel.builder()
                            .modelFile(mat)
                            .rotationY(getRotation(dir))
                            .build()
            );

            builder.addModels(
                    builder.partialState()
                            .with(PropertyHolder.FACING_NE, dir)
                            .with(PropertyHolder.TRIGGERED, true),
                    ConfiguredModel.builder()
                            .modelFile(matActive)
                            .rotationY(getRotation(dir))
                            .build()
            );
        }
    }

    private void generateEvilEyeState()
    {
        ModelFile evilEye = models().getExistingFile(modLoc("block/gadget/block_evil_eye"));
        VariantBlockStateBuilder builder = getVariantBuilder(R6Content.blockEvilEye);

        for (Direction dir : PropertyHolder.FACING_NOT_DOWN.getAllowedValues())
        {
            int rotX = dir == Direction.NORTH || dir == Direction.EAST ? 90 : -90;

            builder.addModels(
                    builder.partialState().with(PropertyHolder.FACING_NOT_DOWN, dir),
                    ConfiguredModel.builder()
                            .modelFile(evilEye)
                            .rotationX(dir != Direction.UP ? rotX : 0)
                            .rotationY(dir.getAxis() == Direction.Axis.X ? 90 : 0)
                            .build()
            );
        }
    }

    private void generateVolcanShieldState()
    {
        ModelFile model = models().getExistingFile(modLoc("block/gadget/block_volcan_shield"));
        simpleHorizontalBlock(R6Content.blockVolcanShield, model);
        simpleBlockItem(R6Content.blockVolcanShield, model);
    }

    private void generateBansheeState()
    {
        ModelFile model = models().getExistingFile(modLoc("block/gadget/block_banshee"));
        VariantBlockStateBuilder builder = getVariantBuilder(R6Content.blockBanshee);

        for (Direction dir : PropertyHolder.FACING_NOT_DOWN.getAllowedValues())
        {
            int rotX = 0;
            if (dir != Direction.UP)
            {
                rotX = dir.getAxisDirection() == Direction.AxisDirection.POSITIVE ? -90 : 90;
            }
            builder.addModels(
                    builder.partialState().with(PropertyHolder.FACING_NOT_DOWN, dir),
                    ConfiguredModel.builder()
                            .modelFile(model)
                            .rotationX(rotX)
                            .rotationY(dir.getAxis() == Direction.Axis.X ? -90 : 0)
                            .build());
        }

        simpleBlockItem(R6Content.blockBanshee, model);
    }

    private void generateSuryaGateState()
    {
        //TODO: implement
    }
}