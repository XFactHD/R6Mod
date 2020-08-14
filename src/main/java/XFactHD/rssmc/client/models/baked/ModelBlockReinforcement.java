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
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class ModelBlockReinforcement implements IBakedModel
{
    private IBakedModel reinforcement;

    public ModelBlockReinforcement(IBakedModel reinforcement)
    {
        this.reinforcement = reinforcement;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
    {
        if (state != null)
        {
            IExtendedBlockState extState = ((IExtendedBlockState)state);
            IBlockState blockToReinforce = extState.getValue(PropertyHolder.IBLOCKSTATE);
            IBakedModel modelToReinforce = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(blockToReinforce);
            ArrayList<BakedQuad> quads = new ArrayList<>();
            quads.addAll(reinforcement.getQuads(state, side, rand));
            quads.addAll(modelToReinforce.getQuads(blockToReinforce, side, rand));
            return quads;
        }
        return null;
    }

    @Override
    public TextureAtlasSprite getParticleTexture()
    {
        return reinforcement.getParticleTexture();
    }

    @Override
    public ItemOverrideList getOverrides()
    {
        return ItemOverrideList.NONE;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms()
    {
        return reinforcement.getItemCameraTransforms();
    }

    @Override
    public boolean isAmbientOcclusion()
    {
        return reinforcement.isAmbientOcclusion();
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
}