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

package XFactHD.rssmc.common.items.itemBlocks;

import XFactHD.rssmc.api.item.IItemAnimationHandler;
import XFactHD.rssmc.api.item.IItemUsageTimer;
import XFactHD.rssmc.common.blocks.BlockBase;
import XFactHD.rssmc.common.blocks.building.BlockBarricade;
import XFactHD.rssmc.common.blocks.gadget.BlockDeployableShield;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;

@SuppressWarnings("ConstantConditions") //TODO: implement placement animation FIXME: can't be placed in BlockWall
public class ItemBlockBarricade extends ItemBlockBase implements IItemUsageTimer//, IItemAnimationHandler
{
    public ItemBlockBarricade(BlockBase block)
    {
        super(block);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (world.getBlockState(pos).getBlock() instanceof BlockBarricade || side == EnumFacing.UP) { return EnumActionResult.FAIL; }

        //Check if we need to recheck if the block can be placed
        if (stack.getTagCompound().hasKey("lastPos") && stack.getTagCompound().getLong("lastPos") != pos.toLong())
        {
            if (!world.isRemote)
            {
                stack.getTagCompound().setLong("time", -1);
                stack.getTagCompound().setLong("lastClick", 0);
                stack.getTagCompound().removeTag("lastPos");
                player.inventory.markDirty();
            }
            return EnumActionResult.FAIL;
        }

        //If the item is reset, check if the block can be placed
        if (!stack.getTagCompound().hasKey("lastPos"))
        {
            Pair<Boolean, Boolean> spaceAndLarge = hasEnoughSpaceAndIsLarge(world, pos, side, player.getHorizontalFacing());
            if (!spaceAndLarge.getLeft())
            {
                if (!world.isRemote)
                {
                    stack.getTagCompound().setLong("time", -1);
                    stack.getTagCompound().setLong("lastClick", 0);
                    stack.getTagCompound().removeTag("lastPos");
                    player.inventory.markDirty();
                }
                return EnumActionResult.FAIL;
            }
        }

        //If the last BlockPos is not set, set it
        if (!world.isRemote && !stack.getTagCompound().hasKey("lastPos"))
        {
            stack.getTagCompound().setLong("lastPos", pos.toLong());
            player.inventory.markDirty();
        }

        if (!world.isRemote)
        {
            //Set lastClick to current time
            if (stack.getTagCompound().getLong("time") == -1) { stack.getTagCompound().setLong("time", world.getTotalWorldTime()); }
            stack.getTagCompound().setLong("lastClick", world.getTotalWorldTime());
            player.inventory.markDirty();

            //If the time has come, place blocks if possible
            if (world.getTotalWorldTime() - stack.getTagCompound().getLong("time") >= getMaxTime(stack))
            {
                //Place bottom blocks
                Pair<Boolean, Boolean> spaceAndLarge =hasEnoughSpaceAndIsLarge(world, pos, side, player.getHorizontalFacing());
                boolean large = spaceAndLarge.getRight();
                boolean window = side.getAxis().isHorizontal();
                if (!window) { pos = pos.down(); }
                EnumFacing facing = window ? side : player.getHorizontalFacing();
                boolean door = !window && isDoor(world, pos, player.getHorizontalFacing(), large);

                IBlockState state = block.getDefaultState()
                        .withProperty(PropertyHolder.FACING_CARDINAL, facing.getOpposite())
                        .withProperty(PropertyHolder.TOP, true)
                        .withProperty(PropertyHolder.WINDOW, window)
                        .withProperty(PropertyHolder.LARGE, large)
                        .withProperty(PropertyHolder.DOOR, door);
                boolean placed = placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, state.withProperty(PropertyHolder.TOP, true));
                if (large)
                {
                    placed = placed && placeBlockAt(stack, player, world, pos.offset(facing.rotateYCCW()), side, hitX, hitY, hitZ, state.withProperty(PropertyHolder.RIGHT, true));
                    placed = placed && placeBlockAt(stack, player, world, pos.offset(facing.rotateY()), side, hitX, hitY, hitZ, state.withProperty(PropertyHolder.LEFT, true));
                }

                state = state.withProperty(PropertyHolder.TOP, false);
                placed = placed && placeBlockAt(stack, player, world, pos.down(), side, hitX, hitY, hitZ, state);
                if (large)
                {
                    placed = placed && placeBlockAt(stack, player, world, pos.down().offset(facing.rotateYCCW()), side, hitX, hitY, hitZ, state.withProperty(PropertyHolder.RIGHT, true));
                    placed = placed && placeBlockAt(stack, player, world, pos.down().offset(facing.rotateY()), side, hitX, hitY, hitZ, state.withProperty(PropertyHolder.LEFT, true));
                }
                if (placed)
                {
                    if (shouldDecreaseStackSize()) { stack.stackSize -= 1; player.inventory.markDirty(); }
                }
                stack.getTagCompound().setLong("time", -1);
                stack.getTagCompound().setLong("lastClick", 0);
                stack.getTagCompound().removeTag("lastPos");
                player.inventory.markDirty();
            }
        }
        return EnumActionResult.SUCCESS;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected)
    {
        if (!(entity instanceof EntityPlayer)) { return; }
        if (!stack.hasTagCompound()) { stack.setTagCompound(new NBTTagCompound()); ((EntityPlayer)entity).inventory.markDirty(); }
        if (world.isRemote) { return; }
        if (world.getTotalWorldTime() - stack.getTagCompound().getLong("lastClick") > 4 && stack.getTagCompound().getLong("time") != -1)
        {
            stack.getTagCompound().setLong("time", -1);
            stack.getTagCompound().setLong("lastClick", 0);
            stack.getTagCompound().removeTag("lastPos");
            ((EntityPlayer)entity).inventory.markDirty();
        }
    }

    @Override
    public int getCurrentTime(World world, ItemStack stack, EntityPlayer player)
    {
        if (!stack.hasTagCompound() || !isInUse(world, stack, player)) { return -1; }
        return (int) (world.getTotalWorldTime() - stack.getTagCompound().getLong("time"));
    }

    @Override
    public int getMaxTime(ItemStack stack)
    {
        return 40;
    }

    @Override
    public String getDescription()
    {
        return "desc.rssmc:place_block_barricade.name";
    }

    @Override
    public boolean isInUse(World world, ItemStack stack, EntityPlayer player)
    {
        return stack.hasTagCompound() && stack.getTagCompound().getLong("time") != -1;
    }

    private Pair<Boolean, Boolean> hasEnoughSpaceAndIsLarge(World world, BlockPos pos, EnumFacing side, EnumFacing playerFacing)
    {
        boolean window = side.getAxis().isHorizontal();
        boolean large = true;

        if (!window) { pos = pos.down(); }
        EnumFacing facing = window ? side : playerFacing;

        ArrayList<BlockPos> glassPos = new ArrayList<>();
        for (BlockPos bp : BlockPos.getAllInBox(pos.offset(facing.rotateY()), pos.down().offset(facing.rotateYCCW())))
        {
            glassPos.add(bp);
            IBlockState state = world.getBlockState(bp);
            if ((window && state.getBlock() != Blocks.GLASS_PANE) ||(!window && !world.isAirBlock(bp)))
            {
                //If there is not even glass or air on the two center blocks, abort early
                if (bp.equals(pos) || bp.equals(pos.down()))
                { return Pair.of(false, false); }
                large = false;
                break;
            }
            if (!isConsideredAir(world.getBlockState(bp.offset(facing))) || !isConsideredAir(world.getBlockState(bp.offset(facing.getOpposite()))))
            {
                //Door can't breath, abort early
                return Pair.of(false, false);
            }
        }
        if (!large) { glassPos.clear(); glassPos.add(pos); glassPos.add(pos.down()); }
        BlockPos rightCorner = pos.offset(facing.rotateY(), large ? 2 : 1).up();
        BlockPos leftCorner = pos.offset(facing.rotateYCCW(), large ? 2 : 1).up();
        for (BlockPos bp : BlockPos.getAllInBox(pos.offset(facing.rotateY(), large ? 2 : 1).up(), pos.offset(facing.rotateYCCW(), large ? 2 : 1).down()))
        {
            if (!glassPos.contains(bp) && !bp.equals(rightCorner) && !bp.equals(leftCorner))
            {
                IBlockState state = world.getBlockState(bp);
                if (!state.isSideSolid(world, bp, EnumFacing.UP))
                {
                    //Frame is incomplete, abort early
                    return Pair.of(false, false);
                }
            }
        }
        return Pair.of(true, large);
    }

    @SuppressWarnings("deprecation")
    private boolean isConsideredAir(IBlockState state)
    {
        if (state.getBlock() == Blocks.AIR) { return true; }
        if (state.getBlock() instanceof BlockDeployableShield) { return false; }
        if (state.getBlock() instanceof BlockBase) { return !((BlockBase)state.getBlock()).isCompleteBlock(state); }
        return !state.getBlock().isFullBlock(state) && !state.getBlock().isFullCube(state) && !state.getBlock().isOpaqueCube(state);
    }

    private boolean isDoor(World world, BlockPos pos, EnumFacing facing, boolean large)
    {
        boolean door = true;
        pos = pos.down(2);

        if (!world.getBlockState(pos.offset(facing)).isSideSolid(world, pos.offset(facing), EnumFacing.UP)) { door = false; }
        if (!world.getBlockState(pos.offset(facing.getOpposite())).isSideSolid(world, pos.offset(facing.getOpposite()), EnumFacing.UP)) { door = false; }

        if (large)
        {
            pos = pos.offset(facing.rotateY()).offset(facing);
            if (!world.getBlockState(pos).isSideSolid(world, pos, EnumFacing.UP)) { door = false; }
            pos = pos.offset(facing.rotateY()).offset(facing.getOpposite());
            if (!world.getBlockState(pos).isSideSolid(world, pos, EnumFacing.UP)) { door = false; }

            pos = pos.offset(facing.rotateYCCW()).offset(facing);
            if (!world.getBlockState(pos).isSideSolid(world, pos, EnumFacing.UP)) { door = false; }
            pos = pos.offset(facing.rotateYCCW()).offset(facing.getOpposite());
            if (!world.getBlockState(pos).isSideSolid(world, pos, EnumFacing.UP)) { door = false; }
        }

        return door;
    }

    protected boolean shouldDecreaseStackSize()
    {
        return false;
    }
}