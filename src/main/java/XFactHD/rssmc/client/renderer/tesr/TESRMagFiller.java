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

import XFactHD.rssmc.common.blocks.survival.TileEntityMagFiller;
import XFactHD.rssmc.common.data.EnumMagazine;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class TESRMagFiller extends TileEntitySpecialRenderer<TileEntityMagFiller> //TODO: eventually special case P90 mag
{
    @Override
    public void renderTileEntityAt(TileEntityMagFiller te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        GlStateManager.pushMatrix();

        GlStateManager.translate(x, y, z);
        EnumFacing facing = getWorld().getBlockState(te.getPos()).getValue(PropertyHolder.FACING_CARDINAL);
        int rotation = 90 * (facing.getHorizontalIndex() - 1);
        GlStateManager.rotate(rotation, 0, 1, 0);
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();

        final float[] rotations = new float[]{-5, 4, -2, 1, -3};

        //Mags in input slot
        GlStateManager.pushMatrix();
        {
            ItemStack stackIn = te.getInputStack();
            if (stackIn != null)
            {
                EnumMagazine magIn = EnumMagazine.valueOf(stackIn);
                if (magIn.rotateInMagFiller())
                {
                    float offX = facing == EnumFacing.EAST ? .025F : facing == EnumFacing.WEST ? 1.025F : facing == EnumFacing.SOUTH ? .325F : facing == EnumFacing.NORTH ? -.675F : 0;
                    float offZ = facing == EnumFacing.EAST ? -1.7F : facing == EnumFacing.WEST ? -.7F : facing == EnumFacing.SOUTH ? -1F : 0;
                    GlStateManager.translate(-.175 + offX, .275, .845 + offZ);
                    if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH) { GlStateManager.rotate(180, 0, 1, 0); }
                    GlStateManager.rotate(90, 0, 1, 0);
                    GlStateManager.scale(.6, .6, 2.4);
                    for (int i = 0; i < stackIn.stackSize; i++)
                    {
                        Minecraft.getMinecraft().getRenderItem().renderItem(stackIn, ItemCameraTransforms.TransformType.FIXED);
                        GlStateManager.translate(0, 0, -.075);
                    }
                }
                else
                {
                    float offX = facing == EnumFacing.EAST ? -.2F : facing == EnumFacing.WEST ? .8F : facing == EnumFacing.SOUTH ? 1F : 0;
                    float offZ = facing == EnumFacing.EAST ? -1.7F : facing == EnumFacing.WEST ? -.7F : facing == EnumFacing.SOUTH ? -1F : 0;
                    GlStateManager.translate(-.4 + offX, 1.5F/16F, .85 + offZ);
                    GlStateManager.rotate(facing == EnumFacing.EAST || facing == EnumFacing.WEST ? -90 : 90, 0, 1, 0);
                    GlStateManager.rotate(-90, 1, 0, 0);
                    GlStateManager.scale(.6, .6, .9);
                    for (int i = 0; i < stackIn.stackSize; i++)
                    {
                        GlStateManager.rotate(rotations[i], 0, 0, 1);
                        Minecraft.getMinecraft().getRenderItem().renderItem(stackIn, ItemCameraTransforms.TransformType.FIXED);
                        GlStateManager.rotate(-rotations[i], 0, 0, 1);
                        GlStateManager.translate(0, 0, 1F/16F);
                    }
                }
            }
        }
        GlStateManager.popMatrix();

        //Mag currently being processed
        GlStateManager.pushMatrix();
        {
            ItemStack stackLoad = te.getProcessStack();
            if (stackLoad != null)
            {
                EnumMagazine magLoad = EnumMagazine.valueOf(stackLoad);
                float offX = facing == EnumFacing.WEST ? .8F : facing == EnumFacing.SOUTH ? 1F : facing == EnumFacing.EAST ? -.2F : 0;
                float offZ = facing == EnumFacing.SOUTH ? -1F : facing == EnumFacing.EAST ? -1F : 0;
                if (magLoad.rotateInMagFiller())
                {
                    GlStateManager.translate(-.4 + offX, .3, .5 + offZ);
                    if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH) { GlStateManager.rotate(180, 0, 1, 0); }
                    GlStateManager.rotate(90, 0, 1, 0);
                    GlStateManager.scale(.6, .6, 2.4);
                }
                else
                {
                    GlStateManager.translate(-.4 + offX, .3, .5 + offZ);
                    if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH) { GlStateManager.rotate(180, 0, 1, 0); }
                    GlStateManager.scale(.6, .6, .9);
                }
                Minecraft.getMinecraft().getRenderItem().renderItem(stackLoad, ItemCameraTransforms.TransformType.FIXED);
            }
        }
        GlStateManager.popMatrix();

        //Mags in output slot
        GlStateManager.pushMatrix();
        {
            ItemStack stackOut = te.getOutputStack();
            if (stackOut != null)
            {
                EnumMagazine magOut = EnumMagazine.valueOf(stackOut);
                if (magOut.rotateInMagFiller())
                {
                    float offX = facing == EnumFacing.EAST ? .025F : facing == EnumFacing.WEST ? 1.025F : facing == EnumFacing.SOUTH ? .325F : facing == EnumFacing.NORTH ? -.675F : 0;
                    float offZ = facing == EnumFacing.EAST ? -.32F : facing == EnumFacing.WEST ? .68F : facing == EnumFacing.SOUTH ? -1F : 0;
                    GlStateManager.translate(-.175 + offX, .275, .16 + offZ);
                    if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH) { GlStateManager.rotate(180, 0, 1, 0); }
                    GlStateManager.rotate(90, 0, 1, 0);
                    GlStateManager.scale(.6, .6, 2.4);
                    for (int i = 0; i < stackOut.stackSize; i++)
                    {
                        Minecraft.getMinecraft().getRenderItem().renderItem(stackOut, ItemCameraTransforms.TransformType.FIXED);
                        GlStateManager.translate(0, 0, -.075);
                    }
                }
                else
                {
                    float offX = facing == EnumFacing.EAST ? -.2F : facing == EnumFacing.WEST ? .8F : facing == EnumFacing.SOUTH ? 1F : 0;
                    float offZ = facing == EnumFacing.EAST ? -.25F : facing == EnumFacing.WEST ? .75F : facing == EnumFacing.SOUTH ? -1F : 0;
                    GlStateManager.translate(-.4 + offX, 1.5F/16F, .125 + offZ);
                    GlStateManager.rotate(facing == EnumFacing.EAST || facing == EnumFacing.WEST ? -90 : 90, 0, 1, 0);
                    GlStateManager.rotate(-90, 1, 0, 0);
                    GlStateManager.scale(.6, .6, .9);
                    for (int i = 0; i < stackOut.stackSize; i++)
                    {
                        GlStateManager.rotate(rotations[i], 0, 0, 1);
                        Minecraft.getMinecraft().getRenderItem().renderItem(stackOut, ItemCameraTransforms.TransformType.FIXED);
                        GlStateManager.rotate(-rotations[i], 0, 0, 1);
                        GlStateManager.translate(0, 0, 1F/16F);
                    }
                }
            }

        }
        GlStateManager.popMatrix();

        float tankFillState = te.getTankFillState();//TODO: draw hopper gauge

        GlStateManager.enableLighting();
        GlStateManager.disableAlpha();

        GlStateManager.popMatrix();
    }
}