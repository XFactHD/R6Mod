package xfacthd.r6mod.common.tileentities.misc;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import xfacthd.r6mod.common.container.ContainerMagFiller;
import xfacthd.r6mod.common.data.PropertyHolder;
import xfacthd.r6mod.common.data.types.TileEntityTypes;
import xfacthd.r6mod.common.data.itemsubtypes.EnumBullet;
import xfacthd.r6mod.common.items.material.ItemBullet;
import xfacthd.r6mod.common.items.gun.ItemMagazine;
import xfacthd.r6mod.common.tileentities.TileEntityBase;
import xfacthd.r6mod.common.util.R6SoundEvents;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityMagFiller extends TileEntityBase implements ITickableTileEntity, INamedContainerProvider
{
    public static final int MAG_SLOT = 3;

    private final LazyOptional<ItemStackHandler> itemHandler = LazyOptional.of(this::createItemHandler);
    private boolean active = false;
    private long lastCheckTime = 0;
    private boolean firstTick = true;

    public TileEntityMagFiller() { super(TileEntityTypes.tileTypeMagFiller); }

    @Override
    public void tick()
    {
        //Only do work on the server and when the itemHandler exists
        //noinspection ConstantConditions
        if (world.isRemote || !itemHandler.isPresent()) { return; }

        if (firstTick)
        {
            firstTick = false;
            markFullUpdate();
        }

        //Retrieve the IItemHandler
        //noinspection ConstantConditions
        ItemStackHandler handler = itemHandler.orElse(null); //Can't be null because of the isPresent() check earlier

        //Do work
        if (!world.isRemote() && world.getGameTime() - lastCheckTime > 5)
        {
            lastCheckTime = world.getGameTime();

            int ammoSlot = checkCanWork(handler);
            if (active) { work(handler, ammoSlot); }
        }
    }

    private int checkCanWork(ItemStackHandler handler)
    {
        boolean wasActive = active;
        active = false;

        if (handler.getStackInSlot(MAG_SLOT).isEmpty()) { return -1; }

        int ammoSlot = findAmmoStack();
        if (ammoSlot == -1) { return -1; }

        ItemStack magStack = handler.getStackInSlot(MAG_SLOT);
        ItemStack ammoStack = handler.getStackInSlot(ammoSlot);
        boolean full = magStack.getDamage() >= magStack.getMaxDamage();
        if (full) { return -1; }

        EnumBullet ammoBullet = ((ItemBullet) ammoStack.getItem()).getBullet();
        EnumBullet magBullet = ((ItemMagazine) magStack.getItem()).getMagazine().getBullet();
        if (ammoBullet == magBullet) { active = true; }
        if (active != wasActive) { markFullUpdate(); }

        return ammoSlot;
    }

    @SuppressWarnings("ConstantConditions")
    private void work(ItemStackHandler handler, int ammoSlot)
    {
        //Get stacks
        ItemStack magStack = handler.getStackInSlot(MAG_SLOT);
        ItemStack ammoStack = handler.getStackInSlot(ammoSlot);

        //Manipulate stacks
        magStack.setDamage(magStack.getDamage() + 1);
        ammoStack.shrink(1);
        markDirty();

        //Put stacks back
        handler.setStackInSlot(MAG_SLOT, magStack);
        handler.setStackInSlot(ammoSlot, ammoStack);

        //Make noise
        world.playSound(null, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 0.3F, 0.75F);
    }

    /*
     * Inventory implementation
     */

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
    {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) { return itemHandler.cast(); }
        return super.getCapability(cap, side);
    }

    @SuppressWarnings("ConstantConditions")
    private ItemStackHandler createItemHandler()
    {
        return new ItemStackHandler(4)
        {
            @Override
            protected void onContentsChanged(int slot) { markDirty(); }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack)
            {
                if (slot == MAG_SLOT) { return stack.getItem() instanceof ItemMagazine; }
                return stack.getItem() instanceof ItemBullet;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
            {
                if (slot == MAG_SLOT && !(stack.getItem() instanceof ItemMagazine)) { return stack; }
                if (slot != MAG_SLOT && !(stack.getItem() instanceof ItemBullet)) { return stack; }

                ItemStack result = super.insertItem(slot, stack, simulate);
                if (!simulate && slot == MAG_SLOT && result != stack) { world.playSound(null, pos, R6SoundEvents.getMiscSound("mag_filler_mag_in"), SoundCategory.BLOCKS, 1F, 1F); }
                if (!simulate && slot != MAG_SLOT && result != stack) { world.playSound(null, pos, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, SoundCategory.BLOCKS, 1F, 1F); }
                return result;
            }

            @Nonnull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate)
            {
                ItemStack result = super.extractItem(slot, amount, simulate);
                if (!simulate && slot == MAG_SLOT && !result.isEmpty()) { world.playSound(null, pos, R6SoundEvents.getMiscSound("mag_filler_mag_out"), SoundCategory.BLOCKS, 1F, 1F); }
                return result;
            }
        };
    }

    @SuppressWarnings("ConstantConditions")
    public void dropContents()
    {
        if (!itemHandler.isPresent()) { return; }
        ItemStackHandler handler = itemHandler.orElse(null); //Can't be null because of the isPresent() check earlier

        for (int i = 0; i < handler.getSlots(); i++)
        {
            InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(i));
        }
    }

    /*
     * Getters
     */

    public Direction getFacing() { return getBlockState().get(PropertyHolder.FACING_HOR); }

    public boolean isActive() { return active; }

    /*
     * Private helpers
     */

    @SuppressWarnings("ConstantConditions")
    private int findAmmoStack()
    {
        if (!itemHandler.isPresent()) { return -1; }
        ItemStackHandler handler = itemHandler.orElse(null); //Can't be null because of the isPresent() check earlier

        for (int i = 2; i >= 0; i--)
        {
            if (!handler.getStackInSlot(i).isEmpty()) { return i; }
        }
        return -1;
    }

    /*
     * NBT stuff
     */

    @Override
    public void writeNetworkNBT(CompoundNBT nbt)
    {
        nbt.putBoolean("active", active);
        itemHandler.ifPresent((handler) -> nbt.put("inv", ((INBTSerializable<CompoundNBT>)handler).serializeNBT()));
    }

    @Override
    public void readNetworkNBT(CompoundNBT nbt)
    {
        active = nbt.getBoolean("active");
        itemHandler.ifPresent((handler) -> ((INBTSerializable<CompoundNBT>)handler).deserializeNBT(nbt.getCompound("inv")));
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        nbt.putBoolean("active", active);
        itemHandler.ifPresent((handler) -> nbt.put("inv", ((INBTSerializable<CompoundNBT>)handler).serializeNBT()));
        return super.write(nbt);
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        super.read(nbt);
        active = nbt.getBoolean("active");
        itemHandler.ifPresent((handler) -> ((INBTSerializable<CompoundNBT>)handler).deserializeNBT(nbt.getCompound("inv")));
    }

    @Override
    public ITextComponent getDisplayName() { return new TranslationTextComponent("gui.r6mod.mag_filler"); }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory inv, PlayerEntity player)
    {
        assert world != null;
        return new ContainerMagFiller(id, world, pos, player, inv);
    }
}