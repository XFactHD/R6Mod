package xfacthd.r6mod.common.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import xfacthd.r6mod.common.data.types.ContainerTypes;
import xfacthd.r6mod.common.tileentities.misc.TileEntityTeamSpawn;

public class ContainerTeamSpawn extends Container
{
    private TileEntityTeamSpawn teamSpawn;

    public ContainerTeamSpawn(int id, World world, BlockPos pos)
    {
        super(ContainerTypes.containerTypeTeamSpawn, id);

        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityTeamSpawn) { teamSpawn = (TileEntityTeamSpawn) te; }
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) { return player.isCreative(); }

    public void onResult(ServerPlayerEntity player, String team)
    {
        if (teamSpawn != null)
        {
            if (!teamSpawn.setTeam(team))
            {
                player.sendMessage(new TranslationTextComponent("msg.r6mod.team_spawn.team_already_bound"), Util.DUMMY_UUID);
            }
        }
    }
}