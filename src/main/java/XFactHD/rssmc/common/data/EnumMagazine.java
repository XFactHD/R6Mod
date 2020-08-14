/*  Copyright (C) <2017>  <XFactHD>

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

package XFactHD.rssmc.common.data;

import net.minecraft.item.ItemStack;

import java.util.Locale;

public enum EnumMagazine//TODO: look up calibers and mag capacity, make all textures with equal distance to top edge
{
    MAG_C8_SFW(null, 30, true, false),
    MAG_L85A2(EnumBullet.ROUND_556x45, 30, true, false),
    MAG_AR33(null, 25, true, false),
    MAG_SIG_556xi(null, 30, true, false),
    MAG_G36_C(null, 30, true, false),
    MAG_R4_C(null, 30, true, false),
    MAG_FAMAS_F2(null, 30, true, false),
    MAG_AK12(null, 30, true, false),
    MAG_HK_416_C(null, 30, true, false),
    MAG_SIG_552_C(null, 30, true, false),
    MAG_AUG_A2(EnumBullet.ROUND_556x45, 30, true, false),
    MAG_Mk17_CQB(null, 20, true, false),
    MAG_PARA_308(null, 30, true, false),
    MAG_TYPE_89(null, 20, true, false),
    MAG_C7E(null, 30, true, false),

    MAG_HK417(null, 10, true, false),
    MAG_OTS_03(null, 10, true, false),
    MAG_CAMRS(null, 20, true, false),
    MAG_SR25(null, 20, true, false),

    MAG_G8A1(null, 50, true, true),
    MAG_PKP_6P41(null, 100, false, true),
    MAG_M249(null, 100, false, true),
    MAG_T_95_LSW(null, 80, true, true),

    MAG_SASG_12(EnumBullet.ROUND_12GAUGE, 10, true, false),
    MAG_SPAS_15(EnumBullet.ROUND_12GAUGE, 7, true, false),
    MAG_SIX12(EnumBullet.ROUND_12GAUGE, 6, false, true),
    MAG_F0_12(EnumBullet.ROUND_12GAUGE, 10, true, false),

    MAG_K9X19VSN(EnumBullet.ROUND_9x19, 30, true, false),
    MAG_FMG9(null, 30, true, false),
    MAG_MP5K(null, 30, true, false),
    MAG_MP5(null, 30, true, false),
    MAG_MP7(null, 30, true, false),
    MAG_P90(EnumBullet.ROUND_57x28, 50, true, false),
    MAG_STEN_9MM_C1(EnumBullet.ROUND_9x19, 34, true, false),
    MAG_UMP45(null, 25, true, false),
    MAG_MPX(null, 30, true, false),
    MAG_M12(null, 30, true, false),
    MAG_MP5SD(null, 30, true, false),
    MAG_SMG11(null, 15, true, false),
    MAG_BEARING_9(null, 25, true, false),
    MAG_VECTOR_45(EnumBullet.ROUND_45ACP, 25, true, false),
    MAG_PDW9(EnumBullet.BULLET_9x19, 50, true, true),
    MAG_T5_SMG(null, 30, true, false),
    MAG_SKORPION_EVO(null, 50, true, false),

    MAG_MK1_9MM(EnumBullet.ROUND_9x19, 30, true, false),
    MAG_P226_MK25(null, 30, true, false),
    MAG_M45_M(EnumBullet.ROUND_45ACP, 7, true, false),
    MAG_FN_57_USG(EnumBullet.ROUND_57x28, 30, true, false),
    MAG_P12(null, 30, true, false),
    MAG_P9(null, 30, true, false),
    MAG_PMM(null, 30, true, false),
    MAG_GSH_18(null, 30, true, false),
    MAG_DEAGLE(EnumBullet.ROUND_50AE, 30, true, false),
    MAG_PRB92(null, 30, true, false),
    MAG_P229(null, 12, true, false),
    MAG_USP40(null, 12, true, false),
    MAG_Q_929(null, 0, true, false),
    MAG_RG15(null, 0, true, false),

    MAG_BREACHSG(EnumBullet.ROUND_12GAUGE, 4, true, false);

    private EnumBullet bullet;
    private int magCap;
    private boolean canSpeedLoad;
    private boolean rotateInMagFiller;

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

    public static EnumMagazine valueOf(ItemStack stack)
    {
        return stack.getMetadata() < values().length ? values()[stack.getMetadata()] : values()[0];
    }

    public static String[] getAsStringArray()
    {
        String[] strings = new String[values().length];
        for (EnumMagazine magazine : values())
        {
            strings[magazine.ordinal()] = magazine.toString().toLowerCase(Locale.ENGLISH);
        }
        return strings;
    }
}