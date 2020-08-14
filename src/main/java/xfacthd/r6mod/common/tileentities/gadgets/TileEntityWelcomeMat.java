package xfacthd.r6mod.common.tileentities.gadgets;

import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import xfacthd.r6mod.common.capability.CapabilityDBNO;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.data.types.TileEntityTypes;
import xfacthd.r6mod.common.tileentities.TileEntityGadget;
import xfacthd.r6mod.common.util.damage.DamageSourceWelcomeMat;

import java.lang.ref.WeakReference;
import java.util.UUID;

public class TileEntityWelcomeMat extends TileEntityGadget
{
    private UUID trappedUUID = null;
    private int trappedId = -1;
    private WeakReference<LivingEntity> trapped;

    public TileEntityWelcomeMat() { super(TileEntityTypes.tileTypeWelcomeMat, EnumGadget.WELCOME_MAT); }

    @Override
    public void tick()
    {
        super.tick();

        LivingEntity entity = getTrappedEntity();
        if (entity != null)
        {
            //AttributeModifiers don't seem to work properly for this
            entity.setMotion(Vector3d.ZERO);
            entity.setPosition(pos.getX() + .5, pos.getY() + (1.1F/16F), pos.getZ() + .5D);
        }
    }

    public boolean shouldTrap(Entity entity)
    {
        if (!(entity instanceof LivingEntity)) { return false; }

        if (getTeam().equals("null"))
        {
            return getOwner() == null || entity == getOwner();
        }
        else
        {
            return entity.getTeam() == null || entity.getTeam().getName().equals(getTeam());
        }
    }

    public void trapEntity(Entity entity)
    {
        if (!shouldTrap(entity)) { return; }

        LivingEntity living = (LivingEntity)entity;

        living.setMotion(Vector3d.ZERO);
        living.setPosition(pos.getX() + .5, pos.getY() + (1.1F/16F), pos.getZ() + .5D);

        if (living instanceof PlayerEntity)
        {
            DamageSource source = new DamageSourceWelcomeMat(getOwner());
            CapabilityDBNO.getFrom((PlayerEntity)living).putInTrapDbno(source, pos);
        }

        trappedUUID = living.getUniqueID();
        trappedId = living.getEntityId();
        trapped = new WeakReference<>(living);

        markFullUpdate();
    }

    public LivingEntity getTrappedEntity()
    {
        if (trapped == null)
        {
            Entity entity;
            //noinspection ConstantConditions
            if (world.isRemote())
            {
                entity = world.getEntityByID(trappedId);
            }
            else
            {
                entity = ((ServerWorld)world).getEntityByUuid(trappedUUID);
            }

            if (entity != null && !(entity instanceof LivingEntity))
            {
                throw new IllegalArgumentException(String.format("Entity with ID %s changed type!", trappedUUID.toString()));
            }

            trapped = new WeakReference<>((LivingEntity) entity);
        }
        return trapped.get();
    }

    @Override
    public void writeNetworkNBT(CompoundNBT nbt)
    {
        super.writeNetworkNBT(nbt);
        nbt.putInt("trappedId", trappedId);
    }

    @Override
    public void readNetworkNBT(CompoundNBT nbt)
    {
        super.readNetworkNBT(nbt);
        trappedId = nbt.getInt("trappedId");
        if (trappedId != -1)
        {
            //noinspection ConstantConditions
            Entity entity = world.getEntityByID(trappedId);
            if (entity instanceof LivingEntity)
            {
                trapped = new WeakReference<>((LivingEntity) entity);
            }
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        if (trappedUUID != null) { nbt.putUniqueId("trapped", trappedUUID); }
        nbt.putInt("trappedId", trappedId);
        return super.write(nbt);
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        super.read(nbt);
        if (nbt.hasUniqueId("trapped"))
        {
            trappedUUID = nbt.getUniqueId("trapped");
        }
        trappedId = nbt.getInt("trappedId");
    }
}