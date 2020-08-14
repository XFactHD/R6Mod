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

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.common.blocks.TileEntityGadget;
import XFactHD.rssmc.common.data.EnumGadget;
import XFactHD.rssmc.common.data.EnumParticle;
import XFactHD.rssmc.client.util.Sounds;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import XFactHD.rssmc.common.utils.propertyEnums.MirrorState;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;

public class TileEntityBlackMirror extends TileEntityGadget
{
    private boolean onReinforcement = false;
    private MirrorState state = MirrorState.INTACT;
    private int destructionTimer = 0;

    @Override
    public void update()
    {
        if (destructionTimer != 0)
        {
            destructionTimer -= 1;
            if (destructionTimer == 0)
            {
                world.playSound(null, pos, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 1, 1);
                setDestroyState(MirrorState.OPEN);
                boolean right = getState().getValue(PropertyHolder.RIGHT);
                EnumFacing facing = getState().getValue(PropertyHolder.FACING_CARDINAL);
                BlockPos adjPos = pos.offset(right ? facing.rotateYCCW() : facing.rotateY());
                TileEntity te = world.getTileEntity(adjPos);
                if (te instanceof TileEntityBlackMirror) { ((TileEntityBlackMirror)te).setDestroyState(MirrorState.OPEN); }
            }
        }
    }

    public void destroy()
    {
        if (getDestroyState() != MirrorState.INTACT) { return; }
        boolean right = getState().getValue(PropertyHolder.RIGHT);
        EnumFacing facing = getState().getValue(PropertyHolder.FACING_CARDINAL);

        setDestroyState(MirrorState.BLOWN);
        BlockPos adjPos = pos.offset(right ? facing.rotateYCCW() : facing.rotateY());
        TileEntity te = world.getTileEntity(adjPos);
        if (te instanceof TileEntityBlackMirror) { ((TileEntityBlackMirror)te).setDestroyState(MirrorState.BLOWN); }

        destructionTimer = 30;
        double posX = getParticleX(pos.getX(), facing, right);
        double posZ = getParticleZ(pos.getZ(), facing, right);
        RainbowSixSiegeMC.proxy.spawnParticle(EnumParticle.OPEN_MIRROR, world.provider.getDimension(), posX, pos.getY(), posZ);
        world.playSound(null, pos, Sounds.getGadgetSound(EnumGadget.BLACK_MIRROR, "open"), SoundCategory.BLOCKS, 1, 1);
    }

    public MirrorState getDestroyState()
    {
        return state;
    }

    public void setDestroyState(MirrorState state)
    {
        this.state = state;
        notifyBlockUpdate();
    }

    public boolean isOnReinforcement()
    {
        return onReinforcement;
    }

    public void setOnReinforcement(boolean onReinforcement)
    {
        this.onReinforcement = onReinforcement;
        notifyBlockUpdate();
    }

    private int getParticleX(int posX, EnumFacing facing, boolean right)
    {
        switch (facing)
        {
            case NORTH: return right ? posX : posX + 1;
            case SOUTH: return right ? posX + 1 : posX;
            case WEST:  return posX + 1;
            case EAST:  return posX;
        }
        return posX;
    }

    private int getParticleZ(int posZ, EnumFacing facing, boolean right)
    {
        switch (facing)
        {
            case NORTH: return posZ + 1;
            case SOUTH: return posZ;
            case WEST:  return right ? posZ + 1 : posZ;
            case EAST:  return right ? posZ : posZ + 1;
        }
        return posZ;
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        super.readCustomNBT(nbt);
        state = MirrorState.values()[nbt.getInteger("state")];
        onReinforcement = nbt.getBoolean("onreinf");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        super.writeCustomNBT(nbt);
        nbt.setInteger("state", state.ordinal());
        nbt.setBoolean("onreinf", onReinforcement);
    }
}