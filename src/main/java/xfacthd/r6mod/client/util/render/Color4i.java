package xfacthd.r6mod.client.util.render;

public class Color4i
{
    public static final Color4i WHITE = new Color4i(255, 255, 255, 255);
    public static final Color4i RED =   new Color4i(255,   0,   0, 255);
    public static final Color4i GREEN = new Color4i(  0, 255,   0, 255);
    public static final Color4i BLUE =  new Color4i(  0,   0, 255, 255);
    public static final Color4i BLACK = new Color4i(  0,   0,   0, 255);

    private final int packed;
    private final int r;
    private final int g;
    private final int b;
    private final int a;

    public Color4i(int r, int g, int b, int a)
    {
        this.packed = (r << 24) | (g << 16) | (b << 8) | a;

        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Color4i(int packed)
    {
        this.packed = packed;

        int[] parts = UIRenderHelper.getRGBAArrayFromHexColor(packed);
        this.r = parts[0];
        this.g = parts[1];
        this.b = parts[2];
        this.a = parts[3];
    }

    public int r() { return r; }

    public int g() { return g; }

    public int b() { return b; }

    public int a() { return a; }

    public int packed() { return packed; }
}