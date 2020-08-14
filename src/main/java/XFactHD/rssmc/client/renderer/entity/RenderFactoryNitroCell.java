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

import XFactHD.rssmc.client.models.base.ModelNitroCell;
import XFactHD.rssmc.common.entity.gadget.EntityNitroCell;
import XFactHD.rssmc.common.utils.Reference;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderFactoryNitroCell implements IRenderFactory<EntityNitroCell>
{
    @Override
    public Render<? super EntityNitroCell> createRenderFor(RenderManager manager)
    {
        return new RenderNitroCell(manager);
    }

    private static class RenderNitroCell extends Render<EntityNitroCell>
    {
        private ModelNitroCell model = new ModelNitroCell();

        private RenderNitroCell(RenderManager renderManager)
        {
            super(renderManager);
        }

        @Override
        public void doRender(EntityNitroCell entity, double x, double y, double z, float entityYaw, float partialTicks)
        {
            GlStateManager.pushMatrix();

            GlStateManager.translate(x, y + 2.65, z);

            if (entity.isSticked())
            {
                switch (entity.getStickSide())
                {
                    case DOWN:  GlStateManager.translate(0, -4.775, 0); break;
                    case UP:    GlStateManager.translate(0, 0, 0); break;
                    case NORTH: GlStateManager.translate(0, -2.275, -2.1); break;
                    case SOUTH: GlStateManager.translate(0, -2.275, 2.1); break;
                    case WEST:  GlStateManager.translate(-2.1, -2.275, 0); break;
                    case EAST:  GlStateManager.translate(2.1, -2.275, 0); break;
                }
            }

            GlStateManager.rotate(180, 1, 0, 0);
            GlStateManager.scale(.1, .1, .1);

            if (entity.isSticked())
            {
                GlStateManager.rotate(entity.rotationYaw, 0, 1, 0);
                GlStateManager.rotate(entity.rotationPitch, 1, 0, 0);
            }

            bindTexture(getEntityTexture(entity));
            model.render(entity, 0, 0, 0, 0, 0, 1);

            GlStateManager.popMatrix();
        }

        @Override
        protected ResourceLocation getEntityTexture(EntityNitroCell entity)
        {
            return new ResourceLocation(Reference.MOD_ID, "textures/entity/entity_nitro_cell.png");
        }
    }
}