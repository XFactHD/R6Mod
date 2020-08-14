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

package XFactHD.rssmc.common;

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.api.capability.*;
import XFactHD.rssmc.common.capability.dbnoHandler.*;
import XFactHD.rssmc.common.capability.gunHandler.*;
import XFactHD.rssmc.common.data.EnumParticle;
import XFactHD.rssmc.common.net.*;
import XFactHD.rssmc.common.net.fx.*;
import XFactHD.rssmc.common.net.keybind.*;
import XFactHD.rssmc.common.utils.DeathEventHandler;
import XFactHD.rssmc.common.utils.helper.GuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class CommonProxy
{
    public void preInit(FMLPreInitializationEvent event)
    {
        CapabilityManager.INSTANCE.register(IGunHandler.class, new GunHandlerStorage(), GunHandler.class);
        CapabilityManager.INSTANCE.register(IDBNOHandler.class, new DBNOHandlerStorage(), DBNOHandler.class);

        RainbowSixSiegeMC.NET.registerMessageForServerSide(PacketSetBlockToReinforce.class,      PacketSetBlockToReinforce.Handler.class,       0);
        RainbowSixSiegeMC.NET.registerMessageForServerSide(PacketSetOperator.class,              PacketSetOperator.Handler.class,               1);
        RainbowSixSiegeMC.NET.registerMessageForServerSide(PacketGunCrafterAddAmmo.class,        PacketGunCrafterAddAmmo.Handler.class,         2);
        RainbowSixSiegeMC.NET.registerMessageForServerSide(PacketGunCrafterAddGun.class,         PacketGunCrafterAddGun.Handler.class,          3);
        RainbowSixSiegeMC.NET.registerMessageForServerSide(PacketGunCrafterConsumeItems.class,   PacketGunCrafterConsumeItems.Handler.class,    4);
        RainbowSixSiegeMC.NET.registerMessageForServerSide(PacketActivateGadget.class,           PacketActivateGadget.Handler.class,            5);
        RainbowSixSiegeMC.NET.registerMessageForClientSide(PacketUpdateGadgetHandler.class,      PacketUpdateGadgetHandler.Handler.class,       6);
        RainbowSixSiegeMC.NET.registerMessageForClientSide(PacketSpawnParticle.class,            PacketSpawnParticle.Handler.class,             7);
        RainbowSixSiegeMC.NET.registerMessageForServerSide(PacketSetBioContainerRange.class,     PacketSetBioContainerRange.Handler.class,      8);
        RainbowSixSiegeMC.NET.registerMessageForServerSide(PacketRightClickItemGadget.class,     PacketRightClickItemGadget.Handler.class,      9);
        RainbowSixSiegeMC.NET.registerMessageForClientSide(PacketUpdateDBNO.class,               PacketUpdateDBNO.Handler.class,               10);
        RainbowSixSiegeMC.NET.registerMessageForServerSide(PacketLeftClickItemGadget.class,      PacketLeftClickItemGadget.Handler.class,      11);
        RainbowSixSiegeMC.NET.registerMessageForServerSide(PacketSetMarker.class,                PacketSetMarker.Handler.class,                12);
        RainbowSixSiegeMC.NET.registerMessageForClientSide(PacketUpdateMarkers.class,            PacketUpdateMarkers.Handler.class,            13);
        RainbowSixSiegeMC.NET.registerMessageForClientSide(PacketADSAnimation.class,             PacketADSAnimation.Handler.class,             14);
        RainbowSixSiegeMC.NET.registerMessageForServerSide(PacketSetBombRange.class,             PacketSetBombRange.Handler.class,             15);
        RainbowSixSiegeMC.NET.registerMessageForServerSide(PacketRightClickItem.class,           PacketRightClickItem.Handler.class,           16);
        RainbowSixSiegeMC.NET.registerMessageForServerSide(PacketLeftClickItem.class,            PacketLeftClickItem.Handler.class,            17);
        RainbowSixSiegeMC.NET.registerMessageForServerSide(PacketReloadGun.class,                PacketReloadGun.Handler.class,                18);
        RainbowSixSiegeMC.NET.registerMessageForClientSide(PacketUpdateGun.class,                PacketUpdateGun.Handler.class,                19);
        RainbowSixSiegeMC.NET.registerMessageForClientSide(PacketEntityKilled.class,             PacketEntityKilled.Handler.class,             20);
        RainbowSixSiegeMC.NET.registerMessageForClientSide(PacketUpdateWorldData.class,          PacketUpdateWorldData.Handler.class,          21);
        RainbowSixSiegeMC.NET.registerMessageForClientSide(PacketSetViewPoint.class,             PacketSetViewPoint.Handler.class,             22);
        RainbowSixSiegeMC.NET.registerMessageForServerSide(PacketSwitchCamera.class,             PacketSwitchCamera.Handler.class,             23);
        RainbowSixSiegeMC.NET.registerMessageForClientSide(PacketUpdateObservationManager.class, PacketUpdateObservationManager.Handler.class, 24);
        RainbowSixSiegeMC.NET.registerMessageForClientSide(PacketAddCollision.class,             PacketAddCollision.Handler.class,             25);
        RainbowSixSiegeMC.NET.registerMessageForClientSide(PacketAddBulletTrace.class,           PacketAddBulletTrace.Handler.class,           26);
        RainbowSixSiegeMC.NET.registerMessageForClientSide(PacketSetTracked.class,               PacketSetTracked.Handler.class,               27);
        RainbowSixSiegeMC.NET.registerMessageForClientSide(PacketHealFX.class,                   PacketHealFX.Handler.class,                   28);
        //RainbowSixSiegeMC.NET.registerMessageForClientSide(PacketSyncConfig.class,               PacketSyncConfig.Handler.class,               29); //TODO: implement for stuff like battle mode
        RainbowSixSiegeMC.NET.registerMessageForServerSide(PacketSpeedLoaderLoad.class,          PacketSpeedLoaderLoad.Handler.class,          30);
        RainbowSixSiegeMC.NET.registerMessageForServerSide(PacketSwitchFiremode.class,           PacketSwitchFiremode.Handler.class,           31);
        RainbowSixSiegeMC.NET.registerMessageForClientSide(PacketSoundEffect.class,              PacketSoundEffect.Handler.class,              32);

        MinecraftForge.EVENT_BUS.register(new EventHandler());
        MinecraftForge.EVENT_BUS.register(new DBNOEventHandler());
        MinecraftForge.EVENT_BUS.register(new DeathEventHandler());

        NetworkRegistry.INSTANCE.registerGuiHandler(RainbowSixSiegeMC.INSTANCE, new GuiHandler());

        Content.preInit();
    }

    public void init(FMLInitializationEvent event)
    {
        Content.init();
    }

    public void postInit(FMLPostInitializationEvent event)
    {
        Content.postInit();
    }

    public void spawnParticle(EnumParticle particle, int dimension, double posX, double posY, double posZ)
    {
        RainbowSixSiegeMC.NET.sendMessageToArea(new PacketSpawnParticle(particle, posX, posY, posZ), new NetworkRegistry.TargetPoint(dimension, posX, posY, posZ, 64));
    }

    public void handleClientPacket(IMessage message, MessageContext ctx) {}

    public void addScheduledTask(Runnable runnable)
    {
        FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(runnable);
    }

    public GameType getPlayersGameType(EntityPlayer player)
    {
        return ((EntityPlayerMP)player).interactionManager.getGameType();
    }

    public World getWorld()
    {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld();
    }

    public void updateEntity(UUID uuid, NBTTagCompound nbt) {}
}