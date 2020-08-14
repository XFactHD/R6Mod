package xfacthd.r6mod.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import xfacthd.r6mod.common.entities.grenade.EntityCandelaFlash;

public class RenderEntityCandelaFlash extends EntityRenderer<EntityCandelaFlash>
{
    public RenderEntityCandelaFlash(EntityRendererManager manager) { super(manager); }

    @Override
    public void render(EntityCandelaFlash entity, float entityYaw, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int packedLight)
    {
        super.render(entity, entityYaw, partialTicks, matrix, buffer, packedLight);

        //TODO: implement
    }

    @Override
    public ResourceLocation getEntityTexture(EntityCandelaFlash entity) { return null; }
}