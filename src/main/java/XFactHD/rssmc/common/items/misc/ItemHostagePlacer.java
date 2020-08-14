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

package XFactHD.rssmc.common.items.misc;

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.common.entity.EntityHostage;
import XFactHD.rssmc.common.items.ItemBase;
import XFactHD.rssmc.common.utils.utilClasses.Position;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemHostagePlacer extends ItemBase
{
    public ItemHostagePlacer()
    {
        super("item_hostage_placer", 1, RainbowSixSiegeMC.CT.buildingTab, null);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (side == EnumFacing.UP)
        {
            if (!world.isRemote)
            {
                EntityHostage hostage = new EntityHostage(world, Position.fromBlockPosCentered(pos.up(), false));
                hostage.rotationYaw = player.rotationYaw;
                world.spawnEntity(hostage);
            }
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.FAIL;
    }
}