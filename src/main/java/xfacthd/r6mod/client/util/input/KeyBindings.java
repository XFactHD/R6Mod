package xfacthd.r6mod.client.util.input;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;
import xfacthd.r6mod.R6Mod;

public class KeyBindings
{
    private static final String KEY_CATEGORY = R6Mod.MODID + ".keycat";

    public static final KeyBinding KEY_RELOAD =      create("reload",      GLFW.GLFW_KEY_R);
    public static final KeyBinding KEY_PRIOR_CAM =   create("prior_cam",   GLFW.GLFW_KEY_Y);
    public static final KeyBinding KEY_NEXT_CAM =    create("next_cam",    GLFW.GLFW_KEY_C);
    public static final KeyBinding KEY_EXIT_CAM =    create("exit_cam",    GLFW.GLFW_KEY_P);
    public static final KeyBinding KEY_MARK_CAM =    create("mark_cam",    GLFW.GLFW_KEY_X);
    public static final KeyBinding KEY_PING =        create("ping",        GLFW.GLFW_KEY_Y);
    public static final KeyBinding KEY_HOLD_WOUND =  create("hold_wound",  GLFW.GLFW_KEY_H);
    public static final KeyBinding KEY_CLEAR_DEBUG = create("clear_debug", GLFW.GLFW_KEY_K);

    public static void registerKeyBinds()
    {
        ClientRegistry.registerKeyBinding(KEY_RELOAD);
        ClientRegistry.registerKeyBinding(KEY_PRIOR_CAM);
        ClientRegistry.registerKeyBinding(KEY_NEXT_CAM);
        ClientRegistry.registerKeyBinding(KEY_EXIT_CAM);
        ClientRegistry.registerKeyBinding(KEY_MARK_CAM);
        ClientRegistry.registerKeyBinding(KEY_PING);
        ClientRegistry.registerKeyBinding(KEY_HOLD_WOUND);
        ClientRegistry.registerKeyBinding(KEY_CLEAR_DEBUG);
    }

    private static KeyBinding create(String name, int keyCode) { return create(name, keyCode, KEY_CATEGORY); }

    @SuppressWarnings("SameParameterValue")
    private static KeyBinding create(String name, int keyCode, String category)
    {
        return new KeyBinding(R6Mod.MODID + "." + name, keyCode, category);
    }
}