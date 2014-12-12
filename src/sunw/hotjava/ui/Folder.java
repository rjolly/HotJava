// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Folder.java

package sunw.hotjava.ui;

import java.awt.*;
import java.io.PrintStream;
import java.util.Vector;

// Referenced classes of package sunw.hotjava.ui:
//            ListContainer, Bookmark, EntrySeparator, HotCanvas, 
//            HotList, HotListEntry, HotListFrame, ListCanvas, 
//            ListItem, PageFolder, Separator, Webmark

public class Folder extends ListContainer
{

    public Folder(HotCanvas hotcanvas, Folder folder, String s, Image image, Image image1)
    {
        super(folder, s);
        setListCanvas(hotcanvas);
        setListContainerImages(image, image1);
    }

    public Folder(HotCanvas hotcanvas, Folder folder, String s)
    {
        super(folder, s);
        setListCanvas(hotcanvas);
    }

    public Folder(ListCanvas listcanvas, Folder folder, PageFolder pagefolder, Image image, Image image1)
    {
        super(folder, ((HotListEntry) (pagefolder)).title);
        setListCanvas(listcanvas);
        entry = pagefolder;
        setContainerState(pagefolder.closed());
        setListContainerImages(image, image1);
    }

    public PageFolder getPageFolder()
    {
        return (PageFolder)entry;
    }

    public void initialize(PageFolder pagefolder)
    {
        Vector vector = pagefolder.getContents();
        if(vector == null)
            return;
        for(int i = 0; i < vector.size(); i++)
        {
            HotListEntry hotlistentry = (HotListEntry)vector.elementAt(i);
            if(hotlistentry instanceof Webmark)
                addBookmark(this, (Webmark)hotlistentry);
            if(hotlistentry instanceof Separator)
                addSeparator(this, (Separator)hotlistentry);
            if(hotlistentry instanceof PageFolder)
            {
                Folder folder = addFolder(this, (PageFolder)hotlistentry);
                folder.initialize((PageFolder)hotlistentry);
            }
        }

    }

    public boolean removeItem(ListItem listitem)
    {
        if(super.contents.removeElement(listitem))
            return true;
        for(int i = 0; i < super.contents.size(); i++)
        {
            ListItem _tmp = (ListItem)super.contents.elementAt(i);
            if(listitem instanceof Folder)
            {
                Folder folder = (Folder)listitem;
                if(folder.removeItem(listitem))
                    return true;
            }
        }

        return false;
    }

    public boolean resolveItemLocation(ListItem listitem, Point point)
    {
        HotList hotlist = HotList.getHotList();
        HotListFrame hotlistframe = HotListFrame.getHotListFrame();
        if(intersectsOnDrop(point) && listitem != this)
        {
            java.awt.Component component = listitem.getCanvas();
            hotlist.reparent(getHotListEntry(listitem), (PageFolder)entry, 0);
            super.total_contents = recalculatePositions();
            hotlistframe.updateCanvas(component, (Folder)listitem.getParentListContainer());
            listitem.setParentListContainer(this);
            hotlist.exportHTMLFile();
            return true;
        }
        if(closed() && !isTopList())
            return false;
        for(int i = 0; i < super.contents.size(); i++)
        {
            ListItem listitem1 = (ListItem)super.contents.elementAt(i);
            if(listitem1 instanceof Folder)
            {
                Folder folder = (Folder)listitem1;
                if(listitem instanceof Folder)
                {
                    Folder folder2 = (Folder)listitem;
                    if(checkParentage(folder2, folder))
                        continue;
                }
                if(folder.resolveItemLocation(listitem, point))
                {
                    if(isTopList())
                        super.total_contents = recalculatePositions(true);
                    else
                        super.total_contents = recalculatePositions();
                    return true;
                }
            } else
            if(listitem1.intersectsOnDrop(point) && listitem != listitem1)
            {
                if(listitem instanceof Folder)
                {
                    Folder folder1 = (Folder)listitem;
                    if(checkParentage(folder1, this))
                    {
                        super.total_contents = recalculatePositions();
                        return true;
                    }
                }
                ListContainer listcontainer = listitem.getParentListContainer();
                if(listcontainer != this || super.contents.indexOf(listitem) > i)
                    hotlist.reparent(getHotListEntry(listitem), (PageFolder)entry, i);
                else
                    hotlist.reparent(getHotListEntry(listitem), (PageFolder)entry, i - 1);
                super.total_contents = recalculatePositions();
                hotlistframe.updateCanvas(listitem.getCanvas(), this);
                listitem.setParentListContainer(this);
                hotlist.exportHTMLFile();
                return true;
            }
        }

        super.total_contents = recalculatePositions();
        return false;
    }

