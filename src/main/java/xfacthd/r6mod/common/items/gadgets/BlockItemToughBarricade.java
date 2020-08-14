package xfacthd.r6mod.common.items.gadgets;

import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import xfacthd.r6mod.api.item.IGadgetItem;
import xfacthd.r6mod.common.blocks.gadgets.BlockToughBarricade;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.items.building.BlockItemBarricade;

public class BlockItemToughBarricade extends BlockItemBarricade implements IGadgetItem
{
    public BlockItemToughBarricade(BlockToughBarricade block, Properties props) { super(block, props); }

    @Override
    public ActionResultType onItemUse(ItemUseContext context)
    {
        ActionResultType result = super.onItemUse(context);
        if (!context.getWorld().isRemote() && result == ActionResultType.SUCCESS)
        {
            //Update stack size
            context.getItem().shrink(1);
            //noinspection ConstantConditions
            context.getPlayer().inventory.markDirty();
        }
        return result;
    }

    @Override
    public EnumGadget getGadget() { return EnumGadget.TOUGH_BARICADE; }
}