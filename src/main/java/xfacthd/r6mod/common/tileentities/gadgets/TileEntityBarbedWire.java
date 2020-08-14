package xfacthd.r6mod.common.tileentities.gadgets;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.util.text.TranslationTextComponent;
import xfacthd.r6mod.api.tileentity.IUsageTimeTile;
import xfacthd.r6mod.common.R6Content;
import xfacthd.r6mod.common.data.InteractState;
import xfacthd.r6mod.common.data.PropertyHolder;
import xfacthd.r6mod.common.data.blockdata.WallSegment;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.data.types.TileEntityTypes;
import xfacthd.r6mod.common.tileentities.TileEntityGadget;

import java.util.UUID;

public class TileEntityBarbedWire extends TileEntityGadget implements IUsageTimeTile
{
    private static final TranslationTextComponent PLACE_TEXT = new TranslationTextComponent("msg.r6mod.place.shock_wire");
    private static final TranslationTextComponent REMOVE_TEXT = new TranslationTextComponent("msg.r6mod.remove.shock_wire");

    private UUID shockWireOwner = null;
    private UUID interactor = null;
    private boolean placing = false;
    private long startUse = 0;
    private long lastUse = 0;

    public TileEntityBarbedWire() { super(TileEntityTypes.tileTypeBarbedWire, EnumGadget.BARBED_WIRE); }

    public boolean shouldSlow(Entity entity)
    {
        if (getTeam().equals("null"))
        {
            PlayerEntity owner = getOwner();
            return owner == null || !entity.getUniqueID().equals(owner.getUniqueID());
        }
        else
        {
            return entity.getTeam() == null || !entity.getTeam().getName().equals(getTeam());
        }
    }

    public boolean isShockWireOwner(PlayerEntity player)
    {
        if (isNotMaster())
        {
            TileEntityBarbedWire te = getMaster();
            return te != null && te.isShockWireOwner(player);
        }
        return player.getUniqueID().equals(shockWireOwner);
    }

    public UUID getShockWireOwner()
    {
        if (isNotMaster())
        {
            TileEntityBarbedWire te = getMaster();
            return te != null ? te.shockWireOwner : null;
        }
        return shockWireOwner;
    }

    public ActionResultType placeShockWire(PlayerEntity player)
    {
        if (isNotMaster())
        {
            TileEntityBarbedWire te = getMaster();
            return te != null ? te.placeShockWire(player) : ActionResultType.FAIL;
        }

        if (getBlockState().get(PropertyHolder.ELECTRIFIED)) { return ActionResultType.FAIL; }
        if (interactor != null && !interactor.equals(player.getUniqueID())) { return ActionResultType.FAIL; }

        Team team = player.getTeam();
        if (team == null && !getTeam().equals("null")) { return ActionResultType.FAIL; }
        if (team == null && player != getOwner()) { return ActionResultType.FAIL; }
        if (team != null && !team.getName().equals(getTeam())) { return ActionResultType.FAIL; }

        placing = true;
        return interact(player).toActionResultType();
    }

    public ActionResultType removeShockWire(PlayerEntity player)
    {
        if (isNotMaster())
        {
            TileEntityBarbedWire te = getMaster();
            return te != null ? te.removeShockWire(player) : ActionResultType.FAIL;
        }

        if (!getBlockState().get(PropertyHolder.ELECTRIFIED)) { return ActionResultType.FAIL; }
        if (!player.getUniqueID().equals(shockWireOwner)) { return ActionResultType.FAIL; }

        placing = false;
        return interact(player).toActionResultType();
    }

    public void onDestroyed()
    {
        if (isNotMaster())
        {
            TileEntityBarbedWire te = getMaster();
            if (te != null) { te.onDestroyed(); }
            return;
        }

        //Reset shock wire owner to make sure he doesn't get the item back
        shockWireOwner = null;
    }



    @Override
    public void tick()
    {
        super.tick();

        //noinspection ConstantConditions
        if (startUse != 0 && world.getGameTime() - lastUse > 4)
        {
            startUse = 0;
            interactor = null;
        }
    }



    @Override
    public InteractState interact(PlayerEntity player)
    {
        if (interactor == null) { interactor = player.getUniqueID(); }

        if (getCurrentTime(player) >= getUsageTime())
        {
            //noinspection ConstantConditions
            if (!world.isRemote())
            {
                shockWireOwner = placing ? player.getUniqueID() : null;
                setElectrified(placing);
                markFullUpdate();

                if (!placing)
                {
                    player.addItemStackToInventory(new ItemStack(R6Content.blockShockWire));
                }
            }
            return InteractState.SUCCESS;
        }
        else
        {
            if (startUse == 0)
            {
                //noinspection ConstantConditions
                startUse = world.getGameTime();
            }
            //noinspection ConstantConditions
            lastUse = world.getGameTime();
            return InteractState.IN_PROGRESS;
        }
    }

    @Override
    public int getCurrentTime(PlayerEntity player)
    {
        if (isNotMaster())
        {
            TileEntityBarbedWire te = getMaster();
            return te != null ? te.getCurrentTime(player) : 0;
        }

        if (!player.getUniqueID().equals(interactor)) { return 0; }

        //noinspection ConstantConditions
        return startUse != 0 ? (int)(world.getGameTime() - startUse) : 0;
    }

