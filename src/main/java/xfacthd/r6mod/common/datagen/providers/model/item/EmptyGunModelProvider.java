package xfacthd.r6mod.common.datagen.providers.model.item;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGun;

public final class EmptyGunModelProvider extends R6ItemModelProvider
{
    public EmptyGunModelProvider(DataGenerator gen, ExistingFileHelper fileHelper)
    {
        super(gen, fileHelper, "empty_gun_models");
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
}