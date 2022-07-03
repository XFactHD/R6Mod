package xfacthd.r6mod.api.capability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

public interface ICapabilityGarraHook extends INBTSerializable<CompoundNBT>
{
    ActionResult<ItemStack> handleRightClick(World world, PlayerEntity player);

    void tick(World world, PlayerEntity player, ItemStack stack, boolean selected);
}