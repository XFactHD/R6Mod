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

package XFactHD.rssmc.common.gui;

import XFactHD.rssmc.common.blocks.survival.TileEntityMagFiller;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;

public class ContainerMagFiller extends Container
{
    private TileEntityMagFiller te;
    private int energy = 0;
    private boolean active = false;

    public ContainerMagFiller(TileEntityMagFiller te, EntityPlayer player)
    {
        this.te = te;
        for (int y = 0; y < 3; ++y)
        {
            for (int x = 0; x < 9; ++x)
            {
                addSlotToContainer(new Slot(player.inventory, x + y * 9 + 9, 8 + x * 18, 101 + y * 18));
            }
        }
        for (int x = 0; x < 9; ++x)
        {
            addSlotToContainer(new Slot(player.inventory, x, 8 + x * 18, 159));
        }
        //addSlotToContainer(new SlotItemHandlerSpeedLoader(itemHandler, 0,  60, 14)); //TODO: reenable when the te has item handlers
        //addSlotToContainer(new SlotItemHandlerSpeedLoader(itemHandler, 1,  99, 14));
        //addSlotToContainer(new SlotItemHandlerSpeedLoader(itemHandler, 0,  60, 14));
        //addSlotToContainer(new SlotItemHandlerSpeedLoader(itemHandler, 1,  99, 14));
        //addSlotToContainer(new SlotItemHandlerSpeedLoader(itemHandler, 1,  99, 14));
    }

    @Override
    public void updateProgressBar(int id, int data)
    {
        if (id == 0) { energy = data; }
        if (id == 1) { active = data == 1; }
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (IContainerListener listener : listeners)
        {
            if (energy != te.getEnergyStored())
            {
                listener.sendProgressBarUpdate(this, 0, te.getEnergyStored());
            }
            if (active != te.isActive())
            {
                listener.sendProgressBarUpdate(this, 1, te.isActive() ? 1 : 0);
            }
        }
        energy = te.getEnergyStored();
        active = te.isActive();
    }

    public int getEnergyStored()
    {
        return energy;
    }

    public boolean isActive()
    {
        return active;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return true;
    }
}