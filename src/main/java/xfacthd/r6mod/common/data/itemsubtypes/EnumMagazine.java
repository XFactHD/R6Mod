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

import java.util.Locale;

public enum EnumMagazine
{
    NONE(null, 0, false, false),

    MAG_L85A2       (EnumBullet.BULLET_556x45,        30, true,  false),
    MAG_AR33        (EnumBullet.BULLET_556x45,        25, true,  false),
    MAG_G36_C       (EnumBullet.BULLET_556x45,        30, true,  false),
    MAG_R4_C        (EnumBullet.BULLET_556x45,        30, true,  false),
    MAG_SIG_556XI   (EnumBullet.BULLET_556x45,        30, true,  false),
    MAG_FAMAS_F2    (EnumBullet.BULLET_556x45,        25, true,  false),
    MAG_AK12        (EnumBullet.BULLET_545x39,        30, true,  false),
    MAG_AUG_A2      (EnumBullet.BULLET_556x45,        30, true,  false),
    MAG_SIG_552_C   (EnumBullet.BULLET_556x45,        30, true,  false),
    MAG_HK_416_C    (EnumBullet.BULLET_556x45,        30, true,  false),
    MAG_C8_SFW      (EnumBullet.BULLET_556x45,        30, true,  false),
    MAG_MK17_CQB    (EnumBullet.BULLET_762x51,        20, true,  false),
    MAG_PARA_308    (EnumBullet.BULLET_762x51,        30, true,  false),
    MAG_TYPE_89     (EnumBullet.BULLET_556x45,        20, true,  false),
    MAG_C7E         (EnumBullet.BULLET_556x45,        30, true,  false),
    MAG_M762        (EnumBullet.BULLET_762x39,        30, true,  false),
    MAG_V308        (EnumBullet.BULLET_762x51,        50, true,  false),
    MAG_SPEAR_308   (EnumBullet.BULLET_762x51,        30, true,  false),
    MAG_COLT_M4     (EnumBullet.BULLET_556x45,        30, true,  false),
    MAG_AK_74M      (EnumBullet.BULLET_545x39,        40, true,  false),
    MAG_ARX200      (EnumBullet.BULLET_762x51,        20, true,  false),
    MAG_F90         (EnumBullet.BULLET_556x45,        30, true,  false),

    MAG_HK417       (EnumBullet.BULLET_762x51,        10, true,  false),
    MAG_OTS_03      (EnumBullet.BULLET_762x54,        10, true,  false),
    MAG_CAMRS       (EnumBullet.BULLET_762x51,        20, true,  false),
    MAG_SR25        (EnumBullet.BULLET_762x51,        20, true,  false),
    MAG_MK14_EBR    (EnumBullet.BULLET_762x51,        20, true,  false),
    MAG_AR15_50     (EnumBullet.BULLET_50_BEOWULF,    10, true,  false),
    MAG_CSRX_300    (EnumBullet.BULLET_300WINMAG,      5, true,  false),

    MAG_PKP_6P41    (EnumBullet.BULLET_762x54,       100, false, true),
    MAG_G8A1        (EnumBullet.BULLET_556x45,        50, true,  true),
    MAG_M249        (EnumBullet.BULLET_556x45,       100, false, true),
    MAG_T_95_LSW    (EnumBullet.BULLET_556x45,        80, true,  true),
    MAG_LMG_E       (EnumBullet.BULLET_556x45,       150, false, true),
    MAG_ALDA_556    (EnumBullet.BULLET_556x45,        80, false, true),
    MAG_M249_SAW    (EnumBullet.BULLET_556x45,        60, false, true),

    MAG_SASG_12     (EnumBullet.BULLET_12GAUGE,       10, true,  false),
    MAG_SPAS_15     (EnumBullet.BULLET_12GAUGE,        7, true,  false),
    MAG_SIX12       (EnumBullet.BULLET_12GAUGE,        6, false, true),
    MAG_FO_12       (EnumBullet.BULLET_12GAUGE,       10, true,  false),
    MAG_ACS12       (EnumBullet.BULLET_12GAUGE_SLUG,  30, true,  true),
    MAG_TCSG12      (EnumBullet.BULLET_12GAUGE_SLUG,  10, true,  false),

