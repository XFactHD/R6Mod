package xfacthd.r6mod.common.data.itemsubtypes;

import net.minecraft.util.text.TranslationTextComponent;

import java.util.Locale;

public enum EnumBullet
{
    BULLET_45ACP       (".45 ACP"),
    BULLET_380ACP      (".380 ACP"),
    BULLET_9x19        ("9x19mm Parabellum"),
    BULLET_9x18_MAK    ("9x18 Makarov"),
    BULLET_50AE        (".50 AE"),
    BULLET_357MAGNUM   (".357 Magnum"),
    BULLET_300WINMAG   (".357 WinMag"),
    BULLET_44_AMP      (".44 AMP"),
    BULLET_357_SIG     (".357 SIG"),
    BULLET_44_SW       (".44 S&W"),
    BULLET_46x30       ("4.6x30mm"),
    BULLET_545x39      ("5.45x39mm"),
    BULLET_556x45      ("5.56x45mm NATO"),
    BULLET_57x28       ("5.7x28mm"),
    BULLET_762x39      ("7.62x39mm"),
    BULLET_762x51      ("7.62x51mm"),
    BULLET_762x54      ("7.62x54mm"),
    BULLET_50_BEOWULF  (".50 Beowulf"),
    BULLET_12GAUGE     ("12Gauge"),
    BULLET_12GAUGE_SLUG("12Gauge Slug");

    private final String name;

    EnumBullet(String name) { this.name = name; }

    public String getName() { return name; }

    public String toItemName() { return "item_ammo_" + toString().toLowerCase(Locale.ENGLISH); }

    public String toCaliberName() { return new TranslationTextComponent("item.r6mod." + toItemName()).getString(); }
}