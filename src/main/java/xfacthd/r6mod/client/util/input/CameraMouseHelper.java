package xfacthd.r6mod.client.util.input;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import xfacthd.r6mod.api.entity.ICameraEntity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CameraMouseHelper
{
    private final Method MouseHelper_cursorPosCallback;

    private boolean isInCam = false;
    private double mouseX = 0;
    private double mouseY = 0;
    private double diffX = 0; //Called velocityX in MouseHelper
    private double diffY = 0; //Called velocityY in MouseHelper

    public CameraMouseHelper(Method cursorCallback) { this.MouseHelper_cursorPosCallback = cursorCallback; }

    //INFO: camera rotation stutter is unfixable because the server constantly resets the client rotation
    public void cursorPosCallback(long handle, double xPos, double yPos)
    {
        if (handle == Minecraft.getInstance().getMainWindow().getHandle())
        {
            Entity entity = mc().getRenderViewEntity();
            if (entity instanceof ICameraEntity && !inGui())
            {
                if (!isInCam)
                {
                    mouseX = xPos;
                    mouseY = yPos;
                    isInCam = true;
                }

                double diffX = xPos - mouseX;
                double diffY = yPos - mouseY;

                mouseX = xPos;
                mouseY = yPos;

                if (mc().mouseHelper.isMouseGrabbed() && mc().isGameFocused())
                {
                    //Magic numbers from MouseHelper
                    double sense = mc().gameSettings.mouseSensitivity * .6D + .2D;
                    double senseMult = sense * sense * sense * 8D;

                    diffX *= senseMult;
                    diffY *= senseMult;

                    if (mc().gameSettings.invertMouse) { diffY *= -1D; }

                    this.diffX += diffX;
                    this.diffY += diffY;
                }
            }
            else
            {
                if (isInCam)
                {
                    mc().mouseHelper.setIgnoreFirstMove();
                    isInCam = false;
                }

                try { MouseHelper_cursorPosCallback.invoke(mc().mouseHelper, handle, xPos, yPos); }
                catch (IllegalAccessException | InvocationTargetException e) { throw new RuntimeException(e); }
            }
        }
    }

    public double getDiffMouseX()
    {
        double diff = diffX;
        diffX = 0;
        return diff;
    }

    public double getDiffMouseY()
    {
        double diff = diffY;
        diffY = 0;
        return diff;
    }

    private static boolean inGui()
    {
        return mc().currentScreen != null || mc().isGamePaused();
    }

    private static Minecraft mc() { return Minecraft.getInstance(); }
}