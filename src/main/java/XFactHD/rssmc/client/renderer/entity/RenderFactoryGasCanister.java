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

import XFactHD.rssmc.common.Content;
import XFactHD.rssmc.common.entity.gadget.EntityGasCanister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderFactoryGasCanister implements IRenderFactory<EntityGasCanister>
{
    @Override
    public Render<? super EntityGasCanister> createRenderFor(RenderManager manager)
    {
        return new RenderSnowball<>(manager, Content.itemGasCanister, Minecraft.getMinecraft().getRenderItem());
    }
}