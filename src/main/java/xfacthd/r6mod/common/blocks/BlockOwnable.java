package xfacthd.r6mod.common.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xfacthd.r6mod.common.tileentities.TileEntityOwnable;

import javax.annotation.Nullable;

public abstract class BlockOwnable extends BlockBase
{
    public BlockOwnable(String name, Properties props, ItemGroup group) { super(name, props, group); }

    @Override
    public final BlockState getStateForPlacement(BlockItemUseContext context)
    {
        if (context.getPlayer() == null) { return null; } //Can only be placed by a player
        return getStateForPlacementOwnable(context);
    }

    protected BlockState getStateForPlacementOwnable(BlockItemUseContext context) { return getDefaultState(); }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        TileEntity te = world.getTileEntity(pos);
        if (placer instanceof PlayerEntity && te instanceof TileEntityOwnable)
        {
            ((TileEntityOwnable) te).setOwner((PlayerEntity)placer);
        }
    }

    @Override
    public final boolean hasTileEntity(BlockState state) { return true; }
}