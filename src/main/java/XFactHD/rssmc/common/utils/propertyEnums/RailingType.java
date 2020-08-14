package XFactHD.rssmc.common.utils.propertyEnums;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum RailingType implements IStringSerializable
{
    NORMAL,
    ROUND,
    CORNER,
    TOP,
    TB,
    BOTTOM;

    @Override
    public String getName()
    {
        return toString().toLowerCase(Locale.ENGLISH);
    }
}
