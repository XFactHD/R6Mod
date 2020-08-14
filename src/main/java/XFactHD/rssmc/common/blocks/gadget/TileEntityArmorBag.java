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

package XFactHD.rssmc.common.blocks.gadget;

import XFactHD.rssmc.common.blocks.TileEntityGadget;
import XFactHD.rssmc.common.data.EnumGadget;
import XFactHD.rssmc.common.items.armor.ItemOperatorArmor;
import XFactHD.rssmc.client.util.Sounds;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;

public class TileEntityArmorBag extends TileEntityGadget
{
    private int armorLeft = 0;

    public void setArmorLeft(int armorLeft)
    {
        this.armorLeft = armorLeft;
    }

    public int getArmorLeft()
    {
        return armorLeft;
    }

    public void tryEquipArmor(EntityPlayer player)
    {
        if (armorLeft == 0)
        {
            return;
        }
        if (ItemOperatorArmor.upgradeWithRookArmor(player))
        {
            armorLeft -= 1;
            world.playSound(null, pos, Sounds.getGadgetSound(EnumGadget.ARMOR_BAG, "use"), SoundCategory.BLOCKS, 1, 1);
            notifyBlockUpdate();
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        super.readCustomNBT(nbt);
        armorLeft = nbt.getInteger("armor");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        super.writeCustomNBT(nbt);
        nbt.setInteger("armor", armorLeft);
    }
}