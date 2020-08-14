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
import XFactHD.rssmc.api.item.ISpecialRightClick;
import XFactHD.rssmc.common.data.EnumParticle;
import XFactHD.rssmc.common.utils.Reference;
import XFactHD.rssmc.common.utils.helper.ConfigHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.UUID;

public class DBNOEventHandler
{
    private HashMap<EntityPlayer, Float> lastHealth = new HashMap<>();
    private HashMap<EntityPlayer, Float> lastDamage = new HashMap<>();
    private static final AttributeModifier DBNO_SPEED_MODIFIER = new AttributeModifier(new UUID(534656132L, 345566212L), "generic.movementSpeed", -.7D, 2);
    private static NBTTagCompound queuedCapData = null;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @SuppressWarnings("ConstantConditions")
    public void onDeath(LivingDeathEvent event)
    {
        if (event.getEntity() instanceof EntityPlayer && !event.getEntity().world.isRemote)
        {
            EntityPlayer player = getPlayer(event);
            if (isPlayerDBNO(player))
            {
                if (getHandler(player).getRemainingHP() > 0 && getHandler(player).getTimeLeft() > 0)
                {
                    event.setCanceled(true);
                }
            }
            else
            {
                float dmgBelowZero = lastHealth.get(player) - lastDamage.get(player);
                event.setCanceled(getHandler(player).setDBNO(event.getSource(), dmgBelowZero));
                if (event.isCanceled()) { player.setHealth(0.5F); }
                ItemStack stack = player.inventory.getStackInSlot(player.inventory.currentItem);
                if (stack != null && stack.getItem() instanceof ISpecialRightClick)
                {
                    ((ISpecialRightClick)stack.getItem()).scrollOff(stack, player, player.world, EnumHand.MAIN_HAND);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @SuppressWarnings("ConstantConditions")
    public void onEntityHurt(LivingHurtEvent event)
    {
        if (event.getEntity() instanceof EntityPlayer && !event.getEntity().world.isRemote)
        {
            lastHealth.put(getPlayer(event), getPlayer(event).getHealth());
            lastDamage.put(getPlayer(event), event.getAmount());
            EntityPlayer player = getPlayer(event);
            if (isPlayerDBNO(player) && getHandler(player).getRemainingHP() > 0 && getHandler(player).getTimeLeft() > 0)
            {
                getHandler(player).hit(event.getSource(), event.getAmount());
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @SuppressWarnings("ConstantConditions")
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START && isPlayerDBNO(event.player) && !event.player.world.isRemote)
        {
            EntityPlayer player = event.player;
            getHandler(player).tick();
            if (!player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED).hasModifier(DBNO_SPEED_MODIFIER))
            {
                player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(DBNO_SPEED_MODIFIER);
            }
        }
        if (event.phase == TickEvent.Phase.START && getHandler(event.player).gotRevived())
        {
            event.player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(DBNO_SPEED_MODIFIER);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @SuppressWarnings("ConstantConditions")
    public void onLivingEvent(LivingEvent event)
    {
        if (event.getEntityLiving() instanceof EntityPlayer && getHandler(getPlayer(event)).isDBNO() && !event.getEntity().world.isRemote)
        {
            if (event instanceof PlayerSetSpawnEvent) { event.setCanceled(true); }
            else if (event instanceof PlayerEvent.BreakSpeed) { ((PlayerEvent.BreakSpeed) event).setNewSpeed(0); }
            else if (event instanceof PlayerInteractEvent.RightClickBlock) { event.setCanceled(true); }
            else if (event instanceof PlayerInteractEvent.EntityInteract) { event.setCanceled(true); }
            else if (event instanceof LivingEvent.LivingJumpEvent) { event.setCanceled(true); }
            else if (event instanceof AttackEntityEvent) { event.setCanceled(true); }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @SuppressWarnings("ConstantConditions")
    public void onLivingInteract(PlayerInteractEvent.EntityInteract event)
    {
        if (event.getTarget() instanceof EntityPlayer && !event.getEntity().world.isRemote)
        {
            EntityPlayer player = event.getEntityPlayer();
            EntityPlayer target = ((EntityPlayer)event.getTarget());
            if (isPlayerDBNO(target))
            {
                if (getHandler(target).getHelper() == null) { getHandler(target).setHelper(player); }
                if (getHandler(target).getHelper() == player)
                {
                    getHandler(target).revive(false);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @SuppressWarnings("ConstantConditions")
    public void onEntityHeal(LivingHealEvent event)
    {
        if (event.getEntity().world.isRemote || !(event.getEntityLiving() instanceof EntityPlayer)) { return; }
        if (!ConfigHandler.battleMode && getPlayer(event).getHealth() == 20)
        {
            event.setCanceled(true);
        }
        if (event.getAmount() == 8)
        {
            EntityLivingBase entity = event.getEntityLiving();
            if (entity instanceof EntityPlayer && getHandler(getPlayer(event)).isDBNO())
            {
                getHandler(getPlayer(event)).revive(true);
                event.setCanceled(true);
            }
            RainbowSixSiegeMC.proxy.spawnParticle(EnumParticle.HEAL, entity.dimension, entity.posX, entity.posY + entity.height, entity.posZ);
        }
        else
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent<Entity> event)
    {
        if(event.getObject() instanceof EntityPlayer)
        {
            event.addCapability(new ResourceLocation(Reference.MOD_ID, "dbno"), new DBNOHandlerProvider((EntityPlayer) event.getObject()));
            if (event.getObject().world.isRemote && queuedCapData != null)
            {
                event.getObject().getCapability(DBNOHandlerStorage.DBNO_HANDLER_CAPABILITY, null).deserializeNBT(queuedCapData);
                queuedCapData = null;
            }
        }
    }

    private static boolean isPlayerDBNO(EntityPlayer player)
    {
        return getHandler(player).isDBNO();
    }

    @SuppressWarnings("ConstantConditions")
    private static IDBNOHandler getHandler(EntityPlayer player)
    {
        return player.getCapability(DBNOHandlerStorage.DBNO_HANDLER_CAPABILITY, null);
    }

    private static EntityPlayer getPlayer(LivingEvent event)
    {
        return ((EntityPlayer)event.getEntity());
    }

    public static void setQueuedCapData(NBTTagCompound queuedCapData)
    {
        DBNOEventHandler.queuedCapData = queuedCapData;
    }
}