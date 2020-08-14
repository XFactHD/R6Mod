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

package XFactHD.rssmc.common.data.team;

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.common.Content;
import XFactHD.rssmc.common.blocks.objective.TileEntityBioContainer;
import XFactHD.rssmc.common.blocks.objective.TileEntityBomb;
import XFactHD.rssmc.common.capability.dbnoHandler.DBNOHandlerStorage;
import XFactHD.rssmc.common.data.EnumOperator;
import XFactHD.rssmc.common.data.EnumSide;
import XFactHD.rssmc.common.items.armor.ItemOperatorArmor;
import XFactHD.rssmc.common.utils.RSSWorldData;
import XFactHD.rssmc.common.utils.Utils;
import XFactHD.rssmc.common.utils.utilClasses.Position;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.UUID;

public class StatusController
{
    private static final ItemStack DEFUSER = new ItemStack(Content.blockDefuser);

    //INFO: other players in dbno only count as alive if they play doc or zofia
    @SuppressWarnings("ConstantConditions")
    public static boolean areOtherPlayersAlive(EntityPlayer player)
    {
        if (player == null || player.world == null) { return false; }
        if (!isPlayerInATeam(player)) { return false; }
        for (UUID uuid : getPlayersTeam(player).getPlayers())
        {
            EntityPlayer teammate = player.world.getPlayerEntityByUUID(uuid);
            if (teammate == null || teammate.getUniqueID().equals(player.getUniqueID())) { continue; }
            if (!teammate.isDead && RainbowSixSiegeMC.proxy.getPlayersGameType(teammate) == GameType.SURVIVAL)
            {
                EnumOperator mateOp = getPlayersOperator(teammate);
                boolean wasMateDBNO = teammate.getCapability(DBNOHandlerStorage.DBNO_HANDLER_CAPABILITY, null).wasAlreadyDBNO();
                if (!teammate.getCapability(DBNOHandlerStorage.DBNO_HANDLER_CAPABILITY, null).isDBNO() || ((mateOp == EnumOperator.DOC || mateOp == EnumOperator.ZOFIA) && !wasMateDBNO))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isPlayerAlive(EntityPlayer player)
    {
        if (!isPlayerInATeam(player)) { return false; }
        return !player.isDead && RainbowSixSiegeMC.proxy.getPlayersGameType(player) == GameType.SURVIVAL;
    }

    public static boolean isPlayerInATeam(EntityPlayer player)
    {
        if (player == null || player.world == null) { return false; }
        Pair<Team, Team> teams = RSSWorldData.get(player.world).getTeams();
        if (teams.getLeft() == null || teams.getRight() == null) { return false; }
        return teams.getLeft().isPlayerInTeam(player) || teams.getRight().isPlayerInTeam(player);
    }

    public static Team getPlayersTeam(EntityPlayer player)
    {
        if (player == null || player.world == null) { return null; }
        Pair<Team, Team> teams = RSSWorldData.get(player.world).getTeams();
        if (teams.getLeft() == null || teams.getRight() == null) { return null; }
        return teams.getLeft().isPlayerInTeam(player) ? teams.getLeft() : teams.getRight().isPlayerInTeam(player) ? teams.getRight() : null;
    }

    public static EnumSide getPlayersSide(EntityPlayer player)
    {
        Team team = getPlayersTeam(player);
        return team == null ? null : team.getSide();
    }

    public static Team getEnemyTeam(Team team, World world)
    {
        return getTeam(team.getSide().getOpposite(), world);
    }

    public static Team getTeam(EnumSide side, World world)
    {
        return side == EnumSide.ATTACKER ? getAttackers(world) : getDeffenders(world);
    }

    public static Team getAttackers(World world)
    {
        Pair<Team, Team> teams = RSSWorldData.get(world).getTeams();
        if (teams.getLeft() == null || teams.getRight() == null) { return null; }
        if (teams.getLeft().getSide() == EnumSide.ATTACKER) { return teams.getLeft(); }
        return teams.getRight();
    }

    public static Team getDeffenders(World world)
    {
        Pair<Team, Team> teams = RSSWorldData.get(world).getTeams();
        if (teams.getLeft() == null || teams.getRight() == null) { return null; }
        if (teams.getLeft().getSide() == EnumSide.DEFFENDER) { return teams.getLeft(); }
        return teams.getRight();
    }

    public static boolean doTeamsExist(World world)
    {
        Pair<Team, Team> teams = RSSWorldData.get(world).getTeams();
        return teams.getLeft() != null && teams.getRight() != null;
    }

    public static EntityPlayer getPlayerForOperator(World world, EnumOperator operator)
    {
        Team team;
        if (operator.getSide() == EnumSide.DEFFENDER) { team = getDeffenders(world); }
        else { team = getAttackers(world); }
        if (team != null)
        {
            for (UUID uuid : team.getPlayerEntityMap().keySet())
            {
                EntityPlayer player = team.getPlayerEntityMap().get(uuid).get();
                if (player != null && getPlayersOperator(player) == operator && isPlayerAlive(player))
                {
                    return player;
                }
            }
        }
        return null;
    }

    public static boolean doesOperatorExist(World world, EnumOperator operator)
    {
        Team team;
        if (operator.getSide() == EnumSide.DEFFENDER) { team = getDeffenders(world); }
        else { team = getAttackers(world); }
        if (team != null)
        {
            for (UUID uuid : team.getPlayerEntityMap().keySet())
            {
                EntityPlayer player = team.getPlayerEntityMap().get(uuid).get();
                if (player != null && getPlayersOperator(player) == operator)
                {
                    return isPlayerAlive(player);
                }
            }
        }
        return false;
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean isPlayerWearingRookArmor(EntityPlayer player)
    {
        ItemStack stack = player.inventory.armorInventory[2];
        if (stack != null && stack.getItem() instanceof ItemOperatorArmor)
        {
            return stack.hasTagCompound() && stack.getTagCompound().getBoolean("rook");
        }
        return true;
    }

    @SuppressWarnings("ConstantConditions")
    public static EnumOperator getPlayersOperator(EntityPlayer player)
    {
        if (player == null) { return null; }
        EnumOperator op = null;
        ItemStack stack = player.inventory.armorInventory[2];
        if (stack != null && stack.getItem() instanceof ItemOperatorArmor && stack.hasTagCompound())
        {
            op = EnumOperator.valueOf(stack.getTagCompound().getInteger("operator"));
        }
        return op;
    }

    public static boolean isOperatorSpotted(EntityPlayer player, Team enemy)
    {
        return false;
    }

    /**
     * @return true if the game mode is secure area and the player is in the objective
     */
    public static boolean isPlayerInObjectiveArea(EntityPlayer player)
    {
        //TODO: implement check for biohazard container and player position, needs the game manager
        return false;
    }

    /**
     * @return an instance of {@code TileEntityBiohazardContainer} if the game mode is secure are, otherwise {@code null}
     */
    public static TileEntityBioContainer getBiohazardContainer(World world)
    {
        //TODO: get the biohazard container from the game manager
        return null;
    }

    public static boolean canShowBombInfo(EntityPlayer player)
    {
        if (player == null || player.world == null) { return false; }
        return doAttackersCarryDefuser(player.world)/* || RSSWorldData.get(player.worldObj).getGameManager().isDefuserPlanted()*/; //TODO: enable when game manager exists
    }

    public static boolean doAttackersCarryDefuser(World world)
    {
        Team team = getAttackers(world);
        if (team == null) { return false; }
        for (UUID uuid : team.getPlayerEntityMap().keySet())
        {
            if (team.getPlayerEntityMap().get(uuid).get() == null) { continue; }
            if (doesPlayerCarryDefuser(team.getPlayerEntityMap().get(uuid).get()))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean doesPlayerCarryDefuser(EntityPlayer player)
    {
        return Utils.getSlotFor(player.inventory.mainInventory, DEFUSER) != -1;
    }

    public static TileEntityBomb getBomb(World world, int location)
    {
        //TODO: get the bomb from the game manager
        //return null;
        return (TileEntityBomb) world.getTileEntity(new BlockPos(1112, 4, -415));
    }

    public static boolean haveDeffendersFoundDefuser(World world)
    {
        //TODO: find a way to efficiently check for line of sight to the defuser.
        return true;
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean isPlayerDBNO(EntityPlayer player)
    {
        return player.getCapability(DBNOHandlerStorage.DBNO_HANDLER_CAPABILITY, null).isDBNO();
    }

    public static boolean isShieldHolder(Entity entity)
    {
        if (entity instanceof EntityPlayer)
        {
            //TODO: improve for shield on back when shields are fully implemented
            EntityPlayer player = (EntityPlayer)entity;
            return player.getHeldItemOffhand() != null && player.getHeldItemOffhand().getItem() instanceof ItemShield;
        }
        return false;
    }

    public static float getDmgModified(Entity entity, float dmg, Position pos)
    {
        if (!(entity instanceof EntityPlayer)) { return dmg; }
        //TODO: implement shield damage reduction
        boolean looksAtExplosion = false;
        if (looksAtExplosion)
        {
            return dmg * (10F/24F);
        }
        return dmg;
    }

    public static boolean canSeeEntity(Entity throwable, EntityLivingBase victim)
    {
        if (victim.canEntityBeSeen(throwable)) { return true; }
        Vec3d start = new Vec3d(throwable.posX, throwable.posY + (double)throwable.getEyeHeight(), throwable.posZ);
        Vec3d end = new Vec3d(victim.posX, victim.posY + (double)victim.getEyeHeight(), victim.posZ);
        RayTraceResult result = throwable.world.rayTraceBlocks(start, end);
        if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            //TODO: check the usefulness of this
        }
        return false;
    }

    public static boolean arePlayersTeamMates(EntityPlayer player1, EntityPlayer player2)
    {
        if (player1 == null || player2 == null) { return false; }
        if (!isPlayerInATeam(player1) || !isPlayerInATeam(player2)) { return false; }
        return getPlayersTeam(player1) == getPlayersTeam(player2);
    }

    public static boolean isGameRunning()
    {
        //TODO: implement
        return true;
    }

    public static boolean shouldReloadPartialMags(EntityPlayer player)
    {
        //TODO: implement when rule set is implemented
        return true;
    }

    public static int getPoints(EntityPlayer player)
    {
        //TODO: implement
        return 0;
    }

    public static int getKills(EntityPlayer player)
    {
        //TODO: implement
        return 0;
    }

    public static int getAssists(EntityPlayer player)
    {
        //TODO: implement
        return 0;
    }

    public static int getDeaths(EntityPlayer player)
    {
        //TODO: implement
        return 0;
    }
}