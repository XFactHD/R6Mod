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

package XFactHD.rssmc.common.blocks.objective;

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.common.blocks.TileEntityBase;
import XFactHD.rssmc.common.data.EnumSide;
import XFactHD.rssmc.common.data.team.StatusController;
import com.google.common.base.Predicate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nullable;
import java.util.List;

public class TileEntityBioContainer extends TileEntityBase implements ITickable
{
    private static final Predicate<EntityPlayer> FILTER_ATT = new Predicate<EntityPlayer>() {
        @Override
        @SuppressWarnings("ConstantConditions")
        public boolean apply(@Nullable EntityPlayer input)
        {
            return RainbowSixSiegeMC.DEBUG ? StatusController.getPlayersSide(input) != EnumSide.DEFFENDER :
                   StatusController.getPlayersSide(input) == EnumSide.ATTACKER;
        }
    };
    private static final Predicate<EntityPlayer> FILTER_DEF = new Predicate<EntityPlayer>() {
        @Override
        public boolean apply(@Nullable EntityPlayer input)
        {
            return StatusController.getPlayersSide(input) == EnumSide.DEFFENDER;
        }
    };
    public static final int MAX_SECURE_TIME = 200;

    private int rangeNorth = 0;
    private int rangeEast = 0;
    private int rangeSouth = 0;
    private int rangeWest = 0;
    private int rangeUp = 0;
    private AxisAlignedBB aabb = null;
    private boolean securing = false;
    private boolean contested = false;
    private boolean secured = false;
    private int secureTimer = 0;

    @Override
    public void update()
    {
        if (!world.isRemote && aabb != null && !secured)
        {
            List<EntityPlayer> attackers = world.getEntitiesWithinAABB(EntityPlayer.class, aabb, FILTER_ATT);
            List<EntityPlayer> defenders = world.getEntitiesWithinAABB(EntityPlayer.class, aabb, FILTER_DEF);
            setSecuring(!attackers.isEmpty());
            setContested(!attackers.isEmpty() && !defenders.isEmpty());
            if (securing && !contested && secureTimer < MAX_SECURE_TIME)
            {
                secureTimer += 1;
                notifyBlockUpdate();
            }
            else if (!securing && secureTimer > 0)
            {
                secureTimer -= 1;
                notifyBlockUpdate();
            }
            if (secureTimer >= MAX_SECURE_TIME && !contested)
            {
                setSecured(true);
                setSecuring(false);
                notifyBlockUpdate();
            }
        }
    }

    private void setSecuring(boolean securing)
    {
        if (securing != this.securing)
        {
            this.securing = securing;
            notifyBlockUpdate();
        }
    }

    private void setContested(boolean contested)
    {
        if (contested != this.contested)
        {
            this.contested = contested;
            notifyBlockUpdate();
        }
    }

    private void setSecured(boolean secured)
    {
        if (secured != this.secured)
        {
            if (secured) { /*TODO: tell game manager that the round is over*/ }
            this.secured = secured;
            notifyBlockUpdate();
        }
    }

    public void setRange(int rangeNorth, int rangeEast, int rangeSouth, int rangeWest, int rangeUp)
    {
        this.rangeNorth = rangeNorth;
        this.rangeEast = rangeEast;
        this.rangeSouth = rangeSouth;
        this.rangeWest = rangeWest;
        this.rangeUp = rangeUp;
        aabb = new AxisAlignedBB(pos.getX() - rangeWest, pos.getY(), pos.getZ() - rangeNorth, pos.getX() + rangeEast + 1, pos.getY() + rangeUp + 1, pos.getZ() + rangeSouth + 1);
        if (world != null && !world.isRemote) { notifyBlockUpdate(); }
    }

    public boolean isSecuring()
    {
        return securing;
    }

    public boolean isContested()
    {
        return contested;
    }

    public boolean isSecured()
    {
        return secured;
    }

    public int getSecureTime()
    {
        return secureTimer;
    }

    public void reset()
    {
        secureTimer = 0;
        secured = false;
        notifyBlockUpdate();
    }

    public AxisAlignedBB getAABB()
    {
        return aabb == null ? new AxisAlignedBB(pos, pos.south().east().up()) : aabb;
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        rangeNorth = nbt.getInteger("rangeNorth");
        rangeEast = nbt.getInteger("rangeEast");
        rangeSouth = nbt.getInteger("rangeSouth");
        rangeWest = nbt.getInteger("rangeWest");
        rangeUp = nbt.getInteger("rangeUp");
        securing = nbt.getBoolean("securing");
        contested = nbt.getBoolean("contested");
        secured = nbt.getBoolean("secured");
        secureTimer = nbt.getInteger("timer");
        setRange(rangeNorth, rangeEast, rangeSouth, rangeWest, rangeUp);
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("rangeNorth", rangeNorth);
        nbt.setInteger("rangeEast", rangeEast);
        nbt.setInteger("rangeSouth", rangeSouth);
        nbt.setInteger("rangeWest", rangeWest);
        nbt.setInteger("rangeUp", rangeUp);
        nbt.setBoolean("securing", securing);
        nbt.setBoolean("contested", contested);
        nbt.setBoolean("secured", secured);
        nbt.setInteger("timer", secureTimer);
    }

    @Override
    public double getMaxRenderDistanceSquared()
    {
        return Double.MAX_VALUE;
    }
}