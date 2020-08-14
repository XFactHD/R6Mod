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

package XFactHD.rssmc.common.blocks.gadget;

import XFactHD.rssmc.RainbowSixSiegeMC;
import XFactHD.rssmc.common.blocks.BlockGadget;
import XFactHD.rssmc.common.items.armor.ItemOperatorArmor;
import XFactHD.rssmc.common.items.itemBlocks.ItemBlockGadget;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("deprecation")
public class BlockArmorBag extends BlockGadget
{
    public BlockArmorBag()
    {
        super("blockArmorBag", Material.CLOTH, RainbowSixSiegeMC.CT.gadgetTab, null, null);
        registerSpecialItemBlock(new ItemBlockGadget(this, 50)); //TODO: consider adding placement animation while timer counts
        registerTileEntity(TileEntityArmorBag.class, "ArmorBag");
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PropertyHolder.FACING_CARDINAL, PropertyHolder.EMPTY);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        if (meta == 0 || meta == 1)
        {
            meta = EnumFacing.NORTH.getIndex();
        }
        return getDefaultState().withProperty(PropertyHolder.FACING_CARDINAL, EnumFacing.getFront(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(PropertyHolder.FACING_CARDINAL).getIndex();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        boolean empty = true;
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityArmorBag)
        {
            empty = ((TileEntityArmorBag)te).getArmorLeft() <= 0;
        }
        return state.withProperty(PropertyHolder.EMPTY, empty);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (hand == EnumHand.OFF_HAND || player instanceof FakePlayer) { return false; }
        TileEntity te = world.getTileEntity(pos);
        boolean playerHasRookArmor = hasRookArmor(player);
        boolean containsArmor = false;
        if (te instanceof TileEntityArmorBag && !playerHasRookArmor)
        {
            TileEntityArmorBag tile = (TileEntityArmorBag)te;
            containsArmor = tile.getArmorLeft() > 0;
            tile.tryEquipArmor(player);
        }
        return !playerHasRookArmor && containsArmor;
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return getDefaultState().withProperty(PropertyHolder.FACING_CARDINAL, placer.getHorizontalFacing());
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityArmorBag && stack.hasTagCompound())
        {
            //noinspection ConstantConditions
            ((TileEntityArmorBag)te).setArmorLeft(stack.getTagCompound().getInteger("armor"));
        }
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        ItemStack stack = new ItemStack(this);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityArmorBag)
        {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setInteger("armor", ((TileEntityArmorBag)te).getArmorLeft());
            stack.setTagCompound(nbt);
        }
        return stack;
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        ItemStack stack = new ItemStack(this);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityArmorBag)
        {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setInteger("armor", ((TileEntityArmorBag)te).getArmorLeft());
            stack.setTagCompound(nbt);
        }
        return Collections.singletonList(stack);
    }

    @Override
    public int getMaxHits()
    {
        return 4;
    }

    @Override
    public boolean needsSpecialDestructionHandling()
    {
        return false;
    }

    @Override
    public boolean canBePickedUp(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean canBeShocked(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isCompleteBlock(IBlockState state)
    {
        return false;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos)
    {
        switch (state.getValue(PropertyHolder.FACING_CARDINAL))
        {
            case NORTH:
            case SOUTH: return new AxisAlignedBB(0, 0, .25, 1, .25, .75);
            case EAST:
            case WEST: return new AxisAlignedBB(.25, 0, 0, .75, .25, 1);
            default: return new AxisAlignedBB(0, 0, 0, 1, 1, 1);
        }
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityArmorBag();
    }

    @SuppressWarnings("ConstantConditions")
    private boolean hasRookArmor(EntityPlayer player)
    {
        ItemStack stack = player.inventory.armorInventory[2];
        return stack != null && stack.getItem() instanceof ItemOperatorArmor && stack.hasTagCompound() && stack.getTagCompound().getBoolean("rook");
    }
}