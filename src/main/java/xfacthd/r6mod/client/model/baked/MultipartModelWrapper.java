package xfacthd.r6mod.client.model.baked;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

@SuppressWarnings("deprecation")
public class MultipartModelWrapper implements IBakedModel //FIXME: filtering out the "missingno" quads can't be the real solution here
{
    private final IBakedModel original;

    public MultipartModelWrapper(IBakedModel original) { this.original = original; }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand)
    {
        List<BakedQuad> quads = original.getQuads(state, side, rand);
        if (quads.size() == 1 && quads.get(0).func_187508_a().getName().getPath().equals("missingno")) { return Collections.emptyList(); }
        return quads;
    }

    @Override
    public boolean isAmbientOcclusion() { return original.isAmbientOcclusion(); }

    @Override
    public boolean isGui3d() { return original.isGui3d(); }

    @Override
    public boolean func_230044_c_() { return original.func_230044_c_(); }

    @Override
    public boolean isBuiltInRenderer() { return original.isBuiltInRenderer(); }

    @Override
    public TextureAtlasSprite getParticleTexture() { return original.getParticleTexture(); }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() { return original.getItemCameraTransforms(); }

    @Override
    public ItemOverrideList getOverrides() { return original.getOverrides(); }

    @Override
    public IBakedModel getBakedModel() { return original.getBakedModel(); }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData)
    {
        List<BakedQuad> quads = original.getQuads(state, side, rand, extraData);
        if (quads.size() == 1 && quads.get(0).func_187508_a().getName().getPath().equals("missingno")) { return Collections.emptyList(); }
        return quads;
    }

    @Override
    public boolean isAmbientOcclusion(BlockState state) { return original.isAmbientOcclusion(state); }

    @Override
    public boolean doesHandlePerspectives() { return original.doesHandlePerspectives(); }

    @Override
    public IBakedModel handlePerspective(ItemCameraTransforms.TransformType type, MatrixStack mat) { return original.handlePerspective(type, mat); }

    @Override
    public IModelData getModelData(@Nonnull ILightReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData)
    {
        return original.getModelData(world, pos, state, tileData);
    }

    @Override
    public TextureAtlasSprite getParticleTexture(@Nonnull IModelData data) { return original.getParticleTexture(data); }
}