    @Override
    public void applySonicBurst(PlayerEntity player, int cooldown)
    {
        if (isNotMaster())
        {
            TileEntityBarbedWire te = getMaster();
            if (te != null)
            {
                te.applySonicBurst(player, cooldown);
            }
            return;
        }

        if (player.getUniqueID().equals(interactor))
        {
            //noinspection ConstantConditions
            startUse = world.getGameTime() + cooldown;
            markFullUpdate();
        }
    }

    @Override
    public int getUsageTime()
    {
        if (isNotMaster())
        {
            TileEntityBarbedWire te = getMaster();
            return te != null ? te.getUsageTime() : -1;
        }
        return placing ? EnumGadget.SHOCK_WIRE.getPlaceTime() : EnumGadget.SHOCK_WIRE.getPickupTime();
    }

    @Override
    public TranslationTextComponent getUseMessage()
    {
        if (isNotMaster())
        {
            TileEntityBarbedWire te = getMaster();
            return te != null ? te.getUseMessage() : null;
        }
        return placing ? PLACE_TEXT : REMOVE_TEXT;
    }



    @Override
    public void shoot(PlayerEntity shooter, Vec3d hitVec)
    {
        if (!getBlockState().get(PropertyHolder.ELECTRIFIED)) { return; }

        WallSegment segment = getBlockState().get(PropertyHolder.SQUARE_SEGMENT);

        double minX = pos.getX() + (segment.isRight() ? -.3125D : .6875D);
        double minY = pos.getY();
        double minZ = pos.getZ() + (segment.isTop() ? .625D : -.375D);
        double maxX = pos.getX() + (segment.isRight() ? .3125D : 1.3125D);
        double maxY = pos.getY() + .4375D;
        double maxZ = pos.getZ() + (segment.isTop() ? 1.375D : .375D);

        AxisAlignedBB aabb = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);

        Vec3d normal = shooter.getLookVec().scale(2);
        if (aabb.rayTrace(hitVec, hitVec.add(normal)).isPresent())
        {
            shockWireOwner = null;
            setElectrified(false);
        }
    }



    private void setElectrified(boolean electrified)
    {
        BlockState state = getBlockState();

        WallSegment segment = state.get(PropertyHolder.SQUARE_SEGMENT);
        Direction xOff = segment.isRight() ? Direction.WEST : Direction.EAST;
        Direction zOff = segment.isTop() ? Direction.SOUTH : Direction.NORTH;

        //noinspection ConstantConditions
        world.setBlockState(pos, state.with(PropertyHolder.ELECTRIFIED, electrified));

        BlockPos adjPos = pos.offset(xOff);
        state = world.getBlockState(adjPos);
        world.setBlockState(adjPos, state.with(PropertyHolder.ELECTRIFIED, electrified));

        adjPos = pos.offset(zOff);
        state = world.getBlockState(adjPos);
        world.setBlockState(adjPos, state.with(PropertyHolder.ELECTRIFIED, electrified));

        adjPos = pos.offset(xOff).offset(zOff);
        state = world.getBlockState(adjPos);
        world.setBlockState(adjPos, state.with(PropertyHolder.ELECTRIFIED, electrified));
    }

    private boolean isNotMaster()
    {
        if (getBlockState().getBlock() != R6Content.blockBarbedWire) { return false; }
        return getBlockState().get(PropertyHolder.SQUARE_SEGMENT) != WallSegment.BOTTOM_LEFT;
    }

    private TileEntityBarbedWire getMaster()
    {
        if (getBlockState().getBlock() != R6Content.blockBarbedWire) { return null; }

        BlockPos masterPos = pos;
        WallSegment segment = getBlockState().get(PropertyHolder.SQUARE_SEGMENT);
        if (segment.isRight()) { masterPos = masterPos.offset(Direction.WEST); }
        if (segment.isTop()) { masterPos = masterPos.offset(Direction.SOUTH); }

        //noinspection ConstantConditions
        TileEntity te = world.getTileEntity(masterPos);
        if (te != null && !(te instanceof TileEntityBarbedWire)) { throw new IllegalStateException("Invalid TileEntity!"); }
        return (TileEntityBarbedWire)te;
    }



    @Override
    public void writeNetworkNBT(CompoundNBT nbt)
    {
        super.writeNetworkNBT(nbt);
        if (shockWireOwner != null) { nbt.putUniqueId("shockWireOwner", shockWireOwner); }
    }

    @Override
    public void readNetworkNBT(CompoundNBT nbt)
    {
        super.readNetworkNBT(nbt);
        if (nbt.hasUniqueId("shockWireOwner")) { shockWireOwner = nbt.getUniqueId("shockWireOwner"); }
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        if (shockWireOwner != null) { nbt.putUniqueId("shockWireOwner", shockWireOwner); }
        return super.write(nbt);
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        super.read(nbt);
        if (nbt.hasUniqueId("shockWireOwner")) { shockWireOwner = nbt.getUniqueId("shockWireOwner"); }
    }
}