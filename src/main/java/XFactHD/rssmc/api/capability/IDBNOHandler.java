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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.util.INBTSerializable;

public interface IDBNOHandler extends INBTSerializable<NBTTagCompound>
{
    void setPlayer(EntityPlayer player);

    boolean isDBNO();

    float getReviveProgress();

    int getTimeLeft();

    float getRemainingHP();

    void tick();

    void revive(boolean stimmed);

    void resetProgress();

    boolean wasAlreadyDBNO();

    void setHoldingWound(boolean holding);

    boolean isHoldingWound();

    boolean setDBNO(DamageSource dmg, float dmgBelowZero);

    void setHelper(EntityPlayer player);

    void firstPlayerTick();

    EntityPlayer getHelper();

    void hit(DamageSource dmg, float amount);

    void kill();

    boolean gotRevived();
}
