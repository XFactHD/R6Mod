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

package XFactHD.rssmc.common.items.itemBlocks;

import XFactHD.rssmc.common.blocks.BlockBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemBlockBase extends ItemBlock
{
    protected BlockBase block;

    public ItemBlockBase(BlockBase block)
    {
        super(block);
        this.block = block;
        setRegistryName(block.getRegistryName());
        setUnlocalizedName(block.getUnlocalizedName());
    }

    public ItemBlockBase(BlockBase block, Integer stackSize)
    {
        this(block);
        setMaxStackSize(stackSize);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        if (block.getSubnames() != null && stack.getMetadata() < block.getSubnames().length)
        {
            return getUnlocalizedName() + "_" + block.getSubnames()[stack.getMetadata()];
        }
        return super.getUnlocalizedName(stack);
    }

    @Override
    public int getMetadata(int damage)
    {
        return block.getSubnames() != null ? damage : 0;
    }

    //TODO: make all names lower case in 1.11 update
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
        String realName = "";
        switch (stack.getItem().getRegistryName().toString().substring(6))
        {
            case "block_ads": realName = "ADS-MK IV 'Magpie'"; break;
            case "blockArmorBag": realName = "R1N 'Rhino' Armor"; break;
            case "blockBlackMirror": realName = "Black Mirror"; break;
            case "blockClusterCharge": realName = "APM-6 'Matryoshka'"; break;
            case "blockJammer": realName = "GC90 Signal Disruptor"; break;
            case "blockKapkanTrap": realName = "EDD MK II"; break;
            case "block_lmg": realName = "RP-46 Degtyaryov Machine Gun"; break;
            case "blockShockWire": realName = "CED-1 (Crude Electrical Device)"; break;
            case "blockThermiteCharge": realName = "Brimstone BC-3"; break;
            case "blockToughBarricade": realName = "UTP1-Universial Tactical Panel"; break;
            case "blockWelcomeMat": realName = "Sterling Mk2 LHT"; break;
            default: return;
        }
        tooltip.add(realName);
    }
}