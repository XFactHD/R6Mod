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

package XFactHD.rssmc.client.util;

import XFactHD.rssmc.common.utils.helper.LogHelper;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.HashMap;
import java.util.Map;

public enum ModelCache implements IResourceManagerReloadListener
{
    INSTANCE;

    ModelCache()
    {
        ((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this);
    }

    private static final IModelState DEFAULTMODELSTATE = new IModelState()
    {
        @Override
        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        public Optional<TRSRTransformation> apply(Optional<? extends IModelPart> opt)
        {
            return Optional.absent();
        }
    };
    private static final Function<ResourceLocation, TextureAtlasSprite> DEFAULTTEXTUREGETTER = new Function<ResourceLocation, TextureAtlasSprite>()
    {
        @Override
        public TextureAtlasSprite apply(ResourceLocation texture)
        {
            return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(texture.toString());
        }
    };

    private final Map<ResourceLocation, IModel> cache = new HashMap<>();
    private final Map<ResourceLocation, IBakedModel> bakedCache = new HashMap<>();

    public IBakedModel getOrLoadBakedModel(ResourceLocation location)
    {
        IBakedModel model = bakedCache.get(location);
        if(model == null)
        {
            model = getOrLoadModel(location).bake(DEFAULTMODELSTATE, DefaultVertexFormats.ITEM, DEFAULTTEXTUREGETTER);
            bakedCache.put(location, model);
        }
        return model;
    }

    private IModel getOrLoadModel(ResourceLocation location)
    {
        IModel model = cache.get(location);
        if(model == null)
        {
            try
            {
                model = ModelLoaderRegistry.getModel(location);
            }
            catch(Exception e)
            {
                LogHelper.info("Couldn't load model for location " + location + " , allocating missing model!");
                e.printStackTrace();
                model = ModelLoaderRegistry.getMissingModel();
            }
            cache.put(location, model);
        }
        return model;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager)
    {
        cache.clear();
        bakedCache.clear();
    }
}