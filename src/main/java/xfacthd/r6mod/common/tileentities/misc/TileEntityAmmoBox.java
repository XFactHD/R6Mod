package xfacthd.r6mod.common.tileentities.misc;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.TranslationTextComponent;
import xfacthd.r6mod.api.interaction.IReloadable;
import xfacthd.r6mod.api.tileentity.IUsageTimeTile;
import xfacthd.r6mod.common.data.InteractState;
import xfacthd.r6mod.common.data.types.TileEntityTypes;
import xfacthd.r6mod.common.tileentities.TileEntityBase;

import java.util.HashMap;
import java.util.UUID;

public class TileEntityAmmoBox extends TileEntityBase implements ITickableTileEntity, IUsageTimeTile
{
    private static final TranslationTextComponent USE_TEXT = new TranslationTextComponent("msg.r6mod.use.ammo_box");

    private Direction facing;
    private final HashMap<UUID, Long> startInteract = new HashMap<>();
    private final HashMap<UUID, Long> lastInteract = new HashMap<>();
    private UUID idToUpdate = null;

    public TileEntityAmmoBox() { super(TileEntityTypes.tileTypeAmmoBox); }

    public TileEntityAmmoBox(Direction facing)
    {
        this();
        this.facing = facing;
    }

    @Override
    public void tick()
    {
        if (world == null) { return; }
        for (UUID entry : startInteract.keySet())
        {
            if (startInteract.get(entry) != 0 && world.getGameTime() - lastInteract.get(entry) > 4)
            {
                startInteract.remove(entry);
                lastInteract.remove(entry);
            }
        }
    }

    @Override
    public InteractState interact(PlayerEntity player)
    {
        if (startInteract.containsKey(player.getUniqueID()))
        {
            long start = startInteract.get(player.getUniqueID());
            //noinspection ConstantConditions
            if (world.getGameTime() - start >= getUsageTime())
            {
                finishInteraction(player);
                return InteractState.SUCCESS;
            }
            else
            {
                lastInteract.put(player.getUniqueID(), world.getGameTime());
                return InteractState.IN_PROGRESS;
            }
        }
        else if (canInteract(player))
        {
            //noinspection ConstantConditions
            startInteract.put(player.getUniqueID(), world.getGameTime());
            lastInteract.put(player.getUniqueID(), world.getGameTime());
            return InteractState.IN_PROGRESS;
        }
        return InteractState.FAILED;
    }

    @Override
    public int getCurrentTime(PlayerEntity player)
    {
        if (!startInteract.containsKey(player.getUniqueID())) { return 0; }

        //noinspection ConstantConditions
        return (int)(world.getGameTime() - startInteract.get(player.getUniqueID()));
    }

    @Override
    public void applySonicBurst(PlayerEntity player, int cooldown)
    {
        if (getCurrentTime(player) != 0) //Player is interacting
        {
            //noinspection ConstantConditions
            startInteract.put(player.getUniqueID(), world.getGameTime() + cooldown);
            markFullUpdate();
        }
    }

    @Override
    public int getUsageTime() { return 60; }

    @Override
    public TranslationTextComponent getUseMessage() { return USE_TEXT; }

    public Direction getFacing() { return facing; }

    private boolean canInteract(PlayerEntity player)
    {
        for (int i = 0; i < player.inventory.getSizeInventory(); i++)
        {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (stack.getItem() instanceof IReloadable) { return true; }
        }
        return false;
    }

    private void finishInteraction(PlayerEntity player)
    {
        //noinspection ConstantConditions
        if (world.isRemote) { return; }

        for (int i = 0; i < player.inventory.getSizeInventory(); i++)
        {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (stack.getItem() instanceof IReloadable)
            {
                ((IReloadable)stack.getItem()).restock(stack, player, i);
            }
        }
    }

    @Override
    public void writeNetworkNBT(CompoundNBT nbt)
    {
        nbt.putInt("facing", facing.getIndex());
        nbt.putBoolean("update", idToUpdate != null);
        if (idToUpdate != null)
        {
            nbt.putUniqueId("toUpdate", idToUpdate);
            nbt.putLong("start_interact", startInteract.get(idToUpdate));
            idToUpdate = null;
        }
    }

    @Override
    public void readNetworkNBT(CompoundNBT nbt)
    {
        facing = Direction.byIndex(nbt.getInt("facing"));
        if (nbt.getBoolean("update"))
        {
            UUID toUpdate = nbt.getUniqueId("toUpdate");
            startInteract.put(toUpdate, nbt.getLong("start_interact"));
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        nbt.putInt("facing", facing.getIndex());
        return super.write(nbt);
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        super.read(nbt);
        facing = Direction.byIndex(nbt.getInt("facing"));
    }
}