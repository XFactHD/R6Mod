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

import XFactHD.rssmc.common.utils.Reference;
import net.minecraft.util.ResourceLocation;

import java.util.Locale;

public enum EnumOperator
{
    MUTE      ("Mute",     EnumSide.DEFFENDER, EnumArmorLevel.MEDIUM, new EnumGun[]{EnumGun.MP5K, EnumGun.M590A1},                       new EnumGun[]{EnumGun.P226_MK25},                new EnumGadget[]{EnumGadget.NITRO_CELL, EnumGadget.DEPLOYABLE_SHIELD},     EnumGadget.JAMMER),
    SMOKE     ("Smoke",    EnumSide.DEFFENDER, EnumArmorLevel.MEDIUM, new EnumGun[]{EnumGun.FMG9, EnumGun.M590A1},                       new EnumGun[]{EnumGun.P226_MK25, EnumGun.SMG11}, new EnumGadget[]{EnumGadget.IMPACT_GRENADE, EnumGadget.BARBED_WIRE},       EnumGadget.GAS_GRENADE),
    PULSE     ("Pulse",    EnumSide.DEFFENDER, EnumArmorLevel.LIGHT,  new EnumGun[]{EnumGun.UMP45, EnumGun.M1014},                       new EnumGun[]{EnumGun.M45_M, EnumGun.FN_57_USG}, new EnumGadget[]{EnumGadget.NITRO_CELL, EnumGadget.BARBED_WIRE},           EnumGadget.CARDIAC_SENSOR),
    CASTLE    ("Castle",   EnumSide.DEFFENDER, EnumArmorLevel.MEDIUM, new EnumGun[]{EnumGun.UMP45, EnumGun.M1014},                       new EnumGun[]{EnumGun.M45_M, EnumGun.FN_57_USG}, new EnumGadget[]{EnumGadget.IMPACT_GRENADE, EnumGadget.BARBED_WIRE},       EnumGadget.TOUGH_BARICADE),
    ROOK      ("Rook",     EnumSide.DEFFENDER, EnumArmorLevel.HEAVY,  new EnumGun[]{EnumGun.MP5, EnumGun.P90},                           new EnumGun[]{EnumGun.P9, EnumGun.LFP586},       new EnumGadget[]{EnumGadget.IMPACT_GRENADE, EnumGadget.DEPLOYABLE_SHIELD}, EnumGadget.ARMOR_BAG),
    DOC       ("Doc",      EnumSide.DEFFENDER, EnumArmorLevel.HEAVY,  new EnumGun[]{EnumGun.MP5, EnumGun.P90},                           new EnumGun[]{EnumGun.P9, EnumGun.LFP586},       new EnumGadget[]{EnumGadget.DEPLOYABLE_SHIELD, EnumGadget.BARBED_WIRE},    EnumGadget.STIM_PISTOL),
    TACHANKA  ("Tachanka", EnumSide.DEFFENDER, EnumArmorLevel.HEAVY,  new EnumGun[]{EnumGun.K9X19VSN, EnumGun.SASG_12},                  new EnumGun[]{EnumGun.GSH_18, EnumGun.PMM},      new EnumGadget[]{EnumGadget.DEPLOYABLE_SHIELD, EnumGadget.BARBED_WIRE},    EnumGadget.MOUNTED_LMG),
    KAPKAN    ("Kapkan",   EnumSide.DEFFENDER, EnumArmorLevel.HEAVY,  new EnumGun[]{EnumGun.K9X19VSN, EnumGun.SASG_12},                  new EnumGun[]{EnumGun.GSH_18, EnumGun.PMM},      new EnumGadget[]{EnumGadget.NITRO_CELL, EnumGadget.BARBED_WIRE},           EnumGadget.KAPKAN_TRAP),
    JAEGER    ("Jäger",    EnumSide.DEFFENDER, EnumArmorLevel.LIGHT,  new EnumGun[]{EnumGun.HK_416_C, EnumGun.M870},                     new EnumGun[]{EnumGun.P12},                      new EnumGadget[]{EnumGadget.BARBED_WIRE, EnumGadget.DEPLOYABLE_SHIELD},    EnumGadget.ACTIVE_DEFENSE_SYSTEM),
    BANDIT    ("Bandit",   EnumSide.DEFFENDER, EnumArmorLevel.LIGHT,  new EnumGun[]{EnumGun.MP7, EnumGun.M870},                          new EnumGun[]{EnumGun.P12},                      new EnumGadget[]{EnumGadget.NITRO_CELL, EnumGadget.BARBED_WIRE},           EnumGadget.SHOCK_WIRE),
    FROST     ("Frost",    EnumSide.DEFFENDER, EnumArmorLevel.MEDIUM, new EnumGun[]{EnumGun.STEN_9MM_C1, EnumGun.SUPER_90},              new EnumGun[]{EnumGun.MK1_9MM},                  new EnumGadget[]{EnumGadget.DEPLOYABLE_SHIELD, EnumGadget.BARBED_WIRE},    EnumGadget.WELCOME_MAT),
    VALKYRIE  ("Valkyrie", EnumSide.DEFFENDER, EnumArmorLevel.MEDIUM, new EnumGun[]{EnumGun.MPX, EnumGun.SPAS_12},                       new EnumGun[]{EnumGun.DEAGLE},                   new EnumGadget[]{EnumGadget.NITRO_CELL, EnumGadget.DEPLOYABLE_SHIELD},     EnumGadget.STICKY_CAMERA),
    CAVEIRA   ("Caveira",  EnumSide.DEFFENDER, EnumArmorLevel.LIGHT,  new EnumGun[]{EnumGun.M12, EnumGun.SPAS_15},                       new EnumGun[]{EnumGun.LUISON},                   new EnumGadget[]{EnumGadget.IMPACT_GRENADE, EnumGadget.BARBED_WIRE},       EnumGadget.INTEROGATION_KNIVE),
    ECHO      ("Echo",     EnumSide.DEFFENDER, EnumArmorLevel.HEAVY,  new EnumGun[]{EnumGun.MP5SD, EnumGun.SUPERNOVA},                   new EnumGun[]{EnumGun.P229, EnumGun.BEARING_9},  new EnumGadget[]{EnumGadget.BARBED_WIRE, EnumGadget.DEPLOYABLE_SHIELD},    EnumGadget.YOKAI_DRONE),
    MIRA      ("Mira",     EnumSide.DEFFENDER, EnumArmorLevel.HEAVY,  new EnumGun[]{EnumGun.VECTOR_45, EnumGun.IPS12L},                  new EnumGun[]{EnumGun.IPS12S, EnumGun.USP40},    new EnumGadget[]{EnumGadget.NITRO_CELL, EnumGadget.DEPLOYABLE_SHIELD},     EnumGadget.BLACK_MIRROR),
    LESION    ("Lesion",   EnumSide.DEFFENDER, EnumArmorLevel.MEDIUM, new EnumGun[]{EnumGun.T5_SMG, EnumGun.SIX12SD},                    new EnumGun[]{EnumGun.Q_929},                    new EnumGadget[]{EnumGadget.IMPACT_GRENADE, EnumGadget.DEPLOYABLE_SHIELD}, EnumGadget.GU_MINE),
    ELA       ("Ela",      EnumSide.DEFFENDER, EnumArmorLevel.LIGHT,  new EnumGun[]{EnumGun.SKORPION_EVO, EnumGun.F0_12},                new EnumGun[]{EnumGun.RG15},                     new EnumGadget[]{EnumGadget.IMPACT_GRENADE, EnumGadget.BARBED_WIRE},       EnumGadget.GRZMOT_MINE),
    VIGIL     ("Vigil",    EnumSide.DEFFENDER, EnumArmorLevel.LIGHT,  new EnumGun[]{EnumGun.K1A, EnumGun.BOSG_12_2},                     new EnumGun[]{EnumGun.SMG12, EnumGun.C75_AUTO},  new EnumGadget[]{EnumGadget.BARBED_WIRE, EnumGadget.IMPACT_GRENADE},       null),

