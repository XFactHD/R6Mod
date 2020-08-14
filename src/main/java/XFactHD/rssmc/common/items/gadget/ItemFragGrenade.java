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

package XFactHD.rssmc.common.items.gadget;

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.api.item.ISpecialRightClick;
import XFactHD.rssmc.common.data.team.StatusController;
import XFactHD.rssmc.common.entity.gadget.EntityFragGrenade;
import XFactHD.rssmc.common.items.ItemBase;
import XFactHD.rssmc.common.utils.Damage;
import XFactHD.rssmc.client.util.Sounds;
import XFactHD.rssmc.common.utils.utilClasses.Position;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

@SuppressWarnings("ConstantConditions")
public class ItemFragGrenade extends ItemBase implements ISpecialRightClick
{
    public ItemFragGrenade()
    {
        super("item_frag_grenade", 2, RainbowSixSiegeMC.CT.gadgetTab, null);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected)
    {
        if (!(entity instanceof EntityPlayer)) { return; }
        if (!stack.hasTagCompound()) { stack.setTagCompound(new NBTTagCompound()); }
        if (stack.getTagCompound().getBoolean("clicked"))
        {
            if (!isSelected) { scrollOff(stack, (EntityPlayer)entity, world, EnumHand.MAIN_HAND); return; }
            if (world.getTotalWorldTime() - stack.getTagCompound().getLong("time") >= 50)
            {
                Position pos = new Position(entity.posX, entity.posY, entity.posZ);
                AxisAlignedBB aabb = new AxisAlignedBB(entity.posX - 3, entity.posY - 3, entity.posZ - 3, entity.posX + 3, entity.posY + 3, entity.posZ + 3);
                for (EntityLivingBase victim : world.getEntitiesWithinAABB(EntityLivingBase.class, aabb))
                {
                    if (StatusController.canSeeEntity(entity, victim))
                    {
                        double distance = pos.distanceTo(new Position(victim.posX, victim.posY, victim.posZ));
                        float amount = distance == 0 ? 4000 : 28F * (float)(distance / 3);
                        victim.attackEntityFrom(Damage.causeFragGrenadeDamage((EntityPlayer)entity), amount);
                    }
                }

                world.playSound(null, entity.getPosition(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, .5F, 1);
                stack.getTagCompound().setBoolean("clicked", false);
                stack.getTagCompound().setLong("time", 0);
            }
        }
    }

    @Override
    public void startRightClick(ItemStack stack, EntityPlayer player, World world, EnumHand hand)
    {
        if (world.isRemote) { return; }

        world.playSound(null, player.getPosition(), Sounds.getGadgetSound(null, "grenade_pull_pin"), SoundCategory.PLAYERS, .5F, 1);
        if (!stack.hasTagCompound()) { stack.setTagCompound(new NBTTagCompound()); }
        stack.getTagCompound().setBoolean("clicked", true);
        stack.getTagCompound().setLong("time", world.getTotalWorldTime());
    }

    @Override
    public void stopRightClick(ItemStack stack, EntityPlayer player, World world, EnumHand hand)
    {
        if (world.isRemote) { return; }

        if (!stack.hasTagCompound()) { stack.setTagCompound(new NBTTagCompound()); }
        if (!stack.getTagCompound().getBoolean("clicked")) { return; }
        stack.getTagCompound().setBoolean("clicked", false);
        world.playSound(null, player.getPosition(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.PLAYERS, .5F, .4F / (itemRand.nextFloat() * .4F + .8F));

        int time = (int) (world.getTotalWorldTime() - stack.getTagCompound().getLong("time"));
        EntityFragGrenade grenade = new EntityFragGrenade(world, player, 50 - time);
        grenade.setHeadingFromThrower(player, player.rotationPitch, player.rotationYaw, 0, 1, 0);
        stack.getTagCompound().setLong("time", 0);
        if (world.spawnEntity(grenade))
        {
            player.inventory.decrStackSize(player.inventory.currentItem, 1);
            player.inventory.markDirty();
        }
    }

    @Override
    public void scrollOff(ItemStack stack, EntityPlayer player, World world, EnumHand hand)
    {
        if (world.isRemote) { return; }

        stack.getTagCompound().setBoolean("clicked", false);
        stack.getTagCompound().setLong("time", 0);
    }

    public int getTimeLeft(ItemStack stack, World world)
    {
        if (!stack.hasTagCompound()) { return -1; }
        if (stack.getTagCompound().getLong("time") == 0) { return -1; }
        return 50 - ((int) (world.getTotalWorldTime() - stack.getTagCompound().getLong("time")));
    }
}