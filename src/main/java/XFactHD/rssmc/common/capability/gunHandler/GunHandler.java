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

package XFactHD.rssmc.common.capability.gunHandler;

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.api.capability.IGunHandler;
import XFactHD.rssmc.common.data.EnumAttachment;
import XFactHD.rssmc.common.data.EnumFiremode;
import XFactHD.rssmc.common.data.EnumGun;
import XFactHD.rssmc.common.data.team.StatusController;
import XFactHD.rssmc.common.net.PacketUpdateGun;
import XFactHD.rssmc.common.utils.Damage;
import XFactHD.rssmc.common.utils.Utils;
import XFactHD.rssmc.common.utils.helper.ConfigHandler;
import XFactHD.rssmc.common.utils.utilClasses.HitData;
import XFactHD.rssmc.common.utils.helper.RayTraceUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;

//TODO: rework completely
@SuppressWarnings("ConstantConditions")
public class GunHandler implements IGunHandler, ICapabilitySerializable<NBTTagCompound>
{
    private boolean loadFromStack = false;
    private EnumGun gun = null;
    private ItemStack stack = null;
    private ItemStack ammo = null;
    private EntityPlayer player = null;
    private EnumFiremode firemode = null;
    private ArrayList<EnumAttachment> attachments = new ArrayList<>();
    private int bulletsToBurstFire = 0;
    private boolean firing = false;
    private boolean aiming = false;
    private boolean reloading = false;
    private int reloadTicks = 0;
    private int ticks = 0;

    public GunHandler(ItemStack stack, NBTTagCompound nbt)
    {
        this.stack = stack;
        if (nbt == null)
        {
            loadFromStack = true;
        }
        else
        {
            deserializeNBT(nbt);
        }
    }

    @Override
    public EnumGun getGun()
    {
        return gun;
    }

    @Override
    public void setGun(EnumGun gun)
    {
        this.gun = gun;
    }

    @Override
    public void setStack(ItemStack gunStack)
    {
        this.stack = gunStack;
    }

    @Override
    public void setPlayer(EntityPlayer player)
    {
        this.player = player;
    }

    @Override
    public ItemStack getStack()
    {
        return stack;
    }

    @Override
    public EntityPlayer getPlayer()
    {
        return player;
    }

    @Override
    public void setAiming(boolean aiming)
    {
        if (!reloading)
        {
            this.aiming = aiming;
            sendUpdate();
        }
    }

    @Override
    public boolean isAiming()
    {
        return aiming;
    }

    @Override
    public void setFiring(boolean firing)
    {
        if ((getAmmoLeft() <= 0 || reloading) && firing) { return; }
        this.firing = firing;
        if (firing)
        {
            fireBullet();
            if (firemode == EnumFiremode.DOUBLE)
            {
                bulletsToBurstFire = 1;
            }
            else if (firemode == EnumFiremode.TRIPLE)
            {
                bulletsToBurstFire = 2;
            }
        }
        else
        {
            ticks = 0;
        }
        sendUpdate();
    }

    @Override
    public boolean isFiring()
    {
        return firing;
    }

    @Override
    public void setReloading(boolean reloading, EntityPlayer player)
    {
        if (reloading && !canReload(player, player.inventory)) { return; }
        this.reloading = reloading;
        sendUpdate();
    }

    @Override
    public boolean isReloading()
    {
        return reloading;
    }

    @Override
    public boolean hasAttachment(EnumAttachment attachment)
    {
        return attachments.contains(attachment);
    }

    @Override
    public int getAmmoLeft()
    {
        return ammo == null || !ammo.hasTagCompound() ? 0 : ammo.getTagCompound().getInteger("currentAmmo");
    }

