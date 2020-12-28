package xfacthd.r6mod.common.datagen.providers.model.item;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import xfacthd.r6mod.R6Mod;

public abstract class R6ItemModelProvider extends ItemModelProvider
{
    private final String name;

    protected R6ItemModelProvider(DataGenerator generator, ExistingFileHelper fileHelper, String name)
    {
        super(generator, R6Mod.MODID, fileHelper);
        this.name = name;
    }

    @Override
    public final String getName() { return R6Mod.MODID + ":" + name; }
}