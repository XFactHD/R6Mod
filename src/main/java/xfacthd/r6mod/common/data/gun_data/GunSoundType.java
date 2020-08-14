package xfacthd.r6mod.common.data.gun_data;

import java.util.Locale;

public enum GunSoundType
{
    //Types used by almost all guns
    FIRE,
    LOCK_BACK,
    EMPTY_TRIGGER,
    CHARGE,

    //Types used by guns with magazines
    MAG_OUT,
    MAG_IN,

    //Types used by guns without magazines
    OPEN_BREACH,
    LOAD_FIRST,
    LOAD,
    CLOSE_BREACH;

    public String getName() { return toString().toLowerCase(Locale.ENGLISH); }
}