    @Override
    public void reload(EntityPlayer player, InventoryPlayer inv)
    {
        boolean beltFed = gun == EnumGun.PKP_6P41 || gun == EnumGun.M249;
        int ammoOffset = gun != null && !beltFed && gun.hasMag() ? 1 : 0;
        if (ammo != null && ammo.getTagCompound().getInteger("currentAmmo") == ammo.getTagCompound().getInteger("maxAmmo") + ammoOffset) { return; }
        ItemStack newMag = null;
        ItemStack oldMag = ammo != null ? ammo.copy() : null;
        if (inv.hasItemStack(EnumGun.valueOf(stack).getMagazineStack(true)))
        {
            int slot = getFullestMag(inv.mainInventory, EnumGun.valueOf(stack).getMagazineStack(true));
            newMag = inv.decrStackSize(slot, 1);
        }
        if (StatusController.shouldReloadPartialMags(player) && newMag != null && oldMag != null && oldMag.getTagCompound().getInteger("currentAmmo") > 0)
        {
            int toReload = (oldMag.getTagCompound().getInteger("maxAmmo") + ammoOffset) - oldMag.getTagCompound().getInteger("currentAmmo");
            boolean fullReload = toReload >= oldMag.getTagCompound().getInteger("maxAmmo");
            toReload = Math.min(toReload, newMag.getTagCompound().getInteger("currentAmmo"));
            oldMag.getTagCompound().setInteger("currentAmmo", oldMag.getTagCompound().getInteger("currentAmmo") + toReload);
            newMag.getTagCompound().setInteger("currentAmmo", newMag.getTagCompound().getInteger("currentAmmo") - toReload);
            ammo = oldMag;
            inv.addItemStackToInventory(newMag);
            boolean canLoad = !fullReload && inv.hasItemStack(EnumGun.valueOf(stack).getMagazineStack(true));
            if (ammo != null && ammo.getTagCompound().getInteger("currentAmmo") < ammo.getTagCompound().getInteger("maxAmmo") + ammoOffset && canLoad)
            {
                reload(player, inv);
            }
        }
        else
        {
            inv.addItemStackToInventory(oldMag);
            ammo = newMag;
        }
        inv.markDirty();
    }

