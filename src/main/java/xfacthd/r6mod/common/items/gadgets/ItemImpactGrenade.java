package xfacthd.r6mod.common.items.gadgets;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.common.data.ItemGroups;
import xfacthd.r6mod.common.entities.grenade.EntityImpactGrenade;

public class ItemImpactGrenade extends Item
{
    public ItemImpactGrenade()
    {
        super(new Properties()
                .maxStackSize(16)
                .group(ItemGroups.GADGETS)
        );

        setRegistryName(R6Mod.MODID, "item_impact_grenade");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote())
        {
            String team = player.getTeam() != null ? player.getTeam().getName() : "null";
            EntityImpactGrenade grenade = new EntityImpactGrenade(world, player, team);
            if (world.addEntity(grenade))
            {
                stack.shrink(1);
                player.inventory.markDirty();
            }
        }
        return ActionResult.resultConsume(stack);
    }
}