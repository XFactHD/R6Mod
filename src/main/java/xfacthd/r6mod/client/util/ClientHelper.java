package xfacthd.r6mod.client.util;

import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import xfacthd.r6mod.api.entity.ICameraEntity;
import xfacthd.r6mod.api.ISidedHelper;
import xfacthd.r6mod.client.render.ister.RenderGun;
import xfacthd.r6mod.common.data.itemsubtypes.*;

public class ClientHelper implements ISidedHelper
{
    @Override
    public World getWorld() { return Minecraft.getInstance().world; }

    @Override
    public PlayerEntity getPlayer() { return Minecraft.getInstance().player; }

    @Override
    public boolean isUsingCamera(PlayerEntity player)
    {
        return Minecraft.getInstance().getRenderViewEntity() instanceof ICameraEntity;
    }

    @Override
    public void parseAttachmentTransforms(EnumGun gun, EnumAttachment attachment, JsonObject json)
    {
        RenderGun.parseAttachmentTransform(gun, attachment, json);
    }

    @Override
    public void parseMagazineTransforms(EnumGun gun, JsonObject json) { RenderGun.parseMagazineTransform(gun, json); }
}