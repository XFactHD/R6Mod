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
import XFactHD.rssmc.common.Content;
import XFactHD.rssmc.common.blocks.BlockBase;
import XFactHD.rssmc.common.data.EnumGadget;
import XFactHD.rssmc.common.items.itemBlocks.ItemBlockFloorPanel;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import XFactHD.rssmc.common.utils.propertyEnums.EnumMaterial;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockStone;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("deprecation")
public class BlockFloorPanel extends BlockBase implements IDestructable
{
    private static final AxisAlignedBB SLAB_AABB = new AxisAlignedBB(0, .5, 0, 1, 1, 1);
    private static final AxisAlignedBB BROKEN_AABB = new AxisAlignedBB(0, .5 + 1F/16F, 0, 1, 1 - 1F/16F, 1);

    public BlockFloorPanel()
    {
        super("blockFloorPanel", Material.IRON, RainbowSixSiegeMC.CT.buildingTab, ItemBlockFloorPanel.class, null);
        registerTileEntity(TileEntityFloorPanel.class, "FloorPanel");
        setDefaultState(super.getDefaultState().withProperty(PropertyHolder.DESTROYED, false));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PropertyHolder.DESTROYED, PropertyHolder.FACING_NE, PropertyHolder.FLOOR_MATERIAL, PropertyHolder.SOLID);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        boolean solid = false;
        if (meta == 4 || meta == 10)
        {
            meta /= 2;
            solid = true;
        }
        if (meta != 2 && meta != 5)
        {
            meta = EnumFacing.NORTH.getIndex();
        }
        return getDefaultState().withProperty(PropertyHolder.FACING_NE, EnumFacing.getFront(meta)).withProperty(PropertyHolder.SOLID, solid);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(PropertyHolder.FACING_NE).getIndex() * (state.getValue(PropertyHolder.SOLID) ? 2 : 1);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        EnumMaterial camo = EnumMaterial.OAK;
        boolean destroyed = false;
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityFloorPanel)
        {
            camo = ((TileEntityFloorPanel)te).getMaterial();
            destroyed = ((TileEntityFloorPanel)te).isDestroyed();
        }
        return state.withProperty(PropertyHolder.FLOOR_MATERIAL, camo).withProperty(PropertyHolder.DESTROYED, destroyed);
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list)
    {
        for (EnumMaterial type : EnumMaterial.FLOOR_MATERIAL)
        {
            ItemStack stackOne = new ItemStack(this, 1, EnumMaterial.FLOOR_MATERIAL.indexOf(type));
            NBTTagCompound nbtOne = new NBTTagCompound();
            nbtOne.setBoolean("solid", false);
            stackOne.setTagCompound(nbtOne);
            list.add(stackOne);

            ItemStack stackTwo = new ItemStack(this, 1, EnumMaterial.FLOOR_MATERIAL.indexOf(type));
            NBTTagCompound nbtTwo = new NBTTagCompound();
            nbtTwo.setBoolean("solid", true);
            stackTwo.setTagCompound(nbtTwo);
            list.add(stackTwo);
        }
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, ItemStack stack)
    {
        EnumFacing placerFacing = placer.getHorizontalFacing();
        EnumFacing facing = placerFacing == EnumFacing.NORTH || placerFacing == EnumFacing.EAST ? placerFacing : placerFacing.getOpposite();
        //noinspection ConstantConditions
        boolean solid = stack.hasTagCompound() && stack.getTagCompound().getBoolean("solid");
        return getDefaultState().withProperty(PropertyHolder.FACING_NE, facing).withProperty(PropertyHolder.SOLID, solid);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityFloorPanel)
        {
            ((TileEntityFloorPanel)te).setMaterial(EnumMaterial.FLOOR_MATERIAL.get(stack.getMetadata()));
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (heldItem != null && heldItem.getItem() instanceof ItemBlock)
        {
            Block block = ((ItemBlock)heldItem.getItem()).getBlock();
            TileEntity te = world.getTileEntity(pos);
            IBlockState itemBlockState = block.getStateFromMeta(heldItem.getMetadata());
            if (te instanceof TileEntityFloorPanel && ((TileEntityFloorPanel)te).isDestroyed() && player.isCreative() && isSameMaterial(((TileEntityFloorPanel)te).getMaterial(), itemBlockState))
            {
                if (!world.isRemote)
                {
                    ((TileEntityFloorPanel)te).setDestroyed(false);
                }
                return true;
            }
        }
        return false;
    }

    private boolean isSameMaterial(EnumMaterial material, IBlockState itemBlockState)
    {
        List<Block> blocks = Arrays.asList(Blocks.PLANKS, Blocks.STONE, Blocks.IRON_BLOCK);
        if (!blocks.contains(itemBlockState.getBlock())) { return false; }
        if (itemBlockState.getBlock() == Blocks.PLANKS)
        {
            return itemBlockState.getValue(BlockPlanks.VARIANT).getName().equals(material.getName());
        }
        else if (itemBlockState.getBlock() == Blocks.STONE)
        {
            return itemBlockState.getValue(BlockStone.VARIANT).getName().equals(material.getName());
        }
        else if (itemBlockState.getBlock() == Blocks.IRON_BLOCK) { return material == EnumMaterial.IRON; }
        return false;
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        int meta = 0;
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityFloorPanel)
        {
            meta = EnumMaterial.FLOOR_MATERIAL.indexOf(((TileEntityFloorPanel)te).getMaterial());
        }
        ItemStack stack = new ItemStack(this, 1, meta);
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setBoolean("solid", state.getValue(PropertyHolder.SOLID));
        stack.setTagCompound(nbt);
        return Collections.singletonList(stack);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        return getDrops(world, pos, state, 0).get(0);
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity)
    {
        boolean destroyed = false;
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityFloorPanel) { destroyed = ((TileEntityFloorPanel)te).isDestroyed(); }

        if (destroyed)
        {
            boolean north = state.getValue(PropertyHolder.FACING_NE) == EnumFacing.NORTH;
            addCollisionBoxToList(pos, entityBox, collidingBoxes, north ? new AxisAlignedBB(  .075, .5625, 0,  .2625, .9375, 1) : new AxisAlignedBB(0, .5625,   .075, 1, .9375,  .2625));
            addCollisionBoxToList(pos, entityBox, collidingBoxes, north ? new AxisAlignedBB(.40625, .5625, 0, .59375, .9375, 1) : new AxisAlignedBB(0, .5625, .40625, 1, .9375, .59375));
            addCollisionBoxToList(pos, entityBox, collidingBoxes, north ? new AxisAlignedBB( .7375, .5625, 0,   .925, .9375, 1) : new AxisAlignedBB(0, .5625,  .7375, 1, .9375,   .925));
        }
        else
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, SLAB_AABB);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos)
    {
        boolean destroyed = false;
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityFloorPanel)
        {
            destroyed = ((TileEntityFloorPanel)te).isDestroyed();
        }
        return destroyed ? BROKEN_AABB.offset(pos) : SLAB_AABB.offset(pos);
    }

    @Nullable
    @Override
    public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end)
    {
        boolean destroyed = false;
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityFloorPanel) { destroyed = ((TileEntityFloorPanel)te).isDestroyed(); }

        if (!destroyed) { return rayTrace(pos, start, end, SLAB_AABB); }
        boolean north = state.getValue(PropertyHolder.FACING_NE) == EnumFacing.NORTH;
        AxisAlignedBB aabbOne   = north ? new AxisAlignedBB(  .075, .5625, 0,  .2625, .9375, 1) : new AxisAlignedBB(0, .5625,   .075, 1, .9375,  .2625);
        AxisAlignedBB aabbTwo   = north ? new AxisAlignedBB(.40625, .5625, 0, .59375, .9375, 1) : new AxisAlignedBB(0, .5625, .40625, 1, .9375, .59375);
        AxisAlignedBB aabbThree = north ? new AxisAlignedBB( .7375, .5625, 0,   .925, .9375, 1) : new AxisAlignedBB(0, .5625,  .7375, 1, .9375,   .925);
        RayTraceResult result;
        for (AxisAlignedBB aabb : Arrays.asList(aabbOne, aabbTwo, aabbThree))
        {
            result = rayTrace(pos, start, end, aabb != null ? aabb : SLAB_AABB);
            if (result != null) { return result; }
        }
        return null;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityFloorPanel();
    }

    @Override
    public boolean isCompleteBlock(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        base_state = getActualState(base_state, world, pos);
        if (base_state.getValue(PropertyHolder.DESTROYED)) { return false; }
        return side == EnumFacing.UP;
    }

    @Override
    public boolean canAttachGagdet(World world, BlockPos pos, IBlockState state, EnumGadget gadget, EnumFacing side)
    {
        boolean destroyed = false;
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityFloorPanel)
        {
            destroyed = ((TileEntityFloorPanel)te).isDestroyed();
        }
        return side == EnumFacing.UP && !destroyed && !state.getValue(PropertyHolder.SOLID);
    }

    @Override
    public void explode(World world, BlockPos pos, IBlockState state, EnumGadget gadget, EnumFacing originatingSide)
    {
        for (BlockPos blockPos : BlockPos.getAllInBox(pos.north().east(), pos.south().west()))
        {
            TileEntity te = world.getTileEntity(blockPos);
            if (te instanceof TileEntityFloorPanel && !state.getValue(PropertyHolder.SOLID))
            {
                ((TileEntityFloorPanel)te).setDestroyed(true);
            }
        }
    }

    @Override
    public boolean destruct(World world, BlockPos pos, IBlockState state, HitType type, EnumFacing originatingSide)
    {
        if (state.getValue(PropertyHolder.SOLID)) { return false; }
        switch (type)
        {
            case HAMMER:
            {
                if (originatingSide == EnumFacing.UP)
                {
                    explode(world, pos, state, null, originatingSide);
                    return true;
                }
                return false;
            }
            case SHOTGUN:
            {
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof TileEntityFloorPanel)
                {
                    ((TileEntityFloorPanel)te).setDestroyed(true);
                }
                return true;
            }
            case IMPACT_GRENADE:
            {
                if (originatingSide == EnumFacing.UP || originatingSide == EnumFacing.DOWN)
                {
                    explode(world, pos, state, null, originatingSide);
                    return true;
                }
                return false;
            }
            case C4:
            {
                if (originatingSide == EnumFacing.UP || originatingSide == EnumFacing.DOWN)
                {
                    explode(world, pos, state, null, originatingSide);
                    return true;
                }
                return false;
            }
        }
        return false;
    }
}