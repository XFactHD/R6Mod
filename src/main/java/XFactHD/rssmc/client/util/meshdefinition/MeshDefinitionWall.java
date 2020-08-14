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
import XFactHD.rssmc.common.utils.propertyEnums.WallType;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

public class MeshDefinitionWall extends CustomMeshDefinition
{
    @Override
    @SuppressWarnings("ConstantConditions")
    public ModelResourceLocation getModelLocation(ItemStack stack)
    {
        EnumMaterial material = EnumMaterial.WALL_MATERIAL.get(stack.getMetadata());
        WallType type = stack.hasTagCompound() ? WallType.values()[stack.getTagCompound().getInteger("type")] : WallType.NORMAL;
        return new ModelResourceLocation(stack.getItem().getRegistryName(), "destroyed=false,facing=north,mat=" + material.getName() + ",type=" + type.getName());
    }

    @Override
    public void registerVariants() {}
}