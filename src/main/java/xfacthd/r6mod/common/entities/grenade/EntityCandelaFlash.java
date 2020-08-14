package xfacthd.r6mod.common.entities.grenade;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import xfacthd.r6mod.common.R6Content;
import xfacthd.r6mod.common.data.effects.EnumEffect;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.data.types.EntityTypes;
import xfacthd.r6mod.common.event.EffectEventHandler;
import xfacthd.r6mod.common.util.R6SoundEvents;

import java.util.List;

public class EntityCandelaFlash extends Entity
{
    private static final int LIFE_TIME = 5; //TODO: find actual time to detonation
    private static final double MAX_RANGE = 5;
    private static final double MAX_RANGE_SQ = MAX_RANGE * MAX_RANGE;
    private static final int MIN_DURATION = 10;
    private static final int MAX_DURATION = 60;

    private EntityCandelaGrenade source;
    private boolean last;

    public EntityCandelaFlash(World world) { super(EntityTypes.entityTypeCandelaFlash, world); }

    public EntityCandelaFlash(EntityCandelaGrenade source, Vec3d pos, Vec3d motion, boolean last)
    {
        this(source.world);

        this.source = source;
        this.last = last;

        forceSetPosition(pos.getX(), pos.getY(), pos.getZ());
        setMotion(motion);
    }

    @Override
    public void tick()
    {
        super.tick();

        if (!world.isRemote())
        {
            //If the source is null, the world was unloaded and reloaded and the flash is thus invalid for simplicity
            if (source == null)
            {
                remove();
                return;
            }

            if (ticksExisted >= LIFE_TIME)
            {
                explode();
            }
        }
    }

    private void explode()
    {
        AxisAlignedBB aabb = new AxisAlignedBB(getPosX() - MAX_RANGE, getPosY() - MAX_RANGE, getPosZ() - MAX_RANGE,
                                               getPosX() + MAX_RANGE, getPosY() + MAX_RANGE, getPosZ() + MAX_RANGE);
        List<Entity> entities = world.getEntitiesInAABBexcluding(null, aabb, (e) -> (e instanceof ServerPlayerEntity) && e.isAlive());
        for (Entity e : entities)
        {
            ServerPlayerEntity player = (ServerPlayerEntity)e;

            ItemStack helmet = player.inventory.armorItemInSlot(3); //TODO: activate when glance is implemented
            if (helmet.getItem() == R6Content.itemYingGlasses/* || helmet.getItem() == R6Content.itemGlanceSmartGlasses*/)
            {
                //noinspection ConstantConditions
                if (helmet.getTag().getBoolean("active"))
                {
                    continue;
                }
            }

            double dist = getPositionVec().squareDistanceTo(player.getPositionVec());
            if (dist <= MAX_RANGE_SQ)
            {
                double mult = dist / MAX_RANGE_SQ;
                int time = (int)MathHelper.lerp(mult, MAX_DURATION, MIN_DURATION);
                EffectEventHandler.addEffect(player, EnumEffect.CANDELA_FLASH, time);
            }
        }

        if (last)
        {
            PlayerEntity player = source.getThrowerEntity();
            if (player != null)
            {
                ItemStack helmet = player.inventory.armorItemInSlot(3);
                if (helmet.getItem() == R6Content.itemYingGlasses)
                {
                    //noinspection ConstantConditions
                    helmet.getTag().putBoolean("active", false);
                    player.inventory.markDirty();
                }
            }
        }

        world.playSound(null, getPosX(), getPosY(), getPosZ(), R6SoundEvents.getGadgetSound(EnumGadget.CANDELA_GRENADE, "crackle"), SoundCategory.BLOCKS, 1, 1);
        remove();
    }

    @Override
    protected void registerData() { }

    @Override
    protected void readAdditional(CompoundNBT nbt) { }

    @Override
    protected void writeAdditional(CompoundNBT nbt) { }

    @Override
    public IPacket<?> createSpawnPacket() { return NetworkHooks.getEntitySpawningPacket(this); }
}