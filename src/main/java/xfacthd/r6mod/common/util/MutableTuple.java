package xfacthd.r6mod.common.util;

public class MutableTuple<A, B>
{
    public A left;
    public B right;

    public MutableTuple(A left, B right)
    {
        this.left = left;
        this.right = right;
    }
}