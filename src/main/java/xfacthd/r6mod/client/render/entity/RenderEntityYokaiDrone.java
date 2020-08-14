package xfacthd.r6mod.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.common.entities.camera.EntityYokaiDrone;

import java.util.Map;
import java.util.Random;

public class RenderEntityYokaiDrone extends EntityRenderer<EntityYokaiDrone>
{
    private final Random RAND = new Random();
    private final IModelData DATA = new ModelDataMap.Builder().build();

    private static IBakedModel DRONE_MODEL;
    private static IBakedModel DRONE_MODEL_CLOAKED;
    private static IBakedModel PROP_MODEL;
    private static IBakedModel PROP_MODEL_CLOAKED;

    public RenderEntityYokaiDrone(EntityRendererManager manager) { super(manager); }

    @Override
    public void render(EntityYokaiDrone entity, float entityYaw, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int packedLight)
    {
        super.render(entity, entityYaw, partialTicks, matrix, buffer, packedLight);

        matrix.push();
        matrix.rotate(Vector3f.YP.rotationDegrees(180 - entityYaw));
        matrix.scale(.7F, .8F, .7F);
        matrix.translate(-.5, .025, -.5);

        IVertexBuilder builder = buffer.getBuffer(Atlases.getTranslucentCullBlockType());

        IBakedModel model = entity.isCloaked() ? DRONE_MODEL_CLOAKED : DRONE_MODEL;
        for (BakedQuad quad : model.getQuads(null, null, RAND, DATA))
        {
            builder.addQuad(matrix.getLast(), quad, 1.0F, 1.0F, 1.0F, packedLight, OverlayTexture.NO_OVERLAY);
        }

        float time = (float)(entity.world.getGameTime() % 5);
        float angle = 360F * ((time + partialTicks) / 5F);

        for (int i = 0; i < 4; i++)
        {
            matrix.push();

            double xOff = (i % 2 == 0) ? -4.875D / 16D : 4.875D / 16D;
            double yOff = (i > 1) ? -4.875D / 16D : 4.875D / 16D;
            matrix.translate(xOff, 0, yOff);

            matrix.translate(0.5, 0, 0.5);
            matrix.rotate(Vector3f.YP.rotationDegrees(angle));
            matrix.translate(-0.5, 0.5D / 16D, -0.5);

            model = entity.isCloaked() ? PROP_MODEL_CLOAKED : PROP_MODEL;
            for (BakedQuad quad : model.getQuads(null, null, RAND, DATA))
            {
                builder.addQuad(matrix.getLast(), quad, 1.0F, 1.0F, 1.0F, packedLight, OverlayTexture.NO_OVERLAY);
            }

            matrix.pop();
        }

        matrix.pop();
    }

    @Override
    public ResourceLocation getEntityTexture(EntityYokaiDrone entity) { return null; }

    public static void registerModels()
    {
        ModelLoader.addSpecialModel(new ResourceLocation(R6Mod.MODID, "entity/entity_yokai_drone"));
        ModelLoader.addSpecialModel(new ResourceLocation(R6Mod.MODID, "entity/entity_yokai_drone_cloaked"));
        ModelLoader.addSpecialModel(new ResourceLocation(R6Mod.MODID, "entity/entity_yokai_propeller"));
        ModelLoader.addSpecialModel(new ResourceLocation(R6Mod.MODID, "entity/entity_yokai_propeller_cloaked"));
    }

    public static void loadModels(Map<ResourceLocation, IBakedModel> modelMap)
    {
        DRONE_MODEL         = modelMap.get(new ResourceLocation(R6Mod.MODID, "entity/entity_yokai_drone"));
        DRONE_MODEL_CLOAKED = modelMap.get(new ResourceLocation(R6Mod.MODID, "entity/entity_yokai_drone_cloaked"));
        PROP_MODEL         = modelMap.get(new ResourceLocation(R6Mod.MODID, "entity/entity_yokai_propeller"));
        PROP_MODEL_CLOAKED = modelMap.get(new ResourceLocation(R6Mod.MODID, "entity/entity_yokai_propeller_cloaked"));
    }
}