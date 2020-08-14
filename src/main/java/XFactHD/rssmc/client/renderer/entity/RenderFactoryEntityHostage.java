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

package XFactHD.rssmc.client.renderer.entity;

import XFactHD.rssmc.client.models.base.ModelEntityHostage;
import XFactHD.rssmc.client.models.base.ModelEntityHostageDBNO;
import XFactHD.rssmc.client.util.ClientUtils;
import XFactHD.rssmc.common.entity.EntityHostage;
import XFactHD.rssmc.common.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

@SuppressWarnings("unchecked")
public class RenderFactoryEntityHostage implements IRenderFactory<EntityHostage>
{
    private static final ModelBase MODEL_ALIVE = new ModelEntityHostage();
    private static final ModelBase MODEL_DBNO = new ModelEntityHostageDBNO();

    @Override
    public Render<? super EntityHostage> createRenderFor(RenderManager manager)
    {
        return new RenderEntityHostage(manager);
    }

    private class RenderEntityHostage extends RenderLiving<EntityHostage>
    {
        RenderEntityHostage(RenderManager manager)
        {
            super(manager, MODEL_ALIVE, 0);
        }

        @Override
        protected ResourceLocation getEntityTexture(EntityHostage entity)
        {
            return new ResourceLocation(Reference.MOD_ID, "textures/entity/entity_hostage.png");
        }

        @Override
        public void doRender(EntityHostage entity, double x, double y, double z, float entityYaw, float partialTicks)
        {
            mainModel = entity.isDBNO() ? MODEL_DBNO : MODEL_ALIVE;
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
        }
    }
}