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

package XFactHD.rssmc.client.util.statemapping;

import XFactHD.rssmc.common.utils.Reference;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.ResourceLocation;

public class StateMapperBlockBarricade extends StateMapperBase
{
    @Override
    protected ModelResourceLocation getModelResourceLocation(IBlockState state)
    {
        boolean window = state.getValue(PropertyHolder.WINDOW);
        boolean large = state.getValue(PropertyHolder.LARGE);
        String path = window ? "blockBarricadeWindow" + (large ? "Large" : "") : "blockBarricade" + (large ? "Large" : "");
        String door = "";
        if (!window)
        {
            door = "door=" + state.getValue(PropertyHolder.DOOR) + ",";
        }
        String facing = state.getValue(PropertyHolder.FACING_CARDINAL).getName();
        String top = state.getValue(PropertyHolder.TOP).toString();
        String side = "";
        if (window && large)
        {
            if (state.getValue(PropertyHolder.TOP))
            {
                side = state.getValue(PropertyHolder.RIGHT) ? ",side=top_right" : state.getValue(PropertyHolder.LEFT) ? ",side=top_left" : ",side=top_center";
            }
            else
            {
                side = state.getValue(PropertyHolder.RIGHT) ? ",side=bottom_right" : state.getValue(PropertyHolder.LEFT) ? ",side=bottom_left" : ",side=bottom_center";
            }
        }
        else if (large)
        {
            side = state.getValue(PropertyHolder.RIGHT) ? ",left=false,right=true" : state.getValue(PropertyHolder.LEFT) ? ",left=true,right=false" : ",left=false,right=false";
        }

        return new ModelResourceLocation(new ResourceLocation(Reference.MOD_ID, path), door + "facing=" + facing + side + (!(window && large) ? ",top=" + top : ""));
    }
}