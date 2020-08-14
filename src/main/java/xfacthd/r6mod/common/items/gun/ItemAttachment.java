package xfacthd.r6mod.common.items.gun;

import net.minecraft.item.Item;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.common.data.ItemGroups;
import xfacthd.r6mod.common.data.itemsubtypes.EnumAttachment;

public class ItemAttachment extends Item
{
    private final EnumAttachment attachment;

    public ItemAttachment(EnumAttachment attachment)
    {
        super(new Item.Properties().group(ItemGroups.GUNS).maxStackSize(1));
        this.attachment = attachment;

        setRegistryName(R6Mod.MODID, attachment.toItemName());
    }

    public EnumAttachment getAttachment() { return attachment; }
}