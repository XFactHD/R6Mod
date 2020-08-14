package xfacthd.r6mod.common.util.data;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import xfacthd.r6mod.common.net.packets.match.PacketMatchUpdate;

import java.util.*;

public class MatchManager implements INBTSerializable<CompoundNBT>
{
    private final R6WorldSavedData savedData;

    private final HashMap<String, String> matchMap = new HashMap<>(); //Mapping between the two teams in a match
    private final HashMap<String, String> reverseMatchMap = new HashMap<>(); //Reverse mapping between the two teams in a match
    private final HashMap<String, BlockPos> spawnMap = new HashMap<>(); //Contains the team spawn block positions for each team
    private final HashMap<UUID, BlockPos> originalSpawnMap = new HashMap<>(); //Mapping between player UUIDs and their original spawns

    public MatchManager(R6WorldSavedData savedData) { this.savedData = savedData; }

    public boolean isTeamPlaying(ScorePlayerTeam team)
    {
        if (team == null) { return false; }
        return matchMap.containsKey(team.getName()) || reverseMatchMap.containsKey(team.getName());
    }

    public ScorePlayerTeam getEnemyTeam(World world, ScorePlayerTeam team)
    {
        if (!isTeamPlaying(team)) { return null; }

        String name = matchMap.get(team.getName());
        if (name == null) { name = reverseMatchMap.get(team.getName()); }
        return world.getScoreboard().getTeam(name);
    }

    public boolean addMatch(ScorePlayerTeam team1, ScorePlayerTeam team2)
    {
        if (isTeamPlaying(team1) || isTeamPlaying(team2)) { return false; }

        matchMap.put(team1.getName(), team2.getName());
        reverseMatchMap.put(team2.getName(), team1.getName());

        savedData.markDirty();

        return true;
    }

    public boolean removeMatch(ScorePlayerTeam team1, ScorePlayerTeam team2)
    {
        if (!isTeamPlaying(team1) || !isTeamPlaying(team2)) { return false; }

        matchMap.remove(team1.getName());
        matchMap.remove(team2.getName());
        reverseMatchMap.remove(team1.getName());
        reverseMatchMap.remove(team2.getName());

        savedData.markDirty();

        return true;
    }

    public Map<String, String> getMatchList() { return matchMap; }

    public PacketMatchUpdate getReconnectPacket(ServerPlayerEntity player)
    {
        if (player.getTeam() == null) { return null; }

        String teamName = player.getTeam().getName();
        if (!matchMap.containsKey(teamName) && !reverseMatchMap.containsKey(teamName)) { return null; }

        String team1;
        String team2;
        if (matchMap.containsKey(teamName))
        {
            team1 = teamName;
            team2 = matchMap.get(teamName);
        }
        else
        {
            team1 = reverseMatchMap.get(teamName);
            team2 = teamName;
        }

        return new PacketMatchUpdate(team1, team2, PacketMatchUpdate.Type.START);
    }

    public List<ServerPlayerEntity> getPlayersInMatch(World world, ScorePlayerTeam team)
    {
        ScorePlayerTeam enemyTeam = getEnemyTeam(world, team);

        List<ServerPlayerEntity> players = new ArrayList<>();
        for (PlayerEntity player : world.getPlayers())
        {
            if (team.isSameTeam(player.getTeam()) || enemyTeam.isSameTeam(player.getTeam()))
            {
                players.add((ServerPlayerEntity)player);
            }
        }
        return players;
    }



    public void setSpawn(ScorePlayerTeam team, BlockPos pos)
    {
        if (team == null) { return; }
        if (pos == null) { removeSpawn(team); }

        spawnMap.put(team.getName(), pos);
        savedData.markDirty();
    }

    public void removeSpawn(ScorePlayerTeam team)
    {
        if (team == null) { return; }

        spawnMap.remove(team.getName());
        savedData.markDirty();
    }

    public boolean hasSpawn(ScorePlayerTeam team)
    {
        if (team == null) { return false; }

        return spawnMap.containsKey(team.getName());
    }

    public BlockPos getSpawn(ScorePlayerTeam team)
    {
        if (team == null) { return null; }

        return spawnMap.get(team.getName());
    }



    public void putOriginalSpawn(ServerPlayerEntity player)
    {
        originalSpawnMap.put(player.getUniqueID(), player.getBedLocation(DimensionType.OVERWORLD));
    }

    public boolean hasOriginalSpawn(ServerPlayerEntity player)
    {
        return originalSpawnMap.containsKey(player.getUniqueID());
    }

    public BlockPos retrieveOriginalSpawn(ServerPlayerEntity player)
    {
        return originalSpawnMap.remove(player.getUniqueID());
    }


    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        ListNBT list = new ListNBT();
        for (String team1 : matchMap.keySet())
        {
            CompoundNBT tag = new CompoundNBT();
            tag.putString("team1", team1);
            tag.putString("team2", matchMap.get(team1));
            list.add(tag);
        }
        nbt.put("matches", list);

        list = new ListNBT();
        for (String team : spawnMap.keySet())
        {
            CompoundNBT tag = new CompoundNBT();
            tag.putString("team", team);
            tag.putLong("pos", spawnMap.get(team).toLong());
            list.add(tag);
        }
        nbt.put("spawns", list);

        list = new ListNBT();
        for (UUID uuid : originalSpawnMap.keySet())
        {
            CompoundNBT tag = new CompoundNBT();
            tag.putUniqueId("id", uuid);
            tag.putLong("pos", originalSpawnMap.get(uuid).toLong());
            list.add(tag);
        }
        nbt.put("playerSpawns", list);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        ListNBT list = nbt.getList("matches", Constants.NBT.TAG_COMPOUND);
        list.forEach((inbt) ->
        {
            if (inbt instanceof CompoundNBT)
            {
                CompoundNBT tag = (CompoundNBT)inbt;
                matchMap.put(tag.getString("team1"), tag.getString("team2"));
                reverseMatchMap.put(tag.getString("team2"), tag.getString("team1"));
            }
        });

        list = nbt.getList("spawns", Constants.NBT.TAG_COMPOUND);
        list.forEach((inbt ->
        {
            if (inbt instanceof CompoundNBT)
            {
                CompoundNBT tag = (CompoundNBT)inbt;
                spawnMap.put(tag.getString("team"), BlockPos.fromLong(tag.getLong("pos")));
            }
        }));

        list = nbt.getList("playerSpawns", Constants.NBT.TAG_COMPOUND);
        list.forEach((inbt ->
        {
            if (inbt instanceof CompoundNBT)
            {
                CompoundNBT tag = (CompoundNBT)inbt;
                originalSpawnMap.put(tag.getUniqueId("id"), BlockPos.fromLong(tag.getLong("pos")));
            }
        }));
    }
}