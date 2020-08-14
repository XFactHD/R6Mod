package xfacthd.r6mod.common.util;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import xfacthd.r6mod.api.ISidedHelper;
import xfacthd.r6mod.common.data.itemsubtypes.*;
import xfacthd.r6mod.common.util.data.R6WorldSavedData;

public class ServerHelper implements ISidedHelper
{
    @Override
    public World getWorld() { return ServerLifecycleHooks.getCurrentServer().getWorld(DimensionType.OVERWORLD); }

    @Override
    public PlayerEntity getPlayer() { throw  new UnsupportedOperationException(); }

    @Override
    public boolean isUsingCamera(PlayerEntity player)
    {
        ServerPlayerEntity sPlayer = (ServerPlayerEntity)player;
        ServerWorld world = (ServerWorld)sPlayer.world;
        return R6WorldSavedData.get(world).getCameraManager().isUsingCamera(sPlayer);
    }

    @Override
    public void parseAttachmentTransforms(EnumGun gun, EnumAttachment attachment, JsonObject json) { }

    @Override
    public void parseMagazineTransforms(EnumGun gun, JsonObject json) { }
}