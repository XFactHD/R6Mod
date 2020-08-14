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
import XFactHD.rssmc.common.entity.gadget.EntityFragGrenade;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderFactoryFragGrenade implements IRenderFactory<EntityFragGrenade>
{
    @Override
    public Render<? super EntityFragGrenade> createRenderFor(RenderManager manager)
    {
        return new RenderFragGrenade(manager, Content.itemFragGrenade, Minecraft.getMinecraft().getRenderItem());
    }

    private static class RenderFragGrenade extends RenderSnowball<EntityFragGrenade>
    {
        private ItemStack stack = new ItemStack(Content.itemFragGrenade);

        @SuppressWarnings("ConstantConditions")
        private RenderFragGrenade(RenderManager manager, Item item, RenderItem render)
        {
            super(manager, item, render);
            stack.setTagCompound(new NBTTagCompound());
            stack.getTagCompound().setBoolean("clicked",true);
        }

        @Override
        public ItemStack getStackToRender(EntityFragGrenade entityIn)
        {
            return stack;
        }
    }
}