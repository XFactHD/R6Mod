package xfacthd.r6mod.common.datagen.providers.model.block;

import net.minecraft.data.DataGenerator;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import xfacthd.r6mod.common.R6Content;
import xfacthd.r6mod.common.data.PropertyHolder;
import xfacthd.r6mod.common.data.blockdata.WallMaterial;

public final class WallStateProvider extends R6BlockStateProvider
{
    public static WallStateProvider INSTANCE;

    public WallStateProvider(DataGenerator gen, ExistingFileHelper fileHelper)
    {
        super(gen, fileHelper, "wall_block_states");
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

            itemModels().getBuilder("block_wall_soft_" + material.getString()).parent(wallSoft);

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

            itemModels().getBuilder("block_wall_barred_" + material.getString()).parent(wallBarred);
        }
    }
}