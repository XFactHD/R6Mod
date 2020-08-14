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
import XFactHD.rssmc.common.items.itemBlocks.ItemBlockWall;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import XFactHD.rssmc.common.utils.propertyEnums.EnumMaterial;
import XFactHD.rssmc.common.utils.propertyEnums.WallType;
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
public class BlockWall extends BlockBase implements IDestructable
{
    private static final AxisAlignedBB NORMAL_AABB_NS = new AxisAlignedBB(0, 0, 4F/16F, 1, 1, 12F/16F);
    private static final AxisAlignedBB NORMAL_AABB_EW = new AxisAlignedBB(4F/16F, 0, 0, 12F/16F, 1, 1);
    private static final AxisAlignedBB DESTROYED_AABB_NS = new AxisAlignedBB(0, 0, 5F/16F, 1, 1, 11F/16F);
    private static final AxisAlignedBB DESTROYED_AABB_EW = new AxisAlignedBB(5F/16F, 0, 0, 11F/16F, 1, 1);

    public BlockWall()
    {
        super("blockWall", Material.WOOD, RainbowSixSiegeMC.CT.buildingTab, ItemBlockWall.class, null);
        registerTileEntity(TileEntityWall.class, "Wall");
        setDefaultState(super.getDefaultState().withProperty(PropertyHolder.DESTROYED, false));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PropertyHolder.FACING_NE, PropertyHolder.WALL_MATERIAL, PropertyHolder.WALL_TYPE, PropertyHolder.DESTROYED);
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list)
    {
        for (EnumMaterial material : EnumMaterial.WALL_MATERIAL)
        {
            for (WallType type : WallType.values())
            {
                ItemStack stack = new ItemStack(this, 1, EnumMaterial.WALL_MATERIAL.indexOf(material));
                NBTTagCompound nbt = new NBTTagCompound();
                nbt.setInteger("type", type.ordinal());
                stack.setTagCompound(nbt);
                list.add(stack);
            }
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing facing = EnumFacing.NORTH;
        WallType type = WallType.NORMAL;
        if (meta == 2 || meta == 4 || meta == 6)
        {
            type = WallType.values()[(meta / 2) - 1];
        }
        else if (meta == 5 || meta == 10 || meta == 15)
        {
            facing = EnumFacing.EAST;
            type = WallType.values()[(meta / 5) - 1];
        }
        return getDefaultState().withProperty(PropertyHolder.FACING_NE, facing).withProperty(PropertyHolder.WALL_TYPE, type);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(PropertyHolder.FACING_NE).getIndex() * (state.getValue(PropertyHolder.WALL_TYPE).ordinal() + 1);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        EnumMaterial material = EnumMaterial.PLASTER;
        boolean destroyed = false;
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityWall)
        {
            material = ((TileEntityWall)te).getMaterial();
            destroyed = ((TileEntityWall)te).isDestroyed();
        }
        return state.withProperty(PropertyHolder.WALL_MATERIAL, material).withProperty(PropertyHolder.DESTROYED, destroyed);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, ItemStack stack)
    {
        EnumFacing placerFacing = placer.getHorizontalFacing();
        EnumFacing facing = placerFacing == EnumFacing.NORTH || placerFacing == EnumFacing.EAST ? placerFacing : placerFacing.getOpposite();
        WallType type = stack.hasTagCompound() ? WallType.values()[stack.getTagCompound().getInteger("type")] : WallType.NORMAL;
        return getDefaultState().withProperty(PropertyHolder.FACING_NE, facing).withProperty(PropertyHolder.WALL_TYPE, type);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityWall)
        {
            ((TileEntityWall)te).setMaterial(EnumMaterial.WALL_MATERIAL.get(stack.getMetadata()));
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (heldItem != null && heldItem.getItem() instanceof ItemBlock)
        {
            Block block = ((ItemBlock)heldItem.getItem()).getBlock();
            if (block instanceof BlockBase && block != Content.blockPlaster) { return false; }
            TileEntity te = world.getTileEntity(pos);
            IBlockState itemBlockState = block.getStateFromMeta(heldItem.getMetadata());
            if (te instanceof TileEntityWall && ((TileEntityWall)te).isDestroyed() && player.isCreative() && isSameMaterial(((TileEntityWall)te).getMaterial(), itemBlockState))
            {
                if (!world.isRemote)
                {
                    ((TileEntityWall)te).setDestroyed(false);
                }
                return true;
            }
        }
        return false;
    }

    private boolean isSameMaterial(EnumMaterial material, IBlockState itemBlockState)
    {
        List<Block> blocks = Arrays.asList(Blocks.PLANKS, Blocks.STONE, Content.blockPlaster, Blocks.IRON_BLOCK);
        if (!blocks.contains(itemBlockState.getBlock())) { return false; }
        if (itemBlockState.getBlock() == Blocks.PLANKS)
        {
            return itemBlockState.getValue(BlockPlanks.VARIANT).getName().equals(material.getName());
        }
        else if (itemBlockState.getBlock() == Blocks.STONE)
        {
            return itemBlockState.getValue(BlockStone.VARIANT).getName().equals(material.getName());
        }
        else if (itemBlockState.getBlock() == Content.blockPlaster) { return material == EnumMaterial.PLASTER; }
        else if (itemBlockState.getBlock() == Blocks.IRON_BLOCK) { return material == EnumMaterial.IRON; }
        return false;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity)
    {
        boolean destroyed = false;
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityFloorPanel) { destroyed = ((TileEntityFloorPanel)te).isDestroyed(); }
        boolean north = state.getValue(PropertyHolder.FACING_NE) == EnumFacing.NORTH;
        if (destroyed)
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, north ? new AxisAlignedBB( 2.5F/16F, 0, 5F/16F,  5.5F/16F, 1, 11F/16F) : new AxisAlignedBB(5F/16F, 0,   2.5/16F, 11F/16F, 1,  5.5F/16F));
            addCollisionBoxToList(pos, entityBox, collidingBoxes, north ? new AxisAlignedBB(10.5F/16F, 0, 5F/16F, 13.5F/16F, 1, 11F/16F) : new AxisAlignedBB(5F/16F, 0, 10.5F/16F, 11F/16F, 1, 13.5F/16F));
        }
        else
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, north ? NORMAL_AABB_NS : NORMAL_AABB_EW);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos)
    {
        boolean destroyed = false;
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityWall) { destroyed = ((TileEntityWall)te).isDestroyed(); }
        EnumFacing facing = state.getValue(PropertyHolder.FACING_NE);
        return destroyed ? (facing == EnumFacing.NORTH ? DESTROYED_AABB_NS.offset(pos) : DESTROYED_AABB_EW).offset(pos) : (facing == EnumFacing.NORTH ? NORMAL_AABB_NS.offset(pos) : NORMAL_AABB_EW.offset(pos));
    }

    @Nullable
    @Override
    public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end)
    {
        boolean destroyed = false;
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityWall) { destroyed = ((TileEntityWall)te).isDestroyed(); }
        boolean north = state.getValue(PropertyHolder.FACING_NE) == EnumFacing.NORTH;
        if (!destroyed) { return rayTrace(pos, start, end, north ? NORMAL_AABB_NS : NORMAL_AABB_EW); }
        AxisAlignedBB aabbOne   = north ? new AxisAlignedBB( 2.5F/16F, 0, 5F/16F,  5.5F/16F, 1, 11F/16F) : new AxisAlignedBB(5F/16F, 0,   2.5/16F, 11F/16F, 1,  5.5F/16F);
        AxisAlignedBB aabbTwo   = north ? new AxisAlignedBB(10.5F/16F, 0, 5F/16F, 13.5F/16F, 1, 11F/16F) : new AxisAlignedBB(5F/16F, 0, 10.5F/16F, 11F/16F, 1, 13.5F/16F);
        RayTraceResult result;
        for (AxisAlignedBB aabb : Arrays.asList(aabbOne, aabbTwo))
        {
            result = rayTrace(pos, start, end, aabb);
            if (result != null) { return result; }
        }
        return null;
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        int meta;
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityWall)
        {
            meta = EnumMaterial.WALL_MATERIAL.indexOf(((TileEntityWall)te).getMaterial());
        }
        else //If no other mod fucks this up, we can expect that no tile entity means that this was called by getPickBlock() or getDrops() in BlockReinforcement
        {
            meta = EnumMaterial.WALL_MATERIAL.indexOf(state.getValue(PropertyHolder.WALL_MATERIAL));
        }
        ItemStack stack = new ItemStack(this, 1, meta);
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("type", state.getValue(PropertyHolder.WALL_TYPE).ordinal());
        stack.setTagCompound(nbt);
        return Collections.singletonList(stack);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        return getDrops(world, pos, state, 0).get(0);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityWall();
    }

    @Override
    public boolean isCompleteBlock(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean canAttachGagdet(World world, BlockPos pos, IBlockState state, EnumGadget gadget, EnumFacing side)
    {
        boolean destroyed = false;
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityWall)
        {
            destroyed = ((TileEntityWall)te).isDestroyed();
        }
        return state.getValue(PropertyHolder.WALL_TYPE) != WallType.SOLID && !destroyed && gadget == EnumGadget.BREACH_CHARGE;
    }

    @Override
    public void explode(World world, BlockPos pos, IBlockState state, EnumGadget gadget, EnumFacing originatingSide)
    {
        for (BlockPos blockPos : BlockPos.getAllInBox(pos.up().offset(originatingSide.rotateYCCW()), pos.down().offset(originatingSide.rotateY())))
        {
            IBlockState bstate = world.getBlockState(blockPos);
            if (bstate.getBlock() == this)
            {
                if (bstate.getValue(PropertyHolder.WALL_TYPE) == WallType.NORMAL)
                {
                    world.setBlockToAir(blockPos);
                }
                else
                {
                    TileEntity te = world.getTileEntity(blockPos);
                    if (te instanceof TileEntityWall)
                    {
                        ((TileEntityWall)te).setDestroyed(true);
                    }
                }
            }
        }
    }

    @Override
    public boolean destruct(World world, BlockPos pos, IBlockState state, HitType type, EnumFacing originatingSide)
    {
        if (state.getValue(PropertyHolder.WALL_TYPE) == WallType.SOLID) { return false; }
        switch (type)
        {
            case HAMMER:
            {
                EnumFacing facing = state.getValue(PropertyHolder.FACING_NE);
                if (originatingSide == facing || originatingSide == facing.getOpposite())
                {
                    explode(world, pos, state, null, originatingSide);
                    return true;
                }
                return false;
            }
            case SHOTGUN:
            {
                if (state.getValue(PropertyHolder.WALL_TYPE) == WallType.NORMAL)
                {
                    world.setBlockToAir(pos);
                }
                else
                {
                    TileEntity te = world.getTileEntity(pos);
                    if (te instanceof TileEntityWall)
                    {
                        ((TileEntityWall)te).setDestroyed(true);
                    }
                }
                return true;
            }
            case IMPACT_GRENADE:
            {
                EnumFacing facing = state.getValue(PropertyHolder.FACING_NE);
                if (originatingSide == facing || originatingSide == facing.getOpposite())
                {
                    explode(world, pos, state, null, originatingSide);
                    return true;
                }
                return false;
            }
            case C4:
            {
                EnumFacing facing = state.getValue(PropertyHolder.FACING_NE);
                if (originatingSide == facing || originatingSide == facing.getOpposite())
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