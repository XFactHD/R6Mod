package xfacthd.r6mod.common.data.itemsubtypes;

import java.util.Locale;

public enum EnumGadgetAmmo
{
    BREACH_GRENADE(2),
    CROSSBOW_BOLT_FIRE(2),
    CROSSBOW_BOLT_SMOKE(2),
    X_KAIROS_CHARGE(3),
    LIFELINE_GRENADE_IMPACT(2),
    LIFELINE_GRENADE_CONCUSSION(2),
    AIRJAB_CHARGE(3),
    LV_LANCE_CHARGE(3),
    STIM_DART(4),
    PEST(3),
    SHUMIKHA_GRENADE(10);

    private final int stackSize;

    EnumGadgetAmmo(int stackSize)
    {
        this.stackSize = stackSize;
    }

    public int getStackSize() { return stackSize; }

    public String toItemName() { return "item_gadgetammo_" + toString().toLowerCase(Locale.ENGLISH); }
}