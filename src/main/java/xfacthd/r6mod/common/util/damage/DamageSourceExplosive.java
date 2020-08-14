package xfacthd.r6mod.common.util.damage;

import net.minecraft.entity.player.PlayerEntity;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;

public class DamageSourceExplosive extends DamageSourceGadget
{
    public DamageSourceExplosive(PlayerEntity player, EnumGadget source)
    {
        super(player, "r6mod.explosive_" + source.getObjectName(), source);
        setExplosion();
        setDamageIsAbsolute();
    }
}