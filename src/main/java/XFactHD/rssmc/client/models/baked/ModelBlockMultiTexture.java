/*  Copyright (C) <2017>  <XFactHD>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see http://www.gnu.org/licenses. */

package XFactHD.rssmc.client.models.baked;

import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import XFactHD.rssmc.common.utils.properties.PropertyBlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings("deprecation")
public class ModelBlockMultiTexture implements IBakedModel
{
    private static final Function<IBlockState, IBakedModel> modelGetter = new Function<IBlockState, IBakedModel>()
    {
        @Override
        public IBakedModel apply(IBlockState state)
        {
            return Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(state);
        }
    };
    private HashMap<IBlockState, IBakedModel> stateModelMap = new HashMap<>();
    private IBakedModel baseModel;

    public ModelBlockMultiTexture(IBakedModel baseModel)
    {
        stateModelMap.put(Blocks.AIR.getDefaultState(), baseModel);
        this.baseModel = baseModel;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
    {
        if (state == null || side == null) { return baseModel.getQuads(state, side, rand); }
        List<BakedQuad> quads = new ArrayList<>();
        IExtendedBlockState extState = (IExtendedBlockState)state;
        IBlockState sideState = extState.getValue(getPropertyForSide(side));
        IBakedModel sideModel = stateModelMap.computeIfAbsent(sideState, modelGetter);
        quads.addAll(sideModel.getQuads(sideState, side, rand));
        return quads;
    }

    @Override
    public TextureAtlasSprite getParticleTexture()
    {
        return baseModel.getParticleTexture();
    }

    @Override
    public ItemOverrideList getOverrides()
    {
        return baseModel.getOverrides();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms()
    {
        return baseModel.getItemCameraTransforms();
    }

    @Override
    public boolean isAmbientOcclusion()
    {
        return baseModel.isAmbientOcclusion();
    }

    @Override
    public boolean isBuiltInRenderer()
    {
        return false;
    }

    @Override
    public boolean isGui3d()
    {
        return true;
    }

    private PropertyBlockState getPropertyForSide(EnumFacing side)
    {
        switch (side)
        {
            case DOWN:  return PropertyHolder.BLOCK_STATE_D;
            case UP:    return PropertyHolder.BLOCK_STATE_U;
            case NORTH: return PropertyHolder.BLOCK_STATE_N;
            case SOUTH: return PropertyHolder.BLOCK_STATE_S;
            case WEST:  return PropertyHolder.BLOCK_STATE_W;
            case EAST:  return PropertyHolder.BLOCK_STATE_E;
            default:    return PropertyHolder.BLOCK_STATE_N;
        }
    }
}