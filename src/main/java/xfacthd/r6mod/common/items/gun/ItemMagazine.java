package xfacthd.r6mod.common.items.gun;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.common.data.ItemGroups;
import xfacthd.r6mod.common.data.itemsubtypes.EnumMagazine;

import javax.annotation.Nullable;
import java.util.List;

public class ItemMagazine extends Item
{
    private final EnumMagazine mag;

    public ItemMagazine(EnumMagazine mag)
    {
        super(new Item.Properties()
                .group(ItemGroups.AMMO)
                .maxDamage(mag.getMagCap())
                .setNoRepair());
        this.mag = mag;

        setRegistryName(R6Mod.MODID, mag.toItemName());
    }

    @Override //Makes sure we can sneak click the mag filler with this item
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player)
    {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        return 1.0 - ((double) stack.getDamage() / (double) stack.getMaxDamage());
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
        tooltip.add(new TranslationTextComponent("info.r6mod.mag_info").appendString(" " + getDamage(stack) + "/" + getMaxDamage(stack)));
        String caliberName = mag.getBullet() == null ? "Unknown" : mag.getBullet().toCaliberName();
        tooltip.add(new TranslationTextComponent("info.r6mod.caliber").appendString(" " + caliberName));
    }

    public EnumMagazine getMagazine() { return mag; }
}