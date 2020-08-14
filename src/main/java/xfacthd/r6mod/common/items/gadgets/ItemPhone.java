package xfacthd.r6mod.common.items.gadgets;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.common.data.ItemGroups;
import xfacthd.r6mod.common.util.data.CameraManager;
import xfacthd.r6mod.common.util.data.R6WorldSavedData;

public class ItemPhone extends Item
{
    public ItemPhone()
    {
        super(new Properties().group(ItemGroups.GADGETS).maxStackSize(1));
        setRegistryName(R6Mod.MODID, "item_phone");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
    {
        if (!world.isRemote)
        {
            ServerWorld sWorld = (ServerWorld)world;
            ServerPlayerEntity sPlayer = (ServerPlayerEntity)player;

            CameraManager camMgr = R6WorldSavedData.get(sWorld).getCameraManager();
            if (camMgr.hasCameras(sPlayer) && !camMgr.isUsingCamera(sPlayer))
            {
                camMgr.enterCamera((ServerPlayerEntity) player);
                return ActionResult.resultSuccess(player.getHeldItem(hand));
            }
            return ActionResult.resultFail(player.getHeldItem(hand));
        }
        return super.onItemRightClick(world, player, hand);
    }
}