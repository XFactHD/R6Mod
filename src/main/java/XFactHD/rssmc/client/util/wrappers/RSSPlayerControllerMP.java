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

package XFactHD.rssmc.client.util.wrappers;

import XFactHD.rssmc.common.utils.helper.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;

public class RSSPlayerControllerMP extends PlayerControllerMP
{
    public RSSPlayerControllerMP(Minecraft mc, NetHandlerPlayClient netHandler)
    {
        super(mc, netHandler);
    }

    @Override
    public float getBlockReachDistance()
    {
        return ConfigHandler.battleMode ? 1.25F : super.getBlockReachDistance();
    }
}