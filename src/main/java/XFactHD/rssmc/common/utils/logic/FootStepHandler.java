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

package XFactHD.rssmc.common.utils.logic;

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.common.data.EnumOperator;
import XFactHD.rssmc.common.data.team.StatusController;
import XFactHD.rssmc.common.net.PacketUpdateFootSteps;
import XFactHD.rssmc.common.utils.Utils;
import XFactHD.rssmc.common.utils.utilClasses.FootStep;
import XFactHD.rssmc.common.utils.utilClasses.Position;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

//TODO: convert to non static so that footsteps don't carry over between worlds
public class FootStepHandler
{
    public static World world = null;
    private static HashMap<Position, FootStep> lastFootSteps = new HashMap<>();
    private static HashMap<Position, FootStep> footSteps = new HashMap<>();
    private static HashMap<UUID, Position> lastPos = new HashMap<>();
    private static int ticks = 0;

    //Called on the server
    public static void tick()
    {
        ArrayList<Position> toRemove = new ArrayList<>();
        ticks = FMLCommonHandler.instance().getMinecraftServerInstance().getTickCounter();
        for (Position pos : footSteps.keySet())
        {
            if (ticks - footSteps.get(pos).getTimestamp() > 1800)
            {
                toRemove.add(pos);
            }
        }
        for (Position pos : toRemove)
        {
            removeFootStep(pos);
        }
        if (lastFootSteps.keySet().size() != footSteps.keySet().size())
        {
            lastFootSteps = new HashMap<>(footSteps);
            sendUpdateToClients();
        }
    }

    //Called on the client, only for the color
    public static void clientTick()
    {
        for (Position pos : footSteps.keySet())
        {
            footSteps.get(pos).tick();
        }
    }

    public static void tickDefender(EntityPlayer player)
    {
        if (StatusController.doesOperatorExist(player.world, EnumOperator.JACKAL))
        {
            if (StatusController.getPlayersOperator(player) == EnumOperator.CAVEIRA && GadgetHandler.getHandlerForPlayer(player).getSilentStep()) { return; }
            Position pos = lastPos.getOrDefault(player.getUniqueID(), new Position(0, 0, 0));
            if (pos.distanceTo(Utils.getPlayerPosition(player)) > .8 && isOnGround(player))
            {
                addFootStep(player);
            }
        }
    }

    private static void addFootStep(EntityPlayer player)
    {
        footSteps.put(Utils.getPlayerPosition(player), new FootStep(ticks, Utils.getPlayerPosition(player), player.rotationYaw, player));
        if (lastPos.size() > 0) { lastPos.replace(player.getUniqueID(), Utils.getPlayerPosition(player)); }
        else { lastPos.put(player.getUniqueID(), Utils.getPlayerPosition(player)); }
    }

    private static void removeFootStep(Position pos)
    {
        footSteps.remove(pos);
    }

    //Called when a player dies or leaves
    public static void removeFootStepsForPlayer(EntityPlayer player)
    {
        ArrayList<Position> toRemove = new ArrayList<>();
        for (Position pos : footSteps.keySet())
        {
            if (player.getUniqueID().equals(footSteps.get(pos).getPlayer()))
            {
                toRemove.add(pos);
            }
        }
        for (Position pos : toRemove)
        {
            footSteps.remove(pos);
        }
    }

    //Will be called by the game manager when a game ends
    public static void forceRemoveFootSteps()
    {
        footSteps.clear();
    }

    @SuppressWarnings("unchecked")
    public static HashMap<Position, FootStep> getFootSteps()
    {
        return new HashMap<>(footSteps);
    }

    private static void sendUpdateToClients()
    {
        EntityPlayer jackal = StatusController.getPlayerForOperator(world, EnumOperator.JACKAL);
        if (jackal != null)
        {
            NBTTagCompound nbt = new NBTTagCompound();
            NBTTagList list = new NBTTagList();
            for (Position pos : footSteps.keySet())
            {
                FootStep step = footSteps.get(pos);
                NBTTagCompound tag = new NBTTagCompound();
                tag.setDouble("x", pos.getX());
                tag.setDouble("y", pos.getY());
                tag.setDouble("z", pos.getZ());
                tag.setFloat("rotation", step.getRotation());
                tag.setBoolean("right", step.isRight());
                list.appendTag(tag);
            }
            nbt.setTag("steps", list);
            RainbowSixSiegeMC.NET.sendMessageToClient(new PacketUpdateFootSteps(nbt), jackal);
        }
    }

    public static void receiveClientUpdates(NBTTagCompound nbt)
    {
        NBTTagList list = nbt.getTagList("steps", Constants.NBT.TAG_COMPOUND);
        HashMap<Position, FootStep> steps = new HashMap<>();
        for (int i = 0; i < list.tagCount(); i++)
        {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            Position pos = new Position(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"));
            FootStep step = new FootStep(0, pos, tag.getFloat("rotation"), null, tag.getBoolean("right"));
            steps.put(pos, step);
        }
        footSteps = steps;
    }

    private static boolean isOnGround(EntityPlayer player)
    {
        if (!player.onGround) { return false; }
        IBlockState state = player.world.getBlockState(Utils.getPlayerPosition(player).toBlockPos().down());
        return state.getBlock() != Blocks.AIR && !state.getBlock().isLadder(state, player.world, Utils.getPlayerPosition(player).toBlockPos().down(), player);
    }

    public static FootStep findFootStep(BlockPos pos)
    {
        for (Position p : footSteps.keySet())
        {
            if (p.toBlockPos().equals(pos))
            {
                return footSteps.get(p);
            }
        }
        return null;
    }
}