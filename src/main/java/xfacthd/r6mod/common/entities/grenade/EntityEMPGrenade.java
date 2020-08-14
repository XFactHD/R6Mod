package xfacthd.r6mod.common.entities.grenade;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import xfacthd.r6mod.api.interaction.IEMPInteract;
import xfacthd.r6mod.common.R6Content;
import xfacthd.r6mod.common.data.effects.EnumEffect;
import xfacthd.r6mod.common.data.types.EntityTypes;
import xfacthd.r6mod.common.event.EffectEventHandler;

public class EntityEMPGrenade extends AbstractEntityGrenade
{
    private static final double RADIUS = 5.2D;
    public static final int EFFECT_TIME = 200;

    public EntityEMPGrenade(World world) { super(EntityTypes.entityTypeEMPGrenade, world); }

    public EntityEMPGrenade(World world, PlayerEntity thrower, String team)
    {
        super(EntityTypes.entityTypeEMPGrenade, world, thrower, team);
    }

    @Override
    protected long getFuseLength() { return 30; }

    @Override
    protected ItemStack getRenderStack() { return new ItemStack(R6Content.itemEMPGrenade); }

    @Override
    protected void onTimerExpired()
    {
        //Spawn explosion particles
        ((ServerWorld)world).spawnParticle(ParticleTypes.EXPLOSION, getPosX(), getPosY(), getPosZ(), 1, 0, 0, 0, 0);

        //Play sound
        world.playSound(null, getPosX(), getPosY(), getPosZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.NEUTRAL, 1, 1F);

        //Only destroy stuff, when this has a team
        if (!teamName.equals("null"))
        {
            //Interact with blocks
            BlockPos origin = getPosition();
            for (BlockPos pos : BlockPos.getAllInBoxMutable(origin.add(-RADIUS, -RADIUS, -RADIUS), origin.add(RADIUS, RADIUS, RADIUS)))
            {
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof IEMPInteract)
                {
                    ((IEMPInteract) te).empPulse(this);
                }
            }

            //Interact with entities
            AxisAlignedBB aabb = new AxisAlignedBB(getPosX() - RADIUS, getPosY() - RADIUS, getPosZ() - RADIUS,
                    getPosX() + RADIUS, getPosY() + RADIUS, getPosZ() + RADIUS);
            for (Entity entity : world.getEntitiesInAABBexcluding(null, aabb, entity -> entity instanceof IEMPInteract || entity instanceof PlayerEntity))
            {
                if (entity instanceof IEMPInteract) { ((IEMPInteract) entity).empPulse(this); }
                else
                {
                    PlayerEntity player = (PlayerEntity) entity;
                    if (player.getTeam() != null && !player.getTeam().getName().equals(getTeamName()))
                    {
                        //Effect system used to affect items in the players inventory and to show the debuff icon
                        EffectEventHandler.addEffect((ServerPlayerEntity) player, getThrower(), EnumEffect.EMP, EFFECT_TIME);
                    }
                }
            }
        }

        //Delete entities
        remove();
    }

    @Override
    protected void onImpact() { startTimer(); }

    @Override
    public void neutralize() { onTimerExpired(); }
}