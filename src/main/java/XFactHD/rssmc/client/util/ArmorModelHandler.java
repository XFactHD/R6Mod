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

package XFactHD.rssmc.client.util;

import XFactHD.rssmc.client.models.base.armor.ModelArmorSmoke;
import XFactHD.rssmc.common.data.EnumOperator;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.inventory.EntityEquipmentSlot;

import java.util.EnumMap;

public class ArmorModelHandler
{
    private static EnumMap<EnumOperator, ModelBiped> models = new EnumMap<>(EnumOperator.class);

    public static ModelBiped getArmorModelForOperator(EnumOperator operator, EntityEquipmentSlot slot, ModelBiped base)
    {
        ModelBiped model = models.get(operator);
        if (model == null || operator == EnumOperator.SMOKE) { model = base; } //TODO: remove " || operator == EnumOperator.SMOKE" when he has a proper model
        model.bipedHead.showModel = false;
        model.bipedHeadwear.showModel = false;
        model.bipedBody.showModel = slot == EntityEquipmentSlot.CHEST;
        model.bipedRightArm.showModel = slot == EntityEquipmentSlot.CHEST;
        model.bipedLeftArm.showModel = slot == EntityEquipmentSlot.CHEST;
        model.bipedRightLeg.showModel = false;
        model.bipedLeftLeg.showModel = false;
        model.isSneak = base.isSneak;
        model.rightArmPose = base.rightArmPose;
        model.leftArmPose = base.leftArmPose;
        model.isChild = base.isChild;
        model.isRiding = base.isRiding;
        return model;
    }

    public static void initialize()
    {
        models.put(EnumOperator.MUTE,     null);
        models.put(EnumOperator.SMOKE,    new ModelArmorSmoke(.5F));
        models.put(EnumOperator.PULSE,    null);
        models.put(EnumOperator.CASTLE,   null);
        models.put(EnumOperator.ROOK,     null);
        models.put(EnumOperator.DOC,      null);
        models.put(EnumOperator.TACHANKA, null);
        models.put(EnumOperator.KAPKAN,   null);
        models.put(EnumOperator.JAEGER,   null);
        models.put(EnumOperator.BANDIT,   null);
        models.put(EnumOperator.FROST,    null);
        models.put(EnumOperator.VALKYRIE, null);
        models.put(EnumOperator.CAVEIRA,  null);
        models.put(EnumOperator.ECHO,     null);
        models.put(EnumOperator.MIRA,     null);
        models.put(EnumOperator.LESION, null);
        models.put(EnumOperator.ELA, null);
        models.put(EnumOperator.VIGIL, null);

        models.put(EnumOperator.SLEDGE,     null);
        models.put(EnumOperator.THATCHER,   null);
        models.put(EnumOperator.THERMITE,   null);
        models.put(EnumOperator.ASH,        null);
        models.put(EnumOperator.MONTAGNE,   null);
        models.put(EnumOperator.TWITCH,     null);
        models.put(EnumOperator.FUZE,       null);
        models.put(EnumOperator.GLAZ,       null);
        models.put(EnumOperator.BLITZ,      null);
        models.put(EnumOperator.IQ,         null);
        models.put(EnumOperator.BUCK,       null);
        models.put(EnumOperator.BLACKBEARD, null);
        models.put(EnumOperator.CAPITAO,    null);
        models.put(EnumOperator.HIBANA,     null);
        models.put(EnumOperator.JACKAL,     null);
        models.put(EnumOperator.YING, null);
        models.put(EnumOperator.ZOFIA, null);
        models.put(EnumOperator.DOKKAEBI, null);

        models.put(EnumOperator.RECRUIT_LIGHT,  null);
        models.put(EnumOperator.RECRUIT_MEDIUM, null);
        models.put(EnumOperator.RECRUIT_HEAVY,  null);
    }
}