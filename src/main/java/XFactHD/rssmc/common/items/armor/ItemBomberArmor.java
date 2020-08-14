/*  Copyright (C) <2017>  <XFactHD>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see http://www.gnu.org/licenses. */

package XFactHD.rssmc.common.items.armor;

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.common.utils.Damage;
import XFactHD.rssmc.common.utils.Reference;
import XFactHD.rssmc.common.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.IForgeRegistry;

public class ItemBomberArmor extends ItemArmor implements ISpecialArmor
{
    private static final ArmorMaterial BOMBER_MATERIAL = EnumHelper.addArmorMaterial("bomber", "", 5000, new int[]{0, 0, 0, 0}, 0, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0);

    public ItemBomberArmor(IForgeRegistry<Item> registry)
    {
        super(BOMBER_MATERIAL, 0, EntityEquipmentSlot.CHEST);
        setRegistryName(new ResourceLocation(Reference.MOD_ID, "item_bomber_armor"));
        setUnlocalizedName(getRegistryName().toString());
        setCreativeTab(RainbowSixSiegeMC.CT.armorTab);
        registry.register(this);
    }

    @Override
    public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType, Entity entity)
    {
        boolean canEquip = entity instanceof EntityPlayer && !Utils.isWearingNonOpArmor(((EntityPlayer)entity).inventory.armorInventory);
        return super.isValidArmor(stack, armorType, entity) && (canEquip || entity instanceof EntityArmorStand);
    }

    @Override
    public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot)
    {
        return new CustomArmorProperties(source, damage);
    }

    @Override
    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {}

    @Override
    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot)
    {
        return 20;
    }

    public void explode(World world, EntityPlayer player, ItemStack stack)
    {
        if (getEquipmentSlot() == EntityEquipmentSlot.CHEST && !Utils.isWearingNonOpArmor(player.inventory.armorInventory))
        {
            float pitch = (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F;
            world.playSound(null, player.getPosition(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 4F, pitch);
            BlockPos pos = player.getPosition();
            for (EntityLivingBase entity : world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos.add(-5, 0, -5), pos.add(5, 2, 5))))
            {
                entity.attackEntityFrom(Damage.causeBomberDamage(player), 50000);
            }
            player.attackEntityFrom(Damage.causeBomberDamage(player), 50000);
        }
    }

    private static class CustomArmorProperties extends ArmorProperties
    {
        public CustomArmorProperties(DamageSource source, double damage)
        {
            super(0, 0, 20);
            AbsorbRatio = getRatio(source, damage);
        }

        private double getRatio(DamageSource source, double damage)
        {
            if (source instanceof Damage.DamageSourceBomber) { return 0; }
            if (damage > 30) { return 1 - (10 / damage); }
            return .75;
        }
    }
}