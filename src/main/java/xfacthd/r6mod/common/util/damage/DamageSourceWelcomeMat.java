package xfacthd.r6mod.common.util.damage;

import net.minecraft.entity.player.PlayerEntity;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;

public class DamageSourceWelcomeMat extends DamageSourceGadget
{
    public DamageSourceWelcomeMat(PlayerEntity player)
    {
        super(player, "r6mod.welcome_mat", EnumGadget.WELCOME_MAT);
        setDamageIsAbsolute();
    }
}