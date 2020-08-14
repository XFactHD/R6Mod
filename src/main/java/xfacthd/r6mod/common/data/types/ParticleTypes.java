package xfacthd.r6mod.common.data.types;

import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.registries.IForgeRegistry;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.common.data.particledata.*;

public class ParticleTypes
{
    private static IForgeRegistry<ParticleType<?>> typeRegistry;

    public static ParticleType<ParticleDataYokaiBlast>   particleTypeYokaiBlast;
    public static ParticleType<ParticleDataEvilEyeLaser> particleTypeEvilEyeLaser;

    public static void setRegistry(IForgeRegistry<ParticleType<?>> registry) { typeRegistry = registry; }

    public static <T extends IParticleData> ParticleType<T> create(String name, boolean showAlways, IParticleData.IDeserializer<T> deserializer)
    {
        ParticleType<T> type = new ParticleType<>(showAlways, deserializer);
        type.setRegistryName(R6Mod.MODID, name);
        typeRegistry.register(type);
        return type;
    }
}