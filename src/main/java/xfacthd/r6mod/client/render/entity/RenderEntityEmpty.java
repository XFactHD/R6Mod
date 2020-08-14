package xfacthd.r6mod.client.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderEntityEmpty extends EntityRenderer<Entity>
{
    public RenderEntityEmpty(EntityRendererManager manager) { super(manager); }

    @Override
    public boolean shouldRender(Entity entity, ClippingHelperImpl camera, double camX, double camY, double camZ)
    {
        return Minecraft.getInstance().getRenderManager().isDebugBoundingBox();
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) { return null; }
}