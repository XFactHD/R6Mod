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

package XFactHD.rssmc.common.data;

public enum EnumScreenEffect
{
    HEAL(false, true, 0, 40),
    ECHO_DISTORTION(true, true, 16, 120),
    GRZMOT_DISTORTION(true, true, 16, 80),
    GU_MINE_POISON(false, true, 0, 40);

    private boolean tileTexture;
    private boolean fade;
    private int tileSize;
    private int duration;

    EnumScreenEffect(boolean tileTexture, boolean fade, int tileSize, int duration)
    {
        this.tileTexture = tileTexture;
        this.fade = fade;
        this.tileSize = tileSize;
        this.duration = duration;
    }

    public boolean shouldTileTexture()
    {
        return tileTexture;
    }

    public int getTileSize()
    {
        return tileSize;
    }

    public int getDuration()
    {
        return duration;
    }

    public boolean shouldFade()
    {
        return fade;
    }
}
