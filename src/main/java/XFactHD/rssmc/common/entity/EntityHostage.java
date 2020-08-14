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

package XFactHD.rssmc.common.entity;

import XFactHD.rssmc.common.utils.Utils;
import XFactHD.rssmc.common.utils.utilClasses.Position;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHandSide;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;

//TODO: finish hurt system and add a primitive dbno system, add an AI that makes the hostage look turn towards drones and players in its frustum
public class EntityHostage extends EntityLiving
{
    private float healthReserve = 8;
    private boolean dbno = false;

    public EntityHostage(World world)
    {
        super(world);
        setSize(1, 1.5F);
    }

    public EntityHostage(World world, Position pos)
    {
        super(world);
        setSize(1, 1.5F);
        setPosition(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public boolean canBePushed()
    {
        return false;
    }

    @Override
    protected boolean canBeRidden(Entity entity)
    {
        return false;
    }

    @Override
    protected void collideWithEntity(Entity entity)
    {
        //super.collideWithEntity(entity);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        //if (!net.minecraftforge.common.ForgeHooks.onLivingAttack(this, source, amount)) return false;
        //if (this.worldObj.isRemote) { return false; }
        //damageEntity(source, amount);
        //playHurtSound(source);
        //if (dbno && healthReserve <= 0) { setKilled(); }
        //else if (actualHealth <= 0 && actualHealth > - 8) { setDBNO(); }
        boolean attack = super.attackEntityFrom(source, amount);
        motionX = 0;
        motionY = 0;
        motionZ = 0;
        return attack;
    }

    //@Override
    //public void setHealth(float health)
    //{
    //    if (!dbno)
    //    {
    //        actualHealth = health;
    //        super.setHealth(health > 0 ? health : .1F);
    //        if (actualHealth <= 0) { healthReserve -= Math.abs(actualHealth); }
    //    }
    //    else
    //    {
    //        float damage = Math.abs(health) + .1F;
    //        healthReserve -= damage;
    //    }
    //}

    //@Override
    //public void setDead()
    //{
    //    if (!dbno && !worldObj.isRemote)
    //    {
    //        setDBNO();
    //        setHealth(.1F);
    //    }
    //    else if (healthReserve <= 0)
    //    {
    //        super.setDead();
    //    }
    //}

    //@Override
    //public void setHealth(float health)
    //{
    //    super.setHealth(health);
    //    if (health <= 0)
    //    {
    //        healthReserve -= Math.abs(health);
    //    }
    //}

    @Override
    public EnumHandSide getPrimaryHand()
    {
        return null;
    }

    @Override
    public Iterable<ItemStack> getArmorInventoryList()
    {
        return new Iterable<ItemStack>()
        {
            @Override
            public Iterator<ItemStack> iterator()
            {
                return new ArrayList<ItemStack>().iterator();
            }
        };
    }

    @Nullable
    @Override
    public ItemStack getItemStackFromSlot(EntityEquipmentSlot slot)
    {
        return null;
    }

    @Override
    public void setItemStackToSlot(EntityEquipmentSlot slot, @Nullable ItemStack stack) {}

    public boolean isDBNO()
    {
        return dbno;
    }

    public void setDBNO()
    {
        this.dbno = true;
        Utils.sendEntityUpdate(this);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("dbno", dbno);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        dbno = nbt.getBoolean("dbno");
    }
}