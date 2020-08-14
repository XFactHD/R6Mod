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

package XFactHD.rssmc.common.utils.propertyEnums;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public enum Connection implements IStringSerializable
{
    UDR,
    UDL,
    UR,
    DR,
    UL,
    DL;

    public static final List<Connection> CON_DOWN_REINF = new ArrayList<>();
    public static final List<Connection> CON_SIDE_REINF = new ArrayList<>();
    public static final List<Connection> CON_FOUR_PARTS = new ArrayList<>();

    static
    {
        CON_DOWN_REINF.add(UR);
        CON_DOWN_REINF.add(UL);
        CON_DOWN_REINF.add(DR);
        CON_DOWN_REINF.add(DL);

        CON_SIDE_REINF.add(UDR);
        CON_SIDE_REINF.add(UDL);
        CON_SIDE_REINF.add(UR);
        CON_SIDE_REINF.add(DR);
        CON_SIDE_REINF.add(UL);
        CON_SIDE_REINF.add(DL);

        CON_FOUR_PARTS.add(UR);
        CON_FOUR_PARTS.add(UL);
        CON_FOUR_PARTS.add(DR);
        CON_FOUR_PARTS.add(DL);
    }

    @Override
    public String getName()
    {
        return toString().toLowerCase(Locale.ENGLISH);
    }

    public static Connection fromBlockPositions(BlockPos posMin, BlockPos pos, EnumFacing side)
    {
        int distY = pos.getY() - posMin.getY();
        int distXZ;
        switch (side)
        {
            case NORTH: distXZ = posMin.getX() - pos.getX(); break;
            case EAST:  distXZ = posMin.getZ() - pos.getZ(); break;
            case SOUTH: distXZ = pos.getX() - posMin.getX(); break;
            case WEST:  distXZ = pos.getZ() - posMin.getZ(); break;
            default:    distXZ = 0; break;
        }

        if (distY == 2)
        {
            if (distXZ == 1)
            {
                return DL;
            }
            else
            {
                return DR;
            }
        }
        else if (distY == 1)
        {
            if (distXZ == 1)
            {
                return UDL;
            }
            else
            {
                return UDR;
            }
        }
        else
        {
            if (distXZ == 1)
            {
                return UL;
            }
            else
            {
                return UR;
            }
        }
    }

    public static Connection valueOf(int index)
    {
        return values()[index];
    }

    public static boolean doConsBelongTogether(Connection con, Connection neighborCon)
    {
        switch (con)
        {
            case UR: return neighborCon == UL || neighborCon == DR;
            case DR: return neighborCon == DL || neighborCon == UR;
            case UL: return neighborCon == UR || neighborCon == DL;
            case DL: return neighborCon == DR || neighborCon == UR;
            default: return false;
        }
    }

    public static List<BlockPos> getBlockPosListForPosAndCon(BlockPos pos, Connection con, EnumFacing facing)
    {
        List<BlockPos> posList = new ArrayList<>();
        if (facing == EnumFacing.DOWN)
        {
            switch (con)
            {
                case UR: posList.add(pos.north()); posList.add(pos.east()); posList.add(pos.north().east()); break;
                case UL: posList.add(pos.north()); posList.add(pos.west()); posList.add(pos.north().west()); break;
                case DR: posList.add(pos.south()); posList.add(pos.east()); posList.add(pos.south().east()); break;
                case DL: posList.add(pos.south()); posList.add(pos.west()); posList.add(pos.south().west()); break;
            }
        }
        else
        {
            switch (facing)
            {
                case NORTH:
                {
                    if (con == UR)
                    {
                        posList.add(pos.up()); posList.add(pos.up(2));
                        posList.add(pos.east()); posList.add(pos.east().up()); posList.add(pos.east().up(2));
                    }
                    else if (con == UL)
                    {
                        posList.add(pos.up()); posList.add(pos.up(2));
                        posList.add(pos.west()); posList.add(pos.west().up()); posList.add(pos.west().up(2));
                    }
                }
                case EAST:
                {
                    if (con == UR)
                    {
                        posList.add(pos.up()); posList.add(pos.up(2));
                        posList.add(pos.south()); posList.add(pos.south().up()); posList.add(pos.south().up(2));
                    }
                    else if (con == UL)
                    {
                        posList.add(pos.up()); posList.add(pos.up(2));
                        posList.add(pos.north()); posList.add(pos.north().up()); posList.add(pos.north().up(2));
                    }
                }
                case SOUTH:
                {
                    if (con == UR)
                    {
                        posList.add(pos.up()); posList.add(pos.up(2));
                        posList.add(pos.west()); posList.add(pos.west().up()); posList.add(pos.west().up(2));
                    }
                    else if (con == UL)
                    {
                        posList.add(pos.up()); posList.add(pos.up(2));
                        posList.add(pos.east()); posList.add(pos.east().up()); posList.add(pos.east().up(2));
                    }
                }
                case WEST:
                {
                    if (con == UR)
                    {
                        posList.add(pos.up()); posList.add(pos.up(2));
                        posList.add(pos.north()); posList.add(pos.north().up()); posList.add(pos.north().up(2));
                    }
                    else if (con == UL)
                    {
                        posList.add(pos.up()); posList.add(pos.up(2));
                        posList.add(pos.south()); posList.add(pos.south().up()); posList.add(pos.south().up(2));
                    }
                }
            }
        }
        return posList;
    }

    public ChargeConnection getChargeConnectionFacingDown()
    {
        switch (this)
        {
            case UR: return ChargeConnection.UP_RIGHT;
            case UL: return ChargeConnection.UP_LEFT;
            case DR: return ChargeConnection.DOWN_RIGHT;
            case DL: return ChargeConnection.DOWN_LEFT;
            default: throw new UnsupportedOperationException();
        }
    }
}