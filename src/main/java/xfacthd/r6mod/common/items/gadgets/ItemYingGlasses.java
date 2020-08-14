package xfacthd.r6mod.common.items.gadgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.client.util.render.TextureDrawer;
import xfacthd.r6mod.common.R6Content;
import xfacthd.r6mod.common.data.ItemGroups;

public class ItemYingGlasses extends ArmorItem
{
    private static final String TEXTURE = R6Mod.MODID + ":textures/armor/item_ying_glasses.png";
    private static final ResourceLocation TINT = new ResourceLocation(R6Mod.MODID, "textures/gui/overlay/black_eye_filter.png");

    public ItemYingGlasses()
    {
        super(new Material(), EquipmentSlotType.HEAD, new Properties().group(ItemGroups.GADGETS));
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
    public void renderHelmetOverlay(ItemStack stack, PlayerEntity player, int width, int height, float partialTicks)
    {
        //noinspection ConstantConditions
        if (stack.hasTag() && stack.getTag().getBoolean("active"))
        {
            Minecraft.getInstance().getTextureManager().bindTexture(TINT);
            TextureDrawer.drawTexture(new MatrixStack(), 0, 0, width, height, 0, 1, 0, 1);
        }
    }

    public static boolean isActive(PlayerEntity player)
    {
        ItemStack helmet = player.inventory.armorItemInSlot(3);
        if (helmet.getItem() == R6Content.itemYingGlasses)
        {
            //noinspection ConstantConditions
            return helmet.getTag().getBoolean("active");
        }
        return false;
    }

    public static void switchGlasses(PlayerEntity player, boolean active)
    {
        ItemStack helmet = player.inventory.armorItemInSlot(3);
        if (helmet.getItem() == R6Content.itemYingGlasses)
        {
            //noinspection ConstantConditions
            helmet.getTag().putBoolean("active", active);
            player.inventory.markDirty();
        }
    }

    private static class Material implements IArmorMaterial
    {
        @Override
        public float getKnockbackResistance() { return 0; }

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