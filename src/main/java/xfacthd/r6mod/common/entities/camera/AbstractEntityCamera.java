package xfacthd.r6mod.common.entities.camera;

import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.*;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;
import xfacthd.r6mod.api.entity.ICameraEntity;
import xfacthd.r6mod.api.interaction.IEMPInteract;
import xfacthd.r6mod.common.util.Config;
import xfacthd.r6mod.common.util.data.CameraManager;
import xfacthd.r6mod.common.util.data.R6WorldSavedData;

import java.util.*;

public abstract class AbstractEntityCamera extends Entity implements ICameraEntity, IEMPInteract
{
    private static final DataParameter<Optional<UUID>> PARAM_OWNER = EntityDataManager.createKey(AbstractEntityCamera.class, DataSerializers.OPTIONAL_UNIQUE_ID);

    protected UUID owner;
    protected String teamName = "null";
    protected String hackerTeam = "";
    protected final ArrayDeque<UUID> users = new ArrayDeque<>();

    private boolean firstTick = true;

    public AbstractEntityCamera(EntityType<?> type, World world) { super(type, world); }

    public AbstractEntityCamera(EntityType<?> type, World world, UUID owner, String team)
    {
        this(type, world);

        this.owner = owner;
        this.teamName = team;
    }

    @Override
    public void tick()
    {
        super.tick();

        if (firstTick) { firstTick(); }
    }

    protected void firstTick()
    {
        firstTick = false;

        recenterBoundingBox();

        if (!world.isRemote)
        {
            CameraManager camMgr = R6WorldSavedData.get((ServerWorld) world).getCameraManager();
            if (!camMgr.isCameraRegistered(this, owner, teamName)) { camMgr.addCamera(this, owner, teamName); }

            dataManager.set(PARAM_OWNER, Optional.of(owner));
        }

        if (!teamName.equals("null"))
        {
            ScorePlayerTeam team = world.getScoreboard().getTeam(teamName);
            //noinspection ConstantConditions
            if (team != null && world.getScoreboard().getPlayersTeam(getScoreboardName()) != team)
            {
                world.getScoreboard().addPlayerToTeam(getScoreboardName(), team);
            }
        }
    }

    protected boolean canPerformAction(PlayerEntity player)
    {
        if (!player.getUniqueID().equals(users.peek())) { return false; } //Only the first user can control the camera
        if (Config.INSTANCE.limitCamActions) { return player.getUniqueID().equals(owner); } //If configured only the owner can perform this action
        return true;
    }

    /*
     * ICameraEntity
     */

    @Override
    public void handleMarkButtonPacket(PlayerEntity player, boolean down, int fovAngle)
    {
        //TODO: implement marking, lookup how to do a frustum check
    }

    @Override
    public void startUsing(PlayerEntity player)
    {
        //If the owner starts using the camera and full control is limited to the owner, he takes over control
        if (Config.INSTANCE.limitCamActions && player.getUniqueID().equals(owner))
        {
            users.addFirst(player.getUniqueID());
        }
        else { users.add(player.getUniqueID()); }
    }

    @Override
    public void stopUsing(PlayerEntity player) { users.remove(player.getUniqueID()); }

    @Override
    public boolean isInUse() { return !users.isEmpty(); }

    @Override
    public PlayerEntity getPrimaryUser()
    {
        if (users.isEmpty()) { return null; }

        UUID id = users.peek();
        return world.getPlayerByUuid(id);
    }

    @Override
    public boolean isUsedBy(PlayerEntity player)
    {
        if (users.isEmpty()) { return false; }
        return users.contains(player.getUniqueID());
    }

    @Override //Personal cameras can't be hacked
    public boolean canHackCamera(String team) { return teamName != null && hackerTeam.isEmpty(); }

    @Override
    public void hackCamera(String team)
    {
        if (!hackerTeam.isEmpty()) { return; }
        hackerTeam = team;
    }

    @Override
    public boolean isHackedBy(String team) { return hackerTeam.equals(team); }

    @Override
    public UUID getOwner() { return owner; }

    @Override
    public String getTeamName() { return teamName != null ? teamName : "null"; }

    /*
     * Misc entity stuff
     */

    @Override
    public boolean canBeCollidedWith() { return true; }

    @Override
    public void remove(boolean keepData)
    {
        super.remove(keepData);

        if (!keepData && !world.isRemote)
        {
            if (isInUse())
            {
                users.forEach((uuid) ->
                {
                    PlayerEntity player = world.getPlayerByUuid(uuid);
                    if (player != null) { R6WorldSavedData.get((ServerWorld)world).getCameraManager().leaveCamera((ServerPlayerEntity) player); }
                });
            }
            R6WorldSavedData.get((ServerWorld)world).getCameraManager().removeCamera(this);
        }
    }

    @Override //FIXME: custom data manager entries are broken in every camera on dedicated server
    protected void registerData() { dataManager.register(PARAM_OWNER, Optional.empty()); }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key)
    {
        super.notifyDataManagerChange(key);
        if (key == PARAM_OWNER) { owner = dataManager.get(PARAM_OWNER).orElse(null); }
    }

    @Override
    protected float getEyeHeight(Pose pose, EntitySize size) { return 0; }

    @Override
    protected void writeAdditional(CompoundNBT nbt)
    {
        nbt.putUniqueId("owner", owner);
        nbt.putString("team", teamName);
    }

    @Override
    protected void readAdditional(CompoundNBT nbt)
    {
        owner = nbt.getUniqueId("owner");
        teamName = nbt.getString("team");
    }

    @Override
    public IPacket<?> createSpawnPacket() { return NetworkHooks.getEntitySpawningPacket(this); }
}