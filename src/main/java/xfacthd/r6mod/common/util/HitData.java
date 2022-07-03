package xfacthd.r6mod.common.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class HitData
{
    private final Type type;
    private final PlayerEntity shooter;
    private final Entity entityHit;
    private final boolean headshot;
    private final BlockPos pos;
    private final Vector3d hitVec;
    private final Direction face;

    public HitData(PlayerEntity shooter, Entity entityHit, boolean headshot, Vector3d hitVec)
    {
        this.type = Type.ENTITY;
        this.shooter = shooter;
        this.entityHit = entityHit;
        this.headshot = headshot;
        this.pos = null;
        this.hitVec = hitVec;
        this.face = null;
    }

    public HitData(PlayerEntity shooter, BlockPos pos, Vector3d hitVec, Direction face)
    {
        this.type = Type.BLOCK;
        this.shooter = shooter;
        this.entityHit = null;
        this.headshot = false;
        this.pos = pos;
        this.hitVec = hitVec;
        this.face = face;
    }

    public Type getHitType() { return type; }

    public PlayerEntity getShooter() { return shooter; }

    public Entity getEntityHit() { return entityHit; }

    public boolean isHeadshot() { return headshot; }

    public BlockPos getPos() { return pos; }

    public Vector3d getHitVec() { return hitVec; }

    public Direction getFace() { return face; }

    public enum Type
    {
        BLOCK,
        ENTITY
    }
}