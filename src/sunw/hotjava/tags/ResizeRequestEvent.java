// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ResizeRequestEvent.java

package sunw.hotjava.tags;

import java.awt.Component;
import java.awt.Point;
import java.util.EventObject;

// Referenced classes of package sunw.hotjava.tags:
//            EdgeInfo

class ResizeRequestEvent extends EventObject
{

    ResizeRequestEvent(Component component, Point point, boolean flag, int i)
    {
        super(component);
        initialLoc = point;
        edgeInfo = new EdgeInfo(flag, i);
    }

    ResizeRequestEvent(ResizeRequestEvent resizerequestevent, int i)
    {
        this((Component)resizerequestevent.getSource(), resizerequestevent.initialLoc, resizerequestevent.isVertical(), i);
    }

    Point getLocation()
    {
        return new Point(initialLoc);
    }

    boolean isVertical()
    {
        return edgeInfo.isVertical;
    }

    int getValue()
    {
        return edgeInfo.getValue();
    }

    private EdgeInfo edgeInfo;
    private Point initialLoc;
}
