// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DraggableContainer.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.event.MouseEvent;

// Referenced classes of package sunw.hotjava.ui:
//            DragAndDropEvent, DragAndDropEventMulticaster, DragAndDropListener, DragStartInfo

public class DraggableContainer extends Window
{

    public DraggableContainer(Frame frame, Component component, DragStartInfo dragstartinfo)
    {
        super(frame);
        draggable = dragstartinfo.getDraggee();
        setLayout(null);
        add(draggable);
        dragArena = component;
        resize(5, 5);
        addNotify();
        Dimension dimension = draggable.getPreferredSize();
        resize(dimension.width, dimension.height);
        Component component1 = dragstartinfo.getSourceComponent();
        Point point = component1.getLocationOnScreen();
        Point point1 = dragstartinfo.getSourceLocation();
        point.x += point1.x;
        point.y += point1.y;
        int i = dragstartinfo.getAdjustmentFactor();
        move(point.x, point.y + i);
        disp = new Point(dragstartinfo.getDisplacement());
        show();
    }

    public synchronized void addDragAndDropListener(DragAndDropListener draganddroplistener)
    {
        listeners = DragAndDropEventMulticaster.add(listeners, draganddroplistener);
    }

    public synchronized void removeDragAndDropListener(DragAndDropListener draganddroplistener)
    {
        listeners = DragAndDropEventMulticaster.remove(listeners, draganddroplistener);
    }

    public void dispatchDragAndDropEvent(int i, Object obj)
    {
        if(listeners != null)
        {
            DragAndDropEvent draganddropevent = new DragAndDropEvent(this, i, obj);
            listeners.dragAndDropPerformed(draganddropevent);
        }
    }

    public void dispatchDragAndDropEvent(Object obj, int i, Point point, Object obj1)
    {
        if(listeners != null)
        {
            DragAndDropEvent draganddropevent = new DragAndDropEvent(obj, i, point, obj1);
            listeners.dragAndDropPerformed(draganddropevent);
        }
    }

    public void processDragAndDropEvent(Component component, MouseEvent mouseevent)
    {
        adjustEventCoordinates(component, mouseevent);
        processDragAndDropMouseEvent(mouseevent);
    }

    public boolean processDragAndDropMouseEvent(MouseEvent mouseevent)
    {
        Point point = mouseevent.getPoint();
        switch(mouseevent.getID())
        {
        default:
            break;

        case 502: 
            Component component = findTarget(mouseevent);
            if(component != null)
            {
                Point point1 = new Point(point.x, point.y);
                Point point3 = translateCoordinateSpace(point1, this, component);
                dispatchDragAndDropEvent(component, 0x16737, point3, draggable);
            }
            break;

        case 506: 
            clampEventToArena(mouseevent);
            Point point2 = getLocation();
            move((point2.x + point.x) - disp.x, (point2.y + point.y + adjustmentFactor) - disp.y);
            Component component1 = findTarget(mouseevent);
            if(component1 != null)
            {
                Point point4 = new Point(point.x, point.y);
                Point point5 = translateCoordinateSpace(point4, this, component1);
                dispatchDragAndDropEvent(component1, 0x1673a, point5, draggable);
            }
            break;
        }
        return true;
    }

    protected void clampEventToArena(MouseEvent mouseevent)
    {
    }

    protected Component findTarget(MouseEvent mouseevent)
    {
        Point point = mouseevent.getPoint();
        Point point1 = translateCoordinateSpace(new Point(point.x, point.y), this, dragArena);
        return dragArena.locate(point1.x, point1.y);
    }

    protected void adjustEventCoordinates(Component component, MouseEvent mouseevent)
    {
        Point point = mouseevent.getPoint();
        Point point1 = translateCoordinateSpace(new Point(point.x, point.y), component, this);
        mouseevent.translatePoint(-point.x, -point.y);
        mouseevent.translatePoint(point1.x, point1.y);
    }

    protected Point translateCoordinateSpace(Point point, Component component, Component component1)
    {
        Point point1 = localToGlobal(point, component);
        return globalToLocal(point1, component1);
    }

    protected Point localToGlobal(Point point, Component component)
    {
        Point point1 = component.getLocationOnScreen();
        point1.x += point.x;
        point1.y += point.y;
        return point1;
    }

    protected Point globalToLocal(Point point, Component component)
    {
        Point point1 = component.getLocationOnScreen();
        point1.x = point.x - point1.x;
        point1.y = point.y - point1.y;
        return point1;
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    public void paint(Graphics g)
    {
        draggable.paint(g);
    }

    public void move(int i, int j)
    {
        super.move(i, j);
        show();
    }

    public Component getDraggee()
    {
        return draggable;
    }

    public static final int DROP_EVENT = 0x16737;
    public static final int DRAG_START = 0x16090;
    public static final int DRAG_DONE = 50296;
    private Point disp;
    private int adjustmentFactor;
    private Component draggable;
    private Component dragArena;
    protected DragAndDropListener listeners;
}
