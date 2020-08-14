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

package XFactHD.rssmc.common.utils.helper;

import XFactHD.rssmc.client.gui.*;
import XFactHD.rssmc.common.blocks.survival.*;
import XFactHD.rssmc.common.gui.*;
import XFactHD.rssmc.common.utils.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch (ID)
        {
            case Reference.GUI_ID_CLASS_EQUIPPER: return new GuiClassEquipper(player);
            case Reference.GUI_ID_SPEED_LOADER:   return new GuiSpeedLoader(player);
            case Reference.GUI_ID_GUN_CRAFTER:    return new GuiGunCraftingTable(((TileEntityGunCraftingTable)world.getTileEntity(new BlockPos(x, y, z))), player);
            case Reference.GUI_ID_BIO_CONTAINER:  return new GuiBioContainer(new BlockPos(x, y, z));
            case Reference.GUI_ID_BOMB:           return new GuiBomb(new BlockPos(x, y, z));
            case Reference.GUI_ID_MAG_FILLER:     return new GuiMagFiller(((TileEntityMagFiller)world.getTileEntity(new BlockPos(x, y, z))), player);
            default: return null;
        }
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch (ID)
        {
            case Reference.GUI_ID_CLASS_EQUIPPER: return new ContainerClassEquipper();
            case Reference.GUI_ID_SPEED_LOADER:   return new ContainerSpeedLoader(player);
            case Reference.GUI_ID_GUN_CRAFTER:    return new ContainerGunCraftingTable(((TileEntityGunCraftingTable)world.getTileEntity(new BlockPos(x, y, z))), player);
            case Reference.GUI_ID_BIO_CONTAINER:  return new ContainerBioContainer();
            case Reference.GUI_ID_BOMB:           return new ContainerBomb();
            case Reference.GUI_ID_MAG_FILLER:     return new ContainerMagFiller(((TileEntityMagFiller)world.getTileEntity(new BlockPos(x, y, z))), player);
            default: return null;
        }
    }
}