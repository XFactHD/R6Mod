package xfacthd.r6mod.common.util.data;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import xfacthd.r6mod.api.entity.ICameraEntity;
import xfacthd.r6mod.common.data.EnumCamera;
import xfacthd.r6mod.common.net.NetworkHandler;
import xfacthd.r6mod.common.net.packets.camera.PacketCameraActiveIndex;
import xfacthd.r6mod.common.net.packets.camera.PacketCameraData;

import java.util.*;

public class CameraManager implements INBTSerializable<CompoundNBT>
{
    private final R6WorldSavedData savedData;

    private final HashMap<String, List<UUID>> teamCameraMap = new HashMap<>(); //INFO: contains all cameras associated with a certain team
    private final HashMap<UUID, List<UUID>> playerCameraMap = new HashMap<>(); //INFO: contains all cameras owned by a certain player
    private final HashMap<UUID, UUID> cameraUsageMap = new HashMap<>(); //INFO: contains the camera a certain player is or was using

    public CameraManager(R6WorldSavedData savedData) { this.savedData = savedData; }

    public void addCamera(ICameraEntity<?> cam, UUID placer, String team)
    {
        if (team == null || team.equals("")) { team = "null"; }

        if (team.equals("null"))
        {
            if (!playerCameraMap.containsKey(placer)) { playerCameraMap.put(placer, new ArrayList<>()); }
            playerCameraMap.get(placer).add(cam.getCameraEntity().getUniqueID());
            sortCameras((ServerWorld) cam.getCameraEntity().world, playerCameraMap.get(placer));

            PlayerEntity player = cam.getCameraEntity().world.getPlayerByUuid(placer);

            if (player != null) { sendToPlayer((ServerPlayerEntity) player); }
        }
        else
        {
            if (!teamCameraMap.containsKey(team)) { teamCameraMap.put(team, new ArrayList<>()); }

            teamCameraMap.get(team).add(cam.getCameraEntity().getUniqueID());
            sortCameras((ServerWorld) cam.getCameraEntity().world, teamCameraMap.get(team));

            sendToAllPlayersInTeam((ServerWorld) cam.getCameraEntity().world, team);
        }

        savedData.markDirty();
    }

    public void removeCamera(ICameraEntity<?> cam)
    {
        Team team = cam.getCameraEntity().getTeam();
        String teamName = team != null ? team.getName() : "null";

        if (teamName.equals("null"))
        {
            UUID owner = cam.getOwner();
            if (playerCameraMap.containsKey(owner))
            {
                playerCameraMap.get(owner).remove(cam.getCameraEntity().getUniqueID());
                if (playerCameraMap.get(owner).isEmpty()) { playerCameraMap.remove(owner); }
                else { sortCameras((ServerWorld)cam.getCameraEntity().world, playerCameraMap.get(owner)); }

                PlayerEntity player = cam.getCameraEntity().world.getPlayerByUuid(owner);
                if (player != null) { sendToPlayer((ServerPlayerEntity) player); }
            }
        }
        else
        {
            if (teamCameraMap.containsKey(teamName))
            {
                teamCameraMap.get(teamName).remove(cam.getCameraEntity().getUniqueID());
                if (teamCameraMap.get(teamName).isEmpty()) { teamCameraMap.remove(teamName); }
                else { sortCameras((ServerWorld) cam.getCameraEntity().world, teamCameraMap.get(teamName)); }

                sendToAllPlayersInTeam((ServerWorld) cam.getCameraEntity().world, teamName);
            }
        }

        UUID camId = cam.getCameraEntity().getUniqueID();
        if (cameraUsageMap.containsValue(camId))
        {
            for (UUID user : cameraUsageMap.keySet())
            {
                if (cameraUsageMap.get(user).equals(camId))
                {
                    cameraUsageMap.remove(user);
                }
            }
        }

        savedData.markDirty();
    }

    public void enterCamera(ServerPlayerEntity player)
    {
        String team = player.getTeam() != null ? player.getTeam().getName() : "null";

        if (!cameraUsageMap.containsKey(player.getUniqueID()))
        {
            cameraUsageMap.put(player.getUniqueID(), findNextCamera(player.getUniqueID(), team, null, true));
        }

        UUID cam = cameraUsageMap.get(player.getUniqueID());

        ServerWorld world = (ServerWorld)player.world;
        Entity camEntity = world.getEntityByUuid(cam);
        if (camEntity instanceof ICameraEntity)
        {
            ((ICameraEntity<?>) camEntity).startUsing(player);
            NetworkHandler.sendToPlayer(new PacketCameraActiveIndex(camEntity.getEntityId()), player);
        }
    }

