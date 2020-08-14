package xfacthd.r6mod.common.util;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.IDataSerializer;

public class R6DataSerializers
{
    public static final IDataSerializer<Long> LONG = new IDataSerializer<Long>() {
        public void write(PacketBuffer buf, Long value) { buf.writeLong(value); }

        public Long read(PacketBuffer buf) { return buf.readLong(); }

        public Long copyValue(Long value) { return value; }
    };
}