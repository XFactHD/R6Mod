package xfacthd.r6mod.common.items.gadgets;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.common.capability.CapabilityEffect;
import xfacthd.r6mod.common.data.ItemGroups;
import xfacthd.r6mod.common.data.effects.EnumEffect;
import xfacthd.r6mod.common.event.EffectEventHandler;

public class ItemStimPistol extends Item
{
    public static final int MIN_DURATION = 20;

    public ItemStimPistol()
    {
        super(new Properties().maxStackSize(1).group(ItemGroups.GADGETS));
        setRegistryName(R6Mod.MODID, "item_stim_pistol");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) //TODO: implement properly
    {
        if (!world.isRemote())
        {
            float overheal = Math.max(CapabilityEffect.STIM_POOL_MAX - (player.getMaxHealth() - player.getHealth()), 0);
            int time = (int)Math.max(MIN_DURATION, overheal * (float)CapabilityEffect.STIM_DEPLETE_INTERVAL);
            EffectEventHandler.addEffect((ServerPlayerEntity)player, EnumEffect.OVERHEAL, time);
        }
        return super.onItemRightClick(world, player, hand);
    }
}