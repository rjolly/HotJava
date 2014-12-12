// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Resizable.java

package sunw.hotjava.tags;

import java.awt.Point;

// Referenced classes of package sunw.hotjava.tags:
//            FrameSetPanel

public interface Resizable
{

    public abstract void setResizeListener(FrameSetPanel framesetpanel, Point point);

    public static final int LEFT_SIDE = 0;
    public static final int RIGHT_SIDE = 1;
    public static final int TOP_SIDE = 2;
    public static final int BOTTOM_SIDE = 3;
}
