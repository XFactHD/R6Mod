package xfacthd.r6mod.common.util.damage;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.EntityDamageSource;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;

public class DamageSourceGadget extends EntityDamageSource
{
    private final EnumGadget gadget;

    public DamageSourceGadget(PlayerEntity player, String damageType, EnumGadget gadget)
    {
        super(damageType, player);
        this.gadget = gadget;
    }

    public EnumGadget getGadget() { return gadget; }
}