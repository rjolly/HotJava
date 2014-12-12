// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HotCanvas.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.PrintStream;
import java.util.EventObject;

// Referenced classes of package sunw.hotjava.ui:
//            ListCanvas, DragAndDropEvent, Folder, HotListEntry, 
//            HotListFrame, ListContainer, ListItem, PageFolder, 
//            Separator, Webmark, HotList

public class HotCanvas extends ListCanvas
{

    public HotCanvas(HotList hotlist1, Panel panel, String s, int i, int j)
    {
        super(panel, s, i, j);
        hotlist = hotlist1;
        setSize(i, j);
    }

    public HotCanvas(HotList hotlist1, Panel panel, String s, int i, int j, HotListFrame hotlistframe)
    {
        super(panel, s, i, j);
        hotlist = hotlist1;
        frm = hotlistframe;
        setSize(i, j);
    }

    public void createInitialFolder()
    {
        try
        {
            String s = getTitle();
            getOpenContainerImage();
            getClosedContainerImage();
            hotFolder = new Folder(this, null, s);
            setMainContainer(hotFolder);
            hotFolder.setCanvas(this);
            return;
        }
        catch(Exception exception)
        {
            System.out.println("CreateInit: " + exception);
        }
    }

    public void createInitialFolder(Folder folder)
    {
        try
        {
            String s = getTitle();
            java.awt.Image image = getOpenContainerImage();
            java.awt.Image image1 = getClosedContainerImage();
            hotFolder = new Folder(this, folder, s, image, image1);
            setMainContainer(hotFolder);
            hotFolder.setCanvas(this);
            return;
        }
        catch(Exception exception)
        {
            System.out.println("CreateInit: " + exception);
        }
    }

    public void initialize(PageFolder pagefolder)
    {
        hotFolder.initialize(pagefolder);
        hotFolder.recalculatePositions();
        hotFolder.setHotListEntry(pagefolder);
        Graphics g = getGraphics();
        if(g != null)
            try
            {
                hotFolder.setAllBoundingBoxes(g);
            }
            finally
            {
                g.dispose();
            }
        adjustSize();
    }

    public HotList getHotList()
    {
        return hotlist;
    }

    public void addEntry(HotListEntry hotlistentry)
    {
        try
        {
            Folder folder = null;
            dbg("addEntry");
            folder = (Folder)getSelectedItemFolder();
            HotListEntry hotlistentry1 = folder.getHotListEntry();
            if(hotlistentry.getParent() != hotlistentry1)
            {
                for(Folder folder1 = (Folder)folder.getParentListContainer().getParentListContainer(); folder1 != null && !folder1.isTopList(); folder1 = (Folder)folder1.getParentListContainer())
                    folder = (Folder)folder.getParentListContainer();

                if(folder.getHotListEntry() != hotlistentry.getParent())
                    return;
            }
            if(hotlistentry instanceof Webmark)
                folder.addBookmark(folder, (Webmark)hotlistentry);
            else
            if(hotlistentry instanceof PageFolder)
                folder.addFolder(folder, (PageFolder)hotlistentry);
            else
            if(hotlistentry instanceof Separator)
                folder.addSeparator(folder, (Separator)hotlistentry);
            hotFolder.recalculatePositions();
            adjustSize();
            repaint();
            return;
        }
        catch(Exception exception)
        {
            System.out.println("addEntry: " + exception);
        }
    }

    public void changeFolderState(HotListEntry hotlistentry)
    {
        ListItem listitem = hotFolder.findEntrySibling(hotlistentry);
        if(listitem == null)
            return;
        if(!(listitem instanceof Folder))
            return;
        Folder folder = (Folder)listitem;
        if(!(hotlistentry instanceof PageFolder))
        {
            return;
        } else
        {
            PageFolder pagefolder = (PageFolder)hotlistentry;
            folder.setClosedValue(pagefolder.closed());
            return;
        }
    }

