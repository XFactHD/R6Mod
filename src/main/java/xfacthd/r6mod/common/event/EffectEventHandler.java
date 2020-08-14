package xfacthd.r6mod.common.event;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.api.capability.ICapabilityEffect;
import xfacthd.r6mod.api.entity.IUsageTimeEntity;
import xfacthd.r6mod.api.interaction.IPickupTime;
import xfacthd.r6mod.api.tileentity.IUsageTimeTile;
import xfacthd.r6mod.common.capability.CapabilityEffect;
import xfacthd.r6mod.common.data.effects.AbstractEffect;
import xfacthd.r6mod.common.data.effects.EnumEffect;
import xfacthd.r6mod.common.entities.camera.EntityYokaiDrone;
import xfacthd.r6mod.common.net.NetworkHandler;
import xfacthd.r6mod.common.net.packets.PacketEffectClear;
import xfacthd.r6mod.common.net.packets.PacketEffectTrigger;
import xfacthd.r6mod.common.util.data.PointManager;

import java.util.*;

@Mod.EventBusSubscriber(modid = R6Mod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EffectEventHandler
{
    private static final Map<UUID, Map<EnumEffect, AbstractEffect>> effects = new HashMap<>();
    private static final Map<UUID, Long> sonicBurstMap = new HashMap<>();

    @SubscribeEvent
    public static void onServerTick(final TickEvent.PlayerTickEvent event)
    {
        if (event.phase != TickEvent.Phase.START || event.player.world.isRemote()) { return; }

        if (effects.containsKey(event.player.getUniqueID()))
        {
            List<EnumEffect> toRemove = new ArrayList<>();

            Map<EnumEffect, AbstractEffect> playersEffects = effects.get(event.player.getUniqueID());
            playersEffects.forEach((effect, instance) ->
            {
                instance.tick();
                if (instance.isInvalid()) { toRemove.add(effect); }
            });

            toRemove.forEach((playersEffects::remove));
        }

        //noinspection ConstantConditions
        event.player.getCapability(CapabilityEffect.EFFECT_CAPABILITY).ifPresent(ICapabilityEffect::tick);
    }

    @SubscribeEvent
    public static void onPlayerAttacked(final LivingDamageEvent event)
    {
        if (event.getEntity().world.isRemote() || !(event.getEntity() instanceof PlayerEntity)) { return; }

        PlayerEntity player = (PlayerEntity)event.getEntity();
        //noinspection ConstantConditions
        player.getCapability(CapabilityEffect.EFFECT_CAPABILITY).ifPresent((cap) ->
                event.setAmount(cap.onPlayerAttacked(event.getAmount())));

        DamageSource source = event.getSource();
        if (player.getTeam() != null && source instanceof EntityDamageSource && source.getImmediateSource() instanceof PlayerEntity)
        {
            PlayerEntity attacker = (PlayerEntity)source.getImmediateSource();
            if (player.getTeam().isSameTeam(attacker.getTeam()))
            {
                PointManager.awardPlayerDamage(attacker, player);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerInteractBlock(final PlayerInteractEvent.RightClickBlock event)
    {
        PlayerEntity player = event.getPlayer();

        if (player.world.isRemote) { return; }

        UUID id = player.getUniqueID();
        if (sonicBurstMap.containsKey(id))
        {
            long diff = player.world.getGameTime() - sonicBurstMap.get(id);
            sonicBurstMap.remove(id);

            BlockState state = player.world.getBlockState(event.getPos());
            if (!state.getBlock().hasTileEntity(state)) { return; } //No TileEntity => can't be IUsageTimer oder IPickupTimer

            //Make sure to only cancel if the request is within one "interact cycle" => 4-5 ticks
            if (diff <= 5)
            {
                TileEntity te = player.world.getTileEntity(event.getPos());
                if (te instanceof IUsageTimeTile)
                {
                    ((IUsageTimeTile)te).applySonicBurst(player, EntityYokaiDrone.INTERACT_COOLDOWN);
                }
                else if (te instanceof IPickupTime)
                {
                    ((IPickupTime)te).applySonicBurst(player, EntityYokaiDrone.INTERACT_COOLDOWN);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerInteractEntity(final PlayerInteractEvent.EntityInteract event)
    {
        PlayerEntity player = event.getPlayer();
        Entity entity = event.getTarget();

        UUID id = player.getUniqueID();
        if (sonicBurstMap.containsKey(id))
        {
            long diff = player.world.getGameTime() - sonicBurstMap.get(id);
            sonicBurstMap.remove(id);

            //Make sure to only cancel if the request is within one "interact cycle" => 4-5 ticks
            if (diff <= 5)
            {
                if (entity instanceof IUsageTimeEntity)
                {
                    ((IUsageTimeEntity)entity).applySonicBurst(player.getUniqueID(), EntityYokaiDrone.INTERACT_COOLDOWN);
                }
                else if (entity instanceof IPickupTime)
                {
                    ((IPickupTime) entity).applySonicBurst(player, EntityYokaiDrone.INTERACT_COOLDOWN);
                }
            }
        }
    }

    public static void applySonicBurst(PlayerEntity player)
    {
        sonicBurstMap.put(player.getUniqueID(), player.world.getGameTime());
    }

    public static void addEffect(ServerPlayerEntity player, UUID source, EnumEffect effect, int time)
    {
        AbstractEffect instance = effect.create(player, source, time, player.world.getGameTime());
        if (!effects.containsKey(player.getUniqueID())) { effects.put(player.getUniqueID(), new HashMap<>()); }
        effects.get(player.getUniqueID()).put(effect, instance);
        NetworkHandler.sendToPlayer(new PacketEffectTrigger(effect, time), player);
    }

    public static void addEffect(ServerPlayerEntity player, EnumEffect effect, int time)
    {
        AbstractEffect instance = effect.create(player, time, player.world.getGameTime());
        if (!effects.containsKey(player.getUniqueID())) { effects.put(player.getUniqueID(), new HashMap<>()); }
        effects.get(player.getUniqueID()).put(effect, instance);
        NetworkHandler.sendToPlayer(new PacketEffectTrigger(effect, time), player);
    }

    public static void removeEffect(ServerPlayerEntity player, EnumEffect effect)
    {
        if (hasEffect(player, effect))
        {
            effects.get(player.getUniqueID()).remove(effect).invalidate();
            NetworkHandler.sendToPlayer(new PacketEffectClear(effect), player);
        }
    }

    public static boolean hasEffect(ServerPlayerEntity player, EnumEffect effect)
    {
        return effects.containsKey(player.getUniqueID()) && effects.get(player.getUniqueID()).containsKey(effect);
    }

    public static void onPlayerLeave(ServerPlayerEntity player)
    {
        if (effects.containsKey(player.getUniqueID()))
        {
            effects.remove(player.getUniqueID()).forEach((effect, instance) -> instance.invalidate());
        }
    }
}