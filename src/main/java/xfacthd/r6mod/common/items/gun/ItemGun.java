package xfacthd.r6mod.common.items.gun;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.api.interaction.IReloadable;
import xfacthd.r6mod.client.render.ister.RenderGun;
import xfacthd.r6mod.common.capability.CapabilityGun;
import xfacthd.r6mod.common.data.ItemGroups;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGun;

import javax.annotation.Nullable;

public class ItemGun extends Item implements IReloadable
{
    private final EnumGun gun;

    public ItemGun(EnumGun gun)
    {
        super(new Item.Properties()
                .group(ItemGroups.GUNS)
                .maxStackSize(1)
                .setISTER(() -> RenderGun::new)
        );
        this.gun = gun;

        setRegistryName(R6Mod.MODID, gun.toItemName());
    }

    public EnumGun getGun() { return gun; }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
    {
        if (entity instanceof PlayerEntity) { CapabilityGun.getFrom(stack).tick((PlayerEntity)entity, slot, selected); }
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt)
    {
        return new CapabilityGun.Provider(stack);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        return oldStack.getItem() != newStack.getItem() || slotChanged;
    }

    /*
     * IReloadable implementation
     */

    @Override
    public void reload(ItemStack stack, PlayerEntity player) { CapabilityGun.getFrom(stack).reload(player); }

    @Override
    public void restock(ItemStack stack, PlayerEntity player, int slot) { CapabilityGun.getFrom(stack).restock(player, slot); }
}