package xfacthd.r6mod.common.datagen.providers.model.block;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.common.data.PropertyHolder;

public abstract class R6BlockStateProvider extends BlockStateProvider
{
    private final String name;

    protected R6BlockStateProvider(DataGenerator gen, ExistingFileHelper fileHelper, String name)
    {
        super(gen, R6Mod.MODID, fileHelper);
        this.name = name;
    }

    @Override
    public final String getName() { return R6Mod.MODID + ":" + name; }

    protected void simpleHorizontalBlock(Block block, ModelFile model) { simpleHorizontalBlock(block, model, false); }

    protected void simpleHorizontalBlock(Block block, ModelFile model, boolean halfRotated)
    {
        VariantBlockStateBuilder builder = getVariantBuilder(block);
        for (Direction dir : PropertyHolder.FACING_HOR.getAllowedValues())
        {
            int rotY = getRotation(dir);
            if (halfRotated) { rotY = (rotY + 180) % 360; }

            builder.addModels(
                    builder.partialState().with(PropertyHolder.FACING_HOR, dir),
                    ConfiguredModel.builder()
                            .modelFile(model)
                            .rotationY(rotY)
                            .build()
            );
        }
    }

    protected static int getRotation(Direction facing) { return (((int) facing.getHorizontalAngle()) + 180) % 360; }
}