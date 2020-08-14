package xfacthd.r6mod.common.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;

public abstract class ContainerPlayerInventoryBase extends Container
{
    protected final PlayerEntity player;
    protected final IItemHandler playerInventory;

    public ContainerPlayerInventoryBase(@Nullable ContainerType<?> type, int id, PlayerInventory inv, PlayerEntity player)
    {
        super(type, id);

        this.player = player;
        this.playerInventory = new InvWrapper(inv);
    }

    @SuppressWarnings("SameParameterValue")
    protected void layoutPlayerInventorySlots(int posX, int posY)
    {
        // Player inventory
        for (int y = 0; y < 3; y++)
        {
            for (int x = 0; x < 9; x++)
            {
                addSlot(new SlotItemHandler(playerInventory, (y * 9) + x + 9, posX + (x * 18), posY + (y * 18)));
            }
        }

        // Hotbar
        posY += 58;
        for (int x = 0; x < 9; x++)
        {
            addSlot(new SlotItemHandler(playerInventory, x, posX + (x * 18), posY));
        }
    }
}