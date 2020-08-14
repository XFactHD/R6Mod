package XFactHD.rssmc.common.data;

import net.minecraft.item.ItemStack;

import java.util.Locale;

public enum EnumBullet
{
    CASING_45ACP     ("Casing_.45_ACP"),
    CASING_380ACP    ("Casing_.380_ACP"),
    CASING_9x19      ("Casing_9x19mm"),
    CASING_50AE      ("Casing_.50_AE"),
    CASING_357MAGNUM ("Casing_.357_Magnum"),
    CASING_46x30     ("Casing_4.6x30mm"),
    CASING_556x45    ("Casing_5.56x45mm_NATO"),
    CASING_57x28     ("Casing_5.7x28mm"),
    CASING_762x51    ("Casing_7.62x51mm"),
    CASING_762x54    ("Casing_7.62x54mm"),
    CASING_12GAUGE   ("Casing_12Gauge"),
    BULLET_45ACP     ("Bullet_.45_ACP"),
    BULLET_380ACP    ("Bullet_.380_ACP"),
    BULLET_9x19      ("Bullet_9x19mm_Parabellum"),
    BULLET_50AE      ("Bullet_.50_AE"),
    BULLET_357MAGNUM ("Bullet_.357_Magnum"),
    BULLET_46x30     ("Bullet_4.6x30mm"),
    BULLET_556x45    ("Bullet_5.56x45mm_NATO"),
    BULLET_57x28     ("Bullet_5.7x28mm"),
    BULLET_762x51    ("Bullet_7.62x51mm"),
    BULLET_762x54    ("Bullet_7.62x54mm"),
    BULLET_12GAUGE   ("Bullet_12Gauge"),
    ROUND_45ACP     ("Round_.45_ACP"),
    ROUND_380ACP    ("Round_.380_ACP"),
    ROUND_9x19      ("Round_9x19mm_Parabellum"),
    ROUND_50AE      ("Round_.50_AE"),
    ROUND_357MAGNUM ("Round_.357_Magnum"),
    ROUND_46x30     ("Round_4.6x30mm"),
    ROUND_556x45    ("Round_5.56x45mm_NATO"),
    ROUND_57x28     ("Round_5.7x28mm"),
    ROUND_762x51    ("Round_7.62x51mm"),
    ROUND_762x54    ("Round_7.62x54mm"),
    ROUND_12GAUGE   ("Round_12Gauge");

    private String name;

    EnumBullet(String name)
    {
        this.name = name;
    }

    public static EnumBullet valueOf(ItemStack stack)
    {
        return stack.getMetadata() < values().length ? values()[stack.getMetadata()] : values()[0];
    }

    public static String[] getAsStringArray()
    {
        String[] strings = new String[values().length];
        for (EnumBullet bullet : values())
        {
            strings[bullet.ordinal()] = bullet.name.toLowerCase(Locale.ENGLISH);
        }
        return strings;
    }
}