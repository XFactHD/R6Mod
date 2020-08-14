package xfacthd.r6mod.common.blocks.gadgets;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import xfacthd.r6mod.common.blocks.building.BlockBarricade;
import xfacthd.r6mod.common.data.ItemGroups;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.items.gadgets.BlockItemToughBarricade;
import xfacthd.r6mod.common.tileentities.gadgets.TileEntityToughBarricade;
import xfacthd.r6mod.common.util.data.PointManager;

import javax.annotation.Nullable;

public class BlockToughBarricade extends BlockBarricade
{
    public BlockToughBarricade()
    {
        super("block_tough_barricade",
                Properties.create(Material.IRON)
                        .notSolid()
                        .hardnessAndResistance(5.0F, 3.0F)
                        .sound(SoundType.METAL),
                ItemGroups.GADGETS);
    }

    @Override
    protected BlockItem createBlockItem(Item.Properties props) { return new BlockItemToughBarricade(this, props); }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityToughBarricade && placer instanceof PlayerEntity)
        {
            ((TileEntityToughBarricade)te).setOwner((PlayerEntity) placer);
        }
    }

    @Override
    public void destroy(World world, BlockPos pos, BlockState state, PlayerEntity player, EnumGadget source, Direction side)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityToughBarricade)
        {
            PlayerEntity owner = ((TileEntityToughBarricade)te).getOwner();
            String team = owner != null && owner.getTeam() != null ? owner.getTeam().getName() : "null";
            PointManager.awardGadgetDestroyed(EnumGadget.BARRICADE, player, team);
        }
        onReplaced(state, world, pos, Blocks.AIR.getDefaultState(), false);
    }

    @Override
    public void onRemovedByCrowbar(World world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityToughBarricade) { ((TileEntityToughBarricade)te).returnToOwner(); }
    }

    @Override
    public boolean hasTileEntity(BlockState state) { return true; }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new TileEntityToughBarricade(); }
}