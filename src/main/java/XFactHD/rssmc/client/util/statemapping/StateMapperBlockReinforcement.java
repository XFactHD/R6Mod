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
import XFactHD.rssmc.common.utils.propertyEnums.Connection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.EnumFacing;

public class StateMapperBlockReinforcement extends StateMapperBase
{
    @Override
    protected ModelResourceLocation getModelResourceLocation(IBlockState state)
    {
        String variant;
        EnumFacing facing = state.getValue(PropertyHolder.FACING_NOT_UP);
        Connection con = state.getValue(PropertyHolder.REINFORCEMENT_CONNECTION);

        if (state.getValue(PropertyHolder.FACING_NOT_UP) == EnumFacing.DOWN && isVariantPossibleForFloor(con))
        {
            variant = "facing=" + facing.getName() + ",con=" + con.getName();
        }
        else if (state.getValue(PropertyHolder.FACING_NOT_UP) != EnumFacing.DOWN)
        {
            variant = "facing=" + facing.getName() + ",con=" + con.getName();
        }
        else
        {
            variant = "";
        }
        return new ModelResourceLocation(state.getBlock().getRegistryName(), variant);
    }

    private boolean isVariantPossibleForFloor(Connection con)
    {
        return con == Connection.UL || con == Connection.UR
                || con == Connection.DL || con == Connection.DR;
    }
}