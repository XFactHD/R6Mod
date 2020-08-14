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

public class ModelNitroCell extends ModelBase
{
    private ModelRenderer bomb;
    private ModelRenderer cellphone;
    private ModelRenderer tapeLeft;
    private ModelRenderer tapeRight;
    private ModelRenderer circuitBoard;

    public ModelNitroCell()
    {
        textureWidth = 32;
        textureHeight = 32;

        bomb = new ModelRenderer(this, 0, 0);
        bomb.addBox(0F, 0F, 0F, 5, 2, 8);
        bomb.setRotationPoint(-2.5F, 22F, -4F);
        bomb.setTextureSize(32, 32);
        bomb.mirror = true;
        setRotation(bomb, 0F, 0F, 0F);
        cellphone = new ModelRenderer(this, 0, 10);
        cellphone.addBox(0F, 0F, 0F, 2, 1, 5);
        cellphone.setRotationPoint(-1F, 21.4F, -2.5F);
        cellphone.setTextureSize(32, 32);
        cellphone.mirror = true;
        setRotation(cellphone, 0F, 0F, 0F);
        tapeLeft = new ModelRenderer(this, 0, 0);
        tapeLeft.addBox(0F, 0F, 0F, 2, 0, 1);
        tapeLeft.setRotationPoint(-2.5F, 22F, 0F);
        tapeLeft.setTextureSize(32, 32);
        tapeLeft.mirror = true;
        setRotation(tapeLeft, 0F, 0F, -0.3630285F);
        tapeRight = new ModelRenderer(this, 0, 1);
        tapeRight.addBox(-2.01F, 0F, 0F, 2, 0, 1);
        tapeRight.setRotationPoint(2.51F, 22F, 0F);
        tapeRight.setTextureSize(32, 32);
        tapeRight.mirror = true;
        setRotation(tapeRight, 0F, 0F, 0.3630285F);
        circuitBoard = new ModelRenderer(this, 0, 5);
        circuitBoard.addBox(0F, 0F, 0F, 2, 1, 2);
        circuitBoard.setRotationPoint(-1F, 21.28F, -1F);
        circuitBoard.setTextureSize(32, 32);
        circuitBoard.mirror = true;
        setRotation(circuitBoard, 0F, 0F, 0F);
    }
  
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(entity, f, f1, f2, f3, f4, f5);
        bomb.render(f5);
        cellphone.render(f5);
        tapeLeft.render(f5);
        tapeRight.render(f5);
        circuitBoard.render(f5);
    }
  
    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
  
    public void setRotationAngles(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
    }

}
