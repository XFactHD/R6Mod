package xfacthd.r6mod.client.particles;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ReuseableStream;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.vector.*;
import net.minecraftforge.client.event.TextureStitchEvent;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.common.data.particledata.ParticleDataYokaiBlast;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class ParticleYokaiBlast extends Particle
{
    private static TextureAtlasSprite sprite;

    private final float yaw;
    private final float pitch;

    public ParticleYokaiBlast(ClientWorld world, double posX, double posY, double posZ, float yaw, float pitch)
    {
        super(world, posX, posY, posZ);

        this.yaw = yaw;
        this.pitch = pitch;

        this.motionX = -MathHelper.sin(yaw * ((float)Math.PI / 180F)) * MathHelper.cos(pitch * ((float)Math.PI / 180F));
        this.motionY = -MathHelper.sin(pitch * ((float)Math.PI / 180F));
        this.motionZ =  MathHelper.cos(yaw * ((float)Math.PI / 180F)) * MathHelper.cos(pitch * ((float)Math.PI / 180F));

        setMaxAge(40);
    }

    @Override
    public void tick()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.age++ >= this.maxAge) { this.setExpired(); }
        else
        {
            if (motionX != 0.0D || motionY != 0.0D || motionZ != 0.0D)
            {
                Vector3d motion = new Vector3d(motionX, motionY, motionZ);
                Vector3d actual = Entity.collideBoundingBoxHeuristically(null, motion, getBoundingBox(), world, ISelectionContext.dummy(), new ReuseableStream<>(Stream.empty()));
                if (actual.getX() != 0.0D || actual.getY() != 0.0D || actual.getZ() != 0.0D)
                {
                    setBoundingBox(getBoundingBox().offset(actual.getX(), actual.getY(), actual.getZ()));
                    resetPositionToBB();
                }

                if (actual.getX() != motionX || actual.getY() != motionY || actual.getZ() != motionZ) { setExpired(); }
            }
            else { setExpired(); }
        }
    }

    @Override
    public void renderParticle(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks)
    {
        Vector3d view = renderInfo.getProjectedView();
        float x = (float)(posX - view.getX());
        float y = (float)(posY - view.getY());
        float z = (float)(posZ - view.getZ());

        float scale = 0.5F + ((float)age / (float)maxAge);

        Quaternion rotation = Vector3f.YP.rotationDegrees(-yaw);
        rotation.multiply(Vector3f.XP.rotationDegrees(pitch + 180));

        Vector3f[] posVecs = new Vector3f[] { new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F,  1.0F, 0.0F), new Vector3f( 1.0F,  1.0F, 0.0F), new Vector3f( 1.0F, -1.0F, 0.0F) };
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

        buffer.pos(posVecs[0].getX(), posVecs[0].getY(), posVecs[0].getZ()).tex(sprite.getMinU(), sprite.getMaxV()).color(1F, 1F, 1F, 1F).lightmap(brightness).endVertex();
        buffer.pos(posVecs[1].getX(), posVecs[1].getY(), posVecs[1].getZ()).tex(sprite.getMinU(), sprite.getMinV()).color(1F, 1F, 1F, 1F).lightmap(brightness).endVertex();
        buffer.pos(posVecs[2].getX(), posVecs[2].getY(), posVecs[2].getZ()).tex(sprite.getMaxU(), sprite.getMinV()).color(1F, 1F, 1F, 1F).lightmap(brightness).endVertex();
        buffer.pos(posVecs[3].getX(), posVecs[3].getY(), posVecs[3].getZ()).tex(sprite.getMaxU(), sprite.getMaxV()).color(1F, 1F, 1F, 1F).lightmap(brightness).endVertex();
    }

    @Override
    public IParticleRenderType getRenderType() { return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT; }

    public static void registerTexture(final TextureStitchEvent.Pre event) { event.addSprite(new ResourceLocation(R6Mod.MODID, "particle/yokai_blast")); }

    public static void loadTexture(AtlasTexture map) { sprite = map.getSprite(new ResourceLocation(R6Mod.MODID, "particle/yokai_blast")); }

    public static class Factory implements IParticleFactory<ParticleDataYokaiBlast>
    {
        @Nullable
        @Override
        public Particle makeParticle(ParticleDataYokaiBlast data, ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            return new ParticleYokaiBlast(world, x, y, z, data.getYaw(), data.getPitch());
        }
    }
}