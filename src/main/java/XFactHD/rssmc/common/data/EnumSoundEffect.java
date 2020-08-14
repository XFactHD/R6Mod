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

import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;

public enum EnumSoundEffect
{
    SWITCH_FIREMODE(SoundEvents.BLOCK_PISTON_CONTRACT, .04F, 2);

    private SoundEvent event;
    private float volume;
    private float pitch;

    EnumSoundEffect(SoundEvent event, float volume, float pitch)
    {
        this.event = event;
        this.volume = volume;
        this.pitch = pitch;
    }

    public SoundEvent getSoundEvent()
    {
        return event;
    }

    public float getVolume()
    {
        return volume;
    }

    public float getPitch()
    {
        return pitch;
    }

    public static EnumSoundEffect valueOf(int index)
    {
        if (index < values().length)
        {
            return values()[index];
        }
        return null;
    }
}