    public void changeCamera(ServerPlayerEntity player, boolean forward)
    {
        String team = player.getTeam() != null ? player.getTeam().getName() : "null";
        UUID oldCam = cameraUsageMap.get(player.getUniqueID());
        UUID cam = findNextCamera(player.getUniqueID(), team, oldCam, forward);
        leaveCamera(player, false);
        cameraUsageMap.put(player.getUniqueID(), cam);

        ServerWorld world = (ServerWorld) player.world;

        Entity oldCamEntity = world.getEntityByUuid(oldCam);
        if (oldCamEntity instanceof ICameraEntity) { ((ICameraEntity<?>) oldCamEntity).stopUsing(player); }

        Entity camEntity = world.getEntityByUuid(cam);
        if (camEntity instanceof ICameraEntity)
        {
            ((ICameraEntity<?>) camEntity).startUsing(player);
            NetworkHandler.sendToPlayer(new PacketCameraActiveIndex(camEntity.getEntityId()), player);
        }
        else
        {
            NetworkHandler.sendToPlayer(new PacketCameraActiveIndex(-1), player);
        }
    }

    public void leaveCamera(ServerPlayerEntity player) { leaveCamera(player, true); }

    public void leaveCamera(ServerPlayerEntity player, boolean inform)
    {
        UUID oldCam = cameraUsageMap.get(player.getUniqueID());
        if (oldCam != null)
        {
            ServerWorld world = (ServerWorld) player.world;
            Entity oldCamEntity = world.getEntityByUuid(oldCam);
            if (oldCamEntity instanceof ICameraEntity) { ((ICameraEntity<?>) oldCamEntity).stopUsing(player); }
            if (inform) { NetworkHandler.sendToPlayer(new PacketCameraActiveIndex(-1), player); }
        }
    }

    public void resetLastUsedCam(ServerPlayerEntity entity){ cameraUsageMap.remove(entity.getUniqueID()); }

    public boolean hasCameras(ServerPlayerEntity player)
    {
        if (player.getTeam() == null) { return playerCameraMap.containsKey(player.getUniqueID()); }
        return hasCameras(player.getTeam().getName());
    }

    public boolean hasCameras(String team)
    {
        if (team.equals("") || team.equals("null")) { return false; }
        return teamCameraMap.containsKey(team);
    }

    public boolean isCameraRegistered(ICameraEntity<?> cam, UUID owner, String team)
    {
        if (team.equals("null"))
        {
            if (!teamCameraMap.containsKey(team)) { return false; }
            return teamCameraMap.get(team).contains(cam.getCameraEntity().getUniqueID());
        }
        else
        {
            if (!playerCameraMap.containsKey(owner)) { return false; }
            return playerCameraMap.get(owner).contains(cam.getCameraEntity().getUniqueID());
        }
    }

    public List<UUID> getCameras(ServerPlayerEntity player)
    {
        if (player.getTeam() == null) { return playerCameraMap.get(player.getUniqueID()); }
        return getCameras(player.getTeam().getName());
    }

    public List<UUID> getCameras(String team)
    {
        if (team.equals("") || team.equals("null")) { return Collections.emptyList(); }
        return teamCameraMap.get(team);
    }

    public boolean isUsingCamera(ServerPlayerEntity player)
    {
        UUID camId = cameraUsageMap.getOrDefault(player.getUniqueID(), null);
        if (camId == null) { return false; }

        Entity entity = ((ServerWorld)player.world).getEntityByUuid(camId);
        if (!(entity instanceof ICameraEntity)) { return false; }

        return ((ICameraEntity<?>)entity).isUsedBy(player);
    }

    public void sendToAllPlayersInTeam(ServerWorld world, String team)
    {
        for (ServerPlayerEntity player : world.getPlayers())
        {
            if (player.getTeam() != null && player.getTeam().getName().equals(team))
            {
                sendToPlayer(player);
            }
        }
    }

    public void sendToPlayer(ServerPlayerEntity player)
    {
        String ownTeam;
        String enemyTeam;

        Team team = player.getTeam();
        if (team instanceof ScorePlayerTeam)
        {
            //Get teams
            ScorePlayerTeam scoreTeam = (ScorePlayerTeam)team;
            ScorePlayerTeam enemyScoreTeam = R6WorldSavedData.get((ServerWorld) player.world).getMatchManager().getEnemyTeam(player.world, scoreTeam);

            //Get team names
            ownTeam = team.getName();
            enemyTeam = enemyScoreTeam != null ? enemyScoreTeam.getName() : "null";

            //Get accessible cameras
            List<Integer> cameraIds = new ArrayList<>();
            if (teamCameraMap.containsKey(ownTeam))
            {
                teamCameraMap.get(ownTeam).forEach((uuid) ->
                {
                    Entity entity = ((ServerWorld)player.world).getEntityByUuid(uuid);
                    if (entity instanceof ICameraEntity) { cameraIds.add(entity.getEntityId()); }
                });
            }

            if (teamCameraMap.containsKey(enemyTeam))
            {
                teamCameraMap.get(enemyTeam).forEach((uuid) ->
                {
                    Entity entity = ((ServerWorld)player.world).getEntityByUuid(uuid);
                    if (entity instanceof ICameraEntity) { cameraIds.add(entity.getEntityId()); }
                });
            }

            //Send to player
            NetworkHandler.sendToPlayer(new PacketCameraData(cameraIds), player);
        }
        else if (team == null && playerCameraMap.containsKey(player.getUniqueID()))
        {
            //Get accessible cameras
            List<Integer> cameraIds = new ArrayList<>();
            playerCameraMap.get(player.getUniqueID()).forEach((uuid) ->
            {
                Entity entity = ((ServerWorld)player.world).getEntityByUuid(uuid);
                if (entity instanceof ICameraEntity) { cameraIds.add(entity.getEntityId()); }
            });

            //Send to player
            NetworkHandler.sendToPlayer(new PacketCameraData(cameraIds), player);
        }
    }

