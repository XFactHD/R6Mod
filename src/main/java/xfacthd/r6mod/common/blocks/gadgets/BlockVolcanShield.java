package xfacthd.r6mod.common.blocks.gadgets;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import xfacthd.r6mod.common.R6Content;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.tileentities.gadgets.TileEntityVolcanShield;
import xfacthd.r6mod.common.tileentities.misc.TileEntityFakeFire;
import xfacthd.r6mod.common.util.Utils;
import xfacthd.r6mod.common.util.damage.DamageSourceGadget;
import xfacthd.r6mod.common.util.data.PointManager;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class BlockVolcanShield extends BlockDeployableShield
{
    private static final Predicate<Entity> DAMAGE_PREDICATE = (e) -> e instanceof LivingEntity && e.isAlive();

    public BlockVolcanShield() { super("block_volcan_shield", EnumGadget.VOLCAN_SHIELD); }

    @Override
    public void destroy(World world, BlockPos pos, BlockState state, PlayerEntity player, EnumGadget source, Direction side)
    {
        PlayerEntity owner = null;

        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityVolcanShield)
        {
            owner = ((TileEntityVolcanShield)te).getOwner();

            AxisAlignedBB aabb = new AxisAlignedBB(pos.north().east(), pos.south().west());
            DamageSource damage = new DamageSourceGadget(owner, "r6mod.fire", EnumGadget.VOLCAN_SHIELD);

            world.getEntitiesInAABBexcluding(null, aabb, DAMAGE_PREDICATE).forEach(entity -> entity.attackEntityFrom(damage, 34F/5F));

            PointManager.awardGadgetDestroyed(EnumGadget.VOLCAN_SHIELD, player);
        }

        world.destroyBlock(pos, false);

        if (owner != null)
        {
            PlayerEntity finalOwner = owner;
            BlockPos.getAllInBox(pos.north().east(), pos.south().west()).forEach((adjPos) ->
            {
                if (world.isAirBlock(adjPos))
                {
                    BlockState fireState = R6Content.blockFakeFire.getDefaultState();
                    world.setBlockState(adjPos, fireState);
                    world.getBlockState(adjPos).getBlock().onBlockPlacedBy(world, adjPos, fireState, finalOwner, ItemStack.EMPTY);

                    TileEntity fireTile = world.getTileEntity(adjPos);
                    if (fireTile instanceof TileEntityFakeFire)
                    {
                        ((TileEntityFakeFire) fireTile).configure(240, 7, 12F / 5F, EnumGadget.VOLCAN_SHIELD);
                    }
                }
            });
        }
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new TileEntityVolcanShield(); }

    @Override
    protected Map<Direction, VoxelShape> createShapes()
    {
        Map<Direction, VoxelShape> shapes = new HashMap<>();

        VoxelShape shape = VoxelShapes.combineAndSimplify(
                makeCuboidShape(0.0, 0.0, 1.0, 16.0, 16.0, 5.5),
                makeCuboidShape(5.0, 9.0, 3.0, 11.0, 15.0, 6.0),
                IBooleanFunction.OR
        );

        shapes.put(Direction.NORTH, shape);
        shapes.put(Direction.EAST,  Utils.rotateShape(Direction.NORTH, Direction.EAST,  shape));
        shapes.put(Direction.SOUTH, Utils.rotateShape(Direction.NORTH, Direction.SOUTH, shape));
        shapes.put(Direction.WEST,  Utils.rotateShape(Direction.NORTH, Direction.WEST,  shape));

        return shapes;
    }
}