    public void delItem(HotListEntry hotlistentry)
    {
        ListItem listitem = hotFolder.findEntrySibling(hotlistentry);
        if(listitem == null)
        {
            return;
        } else
        {
            listitem.removeMyself();
            setCurrentItem(null);
            setSelectedItem(null);
            hotFolder.recalculatePositions();
            adjustSize();
            repaint();
            return;
        }
    }

    protected void redispatchEvent(MouseEvent mouseevent)
    {
        Panel panel = frm.getContainingPanelForCanvas(this);
        if(panel != null)
        {
            Point point = panel.getLocation();
            mouseevent.translatePoint(point.x, point.y - getAdjustmentFactor());
            panel.dispatchEvent(mouseevent);
        }
    }

    public boolean mousePressedOnCanvas(MouseEvent mouseevent)
    {
        Point point = mouseevent.getPoint();
        boolean flag = super.mousePressedOnCanvas(mouseevent);
        if(mouseevent.getClickCount() == 2)
        {
            ListItem listitem = super.mainFolder.locateItem(point.x, point.y);
            if(listitem == null)
                return flag;
            if(!listitem.doubleClick(point.x, point.y))
            {
                redispatchEvent(mouseevent);
                return false;
            }
            super.mainFolder.recalculatePositions();
            adjustSize();
        }
        frm.selectCanvas(this);
        return flag;
    }

    public boolean mouseDraggedOnCanvas(MouseEvent mouseevent)
    {
        if(!frm.readOnlyList)
        {
            super.mouseDraggedOnCanvas(mouseevent);
            redispatchEvent(mouseevent);
        }
        return true;
    }

    public boolean mouseReleasedOnCanvas(MouseEvent mouseevent)
    {
        super.mouseReleasedOnCanvas(mouseevent);
        redispatchEvent(mouseevent);
        return true;
    }

    public boolean processDragAndDropEvent(DragAndDropEvent draganddropevent)
    {
        Component component = (Component)draganddropevent.getSource();
        switch(draganddropevent.getID())
        {
        default:
            break;

        case 91959: 
            if(component instanceof Panel)
            {
                Panel panel = (Panel)component;
                Panel panel2 = frm.getContainingPanelForCanvas(this);
                if(panel == panel2)
                    return super.processDragAndDropEvent(draganddropevent);
            }
            break;

        case 91962: 
            if(!(component instanceof Panel))
                break;
            Panel panel1 = (Panel)component;
            Panel panel3 = frm.getContainingPanelForCanvas(this);
            if(panel1 == panel3)
                return super.processDragAndDropEvent(draganddropevent);
            Graphics g = getGraphics();
            try
            {
                eraseHiliteBox(g);
                break;
            }
            finally
            {
                g.dispose();
            }
        }
        return true;
    }

    protected Point getAdjustedLocation(Point point)
    {
        ScrollPane scrollpane = findScrollPane();
        Point point1 = new Point();
        point1.x = point.x;
        point1.y = point.y;
        if(scrollpane != null)
        {
            Point point2 = scrollpane.getScrollPosition();
            point1.y -= point2.y;
            return point1;
        } else
        {
            return point;
        }
    }

    public ScrollPane findScrollPane()
    {
        for(java.awt.Container container = getParent(); container != null; container = container.getParent())
            if(container instanceof ScrollPane)
                return (ScrollPane)container;

        return null;
    }

    public ComponentListener getListener()
    {
        return componentEventsListener;
    }

    HotList hotlist;
    Folder hotFolder;
    HotListFrame frm;
    ComponentListener componentEventsListener = new ComponentAdapter() {

        public void componentResized(ComponentEvent componentevent)
        {
            adjustSize();
        }

    }
;

    // Unreferenced inner class sunw/hotjava/ui/HotCanvas$2

/* anonymous class */
    final class _cls2 extends ComponentAdapter
    {

        public void componentResized(ComponentEvent componentevent)
        {
            adjustSize();
        }

    }

}
