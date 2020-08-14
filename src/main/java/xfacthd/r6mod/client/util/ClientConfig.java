package xfacthd.r6mod.client.util;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ClientConfig
{
    public static final ForgeConfigSpec SPEC;
    public static final ClientConfig INSTANCE;

    static
    {
        final Pair<ClientConfig, ForgeConfigSpec> configSpecPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        SPEC = configSpecPair.getRight();
        INSTANCE = configSpecPair.getLeft();
    }

    public boolean holdToAim = true;
    public boolean debugShowAllGunTraces = true;
    public boolean debugShowGunSearchBox = false;
    public boolean debugDumpTextures = false;

    private final ForgeConfigSpec.BooleanValue holdToAimValue;
    private final ForgeConfigSpec.BooleanValue debugShowAllGunTracesValue;
    private final ForgeConfigSpec.BooleanValue debugShowGunSearchBoxValue;
    private final ForgeConfigSpec.BooleanValue debugDumpTexturesValue;

    public ClientConfig(ForgeConfigSpec.Builder builder)
    {
        builder.push("gameplay");
        holdToAimValue = builder
                .comment("If the mouse should be held to aim")
                .translation("config.r6mod.hold_to_aim")
                .define("holdToAim", true);
        builder.pop();

        builder.push("debug");
        debugShowAllGunTracesValue = builder
                .comment("Wether all gun traces or just the latest one should be drawn")
                .translation("config.r6mod.show_all_gun_traces")
                .define("showAllGunTraces", true);
        debugShowGunSearchBoxValue = builder
                .comment("Wether the search bounding box resulting from a gun trace should be drawn")
                .translation("config.r6mod.show_gun_search_box")
                .define("showGunSearchBox", false);
        debugDumpTexturesValue = builder
                .comment("If true, all AtlasTextures will be dumped on startup and resource reload")
                .translation("config.r6mod.dump_textures")
                .define("dumpTextures", false);
        builder.pop();
    }

    public void rebake()
    {
        holdToAim = holdToAimValue.get();
        debugShowAllGunTraces = debugShowAllGunTracesValue.get();
        debugShowGunSearchBox = debugShowGunSearchBoxValue.get();
        debugDumpTextures = debugDumpTexturesValue.get();
    }
}