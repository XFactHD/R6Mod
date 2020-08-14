package xfacthd.r6mod.common.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class HitData
{
    private final Type type;
    private final PlayerEntity shooter;
    private final Entity entityHit;
    private final boolean headshot;
    private final BlockPos pos;
    private final Vec3d hitVec;

    public HitData(PlayerEntity shooter, Entity entityHit, boolean headshot, Vec3d hitVec)
    {
        this.type = Type.ENTITY;
        this.shooter = shooter;
        this.entityHit = entityHit;
        this.headshot = headshot;
        this.pos = null;
        this.hitVec = hitVec;
    }

    public HitData(PlayerEntity shooter, BlockPos pos, Vec3d hitVec)
    {
        this.type = Type.BLOCK;
        this.shooter = shooter;
        this.entityHit = null;
        this.headshot = false;
        this.pos = pos;
        this.hitVec = hitVec;
    }

    public Type getHitType() { return type; }

    public PlayerEntity getShooter() { return shooter; }

    public Entity getEntityHit() { return entityHit; }

    public boolean isHeadshot() { return headshot; }

    public BlockPos getPos() { return pos; }

    public Vec3d getHitVec() { return hitVec; }

    public enum Type
    {
        BLOCK,
        ENTITY
    }
}