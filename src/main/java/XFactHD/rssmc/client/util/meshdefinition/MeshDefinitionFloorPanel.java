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

import XFactHD.rssmc.common.utils.propertyEnums.EnumMaterial;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

public class MeshDefinitionFloorPanel extends CustomMeshDefinition
{
    @Override
    @SuppressWarnings("ConstantConditions")
    public ModelResourceLocation getModelLocation(ItemStack stack)
    {
        EnumMaterial material = EnumMaterial.FLOOR_MATERIAL.get(stack.getMetadata());
        boolean solid = stack.hasTagCompound() && stack.getTagCompound().getBoolean("solid");
        return new ModelResourceLocation(stack.getItem().getRegistryName(), "destroyed=false,facing=north,mat=" + material.getName() + ",solid=" + solid);
    }

    @Override
    public void registerVariants() {}
}