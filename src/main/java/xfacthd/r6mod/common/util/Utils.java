package xfacthd.r6mod.common.util;

import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

public class Utils
{
    public static boolean isBitSet(byte mask, int bit) { return ((mask >> bit) & 1) == 1; }

    public static byte setBit(byte mask, int bit) { return (byte) (mask | (1 << bit)); }

    public static byte clearBit(byte mask, int bit) { return (byte) (mask & ~(1 << bit)); }

    public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape)
    {
        if (from.getAxis() == Direction.Axis.Y || to.getAxis() == Direction.Axis.Y) { throw new IllegalArgumentException("Invalid Direction!"); }
        if (from == to) { return shape; }

        VoxelShape[] buffer = new VoxelShape[] { shape, VoxelShapes.empty() };

        int times = (to.getHorizontalIndex() - from.getHorizontalIndex() + 4) % 4;
        for (int i = 0; i < times; i++)
        {
            buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.or(
                    buffer[1],
                    VoxelShapes.create(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)
            ));
            buffer[0] = buffer[1];
            buffer[1] = VoxelShapes.empty();
        }

        return buffer[0];
    }

    public static <T> boolean arrayContains(T[] array, T toFind)
    {
        for (T val : array)
        {
            if (val == toFind) { return true; }
        }
        return false;
    }
}