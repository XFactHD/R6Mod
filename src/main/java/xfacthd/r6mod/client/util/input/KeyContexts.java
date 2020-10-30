package xfacthd.r6mod.client.util.input;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import xfacthd.r6mod.api.entity.ICameraEntity;
import xfacthd.r6mod.common.capability.CapabilityDBNO;
import xfacthd.r6mod.common.items.gun.ItemGun;

public enum KeyContexts implements IKeyConflictContext
{
    IN_CAMERA
    {
        @Override
        public boolean isActive() { return isInGame() && mc().renderViewEntity instanceof ICameraEntity<?>; }
    },

    NOT_IN_CAMERA
    {
        @Override
        public boolean isActive() { return isInGame() && !IN_CAMERA.isActive(); }
    },

    DBNO
    {
        @Override
        public boolean isActive() { return player() != null && CapabilityDBNO.getFrom(player()).isDBNO(); }
    },

    ALIVE_WITH_GUN
    {
        @Override
        public boolean isActive()
        {
            if (!isInGame() || SPECTATOR.isActive() || IN_CAMERA.isActive() || DBNO.isActive()) { return false; }

            ItemStack stack = player().getHeldItemMainhand();
            return !stack.isEmpty() && stack.getItem() instanceof ItemGun;
        }
    },

    SPECTATOR
    {
        @Override
        public boolean isActive() { return player().isSpectator(); }
    };

    @Override
    public boolean conflicts(IKeyConflictContext other) { return this == other; }

    private static Minecraft mc() { return Minecraft.getInstance(); }

    private static PlayerEntity player() { return mc().player; }

    private static boolean isInGame() { return player() != null && KeyConflictContext.IN_GAME.isActive(); }
}