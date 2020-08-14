package xfacthd.r6mod.common.net;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.common.net.packets.*;
import xfacthd.r6mod.common.net.packets.camera.*;
import xfacthd.r6mod.common.net.packets.capability.*;
import xfacthd.r6mod.common.net.packets.dbno.*;
import xfacthd.r6mod.common.net.packets.debug.*;
import xfacthd.r6mod.common.net.packets.gun.*;
import xfacthd.r6mod.common.net.packets.match.*;

public class NetworkHandler
{
    private static final String PROTOCOL_VERSION = "1"; //INFO: change when adding packets due to the indices changing

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(R6Mod.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    @SuppressWarnings("UnusedAssignment")
    public static void initPackets()
    {
        int idx = 0;

        //Client to server packets
        CHANNEL.registerMessage(idx++, PacketAiming.class,              PacketAiming::encode,              PacketAiming::new,              PacketAiming::handle);
        CHANNEL.registerMessage(idx++, PacketFiring.class,              PacketFiring::encode,              PacketFiring::new,              PacketFiring::handle);
        CHANNEL.registerMessage(idx++, PacketCancelGunHandling.class,   PacketCancelGunHandling::encode,   PacketCancelGunHandling::new,   PacketCancelGunHandling::handle);
        CHANNEL.registerMessage(idx++, PacketReload.class,              PacketReload::encode,              PacketReload::new,              PacketReload::handle);
        CHANNEL.registerMessage(idx++, PacketCameraRotation.class,      PacketCameraRotation::encode,      PacketCameraRotation::new,      PacketCameraRotation::handle);
        CHANNEL.registerMessage(idx++, PacketCameraLeftClick.class,     PacketCameraLeftClick::encode,     PacketCameraLeftClick::new,     PacketCameraLeftClick::handle);
        CHANNEL.registerMessage(idx++, PacketCameraRightClick.class,    PacketCameraRightClick::encode,    PacketCameraRightClick::new,    PacketCameraRightClick::handle);
        CHANNEL.registerMessage(idx++, PacketCameraMarkButton.class,    PacketCameraMarkButton::encode,    PacketCameraMarkButton::new,    PacketCameraMarkButton::handle);
        CHANNEL.registerMessage(idx++, PacketCameraSwitch.class,        PacketCameraSwitch::encode,        PacketCameraSwitch::new,        PacketCameraSwitch::handle);
        CHANNEL.registerMessage(idx++, PacketCameraKeyInput.class,      PacketCameraKeyInput::encode,      PacketCameraKeyInput::new,      PacketCameraKeyInput::handle);
        CHANNEL.registerMessage(idx++, PacketCameraExit.class,          PacketCameraExit::encode,          PacketCameraExit::new,          PacketCameraExit::handle);
        CHANNEL.registerMessage(idx++, PacketCameraTeamGuiResult.class, PacketCameraTeamGuiResult::encode, PacketCameraTeamGuiResult::new, PacketCameraTeamGuiResult::handle);
        CHANNEL.registerMessage(idx++, PacketTeamSpawnGuiResult.class,  PacketTeamSpawnGuiResult::encode,  PacketTeamSpawnGuiResult::new,  PacketTeamSpawnGuiResult::handle);
        CHANNEL.registerMessage(idx++, PacketHoldWound.class,           PacketHoldWound::encode,           PacketHoldWound::new,           PacketHoldWound::handle);

        //Server to client packets
        CHANNEL.registerMessage(idx++, PacketRayTraceResult.class,     PacketRayTraceResult::encode,     PacketRayTraceResult::new,     PacketRayTraceResult::handle);
        CHANNEL.registerMessage(idx++, PacketExplosionParticles.class, PacketExplosionParticles::encode, PacketExplosionParticles::new, PacketExplosionParticles::handle);
        CHANNEL.registerMessage(idx++, PacketMatchUpdate.class,        PacketMatchUpdate::encode,        PacketMatchUpdate::new,        PacketMatchUpdate::handle);
        CHANNEL.registerMessage(idx++, PacketCameraData.class,         PacketCameraData::encode,         PacketCameraData::new,         PacketCameraData::handle);
        CHANNEL.registerMessage(idx++, PacketCameraActiveIndex.class,  PacketCameraActiveIndex::encode,  PacketCameraActiveIndex::new,  PacketCameraActiveIndex::handle);
        CHANNEL.registerMessage(idx++, PacketEffectTrigger.class,      PacketEffectTrigger::encode,      PacketEffectTrigger::new,      PacketEffectTrigger::handle);
        CHANNEL.registerMessage(idx++, PacketEffectClear.class,        PacketEffectClear::encode,        PacketEffectClear::new,        PacketEffectClear::handle);
        CHANNEL.registerMessage(idx++, PacketUpdateFinkaBoost.class,   PacketUpdateFinkaBoost::encode,   PacketUpdateFinkaBoost::new,   PacketUpdateFinkaBoost::handle);
        CHANNEL.registerMessage(idx++, PacketUpdateStimState.class,    PacketUpdateStimState::encode,    PacketUpdateStimState::new,    PacketUpdateStimState::handle);
        CHANNEL.registerMessage(idx++, PacketReloadState.class,        PacketReloadState::encode,        PacketReloadState::new,        PacketReloadState::handle);
        CHANNEL.registerMessage(idx++, PacketGunState.class,           PacketGunState::encode,           PacketGunState::new,           PacketGunState::handle);
        CHANNEL.registerMessage(idx++, PacketPointInfo.class,          PacketPointInfo::encode,          PacketPointInfo::new,          PacketPointInfo::handle);
        CHANNEL.registerMessage(idx++, PacketKillfeedEntry.class,      PacketKillfeedEntry::encode,      PacketKillfeedEntry::new,      PacketKillfeedEntry::handle);
        CHANNEL.registerMessage(idx++, PacketGrenadeBounce.class,      PacketGrenadeBounce::encode,      PacketGrenadeBounce::new,      PacketGrenadeBounce::handle);
        CHANNEL.registerMessage(idx++, PacketDBNOState.class,          PacketDBNOState::encode,          PacketDBNOState::new,          PacketDBNOState::handle);
        CHANNEL.registerMessage(idx++, PacketInformHelper.class,       PacketInformHelper::encode,       PacketInformHelper::new,       PacketInformHelper::handle);
        CHANNEL.registerMessage(idx++, PacketInformSpectator.class,    PacketInformSpectator::encode,    PacketInformSpectator::new,    PacketInformSpectator::handle);
    }

    public static void sendToServer(AbstractPacket packet) { CHANNEL.sendToServer(packet); }

    public static void sendToPlayer(AbstractPacket packet, ServerPlayerEntity player)
    {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static void sendToPlayersTrackingChunk(AbstractPacket packet, Chunk chunk)
    {
        CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), packet);
    }

    public static void sendToAllPlayers(AbstractPacket packet) { CHANNEL.send(PacketDistributor.ALL.noArg(), packet); }
}