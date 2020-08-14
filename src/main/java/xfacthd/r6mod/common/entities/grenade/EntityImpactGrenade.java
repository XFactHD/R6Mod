package xfacthd.r6mod.common.entities.grenade;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import xfacthd.r6mod.api.interaction.IDestructable;
import xfacthd.r6mod.common.R6Content;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.data.types.EntityTypes;
import xfacthd.r6mod.common.util.*;
import xfacthd.r6mod.common.util.damage.DamageSourceImpactGrenade;

public class EntityImpactGrenade extends AbstractEntityGrenade
{
    private static final int BLOCK_RADIUS = 1;
    private static final double ENTITY_RADIUS = 2D;
    private static final float DAMAGE = 10F;

    public EntityImpactGrenade(World world) { super(EntityTypes.entityTypeImpactGrenade, world); }

    public EntityImpactGrenade(World world, PlayerEntity thrower, String team)
    {
        super(EntityTypes.entityTypeImpactGrenade, world, thrower, team, world.getGameTime());
    }

    @Override
    protected long getFuseLength() { return 20; }

    @Override
    protected ItemStack getRenderStack() { return new ItemStack(R6Content.itemImpactGrenade); }

    @Override
    protected void onTimerExpired() { explode(); }

    @Override
    protected void onImpact() { explode(); }

    @Override
    public void neutralize() { explode(); }

    private void explode()
    {
        //Spawn explosion particles
        ((ServerWorld)world).spawnParticle(ParticleTypes.EXPLOSION, getPosX(), getPosY(), getPosZ(), 1, 0, 0, 0, 0);

        //Play sound
        world.playSound(null, getPosX(), getPosY(), getPosZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.NEUTRAL, 1, WorldUtils.getRandomSoundPitch(world));

        //Destroy blocks
        BlockPos origin = getPosition();
        for (BlockPos pos : BlockPos.getAllInBoxMutable(origin.add(-BLOCK_RADIUS, -BLOCK_RADIUS, -BLOCK_RADIUS), origin.add(BLOCK_RADIUS, BLOCK_RADIUS, BLOCK_RADIUS)))
        {
            BlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof IDestructable)
            {
                ((IDestructable)state.getBlock()).destroy(world, pos, state, getThrowerEntity(), EnumGadget.IMPACT_GRENADE, null);
            }
            else if (Config.INSTANCE.destroyWood && state.getMaterial() == Material.WOOD)
            {
                world.destroyBlock(pos, false);
            }
        }

        //Damage entities
        AxisAlignedBB aabb = new AxisAlignedBB(getPosX() - ENTITY_RADIUS, getPosY() - ENTITY_RADIUS, getPosZ() - ENTITY_RADIUS,
                                               getPosX() + ENTITY_RADIUS, getPosY() + ENTITY_RADIUS, getPosZ() + ENTITY_RADIUS);
        DamageSourceImpactGrenade source = new DamageSourceImpactGrenade(getThrowerEntity());
        for (Entity entity : world.getEntitiesInAABBexcluding(null, aabb, Entity::isAlive))
        {
            float damage = DAMAGE * (float)(entity.getDistanceSq(this) / ENTITY_RADIUS);
            entity.attackEntityFrom(source, damage);
        }

        //Delete entity
        remove();
    }
}