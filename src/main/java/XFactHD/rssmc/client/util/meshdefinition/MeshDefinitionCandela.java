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

@SuppressWarnings("ConstantConditions")
public class MeshDefinitionCandela extends CustomMeshDefinition
{
    private static final ModelResourceLocation TIMER_0 = new ModelResourceLocation(new ResourceLocation("rssmc", "item_candela_0"), "inventory");
    private static final ModelResourceLocation TIMER_1 = new ModelResourceLocation(new ResourceLocation("rssmc", "item_candela_1"), "inventory");
    private static final ModelResourceLocation TIMER_2 = new ModelResourceLocation(new ResourceLocation("rssmc", "item_candela_2"), "inventory");
    private static final ModelResourceLocation TIMER_3 = new ModelResourceLocation(new ResourceLocation("rssmc", "item_candela_3"), "inventory");
    private static final ModelResourceLocation[] LOCATIONS = new ModelResourceLocation[] {TIMER_0, TIMER_1, TIMER_2, TIMER_3};

    @Override
    public ModelResourceLocation getModelLocation(ItemStack stack)
    {
        if (!stack.hasTagCompound()) { return TIMER_0; }
        return LOCATIONS[stack.getTagCompound().getInteger("timer")];
    }

    @Override
    public void registerVariants()
    {
        ModelBakery.registerItemVariants(Content.itemCandelaGrenade, TIMER_0, TIMER_1, TIMER_2, TIMER_3);
    }
}