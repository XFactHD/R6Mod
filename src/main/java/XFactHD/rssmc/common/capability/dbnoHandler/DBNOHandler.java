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

package XFactHD.rssmc.common.capability.dbnoHandler;

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.api.capability.IDBNOHandler;
import XFactHD.rssmc.common.data.EnumOperator;
import XFactHD.rssmc.common.data.team.StatusController;
import XFactHD.rssmc.common.net.PacketUpdateDBNO;
import XFactHD.rssmc.common.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;

import java.util.UUID;

public class DBNOHandler implements IDBNOHandler
{
    private EntityPlayer player;
    private EntityPlayer helper = null;
    private long lastHelpTime = 0;
    private boolean dbno = false;
    private boolean wasAlreadyDBNO = false;
    private DamageSource lastDMGSource = null;
    private boolean holdingWound = false;
    private int timeLeft = 300;
    private float progress = 0;
    private boolean revived = false;
    private float dbnoHPPool = 5;
    private int lastBarValue = 0;

    @Override
    public void setPlayer(EntityPlayer player)
    {
        this.player = player;
    }

    @Override
    public boolean isDBNO()
    {
        return dbno;
    }

    @Override
    public boolean wasAlreadyDBNO()
    {
        return wasAlreadyDBNO;
    }

    @Override
    public boolean setDBNO(DamageSource source, float dmgBelowZero)
    {
        if (!canSurvive(source, dmgBelowZero)) { return false; }
        dbnoHPPool -= Math.abs(dmgBelowZero);
        lastDMGSource = source;
        dbno = true;
        wasAlreadyDBNO = true;
        lastBarValue = 124;
        sendUpdatePacket();
        return true;
    }

    @Override
    public void tick()
    {
        if (progress > 0.0)
        {
            if (progress >= 1 && dbno)
            {
                progress = 0;
                dbno = false;
                revived = true;
                player.setHealth(10);
                setHelper(null);
                return;
            }
            else if (Utils.currentTicks() - lastHelpTime > 2)
            {
                resetProgress();
                setHelper(null);
            }
            else { return; }
        }
        timeLeft -= getLifeLossPerTick();
        if (Math.round(((float)124) * (((float)timeLeft) / 300F)) != lastBarValue)
        {
            lastBarValue = Math.round(((float)360) * (((float)timeLeft) / 300F));
            sendUpdatePacket();
        }
        if (timeLeft <= 0) { kill(); }
        else if (!StatusController.isPlayerInObjectiveArea(player) && !helpersAvailable()) { kill(); }
    }

    @Override
    public void revive(boolean stimmed)
    {
        if (stimmed)
        {
            progress = 1;
            dbno = false;
            player.setHealth(15);
            setHelper(null);
        }
        else
        {
            progress += .0125F;
            lastHelpTime = Utils.currentTicks();
        }
        sendUpdatePacket();
    }

    @Override
    public void resetProgress()
    {
        progress = 0;
    }

    @Override
    public void hit(DamageSource dmg, float amount)
    {
        lastDMGSource = dmg;
        dbnoHPPool -= amount;
        sendUpdatePacket();
        if (dbnoHPPool <= 0)
        {
            kill();
        }
    }

    @Override
    public void kill()
    {
        if (lastDMGSource == null) { lastDMGSource = DamageSource.fall; }
        player.attackEntityFrom(lastDMGSource, Float.MAX_VALUE);
        sendUpdatePacket();
    }

    @Override
    public void setHelper(EntityPlayer helper)
    {
        this.helper = helper;
        resetProgress();
        sendUpdatePacket();
    }

    @Override
    public EntityPlayer getHelper()
    {
        return helper;
    }

    @Override
    public boolean gotRevived()
    {
        if (revived)
        {
            revived = false;
            return true;
        }
        return false;
    }

    @Override
    public float getReviveProgress()
    {
        return progress;
    }

    @Override
    public int getTimeLeft()
    {
        return timeLeft;
    }

    @Override
    public float getRemainingHP()
    {
        return dbnoHPPool;
    }

    @Override
    public void setHoldingWound(boolean holding)
    {
        this.holdingWound = holding;
        sendUpdatePacket();
    }

    @Override
    public boolean isHoldingWound()
    {
        return holdingWound;
    }

    private void sendUpdatePacket()
    {
        RainbowSixSiegeMC.NET.sendMessageToClient(new PacketUpdateDBNO(serializeNBT()), player);
    }

    private int getLifeLossPerTick()
    {
        boolean rook = StatusController.isPlayerWearingRookArmor(player);
        if (holdingWound && rook)
        {
            return 1;
        }
        else if (holdingWound)
        {
            return 2;
        }
        else if (rook)
        {
            return 3;
        }
        else
        {
            return 5;
        }
    }

    private boolean canSurvive(DamageSource source, float dmgBelowZero)
    {
        if (wasAlreadyDBNO) { return false; }
        if (!isDmgIgnored(source)) { return Math.abs(dmgBelowZero) >= 5; }
        if (StatusController.isPlayerInObjectiveArea(player)) { return true; }
        return StatusController.areOtherPlayersAlive(player) || StatusController.getPlayersOperator(player) == EnumOperator.DOC;
    }

    private boolean helpersAvailable()
    {
        return StatusController.areOtherPlayersAlive(player) || StatusController.getPlayersOperator(player) == EnumOperator.DOC;
    }

    private boolean isDmgIgnored(DamageSource source)
    {
        return source == DamageSource.starve ||
               source == DamageSource.drown ||
               source == DamageSource.inWall ||
               source == DamageSource.fall ||
               source == DamageSource.flyIntoWall ||
               source == DamageSource.fallingBlock;
    }

    @Override
    public void firstPlayerTick()
    {
        if (!player.world.isRemote) sendUpdatePacket();
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setBoolean("dbno", dbno);
        nbt.setBoolean("wasdbno", wasAlreadyDBNO);
        nbt.setBoolean("holding", holdingWound);
        nbt.setInteger("time", timeLeft);
        nbt.setFloat("prog", progress);
        nbt.setBoolean("revived", revived);
        nbt.setFloat("hppool", dbnoHPPool);
        nbt.setInteger("bar", lastBarValue);
        nbt.setString("uuid", player.getUniqueID().toString());
        nbt.setString("helper", helper == null ? "" : helper.getUniqueID().toString());
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        dbno = nbt.getBoolean("dbno");
        wasAlreadyDBNO = nbt.getBoolean("wasdbno");
        holdingWound = nbt.getBoolean("holding");
        timeLeft = nbt.getInteger("time");
        progress = nbt.getFloat("prog");
        revived = nbt.getBoolean("revived");
        dbnoHPPool = nbt.getFloat("hppool");
        lastBarValue = nbt.getInteger("bar");
        if (player == null)
        {
            player = RainbowSixSiegeMC.proxy.getWorld().getPlayerEntityByUUID(UUID.fromString(nbt.getString("uuid")));
        }
        String uuid = nbt.getString("helper");
        helper = uuid.equals("") ? null : player.world.getPlayerEntityByUUID(UUID.fromString(uuid));
    }
}