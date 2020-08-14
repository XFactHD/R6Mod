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

import XFactHD.rssmc.common.blocks.gadget.TileEntityActiveDefenseSystem;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class TESRActiveDefenseSystem extends TileEntitySpecialRenderer<TileEntityActiveDefenseSystem> //TODO: implement
{
    @Override
    public void renderTileEntityAt(TileEntityActiveDefenseSystem te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        if (te.isAnimationActive())
        {

        }
    }
}