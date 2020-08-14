package xfacthd.r6mod.api.item;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import xfacthd.r6mod.common.data.InteractState;

import java.util.UUID;

public interface IUsageTimeItem
{
    InteractState interact(World world, BlockPos pos, BlockState state, ItemStack stack, PlayerEntity player);

    int getUsageTime(ItemStack stack);

    /**
     * @param stack is null if this is called on a TileEntity
     */
    int getCurrentTime(World world, ItemStack stack, UUID interactor);

    /**
     * @param stack is null if this is called on a TileEntity
     */
    void applySonicBurst(World world, ItemStack stack, UUID interactor, int cooldown);

    TranslationTextComponent getUseMessage(ItemStack stack);
}