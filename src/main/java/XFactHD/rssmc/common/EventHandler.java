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
import XFactHD.rssmc.common.blocks.BlockBase;
import XFactHD.rssmc.common.capability.dbnoHandler.DBNOHandlerStorage;
import XFactHD.rssmc.common.capability.gunHandler.GunHandlerStorage;
import XFactHD.rssmc.common.data.EnumOperator;
import XFactHD.rssmc.common.data.EnumSide;
import XFactHD.rssmc.common.data.team.StatusController;
import XFactHD.rssmc.common.items.gun.ItemGun;
import XFactHD.rssmc.common.net.PacketEntityKilled;
import XFactHD.rssmc.common.utils.Damage;
import XFactHD.rssmc.common.utils.RSSWorldData;
import XFactHD.rssmc.common.utils.helper.ConfigHandler;
import XFactHD.rssmc.common.utils.logic.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class EventHandler
{
    private HashMap<EntityPlayer, Integer> ticks = new HashMap<>();
    private ArrayList<EntityPlayer> hadFirstTick = new ArrayList<>();
    private static final AttributeModifier MAX_HEALTH_MODIFIER = new AttributeModifier(new UUID(534676132L, 345566213L), "generic.maxHealth", .4, 2);

    @SubscribeEvent
    public void breakBlock(BreakSpeed event)
    {
        IBlockState state = event.getEntityPlayer().world.getBlockState(event.getPos());
        if (state.getBlock() instanceof BlockBase && ((BlockBase)state.getBlock()).isUnbreakableInSurvivalMode(state))
        {
            event.setCanceled(!event.getEntityPlayer().isCreative() || ConfigHandler.battleMode);
        }
        else if (ConfigHandler.battleMode)
        {
            if (event.getState().getBlock() == Blocks.GLASS_PANE)
            {
                event.setNewSpeed(Float.MAX_VALUE);
            }
            else
            {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void loadWorld(WorldEvent.Load event)
    {
        if (World.MAX_ENTITY_RADIUS < 4D)
        {
            World.MAX_ENTITY_RADIUS = 4D;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @SuppressWarnings("ConstantConditions")
    public void tickPlayer(TickEvent.PlayerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START && !event.player.world.isRemote)
        {
            if (!hadFirstTick.contains(event.player))
            {
                hadFirstTick.add(event.player);
                event.player.getCapability(DBNOHandlerStorage.DBNO_HANDLER_CAPABILITY, null).firstPlayerTick();
            }
            if (!event.player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH).hasModifier(MAX_HEALTH_MODIFIER))
            {
                event.player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH).applyModifier(MAX_HEALTH_MODIFIER);
            }
            GadgetHandler.tickPlayersHandler(event.player);
            if (ConfigHandler.battleMode || event.player.getHealth() > 20) { event.player.hurtResistantTime = 0; }
        }

        //FIXME: can't cancel hurt animation by removing health on server and client
        //Slowly trickle down health to 20 when player was overhealed
        if (event.phase == TickEvent.Phase.START && !event.player.world.isRemote)
        {
            ticks.putIfAbsent(event.player, 0);
            if (event.player.getHealth() > 20)
            {
                ticks.replace(event.player, ticks.get(event.player) + 1);
                if (ticks.get(event.player) >= 40)
                {
                    event.player.setHealth(event.player.getHealth() - .2F);
                    ticks.replace(event.player, 0);
                }
            }
        }

        //Prevent player from sprinting while aiming TODO: add echo, grzmot and gu effect to this
        ItemStack stack = event.player.inventory.getCurrentItem();
        if (stack != null && stack.getItem() instanceof ItemGun && stack.getCapability(GunHandlerStorage.GUN_HANDLER_CAPABILITY, null).isAiming())
        {
            event.player.setSprinting(false);
        }

        //Tick FootstepHandler for footstep placement
        if (event.phase == TickEvent.Phase.START && StatusController.doesOperatorExist(event.player.world, EnumOperator.JACKAL))
        {
            if (FootStepHandler.world == null) { FootStepHandler.world = event.player.world; }
            if (!event.player.world.isRemote)
            {
                if (StatusController.getPlayersOperator(event.player) == EnumOperator.JACKAL)
                {
                    FootStepHandler.tick();
                }
                if (StatusController.getPlayersSide(event.player) == EnumSide.DEFFENDER)
                {
                    FootStepHandler.tickDefender(event.player);
                }
            }
            else
            {
                if (StatusController.getPlayersOperator(event.player) == EnumOperator.JACKAL)
                {
                    FootStepHandler.clientTick();
                }
            }
        }
    }

    @SubscribeEvent
    public void tickWorld(TickEvent.WorldTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            MarkerHandler.tick();
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerRespawnEvent event)
    {
        if (ConfigHandler.battleMode)
        {
            event.player.setGameType(GameType.SPECTATOR);
        }
    }

    @SubscribeEvent
    public void entityKilled(LivingDeathEvent event)
    {
        if (!event.getEntity().world.isRemote)
        {
            DamageSource source = event.getSource();
            if ((source instanceof EntityDamageSource && ((EntityDamageSource)source).damageType.equals("player")) || source instanceof Damage.CustomDamageSource)
            {
                RainbowSixSiegeMC.NET.sendMessageToClient(new PacketEntityKilled(), (EntityPlayer)source.getEntity());
            }
            if (event.getEntityLiving() instanceof EntityPlayer)
            {
                FootStepHandler.removeFootStepsForPlayer((EntityPlayer)event.getEntityLiving());
            }
        }

    }

    @SubscribeEvent
    public void onPlayerJoined(PlayerEvent.PlayerLoggedInEvent event)
    {
        RSSWorldData.get(event.player.world).sendToClient(event.player);
    }

    @SubscribeEvent
    public void onPlayerLeft(PlayerEvent.PlayerLoggedOutEvent event)
    {
        RSSWorldData.get(event.player.world).getObservationManager().removePlayerFromCamFeeds(event.player.getUniqueID());
        FootStepHandler.removeFootStepsForPlayer(event.player);
    }
}