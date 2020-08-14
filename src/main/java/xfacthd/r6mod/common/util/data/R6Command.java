package xfacthd.r6mod.common.util.data;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.*;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.TeamArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.scoreboard.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.common.net.NetworkHandler;
import xfacthd.r6mod.common.net.packets.match.PacketMatchUpdate;
import xfacthd.r6mod.common.tileentities.misc.TileEntityTeamSpawn;
import xfacthd.r6mod.common.util.TextStyles;

import java.util.*;

public class R6Command
{
    private static final DynamicCommandExceptionType TEAM_ALREADY_PLAYING = new DynamicCommandExceptionType((team) -> new TranslationTextComponent("commands.r6mod.team.already_playing", team));
    private static final DynamicCommandExceptionType TEAM_NOT_PLAYING = new DynamicCommandExceptionType((team) -> new TranslationTextComponent("commands.r6mod.team.not_playing", team));
    private static final DynamicCommandExceptionType TEAM_NO_SPAWN = new DynamicCommandExceptionType((team) -> new TranslationTextComponent("commands.r6mod.team.no_spawn", team));
    private static final DynamicCommandExceptionType TEAM_SPAWN_NOT_FOUND = new DynamicCommandExceptionType((team) -> new TranslationTextComponent("commands.r6mod.team.no_spawn", team));
    private static final DynamicCommandExceptionType TEAM_SPAWN_LOCKED = new DynamicCommandExceptionType((team) -> new TranslationTextComponent("commands.r6mod.team.spawn_locked", team));
    private static final DynamicCommandExceptionType TEAM_EMPTY = new DynamicCommandExceptionType((team) -> new TranslationTextComponent("commands.r6mod.team.empty", team));
    private static final DynamicCommandExceptionType TEAM_TOO_LARGE = new DynamicCommandExceptionType((team) -> new TranslationTextComponent("commands.r6mod.team.too_large", team));
    private static final SimpleCommandExceptionType MATCH_START_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.r6mod.match.start_failed"));
    private static final SimpleCommandExceptionType MATCH_STOP_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.r6mod.match.stop_failed"));

