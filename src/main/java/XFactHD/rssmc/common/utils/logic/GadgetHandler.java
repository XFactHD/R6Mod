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

package XFactHD.rssmc.common.utils.logic;

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.common.net.PacketSetTracked;
import XFactHD.rssmc.common.net.PacketUpdateGadgetHandler;
import XFactHD.rssmc.common.utils.Utils;
import XFactHD.rssmc.common.utils.utilClasses.FootStep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.HashMap;
import java.util.UUID;

public class GadgetHandler implements INBTSerializable<NBTTagCompound>
{
    private static final HashMap<UUID, GadgetHandler> handlers = new HashMap<>();
    public static final int SILENT_STEP_TICKS = 160;
    public static final int SILENT_STEP_COOLDOWN = 200;
    private static final int MAX_FOOT_PRINTS = 3;

    private World world = null;
    private int ticks = 0;
    private UUID playerUUID;
    private EntityPlayer player;
    private boolean silentStep = false;
    private double timer = 0;
    private boolean footPrintScanner = false;
    private int footPrintsScanned = 0;
    private boolean tracking = false;
    private EntityPlayer trackedPlayer = null;
    private int trackTimer;
    private int trackingsLeft;

    public GadgetHandler(UUID playerUUID)
    {
        this.playerUUID = playerUUID;
        handlers.put(playerUUID, this);
    }

    //Silent step
    public void setSilentStep(boolean active)
    {
        silentStep = active;
    }

    public void switchSilentStep()
    {
        silentStep = !silentStep;
    }

    public boolean getSilentStep()
    {
        return silentStep;
    }

    public int getSilentStepTimer()
    {
        return (int) timer;
    }

    //Footprint scanner
    public void setFootPrintScanner(boolean active)
    {
        footPrintScanner = active;
    }

    public void switchFootPrintScanner()
    {
        footPrintScanner = !footPrintScanner;
    }

    public boolean getFootPrintScanner()
    {
        return footPrintScanner;
    }