    SLEDGE    ("Sledge",     EnumSide.ATTACKER, EnumArmorLevel.MEDIUM, new EnumGun[]{EnumGun.L85A2, EnumGun.M590A1},                          new EnumGun[]{EnumGun.P226_MK25, EnumGun.SMG11}, new EnumGadget[]{EnumGadget.FRAG_GRENADE, EnumGadget.STUN_GRENADE},        EnumGadget.SLEDGE_HAMMER),
    THATCHER  ("Thatcher",   EnumSide.ATTACKER, EnumArmorLevel.MEDIUM, new EnumGun[]{EnumGun.L85A2, EnumGun.AR33, EnumGun.M590A1},            new EnumGun[]{EnumGun.P226_MK25},                new EnumGadget[]{EnumGadget.BREACH_CHARGE, EnumGadget.CLAYMORE},           EnumGadget.EMP_GRENADE),
    THERMITE  ("Thermite",   EnumSide.ATTACKER, EnumArmorLevel.MEDIUM, new EnumGun[]{EnumGun.SIG_556xi, EnumGun.M1014},                       new EnumGun[]{EnumGun.M45_M, EnumGun.FN_57_USG}, new EnumGadget[]{EnumGadget.CLAYMORE, EnumGadget.SMOKE_GRENADE},           EnumGadget.THERMITE_CHARGE),
    ASH       ("Ash",        EnumSide.ATTACKER, EnumArmorLevel.LIGHT,  new EnumGun[]{EnumGun.G36_C, EnumGun.R4_C},                            new EnumGun[]{EnumGun.M45_M, EnumGun.FN_57_USG}, new EnumGadget[]{EnumGadget.BREACH_CHARGE, EnumGadget.STUN_GRENADE},       EnumGadget.GRENADE_LAUNCHER),
    MONTAGNE  ("Montagne",   EnumSide.ATTACKER, EnumArmorLevel.HEAVY,  new EnumGun[]{EnumGun.EXTENDABLE_SHIELD},                              new EnumGun[]{EnumGun.P9, EnumGun.LFP586},       new EnumGadget[]{EnumGadget.STUN_GRENADE, EnumGadget.SMOKE_GRENADE},       null),
    TWITCH    ("Twitch",     EnumSide.ATTACKER, EnumArmorLevel.MEDIUM, new EnumGun[]{EnumGun.FAMAS_F2, EnumGun.HK417, EnumGun.SG_CQB},        new EnumGun[]{EnumGun.P9, EnumGun.LFP586},       new EnumGadget[]{EnumGadget.CLAYMORE, EnumGadget.BREACH_CHARGE},           EnumGadget.SHOCK_DRONE),
    FUZE      ("Fuze",       EnumSide.ATTACKER, EnumArmorLevel.HEAVY,  new EnumGun[]{EnumGun.AK12, EnumGun.RUSSIAN_SHIELD, EnumGun.PKP_6P41}, new EnumGun[]{EnumGun.PMM, EnumGun.GSH_18},      new EnumGadget[]{EnumGadget.BREACH_CHARGE, EnumGadget.SMOKE_GRENADE},      EnumGadget.CLUSTER_CHARGE),
    GLAZ      ("Glaz",       EnumSide.ATTACKER, EnumArmorLevel.MEDIUM, new EnumGun[]{EnumGun.OTS_03},                                         new EnumGun[]{EnumGun.PMM, EnumGun.GSH_18},      new EnumGadget[]{EnumGadget.SMOKE_GRENADE, EnumGadget.CLAYMORE},           null),
    BLITZ     ("Blitz",      EnumSide.ATTACKER, EnumArmorLevel.HEAVY,  new EnumGun[]{EnumGun.FLASH_SHIELD},                                   new EnumGun[]{EnumGun.P12},                      new EnumGadget[]{EnumGadget.SMOKE_GRENADE, EnumGadget.BREACH_CHARGE},      null),
    IQ        ("IQ",         EnumSide.ATTACKER, EnumArmorLevel.LIGHT,  new EnumGun[]{EnumGun.AUG_A2, EnumGun.SIG_552_C, EnumGun.G8A1},        new EnumGun[]{EnumGun.P12},                      new EnumGadget[]{EnumGadget.BREACH_CHARGE, EnumGadget.FRAG_GRENADE},       EnumGadget.ELECTRONICS_DETECTOR),
    BUCK      ("Buck",       EnumSide.ATTACKER, EnumArmorLevel.MEDIUM, new EnumGun[]{EnumGun.C8_SFW, EnumGun.CAMRS},                          new EnumGun[]{EnumGun.MK1_9MM},                  new EnumGadget[]{EnumGadget.FRAG_GRENADE, EnumGadget.STUN_GRENADE},        null),
    BLACKBEARD("Blackbeard", EnumSide.ATTACKER, EnumArmorLevel.MEDIUM, new EnumGun[]{EnumGun.Mk17_CQB, EnumGun.SR25},                         new EnumGun[]{EnumGun.DEAGLE},                   new EnumGadget[]{EnumGadget.BREACH_CHARGE, EnumGadget.STUN_GRENADE},       null),
    CAPITAO   ("Capitao",    EnumSide.ATTACKER, EnumArmorLevel.LIGHT,  new EnumGun[]{EnumGun.PARA_308, EnumGun.M249},                         new EnumGun[]{EnumGun.PRB92},                    new EnumGadget[]{EnumGadget.CLAYMORE, EnumGadget.STUN_GRENADE},            EnumGadget.CROSSBOW),
    HIBANA    ("Hibana",     EnumSide.ATTACKER, EnumArmorLevel.LIGHT,  new EnumGun[]{EnumGun.TYPE_89, EnumGun.SUPERNOVA},                     new EnumGun[]{EnumGun.P229, EnumGun.BEARING_9},  new EnumGadget[]{EnumGadget.STUN_GRENADE, EnumGadget.CLAYMORE},            EnumGadget.X_KAIROS_LAUNCHER),
    JACKAL    ("Jackal",     EnumSide.ATTACKER, EnumArmorLevel.MEDIUM, new EnumGun[]{EnumGun.C7E, EnumGun.PDW9, EnumGun.IPS12L},              new EnumGun[]{EnumGun.IPS12S, EnumGun.USP40},    new EnumGadget[]{EnumGadget.STUN_GRENADE, EnumGadget.BREACH_CHARGE},       null),
    YING      ("Ying",       EnumSide.ATTACKER, EnumArmorLevel.MEDIUM, new EnumGun[]{EnumGun.SIX12, EnumGun.T_95_LSW},                        new EnumGun[]{EnumGun.Q_929},                    new EnumGadget[]{EnumGadget.BREACH_CHARGE, EnumGadget.SMOKE_GRENADE},      EnumGadget.CANDELA_GRENADE),
    ZOFIA     ("Zofia",      EnumSide.ATTACKER, EnumArmorLevel.MEDIUM, new EnumGun[]{EnumGun.M762, EnumGun.LMG_E},                            new EnumGun[]{EnumGun.RG15},                     new EnumGadget[]{EnumGadget.BREACH_CHARGE, EnumGadget.CLAYMORE},           EnumGadget.KS79_LAUNCHER),
    DOKKAEBI  ("Dokkaebi",   EnumSide.ATTACKER, EnumArmorLevel.MEDIUM, new EnumGun[]{EnumGun.Mk14_EBR, EnumGun.BOSG_12_2},                    new EnumGun[]{EnumGun.SMG12, EnumGun.C75_AUTO},  new EnumGadget[]{EnumGadget.SMOKE_GRENADE, EnumGadget.CLAYMORE},           EnumGadget.LOGIC_BOMB),
    LION      ("Lion",       EnumSide.ATTACKER, EnumArmorLevel.MEDIUM, new EnumGun[]{}, new EnumGun[]{}, new EnumGadget[]{}, null),
    FINKA     ("Finka",      EnumSide.ATTACKER, EnumArmorLevel.MEDIUM, new EnumGun[]{}, new EnumGun[]{}, new EnumGadget[]{}, null),

