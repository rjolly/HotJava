// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DragAndDropEvent.java

package sunw.hotjava.ui;

import java.awt.Point;
import java.util.EventObject;

public class DragAndDropEvent extends EventObject
{

    public DragAndDropEvent(Object obj, int i, Point point1, Object obj1)
    {
        super(obj);
        arg = obj1;
        id = i;
        point = point1;
    }

    public DragAndDropEvent(Object obj, int i, Object obj1)
    {
        this(obj, i, new Point(0, 0), obj1);
    }

    public int getX()
    {
        return point.x;
    }

    public int getY()
    {
        return point.y;
    }

    public Point getPoint()
    {
        return point;
    }

    public void translatePoint(int i, int j)
    {
        point.x += i;
        point.y += j;
    }

    public int getID()
    {
        return id;
    }

    public Object getArgument()
    {
        return arg;
    }

    public String toString()
    {
        String s = getClass().getName() + "[source=" + super.source + " + id=" + id;
        if(arg != null)
            s = s + " + arg=" + arg;
        s = s + " ]";
        return s;
    }

    public static final int DND_EVENT = 0x16737;
    public static final int DROP_EVENT = 0x16737;
    public static final int DRAG_START = 0x16738;
    public static final int DRAG_DONE = 0x16739;
    public static final int HILITE_EVENT = 0x1673a;
    Object arg;
    private int id;
    Point point;
}
