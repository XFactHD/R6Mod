package xfacthd.r6mod.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import xfacthd.r6mod.common.entities.grenade.AbstractEntityGrenade;

public class RenderEntityGrenade<T extends AbstractEntityGrenade> extends EntityRenderer<T>
{
    private final ItemRenderer itemRenderer;
    private final boolean fullbright;

    public RenderEntityGrenade(EntityRendererManager manager, ItemRenderer renderer) { this(manager, renderer, false); }

    public RenderEntityGrenade(EntityRendererManager manager, ItemRenderer renderer, boolean fullbright)
    {
        super(manager);
        this.itemRenderer = renderer;
        this.fullbright = fullbright;
    }

    @Override
    protected int getBlockLight(T entity, BlockPos pos) { return fullbright ? 15 : super.getBlockLight(entity, pos); }

    public void render(T entity, float entityYaw, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int packedLight)
    {
        if (entity.ticksExisted >= 2 || (renderManager.info.getRenderViewEntity().getDistanceSq(entity) >= 12.25D))
        {
            matrix.push();

            matrix.rotate(renderManager.getCameraOrientation());
            matrix.rotate(Vector3f.YP.rotationDegrees(180.0F));

            itemRenderer.renderItem(entity.getItem(), ItemCameraTransforms.TransformType.GROUND, packedLight, OverlayTexture.NO_OVERLAY, matrix, buffer);

            matrix.pop();

            super.render(entity, entityYaw, partialTicks, matrix, buffer, packedLight);
        }
    }

    @SuppressWarnings("deprecation")
    public ResourceLocation getEntityTexture(T entity) { return AtlasTexture.LOCATION_BLOCKS_TEXTURE; }
}