// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BrowserListCanvas.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.PrintStream;
import java.util.Vector;

// Referenced classes of package sunw.hotjava.ui:
//            HotCanvas, Folder, HotListFrame, ListCanvas, 
//            ListContainer, ListItem, PageFolder, HotList, 
//            HotListEntry

public class BrowserListCanvas extends HotCanvas
{

    public BrowserListCanvas(HotList hotlist, Panel panel, String s, int i, int j, HotListFrame hotlistframe)
    {
        super(hotlist, panel, s, i, j, hotlistframe);
    }

    public void initialize(PageFolder pagefolder, HotCanvas hotcanvas)
    {
        canvas = hotcanvas;
        initialize(pagefolder);
    }

    public boolean mousePressedOnCanvas(MouseEvent mouseevent)
    {
        oldSelection = getSelectedItem();
        super.mousePressedOnCanvas(mouseevent);
        if(mouseevent.getClickCount() == 2)
        {
            super.hotFolder.recalculatePositions(true);
            adjustSize();
        }
        if(getSelectedItem() != null)
            super.frm.setListName(getSelectedItem().getTitle());
        redispatchEvent(mouseevent);
        return false;
    }

    public void processHiliteEvent(Component component, Point point)
    {
        ListItem listitem = super.mainFolder.locateItemOnDrag(point.x, point.y);
        Graphics g = getGraphics();
        try
        {
            if(listitem != null)
            {
                eraseHiliteBox(g);
                paintHiliteBox(g, listitem);
            } else
            {
                eraseHiliteBox(g);
            }
        }
        finally
        {
            g.dispose();
        }
    }

    protected boolean isDragSource()
    {
        return false;
    }

    public ListItem previousSelection()
    {
        return oldSelection;
    }

    public Folder getFolder(String s)
    {
        Vector vector = super.hotFolder.getContents();
        for(int i = 0; i < vector.size(); i++)
        {
            Folder folder = (Folder)vector.elementAt(i);
            String s1 = folder.getTitle();
            if(s1.equals(s))
                return folder;
        }

        return null;
    }

    public void paint(Graphics g)
    {
        ListContainer listcontainer = getMainContainer();
        if(listcontainer == null)
        {
            return;
        } else
        {
            listcontainer.drawChildren(g);
            return;
        }
    }

    public void addEntry(HotListEntry hotlistentry, boolean flag)
    {
        try
        {
            Folder folder = null;
            folder = (Folder)getMainContainer();
            if(hotlistentry instanceof PageFolder)
                folder.addFolder(folder, (PageFolder)hotlistentry);
            super.hotFolder.recalculatePositions(flag);
            adjustSize();
            repaint();
            return;
        }
        catch(Exception exception)
        {
            System.out.println("addEntry: " + exception);
        }
    }

    public void delItem(HotListEntry hotlistentry, boolean flag)
    {
        ListItem listitem = super.hotFolder.findEntrySibling(hotlistentry);
        if(listitem == null)
        {
            return;
        } else
        {
            listitem.removeMyself();
            setCurrentItem(null);
            setSelectedItem(null);
            super.hotFolder.recalculatePositions(flag);
            adjustSize();
            repaint();
            return;
        }
    }

    public HotCanvas canvas;
    ListItem oldSelection;
}