    public static final ScoreCriteria POINTS = new ScoreCriteria("r6mod_points");
    public static final ScoreCriteria KILLS = new ScoreCriteria("r6mod_kills");
    public static final ScoreCriteria ASSISTS = new ScoreCriteria("r6mod_assists");
    public static final ScoreCriteria DEATHS = new ScoreCriteria("r6mod_deaths");

    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(Commands.literal(R6Mod.MODID).requires((source) -> source.hasPermissionLevel(2))
                .then(Commands.literal("start_game")
                        .then(Commands.argument("team1", TeamArgument.team())
                                .then(Commands.argument("team2", TeamArgument.team())
                                        .then(Commands.argument("friendlyfire", BoolArgumentType.bool())
                                                .executes(R6Command::startGame)))))
                .then(Commands.literal("list_games")
                        .executes(R6Command::listGames))
                .then(Commands.literal("end_game")
                        .then(Commands.argument("any_team", TeamArgument.team())
                                .executes(R6Command::endGame)))
                .then(Commands.literal("clear_cams").requires((source -> source.hasPermissionLevel(4)))
                        .executes(R6Command::clearCameras)));
    }



    private static int startGame(CommandContext<CommandSource> context) throws CommandSyntaxException
    {
        ServerWorld world = context.getSource().getWorld();
        MatchManager matches = R6WorldSavedData.get(context.getSource().getWorld()).getMatchManager();

        //Retrieve arguments
        ScorePlayerTeam team1 = TeamArgument.getTeam(context, "team1");
        ScorePlayerTeam team2 = TeamArgument.getTeam(context, "team2");
        boolean friendlyFire = BoolArgumentType.getBool(context, "friendlyfire");

        //Check if one of the teams is already playing
        if (matches.isTeamPlaying(team1)) { throw TEAM_ALREADY_PLAYING.create(team1); }
        if (matches.isTeamPlaying(team2)) { throw TEAM_ALREADY_PLAYING.create(team2); }
        context.getSource().sendFeedback(new TranslationTextComponent("commands.r6mod.team.not_playing"), true);

        //Get players in teams
        List<ServerPlayerEntity> playersTeam1 = getPlayersInTeam(world, team1);
        List<ServerPlayerEntity> playersTeam2 = getPlayersInTeam(world, team2);

        //Check team sizes
        if (playersTeam1.size() == 0) { throw TEAM_EMPTY.create(team1.getName()); }
        //if (playersTeam2.size() == 0) { throw TEAM_EMPTY.create(team2.getName()); }
        if (playersTeam1.size() > 5) { throw TEAM_TOO_LARGE.create(team1.getName()); }
        if (playersTeam2.size() > 5) { throw TEAM_TOO_LARGE.create(team2.getName()); }
        context.getSource().sendFeedback(new TranslationTextComponent("commands.r6mod.team.size_correct"), true);

        //Check team spawns
        if (!matches.hasSpawn(team1)) { throw TEAM_NO_SPAWN.create(team1.getName()); }
        if (!matches.hasSpawn(team2)) { throw TEAM_NO_SPAWN.create(team2.getName()); }
        context.getSource().sendFeedback(new TranslationTextComponent("commands.r6mod.team.spawn_exists"), true);

        //Lock team spawns
        lockTeamSpawn(world, matches.getSpawn(team1), team1.getName());
        lockTeamSpawn(world, matches.getSpawn(team2), team2.getName());
        context.getSource().sendFeedback(new TranslationTextComponent("commands.r6mod.team.spawn_locked"), true);

        //Configure teams
        team1.setCollisionRule(Team.CollisionRule.ALWAYS);
        team1.setNameTagVisibility(Team.Visible.HIDE_FOR_OTHER_TEAMS);
        team1.setAllowFriendlyFire(friendlyFire);
        team2.setCollisionRule(Team.CollisionRule.ALWAYS);
        team2.setNameTagVisibility(Team.Visible.HIDE_FOR_OTHER_TEAMS);
        team2.setAllowFriendlyFire(friendlyFire);
        context.getSource().sendFeedback(new TranslationTextComponent("commands.r6mod.team.configured"), true);

        //Save the match to disk
        if (!matches.addMatch(team1, team2)) { throw MATCH_START_FAILED.create(); }
        context.getSource().sendFeedback(new TranslationTextComponent("commands.r6mod.match.saved"), true);

        //Inform players
        playersTeam1.forEach((player) -> NetworkHandler.sendToPlayer(new PacketMatchUpdate(team1.getName(), team2.getName(), PacketMatchUpdate.Type.START), player));
        playersTeam2.forEach((player) -> NetworkHandler.sendToPlayer(new PacketMatchUpdate(team1.getName(), team2.getName(), PacketMatchUpdate.Type.START), player));
        context.getSource().sendFeedback(new TranslationTextComponent("commands.r6mod.team.informed"), true);

        //Add scores to players
        addAndClearScore(world, playersTeam1);
        addAndClearScore(world, playersTeam2);

        //Configure team spawns, preserve players original spawns and teleport players to team spawns
        playersTeam1.forEach((player) ->
        {
            BlockPos pos = matches.getSpawn(team1);
            matches.putOriginalSpawn(player);
            player.setSpawnPoint(pos, true, false, DimensionType.OVERWORLD);
            player.teleport(world, pos.getX(), pos.getY(), pos.getZ(), player.rotationYaw, player.rotationPitch);
        });
        playersTeam2.forEach((player) ->
        {
            BlockPos pos = matches.getSpawn(team2);
            matches.putOriginalSpawn(player);
            player.setSpawnPoint(pos, true, false, DimensionType.OVERWORLD);
            player.teleport(world, pos.getX(), pos.getY(), pos.getZ(), player.rotationYaw, player.rotationPitch);
        });
        context.getSource().sendFeedback(new TranslationTextComponent("commands.r6mod.team.teleported"), true);

        //Respond to command execution
        context.getSource().sendFeedback(new TranslationTextComponent("commands.r6mod.match.started", team1.getName(), team2.getName()), true);

        return 0;
    }

    private static int listGames(CommandContext<CommandSource> context)
    {
        Map<String, String> matches = R6WorldSavedData.get(context.getSource().getWorld()).getMatchManager().getMatchList();
        if (matches.isEmpty())
        {
            context.getSource().sendFeedback(new TranslationTextComponent("commands.r6mod.match.list_empty"), true);
        }
        else
        {
            context.getSource().sendFeedback(new TranslationTextComponent("commands.r6mod.match.list"), true);
            matches.forEach((team1, team2) -> context.getSource().sendFeedback(new TranslationTextComponent("commands.r6mod.match.list_entry", team1, team2), true));
        }

        return 0;
    }

    private static int endGame(CommandContext<CommandSource> context) throws CommandSyntaxException
    {
        ServerWorld world = context.getSource().getWorld();
        MatchManager matches = R6WorldSavedData.get(context.getSource().getWorld()).getMatchManager();

        ScorePlayerTeam team1 = TeamArgument.getTeam(context, "any_team");
        if (!matches.isTeamPlaying(team1)) { throw TEAM_NOT_PLAYING.create(team1.getName()); }

        ScorePlayerTeam team2 = matches.getEnemyTeam(world, team1);

        //Get players in teams
        List<ServerPlayerEntity> playersTeam1 = getPlayersInTeam(world, team1);
        List<ServerPlayerEntity> playersTeam2 = getPlayersInTeam(world, team2);

        //Reset players spawns to their original spawns
        playersTeam1.forEach((player) ->
        {
            BlockPos pos = matches.retrieveOriginalSpawn(player);
            player.setSpawnPoint(pos, true, false, DimensionType.OVERWORLD);
        });
        playersTeam2.forEach((player) ->
        {
            BlockPos pos = matches.retrieveOriginalSpawn(player);
            player.setSpawnPoint(pos, true, false, DimensionType.OVERWORLD);
        });

        //Remove scores from players
        removeScore(world, playersTeam1);
        removeScore(world, playersTeam2);

        //Inform players
        playersTeam1.forEach((player) -> NetworkHandler.sendToPlayer(new PacketMatchUpdate(team1.getName(), team2.getName(), PacketMatchUpdate.Type.FINISH), player));
        playersTeam2.forEach((player) -> NetworkHandler.sendToPlayer(new PacketMatchUpdate(team1.getName(), team2.getName(), PacketMatchUpdate.Type.FINISH), player));
        context.getSource().sendFeedback(new TranslationTextComponent("commands.r6mod.team.informed"), true);

        //Unlock team spawns
        unlockTeamSpawn(world, matches.getSpawn(team1), team1.getName());
        unlockTeamSpawn(world, matches.getSpawn(team2), team2.getName());
        context.getSource().sendFeedback(new TranslationTextComponent("commands.r6mod.team.spawn_unlocked"), true);

        //Remove the match from storage
        if (!matches.removeMatch(team1, team2)) { throw MATCH_STOP_FAILED.create(); }

        //Respond to command execution
        context.getSource().sendFeedback(new TranslationTextComponent("commands.r6mod.match.stopped", team1.getName(), team2.getName()), true);

        return 0;
    }

    private static int clearCameras(CommandContext<CommandSource> context)
    {
        boolean result = R6WorldSavedData.get(context.getSource().getWorld()).getCameraManager().clearCameras();
        if (result) { context.getSource().sendFeedback(new TranslationTextComponent("commands.r6mod.cameras.cleared"), true); }
        else { context.getSource().sendFeedback(new TranslationTextComponent("commands.r6mod.cameras.already_empty").setStyle(TextStyles.RED), true); }
        return 0;
    }



    private static List<ServerPlayerEntity> getPlayersInTeam(ServerWorld world, ScorePlayerTeam team)
    {
        List<ServerPlayerEntity> players = new ArrayList<>();
        world.getPlayers().forEach((player) -> { if (player.getTeam() == team) { players.add(player); } });
        return players;
    }

    @SuppressWarnings("ConstantConditions")
    private static void addAndClearScore(ServerWorld world, List<ServerPlayerEntity> players)
    {
        prepareScoreboardEntries(world);

        players.forEach((player) ->
        {
            world.getScoreboard().getOrCreateScore(player.getScoreboardName(), world.getScoreboard().getObjective("r6mod_points"));
            world.getScoreboard().getOrCreateScore(player.getScoreboardName(), world.getScoreboard().getObjective("r6mod_kills"));
            world.getScoreboard().getOrCreateScore(player.getScoreboardName(), world.getScoreboard().getObjective("r6mod_assists"));
            world.getScoreboard().getOrCreateScore(player.getScoreboardName(), world.getScoreboard().getObjective("r6mod_deaths"));

            world.getScoreboard().forAllObjectives(POINTS, player.getScoreboardName(), Score::reset);
            world.getScoreboard().forAllObjectives(KILLS, player.getScoreboardName(), Score::reset);
            world.getScoreboard().forAllObjectives(ASSISTS, player.getScoreboardName(), Score::reset);
            world.getScoreboard().forAllObjectives(DEATHS, player.getScoreboardName(), Score::reset);
        });
    }

    private static void removeScore(ServerWorld world, List<ServerPlayerEntity> players)
    {
        players.forEach((player) ->
        {
            world.getScoreboard().removeObjectiveFromEntity(player.getScoreboardName(), world.getScoreboard().getObjective("r6mod_points"));
            world.getScoreboard().removeObjectiveFromEntity(player.getScoreboardName(), world.getScoreboard().getObjective("r6mod_kills"));
            world.getScoreboard().removeObjectiveFromEntity(player.getScoreboardName(), world.getScoreboard().getObjective("r6mod_assists"));
            world.getScoreboard().removeObjectiveFromEntity(player.getScoreboardName(), world.getScoreboard().getObjective("r6mod_deaths"));
        });
    }

    private static void prepareScoreboardEntries(ServerWorld world)
    {
        ServerScoreboard scoreboard = world.getScoreboard();

        if (!scoreboard.hasObjective("r6mod_points"))
        {
            scoreboard.addObjective(new ScoreObjective(scoreboard, "r6mod_points", POINTS, new TranslationTextComponent("score.r6mod.points"), ScoreCriteria.RenderType.INTEGER));
        }

        if (!scoreboard.hasObjective("r6mod_kills"))
        {
            scoreboard.addObjective(new ScoreObjective(scoreboard, "r6mod_kills", POINTS, new TranslationTextComponent("score.r6mod.kills"), ScoreCriteria.RenderType.INTEGER));
        }

        if (!scoreboard.hasObjective("r6mod_assists"))
        {
            scoreboard.addObjective(new ScoreObjective(scoreboard, "r6mod_assists", POINTS, new TranslationTextComponent("score.r6mod.assists"), ScoreCriteria.RenderType.INTEGER));
        }

        if (!scoreboard.hasObjective("r6mod_deaths"))
        {
            scoreboard.addObjective(new ScoreObjective(scoreboard, "r6mod_deaths", POINTS, new TranslationTextComponent("score.r6mod.deaths"), ScoreCriteria.RenderType.INTEGER));
        }
    }

    private static void lockTeamSpawn(ServerWorld world, BlockPos pos, String team) throws CommandSyntaxException
    {
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileEntityTeamSpawn)) { throw TEAM_SPAWN_NOT_FOUND.create(team); }

        TileEntityTeamSpawn spawn = (TileEntityTeamSpawn)te;
        if (!spawn.getTeam().equals(team)) { throw TEAM_SPAWN_NOT_FOUND.create(team); }

        if (spawn.isLocked()) { throw TEAM_SPAWN_LOCKED.create(team); }

        spawn.lock();
    }

    private static void unlockTeamSpawn(ServerWorld world, BlockPos pos, String team) throws CommandSyntaxException
    {
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileEntityTeamSpawn)) { throw TEAM_SPAWN_NOT_FOUND.create(team); }

        TileEntityTeamSpawn spawn = (TileEntityTeamSpawn)te;
        if (!spawn.getTeam().equals(team)) { throw TEAM_SPAWN_NOT_FOUND.create(team); }

        spawn.unlock();
    }
}