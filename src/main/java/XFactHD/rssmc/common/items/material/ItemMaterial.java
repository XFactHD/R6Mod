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

package XFactHD.rssmc.common.items.material;

import XFactHD.rssmc.common.items.ItemBase;
import net.minecraft.creativetab.CreativeTabs;

import java.util.Locale;

public class ItemMaterial extends ItemBase
{
    public ItemMaterial()
    {
        super("itemMaterial", 64, CreativeTabs.MATERIALS, EnumMaterial.getAsStringArray(), EnumMaterial.getOreDictNames());
    }

    public enum EnumMaterial
    {
        //TODO: finish
        PLASTIC_BAR("IngotPlastic", "ingotPlastic"),
        STEEL_INGOT("IngotSteel", "ingotSteel");

        private String name;
        private String oreDictName;

        EnumMaterial(String name, String oreDictName)
        {
            this.oreDictName = oreDictName;
            this.name = name;
        }

        public String getName()
        {
            return name;
        }

        public String getOreDictName()
        {
            return oreDictName;
        }

        public static String[] getAsStringArray()
        {
            String[] strings = new String[values().length];
            for (EnumMaterial material : values())
            {
                strings[material.ordinal()] = material.name;
            }
            return strings;
        }

        public static String[] getOreDictNames()
        {
            String[] strings = new String[values().length];
            for (EnumMaterial material : values())
            {
                strings[material.ordinal()] = material.oreDictName;
            }
            return strings;
        }
    }
}