package xfacthd.r6mod.common.data.effects;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import xfacthd.r6mod.common.R6Content;

public class EffectCandelaFlash extends AbstractEffect
{
    public EffectCandelaFlash(ServerPlayerEntity player, int time, long start)
    {
        super(player, EnumEffect.CANDELA_FLASH, time, start);
    }

    @Override
    protected void handleEffect(int runTime)
    {
        ItemStack helmet = player.inventory.armorItemInSlot(3); //TODO: activate when glance is implemented
        if (helmet.getItem() == R6Content.itemYingGlasses/* || helmet.getItem() == R6Content.itemGlanceSmartGlasses*/)
        {
            //noinspection ConstantConditions
            if (helmet.getTag().getBoolean("active"))
            {
                invalidate();
            }
        }
    }

    @Override
    public void drawEffect()
    {
        //TODO: draw flash overlay
    }

    @Override
    protected boolean isPositive() { return false; }

    @Override
    public boolean showIcon() { return false; }
}