package xfacthd.r6mod.common.items.material;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.common.data.ItemGroups;
import xfacthd.r6mod.common.data.itemsubtypes.EnumBullet;

public class ItemBullet extends Item
{
    private final EnumBullet bullet;

    public ItemBullet(EnumBullet bullet)
    {
        super(new Item.Properties().group(ItemGroups.AMMO));
        this.bullet = bullet;

        setRegistryName(R6Mod.MODID, bullet.toItemName());
    }

    @Override //Makes sure we can sneak click the mag filler with this item
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player)
    {
        return true;
    }

    public EnumBullet getBullet() { return bullet; }
}