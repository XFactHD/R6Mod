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

import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;

public class StateMapperBlockBlackMirror extends StateMapperBase
{
    @Override
    protected ModelResourceLocation getModelResourceLocation(IBlockState state)
    {
        String variant = "facing=" + state.getValue(PropertyHolder.FACING_CARDINAL).getName() + ",mat=" + state.getValue(PropertyHolder.WALL_MATERIAL).getName();
        String side = state.getValue(PropertyHolder.RIGHT) ? "right" : "left";
        String status = state.getValue(PropertyHolder.MIRROR_STATE).getName();
        return new ModelResourceLocation("rssmc:block_black_mirror_" + side + "_" + status, variant);
    }
}