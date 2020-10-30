package xfacthd.r6mod.common.util;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraftforge.registries.DataSerializerEntry;
import net.minecraftforge.registries.IForgeRegistry;
import xfacthd.r6mod.R6Mod;

public class R6DataSerializers
{
    private static IForgeRegistry<DataSerializerEntry> registry;

    public static final IDataSerializer<Long> LONG = new IDataSerializer<Long>() {
        public void write(PacketBuffer buf, Long value) { buf.writeLong(value); }

        public Long read(PacketBuffer buf) { return buf.readLong(); }

        public Long copyValue(Long value) { return value; }
    };



    public static void setRegistry(IForgeRegistry<DataSerializerEntry> reg) { registry = reg; }

    public static void registerSerializer(String name, IDataSerializer<?> serializer)
    {
        DataSerializerEntry entry = new DataSerializerEntry(serializer);
        entry.setRegistryName(R6Mod.MODID, name);
        registry.register(entry);
    }
}