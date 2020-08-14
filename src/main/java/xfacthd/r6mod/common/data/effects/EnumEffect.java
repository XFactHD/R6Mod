package xfacthd.r6mod.common.data.effects;

import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.Locale;
import java.util.UUID;

public enum EnumEffect
{
    OVERHEAL      ((player, sourcePlayer, effect, time, start) -> new EffectOverheal(player, time, start)),
    YOKAI_BLAST   ((player, sourcePlayer, effect, time, start) -> new EffectYokaiBlast(player, time, start)),
    GU_MINE       ((player, sourcePlayer, effect, time, start) -> new EffectGuMine(player, sourcePlayer, time, start)),
    FINKA_BOOST   ((player, sourcePlayer, effect, time, start) -> new EffectFinkaBoost(player, time, start)),
    EMP           ((player, sourcePlayer, effect, time, start) -> new EffectEMP(player, sourcePlayer, time, start)),
    CANDELA_FLASH ((player, sourcePlayer, effect, time, start) -> new EffectCandelaFlash(player, time, start));

    private final IEffectFactory<AbstractEffect> factory;

    EnumEffect(IEffectFactory<AbstractEffect> factory) { this.factory = factory; }

    public AbstractEffect create(ServerPlayerEntity player, UUID sourcePlayer, int effectTime, long effectStart)
    {
        return factory.create(player, sourcePlayer, this, effectTime, effectStart);
    }

    public AbstractEffect create(ServerPlayerEntity player, int effectTime, long effectStart)
    {
        return create(player, null, effectTime, effectStart);
    }

    public AbstractEffect create(int effectTime, long effectStart)
    {
        return create(null, null, effectTime, effectStart);
    }

    public String getName() { return toString().toLowerCase(Locale.ENGLISH); }

    public interface IEffectFactory<T extends AbstractEffect>
    {
        T create(ServerPlayerEntity player, UUID sourcePlayer, EnumEffect effect, int effectTime, long effectStart);
    }
}