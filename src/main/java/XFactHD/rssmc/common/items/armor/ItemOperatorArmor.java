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
import XFactHD.rssmc.client.util.ArmorModelHandler;
import XFactHD.rssmc.common.Content;
import XFactHD.rssmc.common.data.EnumArmorLevel;
import XFactHD.rssmc.common.data.EnumOperator;
import XFactHD.rssmc.common.utils.Reference;
import XFactHD.rssmc.common.utils.Utils;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.IForgeRegistry;

import java.util.List;
import java.util.Locale;

public class ItemOperatorArmor extends ItemArmor implements ISpecialArmor
{
    private static final ArmorMaterial OP_ARMOR = EnumHelper.addArmorMaterial("op_armor", "", 5000, new int[]{0, 0, 0, 0}, 0, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0);

    public ItemOperatorArmor(IForgeRegistry<Item> registry)
    {
        super(OP_ARMOR, 0, EntityEquipmentSlot.CHEST);
        setRegistryName(new ResourceLocation(Reference.MOD_ID, "item_operator_armor"));
        setUnlocalizedName(getRegistryName().toString());
        setCreativeTab(RainbowSixSiegeMC.CT.armorTab);
        registry.register(this);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> subItems)
    {
        for (EnumOperator op : EnumOperator.values())
        {
            subItems.add(getArmorStack(op, false));
        }
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack)
    {
        EnumArmorLevel level = stack.hasTagCompound() ? EnumOperator.valueOf(stack.getTagCompound().getInteger("operator")).getArmorLevel() : null;
        if (level == EnumArmorLevel.MEDIUM)
        {
            player.motionX *= .95; //.9 ?
            player.motionZ *= .95; //.9 ?
        }
        else if (level == EnumArmorLevel.HEAVY)
        {
            player.motionX *= .9; //.8 ?
            player.motionZ *= .9; //.8 ?
        }
    }

    @Override
    public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType, Entity entity)
    {
        boolean canEquip = entity instanceof EntityPlayer && !Utils.isWearingNonOpArmor(((EntityPlayer)entity).inventory.armorInventory);
        return super.isValidArmor(stack, armorType, entity) && (canEquip || entity instanceof EntityArmorStand);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot)
    {
        if (!(player instanceof EntityPlayer)) { return null; }
        if (Utils.isWearingNonOpArmor(((EntityPlayer)player).inventory.armorInventory)) { return null; }
        EnumArmorLevel level = armor.hasTagCompound() ? EnumOperator.valueOf(armor.getTagCompound().getInteger("operator")).getArmorLevel() : null;
        boolean rook = armor.hasTagCompound() && armor.getTagCompound().getBoolean("rook");
        return new CustomArmorProperties(level, rook, source, damage);
    }

    @Override
    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) { /*This armor won't be damaged*/ }

    @Override
    @SuppressWarnings("ConstantConditions")
    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot)
    {
        if (!armor.hasTagCompound()) { return 0; }
        EnumOperator operator = EnumOperator.valueOf(armor.getTagCompound().getInteger("operator"));
        boolean rook = armor.getTagCompound().getBoolean("rook");
        int value = 0;
        switch (operator.getArmorLevel())
        {
            case LIGHT:  value = rook ? 10 :  5; break;
            case MEDIUM: value = rook ? 15 : 10; break;
            case HEAVY:  value = rook ? 20 : 15; break;
        }
        return value;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public String getItemStackDisplayName(ItemStack stack)
    {
        EnumOperator op = stack.hasTagCompound() ? EnumOperator.valueOf(stack.getTagCompound().getInteger("operator")) : null;
        return super.getItemStackDisplayName(stack) + " (" + (op != null ? op.getDisplayName() : "Unknown") + ")";
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
        EnumOperator op = stack.hasTagCompound() ? EnumOperator.valueOf(stack.getTagCompound().getInteger("operator")) : null;
        boolean rook = stack.hasTagCompound() && stack.getTagCompound().getBoolean("rook");
        tooltip.add(I18n.format("desc.rssmc:operator.name") + ": " + (op != null ? op.getDisplayName() : "Unknown"));
        tooltip.add(I18n.format("desc.rssmc:speed_rating.name") + ": " + (op != null ? op.getArmorLevel().getSpeedDisplayName() : "Unknown"));
        tooltip.add(I18n.format("desc.rssmc:armor_rating.name") + ": " + (op != null ? op.getArmorLevel().getDisplayName() : "Unknown"));
        tooltip.add(I18n.format("desc.rssmc:has_plate.name") + ": " + (rook ? ChatFormatting.GREEN + I18n.format("desc.rssmc:true.name") : ChatFormatting.RED + I18n.format("desc.rssmc:false.name")));
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public ModelBiped getArmorModel(EntityLivingBase entity, ItemStack stack, EntityEquipmentSlot slot, ModelBiped _default)
    {
        EnumOperator operator = stack.hasTagCompound() ? EnumOperator.valueOf(stack.getTagCompound().getInteger("operator")) : null;
        return ArmorModelHandler.getArmorModelForOperator(operator, slot, _default);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type)
    {
        EnumOperator op = stack.hasTagCompound() ? EnumOperator.valueOf(stack.getTagCompound().getInteger("operator")) : null;
        return "rssmc:textures/armor/armor_" + (op != null ? op.toString().toLowerCase(Locale.ENGLISH) : "unknown") + ".png";
    }

    @SuppressWarnings("ConstantConditions")
    public static ItemStack getArmorStack(EnumOperator operator, boolean rook)
    {
        ItemStack stack = new ItemStack(Content.itemOperatorArmor);
        stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setInteger("operator", operator.ordinal());
        stack.getTagCompound().setBoolean("rook", rook);
        return stack;
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean upgradeWithRookArmor(EntityPlayer player)
    {
        ItemStack stack = player.inventory.armorInventory[2];
        if (stack != null && stack.hasTagCompound() && !stack.getTagCompound().getBoolean("rook"))
        {
            stack.getTagCompound().setBoolean("rook", true);
            player.inventory.markDirty();
            return true;
        }
        return false;
    }

    private static class CustomArmorProperties extends ArmorProperties
    {
        public CustomArmorProperties(EnumArmorLevel level, boolean rook, DamageSource source, double damage)
        {
            super(0, 0, 20);
            AbsorbRatio = getRatio(level, rook, source, damage);
        }

        private double getRatio(EnumArmorLevel level, boolean rook, DamageSource source, double damage)
        {
            if (damage > 30) { return 0; }
            switch (level)
            {
                case LIGHT:  return rook ?  .2 :  0;
                case MEDIUM: return rook ? .28 : .1;
                case HEAVY:  return rook ? .36 : .2;
            }
            return 0;
        }
    }
}