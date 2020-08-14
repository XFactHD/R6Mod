package xfacthd.r6mod.common.tileentities.misc;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import xfacthd.r6mod.common.container.ContainerTeamSpawn;
import xfacthd.r6mod.common.data.types.TileEntityTypes;
import xfacthd.r6mod.common.tileentities.TileEntityBase;
import xfacthd.r6mod.common.util.data.MatchManager;
import xfacthd.r6mod.common.util.data.R6WorldSavedData;

import javax.annotation.Nullable;

public class TileEntityTeamSpawn extends TileEntityBase implements INamedContainerProvider
{
    private String team = "null";
    private boolean locked = false;

    public TileEntityTeamSpawn() { super(TileEntityTypes.tileTypeTeamSpawn); }

    @SuppressWarnings("ConstantConditions")
    public boolean setTeam(String newTeam)
    {
        if (!world.isRemote)
        {
            //Can't change associated team if this is locked
            if (locked) { return false; }

            MatchManager matches = R6WorldSavedData.get((ServerWorld)world).getMatchManager();

            ScorePlayerTeam scoreTeam;

            //Check if the new team already has a spawn
            if (!newTeam.equals("null"))
            {
                scoreTeam = world.getScoreboard().getTeam(newTeam);
                if (matches.hasSpawn(scoreTeam)) { return false; } //Can't associate multiple spawns with one team
            }

            //Remove this from the team that currently uses it as a spawn
            if (!team.equals("null"))
            {
                scoreTeam = world.getScoreboard().getTeam(this.team);
                if (scoreTeam != null && matches.hasSpawn(scoreTeam)) { matches.removeSpawn(scoreTeam); }
            }

            if (!newTeam.equals("null"))
            {
                scoreTeam = world.getScoreboard().getTeam(newTeam);
                if (scoreTeam != null) { matches.setSpawn(scoreTeam, pos.up()); }
            }

            this.team = newTeam;
            return true;
        }
        return false;
    }

    public void removeSpawn()
    {
        if (locked) { return; }
        setTeam("null"); //INFO: calling set team with "null", only unregisters the current team
    }

    public void lock() { this.locked = true; }

    public void unlock() { this.locked = false; }

    public boolean isLocked() { return locked; }

    public String getTeam() { return team; }

    @Override
    public void writeNetworkNBT(CompoundNBT nbt) { }

    @Override
    public void readNetworkNBT(CompoundNBT nbt) { }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        nbt.putString("team", team);
        nbt.putBoolean("locked", locked);
        return super.write(nbt);
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        super.read(nbt);
        team = nbt.getString("team");
        locked = nbt.getBoolean("locked");
    }

    @Override
    public ITextComponent getDisplayName() { return new TranslationTextComponent("gui.r6mod.team_spawn"); }

    @Nullable
    @Override
    @SuppressWarnings("ConstantConditions")
    public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
    {
        return new ContainerTeamSpawn(windowId, world, pos);
    }
}