    public boolean clearCameras()
    {
        if (teamCameraMap.isEmpty() && playerCameraMap.isEmpty() && cameraUsageMap.isEmpty()) { return false; }

        teamCameraMap.clear();
        playerCameraMap.clear();
        cameraUsageMap.clear();

        savedData.markDirty();
        return true;
    }

    private UUID findNextCamera(UUID owner, String team, UUID current, boolean forward)
    {
        if (team.equals("null"))
        {
            List<UUID> cameras = playerCameraMap.get(owner);

            if (current == null) { return cameras.get(0); }
            else
            {
                int idx = cameras.indexOf(current);
                if (forward)
                {
                    idx++;
                    if (idx >= cameras.size()) { idx = 0; }
                }
                else
                {
                    idx--;
                    if (idx < 0) { idx = cameras.size() - 1; }
                }
                return cameras.get(idx);
            }
        }
        else
        {
            List<UUID> cameras = teamCameraMap.get(team);

            if (current == null) { return cameras.get(0); }
            else
            {
                int idx = cameras.indexOf(current);
                if (idx == cameras.size() - 1)
                {
                    return cameras.get(0);
                }
                else
                {
                    return cameras.get(idx + 1);
                }
            }
        }
    }

    private void sortCameras(ServerWorld world, List<UUID> cameras)
    {
        if (cameras.isEmpty()) { return; }

        cameras.sort((cam1, cam2) ->
        {
            Entity entity1 = world.getEntityByUuid(cam1);
            Entity entity2 = world.getEntityByUuid(cam2);
            if (entity1 instanceof ICameraEntity && entity2 instanceof ICameraEntity)
            {
                EnumCamera camType1 = ((ICameraEntity<?>)entity1).getCameraType();
                EnumCamera camType2 = ((ICameraEntity<?>)entity2).getCameraType();
                if (camType1 == camType2) { return Integer.compare(entity1.getEntityId(), entity2.getEntityId()); }
                else { return camType1.compareTo(camType2); }
            }
            else
            {
                String msg = String.format("Invalid entity in camera manager, entity1 id: %d, entity2 id: %d",
                        entity1 == null ? -1 : entity1.getEntityId(),
                        entity2 == null ? -1 : entity2.getEntityId());
                throw new IllegalArgumentException(msg);
            }
        });
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();

        ListNBT teams = new ListNBT();
        for (String team : teamCameraMap.keySet())
        {
            CompoundNBT teamTag = new CompoundNBT();
            teamTag.putString("name", team);

            ListNBT cams = new ListNBT();
            for (UUID cam : teamCameraMap.get(team))
            {
                CompoundNBT camId = new CompoundNBT();
                camId.putUniqueId("cam", cam);
            }
            teamTag.put("cams", cams);

            teams.add(teamTag);
        }
        nbt.put("teams", teams);

        ListNBT owners = new ListNBT();
        for (UUID owner : playerCameraMap.keySet())
        {
            CompoundNBT ownerTag = new CompoundNBT();
            ownerTag.putUniqueId("owner", owner);

            ListNBT cams = new ListNBT();
            for (UUID cam : playerCameraMap.get(owner))
            {
                CompoundNBT camId = new CompoundNBT();
                camId.putUniqueId("cam", cam);
            }
            ownerTag.put("cams", cams);

            owners.add(ownerTag);
        }
        nbt.put("owners", owners);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        teamCameraMap.clear();
        playerCameraMap.clear();

        ListNBT teams = nbt.getList("teams", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < teams.size(); i++)
        {
            CompoundNBT teamTag = teams.getCompound(i);
            String name = teamTag.getString("name");

            List<UUID> camList = new ArrayList<>();
            ListNBT cams = teamTag.getList("cams", Constants.NBT.TAG_COMPOUND);
            for (int j = 0; j < cams.size(); j++)
            {
                CompoundNBT camId = cams.getCompound(i);
                UUID cam = camId.getUniqueId("cam");
                camList.add(cam);
            }

            teamCameraMap.put(name, camList);
        }

        ListNBT owners = nbt.getList("owners", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < owners.size(); i++)
        {
            CompoundNBT ownerTag = owners.getCompound(i);
            UUID owner = ownerTag.getUniqueId("owner");

            List<UUID> camList = new ArrayList<>();
            ListNBT cams = ownerTag.getList("cams", Constants.NBT.TAG_COMPOUND);
            for (int j = 0; j < cams.size(); j++)
            {
                CompoundNBT camId = cams.getCompound(i);
                UUID cam = camId.getUniqueId("cam");
                camList.add(cam);
            }

            playerCameraMap.put(owner, camList);
        }
    }
}