package xfacthd.r6mod.common.items.gadgets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xfacthd.r6mod.R6Mod;

public class ItemYingGlasses extends ArmorItem
{
    private static final String TEXTURE = R6Mod.MODID + ":textures/armor/item_ying_glasses.png";

    public ItemYingGlasses()
    {
        super(new Material(), EquipmentSlotType.HEAD, new Properties());
        setRegistryName(R6Mod.MODID, "item_ying_glasses");
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
    {
        if (!stack.hasTag())
        {
            stack.setTag(new CompoundNBT());
            //noinspection ConstantConditions
            stack.getTag().putBoolean("active", false);
        }
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) { return TEXTURE; }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderHelmetOverlay(ItemStack stack, PlayerEntity player, int width, int height, float partialTicks)
    {
        //TODO: render tinted overlay
    }

    private static class Material implements IArmorMaterial
    {
        @Override
        public int getDurability(EquipmentSlotType slot) { return 0; }

        @Override
        public int getDamageReductionAmount(EquipmentSlotType slot) { return 0; }

        @Override
        public int getEnchantability() { return 0; }

        @Override
        public SoundEvent getSoundEvent() { return null; }

        @Override
        public Ingredient getRepairMaterial() { return null; }

        @Override
        public String getName() { return R6Mod.MODID + ".material_ying_glasses"; }

        @Override
        public float getToughness() { return 0; }
    }
}