package xfacthd.r6mod.common.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import xfacthd.r6mod.api.interaction.IDestructable;
import xfacthd.r6mod.common.data.ItemGroups;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.items.BlockItemGadget;
import xfacthd.r6mod.common.tileentities.TileEntityGadget;
import xfacthd.r6mod.common.util.data.PointManager;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public abstract class BlockGadget extends BlockOwnable implements IDestructable
{
    protected final EnumGadget gadget;

    public BlockGadget(String name, Properties props, EnumGadget gadget)
    {
        super(name, props, ItemGroups.GADGETS);
        this.gadget = gadget;
    }

    @Override
    protected BlockItem createBlockItem(Item.Properties props) { return new BlockItemGadget(this, props, gadget); }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        super.onBlockPlacedBy(world, pos, state, placer, stack);

        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityGadget && placer instanceof PlayerEntity)
        {
            ((TileEntityGadget)te).setTeam(placer.getTeam());
        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult raytrace)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityGadget && player.isCrouching())
        {
            return ((TileEntityGadget)te).pickUp(player).toActionResultType();
        }
        return super.onBlockActivated(state, world, pos, player, hand, raytrace);
    }

    @Override
    public void destroy(World world, BlockPos pos, BlockState state, PlayerEntity player, EnumGadget source, Direction side)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityGadget && player != null)
        {
            String team = ((TileEntityGadget)te).getTeam();
            PointManager.awardGadgetDestroyed(gadget, player, team);
        }

        world.destroyBlock(pos, false);
    }
}