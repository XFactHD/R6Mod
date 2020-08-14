package xfacthd.r6mod.api.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Team;
import xfacthd.r6mod.api.client.ICameraOverlay;
import xfacthd.r6mod.common.data.EnumCamera;
import xfacthd.r6mod.common.net.NetworkHandler;
import xfacthd.r6mod.common.net.packets.camera.PacketCameraMarkButton;
import xfacthd.r6mod.common.net.packets.camera.PacketCameraRotation;

import java.util.UUID;

public interface ICameraEntity<T extends ICameraEntity<T>>
{
    default Entity getCameraEntity() { return (Entity) this; }

    UUID getOwner();

    String getTeamName();



    void startUsing(PlayerEntity player);

    void stopUsing(PlayerEntity player);

    boolean isInUse();

    PlayerEntity getPrimaryUser();

    boolean isUsedBy(PlayerEntity player);

    default boolean isFriendly(PlayerEntity player)
    {
        if (player == null) { return false; }
        if (player.getTeam() == null) { return player.getUniqueID().equals(getOwner()); }
        return player.getTeam() == getCameraEntity().getTeam();
    }

    default boolean canAccess(PlayerEntity player)
    {
        if (getTeamName().equals("null")) { return player.getUniqueID().equals(getOwner()); }
        Team team = player.getTeam();
        return isFriendly(player) || (team != null && isHackedBy(team.getName()));
    }

    boolean canHackCamera(String team);

    void hackCamera(String team);

    boolean isHackedBy(String team);



    default void handleMouseMovement(double diffX, double diffY)
    {
        if (diffX == 0D && diffY == 0D) { return; }
        NetworkHandler.sendToServer(new PacketCameraRotation(getCameraEntity().getEntityId(), diffX, diffY));
    }

    default void handleLeftClick(boolean down) { }

    default void handleRightClick(boolean down) { }

    default void handleKeyInput(byte mask) { }

    default void handleMarkButton(boolean down, int fovAngle)
    {
        NetworkHandler.sendToServer(new PacketCameraMarkButton(getCameraEntity().getEntityId(), down, fovAngle));
    }

    void handleRotationPacket(PlayerEntity player, double diffX, double diffY);

    default void handleLeftClickPacket(PlayerEntity player, boolean down) { }

    default void handleRightClickPacket(PlayerEntity player, boolean down) { }

    default void handleKeyInputPacket(PlayerEntity player, byte mask) { }

    void handleMarkButtonPacket(PlayerEntity player, boolean down, int fovAngle);



    EnumCamera getCameraType();

    ICameraOverlay<T> createOverlayRenderer();
}