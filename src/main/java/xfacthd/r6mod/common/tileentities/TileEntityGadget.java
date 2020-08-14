package xfacthd.r6mod.common.tileentities;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TranslationTextComponent;
import xfacthd.r6mod.api.interaction.*;
import xfacthd.r6mod.common.data.InteractState;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.entities.grenade.EntityEMPGrenade;
import xfacthd.r6mod.common.items.gadgets.ItemActivator;
import xfacthd.r6mod.common.util.data.PointManager;

import javax.annotation.Nonnull;
import java.util.UUID;

public abstract class TileEntityGadget extends TileEntityOwnable implements IPickupTime, ITickableTileEntity, IShootable, IShockable, IEMPInteract
{
    private EnumGadget gadget;
    private String team;
    private UUID interactor = null;
    private long startInteract = 0;
    private long lastInteract = 0;
    private boolean firstTick = true;

    public TileEntityGadget(TileEntityType type, EnumGadget gadget)
    {
        super(type);
        this.gadget = gadget;
    }

    @Override
    public void tick()
    {
        if (world == null) { return; }

        if (!world.isRemote && firstTick)
        {
            firstTick = false;
            markFullUpdate();
        }

        if (startInteract != 0 && world.getGameTime() - lastInteract > 4)
        {
            startInteract = 0;
            interactor = null;
        }
    }

    @Override
    public final InteractState pickUp(PlayerEntity player)
    {
        if (interactor != null && !player.getUniqueID().equals(interactor)) { return InteractState.FAILED; }

        if (getOwner() == null || getOwner().getUniqueID().equals(player.getUniqueID()))
        {
            interactor = player.getUniqueID();
            if (getCurrentTime() >= gadget.getPickupTime())
            {
                //noinspection ConstantConditions
                if (!world.isRemote)
                {
                    if (this instanceof IActivatable)
                    {
                        removeActivatorFromInventory(player);
                    }

                    addItemStackFromBlock(player, getBlockState());
                    world.destroyBlock(pos, false);
                }
                return InteractState.SUCCESS;
            }
            else
            {
                if (startInteract == 0)
                {
                    //noinspection ConstantConditions
                    startInteract = world.getGameTime();
                }
                //noinspection ConstantConditions
                lastInteract = world.getGameTime();
                return InteractState.IN_PROGRESS;
            }
        }
        return InteractState.FAILED;
    }

    @Override
    public final int getCurrentTime()
    {
        //noinspection ConstantConditions
        return startInteract != 0 ? (int)(world.getGameTime() - startInteract) : 0;
    }

    @Override
    public void applySonicBurst(PlayerEntity player, int cooldown)
    {
        if (player.getUniqueID().equals(interactor))
        {
            //noinspection ConstantConditions
            startInteract = world.getGameTime() + cooldown;
            markFullUpdate();
        }
    }

    @Override
    public int getPickupTime() { return gadget.getPickupTime(); }

    @Override
    public UUID getPickupInteractor() { return interactor; }

    @Override
    public TranslationTextComponent getPickupMessage() { return gadget.getPickupMessage(); }

    private void removeActivatorFromInventory(PlayerEntity player)
    {
        if (!(this instanceof IActivatable)) { return; }

        for (int i = 0; i < player.inventory.getSizeInventory(); i++)
        {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (stack.getItem() instanceof ItemActivator && stack.hasTag())
            {
                //noinspection ConstantConditions
                BlockPos storedPos = BlockPos.fromLong(stack.getTag().getLong("pos"));
                String objName = stack.getTag().getString("object");
                if (storedPos.equals(pos) && objName.equals(((IActivatable)this).getObjectName()))
                {
                    player.inventory.removeStackFromSlot(i);
                    break;
                }
            }
        }
    }

    private void addItemStackFromBlock(PlayerEntity player, BlockState state)
    {
        Item item = state.getBlock().asItem();
        player.addItemStackToInventory(new ItemStack(item));
    }

    @Override
    public void shoot(PlayerEntity shooter, Vec3d hitVec)
    {
        if (world == null) { return; }

        PointManager.awardGadgetDestroyed(gadget, shooter, getTeam());
        world.destroyBlock(pos, false);
    }

    @Override
    public void shock(PlayerEntity shooter, Vec3d hitVec) { shoot(shooter, hitVec); }

    @Override
    public void empPulse(EntityEMPGrenade emp)
    {
        if (!getTeam().equals("null") && !emp.getTeamName().equals(getTeam()))
        {
            //noinspection ConstantConditions
            world.destroyBlock(pos, false);

            PlayerEntity thrower = emp.getThrowerEntity();
            if (thrower != null)
            {
                PointManager.awardGadgetDisabled(gadget, thrower, getTeam());
            }
        }
    }

    public void setTeam(Team team) { this.team = team != null ? team.getName() : "null"; }

    @Nonnull
    public String getTeam() { return team != null ? team : "null"; }

    @Override
    public void writeNetworkNBT(CompoundNBT nbt)
    {
        nbt.putInt("gadget", gadget.ordinal());
        nbt.putLong("start_time", startInteract);
        super.writeNetworkNBT(nbt);
    }

    @Override
    public void readNetworkNBT(CompoundNBT nbt)
    {
        super.readNetworkNBT(nbt);
        gadget = EnumGadget.values()[nbt.getInt("gadget")];
        startInteract = nbt.getLong("start_time");
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        nbt.putInt("gadget", gadget.ordinal());
        nbt.putString("team", team);
        return super.write(nbt);
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        super.read(nbt);
        gadget = EnumGadget.values()[nbt.getInt("gadget")];
        team = nbt.getString("team");
    }
}