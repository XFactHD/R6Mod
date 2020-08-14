package xfacthd.r6mod.common.blocks.misc;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.IBlockReader;
import xfacthd.r6mod.common.blocks.BlockOwnable;
import xfacthd.r6mod.common.tileentities.misc.TileEntityFakeFire;

@SuppressWarnings("deprecation")
public class BlockFakeFire extends BlockOwnable
{
    public BlockFakeFire()
    {
        super("block_fake_fire",
                Properties.create(Material.FIRE)
                        .notSolid()
                        .noDrops()
                        .doesNotBlockMovement()
                        .hardnessAndResistance(0)
                        .tickRandomly()
                        .setLightLevel((state) -> 15),
                null);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new TileEntityFakeFire(); }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
    {
        return VoxelShapes.empty();
    }
}