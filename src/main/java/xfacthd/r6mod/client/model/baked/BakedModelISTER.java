package xfacthd.r6mod.client.model.baked;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.model.TransformationHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation")
public class BakedModelISTER implements IBakedModel
{
    private final IBakedModel original;
    private ItemCameraTransforms.TransformType transform = ItemCameraTransforms.TransformType.NONE;

    public BakedModelISTER(IBakedModel original) { this.original = original; }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData)
    {
        return original.getQuads(state, side, rand, extraData);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand)
    {
        return original.getQuads(state, side, rand);
    }

    @Override
    public TextureAtlasSprite getParticleTexture() { return original.getParticleTexture(); }

    @Override
    public IBakedModel handlePerspective(ItemCameraTransforms.TransformType type, MatrixStack matrix)
    {
        transform = type;

        TransformationMatrix tr = TransformationHelper.toTransformation(this.getItemCameraTransforms().getTransform(type));
        if (!tr.isIdentity()) { tr.push(matrix); }
        return this;
    }

    @Override
    public ItemOverrideList getOverrides() { return original.getOverrides(); }

    @Override
    public boolean isAmbientOcclusion() { return original.isAmbientOcclusion(); }

    @Override
    public boolean isGui3d() { return original.isGui3d(); }

    @Override
    public boolean func_230044_c_() { return original.func_230044_c_(); }

    @Override //INFO: Must be true for ISTERs to work
    public boolean isBuiltInRenderer() { return true; }

    public ItemCameraTransforms.TransformType getTransformType() { return transform; }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() { return original.getItemCameraTransforms(); }
}