package xfacthd.r6mod.common.data.effects;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.UUID;

public class EffectEMP extends AbstractEffect
{
    private final UUID source;

    public EffectEMP(ServerPlayerEntity player, UUID source, int time, long start)
    {
        super(player, EnumEffect.EMP, time, start);
        this.source = source;
    }

    @Override
    protected void handleEffect(int runTime) { /*NOOP*/ }

    @Override
    public void drawEffect(MatrixStack matrix) { /*NOOP*/ }

    public UUID getSource() { return source; }

    @Override
    protected boolean showProgress() { return true; }

    @Override
    protected boolean isPositive() { return false; }
}