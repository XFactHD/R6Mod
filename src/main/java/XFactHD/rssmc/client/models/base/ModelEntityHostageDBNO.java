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

public class ModelEntityHostageDBNO extends ModelBase
{
    public ModelRenderer Head;
    public ModelRenderer Body;
    public ModelRenderer LegRight;
    public ModelRenderer LegLeft;
    public ModelRenderer ArmRight;
    public ModelRenderer ArmLeft;
    public ModelRenderer Hands;

    public ModelEntityHostageDBNO()
    {
        this.textureWidth = 64;
        this.textureHeight = 64;

        this.LegRight = new ModelRenderer(this, 48, 20);
        this.LegRight.setRotationPoint(0.0F, 22.0F, 5.0F);
        this.LegRight.addBox(0.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
        this.setRotateAngle(LegRight, -1.5707963705062866F, -0.06981316953897476F, 0.0F);
        this.ArmLeft = new ModelRenderer(this, 32, 0);
        this.ArmLeft.setRotationPoint(-4.0F, 20.0F, 17.0F);
        this.ArmLeft.addBox(-4.0F, 0.0F, 0.0F, 4, 9, 4, 0.0F);
        this.setRotateAngle(ArmLeft, -2.1101030656611446F, -0.47123889803846897F, 0.26529004630313807F);
        this.LegLeft = new ModelRenderer(this, 32, 20);
        this.LegLeft.setRotationPoint(0.0F, 20.0F, 5.0F);
        this.LegLeft.addBox(-4.0F, 0.0F, 0.0F, 4, 12, 4, 0.0F);
        this.setRotateAngle(LegLeft, -1.5707963705062866F, 0.06981316953897476F, 0.0F);
        this.Hands = new ModelRenderer(this, 32, 13);
        this.Hands.mirror = true;
        this.Hands.setRotationPoint(-4.0F, 17.88F, 12.8F);
        this.Hands.addBox(0.0F, 2.9F, 0.0F, 8, 3, 4, 0.0F);
        this.setRotateAngle(Hands, -2.0577431881013144F, -0.0F, 0.0F);
        this.Head = new ModelRenderer(this, 0, 0);
        this.Head.setRotationPoint(-4.0F, 18.0F, 25.0F);
        this.Head.addBox(0.0F, 0.0F, 0.0F, 8, 8, 8, 0.0F);
        this.setRotateAngle(Head, -1.5707963267948966F, -0.0F, 0.0F);
        this.Body = new ModelRenderer(this, 4, 16);
        this.Body.setRotationPoint(-4.0F, 20.0F, 17.0F);
        this.Body.addBox(0.0F, 0.0F, 0.0F, 8, 12, 4, 0.0F);
        this.setRotateAngle(Body, -1.5707963267948966F, 0.0F, 0.0F);
        this.ArmRight = new ModelRenderer(this, 48, 0);
        this.ArmRight.setRotationPoint(4.0F, 20.0F, 17.0F);
        this.ArmRight.addBox(0.0F, 0.0F, 0.0F, 4, 9, 4, 0.0F);
        this.setRotateAngle(ArmRight, -2.111848394913139F, 0.48694686130641796F, -0.26529004630313807F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        this.LegRight.render(f5);
        this.ArmLeft.render(f5);
        this.LegLeft.render(f5);
        this.Hands.render(f5);
        this.Head.render(f5);
        this.Body.render(f5);
        this.ArmRight.render(f5);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z)
    {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