    MAG_FMG9        (EnumBullet.BULLET_9x19,          30, true,  false),
    MAG_MP5K        (EnumBullet.BULLET_9x19,          30, true,  false),
    MAG_SMG11       (EnumBullet.BULLET_45ACP,         15, true,  false),
    MAG_UMP45       (EnumBullet.BULLET_45ACP,         25, true,  false),
    MAG_MP5         (EnumBullet.BULLET_9x19,          30, true,  false),
    MAG_P90         (EnumBullet.BULLET_57x28,         50, true,  false),
    MAG_K9X19VSN    (EnumBullet.BULLET_9x19,          30, true,  false),
    MAG_MP7         (EnumBullet.BULLET_46x30,         30, true,  false),
    MAG_STEN_9MM_C1 (EnumBullet.BULLET_9x19,          34, true,  false),
    MAG_MPX         (EnumBullet.BULLET_9x19,          30, true,  false),
    MAG_M12         (EnumBullet.BULLET_9x19,          30, true,  false),
    MAG_MP5SD       (EnumBullet.BULLET_9x19,          30, true,  false),
    MAG_BEARING_9   (EnumBullet.BULLET_9x19,          25, true,  false),
    MAG_VECTOR_45   (EnumBullet.BULLET_45ACP,         25, true,  false),
    MAG_PDW9        (EnumBullet.BULLET_9x19,          50, true,  true),
    MAG_T5_SMG      (EnumBullet.BULLET_9x19,          30, true,  false),
    MAG_SKORPION_EVO(EnumBullet.BULLET_9x19,          40, true,  false),
    MAG_K1A         (EnumBullet.BULLET_556x45,        30, true,  false),
    MAG_C75_AUTO    (EnumBullet.BULLET_9x19,          26, true,  false),
    MAG_SMG12       (EnumBullet.BULLET_9x19,          32, true,  false),
    MAG_MX4_STORM   (EnumBullet.BULLET_9x19,          30, true,  false),
    MAG_SPSMG9      (EnumBullet.BULLET_9x19,          20, true,  false),
    MAG_AUG_A3      (EnumBullet.BULLET_9x19,          31, true,  false),
    MAG_P10_RONI    (EnumBullet.BULLET_9x19,          19, true,  false),
    MAG_COMMANDO9   (EnumBullet.BULLET_9x19,          25, true,  false),

    MAG_P226_MK25   (EnumBullet.BULLET_9x19,          30, true,  false),
    MAG_M45_M       (EnumBullet.BULLET_45ACP,          7, true,  false),
    MAG_FN_57_USG   (EnumBullet.BULLET_57x28,         20, true,  false),
    MAG_P9          (EnumBullet.BULLET_9x19,          16, true,  false),
    MAG_GSH_18      (EnumBullet.BULLET_9x19,          18, true,  false),
    MAG_PMM         (EnumBullet.BULLET_9x18_MAK,       8, true,  false),
    MAG_P12         (EnumBullet.BULLET_45ACP,         15, true,  false),
    MAG_MK1_9MM     (EnumBullet.BULLET_9x19,          13, true,  false),
    MAG_D_50        (EnumBullet.BULLET_50AE,           7, true,  false),
    MAG_PRB92       (EnumBullet.BULLET_9x19,          15, true,  false),
    MAG_LUISON      (EnumBullet.BULLET_9x19,          12, true,  false),
    MAG_P229        (EnumBullet.BULLET_357_SIG,       12, true,  false),
    MAG_USP40       (EnumBullet.BULLET_44_SW,         12, true,  false),
    MAG_Q_929       (EnumBullet.BULLET_9x19,          10, true,  false),
    MAG_RG15        (EnumBullet.BULLET_9x19,          15, true,  false),
    MAG_M1911       (EnumBullet.BULLET_45ACP,          8, true,  false),
    MAG_P10_C       (EnumBullet.BULLET_9x19,          15, true,  false),
    MAG_AMP_44_MAG  (EnumBullet.BULLET_44_AMP,         7, true,  false),
    MAG_SDP_9       (EnumBullet.BULLET_9x19,          16, true,  false),

    MAG_BREACHSG(EnumBullet.BULLET_12GAUGE, 5, true, false);

    private final EnumBullet bullet;
    private final int magCap;
    private final boolean canSpeedLoad;
    private final boolean rotateInMagFiller;

    EnumMagazine(EnumBullet bullet, int magCap, boolean canSpeedLoad, boolean rotateInMagFiller)
    {
        this.bullet = bullet;
        this.magCap = magCap;
        this.canSpeedLoad = canSpeedLoad;
        this.rotateInMagFiller = rotateInMagFiller;
    }

    public EnumBullet getBullet()
    {
        return bullet;
    }

    public int getMagCap()
    {
        return magCap;
    }

    public boolean canSpeedLoad()
    {
        return canSpeedLoad;
    }

    public boolean rotateInMagFiller()
    {
        return rotateInMagFiller;
    }

    public String toItemName() { return "item_" + toString().toLowerCase(Locale.ENGLISH).replaceFirst("mag", "magazine"); }
}