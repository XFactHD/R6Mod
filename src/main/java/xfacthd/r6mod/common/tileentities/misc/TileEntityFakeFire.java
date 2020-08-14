package xfacthd.r6mod.common.tileentities.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.data.types.TileEntityTypes;
import xfacthd.r6mod.common.tileentities.TileEntityOwnable;
import xfacthd.r6mod.common.util.damage.DamageSourceGadget;

import java.util.function.Predicate;

public class TileEntityFakeFire extends TileEntityOwnable implements ITickableTileEntity
{
    private static final Predicate<Entity> DAMAGE_PREDICATE = (e) -> e instanceof LivingEntity && e.isAlive();

    private boolean configured = false;
    private int maxAge;
    private int age = 0;
    private int damageTick;
    private float damageAmount;
    private DamageSource damageType;

    public TileEntityFakeFire() { super(TileEntityTypes.tileTypeFakeFire); }

    @Override
    public void tick()
    {
        //noinspection ConstantConditions
        if (!world.isRemote() && configured)
        {
            age++;

            if (age % damageTick == 0)
            {
                world.getEntitiesInAABBexcluding(null, new AxisAlignedBB(pos), DAMAGE_PREDICATE).forEach(entity ->
                        entity.attackEntityFrom(damageType, damageAmount));
            }

            if (age >= maxAge) { world.removeBlock(pos, false); }
        }
    }

    public void configure(int maxAge, int damageTick, float damageAmount, EnumGadget gadget)
    {
        this.maxAge = maxAge;
        this.damageTick = damageTick;
        this.damageAmount = damageAmount;
        this.damageType = new DamageSourceGadget(getOwner(), "r6mod.fire", gadget);
        configured = true;
    }
}