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

package XFactHD.rssmc.api.capability;

import XFactHD.rssmc.common.data.EnumAttachment;
import XFactHD.rssmc.common.data.EnumFiremode;
import XFactHD.rssmc.common.data.EnumGun;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

public interface IGunHandler
{
    EnumGun getGun();

    int getAmmoLeft();

    void loadFromStack();

    void update(World world, EntityPlayer player, int slot, boolean isSelected);

    void reload(EntityPlayer player, InventoryPlayer inv);

    void setFiremode(EnumFiremode mode);

    EnumFiremode getFiremode();

    boolean isFiring();

    void setFiring(boolean firing);

    boolean isAiming();

    void setAiming(boolean aiming);

    boolean isReloading();

    void setReloading(boolean reloading, EntityPlayer player);

    boolean canSwitchFiremode();

    boolean switchFiremode();

    default int getStandardSpreadWidth() { return 10; }

    default int getCurrentSpreadWidth()
    {
        if (getPlayer() == null) { return getStandardSpreadWidth(); }
        if (getPlayer().isSprinting() || !getPlayer().onGround) { return (int) ((float) getStandardSpreadWidth() * 2.5F); }
        if (getPlayer().isSneaking()) { return (int) ((float) getStandardSpreadWidth() * 0.8F); }
        if (getPlayer().moveForward != 0) { return (int) ((float) getStandardSpreadWidth() * 1.5F); }
        return getStandardSpreadWidth();
    }

    EntityPlayer getPlayer();

    void setPlayer(EntityPlayer player);

    ItemStack getStack();

    void setStack(ItemStack stack);

    void addAttachment(EnumAttachment attachment);

    void removeAttachment(EnumAttachment attachment);

    boolean hasAttachment(EnumAttachment attachment);

    void preLoad();

    void receiveUpdate(NBTTagCompound nbt);

    void setGun(EnumGun gun);
}