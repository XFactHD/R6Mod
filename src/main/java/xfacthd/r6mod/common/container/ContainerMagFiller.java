package xfacthd.r6mod.common.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import xfacthd.r6mod.common.R6Content;
import xfacthd.r6mod.common.data.types.ContainerTypes;
import xfacthd.r6mod.common.items.gun.ItemMagazine;
import xfacthd.r6mod.common.items.material.ItemBullet;

public class ContainerMagFiller extends ContainerPlayerInventoryBase
{
    private final TileEntity te;

    @SuppressWarnings("ConstantConditions")
    public ContainerMagFiller(int id, World world, BlockPos pos, PlayerEntity player, PlayerInventory inv)
    {
        super(ContainerTypes.containerTypeMagFiller, id, inv, player);

        te = world.getTileEntity(pos);
        te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((handler) ->
        {
            addSlot(new SlotItemHandler(handler, 0,  60, 16));
            addSlot(new SlotItemHandler(handler, 1,  80, 16));
            addSlot(new SlotItemHandler(handler, 2, 100, 16));
            addSlot(new SlotItemHandler(handler, 3,  80, 73));
        });

        layoutPlayerInventorySlots(8, 101);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean canInteractWith(PlayerEntity player)
    {
        return isWithinUsableDistance(IWorldPosCallable.of(te.getWorld(), te.getPos()), player, R6Content.blockMagFiller);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public ItemStack transferStackInSlot(PlayerEntity player, int index)
    {
        ItemStack resultStack = ItemStack.EMPTY;

        Slot slot = getSlot(index);
        if (slot != null && slot.getHasStack())
        {
            ItemStack slotStack = slot.getStack();
            resultStack = slotStack.copy();
            if (index < 4) //Machine inventory
            {
                if (!mergeItemStack(slotStack, 4, 40, true))
                {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChanged();
            }
            else //Player inventory
            {
                if (slotStack.getItem() instanceof ItemMagazine)
                {
                    if (!mergeItemStack(slotStack, 3, 4, false)) //Try moving magazine to appropriate slot
                    {
                        return ItemStack.EMPTY;
                    }
                    slot.onSlotChanged();
                }
                else if (slotStack.getItem() instanceof ItemBullet) //Try moving bullets to appropriate slots
                {
                    if (!mergeItemStack(slotStack, 0, 3, false))
                    {
                        return ItemStack.EMPTY;
                    }
                    slot.onSlotChanged();
                }
            }
        }

        return resultStack;
    }
}