package xfacthd.r6mod.common.items.gadgets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.api.item.IGadgetItem;
import xfacthd.r6mod.common.capability.CapabilityGarraHook;
import xfacthd.r6mod.common.data.ItemGroups;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;

import javax.annotation.Nullable;

public class ItemGarraHook extends Item implements IGadgetItem
{
    public ItemGarraHook()
    {
        super(new Properties()
                .group(ItemGroups.GADGETS)
                .maxStackSize(1)
        );

        setRegistryName(R6Mod.MODID, "item_garra_hook");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        return CapabilityGarraHook.get(stack).handleRightClick(world, player);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
    {
        if (entity instanceof PlayerEntity)
        {
            CapabilityGarraHook.get(stack).tick(world, (PlayerEntity) entity, stack, selected);
        }
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt)
    {
        return new CapabilityGarraHook.Provider(stack);
    }

    @Override
    public EnumGadget getGadget() { return EnumGadget.GARRA_HOOK; }
}