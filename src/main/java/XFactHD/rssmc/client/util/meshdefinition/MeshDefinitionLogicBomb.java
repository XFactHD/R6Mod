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
import XFactHD.rssmc.common.items.gadget.ItemLogicBomb;
import XFactHD.rssmc.common.utils.helper.LogHelper;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class MeshDefinitionLogicBomb extends CustomMeshDefinition
{
    private static final int TIME_TO_MAX_STAGE = ItemLogicBomb.RINGING_DURATION - (ItemLogicBomb.RINGING_DURATION / 6);
    private static final ModelResourceLocation BASE_MODEL = new ModelResourceLocation(new ResourceLocation("rssmc", "item_logic_bomb"), "inventory");
    private static final ModelResourceLocation HACK_MODEL = new ModelResourceLocation(new ResourceLocation("rssmc", "item_logic_bomb_hacking"), "inventory");
    private static final ModelResourceLocation RING_MODEL_0 = new ModelResourceLocation(new ResourceLocation("rssmc", "item_logic_bomb_ringing_0"), "inventory");
    private static final ModelResourceLocation RING_MODEL_1 = new ModelResourceLocation(new ResourceLocation("rssmc", "item_logic_bomb_ringing_1"), "inventory");
    private static final ModelResourceLocation RING_MODEL_2 = new ModelResourceLocation(new ResourceLocation("rssmc", "item_logic_bomb_ringing_2"), "inventory");
    private static final ModelResourceLocation RING_MODEL_3 = new ModelResourceLocation(new ResourceLocation("rssmc", "item_logic_bomb_ringing_3"), "inventory");
    private static final ModelResourceLocation RING_MODEL_4 = new ModelResourceLocation(new ResourceLocation("rssmc", "item_logic_bomb_ringing_4"), "inventory");
    private static final ModelResourceLocation RING_MODEL_5 = new ModelResourceLocation(new ResourceLocation("rssmc", "item_logic_bomb_ringing_5"), "inventory");

    @Override
    @SuppressWarnings({"ConstantConditions", "IfCanBeSwitch"})
    public ModelResourceLocation getModelLocation(ItemStack stack)
    {
        if (stack.hasTagCompound())
        {
            if (stack.getTagCompound().getString("action").equals("")) { return BASE_MODEL; }
            else if (stack.getTagCompound().getString("action").equals("hacking")) { return HACK_MODEL; }
            else if (stack.getTagCompound().getString("action").equals("ringing"))
            {
                int diff = (int)(stack.getTagCompound().getLong("timer") - stack.getTagCompound().getLong("stamp"));
                int stage = (int)(((float)diff) / ((float) TIME_TO_MAX_STAGE / 5F));
                switch (stage)
                {
                    case 0:  return RING_MODEL_0;
                    case 1:  return RING_MODEL_1;
                    case 2:  return RING_MODEL_2;
                    case 3:  return RING_MODEL_3;
                    case 4:  return RING_MODEL_4;
                    case 5:  return RING_MODEL_5;
                    default: return RING_MODEL_0;
                }
            }
        }
        return BASE_MODEL;
    }

    @Override
    public void registerVariants()
    {
        ModelBakery.registerItemVariants(Content.itemLogicBomb, BASE_MODEL, HACK_MODEL, RING_MODEL_0, RING_MODEL_1, RING_MODEL_2, RING_MODEL_3, RING_MODEL_4, RING_MODEL_5);
    }
}