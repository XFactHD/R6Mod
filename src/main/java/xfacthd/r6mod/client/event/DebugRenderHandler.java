package xfacthd.r6mod.client.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.client.util.*;
import xfacthd.r6mod.client.util.input.KeyBindings;
import xfacthd.r6mod.client.util.render.WorldRenderHelper;

import java.util.*;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = R6Mod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DebugRenderHandler
{
    private static final List<Tuple<Vec3d, Vec3d>> shotTracers = new ArrayList<>();
    private static final Map<UUID, Queue<Vec3d>> grenadeTracers = new HashMap<>();

    @SubscribeEvent
    public static void onRenderWorldLast(final RenderWorldLastEvent event)
    {
        if (!mc().gameSettings.showDebugInfo) { return; }

        if (!shotTracers.isEmpty()) { renderShotTracers(event.getMatrixStack()); }
        if (!grenadeTracers.isEmpty())
        {
            grenadeTracers.forEach(((uuid, bounces) -> renderGrenadeTraces(event.getMatrixStack(), bounces)));
        }
    }

    @SubscribeEvent
    public static void onClientTick(final TickEvent.ClientTickEvent event)
    {
        if (event.phase != TickEvent.Phase.START) { return; }

        if (shotTracers.isEmpty() && grenadeTracers.isEmpty()) { return; }

        if (KeyBindings.KEY_CLEAR_DEBUG.isPressed()) { clearDebugData(); }
    }

    private static void renderShotTracers(MatrixStack mstack)
    {
        if (ClientConfig.INSTANCE.debugShowAllGunTraces)
        {
            for (Tuple<Vec3d, Vec3d> tracer : shotTracers)
            {
                renderShotTracer(mstack, tracer);
            }
        }
        else
        {
            renderShotTracer(mstack, shotTracers.get(shotTracers.size() - 1));
        }
    }

    private static void renderShotTracer(MatrixStack mstack, Tuple<Vec3d, Vec3d> tracer)
    {
        Vec3d a = tracer.getA();
        Vec3d b = tracer.getB();

        mstack.push();

        Vec3d playerPos = mc().gameRenderer.getActiveRenderInfo().getProjectedView();
        mstack.translate(-playerPos.x, -playerPos.y, -playerPos.z);

        IRenderTypeBuffer.Impl buffer = mc().getRenderTypeBuffers().getBufferSource();
        IVertexBuilder builder = buffer.getBuffer(RenderType.LINES);
        Matrix4f matrix = mstack.getLast().getMatrix();

        WorldRenderHelper.drawLine(builder, matrix, a, b, 255, 0, 0, 255); //Draw bullet trace
        if (ClientConfig.INSTANCE.debugShowGunSearchBox)
        {
            WorldRenderer.drawBoundingBox(mstack, builder, new AxisAlignedBB(a, b), 0F, 1F, 0F, 1F); //Draw search area
        }
        buffer.finish(RenderType.LINES);

        mstack.pop();
    }

    private static void renderGrenadeTraces(MatrixStack mstack, Queue<Vec3d> bounces)
    {
        mstack.push();

        Vec3d playerPos = mc().gameRenderer.getActiveRenderInfo().getProjectedView();
        mstack.translate(-playerPos.x, -playerPos.y, -playerPos.z);

        IRenderTypeBuffer.Impl buffer = mc().getRenderTypeBuffers().getBufferSource();
        IVertexBuilder builder = buffer.getBuffer(RenderType.LINES);
        Matrix4f matrix = mstack.getLast().getMatrix();

        Vec3d lastBounce = null;
        for (Vec3d bounce : bounces)
        {
            if (lastBounce != null)
            {
                WorldRenderHelper.drawLine(builder, matrix, lastBounce, bounce, 255, 0, 0, 255);
            }
            lastBounce = bounce;
        }

        buffer.finish(RenderType.LINES);

        mstack.pop();
    }

    public static void addShotTracer(Vec3d start, Vec3d end) { shotTracers.add(new Tuple<>(start, end)); }

    public static void addGrenadeBounce(UUID entity, Vec3d pos)
    {
        if (!grenadeTracers.containsKey(entity)) { grenadeTracers.put(entity, new ArrayDeque<>()); }
        grenadeTracers.get(entity).add(pos);
    }

    public static void clearDebugData()
    {
        shotTracers.clear();
        grenadeTracers.clear();
    }

    private static Minecraft mc() { return Minecraft.getInstance(); }
}