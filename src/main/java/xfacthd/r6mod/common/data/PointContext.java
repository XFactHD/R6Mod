package xfacthd.r6mod.common.data;

import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;

import java.util.Locale;

public enum PointContext
{
    //Main point contexts
    KILL,
    INJURE,
    REVIVE,
    TEAM_DAMAGE,
    GADGET_USE,
    GADGET_DISABLE,
    GADGET_DESTROY,

    //Extra points contexts
    REPELLING,
    SILENCED,
    GADGET;

    public String translation() { return "gui.r6mod.points." + toString().toLowerCase(Locale.ENGLISH); }

    public String translation(EnumGadget gadget) { return translation() + "." + gadget.getObjectName(); }

    public boolean needsGadget() { return this == GADGET_USE || this == GADGET_DISABLE || this == GADGET_DESTROY || this == GADGET; }
}