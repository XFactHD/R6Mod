package xfacthd.r6mod.client.render.ter;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.common.tileentities.gadgets.TileEntityEvilEye;

import java.util.Map;
import java.util.Random;

public class RenderEvilEye extends TileEntityRenderer<TileEntityEvilEye>
{
    private static IBakedModel GIMBAL_MODEL;
    private static IBakedModel GLOBE_MODEL;

    private static TextureAtlasSprite DOOR_OUTSIDE;
    private static TextureAtlasSprite DOOR_INSIDE;

    private final Random rand = new Random();
    private final IModelData data = new ModelDataMap.Builder().build();

    public RenderEvilEye(TileEntityRendererDispatcher dispatch) { super(dispatch); }

    @Override
    public void render(TileEntityEvilEye te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
    {
        IVertexBuilder builder = buffer.getBuffer(Atlases.getCutoutBlockType());
        boolean playerInCam = Minecraft.getInstance().getRenderViewEntity() == te.getCamera();
        boolean isUp = te.getFacing() == Direction.UP;
        boolean inUse = te.getCamera() != null && te.getCamera().isInUse();
        boolean friendly = te.isFriendly(Minecraft.getInstance().player);

        matrix.push();

        float yaw = te.getRotationYaw(partialTicks);
        float pitch = te.getRotationPitch(partialTicks);

        applyFacingRotation(te.getFacing(), matrix);
        drawGimbal(builder, matrix, yaw, pitch, playerInCam, combinedLight, combinedOverlay);
        drawGlobe(builder, matrix, isUp, yaw, pitch, combinedLight, combinedOverlay);

        builder = buffer.getBuffer(Atlases.getTranslucentBlockType());
        drawDoor(builder, matrix, playerInCam, te.getDoorState(), combinedLight, combinedOverlay);

        builder = buffer.getBuffer(RenderType.getLeash());
        drawActivityLight(builder, matrix, inUse, friendly);

        matrix.pop();
    }

    private void applyFacingRotation(Direction facing, MatrixStack matrix)
    {
        if (facing == Direction.UP) { return; }

        matrix.translate(.5, .5, .5);
        matrix.rotate(Vector3f.YP.rotationDegrees(facing.getAxis() == Direction.Axis.X ? 90 : 180));
        matrix.rotate(Vector3f.XP.rotationDegrees(facing == Direction.NORTH || facing == Direction.EAST ? 90 : -90));
        matrix.translate(-.5, -.5, -.5);
    }

    private void drawGimbal(IVertexBuilder builder, MatrixStack matrix, float yaw, float pitch, boolean playerInCam, int combinedLight, int combinedOverlay)
    {
        if (playerInCam) { return; }

        float actual = (Math.abs(pitch) < 45F) ? yaw : 0F;

        matrix.push();

        matrix.translate(8D / 16D, 0, 8D / 16D);
        matrix.rotate(Vector3f.YP.rotationDegrees(actual));
        matrix.translate(-8D / 16D, 0, -8D / 16D);

        for (BakedQuad quad : GIMBAL_MODEL.getQuads(null, null, rand, data))
        {
            builder.addQuad(matrix.getLast(), quad, 1.0F, 1.0F, 1.0F, combinedLight, combinedOverlay);
        }

        matrix.pop();
    }

    private void drawGlobe(IVertexBuilder builder, MatrixStack matrix, boolean isUp, float yaw, float pitch, int combinedLight, int combinedOverlay)
    {
        matrix.translate(8D / 16D, 9D / 16D, 8D / 16D);
        if (isUp) { matrix.rotate(Vector3f.YP.rotationDegrees(yaw)); }
        else { matrix.rotate(Vector3f.ZP.rotationDegrees(yaw)); }
        matrix.rotate(Vector3f.XP.rotationDegrees(pitch));
        matrix.translate(-8D / 16D, -9D / 16D, -8D / 16D);

        for (BakedQuad quad : GLOBE_MODEL.getQuads(null, null, rand, data))
        {
            builder.addQuad(matrix.getLast(), quad, 1.0F, 1.0F, 1.0F, combinedLight, combinedOverlay);
        }
    }

    private void drawDoor(IVertexBuilder builder, MatrixStack matrix, boolean playerInCam, float doorState, int combinedLight, int combinedOverlay)
    {
        if (playerInCam) { drawDoorInside(builder, matrix, doorState, combinedLight, combinedOverlay); }
        else { drawDoorOutside(builder, matrix, doorState, combinedLight, combinedOverlay); }
    }

    private void drawDoorOutside(IVertexBuilder builder, MatrixStack matrix, float doorState, int combinedLight, int combinedOverlay)
    {
        doorState = Math.max(doorState, 0.01F);

        float x = 4F / 16F;
        float y = 5F / 16F;
        float w = 4F / 16F * doorState;
        float h = 6F / 16F;

        matrix.translate(8D / 16D, 8D / 16D, 8D / 16D);
        matrix.rotate(Vector3f.XP.rotationDegrees(-90));
        matrix.translate(-8D / 16D, -8D / 16D, -8D / 16D);

        //Left door
        float minU = DOOR_OUTSIDE.getInterpolatedU(5F - w);
        float minV = DOOR_OUTSIDE.getInterpolatedV(2);
        float maxU = DOOR_OUTSIDE.getInterpolatedU(5);
        float maxV = DOOR_OUTSIDE.getInterpolatedV(8);
        builder.pos(matrix.getLast().getMatrix(), x, y, 14F / 16F).color(255, 255, 255, 255).tex(minU, minV).overlay(combinedOverlay).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 1F, 1F, 1F).endVertex();
        builder.pos(matrix.getLast().getMatrix(), x + w, y, 14F / 16F).color(255, 255, 255, 255).tex(maxU, minV).overlay(combinedOverlay).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 1F, 1F, 1F).endVertex();
        builder.pos(matrix.getLast().getMatrix(), x + w, y + h, 14F / 16F).color(255, 255, 255, 255).tex(maxU, maxV).overlay(combinedOverlay).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 1F, 1F, 1F).endVertex();
        builder.pos(matrix.getLast().getMatrix(), x, y + h, 14F / 16F).color(255, 255, 255, 255).tex(minU, maxV).overlay(combinedOverlay).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 1F, 1F, 1F).endVertex();

        //Left door lip
        minU = DOOR_OUTSIDE.getInterpolatedU(4);
        minV = DOOR_OUTSIDE.getInterpolatedV(2);
        maxU = DOOR_OUTSIDE.getInterpolatedU(5);
        maxV = DOOR_OUTSIDE.getInterpolatedV(8);
        builder.pos(matrix.getLast().getMatrix(), x + w, y, 14F / 16F).color(255, 255, 255, 255).tex(minU, minV).overlay(combinedOverlay).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 1F, 1F, 1F).endVertex();
        builder.pos(matrix.getLast().getMatrix(), x + w, y, 13.75F / 16F).color(255, 255, 255, 255).tex(maxU, minV).overlay(combinedOverlay).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 1F, 1F, 1F).endVertex();
        builder.pos(matrix.getLast().getMatrix(), x + w, y + h, 13.75F / 16F).color(255, 255, 255, 255).tex(maxU, maxV).overlay(combinedOverlay).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 1F, 1F, 1F).endVertex();
        builder.pos(matrix.getLast().getMatrix(), x + w, y + h, 14F / 16F).color(255, 255, 255, 255).tex(minU, maxV).overlay(combinedOverlay).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 1F, 1F, 1F).endVertex();

        //Right door
        minU = DOOR_OUTSIDE.getInterpolatedU(5);
        minV = DOOR_OUTSIDE.getInterpolatedV(2);
        maxU = DOOR_OUTSIDE.getInterpolatedU(5F + w);
        maxV = DOOR_OUTSIDE.getInterpolatedV(8);
        x = 12F / 16F;
        builder.pos(matrix.getLast().getMatrix(), x - w, y, 14F / 16F).color(255, 255, 255, 255).tex(minU, minV).overlay(combinedOverlay).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 1F, 1F, 1F).endVertex();
        builder.pos(matrix.getLast().getMatrix(), x, y, 14F / 16F).color(255, 255, 255, 255).tex(maxU, minV).overlay(combinedOverlay).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 1F, 1F, 1F).endVertex();
        builder.pos(matrix.getLast().getMatrix(), x, y + h, 14F / 16F).color(255, 255, 255, 255).tex(maxU, maxV).overlay(combinedOverlay).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 1F, 1F, 1F).endVertex();
        builder.pos(matrix.getLast().getMatrix(), x - w, y + h, 14F / 16F).color(255, 255, 255, 255).tex(minU, maxV).overlay(combinedOverlay).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 1F, 1F, 1F).endVertex();

        //Right door lip
        minU = DOOR_OUTSIDE.getInterpolatedU(4);
        minV = DOOR_OUTSIDE.getInterpolatedV(2);
        maxU = DOOR_OUTSIDE.getInterpolatedU(5);
        maxV = DOOR_OUTSIDE.getInterpolatedV(8);
        builder.pos(matrix.getLast().getMatrix(), x - w, y, 13.75F / 16F).color(255, 255, 255, 255).tex(minU, minV).overlay(combinedOverlay).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 1F, 1F, 1F).endVertex();
        builder.pos(matrix.getLast().getMatrix(), x - w, y, 14F / 16F).color(255, 255, 255, 255).tex(maxU, minV).overlay(combinedOverlay).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 1F, 1F, 1F).endVertex();
        builder.pos(matrix.getLast().getMatrix(), x - w, y + h, 14F / 16F).color(255, 255, 255, 255).tex(maxU, maxV).overlay(combinedOverlay).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 1F, 1F, 1F).endVertex();
        builder.pos(matrix.getLast().getMatrix(), x - w, y + h, 13.75F / 16F).color(255, 255, 255, 255).tex(minU, maxV).overlay(combinedOverlay).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 1F, 1F, 1F).endVertex();
    }

    private void drawDoorInside(IVertexBuilder builder, MatrixStack matrix, float doorState, int combinedLight, int combinedOverlay)
    {
        doorState = Math.max(doorState, 0.01F);

        float x = 4F / 16F;
        float y = 5F / 16F;
        float w = 4F / 16F * doorState;
        float h = 6F / 16F;

        matrix.translate(8D / 16D, 8D / 16D, 8D / 16D);
        matrix.rotate(Vector3f.XP.rotationDegrees(-90));
        matrix.translate(-8D / 16D, -8D / 16D, -8D / 16D);

        //Left door
        float minU = DOOR_INSIDE.getInterpolatedU(5F - w);
        float minV = DOOR_INSIDE.getInterpolatedV(2);
        float maxU = DOOR_INSIDE.getInterpolatedU(5);
        float maxV = DOOR_INSIDE.getInterpolatedV(8);
        builder.pos(matrix.getLast().getMatrix(), x + w, y, 14F / 16F).color(255, 255, 255, 255).tex(minU, minV).overlay(combinedOverlay).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 1F, 1F, 1F).endVertex();
        builder.pos(matrix.getLast().getMatrix(), x, y, 14F / 16F).color(255, 255, 255, 255).tex(maxU, minV).overlay(combinedOverlay).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 1F, 1F, 1F).endVertex();
        builder.pos(matrix.getLast().getMatrix(), x, y + h, 14F / 16F).color(255, 255, 255, 255).tex(maxU, maxV).overlay(combinedOverlay).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 1F, 1F, 1F).endVertex();
        builder.pos(matrix.getLast().getMatrix(), x + w, y + h, 14F / 16F).color(255, 255, 255, 255).tex(minU, maxV).overlay(combinedOverlay).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 1F, 1F, 1F).endVertex();

        //Left door lip
        minU = DOOR_INSIDE.getInterpolatedU(4);
        minV = DOOR_INSIDE.getInterpolatedV(2);
        maxU = DOOR_INSIDE.getInterpolatedU(5);
        maxV = DOOR_INSIDE.getInterpolatedV(8);
        builder.pos(matrix.getLast().getMatrix(), x + w, y, 14F / 16F).color(255, 255, 255, 255).tex(minU, minV).overlay(combinedOverlay).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 1F, 1F, 1F).endVertex();
        builder.pos(matrix.getLast().getMatrix(), x + w, y, 13.75F / 16F).color(255, 255, 255, 255).tex(maxU, minV).overlay(combinedOverlay).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 1F, 1F, 1F).endVertex();
        builder.pos(matrix.getLast().getMatrix(), x + w, y + h, 13.75F / 16F).color(255, 255, 255, 255).tex(maxU, maxV).overlay(combinedOverlay).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 1F, 1F, 1F).endVertex();
        builder.pos(matrix.getLast().getMatrix(), x + w, y + h, 14F / 16F).color(255, 255, 255, 255).tex(minU, maxV).overlay(combinedOverlay).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 1F, 1F, 1F).endVertex();

        //Right door
        minU = DOOR_INSIDE.getInterpolatedU(5);
        minV = DOOR_INSIDE.getInterpolatedV(2);
        maxU = DOOR_INSIDE.getInterpolatedU(5F + w);
        maxV = DOOR_INSIDE.getInterpolatedV(8);
        x = 12F / 16F;
        builder.pos(matrix.getLast().getMatrix(), x, y, 14F / 16F).color(255, 255, 255, 255).tex(minU, minV).overlay(combinedOverlay).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 0F, 0F, 0F).endVertex();
        builder.pos(matrix.getLast().getMatrix(), x - w, y, 14F / 16F).color(255, 255, 255, 255).tex(maxU, minV).overlay(combinedOverlay).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 0F, 0F, 0F).endVertex();
        builder.pos(matrix.getLast().getMatrix(), x - w, y + h, 14F / 16F).color(255, 255, 255, 255).tex(maxU, maxV).overlay(combinedOverlay).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 0F, 0F, 0F).endVertex();
        builder.pos(matrix.getLast().getMatrix(), x, y + h, 14F / 16F).color(255, 255, 255, 255).tex(minU, maxV).overlay(combinedOverlay).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 0F, 0F, 0F).endVertex();

        //Right door lip
        minU = DOOR_INSIDE.getInterpolatedU(4);
        minV = DOOR_INSIDE.getInterpolatedV(2);
        maxU = DOOR_INSIDE.getInterpolatedU(5);
        maxV = DOOR_INSIDE.getInterpolatedV(8);
        builder.pos(matrix.getLast().getMatrix(), x - w, y, 13.75F / 16F).color(255, 255, 255, 255).tex(minU, minV).overlay(combinedOverlay).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 1F, 1F, 1F).endVertex();
        builder.pos(matrix.getLast().getMatrix(), x - w, y, 14F / 16F).color(255, 255, 255, 255).tex(maxU, minV).overlay(combinedOverlay).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 1F, 1F, 1F).endVertex();
        builder.pos(matrix.getLast().getMatrix(), x - w, y + h, 14F / 16F).color(255, 255, 255, 255).tex(maxU, maxV).overlay(combinedOverlay).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 1F, 1F, 1F).endVertex();
        builder.pos(matrix.getLast().getMatrix(), x - w, y + h, 13.75F / 16F).color(255, 255, 255, 255).tex(minU, maxV).overlay(combinedOverlay).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 1F, 1F, 1F).endVertex();
    }

    private void drawActivityLight(IVertexBuilder builder, MatrixStack matrix, boolean inUse, boolean friendly)
    {
        if (!inUse) { return; }

        float SIZE = 0.02F;
        float[] rand = new float[]
                {
                        (this.rand.nextFloat() % SIZE / 2F) - (SIZE / 4F),
                        (this.rand.nextFloat() % SIZE / 2F) - (SIZE / 4F),
                        (this.rand.nextFloat() % SIZE / 2F) - (SIZE / 4F),
                        (this.rand.nextFloat() % SIZE / 2F) - (SIZE / 4F)
                };

        int r = friendly ? 0x00 : 0xFF;
        int g = friendly ? 0x9B : 0x00;
        int b = friendly ? 0xFF : 0x00;

        builder.pos(matrix.getLast().getMatrix(), 0.5F - SIZE - rand[0], 4F / 16F - SIZE - rand[0], 14.01F / 16F).color(r, g, b, 0xFF).lightmap(240, 240).endVertex();
        builder.pos(matrix.getLast().getMatrix(), 0.5F + SIZE + rand[1], 4F / 16F - SIZE - rand[1], 14.01F / 16F).color(r, g, b, 0xFF).lightmap(240, 240).endVertex();
        builder.pos(matrix.getLast().getMatrix(), 0.5F + SIZE + rand[2], 4F / 16F + SIZE + rand[2], 14.01F / 16F).color(r, g, b, 0xFF).lightmap(240, 240).endVertex();
        builder.pos(matrix.getLast().getMatrix(), 0.5F - SIZE - rand[3], 4F / 16F + SIZE + rand[3], 14.01F / 16F).color(r, g, b, 0xFF).lightmap(240, 240).endVertex();
    }



    public static void registerModels()
    {
        ModelLoader.addSpecialModel(new ResourceLocation(R6Mod.MODID, "block/gadget/block_evil_eye_gimbal"));
        ModelLoader.addSpecialModel(new ResourceLocation(R6Mod.MODID, "block/gadget/block_evil_eye_globe"));
    }

    public static void loadModels(Map<ResourceLocation, IBakedModel> modelMap)
    {
        GIMBAL_MODEL = modelMap.get(new ResourceLocation(R6Mod.MODID, "block/gadget/block_evil_eye_gimbal"));
        GLOBE_MODEL =  modelMap.get(new ResourceLocation(R6Mod.MODID, "block/gadget/block_evil_eye_globe"));
    }

    public static void registerTextures(TextureStitchEvent.Pre event)
    {
        event.addSprite(new ResourceLocation(R6Mod.MODID, "block/block_evil_eye_front_inner"));
    }

    public static void loadTextures(AtlasTexture texMap)
    {
        DOOR_OUTSIDE = texMap.getSprite(new ResourceLocation(R6Mod.MODID, "block/block_evil_eye_front"));
        DOOR_INSIDE = texMap.getSprite(new ResourceLocation(R6Mod.MODID, "block/block_evil_eye_front_inner"));
    }
}