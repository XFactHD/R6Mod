package xfacthd.r6mod.common.util.damage;

import net.minecraft.entity.player.PlayerEntity;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;

public class DamageSourceClaymore extends DamageSourceGadget
{
    public DamageSourceClaymore(PlayerEntity player)
    {
        super(player, "r6mod.claymore", EnumGadget.CLAYMORE);
        setExplosion();
        setDamageIsAbsolute();
    }
}