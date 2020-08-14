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

package XFactHD.rssmc.common.blocks.building;

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.api.block.HitType;
import XFactHD.rssmc.api.block.IDestructable;
import XFactHD.rssmc.api.block.IShootable;
import XFactHD.rssmc.common.Content;
import XFactHD.rssmc.common.blocks.BlockBase;
import XFactHD.rssmc.common.blocks.gadget.TileEntityToughBarricade;
import XFactHD.rssmc.common.data.EnumGadget;
import XFactHD.rssmc.common.items.itemBlocks.ItemBlockBarricade;
import XFactHD.rssmc.common.items.itemBlocks.ItemBlockBase;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class BlockBarricade extends BlockBase implements IDestructable, IShootable
{
    public BlockBarricade(String name, Material material, CreativeTabs creativeTab, Class<? extends ItemBlockBase> itemBlockClass)
    {
        super(name, material, creativeTab, itemBlockClass, null);
    }

    public BlockBarricade()
    {
        super("blockBarricade", Material.WOOD, RainbowSixSiegeMC.CT.buildingTab, ItemBlockBarricade.class, null);
        setDefaultState(super.getDefaultState().withProperty(PropertyHolder.TOP, false).withProperty(PropertyHolder.WINDOW, false)
                .withProperty(PropertyHolder.FACING_CARDINAL, EnumFacing.NORTH).withProperty(PropertyHolder.DOOR, false).withProperty(PropertyHolder.LARGE, false)
                .withProperty(PropertyHolder.RIGHT, false).withProperty(PropertyHolder.LEFT, false));
        setSoundType(SoundType.WOOD);
        registerTileEntity(TileEntityBarricade.class, "Barricade");
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PropertyHolder.TOP, PropertyHolder.FACING_CARDINAL, PropertyHolder.WINDOW, PropertyHolder.DOOR,
                                             PropertyHolder.LARGE, PropertyHolder.RIGHT, PropertyHolder.LEFT);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(PropertyHolder.FACING_CARDINAL).getIndex() + (state.getValue(PropertyHolder.TOP) ? 10 : 0);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        boolean top = false;
        if (meta > 5)
        {
            meta -= 10;
            top = true;
        }
        if (meta == 0 || meta == 1)
        {
            meta = EnumFacing.NORTH.getIndex();
        }
        return getDefaultState().withProperty(PropertyHolder.FACING_CARDINAL, EnumFacing.getFront(meta)).withProperty(PropertyHolder.TOP, top);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        boolean window = false;
        boolean door = false;
        boolean large = false;
        boolean right = false;
        boolean left = false;
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityBarricade)
        {
            window = ((TileEntityBarricade)te).isWindow();
            door = ((TileEntityBarricade)te).isDoor();
            large = ((TileEntityBarricade)te).isLarge();
            right = ((TileEntityBarricade)te).isRight();
            left = ((TileEntityBarricade)te).isLeft();
        }
        return state.withProperty(PropertyHolder.WINDOW, window).withProperty(PropertyHolder.DOOR, door).withProperty(PropertyHolder.LARGE, large)
               .withProperty(PropertyHolder.RIGHT, right).withProperty(PropertyHolder.LEFT, left);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        onBlockDestroyedByShot(world, pos, state, null, 0, 0, 0, null);
        super.breakBlock(world, pos, state);
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos)
    {
        return true;
    }

    @Override
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side)
    {
        return side != EnumFacing.UP;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        if (world.isRemote) { return; }
        boolean window = state.getValue(PropertyHolder.WINDOW);
        boolean large = state.getValue(PropertyHolder.LARGE);
        boolean right = state.getValue(PropertyHolder.RIGHT);
        boolean left = state.getValue(PropertyHolder.LEFT);
        boolean door = state.getValue(PropertyHolder.DOOR);
        setTileValues(world.getTileEntity(pos), (EntityPlayer) placer, large, right, left, window, !window && door);
    }

    @Override
    public void onBlockClicked(World world, BlockPos pos, EntityPlayer player)
    {
        IBlockState state = world.getBlockState(pos);
        boolean top = state.getValue(PropertyHolder.TOP);
        boolean large = state.getValue(PropertyHolder.LARGE);
        EnumFacing facing = state.getValue(PropertyHolder.FACING_CARDINAL);
        TileEntity te = world.getTileEntity(pos);
        TileEntity te2 = world.getTileEntity(top ? pos.down() : pos.up());
        TileEntity te3 = world.getTileEntity(pos.offset(facing.rotateY()));
        TileEntity te4 = world.getTileEntity(top ? pos.down().offset(facing.rotateY()) : pos.up().offset(facing.rotateY()));
        TileEntity te5 = world.getTileEntity(pos.offset(facing.rotateYCCW()));
        TileEntity te6 = world.getTileEntity(top ? pos.down().offset(facing.rotateYCCW()) : pos.up().offset(facing.rotateYCCW()));
        if (!player.isCreative() && te instanceof TileEntityBarricade && te2 instanceof TileEntityBarricade)
        {
            ((TileEntityBarricade)te).hitBarricade();
            if (((TileEntityBarricade)te).isWindow())
            {
                ((TileEntityBarricade)te).setWindow(false);
                ((TileEntityBarricade)te2).setWindow(false);
                if (large && te3 instanceof TileEntityBarricade && te4 instanceof TileEntityBarricade && te5 instanceof TileEntityBarricade && te6 instanceof TileEntityBarricade)
                {
                    ((TileEntityBarricade)te3).setWindow(false);
                    ((TileEntityBarricade)te4).setWindow(false);
                    ((TileEntityBarricade)te5).setWindow(false);
                    ((TileEntityBarricade)te6).setWindow(false);
                }
                world.playSound(null, pos, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 1, .6F);
            }

            int hits = large ? getTotalHits(te, te2, te3, te4, te5, te6) : getTotalHits(te, te2);
            if (hits >= ((TileEntityBarricade)te).getMaxHits())
            {
                if (world.getBlockState(pos).getValue(PropertyHolder.TOP))
                {
                    world.destroyBlock(pos.down(), false);
                }
                else
                {
                    world.destroyBlock(pos.up(), false);
                }
                world.destroyBlock(pos, false);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityBarricade)
        {
            if (!((TileEntityBarricade)te).isDoor() && !((TileEntityBarricade)te).isWindow() && heldItem != null && heldItem.getItem() == Item.getItemFromBlock(Blocks.GLASS_PANE))
            {
                if (state.getValue(PropertyHolder.TOP) && (!state.getValue(PropertyHolder.LARGE) || (!state.getValue(PropertyHolder.RIGHT) && !state.getValue(PropertyHolder.LEFT))))
                {
                    if (!world.isRemote)
                    {
                        EnumFacing facing = state.getValue(PropertyHolder.FACING_CARDINAL);
                        ((TileEntityBarricade)te).setWindow(true);
                        setTileWindow(world.getTileEntity(pos.down()), true);
                        if (state.getValue(PropertyHolder.LARGE))
                        {
                            setTileWindow(world.getTileEntity(pos.offset(facing.rotateY())), true);
                            setTileWindow(world.getTileEntity(pos.down().offset(facing.rotateY())), true);
                            setTileWindow(world.getTileEntity(pos.offset(facing.rotateYCCW())), true);
                            setTileWindow(world.getTileEntity(pos.down().offset(facing.rotateYCCW())), true);
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileEntityBarricade)) { return Block.FULL_BLOCK_AABB; }
        boolean window = ((TileEntityBarricade)te).isWindow();
        boolean door = ((TileEntityBarricade)te).isDoor();
        boolean top = state.getValue(PropertyHolder.TOP);
        switch (state.getValue(PropertyHolder.FACING_CARDINAL))
        {
            case NORTH: return window ? new AxisAlignedBB(0, 0, 7F/16F, 1, 1, 11F/16F) : door ? new AxisAlignedBB(0, top ? 0 : .1875, .4375, 1, 1, .5625) : new AxisAlignedBB(0, 0, 9F/16F, 1, 1, 11F/16F);
            case SOUTH: return window ? new AxisAlignedBB(0, 0, 5F/16F, 1, 1,  9F/16F) : door ? new AxisAlignedBB(0, top ? 0 : .1875, .4375, 1, 1, .5625) : new AxisAlignedBB(0, 0, 5F/16F, 1, 1, 7F/16F);
            case EAST:  return window ? new AxisAlignedBB(5F/16F, 0, 0,  9F/16F, 1, 1) : door ? new AxisAlignedBB(.4375, top ? 0 : .1875, 0, .5625, 1, 1) : new AxisAlignedBB(5F/16F, 0, 0, 7F/16F, 1, 1);
            case WEST:  return window ? new AxisAlignedBB(7F/16F, 0, 0, 11F/16F, 1, 1) : door ? new AxisAlignedBB(.4375, top ? 0 : .1875, 0, .5625, 1, 1) : new AxisAlignedBB(9F/16F, 0, 0, 11F/16F, 1, 1);
            default: return super.getCollisionBoundingBox(state, world, pos);
        }
    }

    @Override
    public boolean isCompleteBlock(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer)
    {
        return layer == BlockRenderLayer.CUTOUT || layer == BlockRenderLayer.SOLID;
    }

    @Override
    public boolean isUnbreakableInSurvivalMode(IBlockState state)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        if (state.getBlock() == Content.blockToughBarricade)
        {
            return new TileEntityToughBarricade();
        }
        return new TileEntityBarricade();
    }

    @Override
    public boolean canAttachGagdet(World world, BlockPos pos, IBlockState state, EnumGadget gadget, EnumFacing side)
    {
        EnumFacing facing = state.getValue(PropertyHolder.FACING_CARDINAL);
        boolean sideConform = side == facing || side == facing.getOpposite();
        return (gadget == EnumGadget.CLUSTER_CHARGE || gadget == EnumGadget.BREACH_CHARGE || gadget == EnumGadget.THERMITE_CHARGE) && sideConform;
    }

    @Override
    public void explode(World world, BlockPos pos, IBlockState state, EnumGadget gadget, EnumFacing originatingSide)
    {
        onBlockDestroyedByPlayer(world, pos, state);
        world.setBlockToAir(pos);
    }

    @Override
    public boolean destruct(World world, BlockPos pos, IBlockState state, HitType type, EnumFacing originatingSide)
    {
        if (type == HitType.HAMMER || type == HitType.IMPACT_GRENADE || type == HitType.C4)
        {
            onBlockDestroyedByPlayer(world, pos, state);
            world.setBlockToAir(pos);
            return true;
        }
        return false;
    }

    @Override
    public boolean onBlockShot(World world, BlockPos pos, IBlockState state, EntityPlayer player, float hitX, float hitY, float hitZ, EnumFacing sideHit)
    {
        if (world.isRemote) { return false; }
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityBarricade)
        {
            if (((TileEntityBarricade)te).getMaxHits() == -1) { return false; }
            ((TileEntityBarricade)te).shootBarricade();
            if (((TileEntityBarricade)te).getShots() >= ((TileEntityBarricade)te).getMaxShots())
            {
                return onBlockDestroyedByShot(world, pos, state, player, hitX, hitY, hitZ, sideHit);
            }
        }
        return false;
    }

    @Override
    public boolean onBlockDestroyedByShot(World world, BlockPos pos, IBlockState state, EntityPlayer player, float hitX, float hitY, float hitZ, EnumFacing sideHit)
    {
        state = getActualState(state, world, pos);
        EnumFacing facing = state.getValue(PropertyHolder.FACING_CARDINAL);
        boolean large = state.getValue(PropertyHolder.LARGE);
        boolean right = state.getValue(PropertyHolder.RIGHT);
        boolean left = state.getValue(PropertyHolder.LEFT);
        boolean window = state.getValue(PropertyHolder.WINDOW);
        if (window) { world.playSound(null, pos, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 1, .6F); }

        if (state.getValue(PropertyHolder.TOP))
        {
            world.destroyBlock(pos.down(), false);
            if (large)
            {
                if (left)
                {
                    world.destroyBlock(pos.offset(facing.rotateY()), false);
                    world.destroyBlock(pos.offset(facing.rotateY(), 2), false);
                    world.destroyBlock(pos.down().offset(facing.rotateY()), false);
                    world.destroyBlock(pos.down().offset(facing.rotateY(), 2), false);
                }
                else if (right)
                {
                    world.destroyBlock(pos.offset(facing.rotateYCCW()), false);
                    world.destroyBlock(pos.offset(facing.rotateYCCW(), 2), false);
                    world.destroyBlock(pos.down().offset(facing.rotateYCCW()), false);
                    world.destroyBlock(pos.down().offset(facing.rotateYCCW(), 2), false);
                }
                else
                {
                    world.destroyBlock(pos.offset(facing.rotateYCCW()), false);
                    world.destroyBlock(pos.offset(facing.rotateY()), false);
                    world.destroyBlock(pos.down().offset(facing.rotateYCCW()), false);
                    world.destroyBlock(pos.down().offset(facing.rotateY()), false);
                }
            }
        }
        else
        {
            world.destroyBlock(pos.up(), false);
            if (large)
            {
                if (left)
                {
                    world.destroyBlock(pos.offset(facing.rotateY()), false);
                    world.destroyBlock(pos.offset(facing.rotateY(), 2), false);
                    world.destroyBlock(pos.up().offset(facing.rotateY()), false);
                    world.destroyBlock(pos.up().offset(facing.rotateY(), 2), false);
                }
                else if (right)
                {
                    world.destroyBlock(pos.offset(facing.rotateYCCW()), false);
                    world.destroyBlock(pos.offset(facing.rotateYCCW(), 2), false);
                    world.destroyBlock(pos.up().offset(facing.rotateYCCW()), false);
                    world.destroyBlock(pos.up().offset(facing.rotateYCCW(), 2), false);
                }
                else
                {
                    world.destroyBlock(pos.offset(facing.rotateYCCW()), false);
                    world.destroyBlock(pos.offset(facing.rotateY()), false);
                    world.destroyBlock(pos.up().offset(facing.rotateYCCW()), false);
                    world.destroyBlock(pos.up().offset(facing.rotateY()), false);
                }
            }
        }
        return true;
    }

    private void setTileValues(TileEntity te, EntityPlayer owner, boolean large, boolean right, boolean left, boolean window, boolean door)
    {
        if (te instanceof TileEntityBarricade)
        {
            ((TileEntityBarricade)te).setOwner(owner);
            ((TileEntityBarricade)te).setLarge(large);
            ((TileEntityBarricade)te).setRight(right);
            ((TileEntityBarricade)te).setLeft(left);
            ((TileEntityBarricade)te).setWindow(window);
            ((TileEntityBarricade)te).setDoor(door);
        }
    }

    private void setTileWindow(TileEntity te, boolean window)
    {
        if (te instanceof TileEntityBarricade) { ((TileEntityBarricade)te).setWindow(window); }
    }

    private int getTotalHits(TileEntity... tiles)
    {
        int hits = 0;
        for (TileEntity te : tiles)
        {
            if (te instanceof TileEntityBarricade) { hits += ((TileEntityBarricade)te).getHits(); }
        }
        return hits;
    }

    private int getTotalBulletHits(TileEntity... tiles)
    {
        int hits = 0;
        for (TileEntity te : tiles)
        {
            if (te instanceof TileEntityBarricade) { hits += ((TileEntityBarricade)te).getShots(); }
        }
        return hits;
    }
}