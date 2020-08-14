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

package XFactHD.rssmc.common.data.team;

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.common.entity.camera.*;
import XFactHD.rssmc.common.net.PacketSetViewPoint;
import XFactHD.rssmc.common.net.PacketUpdateObservationManager;
import XFactHD.rssmc.common.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.UUID;

@SuppressWarnings("SynchronizeOnNonFinalField")
public class ObservationManager implements INBTSerializable<NBTTagCompound> //FIXME: cameras report null player when used after restart
{
    private static final Comparator<AbstractEntityCamera> camComp = new Comparator<AbstractEntityCamera>() {
        @Override
        public int compare(AbstractEntityCamera cam1, AbstractEntityCamera cam2)
        {
            if (cam1 instanceof EntityCamera && cam2 instanceof EntityBlackEyeCam)
            {
                return -1;
            }
            else if (cam1 instanceof EntityBlackEyeCam && cam2 instanceof EntityCamera)
            {
                return 1;
            }
            else if ((cam1 instanceof EntityCamera || cam1 instanceof EntityBlackEyeCam) && cam2 instanceof EntityYokaiCam)
            {
                return -1;
            }
            else if (cam1 instanceof EntityYokaiCam && (cam2 instanceof EntityCamera || cam2 instanceof EntityBlackEyeCam))
            {
                return 1;
            }
            else if (cam1 instanceof EntityDroneCam && cam2 instanceof EntityTwitchDroneCam)
            {
                return -1;
            }
            else if (cam1 instanceof EntityTwitchDroneCam && cam2 instanceof EntityDroneCam)
            {
                return 1;
            }
            return 0;
        }
    };
    private ArrayList<UUID> cameraUUIDs = new ArrayList<>();
    private ArrayList<AbstractEntityCamera> cameras = new ArrayList<>();
    private boolean systemHacked = false;

    public ArrayList<AbstractEntityCamera> getObservableCameras(EntityPlayer player)
    {
        if ((!cameraUUIDs.isEmpty() && cameras.isEmpty()) || cameraUUIDs.size() != cameras.size()) { initializeCollection(); }
        ArrayList<AbstractEntityCamera> cams = new ArrayList<>();
        synchronized (cameras)
        {
            for (AbstractEntityCamera camera : cameras)
            {
                if (camera.canBeViewedBy(player))
                {
                    cams.add(camera);
                }
            }
        }
        cams.sort(camComp);
        return cams;
    }

    public void addCamera(AbstractEntityCamera camera)
    {
        synchronized (cameras)
        {
            if (!cameraUUIDs.contains(camera.getUniqueID())) { cameraUUIDs.add(camera.getUniqueID()); }
            if (!cameras.contains(camera)) { cameras.add(camera); }
            sendToClients();
        }
    }

    public void removeCamera(AbstractEntityCamera camera)
    {
        synchronized (cameras)
        {
            cameraUUIDs.remove(camera.getUniqueID());
            cameras.remove(camera);
            sendToClients();
        }
    }

    public boolean hasCamera(AbstractEntityCamera camera)
    {
        return cameraUUIDs.contains(camera.getUniqueID());
    }

    //Only used by player logged out event as it doesn't update the removed client
    public void removePlayerFromCamFeeds(UUID uuid)
    {
        synchronized (cameras)
        {
            for (AbstractEntityCamera camera : cameras)
            {
                if (camera.isViewedBy(uuid))
                {
                    camera.removeViewer(uuid);
                }
            }
        }
    }

    public void removePlayerFromCamFeeds(EntityPlayer player)
    {
        synchronized (cameras)
        {
            for (AbstractEntityCamera camera : cameras)
            {
                if (camera.isViewedBy(player.getUniqueID()))
                {
                    camera.removeViewer(player.getUniqueID());
                    RainbowSixSiegeMC.NET.sendMessageToClient(new PacketSetViewPoint(null), player);
                }
            }
        }
    }

    public boolean isInCam(EntityPlayer player)
    {
        synchronized (cameras)
        {
            for (AbstractEntityCamera camera : cameras)
            {
                if (camera.isViewedBy(player.getUniqueID()))
                {
                    return true;
                }
            }
            return false;
        }
    }

    public void moveToNextCam(EntityPlayer player)
    {
        synchronized (cameras)
        {
            if (cameras.size() == 1) { return; }
            int index = 0;
            for (AbstractEntityCamera camera : cameras)
            {
                if (camera.isViewedBy(player.getUniqueID()))
                {
                    index = cameras.indexOf(camera);
                    break;
                }
            }
            cameras.get(index).removeViewer(player.getUniqueID());
            index += 1;
            if (index == cameras.size()) { index = 0; }
            cameras.get(index).addViewer(player.getUniqueID());
            RainbowSixSiegeMC.NET.sendMessageToClient(new  PacketSetViewPoint(cameras.get(index).getUniqueID()), player);
        }
    }

    public void moveToPriorCam(EntityPlayer player)
    {
        synchronized (cameras)
        {
            if (cameras.size() == 1) { return; }
            int index = 0;
            for (AbstractEntityCamera camera : cameras)
            {
                if (camera.isViewedBy(player.getUniqueID()))
                {
                    index = cameras.indexOf(camera);
                    break;
                }
            }
            cameras.get(index).removeViewer(player.getUniqueID());
            index -= 1;
            if (index < 0) { index = cameras.size() - 1; }
            cameras.get(index).addViewer(player.getUniqueID());
            RainbowSixSiegeMC.NET.sendMessageToClient(new  PacketSetViewPoint(cameras.get(index).getUniqueID()), player);
        }
    }

    public void hackCams()
    {
        systemHacked = true;
        sendToClients();
    }

    public boolean isSystemHacked()
    {
        return systemHacked;
    }

    private void initializeCollection()
    {
        cameras.clear();
        for (UUID uuid : cameraUUIDs)
        {
            Entity entity = Utils.getEntityByUUID(uuid);
            if (entity instanceof AbstractEntityCamera)
            {
                cameras.add((AbstractEntityCamera)entity);
            }
        }
    }

    private World world()
    {
        return RainbowSixSiegeMC.proxy.getWorld();
    }

    private void sendToClients()
    {
        RainbowSixSiegeMC.NET.sendMessageToAllClients(new PacketUpdateObservationManager(serializeNBT()));
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList list = new NBTTagList();
        for (UUID uuid : cameraUUIDs)
        {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setUniqueId("uuid", uuid);
            list.appendTag(tag);
        }
        nbt.setTag("cameras", list);
        nbt.setBoolean("hacked", systemHacked);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        NBTTagList list = nbt.getTagList("cameras", Constants.NBT.TAG_COMPOUND);
        systemHacked = nbt.getBoolean("hacked");
        cameraUUIDs.clear();
        for (int i = 0; i < list.tagCount(); i++)
        {
            cameraUUIDs.add(list.getCompoundTagAt(i).getUniqueId("uuid"));
        }
    }
}