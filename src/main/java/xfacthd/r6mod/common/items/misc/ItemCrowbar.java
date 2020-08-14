package xfacthd.r6mod.common.items.misc;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.api.item.IUsageTimeItem;
import xfacthd.r6mod.common.blocks.building.BlockBarricade;
import xfacthd.r6mod.common.blocks.gadgets.BlockToughBarricade;
import xfacthd.r6mod.common.data.InteractState;
import xfacthd.r6mod.common.data.ItemGroups;
import xfacthd.r6mod.common.util.Config;

import java.util.UUID;

public class ItemCrowbar extends Item implements IUsageTimeItem
{
    private static final TranslationTextComponent USE_MSG_BARRICADE = new TranslationTextComponent("msg.r6mod.use_crowbar.barricade");
    private static final TranslationTextComponent USE_MSG_TOUGH_BARRICADE = new TranslationTextComponent("msg.r6mod.use_crowbar.tough_barricade");

    public ItemCrowbar()
    {
        super(new Properties().maxStackSize(1).group(ItemGroups.MISC));
        setRegistryName(R6Mod.MODID, "item_crowbar");
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context)
    {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        BlockState state = world.getBlockState(pos);
        PlayerEntity player = context.getPlayer();
        if (player != null && state.getBlock() instanceof BlockBarricade)
        {
            return interact(world, pos, state, stack, player).toActionResultType();
        }
        return ActionResultType.FAIL;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean isSelected)
    {
        if (stack.hasTag())
        {
            //noinspection ConstantConditions
            long lastInteract = stack.getTag().getLong("use_last");
            if (world.getGameTime() - lastInteract > 5)
            {
                stack.getTag().putLong("use_start", 0);
                stack.getTag().putLong("use_last", 0);
            }
        }
    }

    @Override
    public InteractState interact(World world, BlockPos pos, BlockState state, ItemStack stack, PlayerEntity player)
    {
        if (!stack.hasTag()) { stack.setTag(new CompoundNBT()); }

        int useTime = getCurrentTime(world, stack, player.getUniqueID());
        if (!Config.INSTANCE.usageTime || useTime >= getUsageTime(stack))
        {
            //noinspection ConstantConditions
            stack.getTag().putLong("use_start", 0);
            stack.getTag().putLong("use_last", 0);

            if (!world.isRemote() && state.getBlock() instanceof BlockBarricade)
            {
                ((BlockBarricade)state.getBlock()).onRemovedByCrowbar(world, pos);
                world.destroyBlock(pos, false);
            }

            return InteractState.SUCCESS;
        }
        else
        {
            if (useTime == 0)
            {
                //noinspection ConstantConditions
                stack.getTag().putLong("use_start", world.getGameTime());

                stack.getTag().putBoolean("tough", state.getBlock() instanceof BlockToughBarricade);
            }
            //noinspection ConstantConditions
            stack.getTag().putLong("use_last", world.getGameTime());
            return InteractState.IN_PROGRESS;
        }
    }

    @Override
    public int getCurrentTime(World world, ItemStack stack, UUID interactor)
    {
        //noinspection ConstantConditions
        if (!stack.hasTag() || stack.getTag().getLong("use_start") == 0) { return 0; }
        return (int)(world.getGameTime() - stack.getTag().getLong("use_start"));
    }

    @Override
    public int getUsageTime(ItemStack stack)
    {
        //noinspection ConstantConditions
        return (stack.hasTag() && stack.getTag().getBoolean("tough")) ? 40 : 20;
    }

    @Override
    public void applySonicBurst(World world, ItemStack stack, UUID interactor, int cooldown)
    {
        if (!stack.hasTag()) { return; } //If the stack has no tag, it definitely isn't being used

        if (getCurrentTime(world, stack, interactor) != 0) //Item in use
        {
            //noinspection ConstantConditions
            stack.getTag().putLong("use_start", world.getGameTime() + cooldown);
        }
    }

    @Override
    public TranslationTextComponent getUseMessage(ItemStack stack)
    {
        //noinspection ConstantConditions
        return (stack.hasTag() && stack.getTag().getBoolean("tough")) ? USE_MSG_TOUGH_BARRICADE : USE_MSG_BARRICADE;
    }
}