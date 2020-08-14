/*  Copyright (C) <2016>  <XFactHD, DrakoAlcarus>

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

package XFactHD.rssmc.client.gui.controls;

import XFactHD.rssmc.common.data.EnumOperator;

import java.util.Locale;

public class GuiButtonOperator extends GuiButtonTextured
{
    private EnumOperator operator;

    public GuiButtonOperator(int ID, int x, int y, EnumOperator operator)
    {
        super(ID, x, y, 20, 20, "", "operators/" + (operator == EnumOperator.JAEGER ? "jaeger" : operator.getDisplayName().toLowerCase(Locale.ENGLISH)), 2, 2, 16, 16);
        this.operator = operator;
    }

    public EnumOperator getOperator()
    {
        return operator;
    }
}