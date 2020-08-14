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

package XFactHD.rssmc.client.models.base;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelEntityHostage extends ModelBase
{
    public ModelRenderer head;
    public ModelRenderer body;
    public ModelRenderer legRight;
    public ModelRenderer legLeft;
    public ModelRenderer armRight;
    public ModelRenderer armLeft;
    public ModelRenderer hands;

    public ModelEntityHostage()
    {
        this.textureWidth = 64;
        this.textureHeight = 64;

        this.head = new ModelRenderer(this, 0, 0);
        this.head.setRotationPoint(-4.0F, 0.0F, 0.0F);
        this.head.addBox(0.0F, 0.0F, 0.0F, 8, 8, 8, 0.0F);
        this.armRight = new ModelRenderer(this, 48, 0);
        this.armRight.setRotationPoint(4.0F, 9.0F, 5.0F);
        this.armRight.addBox(0.0F, 0.0F, 0.0F, 4, 9, 4, 0.0F);
        this.setRotateAngle(armRight, -1.5707963705062866F, 0.471238911151886F, 0.0F);
        this.armLeft = new ModelRenderer(this, 32, 0);
        this.armLeft.setRotationPoint(-4.0F, 9.0F, 5.0F);
        this.armLeft.addBox(-4.0F, 0.0F, 0.0F, 4, 9, 4, 0.0F);
        this.setRotateAngle(armLeft, -1.5707963705062866F, -0.471238911151886F, 0.0F);
        this.legLeft = new ModelRenderer(this, 32, 20);
        this.legLeft.setRotationPoint(0.0F, 20.0F, 5.0F);
        this.legLeft.addBox(-4.0F, 0.0F, 0.0F, 4, 12, 4, 0.0F);
        this.setRotateAngle(legLeft, -1.5707963705062866F, 0.06981316953897476F, 0.0F);
        this.body = new ModelRenderer(this, 4, 16);
        this.body.setRotationPoint(-4.0F, 8.0F, 2.0F);
        this.body.addBox(0.0F, 0.0F, 0.0F, 8, 12, 4, 0.0F);
        this.hands = new ModelRenderer(this, 32, 13);
        this.hands.mirror = true;
        this.hands.setRotationPoint(-4.0F, 9.0F, 0.0F);
        this.hands.addBox(0.0F, 2.9000000953674316F, 0.0F, 8, 3, 4, 0.0F);
        this.setRotateAngle(hands, -1.5707963705062866F, -0.0F, 0.0F);
        this.legRight = new ModelRenderer(this, 48, 20);
        this.legRight.setRotationPoint(0.0F, 22.0F, 5.0F);
        this.legRight.addBox(0.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
        this.setRotateAngle(legRight, -1.5707963705062866F, -0.06981316953897476F, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        this.head.render(f5);
        this.armRight.render(f5);
        this.armLeft.render(f5);
        this.legLeft.render(f5);
        this.body.render(f5);
        this.hands.render(f5);
        this.legRight.render(f5);
    }

    public void setRotateAngle(ModelRenderer part, float x, float y, float z)
    {
        part.rotateAngleX = x;
        part.rotateAngleY = y;
        part.rotateAngleZ = z;
    }
}
