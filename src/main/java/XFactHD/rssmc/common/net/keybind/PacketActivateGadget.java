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

package XFactHD.rssmc.common.net.keybind;

import XFactHD.rssmc.common.Content;
import XFactHD.rssmc.common.data.EnumAttachment;
import XFactHD.rssmc.common.data.EnumGun;
import XFactHD.rssmc.common.data.EnumOperator;
import XFactHD.rssmc.common.items.armor.ItemOperatorArmor;
import XFactHD.rssmc.common.items.gadget.ItemStimPistol;
import XFactHD.rssmc.common.items.gun.ItemGun;
import XFactHD.rssmc.common.utils.logic.GadgetHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketActivateGadget extends AbstractPacketKeyBind
{
    private static final ItemStack JACKAL_HELMET = ItemOperatorArmor.getArmorStack(EnumOperator.JACKAL, false);
    private static final ItemStack JACKAL_HELMET_ROOK = ItemOperatorArmor.getArmorStack(EnumOperator.JACKAL, false);
    private static final ItemStack SILENCED_PRB92 = new ItemStack(Content.itemGun, 1, EnumGun.PRB92.ordinal());
    private static final ItemStack STIM_PISTOL = new ItemStack(Content.itemStimPistol);

    public static class Handler implements IMessageHandler<PacketActivateGadget, IMessage>
    {
        @Override
        public IMessage onMessage(PacketActivateGadget message, final MessageContext ctx)
        {
            ((WorldServer)ctx.getServerHandler().playerEntity.world).addScheduledTask(new Runnable()
            {
                @Override
                public void run()
                {
                    EntityPlayer player = ctx.getServerHandler().playerEntity;
                    ItemStack current = player.inventory.getStackInSlot(player.inventory.currentItem);
                    if (player.inventory.hasItemStack(JACKAL_HELMET) || player.inventory.hasItemStack(JACKAL_HELMET_ROOK))
                    {
                        GadgetHandler.getHandlerForPlayer(player).switchFootPrintScanner();
                    }
                    else if (current != null && current.isItemEqual(SILENCED_PRB92) && ItemGun.hasAttachment(current, EnumAttachment.SUPPRESSOR))
                    {
                        GadgetHandler.getHandlerForPlayer(player).switchSilentStep();
                    }
                    else if (current != null && current.isItemEqual(STIM_PISTOL) && ((ItemStimPistol)current.getItem()).isLoaded(current))
                    {
                        ((ItemStimPistol)current.getItem()).doSelfHeal(current, player);
                    }
                }
            });
            return null;
        }
    }
}