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
import XFactHD.rssmc.api.block.IUsageTimer;
import XFactHD.rssmc.api.capability.IGunHandler;
import XFactHD.rssmc.common.Content;
import XFactHD.rssmc.common.blocks.BlockBase;
import XFactHD.rssmc.common.capability.gunHandler.GunHandlerStorage;
import XFactHD.rssmc.common.data.EnumBullet;
import XFactHD.rssmc.common.data.EnumGun;
import XFactHD.rssmc.common.data.EnumMagazine;
import XFactHD.rssmc.common.items.ammo.ItemAmmo;
import XFactHD.rssmc.common.items.ammo.ItemMagazine;
import XFactHD.rssmc.common.items.gun.ItemGun;
import XFactHD.rssmc.common.items.itemBlocks.ItemBlockBase;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;

@SuppressWarnings("deprecation")
public class BlockAmmoBox extends BlockBase implements IUsageTimer
{
    public BlockAmmoBox()
    {
        super("blockAmmoBox", Material.IRON, RainbowSixSiegeMC.CT.buildingTab, ItemBlockBase.class, null);
        registerTileEntity(TileEntityAmmoBox.class, "AmmoBox");
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PropertyHolder.FACING_CARDINAL);
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
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return getDefaultState().withProperty(PropertyHolder.FACING_CARDINAL, placer.getHorizontalFacing());
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityAmmoBox)
        {
            if (((TileEntityAmmoBox)te).getTime(player) != -1 || checkPlayerNeedsMags(player))
            {
                if (!world.isRemote)
                {
                    boolean ready = ((TileEntityAmmoBox)te).click(player);
                    if (ready)
                    { givePlayerMags(player); }
                }
                return true;
            }
            return false;
        }
        return false;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos)
    {
        switch (state.getValue(PropertyHolder.FACING_CARDINAL))
        {
            case NORTH:
            case SOUTH: return new AxisAlignedBB(0, 0, .3125, 1, .25, .6875);
            case EAST:
            case WEST: return new AxisAlignedBB(.3125, 0, 0, .6875, .25, 1);
            default: return super.getCollisionBoundingBox(state, world, pos);
        }
    }

    @Override
    public String getDescription()
    {
        return "desc.rssmc:refill_ammo.name";
    }

    @Override
    public int getCurrentTime(World world, BlockPos pos, EntityPlayer player)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityAmmoBox)
        {
            return ((TileEntityAmmoBox)te).getTime(player);
        }
        return -1;
    }

    @Override
    public int getMaxTime(World world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityAmmoBox)
        {
            return ((TileEntityAmmoBox)te).getMaxTime();
        }
        return 0;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityAmmoBox();
    }

    @Override
    public boolean isCompleteBlock(IBlockState state)
    {
        return false;
    }

    private boolean checkPlayerNeedsMags(EntityPlayer player)
    {
        HashMap<EnumGun, Integer> guns = new HashMap<>();
        for (ItemStack stack : player.inventory.mainInventory)
        {
            if (stack != null && stack.getItem() instanceof ItemGun)
            {
                EnumGun gun = EnumGun.valueOf(stack);
                int mags = 0;
                if (gun.hasMag())
                {
                    if (getGunHandler(stack).getAmmoLeft() <= 1) { mags = -1; }
                }
                else if (gun.getGunType() == EnumGun.EnumGunType.SHOTGUN || gun == EnumGun.LFP586)
                {
                    mags -= (gun.getMagCapacity() - getGunHandler(stack).getAmmoLeft());
                }
                guns.put(gun, mags);
            }
        }
        if (guns.isEmpty()) { return false; }
        for (EnumGun gun : guns.keySet())
        {
            int mags = 0;
            for (ItemStack stack : player.inventory.mainInventory)
            {
                if (stack != null && stack.getItem() instanceof ItemMagazine && gun.hasMag())
                {
                    if (EnumMagazine.values()[stack.getMetadata()] == gun.getMagazine())
                    {
                        mags += stack.stackSize;
                    }
                }
                else if (stack != null && stack.getItem() instanceof ItemAmmo)
                {
                    ItemStack ammo = gun.getMagazineStack(false);
                    if (ammo.getMetadata() == stack.getMetadata())
                    {
                        mags += stack.stackSize;
                    }
                }
            }
            guns.replace(gun, guns.get(gun) + mags);
        }
        for (EnumGun gun : guns.keySet())
        {
            if (guns.get(gun) < gun.getMagazineStack(false).stackSize) { return true; }
        }
        return false;
    }

    private void givePlayerMags(EntityPlayer player)
    {
        HashMap<EnumGun, Integer> guns = new HashMap<>();
        for (ItemStack stack : player.inventory.mainInventory)
        {
            if (stack != null && stack.getItem() instanceof ItemGun)
            {
                EnumGun gun = EnumGun.valueOf(stack);
                int mags = 0;
                if (gun.hasMag())
                {
                    if (getGunHandler(stack).getAmmoLeft() <= 1) { mags = -1; }
                }
                else if (gun.getGunType() == EnumGun.EnumGunType.SHOTGUN || gun == EnumGun.LFP586)
                {
                    mags = -(gun.getMagCapacity() - getGunHandler(stack).getAmmoLeft());
                }
                guns.put(gun, mags);
            }
        }
        if (guns.isEmpty()) { return; }
        for (EnumGun gun : guns.keySet())
        {
            int mags = 0;
            for (ItemStack stack : player.inventory.mainInventory)
            {
                if (stack != null && stack.getItem() instanceof ItemMagazine && gun.hasMag())
                {
                    if (EnumMagazine.values()[stack.getMetadata()] == gun.getMagazine())
                    {
                        mags += stack.stackSize;
                    }
                }
                else if (stack != null && stack.getItem() instanceof ItemAmmo)
                {
                    ItemStack ammo = gun.getMagazineStack(false);
                    if (ammo.getMetadata() == stack.getMetadata())
                    {
                        mags += stack.stackSize;
                    }
                }
            }
            guns.replace(gun, guns.get(gun) + mags);
        }
        for (EnumGun gun : guns.keySet())
        {
            int amount = gun.getAdditionalMags() - guns.get(gun);
            ItemStack stack = gun.getMagazineStack(true);
            stack.stackSize = amount;
            player.inventory.addItemStackToInventory(stack);
        }
        player.inventory.markDirty();
    }

    private IGunHandler getGunHandler(ItemStack stack)
    {
        return stack.getCapability(GunHandlerStorage.GUN_HANDLER_CAPABILITY, null);
    }
}