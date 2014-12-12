// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ResizeTracker.java

package sunw.hotjava.tags;

import java.awt.*;
import java.awt.event.*;

// Referenced classes of package sunw.hotjava.tags:
//            EdgeInfo, FrameSetPanel, RuleWindow

class ResizeTracker
    implements MouseListener, MouseMotionListener
{

    ResizeTracker(Point point, Rectangle rectangle, FrameSetPanel framesetpanel, Component component, EdgeInfo edgeinfo)
    {
        travelLimits = rectangle;
        owner = framesetpanel;
        edgeInfo = edgeinfo;
        originComp = component;
        component.addMouseListener(this);
        component.addMouseMotionListener(this);
        oldCursor = framesetpanel.getCursor();
        framesetpanel.setCursor(Cursor.getPredefinedCursor(12));
        java.awt.Container container;
        for(container = framesetpanel.getParent(); !(container instanceof Frame); container = container.getParent());
        sizingBar = new RuleWindow((Frame)container);
        if(edgeInfo.isVertical())
            sizingBar.setSize(rectangle.width, 1);
        else
            sizingBar.setSize(1, rectangle.height);
        displacement = computeDisplacement(component);
        setLocation(point);
        sizingBar.show();
    }

    private Point computeDisplacement(Component component)
    {
        Point point = new Point();
        java.awt.Container container;
        do
        {
            Point point1 = component.getLocation();
            point.x += point1.x;
            point.y += point1.y;
            container = component.getParent();
            component = container;
        } while(container != owner);
        return point;
    }

    public void mouseDragged(MouseEvent mouseevent)
    {
        setLocation(mouseevent.getPoint());
    }

    public void mouseMoved(MouseEvent mouseevent)
    {
    }

    public void mouseClicked(MouseEvent mouseevent)
    {
    }

    public void mouseEntered(MouseEvent mouseevent)
    {
    }

    public void mouseExited(MouseEvent mouseevent)
    {
    }

    public void mousePressed(MouseEvent mouseevent)
    {
    }

    public void mouseReleased(MouseEvent mouseevent)
    {
        owner.setCursor(oldCursor);
        originComp.removeMouseListener(this);
        originComp.removeMouseMotionListener(this);
        setLocation(mouseevent.getPoint());
        sizingBar.hide();
        sizingBar.dispose();
        owner.resizeFrames(edgeInfo, loc);
    }

    void setLocation(Point point)
    {
        point.x += displacement.x;
        point.y += displacement.y;
        loc = clampToLimits(point);
        Point point1 = owner.getLocationOnScreen();
        sizingBar.setLocation(point1.x + loc.x, point1.y + loc.y);
    }

    private Point clampToLimits(Point point)
    {
        Dimension dimension = sizingBar.getSize();
        if(point.x < travelLimits.x)
            point.x = travelLimits.x;
        else
        if(point.x + dimension.width >= travelLimits.x + travelLimits.width)
            point.x = (travelLimits.x + travelLimits.width) - dimension.width;
        if(point.y < travelLimits.y)
            point.y = travelLimits.y;
        else
        if(point.y + dimension.height >= travelLimits.y + travelLimits.height)
            point.y = (travelLimits.y + travelLimits.height) - dimension.height;
        return point;
    }

    private Point loc;
    private Rectangle travelLimits;
    private EdgeInfo edgeInfo;
    private FrameSetPanel owner;
    private Window sizingBar;
    private Cursor oldCursor;
    private Point displacement;
    private Component originComp;
}
