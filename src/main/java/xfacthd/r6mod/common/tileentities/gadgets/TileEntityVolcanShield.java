package xfacthd.r6mod.common.tileentities.gadgets;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
import xfacthd.r6mod.common.blocks.gadgets.BlockVolcanShield;
import xfacthd.r6mod.common.data.PropertyHolder;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.data.types.TileEntityTypes;
import xfacthd.r6mod.common.tileentities.TileEntityGadget;

public class TileEntityVolcanShield extends TileEntityGadget
{
    public TileEntityVolcanShield() { super(TileEntityTypes.tileTypeVolcanShield, EnumGadget.VOLCAN_SHIELD); }

    @Override
    public void shoot(PlayerEntity shooter, Vector3d hitVec)
    {
        Vector3d subHit = hitVec.subtract(Math.floor(hitVec.getX()), Math.floor(hitVec.getY()), Math.floor(hitVec.getZ()));

        if (subHit.getY() < (9.0/16.0) || subHit.getY() > (15.0/16.0)) { return; }

        BlockState state = getBlockState();

        boolean hit = false;
        switch (state.get(PropertyHolder.FACING_HOR))
        {
            case NORTH:
            {
                hit = subHit.getZ() == (6.0/16.0) && subHit.getX() >= (5.0/16.0) && subHit.getX() <= (11.0/16.0);
                break;
            }
            case EAST:
            {
                hit = subHit.getX() == (10.0/16.0) && subHit.getZ() >= (5.0/16.0) && subHit.getZ() <= (11.0/16.0);
                break;
            }
            case SOUTH:
            {
                hit = subHit.getZ() == (10.0/16.0) && subHit.getX() >= (5.0/16.0) && subHit.getX() <= (11.0/16.0);
                break;
            }
            case WEST:
            {
                hit = subHit.getX() == (6.0/16.0) && subHit.getZ() >= (5.0/16.0) && subHit.getZ() <= (11.0/16.0);
                break;
            }
        }

        if (hit)
        {
            Direction face = state.get(PropertyHolder.FACING_HOR).getOpposite();
            //noinspection ConstantConditions
            ((BlockVolcanShield)state.getBlock()).destroy(world, pos, state, shooter, null, face);
        }
    }
}