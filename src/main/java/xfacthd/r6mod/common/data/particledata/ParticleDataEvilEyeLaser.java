package xfacthd.r6mod.common.data.particledata;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.*;
import xfacthd.r6mod.common.data.types.ParticleTypes;

import java.util.Locale;

public class ParticleDataEvilEyeLaser implements IParticleData
{
    public static final Deserializer DESERIALIZER = new Deserializer();

    private final float yaw;
    private final float pitch;

    public ParticleDataEvilEyeLaser(float yaw, float pitch)
    {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public ParticleType<?> getType() { return ParticleTypes.particleTypeEvilEyeLaser; }

    @Override
    public void write(PacketBuffer buffer)
    {
        buffer.writeFloat(yaw);
        buffer.writeFloat(pitch);
    }

    @Override
    public String getParameters() { return String.format(Locale.ROOT, "%s %.2f %.2f", getType().getRegistryName(), yaw, pitch); }

    public float getYaw() { return yaw; }

    public float getPitch() { return pitch; }

    public static class Deserializer implements IDeserializer<ParticleDataEvilEyeLaser>
    {
        @Override
        public ParticleDataEvilEyeLaser deserialize(ParticleType<ParticleDataEvilEyeLaser> type, StringReader reader) throws CommandSyntaxException
        {
            reader.expect(' ');
            float yaw = reader.readFloat();
            reader.expect(' ');
            float pitch = reader.readFloat();
            return new ParticleDataEvilEyeLaser(yaw, pitch);
        }

        @Override
        public ParticleDataEvilEyeLaser read(ParticleType<ParticleDataEvilEyeLaser> type, PacketBuffer buffer)
        {
            float yaw = buffer.readFloat();
            float pitch = buffer.readFloat();
            return new ParticleDataEvilEyeLaser(yaw, pitch);
        }
    }
}