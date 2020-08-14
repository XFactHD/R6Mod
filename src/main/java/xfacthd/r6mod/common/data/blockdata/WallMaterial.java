package xfacthd.r6mod.common.data.blockdata;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum WallMaterial implements IStringSerializable
{
    OAK     ("minecraft:block/oak_planks"),
    SPRUCE  ("minecraft:block/spruce_planks"),
    BIRCH   ("minecraft:block/birch_planks"),
    JUNGLE  ("minecraft:block/jungle_planks"),
    ACACIA  ("minecraft:block/acacia_planks"),
    DARK_OAK("minecraft:block/dark_oak_planks"),
    PLASTER ("r6mod:block/building/block_plaster");

    private final String texture;

    WallMaterial(String texture) { this.texture = texture; }

    @Override
    public String getName() { return toString().toLowerCase(Locale.ENGLISH); }

    public String getTexture() { return texture; }
}