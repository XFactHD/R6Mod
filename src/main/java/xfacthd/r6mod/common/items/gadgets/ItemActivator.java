package xfacthd.r6mod.common.items.gadgets;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.api.interaction.IActivatable;
import xfacthd.r6mod.common.util.TextStyles;

public class ItemActivator extends Item
{
    private static final TranslationTextComponent USELESS = new TranslationTextComponent("msg.r6mod.item_useless");

    public ItemActivator()
    {
        super(new Item.Properties().maxStackSize(1));
        setRegistryName(R6Mod.MODID, "item_activator");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItem() instanceof ItemActivator && stack.hasTag())
        {
            //noinspection ConstantConditions
            BlockPos pos = BlockPos.fromLong(stack.getTag().getLong("pos"));
            String object = stack.getTag().getString("object");

            //noinspection deprecation
            if (!world.isRemote && world.isBlockLoaded(pos))
            {
                BlockState state = world.getBlockState(pos);
                TileEntity te = world.getTileEntity(pos);

                if (te instanceof IActivatable)
                {
                    ((IActivatable)te).activate(world, pos, object, player);
                    return ActionResult.resultSuccess(ItemStack.EMPTY);
                }
                else if (state.getBlock() instanceof IActivatable)
                {
                    ((IActivatable)state.getBlock()).activate(world, pos, object, player);
                    return ActionResult.resultSuccess(ItemStack.EMPTY);
                }
            }
        }
        return super.onItemRightClick(world, player, hand);
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack)
    {
        if (!stack.hasTag()) { return USELESS.setStyle(TextStyles.RED); }

        ITextComponent displayName = super.getDisplayName(stack);
        if (!(displayName instanceof TranslationTextComponent)) { return displayName; }

        TranslationTextComponent name = (TranslationTextComponent)displayName;

        //noinspection ConstantConditions
        String objName = stack.hasTag() ? stack.getTag().getString("object") : "";
        return name.appendString(" [").append(new TranslationTextComponent("block.r6mod." + objName)).appendString("]");
    }
}