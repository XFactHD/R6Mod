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
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.common.entities.grenade.EntityCandelaGrenade;

import java.util.Map;
import java.util.Random;

public class RenderEntityCandela extends EntityRenderer<EntityCandelaGrenade>
{
    private static IBakedModel modelCandela;

    private final Random RAND = new Random();
    private final IModelData DATA = new ModelDataMap.Builder().build();

    public RenderEntityCandela(EntityRendererManager renderManager) { super(renderManager); }

    @Override
    public void render(EntityCandelaGrenade entity, float entityYaw, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int packedLight)
    {
        super.render(entity, entityYaw, partialTicks, matrix, buffer, packedLight);

        matrix.push();

        if (entity.isOnBlock())
        {
            matrix.rotate(Vector3f.YP.rotationDegrees(180 - entityYaw));

            float pitch = entity.getPitch(partialTicks);
            matrix.rotate(Vector3f.XP.rotationDegrees(-pitch));

            double y = pitch == -90 ? -.25 : -.125;
            double z = pitch == -90 ? -.375 : -.25;
            matrix.translate(-.25, y, z);
        }
        else
        {
            matrix.rotate(Vector3f.YP.rotationDegrees(90 - entityYaw));

            matrix.translate(-.25, -.125, -.25);
        }
        matrix.scale(.5F, .5F, .5F);

        IVertexBuilder builder = buffer.getBuffer(Atlases.getCutoutBlockType());
        for (BakedQuad quad : modelCandela.getQuads(null, null, RAND, DATA))
        {
            builder.addQuad(matrix.getLast(), quad, 1.0F, 1.0F, 1.0F, packedLight, OverlayTexture.NO_OVERLAY);
        }

        matrix.pop();
    }

    @Override
    public ResourceLocation getEntityTexture(EntityCandelaGrenade entity) { return null; }

    public static void loadModels(Map<ResourceLocation, IBakedModel> registry)
    {
        ResourceLocation location = new ResourceLocation(R6Mod.MODID, "item/item_candela_0");
        modelCandela = registry.get(location);
    }
}