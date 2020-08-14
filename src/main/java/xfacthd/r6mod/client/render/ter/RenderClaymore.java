package xfacthd.r6mod.client.render.ter;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
import org.apache.commons.lang3.tuple.Triple;
import xfacthd.r6mod.common.tileentities.gadgets.TileEntityClaymore;

import java.util.HashMap;

public class RenderClaymore extends TileEntityRenderer<TileEntityClaymore>
{
    private static final double MAX_RENDER_DISTANCE = 500;
    private static final HashMap<Direction, Vector3d> ORIGIN_VECTORS = new HashMap<>();

    static
    {
        ORIGIN_VECTORS.put(Direction.NORTH, new Vector3d(0.5D, 3.635D/16D, 8D/16D));
        ORIGIN_VECTORS.put(Direction.EAST,  new Vector3d(8D/16D, 3.635D/16D, 0.5D));
        ORIGIN_VECTORS.put(Direction.SOUTH, new Vector3d(0.5D, 3.635D/16D, 8D/16D));
        ORIGIN_VECTORS.put(Direction.WEST,  new Vector3d(8D/16D, 3.635D/16D, 0.5D));
    }

    public RenderClaymore(TileEntityRendererDispatcher dispatch) { super(dispatch); }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void render(TileEntityClaymore te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
    {
        if (te.isActive())
        {
            Vector3d posVec = ORIGIN_VECTORS.get(te.getFacing());
            Triple<Vector3d, Vector3d, Vector3d> laserVecs = te.getLasers();
            if (posVec == null || laserVecs == null) { return; }

            matrix.push();
            matrix.translate(posVec.x, posVec.y, posVec.z);

            IVertexBuilder builder = buffer.getBuffer(RenderType.LINES);

            Vector3d player = Minecraft.getInstance().player.getPositionVec();
            double distance = te.getPos().distanceSq(player.x, player.y, player.z, true);
            int alpha = (int)(127.0 * (1.0 - Math.min(distance, MAX_RENDER_DISTANCE) / MAX_RENDER_DISTANCE));

            builder.pos(matrix.getLast().getMatrix(), 0, 0, 0).color(255, 0, 0, alpha).endVertex();
            builder.pos(matrix.getLast().getMatrix(), (float)laserVecs.getLeft().x, 0, (float)laserVecs.getLeft().z).color(255, 0, 0, alpha).endVertex();

            builder.pos(matrix.getLast().getMatrix(), 0, 0, 0).color(255, 0, 0, alpha).endVertex();
            builder.pos(matrix.getLast().getMatrix(), (float)laserVecs.getMiddle().x, 0, (float)laserVecs.getMiddle().z).color(255, 0, 0, alpha).endVertex();

            builder.pos(matrix.getLast().getMatrix(), 0, 0, 0).color(255, 0, 0, alpha).endVertex();
            builder.pos(matrix.getLast().getMatrix(), (float)laserVecs.getRight().x, 0, (float)laserVecs.getRight().z).color(255, 0, 0, alpha).endVertex();

            matrix.pop();
        }
    }
}