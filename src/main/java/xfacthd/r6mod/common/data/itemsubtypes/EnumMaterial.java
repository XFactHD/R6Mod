package xfacthd.r6mod.common.data.itemsubtypes;

import java.util.Locale;

public enum EnumMaterial
{
    INGOT_BRASS("forge:ingot/brass"),
    CASING(),
    PROJECTILE();

    private final String tagName;

    EnumMaterial() { this(""); }

    EnumMaterial(String tagName)
    {
        this.tagName = tagName;
    }

    public String toItemName()
    {
        return "item_material_" + toString().toLowerCase(Locale.ENGLISH);
    }

    public boolean hasTagName() { return !tagName.isEmpty(); }

    public String getTagName() { return tagName; }
}