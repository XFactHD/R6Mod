package xfacthd.r6mod.common.data;

import net.minecraft.item.*;
import net.minecraft.util.NonNullList;
import xfacthd.r6mod.api.item.IGadgetItem;
import xfacthd.r6mod.common.R6Content;
import xfacthd.r6mod.common.data.blockdata.WallMaterial;
import xfacthd.r6mod.common.data.itemsubtypes.*;
import xfacthd.r6mod.common.items.building.BlockItemFloorPanel;
import xfacthd.r6mod.common.items.building.BlockItemWall;
import xfacthd.r6mod.common.items.gadgets.ItemGadgetAmmo;
import xfacthd.r6mod.common.items.gun.ItemAttachment;
import xfacthd.r6mod.common.items.gun.ItemGun;
import xfacthd.r6mod.common.items.gun.ItemMagazine;
import xfacthd.r6mod.common.items.material.ItemBullet;

import javax.annotation.Nonnull;
import java.util.Comparator;

public class ItemGroups
{
    public static final R6ItemGroup MATERIALS = new R6ItemGroup("r6mod.materials");
    public static final R6ItemGroup AMMO = new R6AmmoItemGroup("r6mod.ammo", (s1, s2) ->
    {
        if (s1.getItem() instanceof ItemBullet && s2.getItem() instanceof ItemMagazine) { return -1; }
        if (s2.getItem() instanceof ItemBullet && s1.getItem() instanceof ItemMagazine) { return 1; }
        if (s1.getItem() instanceof ItemBullet && s2.getItem() instanceof ItemGadgetAmmo) { return -1; }
        if (s2.getItem() instanceof ItemBullet && s1.getItem() instanceof ItemGadgetAmmo) { return 1; }
        if (s1.getItem() instanceof ItemMagazine && s2.getItem() instanceof ItemGadgetAmmo) { return -1; }
        if (s2.getItem() instanceof ItemMagazine && s1.getItem() instanceof ItemGadgetAmmo) { return 1; }
        if (s1.getItem() instanceof ItemBullet && s2.getItem() instanceof ItemBullet)
        {
            return ((ItemBullet)s1.getItem()).getBullet().compareTo(((ItemBullet)s2.getItem()).getBullet());
        }
        if (s1.getItem() instanceof ItemMagazine && s2.getItem() instanceof ItemMagazine)
        {
            return ((ItemMagazine)s1.getItem()).getMagazine().compareTo(((ItemMagazine)s2.getItem()).getMagazine());
        }
        if (s1.getItem() instanceof ItemGadgetAmmo && s2.getItem() instanceof ItemGadgetAmmo)
        {
            return ((ItemGadgetAmmo)s1.getItem()).getGadgetAmmo().compareTo(((ItemGadgetAmmo)s2.getItem()).getGadgetAmmo());
        }
        return 0;
    });
    public static final R6ItemGroup GUNS = new R6SortedItemGroup("r6mod.guns", (s1, s2) ->
    {
        if (s1.getItem() instanceof ItemGun && s2.getItem() instanceof ItemAttachment) { return -1; }
        if (s2.getItem() instanceof ItemGun && s1.getItem() instanceof ItemAttachment) { return 1; }
        if (s1.getItem() instanceof ItemGun && s2.getItem() instanceof ItemGun)
        {
            return ((ItemGun)s1.getItem()).getGun().compareTo(((ItemGun)s2.getItem()).getGun());
        }
        if (s1.getItem() instanceof ItemAttachment && s2.getItem() instanceof ItemAttachment)
        {
            return ((ItemAttachment)s1.getItem()).getAttachment().compareTo(((ItemAttachment)s2.getItem()).getAttachment());
        }
        return 0;
    });
    public static final R6ItemGroup GADGETS = new R6SortedItemGroup("r6mod.gadgets", (s1, s2) ->
    {
        EnumGadget g1 = null;
        EnumGadget g2 = null;

        if (s1.getItem() instanceof IGadgetItem) { g1 = ((IGadgetItem)s1.getItem()).getGadget(); }
        if (s2.getItem() instanceof IGadgetItem) { g2 = ((IGadgetItem)s2.getItem()).getGadget(); }

        return g1 != null && g2 != null ? g1.compareTo(g2) : 0;
    });
    public static final R6ItemGroup BUILDING = new R6SortedItemGroup("r6mod.building", (s1, s2) ->
    {
        if (s1.getItem() instanceof BlockItemWall && s2.getItem() instanceof BlockItemWall)
        {
            BlockItemWall i1 = ((BlockItemWall)s1.getItem());
            BlockItemWall i2 = ((BlockItemWall)s2.getItem());

            if (i1.getMaterial() == i2.getMaterial())
            {
                return Boolean.compare(i1.isBarred(), i2.isBarred());
            }
            return i1.getMaterial().compareTo(i2.getMaterial());
        }
        else if (s1.getItem() instanceof BlockItemFloorPanel && s2.getItem() instanceof BlockItemFloorPanel)
        {
            BlockItemFloorPanel i1 = ((BlockItemFloorPanel)s1.getItem());
            BlockItemFloorPanel i2 = ((BlockItemFloorPanel)s2.getItem());

            return i1.getMaterial().compareTo(i2.getMaterial());
        }
        return 0;
    });
    public static final R6ItemGroup MISC = new R6ItemGroup("r6mod.misc");

    public static void finalizeItemGroups()
    {
        MATERIALS.setIconItem(R6Content.itemMaterials.get(EnumMaterial.INGOT_BRASS));
        AMMO.setIconItem(R6Content.itemMagazines.get(EnumMagazine.MAG_R4_C));
        GUNS.setIconItem(R6Content.itemGuns.get(EnumGun.MP7));
        GADGETS.setIconItem(R6Content.blockBreachCharge.asItem());
        BUILDING.setIconItem(R6Content.blockWalls.get(WallMaterial.OAK).asItem());
        MISC.setIconItem(R6Content.itemCrowbar);
    }

    private static class R6AmmoItemGroup extends R6SortedItemGroup
    {
        public R6AmmoItemGroup(String name, @Nonnull Comparator<ItemStack> comp) { super(name, comp); }

        @Override
        public void fill(NonNullList<ItemStack> items)
        {
            super.fill(items);
            for (ItemStack stack : items)
            {
                if (stack.getItem() instanceof ItemMagazine)
                {
                    stack.setDamage(((ItemMagazine)stack.getItem()).getMagazine().getMagCap());
                }
            }
        }
    }

    private static class R6SortedItemGroup extends R6ItemGroup
    {
        protected final Comparator<ItemStack> comp;

        public R6SortedItemGroup(String name, @Nonnull Comparator<ItemStack> comp)
        {
            super(name);
            this.comp = comp;
        }

        @Override
        public void fill(NonNullList<ItemStack> items)
        {
            super.fill(items);
            items.sort(comp);
        }
    }

    private static class R6ItemGroup extends ItemGroup
    {
        private Item iconItem = null;

        public R6ItemGroup(String name) { super(name); }

        public void setIconItem(Item iconItem) { this.iconItem = iconItem; }

        @Override
        public ItemStack createIcon() { return new ItemStack(iconItem); }
    }
}