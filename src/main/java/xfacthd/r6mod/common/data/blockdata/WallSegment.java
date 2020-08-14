package xfacthd.r6mod.common.data.blockdata;

import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public enum WallSegment implements IStringSerializable
{
    TOP_RIGHT,
    TOP_LEFT,
    CENTER_RIGHT,
    CENTER_LEFT,
    BOTTOM_RIGHT,
    BOTTOM_LEFT;

    @Override
    public String getName() { return toString().toLowerCase(Locale.ENGLISH); }

    public boolean isTop() { return this == TOP_LEFT || this == TOP_RIGHT; }

    public boolean isCenter() { return this == CENTER_LEFT || this == CENTER_RIGHT; }

    public boolean isBottom() { return this == BOTTOM_LEFT || this == BOTTOM_RIGHT; }

    public boolean isRight() { return this == TOP_RIGHT || this == CENTER_RIGHT || this == BOTTOM_RIGHT; }

    public WallSegment offsetSide(boolean right)
    {
        if (isRight() == right) { return this; }

        switch (this)
        {
            case TOP_RIGHT:    return TOP_LEFT;
            case TOP_LEFT:     return TOP_RIGHT;
            case CENTER_RIGHT: return CENTER_LEFT;
            case CENTER_LEFT:  return CENTER_RIGHT;
            case BOTTOM_RIGHT: return BOTTOM_LEFT;
            case BOTTOM_LEFT:  return BOTTOM_RIGHT;
            default: throw new IllegalArgumentException("Invalid enum constant!");
        }
    }

    public WallSegment squareOffsetHeight(boolean top)
    {
        if (isTop() == top) { return this; }
        if (isCenter()) { throw new IllegalArgumentException("This is not a valid square segment!"); }

        switch (this)
        {
            case TOP_RIGHT:    return BOTTOM_RIGHT;
            case TOP_LEFT:     return BOTTOM_LEFT;
            case BOTTOM_RIGHT: return TOP_RIGHT;
            case BOTTOM_LEFT:  return TOP_LEFT;
            default: throw new IllegalArgumentException("Invalid enum constant!");
        }
    }

    public Map<WallSegment, BlockPos> squarePositions(BlockPos pos)
    {
        Map<WallSegment, BlockPos> posMap = new HashMap<>();

        posMap.put(this, pos);

        Direction heightOff = isTop() ? Direction.SOUTH : Direction.NORTH;
        Direction sideOff = isRight() ? Direction.WEST : Direction.EAST;

        posMap.put(squareOffsetHeight(!isTop()), pos.offset(heightOff));
        posMap.put(offsetSide(!isRight()), pos.offset(sideOff));
        posMap.put(squareFromBools(!isTop(), !isRight()), pos.offset(heightOff).offset(sideOff));

        return posMap;
    }

    public static WallSegment squareFromBools(boolean top, boolean right)
    {
        if (top) { return right ? TOP_RIGHT : TOP_LEFT; }
        else { return right ? BOTTOM_RIGHT : BOTTOM_LEFT; }
    }
}