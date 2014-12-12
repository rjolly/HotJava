// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ListCanvas.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.PrintStream;
import java.util.EventObject;

// Referenced classes of package sunw.hotjava.ui:
//            DragAndDropEvent, DragAndDropEventMulticaster, DragAndDropListener, DragStartInfo, 
//            ListContainer, ListElement, ListItem, ListItemComponent

public class ListCanvas extends Canvas
{
    private final class ListCanvasMouseListener extends MouseAdapter
        implements MouseMotionListener
    {

        public void mousePressed(MouseEvent mouseevent)
        {
            processCanvasMouseEvent(mouseevent);
        }

        public void mouseReleased(MouseEvent mouseevent)
        {
            processCanvasMouseEvent(mouseevent);
        }

        public void mouseDragged(MouseEvent mouseevent)
        {
            processCanvasMouseEvent(mouseevent);
        }

        public void mouseMoved(MouseEvent mouseevent)
        {
        }

        ListCanvasMouseListener()
        {
        }
    }

    protected class DnDListener
        implements DragAndDropListener
    {

        public void dragAndDropPerformed(DragAndDropEvent draganddropevent)
        {
            processDragAndDropEvent(draganddropevent);
        }

        protected DnDListener()
        {
        }
    }


    public void dbg(String s)
    {
        if(debug)
            System.out.println("debug> " + s);
    }

    public DragAndDropListener getDragAndDropListener()
    {
        return new DnDListener();
    }

    public synchronized void addDragAndDropListener(DragAndDropListener draganddroplistener)
    {
        listeners = DragAndDropEventMulticaster.add(listeners, draganddroplistener);
    }

    public synchronized void removeDragAndDropListener(DragAndDropListener draganddroplistener)
    {
        listeners = DragAndDropEventMulticaster.remove(listeners, draganddroplistener);
    }

    public void dispatchDragAndDropEvent(DragAndDropEvent draganddropevent)
    {
        if(listeners != null)
            listeners.dragAndDropPerformed(draganddropevent);
    }

    public ListCanvas(Panel panel1, String s, int i, int j)
    {
        hand_cursor = new Cursor(12);
        default_cursor = new Cursor(0);
        debug = false;
        dragged = false;
        handleDrop = true;
        title = s;
        panel = panel1;
        setSize(i, j);
        addMouseListener(new ListCanvasMouseListener());
        addMouseMotionListener(new ListCanvasMouseListener());
    }

    public void setListElementImage(Image image)
    {
        element_img = image;
    }

    public Image getListElementImage()
    {
        return element_img;
    }

    public void setListContainerImages(Image image, Image image1)
    {
        open_img = image;
        closed_img = image1;
    }

    public Image getOpenContainerImage()
    {
        return open_img;
    }

    public Image getClosedContainerImage()
    {
        return closed_img;
    }

    public void createInitialFolder()
    {
        mainFolder = new ListContainer(this, null, title, open_img, closed_img);
        mainFolder.setCanvas(this);
    }

    static Point getTranslatedPoint(int i, int j, Component component, Component component1)
    {
        Object obj;
        for(obj = component1; obj != null && obj != component; obj = ((Component) (obj)).getParent())
        {
            Rectangle rectangle = ((Component) (obj)).getBounds();
            i -= rectangle.x;
            j -= rectangle.y;
        }

        if(obj != null)
            return new Point(i, j);
        else
            return null;
    }

    public boolean processDragAndDropEvent(DragAndDropEvent draganddropevent)
    {
        Component component = (Component)draganddropevent.getSource();
        Point point = getTranslatedPoint(draganddropevent.getX(), draganddropevent.getY(), component, this);
        switch(draganddropevent.getID())
        {
        case 91959: 
            ListItemComponent listitemcomponent = (ListItemComponent)draganddropevent.getArgument();
            ListItem listitem = listitemcomponent.getBaseItem();
            Point point1 = listitem.getPoint();
            ListContainer listcontainer = listitem.getParentListContainer();
            mainFolder.resolveItemLocation(listitem, new Point(point.x, point.y));
            if(listitem.getPoint().equals(point1) && listitem.getParentListContainer() == listcontainer)
            {
                handleDrop = false;
                Graphics g = getGraphics();
                try
                {
                    eraseHiliteBox(g);
                }
                finally
                {
                    g.dispose();
                }
            } else
            {
                handleDrop = true;
                adjustSize();
                repaint(50L);
            }
            break;

        case 91962: 
            processHiliteEvent(component, point);
            break;
        }
        return true;
    }

    public void processHiliteEvent(Component component, Point point)
    {
    }

    public void processCanvasMouseEvent(MouseEvent mouseevent)
    {
        mouseevent.getPoint();
        switch(mouseevent.getID())
        {
        case 501: 
            mousePressedOnCanvas(mouseevent);
            return;

        case 502: 
            mouseReleasedOnCanvas(mouseevent);
            return;

        case 506: 
            mouseDraggedOnCanvas(mouseevent);
            return;
        }
    }

    public boolean mousePressedOnCanvas(MouseEvent mouseevent)
    {
        Point point = mouseevent.getPoint();
        requestFocus();
        if(mainFolder == null)
            return true;
        ListItem listitem = mainFolder.locateItem(point.x, point.y);
        if(listitem == null || listitem.getCanvas() != this)
            return true;
        setCursor(hand_cursor);
        if(selectedItem != null)
            selectedItem.toggleSelected();
        listitem.setSelected(true);
        selectedItem = listitem;
        repaint();
        if(isDragSource())
        {
            ListItem listitem1 = (ListItem)listitem.clone();
            listitem1.location = new Point(0, 0);
            listitem1.setSelected(true);
            draggee = new ListItemComponent(listitem1, listitem);
            Point point1 = getAdjustedLocation(listitem.location);
            adjustmentFactor = listitem.location.y - point1.y;
            dragStartEvent = makeDragStart(draggee, listitem.location, point.x, point.y);
        }
        return true;
    }

