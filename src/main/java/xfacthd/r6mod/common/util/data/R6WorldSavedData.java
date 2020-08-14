package xfacthd.r6mod.common.util.data;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

public class R6WorldSavedData extends WorldSavedData
{
    private static final String NAME = "r6mod_data";

    private final MatchManager matchManager = new MatchManager(this);
    private final CameraManager cameraManager = new CameraManager(this);

    public R6WorldSavedData() { super("r6mod_data"); }

    public static R6WorldSavedData get(ServerWorld world)
    {
        return world.getSavedData().getOrCreate(R6WorldSavedData::new, NAME);
    }

    public MatchManager getMatchManager() { return matchManager; }

    public CameraManager getCameraManager() { return cameraManager; }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        nbt.put("matches", matchManager.serializeNBT());
        nbt.put("cameras", cameraManager.serializeNBT());

        return nbt;
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        matchManager.deserializeNBT(nbt.getCompound("matches"));
        cameraManager.deserializeNBT(nbt.getCompound("cameras"));
    }
}