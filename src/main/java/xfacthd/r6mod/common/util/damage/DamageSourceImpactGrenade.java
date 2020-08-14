package xfacthd.r6mod.common.util.damage;

import net.minecraft.entity.player.PlayerEntity;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;

public class DamageSourceImpactGrenade extends DamageSourceGadget
{
    public DamageSourceImpactGrenade(PlayerEntity player)
    {
        super(player, "r6mod.impact_grenade", EnumGadget.IMPACT_GRENADE);
        setExplosion();
        setDamageIsAbsolute();
    }
}