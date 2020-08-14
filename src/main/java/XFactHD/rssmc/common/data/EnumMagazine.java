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
    MAG_C8_SFW(EnumBullet.CARTRIDGE_556x45, 30, true, false),
    MAG_L85A2(EnumBullet.CARTRIDGE_556x45, 30, true, false),
    MAG_AR33(EnumBullet.CARTRIDGE_556x45, 25, true, false),
    MAG_SIG_556xi(EnumBullet.CARTRIDGE_556x45, 30, true, false),
    MAG_G36_C(EnumBullet.CARTRIDGE_556x45, 30, true, false),
    MAG_R4_C(EnumBullet.CARTRIDGE_556x45, 30, true, false),
    MAG_FAMAS_F2(EnumBullet.CARTRIDGE_556x45, 30, true, false),
    MAG_AK12(EnumBullet.CARTRIDGE_554x39, 30, true, false),
    MAG_HK_416_C(EnumBullet.CARTRIDGE_556x45, 30, true, false),
    MAG_SIG_552_C(EnumBullet.CARTRIDGE_556x45, 30, true, false),
    MAG_AUG_A2(EnumBullet.CARTRIDGE_556x45, 30, true, false),
    MAG_Mk17_CQB(EnumBullet.CARTRIDGE_762x51, 20, true, false),
    MAG_PARA_308(EnumBullet.CARTRIDGE_762x51, 30, true, false),
    MAG_TYPE_89(EnumBullet.CARTRIDGE_556x45, 20, true, false),
    MAG_C7E(EnumBullet.CARTRIDGE_556x45, 30, true, false),
    MAG_M762(EnumBullet.CARTRIDGE_762x39, 30, true, false),

    MAG_HK417(EnumBullet.CARTRIDGE_762x51, 10, true, false),
    MAG_OTS_03(EnumBullet.CARTRIDGE_762x54, 10, true, false),
    MAG_CAMRS(EnumBullet.CARTRIDGE_762x51, 20, true, false),
    MAG_SR25(EnumBullet.CARTRIDGE_762x51, 20, true, false),
    MAG_Mk14_EBR(EnumBullet.CARTRIDGE_762x51, 20, true, false),

    MAG_G8A1(EnumBullet.CARTRIDGE_762x51, 50, true, true),
    MAG_PKP_6P41(EnumBullet.CARTRIDGE_762x54, 100, false, true),
    MAG_M249(EnumBullet.CARTRIDGE_556x45, 100, false, true),
    MAG_T_95_LSW(EnumBullet.CARTRIDGE_58x42, 80, true, true),
    MAG_LMG_E(EnumBullet.CARTRIDGE_556x45, 150, true, true),

    MAG_SASG_12(EnumBullet.CARTRIDGE_12GAUGE, 10, true, false),
    MAG_SPAS_15(EnumBullet.CARTRIDGE_12GAUGE, 7, true, false),
    MAG_SIX12(EnumBullet.CARTRIDGE_12GAUGE, 6, false, true),
    MAG_F0_12(EnumBullet.CARTRIDGE_12GAUGE, 10, true, false),

    MAG_K9X19VSN(EnumBullet.CARTRIDGE_9x19, 30, true, false),
    MAG_FMG9(EnumBullet.CARTRIDGE_9x19, 30, true, false),
    MAG_MP5K(EnumBullet.CARTRIDGE_9x19, 30, true, false),
    MAG_MP5(EnumBullet.CARTRIDGE_9x19, 30, true, false),
    MAG_MP7(EnumBullet.CARTRIDGE_46x30, 30, true, false),
    MAG_P90(EnumBullet.CARTRIDGE_57x28, 50, true, false),
    MAG_STEN_9MM_C1(EnumBullet.CARTRIDGE_9x19, 34, true, false),
    MAG_UMP45(EnumBullet.CARTRIDGE_45ACP, 25, true, false),
    MAG_MPX(EnumBullet.CARTRIDGE_9x19, 30, true, false),
    MAG_M12(EnumBullet.CARTRIDGE_9x19, 30, true, false),
    MAG_MP5SD(EnumBullet.CARTRIDGE_9x19, 30, true, false),
    MAG_SMG11(EnumBullet.CARTRIDGE_380ACP, 15, true, false),
    MAG_BEARING_9(EnumBullet.CARTRIDGE_9x19, 25, true, false),
    MAG_VECTOR_45(EnumBullet.CARTRIDGE_45ACP, 25, true, false),
    MAG_PDW9(EnumBullet.CARTRIDGE_9x19, 50, true, true),
    MAG_T5_SMG(EnumBullet.CARTRIDGE_9x19, 30, true, false),
    MAG_SKORPION_EVO(EnumBullet.CARTRIDGE_9x19, 50, true, false),
    MAG_K1A(EnumBullet.CARTRIDGE_223_REM, 30, true, false),
    MAG_SMG12(EnumBullet.CARTRIDGE_9x19, 32, true, false),
    MAG_C75_AUTO(EnumBullet.CARTRIDGE_9x19, 26, true, false),

    MAG_MK1_9MM(EnumBullet.CARTRIDGE_9x19, 30, true, false),
    MAG_P226_MK25(EnumBullet.CARTRIDGE_9x19, 30, true, false),
    MAG_M45_M(EnumBullet.CARTRIDGE_45ACP, 7, true, false),
    MAG_FN_57_USG(EnumBullet.CARTRIDGE_57x28, 30, true, false),
    MAG_P12(EnumBullet.CARTRIDGE_45ACP, 30, true, false),
    MAG_P9(EnumBullet.CARTRIDGE_9x19, 30, true, false),
    MAG_PMM(EnumBullet.CARTRIDGE_9x19, 30, true, false),
    MAG_GSH_18(EnumBullet.CARTRIDGE_9x19, 30, true, false),
    MAG_DEAGLE(EnumBullet.CARTRIDGE_50AE, 30, true, false),
    MAG_PRB92(EnumBullet.CARTRIDGE_9x19, 30, true, false),
    MAG_P229(EnumBullet.CARTRIDGE_357SIG, 12, true, false),
    MAG_USP40(EnumBullet.CARTRIDGE_40SW, 12, true, false),
    MAG_Q_929(EnumBullet.CARTRIDGE_9x19, 0, true, false),
    MAG_RG15(EnumBullet.CARTRIDGE_9x19, 0, true, false),

    MAG_BREACHSG(EnumBullet.CARTRIDGE_12GAUGE, 4, true, false);

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