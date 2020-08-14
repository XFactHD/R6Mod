package XFactHD.rssmc.common.data;

import net.minecraft.item.ItemStack;

import java.util.Locale;

public enum EnumBullet
{
    CASING_223_REM   ("Casing_.223_Rem"),
    CASING_357MAGNUM ("Casing_.357_Magnum"),
    CASING_357SIG    ("Casing_.357_SIG"),
    CASING_380ACP    ("Casing_.380_ACP"),
    CASING_40SW      ("Casing_.40_SW"),
    CASING_45ACP     ("Casing_.45_ACP"),
    CASING_50AE      ("Casing_.50_AE"),
    CASING_9x19      ("Casing_9x19mm"),
    CASING_46x30     ("Casing_4.6x30mm"),
    CASING_554x39    ("Casing_5.54x39mm"),
    CASING_556x45    ("Casing_5.56x45mm_NATO"),
    CASING_57x28     ("Casing_5.7x28mm"),
    CASING_58x42     ("Casing_5.8x42mm"),
    CASING_762x39    ("Casing_7.62x39mm"),
    CASING_762x51    ("Casing_7.62x51mm"),
    CASING_762x54    ("Casing_7.62x54mm"),
    CASING_12GAUGE   ("Casing_12Gauge"),

    BULLET_223_REM   ("Bullet_.223_Rem"),
    BULLET_357MAGNUM ("Bullet_.357_Magnum"),
    BULLET_357SIG    ("Bullet_.357_SIG"),
    BULLET_380ACP    ("Bullet_.380_ACP"),
    BULLET_40SW      ("Bullet_.40_SW"),
    BULLET_45ACP     ("Bullet_.45_ACP"),
    BULLET_50AE      ("Bullet_.50_AE"),
    BULLET_9x19      ("Bullet_9x19mm"),
    BULLET_46x30     ("Bullet_4.6x30mm"),
    BULLET_554x39    ("Bullet_5.54x39mm"),
    BULLET_556x45    ("Bullet_5.56x45mm_NATO"),
    BULLET_57x28     ("Bullet_5.7x28mm"),
    BULLET_58x42     ("Bullet_5.8x42mm"),
    BULLET_762x39    ("Bullet_7.62x39mm"),
    BULLET_762x51    ("Bullet_7.62x51mm"),
    BULLET_762x54    ("Bullet_7.62x54mm"),
    BULLET_12GAUGE   ("Bullet_12Gauge"),

    CARTRIDGE_223_REM   ("Round_.223_Rem", ".223 Remington"),
    CARTRIDGE_357MAGNUM ("Round_.357_Magnum", ".357 Magnum"),
    CARTRIDGE_357SIG    ("Round_.357_SIG", ".357 SIG"),
    CARTRIDGE_380ACP    ("Round_.380_ACP", ".308 ACP"),
    CARTRIDGE_40SW      ("Round_.40_SW", ".40 S&W"),
    CARTRIDGE_45ACP     ("Round_.45_ACP", ".45 ACP"),
    CARTRIDGE_50AE      ("Round_.50_AE", ".50 AE"),
    CARTRIDGE_9x19      ("Round_9x19mm", "9x19mm"),
    CARTRIDGE_46x30     ("Round_4.6x30mm", "4.6x30mm"),
    CARTRIDGE_554x39    ("Round_5.54x39mm", "5.54x39mm"),
    CARTRIDGE_556x45    ("Round_5.56x45mm_NATO", "5.56x45mm"),
    CARTRIDGE_57x28     ("Round_5.7x28mm", "5.7x28mm"),
    CARTRIDGE_58x42     ("Round_5.8x42mm", "5.8x42mm"),
    CARTRIDGE_762x39    ("Round_7.62x39mm", "7.62x39mm"),
    CARTRIDGE_762x51    ("Round_7.62x51mm", "7.62x51mm"),
    CARTRIDGE_762x54    ("Round_7.62x54mm", "7.62x54mm"),
    CARTRIDGE_12GAUGE   ("Round_12Gauge", "12 Gauge");

    private String name;
    private String caliberName;

    EnumBullet(String name)
    {
        this.name = name;
    }

    EnumBullet(String name, String caliberName)
    {
        this(name);
        this.caliberName = caliberName;
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

    public String getCaliberName()
    {
        return caliberName;
    }
}