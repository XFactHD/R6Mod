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

package XFactHD.rssmc.common.utils;

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.common.net.PacketUpdateEntity;
import XFactHD.rssmc.common.utils.utilClasses.Position;
import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import com.google.common.base.Preconditions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.ArrayList;
import java.util.UUID;

public class Utils
{
    private static final Converter<String, String> converter = CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE);

    public static int getOpposite(int index, int range)
    {
        return range - index;
    }

    public static String toSnakeCase(String input)
    {
        return converter.convert(input);
    }

    public static ResourceLocation locationToSnakeCase(ResourceLocation input)
    {
        return new ResourceLocation(input.getResourceDomain(), toSnakeCase(input.getResourcePath()));
    }

    public static int getSlotFor(ItemStack[] inventory, ItemStack stack)
    {
        for (int i = 0; i < inventory.length; ++i)
        {
            if (inventory[i] != null && stack.isItemEqual(inventory[i]))
            {
                return i;
            }
        }
        return -1;
    }

    public static ArrayList<Integer> getSlotsFor(ItemStack[] inventory, ItemStack stack)
    {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < inventory.length; ++i)
        {
            if (inventory[i] != null && stack.isItemEqual(inventory[i]))
            {
                list.add(i);
            }
        }
        return list;
    }

    public static Position getPlayerPosition(EntityPlayer player)
    {
        return new Position(player.posX, player.posY, player.posZ);
    }

    public static int currentTicks()
    {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getTickCounter();
    }

    public static NetworkRegistry.TargetPoint getTarget(World world, BlockPos pos)
    {
        return getTarget(world, pos, 160);
    }

    public static NetworkRegistry.TargetPoint getTarget(World world, BlockPos pos, int range)
    {
        return new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), range);
    }

    public static int map(int value, int valueRange, int outputRange)
    {
        Preconditions.checkArgument(value >= 0 && valueRange > 0 && outputRange > 0);
        float part = ((float)value) / ((float)valueRange);
        return (int) (part * ((float)outputRange));
    }

    public static boolean isPosInArea(AxisAlignedBB aabb, BlockPos pos)
    {
        return pos.getX() >= aabb.minX && pos.getX() < aabb.maxX &&
               pos.getY() >= aabb.minY && pos.getY() < aabb.maxY &&
               pos.getZ() >= aabb.minZ && pos.getZ() < aabb.maxZ;
    }

    public static boolean isWearingNonOpArmor(ItemStack[] armorInventory)
    {
        if (armorInventory[0] != null) { return true; }
        if (armorInventory[1] != null) { return true; }
        return armorInventory[3] != null;
    }

    public static void sendEntityUpdate(Entity entity)
    {
        RainbowSixSiegeMC.NET.sendMessageToAllClients(new PacketUpdateEntity(entity.getUniqueID(), entity.writeToNBT(new NBTTagCompound())));
    }

    public static Entity getEntityByUUID(UUID uuid)
    {
        World world = RainbowSixSiegeMC.proxy.getWorld();
        if (world == null) { return null; }
        for (Entity e : world.loadedEntityList)
        {
            if (e.getUniqueID().equals(uuid))
            {
                return e;
            }
        }
        return null;
    }
}