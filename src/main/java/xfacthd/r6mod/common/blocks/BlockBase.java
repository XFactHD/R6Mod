package xfacthd.r6mod.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.registries.IForgeRegistry;
import xfacthd.r6mod.R6Mod;

public abstract class BlockBase extends Block
{
    protected final ItemGroup group;

    public BlockBase(String name, Properties props, ItemGroup group)
    {
        super(props);
        this.group = group;

        setRegistryName(R6Mod.MODID, name);
    }

    // Override to provide custom Item.Properties
    protected Item.Properties createItemProperties() { return new Item.Properties(); }

    // Override to supply a custom BlockItem
    protected BlockItem createBlockItem(Item.Properties props) { return new BlockItem(this, props); }

    // Registers an Item for this block
    public void registerItemBlock(IForgeRegistry<Item> registry)
    {
        if (getRegistryName() == null) { throw new RuntimeException("Registry name cannot be null"); }

        Item.Properties props = createItemProperties();
        if (group != null) { props.group(group); }
        BlockItem item = createBlockItem(props);
        item.setRegistryName(getRegistryName());
        registry.register(item);
    }
}