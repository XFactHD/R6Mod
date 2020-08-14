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

package XFactHD.rssmc.client.util.wrappers;

import net.minecraft.client.resources.I18n;

public class PointInfo
{
    private long timestamp;
    private String text;
    private int points;
    private String[] bonusText;
    private int[] bonus;

    public PointInfo(long timestamp, String text, int points, Object... bonusList)
    {
        this.timestamp = timestamp;
        this.text = text;
        this.points = points;
        int index = 0;
        bonusText = new String[bonusList.length / 2];
        bonus = new int[bonusList.length / 2];
        for (Object o : bonusList)
        {
            if (bonusText[index] == null)
            {
                bonusText[index] = I18n.format((String)o);
            }
            else
            {
                bonus[index] = (int)o;
                index += 1;
            }
        }
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public String getText()
    {
        return I18n.format(text);
    }

    public int getPoints()
    {
        return points;
    }

    public String[] getBonusText()
    {
        return bonusText;
    }

    public int[] getBonus()
    {
        return bonus;
    }
}