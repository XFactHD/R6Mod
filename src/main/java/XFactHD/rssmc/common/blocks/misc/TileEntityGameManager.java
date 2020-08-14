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

package XFactHD.rssmc.common.blocks.misc;

import XFactHD.rssmc.api.util.IGameHandler;
import XFactHD.rssmc.common.blocks.TileEntityBase;
import XFactHD.rssmc.common.data.team.Team;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TileEntityGameManager extends TileEntityBase implements ITickable
{
    public static HashMap<ResourceLocation, Class<? extends IGameHandler>> GAME_MODES = new HashMap<>();
    private IGameHandler runningGame = null;

    @Override
    public void update()
    {
        if (runningGame != null) { runningGame.update(); }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        if (runningGame != null)
        {
            nbt.setString("mode", runningGame.getName().toString());
            nbt.setTag("gameData", runningGame.serializeNBT());
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        if (nbt.hasKey("mode"))
        {
            ResourceLocation name = new ResourceLocation(nbt.getString("mode"));
            if (runningGame != null)
            {
                if (!runningGame.getName().equals(name))
                {
                    runningGame.terminate(true);
                    initializeGame(name);
                }
                runningGame.deserializeNBT(nbt.getCompoundTag("gameData"));
            }
            else
            {
                initializeGame(name);
                runningGame.deserializeNBT(nbt.getCompoundTag("gameData"));
            }
        }
        else { runningGame = null; }
    }

    private void initializeGame(ResourceLocation name)
    {
        try
        {
            runningGame = GAME_MODES.get(name).newInstance();
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            throw new RuntimeException("Could not initialize game mode '" + name.getResourcePath() + "'! Report this to " + name.getResourceDomain(), e);
        }
        runningGame.setManager(this);
        if (!world.isRemote) { notifyBlockUpdate(); }
    }

    public boolean isGameRunning()
    {
        return false;
    }

    public int getTimeLeftSeconds()
    {
        return 0;
    }

    public int getPoints(Team team)
    {
        return 0;
    }

    public IGameHandler getRunningGame()
    {
        return runningGame;
    }

    public GameHandlerBomb getRunningBombGame()
    {
        return runningGame instanceof GameHandlerBomb ? (GameHandlerBomb)runningGame : null;
    }

    public GameHandlerBioContainer getRunningBioContainerGame()
    {
        return runningGame instanceof GameHandlerBioContainer ? (GameHandlerBioContainer)runningGame : null;
    }

    public GameHandlerHostage getRunningHostageGame()
    {
        return runningGame instanceof GameHandlerHostage ? (GameHandlerHostage)runningGame : null;
    }

    public static void registerGameMode(ResourceLocation name, Class<? extends IGameHandler> handler)
    {
        if (GAME_MODES.containsKey(name)) { throw new IllegalArgumentException("IGameHandler is already registered!"); }
        GAME_MODES.put(name, handler);
    }

    public static class GameHandlerBomb implements IGameHandler
    {
        public static final ResourceLocation NAME = new ResourceLocation("rssmc:mode_bomb");
        private TileEntityGameManager te = null;
        private World world = null;

        @Override
        public ResourceLocation getName()
        {
            return NAME;
        }

        @Override
        public void update()
        {
            if (world == null && te.hasWorld()) { world = te.world; }
        }

        @Override
        public void terminate(boolean forced)
        {

        }

        @Override
        public void setManager(TileEntityGameManager te)
        {
            this.te = te;
        }

        @Override
        public NBTTagCompound serializeNBT()
        {
            NBTTagCompound nbt = new NBTTagCompound();

            return nbt;
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt)
        {

        }
    }

    public static class GameHandlerBioContainer implements IGameHandler
    {
        public static final ResourceLocation NAME = new ResourceLocation("rssmc:mode_bio_container");
        private TileEntityGameManager te = null;
        private World world = null;

        @Override
        public ResourceLocation getName()
        {
            return NAME;
        }

        @Override
        public void update()
        {
            if (world == null && te.hasWorld()) { world = te.world; }
        }

        @Override
        public void terminate(boolean forced)
        {

        }

        @Override
        public void setManager(TileEntityGameManager te)
        {
            this.te = te;
        }

        @Override
        public NBTTagCompound serializeNBT()
        {
            NBTTagCompound nbt = new NBTTagCompound();

            return nbt;
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt)
        {

        }
    }

    public static class GameHandlerHostage implements IGameHandler
    {
        public static final ResourceLocation NAME = new ResourceLocation("rssmc:mode_hostage");
        private List<BlockPos> rescuePoints = new ArrayList<>();
        private TileEntityGameManager te = null;
        private World world = null;

        @Override
        public ResourceLocation getName()
        {
            return NAME;
        }

        @Override
        public void update()
        {
            if (world == null && te.hasWorld()) { world = te.world; }
        }

        public void rescueHostage()
        {
            //TODO: implement
        }

        public void addRescuePoint(BlockPos pos)
        {
            rescuePoints.add(pos);
            if (!world.isRemote) { te.notifyBlockUpdate(); }
        }

        public void removeRescuePoint(BlockPos pos)
        {
            rescuePoints.remove(pos);
            if (!world.isRemote) { te.notifyBlockUpdate(); }
        }

        @Override
        public void terminate(boolean forced)
        {

        }

        @Override
        public void setManager(TileEntityGameManager te)
        {
            this.te = te;
        }

        @Override
        public NBTTagCompound serializeNBT()
        {
            NBTTagCompound nbt = new NBTTagCompound();

            return nbt;
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt)
        {

        }
    }
}