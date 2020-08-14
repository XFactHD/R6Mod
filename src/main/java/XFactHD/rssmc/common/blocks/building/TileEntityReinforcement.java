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

package XFactHD.rssmc.common.blocks.building;

import XFactHD.rssmc.common.blocks.TileEntityBase;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import XFactHD.rssmc.common.utils.utilClasses.DataCallable;
import XFactHD.rssmc.common.utils.helper.LogHelper;
import XFactHD.rssmc.common.utils.propertyEnums.Connection;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.List;

@SuppressWarnings("deprecation")
public class TileEntityReinforcement extends TileEntityBase implements ITickable
{
    public static final HashMap<BlockPos, DataCallable> dataGetters = new HashMap<>();
    private IBlockState stateToReinforce = Blocks.STONE.getDefaultState();
    private Connection con = Connection.UR;
    private boolean electrified = false;
    private boolean waitForData = true;

    public IBlockState getStateToReinforce()
    {
        return stateToReinforce;
    }

    public Connection getCon()
    {
        return con;
    }

    public boolean isElectrified()
    {
        return electrified;
    }

    public void setElectrified(boolean electrified, boolean updateOthers)
    {
        this.electrified = electrified;
        notifyBlockUpdate();
        if (updateOthers)
        {
            List<BlockPos> posList = Connection.getBlockPosListForPosAndCon(pos, con, getState().getValue(PropertyHolder.FACING_NOT_UP));
            for (BlockPos pos : posList)
            {
                if (pos == this.pos) { continue; }
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof TileEntityReinforcement)
                {
                    ((TileEntityReinforcement)te).setElectrified(electrified, false);
                }
            }
        }
    }

    @Override
    public void update()
    {
        if (!world.isRemote && waitForData)
        {
            try
            {
                readCustomNBT(dataGetters.get(pos).call());
                waitForData = false;
                dataGetters.remove(pos);
                notifyBlockUpdate();
            }
            catch (Exception e)
            {
                LogHelper.info("A TileEntityReinforcement at %s threw an error while getting data from its DataCallable! This is a bug!", pos);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        nbt.setString("blockName", stateToReinforce.getBlock().getRegistryName().toString());
        nbt.setInteger("blockMeta", stateToReinforce.getBlock().getMetaFromState(stateToReinforce));
        nbt.setInteger("con", con.ordinal());
        nbt.setBoolean("electrified", electrified);
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        stateToReinforce = Block.REGISTRY.getObject(new ResourceLocation(nbt.getString("blockName"))).getStateFromMeta(nbt.getInteger("blockMeta"));
        con = Connection.valueOf(nbt.getInteger("con"));
        waitForData = stateToReinforce == Blocks.STONE.getDefaultState();
        electrified = nbt.getBoolean("electrified");
    }
}