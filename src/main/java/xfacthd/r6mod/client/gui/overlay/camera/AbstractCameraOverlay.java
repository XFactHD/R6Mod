package xfacthd.r6mod.client.gui.overlay.camera;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import xfacthd.r6mod.api.entity.ICameraEntity;
import xfacthd.r6mod.api.client.ICameraOverlay;

public abstract class AbstractCameraOverlay<T extends ICameraEntity> implements ICameraOverlay<T>
{
    protected final Minecraft mc() { return Minecraft.getInstance(); }

    protected final World world() { return Minecraft.getInstance().world; }

    protected final PlayerEntity player() { return Minecraft.getInstance().player; }

    protected final MainWindow window() { return Minecraft.getInstance().getMainWindow(); }

    protected final FontRenderer font() { return Minecraft.getInstance().fontRenderer; }
}