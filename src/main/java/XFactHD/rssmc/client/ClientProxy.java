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

package XFactHD.rssmc.client;

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.api.capability.IGunHandler;
import XFactHD.rssmc.client.event.*;
import XFactHD.rssmc.client.keybind.*;
import XFactHD.rssmc.client.renderer.world.InWorldRenderHandler;
import XFactHD.rssmc.client.util.ArmorModelHandler;
import XFactHD.rssmc.client.util.ClientReference;
import XFactHD.rssmc.common.CommonProxy;
import XFactHD.rssmc.common.capability.gunHandler.GunHandlerStorage;
import XFactHD.rssmc.common.data.*;
import XFactHD.rssmc.common.entity.camera.AbstractEntityCamera;
import XFactHD.rssmc.common.entity.camera.EntityCamera;
import XFactHD.rssmc.common.items.gun.ItemGun;
import XFactHD.rssmc.common.net.*;
import XFactHD.rssmc.client.util.Sounds;
import XFactHD.rssmc.common.net.fx.*;
import XFactHD.rssmc.common.utils.Utils;
import XFactHD.rssmc.common.utils.logic.GadgetHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);
        MinecraftForge.EVENT_BUS.register(new ClientManager());
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        MinecraftForge.EVENT_BUS.register(UIEventHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(new DBNOClientEventHandler());
        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
        MinecraftForge.EVENT_BUS.register(new InWorldRenderHandler());
        ClientManager.registerModels();
        ClientManager.registerRenderers();
        ArmorModelHandler.initialize();
        Sounds.register();
    }

    @Override
    public void init(FMLInitializationEvent event)
    {
        super.init(event);
        KeyBindings.register();
        ClientRegistry.registerEntityShader(EntityCamera.class, new ResourceLocation("rssmc:shaders/desaturate.json"));
    }

    @Override
    public void postInit(FMLPostInitializationEvent event)
    {
        super.postInit(event);
        ClientReference.init();
    }

    @Override
    public void spawnParticle(EnumParticle particle, int dimension, double x, double y, double z)
    {
        if (particle == EnumParticle.OPEN_MIRROR)
        {
            Minecraft.getMinecraft().world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0, .1, 0);
        }
        else if (particle == EnumParticle.HEAL)
        {
            Minecraft.getMinecraft().world.spawnParticle(EnumParticleTypes.HEART, x, y, z, 0, .1, 0);
        }
        else if (particle == EnumParticle.BIG_EXPLOSION)
        {
            Minecraft.getMinecraft().world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, x, y, z, 0, .1, 0);
        }
        else
        {
            InWorldRenderHandler.addParticle(particle, x, y, z);
        }
    }

    @Override
    public void handleClientPacket(final IMessage message, final MessageContext ctx)
    {
        if (message instanceof PacketUpdateGadgetHandler)
        {
            addScheduledTask(new Runnable()
            {
                @Override
                public void run()
                {
                    GadgetHandler handler = GadgetHandler.getHandlerForPlayer(Minecraft.getMinecraft().player);
                    if (handler != null)
                    {
                        handler.deserializeNBT(((PacketUpdateGadgetHandler)message).nbt);
                    }
                }
            });
        }
        else if (message instanceof PacketSpawnParticle)
        {
            addScheduledTask(new Runnable()
            {
                @Override
                public void run()
                {
                    PacketSpawnParticle packet = (PacketSpawnParticle)message;
                    int dimension = Minecraft.getMinecraft().world.provider.getDimension();
                    RainbowSixSiegeMC.proxy.spawnParticle(packet.particle, dimension, packet.posX, packet.posY, packet.posZ);
                }
            });
        }
        else if (message instanceof PacketUpdateGun)
        {
            addScheduledTask(new Runnable()
            {
                @Override
                public void run()
                {
                    PacketUpdateGun packet = (PacketUpdateGun)message;
                    EntityPlayer player = Minecraft.getMinecraft().player;
                    ItemStack stack = player.inventory.getCurrentItem();
                    if (stack != null && stack.getItem() instanceof ItemGun)
                    {
                        IGunHandler handler = stack.getCapability(GunHandlerStorage.GUN_HANDLER_CAPABILITY, null);
                        if (handler.getPlayer() == null) { handler.setPlayer(player); }
                        handler.receiveUpdate(packet.getTag());
                    }
                }
            });
        }
        else if (message instanceof PacketEntityKilled)
        {
            addScheduledTask(new Runnable()
            {
                @Override
                public void run()
                {
                    UIEventHandler.INSTANCE.setLastKillStamp(getWorld().getTotalWorldTime());
                }
            });
        }
        else if (message instanceof PacketHealFX)
        {
            addScheduledTask(new Runnable()
            {
                @Override
                public void run()
                {
                    UIEventHandler.INSTANCE.setCurrentScreenEffect(EnumScreenEffect.HEAL);
                }
            });
        }
        else if (message instanceof PacketSetViewPoint)
        {
            addScheduledTask(new Runnable()
            {
                @Override
                public void run()
                {
                    PacketSetViewPoint packet = (PacketSetViewPoint)message;
                    UUID uuid = packet.uuid;
                    Entity entity = uuid != null ? Utils.getEntityByUUID(uuid) : null;
                    EntityPlayer player = Minecraft.getMinecraft().player;
                    if (Minecraft.getMinecraft().getRenderViewEntity() instanceof AbstractEntityCamera && entity != null)
                    {
                        getWorld().playSound(player.posX, player.posY, player.posZ, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.PLAYERS, .5F, 2F, false);
                    }
                    else
                    {
                        player.ignoreFrustumCheck = !player.ignoreFrustumCheck;
                        getWorld().playSound(player.posX, player.posY, player.posZ, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.PLAYERS, .5F, 1F, false);
                    }
                    Minecraft.getMinecraft().setRenderViewEntity(entity instanceof AbstractEntityCamera ? entity : Minecraft.getMinecraft().player);
                }
            });
        }
        else if (message instanceof PacketAddCollision)
        {
            addScheduledTask(new Runnable()
            {
                @Override
                public void run()
                {
                    PacketAddCollision packet = (PacketAddCollision)message;
                    if (packet.x == -1 && packet.y == -1 && packet.z == -1)
                    {
                        InWorldRenderHandler.clearCollisions();
                    }
                    else
                    {
                        InWorldRenderHandler.addCollisionPoint(packet.x, packet.y, packet.z);
                    }
                }
            });
        }
        else if (message instanceof PacketAddBulletTrace)
        {
            addScheduledTask(new Runnable()
            {
                @Override
                public void run()
                {
                    PacketAddBulletTrace packet = (PacketAddBulletTrace)message;
                    if (packet.pos == null)
                    {
                        InWorldRenderHandler.clearGunTraces();
                    }
                    else
                    {
                        InWorldRenderHandler.addGunTrace(packet.pos, packet.startVec, packet.endVec);
                    }
                }
            });
        }
        else if (message instanceof PacketSetTracked)
        {
            addScheduledTask(new Runnable()
            {
                @Override
                public void run()
                {
                    PacketSetTracked packet = (PacketSetTracked)message;
                    EntityPlayer player = Minecraft.getMinecraft().player;
                    if (packet.tracking)
                    {
                        player.getEntityData().setBoolean("tracked", true);
                        player.getEntityData().setLong("timestamp", packet.worldTime);
                    }
                    else
                    {
                        player.getEntityData().setBoolean("tracked", false);
                        player.getEntityData().setLong("timestamp", -1);
                    }
                }
            });
        }
        else if (message instanceof PacketSoundEffect)
        {
            addScheduledTask(new Runnable()
            {
                @Override
                public void run()
                {
                    EnumSoundEffect effect = ((PacketSoundEffect)message).getEffect();
                    if(effect == null) { return; }
                    Minecraft.getMinecraft().player.playSound(effect.getSoundEvent(), effect.getVolume(), effect.getPitch());
                }
            });
        }
    }

    @Override
    public void addScheduledTask(Runnable runnable)
    {
        Minecraft.getMinecraft().addScheduledTask(runnable);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public GameType getPlayersGameType(EntityPlayer player)
    {
        if (Minecraft.getMinecraft().playerController != null)
        {
            return Minecraft.getMinecraft().playerController.getCurrentGameType();
        }
        return player.capabilities.isCreativeMode ? GameType.CREATIVE : player.isSpectator() ? GameType.SPECTATOR : GameType.SURVIVAL;
    }

    @Override
    public World getWorld()
    {
        return Minecraft.getMinecraft().world;
    }

    public Entity getEntityByUUID(UUID uuid)
    {
        for (Entity entity : getWorld().loadedEntityList)
        {
            if (entity.getUniqueID().equals(uuid)) { return entity; }
        }
        return null;
    }

    @Override
    public void updateEntity(UUID uuid, NBTTagCompound nbt)
    {
        Entity entity = getEntityByUUID(uuid);
        if (entity != null)
        {
            entity.readFromNBT(nbt);
        }
    }
}