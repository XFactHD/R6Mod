package xfacthd.r6mod.common.tileentities.gadgets;

import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.data.types.TileEntityTypes;
import xfacthd.r6mod.common.tileentities.TileEntityGadget;

public class TileEntityDeployableShield extends TileEntityGadget
{
    public TileEntityDeployableShield()
    {
        super(TileEntityTypes.tileTypeDeployableShield, EnumGadget.DEPLOYABLE_SHIELD);
    }
}