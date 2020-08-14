package xfacthd.r6mod.client.util.render;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.*;
import net.minecraft.util.math.Vec3d;

public class WorldRenderHelper
{
    public static void drawLine(IVertexBuilder builder, Matrix4f matrix, Vec3d vecA, Vec3d vecB, int r, int g, int b, int a)
    {
        builder.pos(matrix, (float)vecA.x, (float)vecA.y, (float)vecA.z).color(r, g, b, a).endVertex();
        builder.pos(matrix, (float)vecB.x, (float)vecB.y, (float)vecB.z).color(r, g, b, a).endVertex();
    }
}