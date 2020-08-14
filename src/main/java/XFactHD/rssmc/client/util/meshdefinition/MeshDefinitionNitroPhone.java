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

package XFactHD.rssmc.client.util.meshdefinition;

import XFactHD.rssmc.common.Content;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class MeshDefinitionNitroPhone extends CustomMeshDefinition
{
    private static final ModelResourceLocation BASE_MODEL = new ModelResourceLocation(new ResourceLocation("rssmc", "item_nitro_phone"), "inventory");
    private static final ModelResourceLocation ACTIVE_MODEL = new ModelResourceLocation(new ResourceLocation("rssmc", "item_nitro_phone_active"), "inventory");

    @Override
    @SuppressWarnings("ConstantConditions")
    public ModelResourceLocation getModelLocation(ItemStack stack)
    {
        if (!stack.hasTagCompound()) { return BASE_MODEL; }
        return stack.getTagCompound().getBoolean("active") ? ACTIVE_MODEL : BASE_MODEL;
    }

    @Override
    public void registerVariants()
    {
        ModelBakery.registerItemVariants(Content.itemNitroPhone, BASE_MODEL, ACTIVE_MODEL);
    }
}