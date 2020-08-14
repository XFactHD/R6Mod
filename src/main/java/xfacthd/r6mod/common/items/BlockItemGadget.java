package xfacthd.r6mod.common.items;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import xfacthd.r6mod.api.item.IGadgetItem;
import xfacthd.r6mod.common.R6Content;
import xfacthd.r6mod.api.interaction.IPlacementTime;
import xfacthd.r6mod.common.blocks.BlockGadget;
import xfacthd.r6mod.common.data.ItemGroups;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;

import javax.annotation.Nullable;

public class BlockItemGadget extends BlockItem implements IPlacementTime, IGadgetItem
{
    private final EnumGadget gadget;

    public BlockItemGadget(BlockGadget block, Properties props, EnumGadget gadget)
    {
        super(block, props.group(ItemGroups.GADGETS));
        this.gadget = gadget;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context)
    {
        BlockItemUseContext blockContext = new BlockItemUseContext(context);
        if (getStateForPlacement(blockContext) == null) { return ActionResultType.FAIL; }
        if (!context.getWorld().isAirBlock(blockContext.getPos())) { return ActionResultType.FAIL; }

        ItemStack stack = context.getItem();
        if (!stack.hasTag()) { stack.setTag(new CompoundNBT()); }

        int placeTime = getCurrentTime(context.getWorld(), context.getItem());
        if (placeTime >= getPlacementTime())
        {
            stack.setTag(null); //Remove tag to make the item stackable again
            return super.onItemUse(context);
        }
        else
        {
            if (placeTime == 0)
            {
                //noinspection ConstantConditions
                stack.getTag().putLong("place_start", context.getWorld().getGameTime());
            }
            //noinspection ConstantConditions
            stack.getTag().putLong("place_last", context.getWorld().getGameTime());
            return ActionResultType.FAIL;
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean isSelected)
    {
        if (stack.hasTag())
        {
            //noinspection ConstantConditions
            long lastInteract = stack.getTag().getLong("place_last");
            if (world.getGameTime() - lastInteract > 5)
            {
                stack.setTag(null);
            }
        }
    }

    @Override
    protected boolean onBlockPlaced(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state)
    {
        if (player == null) { return false; }
        if (!needsActivator()) { return true; }

        ItemStack newStack = new ItemStack(R6Content.itemActivator);
        CompoundNBT nbt = new CompoundNBT();
        nbt.putLong("pos", pos.toLong());
        nbt.putString("object", getObjectName());
        newStack.setTag(nbt);
        player.inventory.addItemStackToInventory(newStack);
        return true;
    }

    public boolean needsActivator() { return gadget.getNeedsActivator(); }

    @Override
    public int getCurrentTime(World world, ItemStack stack)
    {
        //noinspection ConstantConditions
        long startInteract = stack.hasTag() ? stack.getTag().getLong("place_start") : 0;
        return startInteract != 0 ? (int)(world.getGameTime() - startInteract) : 0;
    }

    @Override
    public int getPlacementTime() { return gadget.getPlaceTime(); }

    @Override
    public void applySonicBurst(World world, ItemStack stack, int cooldown)
    {
        if (!stack.hasTag()) { return; } //If the stack has no tag, it definitely isn't being used

        if (getCurrentTime(world, stack) != 0) //Item in use
        {
            //noinspection ConstantConditions
            stack.getTag().putLong("place_start", world.getGameTime() + cooldown);
        }
    }

    @Override
    public TranslationTextComponent getPlaceMessage() { return gadget.getPlaceMessage(); }

    @SuppressWarnings("ConstantConditions")
    public String getObjectName() { return getRegistryName().getPath(); }

    @Override
    public EnumGadget getGadget() { return gadget; }
}