package xfacthd.r6mod.common.util.damage;

import net.minecraft.entity.player.PlayerEntity;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;

public class DamageSourceEvilEye extends DamageSourceGadget
{
    public DamageSourceEvilEye(PlayerEntity shooter) { super(shooter, "r6mod.laser", EnumGadget.EVIL_EYE); }
}