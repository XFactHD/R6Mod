package XFactHD.rssmc.common.blocks.building;

import XFactHD.rssmc.common.blocks.TileEntityBase;
import XFactHD.rssmc.common.utils.propertyEnums.EnumMaterial;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityWall extends TileEntityBase
{
    private boolean destroyed = false;
    private EnumMaterial material = EnumMaterial.PLASTER;

    public void setDestroyed(boolean destroyed)
    {
        this.destroyed = destroyed;
        notifyBlockUpdate();
    }

    public boolean isDestroyed()
    {
        return destroyed;
    }

    public void setMaterial(EnumMaterial material)
    {
        this.material = material;
        notifyBlockUpdate();
    }

    public EnumMaterial getMaterial()
    {
        return material;
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        nbt.setString("camo", material.toString());
        nbt.setBoolean("destroyed", destroyed);
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        material = EnumMaterial.valueOf(nbt.getString("camo"));
        destroyed = nbt.getBoolean("destroyed");
    }
}