    RECRUIT_LIGHT ("Recruit", EnumSide.UNSPECIFIED, EnumArmorLevel.LIGHT,  null, null, null, null),
    RECRUIT_MEDIUM("Recruit", EnumSide.UNSPECIFIED, EnumArmorLevel.MEDIUM, null, null, null, null),
    RECRUIT_HEAVY ("Recruit", EnumSide.UNSPECIFIED, EnumArmorLevel.HEAVY,  null, null, null, null);

    private String displayName;
    private EnumSide side;
    private EnumArmorLevel armorLevel;
    private EnumGun[] primaries;
    private EnumGun[] secondaries;
    private EnumGadget[] gadgets;
    private EnumGadget special;

    EnumOperator(String displayName, EnumSide side, EnumArmorLevel armorLevel, EnumGun[] primaries, EnumGun[] secondaries, EnumGadget[] gadgets, EnumGadget special)
    {
        this.displayName = displayName;
        this.side = side;
        this.armorLevel = armorLevel;
        this.primaries = primaries;
        this.secondaries = secondaries;
        this.gadgets = gadgets;
        this.special = special;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public EnumSide getSide()
    {
        return side;
    }

    public EnumArmorLevel getArmorLevel()
    {
        return armorLevel;
    }

    public EnumGun[] getPrimaries()
    {
        return primaries;
    }

    public EnumGun[] getSecondaries()
    {
        return secondaries;
    }

    public EnumGadget[] getGadgets()
    {
        return gadgets;
    }

    public EnumGadget getSpecial()
    {
        return special;
    }

    public ResourceLocation getSkinLocation()
    {
        return new ResourceLocation(Reference.MOD_ID, "textures/skins/" + toString().toLowerCase(Locale.ENGLISH));
    }

    public static EnumOperator valueOf(int index)
    {
        return EnumOperator.values()[index];
    }
}