package xfacthd.r6mod.common.util.data;

import xfacthd.r6mod.common.data.PointContext;

public class ExtraPointsEntry
{
    private final PointContext context;
    private final int points;

    public ExtraPointsEntry(PointContext context, int points)
    {
        this.context = context;
        this.points = points;
    }

    public PointContext getContext() { return context; }

    public int getPoints() { return points; }
}