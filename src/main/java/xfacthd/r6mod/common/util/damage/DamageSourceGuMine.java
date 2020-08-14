package xfacthd.r6mod.common.util.damage;

import net.minecraft.entity.player.PlayerEntity;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;

public class DamageSourceGuMine extends DamageSourceGadget
{
    public DamageSourceGuMine(PlayerEntity thrower)
    {
        super(thrower, "r6mod.gu_mine", EnumGadget.GU_MINE);
        setDamageIsAbsolute();
    }
}