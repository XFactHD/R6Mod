package xfacthd.r6mod.common.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xfacthd.r6mod.common.data.types.ContainerTypes;
import xfacthd.r6mod.common.tileentities.building.TileEntityCamera;

public class ContainerCamera extends Container
{
    private final PlayerEntity player;
    private TileEntityCamera cam;

    public ContainerCamera(int id, World world, BlockPos pos, PlayerEntity player)
    {
        super(ContainerTypes.containerTypeCamera, id);

        this.player = player;
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityCamera) { cam = (TileEntityCamera)te; }
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) { return player.isCreative(); }

    public void onResult(String team) { if (cam != null) { cam.addCameraWithTeam(player.getUniqueID(), team); } }
}