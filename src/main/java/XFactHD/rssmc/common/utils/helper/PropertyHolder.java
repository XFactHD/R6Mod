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

package XFactHD.rssmc.common.utils.helper;

import XFactHD.rssmc.common.utils.properties.PropertyBlockState;
import XFactHD.rssmc.common.utils.propertyEnums.*;
import net.minecraft.block.properties.*;
import net.minecraft.util.EnumFacing;

import java.util.Arrays;

public class PropertyHolder
{
    public static final PropertyBool TOP              = PropertyBool.create("top");
    public static final PropertyBool BOTTOM           = PropertyBool.create("bottom");
    public static final PropertyBool DEFUSING         = PropertyBool.create("defusing");
    public static final PropertyBool DEFUSED          = PropertyBool.create("defused");
    public static final PropertyBool ACTIVE           = PropertyBool.create("active");
    public static final PropertyBool ACTIVATED        = PropertyBool.create("activated");
    public static final PropertyBool EMPTY            = PropertyBool.create("empty");
    public static final PropertyBool WINDOW           = PropertyBool.create("window");
    public static final PropertyBool ELECTRIFIED      = PropertyBool.create("electro");
    public static final PropertyBool DESTROYED        = PropertyBool.create("destroyed");
    public static final PropertyBool DOOR             = PropertyBool.create("door");
    public static final PropertyBool RIGHT            = PropertyBool.create("right");
    public static final PropertyBool LEFT             = PropertyBool.create("left");
    public static final PropertyBool ON_REINFORCEMENT = PropertyBool.create("on_reinf");
    public static final PropertyBool SECURING         = PropertyBool.create("securing");
    public static final PropertyBool SOLID            = PropertyBool.create("solid");
    public static final PropertyBool LOADED           = PropertyBool.create("loaded");
    public static final PropertyBool CORNER           = PropertyBool.create("corner");
    public static final PropertyBool LARGE            = PropertyBool.create("large");

    public static final PropertyDirection FACING_FULL     = PropertyDirection.create("facing");
    public static final PropertyDirection FACING_CARDINAL = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyDirection FACING_NOT_UP   = PropertyDirection.create("facing", Arrays.asList(EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.DOWN));
    public static final PropertyDirection FACING_NOT_DOWN = PropertyDirection.create("facing", Arrays.asList(EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.UP));
    public static final PropertyDirection FACING_NE       = PropertyDirection.create("facing", Arrays.asList(EnumFacing.NORTH, EnumFacing.EAST));

    public static final PropertyEnum<Connection> REINFORCEMENT_CONNECTION  = PropertyEnum.create("con", Connection.class);
    public static final PropertyEnum<Connection> DROP_HATCH_CONNECTION     = PropertyEnum.create("con", Connection.class, Connection.CON_FOUR_PARTS);
    public static final PropertyEnum<Connection> BARBED_WIRE_CONNECTION    = PropertyEnum.create("con", Connection.class, Connection.CON_FOUR_PARTS);
    public static final PropertyEnum<EnumMaterial> FLOOR_MATERIAL          = PropertyEnum.create("mat", EnumMaterial.class, EnumMaterial.FLOOR_MATERIAL);
    public static final PropertyEnum<EnumMaterial> WALL_MATERIAL           = PropertyEnum.create("mat", EnumMaterial.class, EnumMaterial.WALL_MATERIAL);
    public static final PropertyEnum<WallType> WALL_TYPE                   = PropertyEnum.create("type", WallType.class);
    public static final PropertyEnum<RailingType> RAILING_TYPE             = PropertyEnum.create("type", RailingType.class);
    public static final PropertyEnum<MirrorState> MIRROR_STATE             = PropertyEnum.create("state", MirrorState.class);

    public static final PropertyBlockState IBLOCKSTATE   = new PropertyBlockState();
    public static final PropertyBlockState BLOCK_STATE_U = new PropertyBlockState();
    public static final PropertyBlockState BLOCK_STATE_D = new PropertyBlockState();
    public static final PropertyBlockState BLOCK_STATE_N = new PropertyBlockState();
    public static final PropertyBlockState BLOCK_STATE_E = new PropertyBlockState();
    public static final PropertyBlockState BLOCK_STATE_S = new PropertyBlockState();
    public static final PropertyBlockState BLOCK_STATE_W = new PropertyBlockState();
}