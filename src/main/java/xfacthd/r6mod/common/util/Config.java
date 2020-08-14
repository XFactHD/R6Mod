package xfacthd.r6mod.common.util;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Config
{
    public static final ForgeConfigSpec SPEC;
    public static final Config INSTANCE;

    static
    {
        final Pair<Config, ForgeConfigSpec> configSpecPair = new ForgeConfigSpec.Builder().configure(Config::new);
        SPEC = configSpecPair.getRight();
        INSTANCE = configSpecPair.getLeft();
    }

    public boolean limitCamActions = true;
    public boolean destroyWood = true;
    public boolean alwaysDbno = true;
    public boolean debugGunShots = false;
    public boolean debugGrenadePath = false;

    private final ForgeConfigSpec.BooleanValue limitCamActionsValue;
    private final ForgeConfigSpec.BooleanValue destroyWoodValue;
    private final ForgeConfigSpec.BooleanValue alwaysDbnoValue;
    private final ForgeConfigSpec.BooleanValue debugGunShotsValue;
    private final ForgeConfigSpec.BooleanValue debugGrenadePathValue;

    public Config(final ForgeConfigSpec.Builder builder)
    {
        builder.push("gameplay");
        limitCamActionsValue = builder
                .comment("If true, only the owner of a camera will be able to perform most actions")
                .translation("config.r6mod.limit_cam_actions")
                .define("limitCamActions", true);
        destroyWoodValue = builder
                .comment("If true, R6Mod explosives will destroy any wood, else only blocks implementing IDestructible or IHardDestructible")
                .translation("config.r6mod.destroy_wood")
                .define("destroyWood", true);
        alwaysDbnoValue = builder
                .comment("If true, the DBNO system is active without being in a match")
                .translation("config.r6mod.always_dbno")
                .define("alwaysDbno", true);
        builder.pop();

        builder.push("debug");
        debugGunShotsValue = builder
                .comment("If true, gun shoot trace details will be sent to the shooting client for debugging")
                .translation("config.r6mod.debug_gun_shots")
                .define("debugGunShots", false);
        debugGrenadePathValue = builder
                .comment("If true, grenade paths will be sent to the throwing client for debugging")
                .translation("config.r6mod.debug_grenade_path")
                .define("debugGrenadePath", false);
        builder.pop();
    }

    public void rebake()
    {
        limitCamActions = limitCamActionsValue.get();
        destroyWood = destroyWoodValue.get();
        alwaysDbno = alwaysDbnoValue.get();

        debugGunShots = debugGunShotsValue.get();
        debugGrenadePath = debugGrenadePathValue.get();
    }
}