package xfacthd.r6mod.client.particles;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.common.data.particledata.ParticleDataEvilEyeLaser;

import javax.annotation.Nullable;

public class ParticleEvilEyeLaser extends Particle
{
    private static TextureAtlasSprite sprite;

    private final float yaw;
    private final float pitch;

    public ParticleEvilEyeLaser(World world, double posX, double posY, double posZ, float yaw, float pitch)
    {
        super(world, posX, posY, posZ, 0, 0, 0);
        this.yaw = yaw;
        this.pitch = pitch;
        setMaxAge(2);
    }

    @Override
    public void tick() { if (this.age++ >= this.maxAge) { this.setExpired(); } }

    @Override
    public void renderParticle(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks)
    {
        Vec3d view = renderInfo.getProjectedView();
        float x = (float)(posX - view.getX());
        float y = (float)(posY - view.getY());
        float z = (float)(posZ - view.getZ());

        float scale = 0.2F;

        Quaternion rotation = Vector3f.YP.rotationDegrees(90F - yaw);
        rotation.multiply(Vector3f.ZP.rotationDegrees(pitch));

        for (int i = 0; i < 5; i++)
        {
            drawSegment(buffer, new Quaternion(rotation), x, y, z, scale, i, partialTicks);
        }
    }

    private void drawSegment(IVertexBuilder buffer, Quaternion rotation, float x, float y, float z, float scale, float segment, float partialTicks)
    {
        Vector3f[] posVecs = new Vector3f[]
                {
                        new Vector3f(-1.0F - segment, -1.0F, 0.0F),
                        new Vector3f(-1.0F - segment,  1.0F, 0.0F),
                        new Vector3f( 1.0F - segment,  1.0F, 0.0F),
                        new Vector3f( 1.0F - segment, -1.0F, 0.0F)
                };
        for(int i = 0; i < 4; i++)
        {
            Vector3f posVec = posVecs[i];
            posVec.transform(rotation);
            posVec.mul(scale);
            posVec.add(x, y, z);
        }

        int brightness = this.getBrightnessForRender(partialTicks);

        buffer.pos(posVecs[0].getX(), posVecs[0].getY(), posVecs[0].getZ()).tex(sprite.getMaxU(), sprite.getMaxV()).color(1F, 1F, 1F, 1F).lightmap(brightness).endVertex();
        buffer.pos(posVecs[1].getX(), posVecs[1].getY(), posVecs[1].getZ()).tex(sprite.getMaxU(), sprite.getMinV()).color(1F, 1F, 1F, 1F).lightmap(brightness).endVertex();
        buffer.pos(posVecs[2].getX(), posVecs[2].getY(), posVecs[2].getZ()).tex(sprite.getMinU(), sprite.getMinV()).color(1F, 1F, 1F, 1F).lightmap(brightness).endVertex();
        buffer.pos(posVecs[3].getX(), posVecs[3].getY(), posVecs[3].getZ()).tex(sprite.getMinU(), sprite.getMaxV()).color(1F, 1F, 1F, 1F).lightmap(brightness).endVertex();

        rotation.multiply(Vector3f.XP.rotationDegrees(90F));
        posVecs = new Vector3f[]
                {
                        new Vector3f(-1.0F - segment, -1.0F, 0.0F),
                        new Vector3f(-1.0F - segment,  1.0F, 0.0F),
                        new Vector3f( 1.0F - segment,  1.0F, 0.0F),
                        new Vector3f( 1.0F - segment, -1.0F, 0.0F)
                };
        for(int i = 0; i < 4; i++)
        {
            Vector3f posVec = posVecs[i];
            posVec.transform(rotation);
            posVec.mul(scale);
            posVec.add(x, y, z);
        }

        buffer.pos(posVecs[0].getX(), posVecs[0].getY(), posVecs[0].getZ()).tex(sprite.getMaxU(), sprite.getMaxV()).color(1F, 1F, 1F, 1F).lightmap(brightness).endVertex();
        buffer.pos(posVecs[1].getX(), posVecs[1].getY(), posVecs[1].getZ()).tex(sprite.getMaxU(), sprite.getMinV()).color(1F, 1F, 1F, 1F).lightmap(brightness).endVertex();
        buffer.pos(posVecs[2].getX(), posVecs[2].getY(), posVecs[2].getZ()).tex(sprite.getMinU(), sprite.getMinV()).color(1F, 1F, 1F, 1F).lightmap(brightness).endVertex();
        buffer.pos(posVecs[3].getX(), posVecs[3].getY(), posVecs[3].getZ()).tex(sprite.getMinU(), sprite.getMaxV()).color(1F, 1F, 1F, 1F).lightmap(brightness).endVertex();
    }

    @Override
    public IParticleRenderType getRenderType() { return IParticleRenderType.PARTICLE_SHEET_LIT; }

    @Override
    protected int getBrightnessForRender(float partialTicks) { return 15728880; }

    public static void registerTexture(final TextureStitchEvent.Pre event) { event.addSprite(new ResourceLocation(R6Mod.MODID, "particle/evil_eye_laser")); }

    public static void loadTexture(AtlasTexture map) { sprite = map.getSprite(new ResourceLocation(R6Mod.MODID, "particle/evil_eye_laser")); }

    public static class Factory implements IParticleFactory<ParticleDataEvilEyeLaser>
    {
        @Nullable
        @Override
        public Particle makeParticle(ParticleDataEvilEyeLaser data, World world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            return new ParticleEvilEyeLaser(world, x, y, z, data.getYaw(), data.getPitch());
        }
    }
}