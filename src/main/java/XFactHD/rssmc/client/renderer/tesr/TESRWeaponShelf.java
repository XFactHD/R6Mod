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

package XFactHD.rssmc.client.renderer.tesr;

import XFactHD.rssmc.common.blocks.misc.TileEntityWeaponShelf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class TESRWeaponShelf extends TileEntitySpecialRenderer<TileEntityWeaponShelf>
{
    @Override
    public void renderTileEntityAt(TileEntityWeaponShelf te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        GlStateManager.pushMatrix();

        double[][] coords = calcItemCoords(x, y, z, te.getFacing());
        double x0 = coords[0][0];
        double y0 = coords[0][1];
        double z0 = coords[0][2];
        double x1 = coords[1][0];
        double y1 = coords[1][1];
        double z1 = coords[1][2];
        double x2 = coords[2][0];
        double y2 = coords[2][1];
        double z2 = coords[2][2];

        ItemStack[] stacks = te.getStacks();
        renderStackAt(stacks[0], x0, y0, z0, te.getFacing());
        renderStackAt(stacks[1], x1, y1, z1, te.getFacing());
        renderStackAt(stacks[2], x2, y2, z2, te.getFacing());

        GlStateManager.popMatrix();
    }

    private void renderStackAt(ItemStack stack, double x, double y , double z, EnumFacing facing)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(-90, 1, 0, 0);
        float angle = 90 * (4 - facing.getHorizontalIndex());
        GlStateManager.rotate(angle, 0, 0, 1);
        GlStateManager.scale(.5, .5, .5);
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
        GlStateManager.popMatrix();
    }

    private double[][] calcItemCoords(double x, double y, double z, EnumFacing facing)
    {
        double[][] coords = new double[3][3];
        switch (facing)
        {
            case NORTH:
            {
                coords[0][0] = x + .3;
                coords[0][1] = y + .5;
                coords[0][2] = z + .6;
                coords[1][0] = x + .5;
                coords[1][1] = y + .5;
                coords[1][2] = z + .6;
                coords[2][0] = x + .725;
                coords[2][1] = y + .5;
                coords[2][2] = z + .6;
                break;
            }
            case EAST:
            {
                coords[0][0] = x + .4;
                coords[0][1] = y + .5;
                coords[0][2] = z + .3;
                coords[1][0] = x + .4;
                coords[1][1] = y + .5;
                coords[1][2] = z + .5;
                coords[2][0] = x + .4;
                coords[2][1] = y + .5;
                coords[2][2] = z + .725;
                break;
            }
            case SOUTH:
            {
                coords[0][0] = x + .275;
                coords[0][1] = y + .5;
                coords[0][2] = z + .4;
                coords[1][0] = x + .5;
                coords[1][1] = y + .5;
                coords[1][2] = z + .4;
                coords[2][0] = x + .7;
                coords[2][1] = y + .5;
                coords[2][2] = z + .4;
                break;
            }
            case WEST:
            {
                coords[0][0] = x + .6;
                coords[0][1] = y + .5;
                coords[0][2] = z + .275;
                coords[1][0] = x + .6;
                coords[1][1] = y + .5;
                coords[1][2] = z + .5;
                coords[2][0] = x + .6;
                coords[2][1] = y + .5;
                coords[2][2] = z + .7;
                break;
            }
        }
        return coords;
    }
}