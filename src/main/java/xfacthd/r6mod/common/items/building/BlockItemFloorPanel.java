package xfacthd.r6mod.common.items.building;

import net.minecraft.item.BlockItem;
import xfacthd.r6mod.common.blocks.building.BlockFloorPanel;
import xfacthd.r6mod.common.data.blockdata.WallMaterial;

public class BlockItemFloorPanel extends BlockItem
{
    private final WallMaterial material;

    public BlockItemFloorPanel(BlockFloorPanel block, Properties props, WallMaterial material)
    {
        super(block, props);

        this.material = material;
    }

    public WallMaterial getMaterial() { return material; }
}