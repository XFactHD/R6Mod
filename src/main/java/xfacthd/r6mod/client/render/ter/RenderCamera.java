package xfacthd.r6mod.client.render.ter;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.PlayerEntity;
import xfacthd.r6mod.common.tileentities.building.TileEntityCamera;

import java.util.Random;

public class RenderCamera extends TileEntityRenderer<TileEntityCamera>
{
    private static final float SIZE = 0.03F;
    private final Random rand = new Random();

    public RenderCamera(TileEntityRendererDispatcher dispatcher) { super(dispatcher); }

    @Override
    public void render(TileEntityCamera te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
    {
        if (!te.isDestroyed() && te.isActive())
        {
            PlayerEntity player = Minecraft.getInstance().player;
            if (player == null) { return; }

            float[] offsets = new float[]
                    {
                            (rand.nextFloat() % SIZE / 2F) - (SIZE / 4F),
                            (rand.nextFloat() % SIZE / 2F) - (SIZE / 4F),
                            (rand.nextFloat() % SIZE / 2F) - (SIZE / 4F),
                            (rand.nextFloat() % SIZE / 2F) - (SIZE / 4F)
                    };

            boolean friendly = te.isFriendly(player);
            int r = friendly ? 0x00 : 0xFF;
            int g = friendly ? 0x9B : 0x00;
            int b = friendly ? 0xFF : 0x00;

            IVertexBuilder builder = buffer.getBuffer(RenderType.getLeash());

            matrix.push();

            float y = 12.9F/16F;

            matrix.translate(.5, y, .5);
            matrix.rotate(Vector3f.YP.rotationDegrees(180 - player.getRotationYawHead()));
            matrix.rotate(Vector3f.XP.rotationDegrees(90 - player.getPitch(partialTicks)));
            matrix.translate(-.5, -y, -.5);

            Matrix4f mat = matrix.getLast().getMatrix();
            builder.pos(mat, .5F - SIZE - offsets[0], y, .5F - SIZE - offsets[0]).color(r, g, b, 0xFF).lightmap(240, 240).endVertex();
            builder.pos(mat, .5F + SIZE + offsets[1], y, .5F - SIZE - offsets[1]).color(r, g, b, 0xFF).lightmap(240, 240).endVertex();
            builder.pos(mat, .5F + SIZE + offsets[2], y, .5F + SIZE + offsets[2]).color(r, g, b, 0xFF).lightmap(240, 240).endVertex();
            builder.pos(mat, .5F - SIZE - offsets[3], y, .5F + SIZE + offsets[3]).color(r, g, b, 0xFF).lightmap(240, 240).endVertex();

            matrix.pop();
        }
    }
}