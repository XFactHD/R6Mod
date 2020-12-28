package xfacthd.r6mod.common.datagen.providers.model.block;

import net.minecraft.data.DataGenerator;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import xfacthd.r6mod.common.R6Content;
import xfacthd.r6mod.common.data.PropertyHolder;
import xfacthd.r6mod.common.data.blockdata.WallMaterial;

public final class BlackMirrorStateProvider extends R6BlockStateProvider
{
    public BlackMirrorStateProvider(DataGenerator gen, ExistingFileHelper fileHelper)
    {
        super(gen, fileHelper, "black_mirror_block_states");
    }

    @Override
    protected void registerStatesAndModels()
    {
        ModelFile windowLeft = models().getExistingFile(modLoc("block/gadget/block_black_mirror_glass_left"));
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
}