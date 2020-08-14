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

import XFactHD.rssmc.common.Content;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

//TODO: add recoil and spread offsets
public enum EnumAttachment
{
    ACOG_SIGHT    (EnumAttachmentType.SIGHT, "AcogSight",   0, 0, "normal", "russian"),
    HOLO_SIGHT    (EnumAttachmentType.SIGHT, "HoloSight",   0, 0, "normal", "russian"),
    RED_DOT_SIGHT (EnumAttachmentType.SIGHT, "RedDotSight", 0, 0, "normal", "russian"),
    REFLEX_SIGHT  (EnumAttachmentType.SIGHT, "ReflexSight", 0, 0, "normal", "russian"),

    VERTICAL_GRIP (EnumAttachmentType.GRIP, "VerticalGrip", 0, 0, "normal"),
    ANGLED_GRIP   (EnumAttachmentType.GRIP, "AngledGrip",   0, 0, "normal"),

    LASER (EnumAttachmentType.UNDER_BARREL, "Laser", 0, 0, "normal", "shotgun_big", "pistol", "revolver"),

    SUPPRESSOR   (EnumAttachmentType.BARREL, "Suppressor",  0, 0, "normal", "pistol", "pistol_round", "large"),
    FLASH_HIDER  (EnumAttachmentType.BARREL, "FlashHider",  0, 0, "normal"), //TODO: complete
    COMPENSATOR  (EnumAttachmentType.BARREL, "Compensator", 0, 0, "normal"), //TODO: complete
    MUZZLE_BREAK (EnumAttachmentType.BARREL, "MuzzleBreak", 0, 0, "normal"), //TODO: complete
    HEAVY_BARREL (EnumAttachmentType.BARREL, "HeavyBarrel", 0, 0, "normal"), //TODO: complete

    RIFLE_SHIELD (EnumAttachmentType.SPECIAL, "RifleShield", 0, 0, "normal"),
    SHOTGUN      (EnumAttachmentType.SPECIAL, "Shotgun",     0, 0, "normal"),
    FLIP_SIGHT   (EnumAttachmentType.SPECIAL, "FlipSight",   0, 0, "normal");

    private EnumAttachmentType type;
    private String name;
    private float spreadReduction;
    private float recoilReduction;
    private List<String> subtypes;

    EnumAttachment(EnumAttachmentType type, String name, float spreadReduction, float recoilReduction, String... subtypes)
    {
        this.type = type;
        this.name = name;
        this.spreadReduction = spreadReduction;
        this.recoilReduction = recoilReduction;
        this.subtypes = subtypes == null ? new ArrayList<>() : Arrays.asList(subtypes);
    }

    public EnumAttachmentType getAttachmentType()
    {
        return type;
    }

    public ItemStack getAttachmentStack(String subtype)
    {
        ItemStack stack = new ItemStack(Content.itemAttachment, 1, ordinal());
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("subtype", subtypes.indexOf(subtype));
        return stack;
    }

    public float getRecoilReduction()
    {
        return recoilReduction;
    }

    public float getSpreadReduction()
    {
        return spreadReduction;
    }

    public static String[] getAsStringArray()
    {
        String[] strings = new String[values().length];
        for (EnumAttachment attachment : values())
        {
            strings[attachment.ordinal()] = attachment.name;
        }
        return strings;
    }

    @Override
    public String toString()
    {
        return super.toString().toLowerCase(Locale.ENGLISH);
    }

    public static EnumAttachment valueOf(int index)
    {
        return values()[index];
    }

    public enum EnumAttachmentType
    {
        UNDER_BARREL,
        GRIP,
        BARREL,
        SIGHT,
        SPECIAL
    }
}
