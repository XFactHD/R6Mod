package xfacthd.r6mod.api;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import xfacthd.r6mod.common.data.itemsubtypes.*;

public interface ISidedHelper
{
    World getWorld();

    PlayerEntity getPlayer();

    boolean isUsingCamera(PlayerEntity player);

    void parseAttachmentTransforms(EnumGun gun, EnumAttachment attachment, JsonObject json);

    void parseMagazineTransforms(EnumGun gun, JsonObject json);
}