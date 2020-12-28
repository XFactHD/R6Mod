package xfacthd.r6mod.common.datagen.providers.model.block;

import net.minecraft.data.DataGenerator;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import xfacthd.r6mod.R6Mod;

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

    protected static int getRotation(Direction facing) { return (((int) facing.getHorizontalAngle()) + 180) % 360; }
}