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

package XFactHD.rssmc.common.items;

import XFactHD.rssmc.common.utils.Reference;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

public class ItemBase extends Item
{
    private static IForgeRegistry<Item> registry;
    private String[] subnames;

    public ItemBase(String name, int stacksize, CreativeTabs creativeTab, String[] subnames)
    {
        this(name, stacksize, creativeTab, subnames, null);
    }

    public ItemBase(String name, int stacksize, CreativeTabs creativeTab, String[] subnames, String[] oreDictNames)
    {
        this.subnames = subnames;
        setHasSubtypes(subnames != null);
        setRegistryName(new ResourceLocation(Reference.MOD_ID, name));
        setUnlocalizedName(getRegistryName().toString());
        setMaxStackSize(stacksize);
        setCreativeTab(creativeTab);
        registry.register(this);
        registerOreDictEntries(oreDictNames);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> subItems)
    {
        if (item != this) { return; }
        if (subnames != null)
        {
            for (int i = 0; i < subnames.length; i++)
            {
                subItems.add(new ItemStack(item, 1, i));
            }
        }
        else
        {
            subItems.add(new ItemStack(item));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        int meta = stack.getMetadata();
        if (subnames != null && meta < subnames.length)
        {
            return getUnlocalizedName() + "_" + subnames[meta];
        }
        return super.getUnlocalizedName(stack);
    }

    protected boolean hasLeftClickAction()
    {
        return false;
    }

    public boolean onItemLeftClick(ItemStack stack, World world, EntityPlayer player)
    {
        return false;
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entity, ItemStack stack)
    {
        if (hasLeftClickAction() && entity instanceof EntityPlayer)
        {
            return onItemLeftClick(stack, entity.world, (EntityPlayer) entity);
        }
        return false;
    }

    //TODO: make all names lower case in 1.11 update
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
        String realName;
        switch (stack.getItem().getRegistryName().toString().substring(6))
        {
            case "item_sledge_hammer": realName = "The Caber"; break;
            case "itemEMPGrenade": realName = "EG MKO-EMP Grenade"; break;
            case "itemGasCanister": realName = "Compound Z8"; break;
            case "item_grenade_launcher": realName = "M120 CREM"; break;
            case "item_cardiac_sensor": realName = "HB-5 Cardiac Sensor"; break;
            case "item_shock_drone": realName = "RSD Model 1"; break;
            case "item_stim_pistol": realName = "MPD-0 Stim Pistol"; break;
            case "item_electronics_detector": realName = "RED MK III 'Spectre'"; break;
            case "item_sticky_camera": realName = "Gyro Cam Mk2 'Black Eye'"; break;
            case "item_crossbow": realName = "Tactical Crossbow Tac Mk0"; break;
            case "item_xkairos_launcher": realName = "X-KAIROS 40mm Launcher"; break;
            case "item_yokai_drone": realName = "Yokai Drone"; break;
            case "item_logic_bomb": realName = "?"; break;
            case "itemRiotShield":
            {
                int meta = stack.getMetadata();
                if (meta == 1) { return; }
                realName = meta == 0 ? "Le Roc" : "G52-Tactical Shield";
                break;
            }
            default: return;
        }
        tooltip.add(realName);
    }

    private void registerOreDictEntries(String[] oreDictNames)
    {
        if (oreDictNames != null)
        {
            for (int i = 0; i < oreDictNames.length; i++)
            {
                if (!oreDictNames[i].equals(""))
                {
                    OreDictionary.registerOre(oreDictNames[i], new ItemStack(this, 1, i));
                }
            }
        }
    }

    public static void setRegistry(IForgeRegistry<Item> registry)
    {
        ItemBase.registry = registry;
    }
}