    public boolean isTopList()
    {
        ListContainer listcontainer = getParentListContainer();
        return listcontainer == null;
    }

    public boolean doubleClick(int i, int j)
    {
        getWidth();
        getHeight();
        Rectangle rectangle = getBoundingBox();
        HotCanvas hotcanvas = (HotCanvas)super.listCanvas;
        HotList hotlist = hotcanvas.getHotList();
        if(rectangle.inside(i, j))
            if(closed())
                hotlist.changeFolderValue(entry, false);
            else
                hotlist.changeFolderValue(entry, true);
        return true;
    }

    public Folder addFolder(Folder folder, PageFolder pagefolder)
    {
        PageFolder pagefolder1 = pagefolder.getParent();
        int i = pagefolder1.getItemPos(pagefolder);
        if(i == -1)
            i = -5;
        Image image = super.listCanvas.getOpenContainerImage();
        Image image1 = super.listCanvas.getClosedContainerImage();
        Folder folder1 = new Folder(super.listCanvas, folder, pagefolder, image, image1);
        folder1.setCanvas(super.listCanvas);
        folder.addItem(folder1, i);
        return folder1;
    }

    public Bookmark addBookmark(Folder folder, Webmark webmark)
    {
        PageFolder pagefolder = webmark.getParent();
        int i = pagefolder.getItemPos(webmark);
        if(i == -1)
            i = -5;
        Bookmark bookmark = new Bookmark(folder, webmark, super.listCanvas.getListElementImage());
        bookmark.setCanvas(super.listCanvas);
        folder.addItem(bookmark, i);
        return bookmark;
    }

    public EntrySeparator addSeparator(Folder folder, Separator separator)
    {
        PageFolder pagefolder = separator.getParent();
        int i = pagefolder.getItemPos(separator);
        if(i == -1)
            i = -5;
        Image image = super.listCanvas.getListElementImage();
        EntrySeparator entryseparator = new EntrySeparator(folder, separator, image);
        entryseparator.setCanvas(super.listCanvas);
        folder.addItem(entryseparator, i);
        return entryseparator;
    }

    public ListItem findEntrySibling(HotListEntry hotlistentry)
    {
        for(int i = 0; i < super.contents.size(); i++)
        {
            ListItem listitem = (ListItem)super.contents.elementAt(i);
            HotListEntry hotlistentry1 = getHotListEntry(listitem);
            if(hotlistentry1 == hotlistentry)
                return listitem;
            if(listitem instanceof Folder)
            {
                Folder folder = (Folder)listitem;
                ListItem listitem1;
                if((listitem1 = folder.findEntrySibling(hotlistentry)) != null)
                    return listitem1;
            }
        }

        return null;
    }

    public void setHotListEntry(HotListEntry hotlistentry)
    {
        entry = hotlistentry;
    }

    public HotListEntry getHotListEntry()
    {
        return entry;
    }

    private HotListEntry getHotListEntry(ListItem listitem)
    {
        HotListEntry hotlistentry = null;
        if(listitem instanceof Folder)
        {
            Folder folder = (Folder)listitem;
            hotlistentry = folder.entry;
        } else
        if(listitem instanceof Bookmark)
        {
            Bookmark bookmark = (Bookmark)listitem;
            hotlistentry = bookmark.entry;
        } else
        if(listitem instanceof EntrySeparator)
        {
            EntrySeparator entryseparator = (EntrySeparator)listitem;
            hotlistentry = entryseparator.entry;
        } else
        {
            System.out.println(" Invalid ListItem ");
        }
        return hotlistentry;
    }

    HotListEntry entry;
}