    protected Point getAdjustedLocation(Point point)
    {
        return point;
    }

    protected int getAdjustmentFactor()
    {
        return adjustmentFactor;
    }

    protected boolean isDragSource()
    {
        return true;
    }

    private DragAndDropEvent makeDragStart(Component component, Point point, int i, int j)
    {
        Point point1 = new Point(i - point.x, j - point.y);
        DragStartInfo dragstartinfo = new DragStartInfo(this, component, point, point1, adjustmentFactor);
        DragAndDropEvent draganddropevent = new DragAndDropEvent(this, 0x16738, dragstartinfo);
        return draganddropevent;
    }

    public void adjustSize()
    {
        if(mainFolder == null)
            return;
        int i = mainFolder.getHeight() + mainFolder.getTotalHeight();
        int j = mainFolder.getTotalWidth();
        Dimension dimension = panel.getParent().getSize();
        ScrollPane scrollpane = (ScrollPane)panel.getParent();
        int k = scrollpane.getVScrollbarWidth();
        int l = scrollpane.getHScrollbarHeight();
        if(dimension.height - 4 > i)
        {
            i = dimension.height - 4;
            if(dimension.width - 4 < j)
                i -= l;
        }
        if(dimension.width - 4 > j)
        {
            j = dimension.width - 4;
            if(dimension.height - 4 < i)
                j -= k;
        }
        setSize(j, i);
        panel.setSize(j, i);
    }

    public boolean mouseDraggedOnCanvas(MouseEvent mouseevent)
    {
        if(dragStartEvent != null)
        {
            dispatchDragAndDropEvent(dragStartEvent);
            dragStartEvent = null;
        }
        Point point = mouseevent.getPoint();
        ListItem listitem = mainFolder.locateItemOnDrag(point.x, point.y);
        Graphics g = getGraphics();
        try
        {
            if(listitem == null)
            {
                eraseHiliteBox(g);
            } else
            {
                Object obj = null;
                if(draggee != null)
                {
                    ListItem listitem1 = draggee.getBaseItem();
                    if(listitem1 == null || listitem == null)
                    {
                        boolean flag = true;
                        return flag;
                    }
                    String s = listitem1.getTitle();
                    String s1 = listitem.getTitle();
                    if(s.equals(s1) && listitem1.getParentListContainer() == listitem.getParentListContainer())
                    {
                        eraseHiliteBox(g);
                        boolean flag1 = true;
                        return flag1;
                    }
                    eraseHiliteBox(g);
                    paintHiliteBox(g, listitem);
                }
            }
        }
        finally
        {
            g.dispose();
        }
        return true;
    }

    public boolean mouseReleasedOnCanvas(MouseEvent mouseevent)
    {
        dragStartEvent = null;
        draggee = null;
        setCursor(default_cursor);
        return false;
    }

    public void eraseHiliteBox(Graphics g)
    {
        if(hiliteRect == null)
        {
            return;
        } else
        {
            g.setColor(getBackground());
            g.drawRect(hiliteRect.x, hiliteRect.y, hiliteRect.width - 1, hiliteRect.height - 1);
            hiliteRect = null;
            return;
        }
    }

    public void paintHiliteBox(Graphics g, ListItem listitem)
    {
        hiliteRect = new Rectangle(listitem.getBoundingBox());
        hiliteRect.width = size().width;
        hiliteRect.x = 0;
        if(listitem instanceof ListElement)
            hiliteRect.height = 1;
        g.setColor(Color.black);
        g.drawRect(hiliteRect.x, hiliteRect.y, hiliteRect.width - 1, hiliteRect.height - 1);
    }

    public ListContainer getSelectedItemFolder()
    {
        ListContainer listcontainer = null;
        dbg("selectedItem " + selectedItem);
        if(selectedItem == null)
            return mainFolder;
        if(selectedItem instanceof ListContainer)
            listcontainer = (ListContainer)selectedItem;
        if(selectedItem instanceof ListElement)
            listcontainer = selectedItem.getParentListContainer();
        if(listcontainer == null)
            return mainFolder;
        else
            return listcontainer;
    }

    public ListItem getSelectedItem()
    {
        return selectedItem;
    }

    public void setMainContainer(ListContainer listcontainer)
    {
        mainFolder = listcontainer;
    }

    public ListContainer getMainContainer()
    {
        return mainFolder;
    }

    public String getTitle()
    {
        return title;
    }

    public void setCurrentItem(ListItem listitem)
    {
        currentIcon = listitem;
    }

    public void setSelectedItem(ListItem listitem)
    {
        selectedItem = listitem;
    }

    public void paint(Graphics g)
    {
        if(mainFolder == null)
            return;
        mainFolder.drawItem(g);
        if(currentIcon != null)
            currentIcon.drawItem(g);
    }

    public boolean itemMoved()
    {
        return handleDrop;
    }

    ListContainer mainFolder;
    ListItem currentIcon;
    ListItem movingIcon;
    ListItem selectedItem;
    Image element_img;
    Image open_img;
    Image closed_img;
    Cursor hand_cursor;
    Cursor default_cursor;
    Dimension canvas_size;
    Panel panel;
    boolean debug;
    String title;
    boolean dragged;
    boolean handleDrop;
    int adjustmentFactor;
    private ListItemComponent draggee;
    private DragAndDropEvent dragStartEvent;
    protected DragAndDropListener listeners;
    Rectangle hiliteRect;
}
