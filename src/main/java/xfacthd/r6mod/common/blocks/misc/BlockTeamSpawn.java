package xfacthd.r6mod.common.blocks.misc;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import xfacthd.r6mod.common.blocks.BlockBase;
import xfacthd.r6mod.common.data.ItemGroups;
import xfacthd.r6mod.common.tileentities.misc.TileEntityTeamSpawn;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class BlockTeamSpawn extends BlockBase
{
    public BlockTeamSpawn()
    {
        super("block_team_spawn",
                Properties.create(Material.IRON)
                .hardnessAndResistance(-1.0F, 3600000.0F)
                .noDrops(),
                ItemGroups.MISC);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult raytrace)
    {
        TileEntity te = world.getTileEntity(pos);
        if (!world.isRemote() && te instanceof TileEntityTeamSpawn)
        {
            if (((TileEntityTeamSpawn) te).isLocked())
            {
                player.sendMessage(new TranslationTextComponent("msg.r6mod.team_spawn.locked"), Util.DUMMY_UUID);
            }
            else
            {
                NetworkHooks.openGui((ServerPlayerEntity) player, (TileEntityTeamSpawn) te, pos);
            }
        }
        return super.onBlockActivated(state, world, pos, player, hand, raytrace);
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityTeamSpawn)
        {
            ((TileEntityTeamSpawn)te).removeSpawn();
        }
        super.onReplaced(state, world, pos, newState, isMoving);
    }

    @Override
    public boolean hasTileEntity(BlockState state) { return true; }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new TileEntityTeamSpawn(); }
}