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

package XFactHD.rssmc.client.models.base.armor;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

//TODO: remake
public class ModelArmorSmoke extends ModelBiped
{
    public ModelArmorSmoke(float scale)
    {
        super(scale, 0, 64, 64);

        textureWidth = 64;
        textureHeight = 64;

        ModelRenderer mask = new ModelRenderer(this, 0, 0);
        mask.addBox(-5F, -9F, -5F, 10, 9, 10);
        mask.setRotationPoint(0F, 0F, 0F);
        mask.setTextureSize(64, 64);
        mask.mirror = true;
        setRotation(mask, 0F, 0F, 0F);
        ModelRenderer filter = new ModelRenderer(this, 31, 46);
        filter.addBox(-2F, -4F, -9F, 4, 4, 4);
        filter.setRotationPoint(0F, 0F, 0F);
        filter.setTextureSize(64, 64);
        filter.mirror = true;
        setRotation(filter, 0F, 0F, 0F);
        ModelRenderer filterPack = new ModelRenderer(this, 31, 38);
        filterPack.addBox(-4F, 3F, 2.5F, 8, 5, 2);
        filterPack.setRotationPoint(0F, 0F, 0F);
        filterPack.setTextureSize(64, 64);
        filterPack.mirror = true;
        setRotation(filterPack, 0F, 0F, 0F);
        ModelRenderer rightSleeve = new ModelRenderer(this, 42, 19);
        rightSleeve.addBox(-3.5F, -2.5F, -2.5F, 5, 13, 5);
        rightSleeve.setRotationPoint(-5F, 2F, 0F);
        rightSleeve.setTextureSize(64, 64);
        rightSleeve.mirror = true;
        setRotation(rightSleeve, 0F, 0F, 0F);
        ModelRenderer leftSleeve = new ModelRenderer(this, 42, 0);
        leftSleeve.addBox(-1.5F, -2.5F, -2.5F, 5, 13, 5);
        leftSleeve.setRotationPoint(5F, 2F, 0F);
        leftSleeve.setTextureSize(64, 64);
        leftSleeve.mirror = true;
        setRotation(leftSleeve, 0F, 0F, 0F);
        ModelRenderer chestplate = new ModelRenderer(this, 0, 38);
        chestplate.addBox(-4.5F, 0F, -3F, 9, 12, 6);
        chestplate.setRotationPoint(0F, 0F, 0F);
        chestplate.setTextureSize(64, 64);
        chestplate.mirror = true;
        setRotation(chestplate, 0F, 0F, 0F);
        ModelRenderer rightLeg = new ModelRenderer(this, 0, 20);
        rightLeg.addBox(-2.5F, 0F, -2.5F, 5, 12, 5);
        rightLeg.setRotationPoint(-2F, 12F, 0F);
        rightLeg.setTextureSize(64, 64);
        rightLeg.mirror = true;
        setRotation(rightLeg, 0F, 0F, 0F);
        ModelRenderer leftLeg = new ModelRenderer(this, 21, 20);
        leftLeg.addBox(-1.5F, 0F, -2.5F, 5, 12, 5);
        leftLeg.setRotationPoint(2F, 12F, 0F);
        leftLeg.setTextureSize(64, 64);
        leftLeg.mirror = true;
        setRotation(leftLeg, 0F, 0F, 0F);

        bipedHead.addChild(mask);
        bipedHead.addChild(filter);
        bipedBody.addChild(filterPack);
        bipedBody.addChild(chestplate);
        bipedRightArm.addChild(rightSleeve);
        bipedLeftArm.addChild(leftSleeve);
        bipedRightLeg.addChild(rightLeg);
        bipedLeftLeg.addChild(leftLeg);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
