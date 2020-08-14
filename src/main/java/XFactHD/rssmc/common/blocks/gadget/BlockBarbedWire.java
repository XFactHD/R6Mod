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
import XFactHD.rssmc.api.block.HitType;
import XFactHD.rssmc.api.block.IDestructable;
import XFactHD.rssmc.api.block.IShockable;
import XFactHD.rssmc.api.block.IShootable;
import XFactHD.rssmc.common.Content;
import XFactHD.rssmc.common.blocks.BlockBase;
import XFactHD.rssmc.common.data.EnumGadget;
import XFactHD.rssmc.common.data.team.StatusController;
import XFactHD.rssmc.common.items.itemBlocks.ItemBlockGadget;
import XFactHD.rssmc.common.utils.Damage;
import XFactHD.rssmc.common.utils.helper.PropertyHolder;
import XFactHD.rssmc.common.utils.propertyEnums.Connection;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class BlockBarbedWire extends BlockBase implements IShootable, IShockable, IDestructable
{
    private static final Material BARBED_WIRE = new Material(MapColor.CLOTH)
    {
        @Override
        public boolean isToolNotRequired()
        {
            return true;
        }

        @Override
        public boolean isSolid()
        {
            return true;
        }

        @Override
        public boolean isOpaque()
        {
            return false;
        }

        @Override
        public boolean getCanBurn()
        {
            return false;
        }

        @Override
        public EnumPushReaction getMobilityFlag()
        {
            return EnumPushReaction.DESTROY;
        }
    };

    //private static final SoundType SOUND_BARBED_WIRE = new SoundType(1, 1, Sounds.getGadgetSound(EnumGadget.BARBED_WIRE, "break"),
    //                                                                       Sounds.getGadgetSound(EnumGadget.BARBED_WIRE, "step"),
    //                                                                       Sounds.getGadgetSound(EnumGadget.BARBED_WIRE, "place"),
    //                                                                       Sounds.getGadgetSound(EnumGadget.BARBED_WIRE, "hit"),
    //                                                                       Sounds.getGadgetSound(EnumGadget.BARBED_WIRE, "fall"));

    public BlockBarbedWire()
    {
        super("blockBarbedWire", BARBED_WIRE, RainbowSixSiegeMC.CT.gadgetTab, null, null);
        setDefaultState(super.getDefaultState().withProperty(PropertyHolder.ELECTRIFIED, false).withProperty(PropertyHolder.BARBED_WIRE_CONNECTION, Connection.UR));
        registerSpecialItemBlock(new ItemBlockGadget(this, 50)); //TODO: consider adding placement animation while timer counts
        registerTileEntity(TileEntityBarbedWire.class, "BarbedWire");
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PropertyHolder.ELECTRIFIED, PropertyHolder.BARBED_WIRE_CONNECTION);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        boolean electro = false;
        if (meta == 0 || meta == 1 || meta == 2)
        {
            meta = Connection.UR.ordinal();
        }
        if (meta > 5)
        {
            meta -= 6;
            electro = true;
        }
        return getDefaultState().withProperty(PropertyHolder.ELECTRIFIED, electro).withProperty(PropertyHolder.BARBED_WIRE_CONNECTION, Connection.valueOf(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(PropertyHolder.BARBED_WIRE_CONNECTION).ordinal() + (state.getValue(PropertyHolder.ELECTRIFIED) ? 6 : 0);
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos)
    {
        boolean top = world.isAirBlock(pos.north()) || world.getBlockState(pos.north()).getBlock().isReplaceable(world, pos.north());
        boolean right = world.isAirBlock(pos.east()) || world.getBlockState(pos.east()).getBlock().isReplaceable(world, pos.east());
        boolean topright = world.isAirBlock(pos.north().east()) || world.getBlockState(pos.north().east()).getBlock().isReplaceable(world, pos.north().east());
        return super.canPlaceBlockAt(world, pos) && top && right && topright;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        world.setBlockState(pos.north(), getDefaultState().withProperty(PropertyHolder.BARBED_WIRE_CONNECTION, Connection.DR));
        world.setBlockState(pos.east(), getDefaultState().withProperty(PropertyHolder.BARBED_WIRE_CONNECTION, Connection.UL));
        world.setBlockState(pos.north().east(), getDefaultState().withProperty(PropertyHolder.BARBED_WIRE_CONNECTION, Connection.DL));

        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityBarbedWire && placer instanceof EntityPlayer)
        {
            ((TileEntityBarbedWire)te).setOwner((EntityPlayer) placer);
        }
    }

    @Override
    public void onBlockClicked(World world, BlockPos pos, EntityPlayer player)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityBarbedWire)
        {
            IBlockState state = world.getBlockState(pos);
            ((TileEntityBarbedWire)te).hit(state.getValue(PropertyHolder.BARBED_WIRE_CONNECTION));
        }
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityBarbedWire && entity instanceof EntityPlayer)
        {
            EntityPlayer owner = ((TileEntityBarbedWire)te).getOwner();
            if (owner == null) { return; }
            if (!entity.equals(owner) && !StatusController.arePlayersTeamMates(owner, (EntityPlayer)entity))
            {
                entity.setInWeb();
            }
            if (state.getValue(PropertyHolder.ELECTRIFIED))
            {
                entity.attackEntityFrom(Damage.causeShockWireDamage(((TileEntityBarbedWire)te).getOwner()), 1F);
            }
        }
    }

    @Override //TODO: implement IUsageTimer for this
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        TileEntity te = world.getTileEntity(pos);
        if (heldItem != null && heldItem.getItem() == Item.getItemFromBlock(Content.blockShockWire) && !state.getValue(PropertyHolder.ELECTRIFIED) && te instanceof TileEntityBarbedWire)
        {
            if (!world.isRemote)
            {
                world.setBlockState(pos, state.withProperty(PropertyHolder.ELECTRIFIED, true));
                ((TileEntityBarbedWire)te).setShockWireOwner(player);
                setElectrified(world, pos, state.getValue(PropertyHolder.BARBED_WIRE_CONNECTION), true);
            }
            return true;
        }
        else if (te instanceof TileEntityBarbedWire && player.isSneaking())
        {
            if (player == ((TileEntityBarbedWire)te).getOwner() && !state.getValue(PropertyHolder.ELECTRIFIED))
            {
                destroy(state, world, pos);
                player.inventory.addItemStackToInventory(new ItemStack(Content.blockBarbedWire));
                return true;
            }
            else if (state.getValue(PropertyHolder.ELECTRIFIED) && player == ((TileEntityBarbedWire)te).getShockWireOwner())
            {
                world.setBlockState(pos, state.withProperty(PropertyHolder.ELECTRIFIED, false));
                setElectrified(world, pos, state.getValue(PropertyHolder.BARBED_WIRE_CONNECTION), false);
                player.inventory.addItemStackToInventory(new ItemStack(Content.blockShockWire));
            }
        }
        return false;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos)
    {
        return null;
    }

    private AxisAlignedBB getBoundingBox(IBlockState state)
    {
    switch (state.getValue(PropertyHolder.BARBED_WIRE_CONNECTION))
    {
        case UR: return new AxisAlignedBB(.1875, 0,     0,     1, .75, .8125);
        case UL: return new AxisAlignedBB(    0, 0,     0, .8125, .75, .8125);
        case DR: return new AxisAlignedBB(.1875, 0, .1875,     1, .75,     1);
        case DL: return new AxisAlignedBB(    0, 0, .1875, .8125, .75,     1);
        default: return new AxisAlignedBB(0, 0, 0, 0, 0, 0);
    }
}

    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos)
    {
        switch (state.getValue(PropertyHolder.BARBED_WIRE_CONNECTION))
        {
            case UR: return new AxisAlignedBB(.1875, 0,     0,     1, .75, .8125).offset(pos); //S(+Z) and W(-X) smaller
            case UL: return new AxisAlignedBB(    0, 0,     0, .8125, .75, .8125).offset(pos); //S(+Z) and E(+X) smaller
            case DR: return new AxisAlignedBB(.1875, 0, .1875,     1, .75,     1).offset(pos); //N(-Z) and W(-X) smaller
            case DL: return new AxisAlignedBB(    0, 0, .1875, .8125, .75,     1).offset(pos); //N(-Z) and E(+X) smaller
            default: return new AxisAlignedBB(0, 0, 0, 0, 0, 0);
        }
    }

    @Nullable
    @Override
    public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end)
    {
        AxisAlignedBB aabb = getBoundingBox(state);
        return rayTrace(pos, start, end, aabb);
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor)
    {
        TileEntity neighborTile = world.getTileEntity(neighbor);
        Connection con = world.getBlockState(pos).getValue(PropertyHolder.BARBED_WIRE_CONNECTION);
        if (neighborTile instanceof TileEntityBarbedWire && ((TileEntityBarbedWire)neighborTile).getOwner() == null)
        {
            Connection neighborCon = world.getBlockState(neighbor).getValue(PropertyHolder.BARBED_WIRE_CONNECTION);
            TileEntity te = world.getTileEntity(pos);
            if (Connection.doConsBelongTogether(con, neighborCon) && te instanceof TileEntityBarbedWire)
            {
                EntityPlayer owner = ((TileEntityBarbedWire)te).getOwner();
                if (((TileEntityBarbedWire)neighborTile).getOwner() != owner)
                {
                    ((TileEntityBarbedWire)neighborTile).setOwner(owner);
                }
            }
        }
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityBarbedWire();
    }

    @Override
    public boolean isCompleteBlock(IBlockState state)
    {
        return false;
    }

    @Override
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean isUnbreakableInSurvivalMode(IBlockState state)
    {
        return true;
    }

    private void destroy(IBlockState state, World world, BlockPos pos)
    {
        Connection con = state.getValue(PropertyHolder.BARBED_WIRE_CONNECTION);
        BlockPos pos1 = null;
        BlockPos pos2 = null;
        BlockPos pos3 = null;
        switch (con)
        {
            case UR:
            {
                pos1 = pos.north();
                pos2 = pos.east();
                pos3 = pos.north().east();
                break;
            }
            case UL:
            {
                pos1 = pos.north();
                pos2 = pos.west();
                pos3 = pos.north().west();
                break;
            }
            case DR:
            {
                pos1 = pos.south();
                pos2 = pos.east();
                pos3 = pos.south().east();
                break;
            }
            case DL:
            {
                pos1 = pos.south();
                pos2 = pos.west();
                pos3 = pos.south().west();
                break;
            }
        }

            world.destroyBlock(pos, false);
            if (pos1 != null) world.destroyBlock(pos1, false);
            if (pos2 != null) world.destroyBlock(pos2, false);
            if (pos3 != null) world.destroyBlock(pos3, false);
    }

    private void setElectrified(World world, BlockPos pos, Connection con, boolean electrified)
    {
        BlockPos pos1 = null;
        BlockPos pos2 = null;
        BlockPos pos3 = null;
        switch (con)
        {
            case UR:
            {
                pos1 = pos.north();
                pos2 = pos.east();
                pos3 = pos.north().east();
                break;
            }
            case UL:
            {
                pos1 = pos.north();
                pos2 = pos.west();
                pos3 = pos.north().west();
                break;
            }
            case DR:
            {
                pos1 = pos.south();
                pos2 = pos.east();
                pos3 = pos.south().east();
                break;
            }
            case DL:
            {
                pos1 = pos.south();
                pos2 = pos.west();
                pos3 = pos.south().west();
                break;
            }
        }
        EntityPlayer owner = null;
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityBarbedWire)
        {
            owner = ((TileEntityBarbedWire)tile).getOwner();
        }
        if (pos1 != null)
        {
            world.setBlockState(pos1, world.getBlockState(pos1).withProperty(PropertyHolder.ELECTRIFIED, electrified));
            TileEntity te = world.getTileEntity(pos1);
            if (te instanceof TileEntityBarbedWire)
            {
                ((TileEntityBarbedWire)te).setShockWireOwner(electrified ? owner : null);
            }
        }
        if (pos2 != null)
        {
            world.setBlockState(pos2, world.getBlockState(pos2).withProperty(PropertyHolder.ELECTRIFIED, electrified));
            TileEntity te = world.getTileEntity(pos2);
            if (te instanceof TileEntityBarbedWire)
            {
                ((TileEntityBarbedWire)te).setShockWireOwner(electrified ? owner : null);
            }
        }
        if (pos3 != null)
        {
            world.setBlockState(pos3, world.getBlockState(pos3).withProperty(PropertyHolder.ELECTRIFIED, electrified));
            TileEntity te = world.getTileEntity(pos3);
            if (te instanceof TileEntityBarbedWire)
            {
                ((TileEntityBarbedWire)te).setShockWireOwner(electrified ? owner : null);
            }
        }
    }

    @Override
    public boolean onBlockShot(World world, BlockPos pos, IBlockState state, EntityPlayer player, float hitX, float hitY, float hitZ, EnumFacing sideHit)
    {
        //TODO: implement shooting out the battery if electrified
        return false;
    }

    @Override
    public boolean onBlockDestroyedByShot(World world, BlockPos pos, IBlockState state, EntityPlayer player, float hitX, float hitY, float hitZ, EnumFacing sideHit) { return false; }

    @Override
    public boolean canBeShocked(IBlockState state)
    {
        return state.getValue(PropertyHolder.ELECTRIFIED);
    }

    @Override
    public boolean canAttachGagdet(World world, BlockPos pos, IBlockState state, EnumGadget gadget, EnumFacing side)
    {
        return false;
    }

    @Override
    public void explode(World world, BlockPos pos, IBlockState state, EnumGadget gadget, EnumFacing originatingSide) {}

    @Override
    public boolean destruct(World world, BlockPos pos, IBlockState state, HitType type, EnumFacing originatingSide)
    {
        if (type == HitType.IMPACT_GRENADE || type == HitType.C4 || type == HitType.BREACH_GRENADE || type == HitType.CLUSTER_CHARGE || type == HitType.FRAG_GRENADE || type == HitType.HAMMER)
        {
            destroy(state, world, pos);
            return true;
        }
        return false;
    }

    @Override
    public boolean shock(World world, BlockPos pos, IBlockState state, EntityPlayer player, float hitX, float hitY, float hitZ)
    {
        //TODO: implement destruction of battery if electrified
        return false;
    }
}