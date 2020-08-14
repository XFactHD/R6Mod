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

package XFactHD.rssmc.common.utils.propertyEnums;

import net.minecraft.util.IStringSerializable;

import java.util.ArrayList;
import java.util.Locale;

public enum EnumMaterial implements IStringSerializable
{
    OAK,
    SPRUCE,
    BIRCH,
    JUNGLE,
    ACACIA,
    DARK_OAK,
    PLASTER,
    DIORITE,
    ANDESITE,
    GRANITE,
    IRON,
    STONE;

    public static final ArrayList<EnumMaterial> WALL_MATERIAL = new ArrayList<>();
    public static final ArrayList<EnumMaterial> FLOOR_MATERIAL = new ArrayList<>();

    static
    {
        WALL_MATERIAL.add(OAK);
        WALL_MATERIAL.add(SPRUCE);
        WALL_MATERIAL.add(BIRCH);
        WALL_MATERIAL.add(JUNGLE);
        WALL_MATERIAL.add(ACACIA);
        WALL_MATERIAL.add(DARK_OAK);
        WALL_MATERIAL.add(PLASTER);
        WALL_MATERIAL.add(IRON);
        WALL_MATERIAL.add(STONE);

        FLOOR_MATERIAL.add(OAK);
        FLOOR_MATERIAL.add(SPRUCE);
        FLOOR_MATERIAL.add(BIRCH);
        FLOOR_MATERIAL.add(JUNGLE);
        FLOOR_MATERIAL.add(ACACIA);
        FLOOR_MATERIAL.add(DARK_OAK);
        FLOOR_MATERIAL.add(DIORITE);
        FLOOR_MATERIAL.add(ANDESITE);
        FLOOR_MATERIAL.add(GRANITE);
        FLOOR_MATERIAL.add(STONE);
    }

    @Override
    public String getName()
    {
        return toString().toLowerCase(Locale.ENGLISH);
    }
}
