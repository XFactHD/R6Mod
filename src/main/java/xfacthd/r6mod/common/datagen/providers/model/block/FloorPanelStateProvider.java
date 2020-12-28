package xfacthd.r6mod.common.datagen.providers.model.block;

import net.minecraft.data.DataGenerator;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import xfacthd.r6mod.common.R6Content;
import xfacthd.r6mod.common.data.PropertyHolder;
import xfacthd.r6mod.common.data.blockdata.WallMaterial;

public final class FloorPanelStateProvider extends R6BlockStateProvider
{
    public FloorPanelStateProvider(DataGenerator gen, ExistingFileHelper fileHelper)
    {
        super(gen, fileHelper, "floor_panel_block_states");
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

            itemModels().getBuilder("block_floor_panel_" + material.getString()).parent(model);
        }
    }
}