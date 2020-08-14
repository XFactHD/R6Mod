package xfacthd.r6mod.common.util.damage;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.EntityDamageSource;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGun;

public class DamageSourceGun extends EntityDamageSource
{
    private final boolean headshot;
    private final EnumGun gun;

    public DamageSourceGun(PlayerEntity shooter, boolean headshot, EnumGun gun)
    {
        super("gun", shooter);
        this.headshot = headshot;
        this.gun = gun;

        setDamageIsAbsolute();
    }

    public boolean isHeadshot() { return headshot; }

    public EnumGun getGun() { return gun; }
}