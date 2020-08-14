package xfacthd.r6mod.client.util.render;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;

public class WorldRenderHelper
{
    public static void drawLine(IVertexBuilder builder, Matrix4f matrix, Vector3d vecA, Vector3d vecB, int r, int g, int b, int a)
    {
        builder.pos(matrix, (float)vecA.x, (float)vecA.y, (float)vecA.z).color(r, g, b, a).endVertex();
        builder.pos(matrix, (float)vecB.x, (float)vecB.y, (float)vecB.z).color(r, g, b, a).endVertex();
    }
}