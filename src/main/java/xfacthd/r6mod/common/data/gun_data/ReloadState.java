package xfacthd.r6mod.common.data.gun_data;

public enum ReloadState
{
    //Not reloading
    NONE(null),

    //Reloading gun with mag
    MAG_OUT(GunSoundType.MAG_OUT),
    MAG_IN(GunSoundType.MAG_IN),
    CHAMBER(GunSoundType.CHARGE),

    //Reloading gun without mag
    OPEN_BREACH(GunSoundType.OPEN_BREACH),
    LOAD(GunSoundType.LOAD),
    CLOSE_BREACH(GunSoundType.CLOSE_BREACH),
    LOAD_FIRST(GunSoundType.LOAD_FIRST); //Used by magless shotguns to put the first bullet directly into the chamber

    private final GunSoundType sound;

    ReloadState(GunSoundType sound) { this.sound = sound; }

    //If true, reloading does not need to be resumed at this state
    public boolean canAbort() { return this == MAG_IN || this == MAG_OUT || this == OPEN_BREACH || this == LOAD; }

    public GunSoundType getSound() { return sound; }
}