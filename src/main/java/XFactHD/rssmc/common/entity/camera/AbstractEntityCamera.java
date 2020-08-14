/*  Copyright (C) <2017>  <XFactHD>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see http://www.gnu.org/licenses. */

package XFactHD.rssmc.common.entity.camera;

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.common.net.PacketUpdateEntity;
import XFactHD.rssmc.common.utils.RSSWorldData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.UUID;

//TODO: implement, used for drones and cameras, handle spectators specating players in cams
public abstract class AbstractEntityCamera extends Entity
{
    private String description = "";
    protected UUID owner = null;
    private ArrayList<UUID> viewers = new ArrayList<>();
    protected UUID user = null;

    public AbstractEntityCamera(World world)
    {
        super(world);
        setSize(0, 0);
        setEntityBoundingBox(new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D));
    }

    public AbstractEntityCamera(World world, UUID owner)
    {
        this(world);
        this.owner = owner;
    }

    public void setPositionDescription(String description)
    {
        this.description = description;
    }

    public String getPositionDescription()
    {
        return description;
    }

    public abstract String getType();

    public abstract boolean canBeViewedBy(EntityPlayer player);

    public abstract boolean canBeUsedBy(EntityPlayer player);

    public abstract boolean canBeControlledBy(EntityPlayer player);

    public boolean isViewedBy(EntityPlayer player)
    {
        return isViewedBy(player.getUniqueID());
    }

    public boolean isViewedBy(UUID uuid) { return viewers.contains(uuid); }

    public boolean isControlledBy(EntityPlayer player)
    {
        return player.getUniqueID().equals(user);
    }

    public EntityPlayer getOwner() { return owner != null ? world.getPlayerEntityByUUID(owner) : null; }

    public EntityPlayer getUser()
    {
        return user != null ? world.getPlayerEntityByUUID(user) : null;
    }

    public void setUser(UUID user)
    {
        this.user = user;
    }

    public void addViewer(UUID viewer)
    {
        viewers.add(viewer);
        if (user == null)
        {
            setUser(viewer);
        }
        sendEntityUpdate();
    }

    public void removeViewer(UUID viewer)
    {
        viewers.remove(viewer);
        if (viewer.equals(user))
        {
            setUser(viewers.isEmpty() ? null : viewers.get(0));
        }
        sendEntityUpdate();
    }

    @Override
    public boolean canBePushed()
    {
        return false;
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return false;
    }

    @Override
    protected void entityInit()
    {
        RSSWorldData.get(world).getObservationManager().addCamera(this);
    }

    @Override
    public void setDead()
    {
        RSSWorldData.get(world).getObservationManager().removeCamera(this);
        for (UUID uuid : viewers) { removeViewer(uuid); }
        super.setDead();
    }

    protected void sendEntityUpdate()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        writeEntityToNBT(nbt);
        RainbowSixSiegeMC.NET.sendMessageToAllClients(new PacketUpdateEntity(entityUniqueID, nbt));
    }

    public void applyMouseMovement(int dx, int dy) {}

    public void applyMovement(boolean forward, boolean back, boolean right, boolean left) {}

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt)
    {
        nbt.setString("desc", description);
        if (owner != null) { nbt.setUniqueId("owner", owner); }
        if (user != null) { nbt.setUniqueId("user", user); }
        NBTTagList list = new NBTTagList();
        for (UUID viewer : viewers)
        {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setUniqueId("viewer", viewer);
            list.appendTag(tag);
        }
        if (!list.hasNoTags()) { nbt.setTag("viewers", list); }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt)
    {
        description = nbt.getString("desc");
        owner = nbt.hasKey("owner") ? nbt.getUniqueId("owner") : null;
        user = nbt.hasKey("user") ? nbt.getUniqueId("user") : null;
        viewers.clear();
        if (nbt.hasKey("viewers"))
        {
            NBTTagList list = nbt.getTagList("viewers", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.tagCount(); i++)
            {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                viewers.add(tag.getUniqueId("viewer"));
            }
        }
    }
}