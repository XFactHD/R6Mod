package xfacthd.r6mod.common.event;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.ScoreHolderArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.api.capability.ICapabilityDBNO;
import xfacthd.r6mod.api.capability.ICapabilityEffect;
import xfacthd.r6mod.common.blocks.misc.BlockTeamSpawn;
import xfacthd.r6mod.common.capability.CapabilityDBNO;
import xfacthd.r6mod.common.capability.CapabilityEffect;
import xfacthd.r6mod.common.net.NetworkHandler;
import xfacthd.r6mod.common.net.packets.match.PacketKillfeedEntry;
import xfacthd.r6mod.common.tileentities.misc.TileEntityTeamSpawn;
import xfacthd.r6mod.common.util.Config;
import xfacthd.r6mod.common.util.data.*;

import java.util.*;

@Mod.EventBusSubscriber(modid = R6Mod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEventHandler
{
    @SubscribeEvent
    public static void onCommandExecute(final CommandEvent event) throws CommandSyntaxException
    {
        ParseResults<CommandSource> parse = event.getParseResults();
        MinecraftServer server = parse.getContext().getSource().getServer();
        ServerWorld world = parse.getContext().getSource().getWorld();
        MatchManager matches = R6WorldSavedData.get(world).getMatchManager();
        CameraManager cameras = R6WorldSavedData.get(world).getCameraManager();

        List<ParsedCommandNode<CommandSource>> nodes = parse.getContext().getNodes();
        Map<String, ParsedArgument<CommandSource, ?>> arguments = parse.getContext().getArguments();

        //Check if enough arguments were provided
        if (nodes.size() < 2 || arguments.isEmpty()) { return; }

        //Check if the team command was executed
        if (nodes.get(0).getNode().getName().equals("team")) //The team command was issued
        {
            String action = nodes.get(1).getNode().getName();
            switch (action)
            {
                case "join": //Someone is trying to join a team
                {
                    //Check if this was called by a player
                    ServerPlayerEntity player;
                    try { player = parse.getContext().getSource().asPlayer(); }
                    catch (CommandSyntaxException e)
                    {
                        event.setException(e);
                        event.setCanceled(true);
                        return;
                    }

                    String teamName = (String) arguments.get("team").getResult();
                    ScorePlayerTeam team = world.getScoreboard().getTeam(teamName);

                    //Check if the team is already playing, else clear last used camera
                    if (matches.isTeamPlaying(team)) //Can't join a team that is already playing
                    {
                        event.setException(new CommandException(new TranslationTextComponent("r6mod.command.team_playing.no_join")));
                        event.setCanceled(true);
                    }
                    else
                    {
                        cameras.resetLastUsedCam(player);
                    }
                    break;
                }
                case "leave": //Someone is removing players from all teams
                {
                    //Get provider of all player names affected
                    ScoreHolderArgument.NameProvider playerProvider = (ScoreHolderArgument.NameProvider) arguments.get("members").getResult();

                    //Check if any of the affected players are currently playing
                    List<ServerPlayerEntity> players = new ArrayList<>();
                    //noinspection ConstantConditions
                    playerProvider.getNames(parse.getContext().getSource(), null).forEach((name) ->
                    {
                        ServerPlayerEntity player = server.getPlayerList().getPlayerByUsername(name);
                        if (player != null)
                        {
                            if (player.getTeam() instanceof ScorePlayerTeam && matches.isTeamPlaying((ScorePlayerTeam)player.getTeam()))
                            {
                                event.setException(new CommandException(new TranslationTextComponent("r6mod.command.team_playing.no_leave")));
                                event.setCanceled(true);
                                return;
                            }
                            players.add(player);
                        }
                    });

                    //Clear last used camera on all players
                    players.forEach(cameras::resetLastUsedCam);
                    break;
                }
                case "empty":
                case "remove": //Someone is removing all players from a team or deleting a team
                {
                    String teamName = (String) arguments.get("team").getResult();
                    ScorePlayerTeam team = world.getScoreboard().getTeam(teamName);

                    //Check if team is already playing, else clear last used camera on all players
                    if (matches.isTeamPlaying(team)) //Can't clear or delete a team that is already playing
                    {
                        String msg = "r6mod.command.team_playing.no_" + action;
                        event.setException(new CommandException(new TranslationTextComponent(msg)));
                        event.setCanceled(true);
                    }
                    else
                        //noinspection ConstantConditions
                        if (team != null)
                    {
                        server.getPlayerList().getPlayers().forEach((player) ->
                        {
                            if (team.isSameTeam(player.getTeam())) { cameras.resetLastUsedCam(player); }
                        });
                    }
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public static void tick(final TickEvent.PlayerTickEvent event)
    {
        PlayerEntity player = event.player;

        CapabilityDBNO.getFrom(player).tick();

        if (!player.world.isRemote() && player.isSpectator())
        {
            Entity entity = ((ServerPlayerEntity)player).getSpectatingEntity();
            if (entity instanceof PlayerEntity && entity != player)
            {
                CapabilityDBNO.getFrom((PlayerEntity)entity).informSpectator(player);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerInteract(final PlayerInteractEvent.EntityInteract event)
    {
        if (event.getTarget() instanceof PlayerEntity)
        {
            PlayerEntity player = (PlayerEntity)event.getTarget();
            ICapabilityDBNO dbno = CapabilityDBNO.getFrom(player);

            if (dbno.isDBNO() && !dbno.isDead())
            {
                dbno.tryRevive(event.getPlayer());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerAttacked(final LivingHurtEvent event)
    {
        if (event.getEntityLiving() instanceof PlayerEntity)
        {
            PlayerEntity player = (PlayerEntity)event.getEntityLiving();
            CapabilityDBNO.getFrom(player).onAttacked(event.getSource(), event.getAmount());
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(final LivingDeathEvent event)
    {
        if (event.getEntityLiving().world.isRemote || !(event.getEntityLiving() instanceof ServerPlayerEntity)) { return; }

        ServerPlayerEntity victim = (ServerPlayerEntity)event.getEntityLiving();
        ICapabilityDBNO dbno = CapabilityDBNO.getFrom(victim);

        ServerWorld world = (ServerWorld)victim.world;
        MatchManager matches = R6WorldSavedData.get(world).getMatchManager();

        boolean cancel = false;
        boolean inMatch = matches.isTeamPlaying((ScorePlayerTeam)victim.getTeam());
        if (Config.INSTANCE.alwaysDbno || inMatch)
        {
            DamageSource source = event.getSource();
            cancel = !dbno.isDead() && dbno.putInDbno();

            PlayerEntity sourcePlayer = null;
            if (source.getImmediateSource() instanceof PlayerEntity)
            {
                sourcePlayer = ((PlayerEntity) source.getImmediateSource());
            }

            if (cancel)
            {
                if (inMatch && sourcePlayer != null)
                {
                    PointManager.awardPlayerInjure(sourcePlayer, victim, source);
                }

                event.setCanceled(true);
            }
            else
            {
                if (inMatch)
                {
                    if (sourcePlayer != null)
                    {
                        PointManager.awardPlayerKill(sourcePlayer, victim, source);
                    }

                    victim.getWorldScoreboard().forAllObjectives(R6Command.DEATHS, victim.getScoreboardName(), Score::incrementScore);
                    sendKillfeedInfo(victim, victim, source);
                }
            }
        }

        if (!cancel) { CapabilityEffect.getFrom(victim).invalidate(); }
    }

    private static void sendKillfeedInfo(PlayerEntity killer, PlayerEntity victim, DamageSource source)
    {
        ServerWorld world = (ServerWorld)victim.world;
        MatchManager matches = R6WorldSavedData.get(world).getMatchManager();
        for (ServerPlayerEntity player : matches.getPlayersInMatch(victim.world, (ScorePlayerTeam) victim.getTeam()))
        {
            NetworkHandler.sendToPlayer(new PacketKillfeedEntry(player, killer, victim, source), player);
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(final PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.getPlayer().world.isRemote) { return; }

        ServerWorld world = (ServerWorld) event.getPlayer().world;
        ServerPlayerEntity player = (ServerPlayerEntity)event.getPlayer();
        R6WorldSavedData.get(world).getCameraManager().sendToPlayer(player);

        if (!(event.getPlayer().getTeam() instanceof ScorePlayerTeam)) { return; }

        ScorePlayerTeam team = (ScorePlayerTeam)player.getTeam();
        MatchManager matches = R6WorldSavedData.get(world).getMatchManager();

        //If the player disconnected before the match ended, reset their spawn when they join the next time
        if (matches.hasOriginalSpawn(player) && !matches.isTeamPlaying(team))
        {
            player.func_242111_a(World.OVERWORLD, matches.retrieveOriginalSpawn(player), 0, true, false);
        }
        else if (matches.isTeamPlaying(team))
        {
            NetworkHandler.sendToPlayer(matches.getReconnectPacket(player), player);
        }
    }

    @SubscribeEvent
    public static void onPlayerLeave(final PlayerEvent.PlayerLoggedOutEvent event)
    {
        if (event.getPlayer().world.isRemote) { return; }

        ServerWorld world = (ServerWorld) event.getPlayer().world;
        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();

        //noinspection ConstantConditions
        player.getCapability(CapabilityEffect.EFFECT_CAPABILITY).ifPresent(ICapabilityEffect::invalidate);

        R6WorldSavedData.get(world).getCameraManager().leaveCamera(player);
        R6WorldSavedData.get(world).getCameraManager().resetLastUsedCam(player);

        EffectEventHandler.onPlayerLeave(player);
    }

    @SubscribeEvent
    public static void onBlockBreak(final BlockEvent.BreakEvent event)
    {
        if (event.getState().getBlock() instanceof BlockTeamSpawn)
        {
            TileEntity te = event.getWorld().getTileEntity(event.getPos());
            if (te instanceof TileEntityTeamSpawn)
            {
                boolean locked = ((TileEntityTeamSpawn)te).isLocked();
                if (locked) { event.setCanceled(true); }
            }
        }
    }
}