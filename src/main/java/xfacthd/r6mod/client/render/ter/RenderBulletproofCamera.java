package xfacthd.r6mod.client.render.ter;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import xfacthd.r6mod.common.tileentities.gadgets.TileEntityBulletproofCamera;

import java.util.Random;

public class RenderBulletproofCamera extends TileEntityRenderer<TileEntityBulletproofCamera>
{
    private static final float SIZE = 0.02F;
    private final Random rand = new Random();

    public RenderBulletproofCamera(TileEntityRendererDispatcher dispatch) { super(dispatch); }

    @Override
    public void render(TileEntityBulletproofCamera te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
    {
        if (te.isActive())
        {
            float[] offsets = new float[]
                    {
                            (rand.nextFloat() % SIZE / 2F) - (SIZE / 4F),
                            (rand.nextFloat() % SIZE / 2F) - (SIZE / 4F),
                            (rand.nextFloat() % SIZE / 2F) - (SIZE / 4F),
                            (rand.nextFloat() % SIZE / 2F) - (SIZE / 4F)
                    };

            boolean friendly = te.isFriendly(Minecraft.getInstance().player);
            int r = friendly ? 0x00 : 0xFF;
            int g = friendly ? 0x9B : 0x00;
            int b = friendly ? 0xFF : 0x00;

            IVertexBuilder builder = buffer.getBuffer(RenderType.getLeash());

            matrix.push();

            matrix.translate(.5, .5, .5);
            if (te.getFacing() == Direction.UP) { matrix.rotate(Vector3f.XP.rotationDegrees(-90)); }
            else { matrix.rotate(Vector3f.YP.rotationDegrees(360 - te.getFacing().getHorizontalAngle())); }
            matrix.translate(-.5, -.5, -.5);

            float z = 1.51F / 16F;
            Matrix4f mat = matrix.getLast().getMatrix();
            builder.pos(mat, 0.5F - SIZE - offsets[0], 4.75F / 16F - SIZE - offsets[0], z).color(r, g, b, 0xFF).lightmap(240, 240).endVertex();
            builder.pos(mat, 0.5F + SIZE + offsets[1], 4.75F / 16F - SIZE - offsets[1], z).color(r, g, b, 0xFF).lightmap(240, 240).endVertex();
            builder.pos(mat, 0.5F + SIZE + offsets[2], 4.75F / 16F + SIZE + offsets[2], z).color(r, g, b, 0xFF).lightmap(240, 240).endVertex();
            builder.pos(mat, 0.5F - SIZE - offsets[3], 4.75F / 16F + SIZE + offsets[3], z).color(r, g, b, 0xFF).lightmap(240, 240).endVertex();

            matrix.pop();
        }
    }
}