package xfacthd.r6mod.common.data;

import net.minecraft.state.*;
import net.minecraft.util.Direction;
import xfacthd.r6mod.common.data.blockdata.WallMaterial;
import xfacthd.r6mod.common.data.blockdata.WallSegment;

public class PropertyHolder
{
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final BooleanProperty DESTROYED = BooleanProperty.create("destroyed");
    public static final BooleanProperty TOP = BooleanProperty.create("top");
    public static final BooleanProperty ON_GLASS = BooleanProperty.create("glass");
    public static final BooleanProperty LARGE = BooleanProperty.create("large");
    public static final BooleanProperty LEFT = BooleanProperty.create("left");
    public static final BooleanProperty CENTER = BooleanProperty.create("center");
    public static final BooleanProperty RIGHT = BooleanProperty.create("right");
    public static final BooleanProperty DOOR = BooleanProperty.create("door");
    public static final BooleanProperty OPEN = BooleanProperty.create("open");
    public static final BooleanProperty REINFORCED = BooleanProperty.create("reinforced");
    public static final BooleanProperty ELECTRIFIED = BooleanProperty.create("electro");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");
    public static final BooleanProperty TRIGGERED = BooleanProperty.create("triggered");

    public static final DirectionProperty FACING_HOR = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);
    public static final DirectionProperty FACING_NE = DirectionProperty.create("facing", Direction.NORTH, Direction.EAST);
    public static final DirectionProperty FACING_NOT_UP = DirectionProperty.create("facing", direction -> direction != Direction.UP);
    public static final DirectionProperty FACING_NOT_DOWN = DirectionProperty.create("facing", direction -> direction != Direction.DOWN);

    public static final EnumProperty<WallMaterial> MATERIAL = EnumProperty.create("material", WallMaterial.class);
    public static final EnumProperty<WallSegment> WALL_SEGMENT = EnumProperty.create("segment", WallSegment.class);
    public static final EnumProperty<WallSegment> SQUARE_SEGMENT = EnumProperty.create("segment", WallSegment.class, (s) -> !s.isCenter());
}