    private void fireBullet() //FIXME: doesn't hit anything
    {
        ammo.getTagCompound().setInteger("currentAmmo", ammo.getTagCompound().getInteger("currentAmmo") - 1);
        player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1, .5F);
        ArrayList<HitData> hitData;
        boolean hitBlock = false;
        BlockPos blockHit = null;
        EnumFacing sideHit = null;
        if (aiming)
        {
            hitData = RayTraceUtils.rayTraceEntities(player.world, player, Utils.getPlayerPosition(player), player.getLookVec().normalize(), ConfigHandler.maxShootRange, gun.getMaxPenetrationCount());
        }
        else
        {
            hitData = RayTraceUtils.rayTraceEntitiesWithSpread(player.world, player, Utils.getPlayerPosition(player), player.getLookVec().normalize(), ConfigHandler.maxShootRange, gun.getMaxPenetrationCount(), ((int)gun.getSpreadModified(attachments) * 100));
        }
        //TODO: apply recoil
        ArrayList<HitData> toRemove = new ArrayList<>();
        for (HitData data : hitData)
        {
            if (data.getRayTrace().typeOfHit == RayTraceResult.Type.BLOCK)
            {
                toRemove.add(data);
                hitBlock = true;
                blockHit = data.getRayTrace().getBlockPos();
                sideHit = data.getRayTrace().sideHit;
            }
        }
        hitData.removeAll(toRemove);
        if (hitData.isEmpty()) { return; }
        for (int i = 0; i < Math.max(gun.getMaxPenetrationCount() + 1, hitData.size()); i++)
        {
            HitData data = hitData.get(i);
            data.getVictim().attackEntityFrom(Damage.causeBulletDamage(player, gun), data.isHeadshot() ? Float.MAX_VALUE : gun.getActualDamage(attachments));
        }
    }

    private void fireShell()
    {
        HashMap<Integer, ArrayList<HitData>> hitData = new HashMap<>();
        ArrayList<HitData> data = new ArrayList<>();
        for (int i = 1; i < 8; ++i)
        {
            if (i == 1)
            {
                hitData.put(i, RayTraceUtils.rayTraceEntities(player.world, player, Utils.getPlayerPosition(player), player.getLookVec().normalize(), ConfigHandler.maxShootRange, gun.getMaxPenetrationCount()));
            }
            else
            {
                hitData.put(i, RayTraceUtils.rayTraceEntitiesWithSpread(player.world, player, Utils.getPlayerPosition(player), player.getLookVec().normalize(), ConfigHandler.maxShootRange, gun.getMaxPenetrationCount(), ((int)gun.getSpreadModified(attachments) * 100)));
            }
        }
        for (int i : hitData.keySet())
        {
            data.add(hitData.get(i).get(0));
        }
        for (HitData hd : data)
        {
            if (hd.getRayTrace().typeOfHit == RayTraceResult.Type.ENTITY)
            {
                hd.getVictim().attackEntityFrom(Damage.causeBulletDamage(hd.getShooter(), gun), gun.getActualDamage(attachments));
            }
        }
    }

    @Override
    public void setFiremode(EnumFiremode firemode)
    {
        if (!firing)
        {
            this.firemode = firemode;
            sendUpdate();
        }
    }

    @Override
    public boolean switchFiremode()
    {
        if (!firing)
        {
            int index = gun.getFiremodes().indexOf(firemode);
            firemode = index == (gun.getFiremodes().size() - 1) ? gun.getFiremodes().get(0) : gun.getFiremodes().get(index + 1);
            sendUpdate();
            return canSwitchFiremode();
        }
        return false;
    }

    @Override
    public EnumFiremode getFiremode()
    {
        return firemode;
    }

    @Override
    public boolean canSwitchFiremode()
    {
        return gun.getFiremodes().size() > 1;
    }

    @Override
    public void update(World world, EntityPlayer player, int slot, boolean isSelected)
    {
        if (this.player == null) { this.player = player; }
        if (loadFromStack || gun == null) { loadFromStack(); }

        if (reloading)
        {
            reloadTicks += 1;
            if (reloadTicks >= gun.getReloadTime())
            {
                reloadTicks = 0;
                reload(player, player.inventory);
                setReloading(false, player);
            }
        }

        if (firing)
        {
            ticks += 1;
            if (ticks >= gun.getTicksBetweenRounds())
            {
                ticks = 0;
                fireBullet();
                if (ammo.getTagCompound().getInteger("currentAmmo") <= 0)
                {
                    bulletsToBurstFire = 0;
                    setFiring(false);
                }
                if (firemode == EnumFiremode.DOUBLE || firemode == EnumFiremode.TRIPLE)
                {
                    if (bulletsToBurstFire > 0)
                    {
                        bulletsToBurstFire -= 1;
                    }
                    else
                    {
                        setFiring(false);
                    }
                }
                else if (firemode == EnumFiremode.SINGLE) { setFiring(false); }
            }
        }
    }

    @Override
    public void addAttachment(EnumAttachment attachment)
    {
        attachments.add(attachment);
        sendUpdate();
    }

    @Override
    public void removeAttachment(EnumAttachment attachment)
    {
        attachments.remove(attachment);
        sendUpdate();
    }

    @Override
    public void preLoad()
    {
        ammo = getGun().getMagazineStack(true);
        boolean beltFed = gun == EnumGun.PKP_6P41 || gun == EnumGun.M249 || gun == EnumGun.LMG_E;
        int ammoOffset = gun != null && !beltFed && gun.hasMag() ? 1 : 0;
        if (ammoOffset > 0)
        {
            ammo.getTagCompound().setInteger("currentAmmo", ammo.getTagCompound().getInteger("currentAmmo") + ammoOffset);
        }
        if (gun == EnumGun.OTS_03)
        {
            attachments.add(EnumAttachment.FLIP_SIGHT);
        }
        else if (gun == EnumGun.C8_SFW ||gun == EnumGun.CAMRS)
        {
            attachments.add(EnumAttachment.SHOTGUN);
        }
        sendUpdate();
    }

    @Override
    public void loadFromStack()
    {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("ForgeCaps"))
        {
            deserializeNBT(stack.getTagCompound().getCompoundTag("ForgeCaps"));
        }
        else
        {
            gun = EnumGun.values()[stack.getMetadata()];
            firemode = gun.getFiremodes().contains(EnumFiremode.AUTO) ? EnumFiremode.AUTO : EnumFiremode.SINGLE;
        }
        loadFromStack = false;
        sendUpdate();
    }

    private void sendUpdate()
    {
        if (player != null)
        {
            RainbowSixSiegeMC.NET.sendMessageToClient(new PacketUpdateGun(serializeNBT()), player);
        }
    }

    private boolean canReload(EntityPlayer player, InventoryPlayer inv)
    {
        boolean beltFed = gun == EnumGun.PKP_6P41 || gun == EnumGun.M249;
        int ammoOffset = gun != null && !beltFed && gun.hasMag() ? 1 : 0;
        if (ammo != null && ammo.getTagCompound().getInteger("currentAmmo") == ammo.getTagCompound().getInteger("maxAmmo") + ammoOffset) { return false; }
        return inv.hasItemStack(EnumGun.valueOf(stack).getMagazineStack(true));
    }

    @Override
    public void receiveUpdate(NBTTagCompound nbt)
    {
        deserializeNBT(nbt);
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("gun", gun != null ? gun.ordinal() : -1);
        if (ammo != null) { nbt.setTag("ammo", ammo.writeToNBT(new NBTTagCompound())); }
        nbt.setInteger("mode", firemode != null ? firemode.ordinal() : -1);
        nbt.setBoolean("aiming", aiming);
        nbt.setBoolean("firing", firing);
        nbt.setBoolean("reloading", reloading);
        NBTTagList list = new NBTTagList();
        for (EnumAttachment attachment : attachments)
        {
            NBTTagCompound data = new NBTTagCompound();
            data.setInteger("index", attachment.ordinal());
            list.appendTag(data);
        }
        if (!list.hasNoTags()) { nbt.setTag("attachments", list); }
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        gun = nbt.getInteger("gun") != -1 ? EnumGun.valueOf(nbt.getInteger("gun")) : null;
        if (nbt.hasKey("ammo")) { ammo = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("ammo")); }
        firemode = nbt.getInteger("mode") != -1 ? EnumFiremode.values()[nbt.getInteger("mode")] : null;
        aiming = nbt.getBoolean("aiming");
        firing = nbt.getBoolean("firing");
        reloading = nbt.getBoolean("reloading");
        if (nbt.hasKey("attachments"))
        {
            NBTTagList list = nbt.getTagList("attachments", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.tagCount(); i++)
            {
                NBTTagCompound data = list.getCompoundTagAt(i);
                attachments.add(EnumAttachment.valueOf(data.getInteger("attachment")));
            }
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == GunHandlerStorage.GUN_HANDLER_CAPABILITY;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        return capability == GunHandlerStorage.GUN_HANDLER_CAPABILITY ? (T)this : null;
    }

    private int getFullestMag(ItemStack[] inv, ItemStack sample)
    {
        ArrayList<Integer> slots = Utils.getSlotsFor(inv, sample);
        ItemStack highest = null;
        int slot = -1;
        for (int i : slots)
        {
            ItemStack stack = inv[i];
            if (highest == null || highest.getTagCompound().getInteger("currentAmmo") < stack.getTagCompound().getInteger("currentAmmo"))
            {
                highest = stack;
                slot = i;
            }
        }
        return slot;
    }
}