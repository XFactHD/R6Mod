package xfacthd.r6mod.client.util.data;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import xfacthd.r6mod.api.entity.ICameraEntity;
import xfacthd.r6mod.client.event.CameraEventHandler;
import xfacthd.r6mod.common.data.EnumCamera;

import java.util.*;

public class ClientCameraManager
{
    private final List<ICameraEntity> cameras = new ArrayList<>();
    private final List<EnumCamera> cats = new ArrayList<>();
    private int lastActiveId = -1;
    private int activeIndex = -1;
    private int categorySize = 0;
    private ICameraEntity activeCamera;

    public void handleCameraDataPacket(List<Integer> camIds)
    {
        World world = Minecraft.getInstance().world;
        if (world == null) { return; }

        cameras.clear();
        for (int id : camIds)
        {
            Entity entity = world.getEntityByID(id);
            if (entity instanceof ICameraEntity) { cameras.add((ICameraEntity) entity); }
        }

        cameras.sort((cam1, cam2) ->
        {
            if (cam1.getCameraType() == cam2.getCameraType())
            {
                return Integer.compare(cam1.getCameraEntity().getEntityId(), cam2.getCameraEntity().getEntityId());
            }
            else
            {
                return cam1.getCameraType().compareTo(cam2.getCameraType());
            }
        });

        activeIndex = recalculateActiveIndex();
        categorySize = recalculateCategorySize(activeCamera != null ? activeCamera.getCameraType() : null);
        recalculateCategories();
    }

    public void handleCameraIndexPacket(int activeId)
    {
        World world = Minecraft.getInstance().world;
        if (world == null) { return; }

        gatherActiveCamera(world, activeId);
        activeIndex = recalculateActiveIndex();
        categorySize = recalculateCategorySize(activeCamera != null ? activeCamera.getCameraType() : null);
        recalculateCategories();
    }

    public List<EnumCamera> getCategories() { return cats; }

    public int getActiveCategorySize() { return categorySize; }

    public ICameraEntity getActiveCamera() { return activeCamera; }

    public int getActiveIndex() { return activeIndex; }

    private void recalculateCategories()
    {
        cats.clear();
        for (ICameraEntity cam : cameras)
        {
            if (!cats.contains(cam.getCameraType())) { cats.add(cam.getCameraType()); }
        }
        cats.sort(EnumCamera::compareTo);
    }

    private int recalculateCategorySize(EnumCamera cat)
    {
        if (cat == null) { return 0; }

        int count = 0;
        for (ICameraEntity cam : cameras)
        {
            if (cam.getCameraType() == cat) { count++; }
        }
        return count;
    }

    private int recalculateActiveIndex()
    {
        if (activeCamera == null) { return -1; }

        EnumCamera type = activeCamera.getCameraType();

        int idx = 0;
        for (ICameraEntity cam : cameras)
        {
            if (cam.getCameraType() == type)
            {
                if (cam == activeCamera) { return idx; }
                idx++;
            }
        }

        return -1;
    }

    private void gatherActiveCamera(World world, int activeId)
    {
        Entity entity = activeId != -1 ? world.getEntityByID(activeId) : null;
        if (entity instanceof ICameraEntity)
        {
            activeCamera = (ICameraEntity)entity;
            Minecraft.getInstance().setRenderViewEntity(activeCamera.getCameraEntity());

            //Not in a camera yet
            if (lastActiveId == -1) { CameraEventHandler.onEnterCamera(); }
            else { CameraEventHandler.onSwitchCamera(); }
        }
        else if (activeId == -1)
        {
            activeCamera = null;

            if (Minecraft.getInstance().getRenderViewEntity() instanceof ICameraEntity)
            {
                //noinspection ConstantConditions
                Minecraft.getInstance().setRenderViewEntity(Minecraft.getInstance().player);
                CameraEventHandler.onLeaveCamera();
            }
        }

        lastActiveId = activeId;
    }

    public void onPlayerDisconnect()
    {
        //noinspection ConstantConditions
        Minecraft.getInstance().setRenderViewEntity(Minecraft.getInstance().player);

        cameras.clear();
        cats.clear();
    }
}