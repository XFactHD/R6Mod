package xfacthd.r6mod.common.items.gadgets;

import net.minecraft.item.Item;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.common.data.ItemGroups;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadgetAmmo;

public class ItemGadgetAmmo extends Item
{
    private final EnumGadgetAmmo ammo;

    public ItemGadgetAmmo(EnumGadgetAmmo ammo)
    {
        super(new Item.Properties().group(ItemGroups.AMMO).maxStackSize(16));
        this.ammo = ammo;

        setRegistryName(R6Mod.MODID, ammo.toItemName());
    }

    public EnumGadgetAmmo getGadgetAmmo()
    {
        return ammo;
    }
}