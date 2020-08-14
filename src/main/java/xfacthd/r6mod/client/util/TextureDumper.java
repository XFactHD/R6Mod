package xfacthd.r6mod.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.texture.AtlasTexture;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import xfacthd.r6mod.common.util.LogHelper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;

public class TextureDumper
{
    public static void dumpAtlasTexture(AtlasTexture atlas)
    {
        File outputFolder = new File("texture_dumps");
        if (!outputFolder.exists())
        {
            if (!outputFolder.mkdir())
            {
                LogHelper.error("Failed to create directory 'texture_dumps'!");
                return;
            }
        }

        RenderSystem.recordRenderCall(() ->
        {
            atlas.bindTexture();

            GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

            int width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
            int height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
            int size = width * height;

            BufferedImage bufferedimage = new BufferedImage(width, height, 2);
            String texPath = atlas.getTextureLocation().getPath();
            String fileName = texPath.substring(texPath.lastIndexOf('/'));
            if (!fileName.endsWith(".png")) { fileName += ".png"; }

            File output = new File(outputFolder, fileName);
            IntBuffer buffer = BufferUtils.createIntBuffer(size);
            int[] data = new int[size];

            GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, buffer);
            buffer.get(data);
            bufferedimage.setRGB(0, 0, width, height, data, 0, width);

            try
            {
                ImageIO.write(bufferedimage, "png", output);
                LogHelper.info("Exported png to: {}", output.getAbsolutePath());
            }
            catch (IOException e)
            {
                LogHelper.info("Unable to write: ", e);
            }
        });
    }
}