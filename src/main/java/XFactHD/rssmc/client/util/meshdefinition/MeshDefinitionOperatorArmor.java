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
import XFactHD.rssmc.common.data.EnumOperator;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

import java.util.Locale;

public class MeshDefinitionOperatorArmor extends CustomMeshDefinition
{
    @Override
    @SuppressWarnings("ConstantConditions")
    public ModelResourceLocation getModelLocation(ItemStack stack)
    {
        EnumOperator operator = stack.hasTagCompound() ? EnumOperator.valueOf(stack.getTagCompound().getInteger("operator")) : null;
        String op = operator == null || operator.toString().toLowerCase(Locale.ENGLISH).contains("unknown") ? "unknown" : operator.toString().toLowerCase(Locale.ENGLISH);
        boolean rook = stack.hasTagCompound() && stack.getTagCompound().getBoolean("rook");
        return new ModelResourceLocation("rssmc:armor/" + op + "/item_armor_" + op + (rook ? "_rook" : "") + "_chest", "inventory");
    }

    @Override
    public void registerVariants()
    {
        for (EnumOperator operator : EnumOperator.values())
        {
            String op = operator.toString().toLowerCase(Locale.ENGLISH);
            ModelBakery.registerItemVariants(Content.itemOperatorArmor,
                    new ModelResourceLocation("rssmc:armor/" + op + "/item_armor_" + op + "_chest", "inventory"),
                    new ModelResourceLocation("rssmc:armor/" + op + "/item_armor_" + op + "_rook_chest", "inventory"));
        }

        ModelBakery.registerItemVariants(Content.itemOperatorArmor,
                new ModelResourceLocation("rssmc:armor/unknown/item_armor_unknown_chest", "inventory"),
                new ModelResourceLocation("rssmc:armor/unknown/item_armor_unknown_rook_chest", "inventory"));
    }
}