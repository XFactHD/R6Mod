package xfacthd.r6mod.common.items.material;

import net.minecraft.item.Item;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.common.data.ItemGroups;
import xfacthd.r6mod.common.data.itemsubtypes.EnumMaterial;

public class ItemMaterial extends Item
{
    private final EnumMaterial material;

    public ItemMaterial(EnumMaterial material)
    {
        super(new Item.Properties().group(ItemGroups.MATERIALS));
        this.material = material;

        setRegistryName(R6Mod.MODID, material.toItemName());
    }

    public EnumMaterial getMaterial()
    {
        return material;
    }
}