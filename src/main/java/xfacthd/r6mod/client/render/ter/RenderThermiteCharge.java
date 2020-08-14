package xfacthd.r6mod.client.render.ter;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import xfacthd.r6mod.common.tileentities.gadgets.TileEntityThermiteCharge;

import java.util.Random;

/**
 * If the exothermic charge was activated, this draws two quads, each traveling along a path that
 * is 396 pixels long in the models original texture
 */
public class RenderThermiteCharge extends TileEntityRenderer<TileEntityThermiteCharge>
{
    private static final Random RAND = new Random();

    private static final float OFF_Z = 1.01F;                 //INFO: Distance to back side of the model
    private static final float POS_Z = (16F - OFF_Z) / 16F;   //INFO: Actual z position
    private static final float SIZE = 0.02F;                  //INFO: Half size of the quad
    private static final float Y_OFF = 13F/256F;              //INFO: Base offset from the top (13 of 256 pixels)
    private static final float HEIGHT = 230F/256F;            //INFO: Height of the y path (230 of 256 pixels)
    private static final float Y_PROG_OFF = 71F/396F;         //INFO: Offset from min and max progress for y to change (71 of 396 pixels)
    private static final float WIDTH = 83F/256F;              //INFO: Width of the x path (83 of 256 pixels)
    private static final float X_PROG_LOW = 83F/396F;         //INFO: Range to min progress within which x changes (83 of 396 pixels)
    private static final float X_PROG_HIGH = 1F - X_PROG_LOW; //INFO: Range to max progress within which x changes

    public RenderThermiteCharge(TileEntityRendererDispatcher dispatch) { super(dispatch); }

    @Override
    public void render(TileEntityThermiteCharge te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
    {
        if (te.isActive())
        {
            IVertexBuilder builder = buffer.getBuffer(RenderType.getLeash());

            float max = te.getBurnDuration();
            float curr = te.getProgress();
            float progress = Math.min((curr + partialTicks) / max, 1F);

            float xMult = progress <= X_PROG_LOW ? (progress / X_PROG_LOW) : (progress >= X_PROG_HIGH ? ((1F - progress) / X_PROG_LOW) : 1F);
            float xOff = WIDTH * xMult;
            float yMult = (Math.min(Math.max(progress, Y_PROG_OFF), 1F - Y_PROG_OFF) - Y_PROG_OFF) / (1F - (Y_PROG_OFF * 2F));
            float yOff = Y_OFF + (yMult * HEIGHT);

            matrix.push();
            switch (te.getFacing())
            {
                case NORTH: matrix.translate(1, 0, 1); matrix.rotate(Vector3f.YP.rotationDegrees(180)); break;
                case EAST:  matrix.translate(0, 0, 1); matrix.rotate(Vector3f.YP.rotationDegrees(90)); break;
                case WEST:  matrix.translate(1, 0, 0); matrix.rotate(Vector3f.YP.rotationDegrees(-90)); break;
                case DOWN:  matrix.translate(0, -1D + ((OFF_Z * 2D)/16D), 1); matrix.rotate(Vector3f.XP.rotationDegrees(-90)); break;
            }

            float[] rand = new float[]
                    {
                            (RAND.nextFloat() % SIZE) - (SIZE / 2F),
                            (RAND.nextFloat() % SIZE) - (SIZE / 2F),
                            (RAND.nextFloat() % SIZE) - (SIZE / 2F),
                            (RAND.nextFloat() % SIZE) - (SIZE / 2F)
                    };

            builder.pos(matrix.getLast().getMatrix(), 0.5F - xOff - SIZE - rand[0], 1F - yOff - SIZE - rand[0], POS_Z).color(0xFF, 0xFF, 0xE0, 0xFF).lightmap(240, 240).endVertex();
            builder.pos(matrix.getLast().getMatrix(), 0.5F - xOff + SIZE + rand[1], 1F - yOff - SIZE - rand[1], POS_Z).color(0xFF, 0xFF, 0xE0, 0xFF).lightmap(240, 240).endVertex();
            builder.pos(matrix.getLast().getMatrix(), 0.5F - xOff + SIZE + rand[2], 1F - yOff + SIZE + rand[2], POS_Z).color(0xFF, 0xFF, 0xE0, 0xFF).lightmap(240, 240).endVertex();
            builder.pos(matrix.getLast().getMatrix(), 0.5F - xOff - SIZE - rand[3], 1F - yOff + SIZE + rand[3], POS_Z).color(0xFF, 0xFF, 0xE0, 0xFF).lightmap(240, 240).endVertex();

            builder.pos(matrix.getLast().getMatrix(), 0.5F + xOff - SIZE - rand[0], 1F - yOff - SIZE - rand[0], POS_Z).color(0xFF, 0xFF, 0xE0, 0xFF).lightmap(240, 240).endVertex();
            builder.pos(matrix.getLast().getMatrix(), 0.5F + xOff + SIZE + rand[1], 1F - yOff - SIZE - rand[1], POS_Z).color(0xFF, 0xFF, 0xE0, 0xFF).lightmap(240, 240).endVertex();
            builder.pos(matrix.getLast().getMatrix(), 0.5F + xOff + SIZE + rand[2], 1F - yOff + SIZE + rand[2], POS_Z).color(0xFF, 0xFF, 0xE0, 0xFF).lightmap(240, 240).endVertex();
            builder.pos(matrix.getLast().getMatrix(), 0.5F + xOff - SIZE - rand[3], 1F - yOff + SIZE + rand[3], POS_Z).color(0xFF, 0xFF, 0xE0, 0xFF).lightmap(240, 240).endVertex();

            //TODO: think about drawing the burn effect on the other side of the wall aswell

            matrix.pop();
        }
    }
}