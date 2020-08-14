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

package XFactHD.rssmc.common.net;

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.common.capability.gunHandler.GunHandlerStorage;
import XFactHD.rssmc.common.data.EnumFiremode;
import XFactHD.rssmc.common.data.EnumGadget;
import XFactHD.rssmc.common.data.EnumGun;
import XFactHD.rssmc.common.data.EnumOperator;
import XFactHD.rssmc.common.items.armor.ItemOperatorArmor;
import XFactHD.rssmc.common.items.gun.ItemRiotShield;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class PacketSetOperator implements IMessage
{
    private NBTTagCompound data;

    public PacketSetOperator() {}

    public PacketSetOperator(EnumOperator operator, EnumGun primary, EnumGun secondary, EnumGadget gadget)
    {
        data = new NBTTagCompound();
        data.setInteger("operator",  operator == null  ? -1 : operator.ordinal());
        data.setInteger("primary",   primary == null   ? -1 : primary.ordinal());
        data.setInteger("secondary", secondary == null ? -1 : secondary.ordinal());
        data.setInteger("gadget",    gadget == null    ? -1 : gadget.ordinal());
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        data = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeTag(buf, data);
    }

    public static class Handler implements IMessageHandler<PacketSetOperator, IMessage>
    {
        @Override
        public IMessage onMessage(PacketSetOperator message, MessageContext ctx)
        {
            RainbowSixSiegeMC.proxy.addScheduledTask(new Runnable()
            {
                @Override
                public void run()
                {
                    handleMessage(ctx.getServerHandler().playerEntity, message.data);
                }
            });
            return null;
        }

        @SuppressWarnings("ConstantConditions")
        private void handleMessage(final EntityPlayer player, final NBTTagCompound data)
        {
            EnumOperator operator = data.getInteger("operator") == -1  ? null : EnumOperator.values()[data.getInteger("operator")];
            EnumGun primary =       data.getInteger("primary") == -1   ? null : EnumGun.values()[data.getInteger("primary")];
            EnumGun secondary =     data.getInteger("secondary") == -1 ? null : EnumGun.values()[data.getInteger("secondary")];
            EnumGadget gadget =     data.getInteger("gadget") == -1    ? null : EnumGadget.values()[data.getInteger("gadget")];
            if (operator == null)
            {
                if (player.inventory.getStackInSlot(38) != null && player.inventory.getStackInSlot(38).getItem() instanceof ItemOperatorArmor) player.inventory.removeStackFromSlot(37);
            }
            else
            {
                player.inventory.setInventorySlotContents(38, ItemOperatorArmor.getArmorStack(operator, false));
                ItemStack primaryStack = primary.getGunItemStack();
                if (primaryStack.getItem() instanceof ItemRiotShield)
                {
                    player.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, primaryStack);
                }
                else
                {
                    primaryStack.getCapability(GunHandlerStorage.GUN_HANDLER_CAPABILITY, null).setPlayer(player);
                    ArrayList<EnumFiremode> modes = primary.getFiremodes();
                    primaryStack.getCapability(GunHandlerStorage.GUN_HANDLER_CAPABILITY, null).setFiremode(modes.contains(EnumFiremode.AUTO) ? EnumFiremode.AUTO : EnumFiremode.SINGLE);
                    player.inventory.addItemStackToInventory(primaryStack);
                }

                ItemStack secondaryStack = secondary.getGunItemStack();
                secondaryStack.getCapability(GunHandlerStorage.GUN_HANDLER_CAPABILITY, null).setPlayer(player);
                ArrayList<EnumFiremode> modes = secondary.getFiremodes();
                secondaryStack.getCapability(GunHandlerStorage.GUN_HANDLER_CAPABILITY, null).setFiremode(modes.contains(EnumFiremode.AUTO) ? EnumFiremode.AUTO : EnumFiremode.SINGLE);
                player.inventory.addItemStackToInventory(secondaryStack);

                if (operator.getSpecial() != null)
                {
                    ItemStack specialStack = operator.getSpecial().getGadgetItemStack();
                    if (operator.getSpecial().isLeftHanded())
                    {
                        player.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, specialStack);
                    }
                    else
                    {
                        player.inventory.addItemStackToInventory(specialStack);
                    }
                }

                player.inventory.addItemStackToInventory(gadget.getGadgetItemStack());
                player.inventory.addItemStackToInventory(primary.getMagazineStack(false));
                player.inventory.addItemStackToInventory(secondary.getMagazineStack(false));
                if (gadget.needsAmmo())
                {
                    for (ItemStack ammo : gadget.getAmmoStacks())
                    {
                        player.inventory.addItemStackToInventory(ammo.copy());
                    }
                }
            }
        }
    }
}