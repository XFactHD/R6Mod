package xfacthd.r6mod.common.items.building;

import net.minecraft.item.BlockItem;
import xfacthd.r6mod.common.blocks.building.BlockWall;
import xfacthd.r6mod.common.data.blockdata.WallMaterial;

public class BlockItemWall extends BlockItem
{
    private final WallMaterial material;
    private final boolean barred;

    public BlockItemWall(BlockWall block, Properties props, WallMaterial material, boolean barred)
    {
        super(block, props);
        this.material = material;
        this.barred = barred;
    }

    public WallMaterial getMaterial() { return material; }

    public boolean isBarred() { return barred; }
}