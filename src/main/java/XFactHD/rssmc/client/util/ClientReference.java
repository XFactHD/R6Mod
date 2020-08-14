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

package XFactHD.rssmc.client.util;

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.I18n;

public class ClientReference
{
    public static final VertexFormat POSITION_TEX_LMAP = new VertexFormat();

    public static String DEFEND = "";
    public static String ATTACK = "";
    public static String CONTESTED = "";
    public static String SECURING = "";
    public static String PLANT = "";
    public static String DEFUSING = "";

    public static void init()
    {
        if (DEFEND.equals(""))
        {
            DEFEND = I18n.format("overlay.rssmc:defend.name");
            ATTACK = I18n.format("overlay.rssmc:attack.name");
            CONTESTED = I18n.format("overlay.rssmc:contested.name");
            SECURING = I18n.format("overlay.rssmc:securing.name");
            PLANT = I18n.format("overlay.rssmc:plant.name");
            DEFUSING = I18n.format("overlay.rssmc:defusing.name");
        }

        POSITION_TEX_LMAP.addElement(DefaultVertexFormats.POSITION_3F);
        POSITION_TEX_LMAP.addElement(DefaultVertexFormats.TEX_2F);
        POSITION_TEX_LMAP.addElement(DefaultVertexFormats.TEX_2S);
    }
}