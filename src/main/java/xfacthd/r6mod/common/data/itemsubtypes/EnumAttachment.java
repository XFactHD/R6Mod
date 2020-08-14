/*  Copyright (C) <2020>  <XFactHD>

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

package xfacthd.r6mod.common.data.itemsubtypes;

import java.util.*;

//TODO: add recoil, spread and ads time offsets
public enum EnumAttachment
{
    ACOG_SIGHT    (Type.SIGHT, 1, 1, 1, .4D),
    SCOPE         (Type.SIGHT, 1, 1, 1, .4D),
    HOLO_SIGHT    (Type.SIGHT, 1, 1, 1, .9D),
    RED_DOT_SIGHT (Type.SIGHT, 1, 1, 1, .9D),
    REFLEX_SIGHT  (Type.SIGHT, 1, 1, 1, .9D),

    VERTICAL_GRIP (Type.GRIP, 1, 1, 1, 0),
    ANGLED_GRIP   (Type.GRIP, 1, 1, 1, 0),

    LASER (Type.UNDER_BARREL, 0.75F, 1, 1, 0),

    SUPPRESSOR   (Type.BARREL, 1, 1, 1, 0),
    FLASH_HIDER  (Type.BARREL, 1, 1, 1, 0),
    COMPENSATOR  (Type.BARREL, 1, 1, 1, 0),
    MUZZLE_BREAK (Type.BARREL, 1, 1, 1, 0),
    HEAVY_BARREL (Type.BARREL, 1, 1, 1, 0),

    RIFLE_SHIELD   (Type.SPECIAL, 1, 1, 1.375F, 0),
    SHOTGUN        (Type.SPECIAL, 1, 1, 1, 0),
    FLIP_SIGHT     (Type.SPECIAL, 1, 1, 1, .25D),
    AIRJAB_LAUNCHER(Type.SPECIAL, 1, 1, 1, 0),
    LV_LANCE       (Type.SPECIAL, 1, 1, 1, 0);

    private final Type type;
    private final float spreadMult;
    private final float recoilMult;
    private final float adsTimeMult;
    private final double fovMult;

    EnumAttachment(Type type, float spreadMult, float recoilMult, float adsTimeMult, double fovMult)
    {
        this.type = type;
        this.spreadMult = spreadMult;
        this.recoilMult = recoilMult;
        this.adsTimeMult = adsTimeMult;
        this.fovMult = fovMult;
    }

    public Type getType() { return type; }

    public float getRecoilMultiplier() { return recoilMult; }

    public float getSpreadMultiplier() { return spreadMult; }

    public float getAimTimeMultiplier() { return adsTimeMult; }

    public double getFovMultiplier() { return fovMult; }

    public String toItemName() { return "item_attachment_" + toString().toLowerCase(Locale.ENGLISH); }

    public enum Type
    {
        UNDER_BARREL,
        GRIP,
        BARREL,
        SIGHT,
        SPECIAL
    }
}