    public void scanFootPrint() //TODO: Test
    {
        if (footPrintsScanned < MAX_FOOT_PRINTS)
        {
            Vec3d startVec = new Vec3d(player.posX, player.posY, player.posZ);
            Vec3d endVec = startVec.add(player.getLookVec().normalize().scale(3));
            RayTraceResult result = world.rayTraceBlocks(startVec, endVec);
            if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK && result.sideHit == EnumFacing.UP)
            {
                FootStep step = FootStepHandler.findFootStep(result.getBlockPos().up());
                if (step != null)
                {
                    trackedPlayer = world.getPlayerEntityByUUID(step.getPlayer());
                    MarkerHandler.addJackalMarker(Utils.getPlayerPosition(trackedPlayer), trackedPlayer);
                    trackingsLeft = 4;
                    footPrintsScanned += 1;
                    setTracking(true);
                    updateTrackedPlayer();
                }
            }
        }
    }

    public boolean isTracking()
    {
        return tracking;
    }

    public int getTrackTimer()
    {
        return trackTimer;
    }

    public int getTrackingsLeft()
    {
        return trackingsLeft;
    }

    private void setTracking(boolean tracking)
    {
        this.tracking = tracking;
        if (!tracking)
        {
            updateTrackedPlayer();
            trackedPlayer = null;
        }
        sendUpdate();
    }

    //Logic
    private void tick(EntityPlayer player)
    {
        if (this.player == null) { this.player = player; }

        ++ticks;
        if (ticks > 20) { ticks = 0; }

        if (silentStep)
        {
            --timer;
        }
        else if (timer < SILENT_STEP_TICKS)
        {
            timer += ((double) SILENT_STEP_TICKS / (double) SILENT_STEP_COOLDOWN);
            if (timer > SILENT_STEP_TICKS) { timer = SILENT_STEP_TICKS; }
        }
        if (timer <= 0)
        {
            setSilentStep(false);
        }

        if (tracking)
        {
            ++trackTimer;
            if (trackTimer >= 100)
            {
                trackTimer = 0;
                if (trackingsLeft > 0) { --trackingsLeft; }
                else { setTracking(false); }
            }
        }

        if (ticks % 5 == 0) { sendUpdate(); }
    }

    private void clientTick()
    {
        if (tracking)
        {
            ++trackTimer;
            if (trackTimer >= 100)
            {
                trackTimer = 0;
                if (trackingsLeft > 0) { --trackingsLeft; }
            }
        }
    }

    private void updateTrackedPlayer()
    {
        RainbowSixSiegeMC.NET.sendMessageToClient(new PacketSetTracked(tracking, world.getTotalWorldTime()), trackedPlayer);
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("uuid", playerUUID.toString());
        nbt.setBoolean("silentStep", silentStep);
        nbt.setDouble("silentStepTimer", timer);
        nbt.setBoolean("footPrintScanner", footPrintScanner);
        nbt.setInteger("footPrintsScanned", footPrintsScanned);
        if (trackedPlayer != null) { nbt.setUniqueId("trackedPlayer", trackedPlayer.getUniqueID()); }
        nbt.setInteger("trackingsLeft", trackingsLeft);
        nbt.setInteger("trackTimer", trackTimer);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        playerUUID = UUID.fromString(nbt.getString("uuid"));
        silentStep = nbt.getBoolean("silentStep");
        timer = nbt.getDouble("silentStepTimer");
        footPrintScanner = nbt.getBoolean("footPrintScanner");
        footPrintsScanned = nbt.getInteger("footPrintsScanned");
        trackingsLeft = nbt.getInteger("trackingsLeft");
        trackTimer = nbt.getInteger("trackTimer");
        if (nbt.hasKey("trackedPlayer"))
        {
            UUID uuid = nbt.getUniqueId("trackedPlayer");
            trackedPlayer = uuid != null ? RainbowSixSiegeMC.proxy.getWorld().getPlayerEntityByUUID(uuid) : null;
        }
        else { trackedPlayer = null; }
    }

    private void sendUpdate()
    {
        NBTTagCompound nbt = serializeNBT();
        World world = FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld();
        EntityPlayer player = world.getPlayerEntityByUUID(playerUUID);
        if (player != null)
        {
            RainbowSixSiegeMC.NET.sendMessageToClient(new PacketUpdateGadgetHandler(nbt), player);
        }
    }

    public static GadgetHandler getHandlerForPlayer(UUID uuid)
    {
        return handlers.get(uuid);
    }

    public static GadgetHandler getHandlerForPlayer(EntityPlayer player)
    {
        return handlers.get(player.getPersistentID());
    }

    public static void tickPlayersHandler(EntityPlayer player)
    {
        if (!handlers.containsKey(player.getPersistentID()))
        {
            handlers.put(player.getPersistentID(), new GadgetHandler(player.getPersistentID()));
            handlers.get(player.getPersistentID()).world = player.world;
            if (!player.world.isRemote)
            {
                handlers.get(player.getPersistentID()).sendUpdate();
            }
        }
        if (!player.world.isRemote) { handlers.get(player.getPersistentID()).tick(player); }
        else { handlers.get(player.getPersistentID()).clientTick(); }
    }

    public static void writeToNBT(NBTTagCompound nbt)
    {
        NBTTagList list = new NBTTagList();
        for (UUID uuid : handlers.keySet())
        {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("uuid", uuid.toString());
            tag.setTag("data", handlers.get(uuid).serializeNBT());
            list.appendTag(tag);
        }
        nbt.setTag("players", list);
    }

    public static void readFromNBT(NBTTagCompound nbt)
    {
        NBTTagList list = nbt.getTagList("players", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++)
        {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            UUID uuid = UUID.fromString(tag.getString("uuid"));
            GadgetHandler handler = new GadgetHandler(uuid);
            handler.deserializeNBT(tag.getCompoundTag("data"));
            if (handler.world != null && !handler.world.isRemote) { handler.sendUpdate(); }
            handlers.put(uuid, handler);
        }
    }
}