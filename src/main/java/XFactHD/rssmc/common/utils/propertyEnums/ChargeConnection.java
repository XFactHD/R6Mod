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

import java.util.*;

public enum ChargeConnection implements IStringSerializable
{
    NONE,
    UP,
    DOWN,
    RIGHT,
    LEFT,
    UP_RIGHT,
    UP_LEFT,
    DOWN_RIGHT,
    DOWN_LEFT;

    public static final List<ChargeConnection> BREACH = new ArrayList<>();
    public static final List<ChargeConnection> THERMITE = new ArrayList<>();

    static
    {
        BREACH.add(NONE);
        BREACH.add(UP);
        BREACH.add(DOWN);

        THERMITE.add(RIGHT);
        THERMITE.add(LEFT);
        THERMITE.add(UP_RIGHT);
        THERMITE.add(UP_LEFT);
        THERMITE.add(DOWN_RIGHT);
        THERMITE.add(DOWN_LEFT);
    }

    @Override
    public String getName()
    {
        return toString().toLowerCase(Locale.ENGLISH);
    }

    public EnumFacing getFacingOffset()
    {
        switch (this)
        {
            case UP: return EnumFacing.UP;
            case DOWN: return EnumFacing.DOWN;
            default: throw new UnsupportedOperationException();
        }
    }

    public ChargeConnection getOpposite()
    {
        switch (this)
        {
            case UP: return DOWN;
            case DOWN: return UP;
            case RIGHT: return LEFT;
            case LEFT: return RIGHT;
            default: throw new UnsupportedOperationException();
        }
    }

    public List<ChargeConnection> getOpposites()
    {
        switch (this)
        {
            case UP_RIGHT:   return Arrays.asList(UP_LEFT, DOWN_RIGHT);
            case UP_LEFT:    return Arrays.asList(UP_RIGHT, DOWN_LEFT);
            case DOWN_RIGHT: return Arrays.asList(DOWN_LEFT, UP_RIGHT);
            case DOWN_LEFT:  return Arrays.asList(DOWN_RIGHT, UP_LEFT);
            case NONE: throw new UnsupportedOperationException();
            default: return Collections.singletonList(getOpposite());
        }
    }

    public List<BlockPos> getBelongingConnectionsFacingDown(BlockPos pos)
    {
        List<BlockPos> cons = new ArrayList<>();
        switch (this)
        {
            case UP_RIGHT:
            {
                cons.add(pos.east());
                cons.add(pos.north());
                cons.add(pos.north().east());
                break;
            }
            case UP_LEFT:
            {
                cons.add(pos.west());
                cons.add(pos.north());
                cons.add(pos.north().west());
                break;
            }
            case DOWN_RIGHT:
            {
                cons.add(pos.east());
                cons.add(pos.south());
                cons.add(pos.east().south());
                break;
            }
            case DOWN_LEFT:
            {
                cons.add(pos.west());
                cons.add(pos.south());
                cons.add(pos.west().south());
                break;
            }
            default: throw new UnsupportedOperationException();
        }
        return cons;
    }
}