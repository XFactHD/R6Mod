package xfacthd.r6mod.common.data.types;

import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.IForgeRegistry;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.common.container.*;

public class ContainerTypes
{
    private static IForgeRegistry<ContainerType<?>> typeRegistry;

    public static ContainerType<ContainerMagFiller> containerTypeMagFiller;
    public static ContainerType<ContainerCamera> containerTypeCamera;
    public static ContainerType<ContainerTeamSpawn> containerTypeTeamSpawn;

    public static void setRegistry(IForgeRegistry<ContainerType<?>> registry) { typeRegistry = registry; }

    public static<T extends Container> ContainerType<T> create(String name, IContainerFactory<T> factory)
    {
        ContainerType<T> type = IForgeContainerType.create(factory);
        type.setRegistryName(R6Mod.MODID, name);
        typeRegistry.register(type);
        return type;
    }
}