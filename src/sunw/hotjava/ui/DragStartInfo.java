// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DragStartInfo.java

package sunw.hotjava.ui;

import java.awt.Component;
import java.awt.Point;

public class DragStartInfo
{

    public DragStartInfo(Component component, Component component1, Point point, Point point1, int i)
    {
        source = component;
        draggee = component1;
        srcLoc = point;
        displacement = point1;
        adjustmentFactor = i;
    }

    Component getSourceComponent()
    {
        return source;
    }

    Component getDraggee()
    {
        return draggee;
    }

    Point getSourceLocation()
    {
        return srcLoc;
    }

    Point getDisplacement()
    {
        return displacement;
    }

    int getAdjustmentFactor()
    {
        return adjustmentFactor;
    }

    private Component source;
    private Component draggee;
    private Point srcLoc;
    private Point displacement;
    private int adjustmentFactor;
}
