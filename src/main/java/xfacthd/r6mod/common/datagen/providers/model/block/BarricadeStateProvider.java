package xfacthd.r6mod.common.datagen.providers.model.block;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import xfacthd.r6mod.common.R6Content;
import xfacthd.r6mod.common.data.PropertyHolder;

public final class BarricadeStateProvider extends R6BlockStateProvider
{
    public BarricadeStateProvider(DataGenerator gen, ExistingFileHelper fileHelper)
    {
        super(gen, fileHelper, "barricade_block_states");
    }

    @Override
    protected void registerStatesAndModels()
    {
        ModelFile glassPane = models().getExistingFile(modLoc("block/building/block_glass_pane"));

        //Wood barricade
        generateBarricadeStates(
                R6Content.blockBarricade,
                glassPane,
                models().getExistingFile(modLoc("block/building/block_barricade_top")),
                models().getExistingFile(modLoc("block/building/block_barricade_bottom")),
                models().getExistingFile(modLoc("block/building/block_barricade_top_window")),
                models().getExistingFile(modLoc("block/building/block_barricade_bottom_window")),
                models().getExistingFile(modLoc("block/building/block_barricade_bottom_door")),
                models().getExistingFile(modLoc("block/building/block_barricade_left_top")),
                models().getExistingFile(modLoc("block/building/block_barricade_right_top")),
                models().getExistingFile(modLoc("block/building/block_barricade_center_top")),
                models().getExistingFile(modLoc("block/building/block_barricade_left_bottom")),
                models().getExistingFile(modLoc("block/building/block_barricade_right_bottom")),
                models().getExistingFile(modLoc("block/building/block_barricade_center_bottom")),
                models().getExistingFile(modLoc("block/building/block_barricade_left_bottom_door")),
                models().getExistingFile(modLoc("block/building/block_barricade_right_bottom_door")),
                models().getExistingFile(modLoc("block/building/block_barricade_center_bottom_door")),
                models().getExistingFile(modLoc("block/building/block_barricade_left_top_window")),
                models().getExistingFile(modLoc("block/building/block_barricade_right_top_window")),
                models().getExistingFile(modLoc("block/building/block_barricade_center_top_window")),
                models().getExistingFile(modLoc("block/building/block_barricade_left_bottom_window")),
                models().getExistingFile(modLoc("block/building/block_barricade_right_bottom_window")),
                models().getExistingFile(modLoc("block/building/block_barricade_center_bottom_window"))
        );

        //Tough barricade
        generateBarricadeStates(
                R6Content.blockToughBarricade,
                glassPane,
                models().getExistingFile(modLoc("block/building/block_tough_barricade_top")),
                models().getExistingFile(modLoc("block/building/block_tough_barricade_bottom")),
                models().getExistingFile(modLoc("block/building/block_tough_barricade_top_window")),
                models().getExistingFile(modLoc("block/building/block_tough_barricade_bottom_window")),
                models().getExistingFile(modLoc("block/building/block_tough_barricade_bottom_door")),
                models().getExistingFile(modLoc("block/building/block_tough_barricade_left_top")),
                models().getExistingFile(modLoc("block/building/block_tough_barricade_right_top")),
                models().getExistingFile(modLoc("block/building/block_tough_barricade_center_top")),
                models().getExistingFile(modLoc("block/building/block_tough_barricade_left_bottom")),
                models().getExistingFile(modLoc("block/building/block_tough_barricade_right_bottom")),
                models().getExistingFile(modLoc("block/building/block_tough_barricade_center_bottom")),
                models().getExistingFile(modLoc("block/building/block_tough_barricade_left_bottom_door")),
                models().getExistingFile(modLoc("block/building/block_tough_barricade_right_bottom_door")),
                models().getExistingFile(modLoc("block/building/block_tough_barricade_center_bottom_door")),
                models().getExistingFile(modLoc("block/building/block_tough_barricade_left_top_window")),
                models().getExistingFile(modLoc("block/building/block_tough_barricade_right_top_window")),
                models().getExistingFile(modLoc("block/building/block_tough_barricade_center_top_window")),
                models().getExistingFile(modLoc("block/building/block_tough_barricade_left_bottom_window")),
                models().getExistingFile(modLoc("block/building/block_tough_barricade_right_bottom_window")),
                models().getExistingFile(modLoc("block/building/block_tough_barricade_center_bottom_window"))
        );
    }

    private void generateBarricadeStates(Block block, ModelFile modelGlass, ModelFile modelTop, ModelFile modelBottom, ModelFile modelTopWindow, ModelFile modelBottomWindow, ModelFile modelBottomDoor, ModelFile modelTopLeft, ModelFile modelTopRight, ModelFile modelTopCenter, ModelFile modelBottomLeft, ModelFile modelBottomRight, ModelFile modelBottomCenter, ModelFile modelBottomDoorLeft, ModelFile modelBottomDoorRight, ModelFile modelBottomDoorCenter, ModelFile modelTopWindowLeft, ModelFile modelTopWindowRight, ModelFile modelTopWindowCenter, ModelFile modelBottomWindowLeft, ModelFile modelBottomWindowRight, ModelFile modelBottomWindowCenter)
    {
        MultiPartBlockStateBuilder builder = getMultipartBuilder(block);

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
}