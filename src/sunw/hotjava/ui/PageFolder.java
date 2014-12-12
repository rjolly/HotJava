// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PageFolder.java

package sunw.hotjava.ui;

import java.io.PrintStream;
import java.util.Vector;

// Referenced classes of package sunw.hotjava.ui:
//            HotListEntry, UserMenu

public class PageFolder extends HotListEntry
    implements Cloneable
{

    public PageFolder(PageFolder pagefolder, String s, boolean flag)
    {
        super(pagefolder, "", s);
        closed = false;
        reverseSort = false;
        contents = new Vector(10);
        closed = flag;
    }

    public PageFolder(PageFolder pagefolder, String s, boolean flag, int i, int j, int k, String s1)
    {
        super(pagefolder, "", s, i, j);
        closed = false;
        reverseSort = false;
        setSortType(k);
        if(s1.equals("reverse"))
            reverseSort();
        contents = new Vector(10);
        closed = flag;
    }

    public void addElement(HotListEntry hotlistentry)
    {
        contents.addElement(hotlistentry);
    }

    public void addElement(HotListEntry hotlistentry, int i)
    {
        contents.insertElementAt(hotlistentry, i);
    }

    public void setClosedValue(boolean flag)
    {
        closed = flag;
    }

    public boolean closed()
    {
        return closed;
    }

    public Vector getContents()
    {
        return contents;
    }

    public int getCount()
    {
        return contents.size();
    }

    public UserMenu getUserMenu()
    {
        return pageFolderMenu;
    }

    public UserMenu setUserMenu(UserMenu usermenu)
    {
        pageFolderMenu = usermenu;
        return pageFolderMenu;
    }

    public PageFolder getLastAddedPageFolder()
    {
        if(contents != null && contents.size() != 0)
        {
            HotListEntry hotlistentry = (HotListEntry)contents.lastElement();
            if(hotlistentry instanceof PageFolder)
                return (PageFolder)hotlistentry;
            else
                return hotlistentry.parent;
        } else
        {
            return null;
        }
    }

    public PageFolder getChildFolder(String s)
    {
        if(contents != null)
        {
            int i = contents.size();
            for(int j = 0; j < i; j++)
            {
                HotListEntry hotlistentry = (HotListEntry)contents.elementAt(j);
                if((hotlistentry instanceof PageFolder) && hotlistentry.title.equals(s))
                    return (PageFolder)hotlistentry;
            }

        }
        return null;
    }

    public boolean isChild(String s)
    {
        if(contents != null)
        {
            int i = contents.size();
            for(int j = 0; j < i; j++)
            {
                HotListEntry hotlistentry = (HotListEntry)contents.elementAt(j);
                if(hotlistentry.title.equals(s))
                    return true;
            }

        }
        return false;
    }

    public void display()
    {
        System.out.println("Folder [" + super.title + "] size = " + contents.size());
        for(int i = 0; i < contents.size(); i++)
        {
            HotListEntry hotlistentry = (HotListEntry)contents.elementAt(i);
            if(hotlistentry instanceof PageFolder)
            {
                PageFolder pagefolder = (PageFolder)hotlistentry;
                pagefolder.display();
            } else
            {
                System.out.println(hotlistentry.title);
            }
        }

        System.out.println("end of Folder [" + super.title + "]");
    }

    public void setSortType(int i)
    {
        sortType = i;
    }

    public int getSortType()
    {
        return sortType;
    }

    public void reverseSort()
    {
        reverseSort = !reverseSort;
    }

    public boolean isReverseSorted()
    {
        return reverseSort;
    }

    public PageFolder getParent()
    {
        return super.parent;
    }

    public Object clone()
    {
        try
        {
            PageFolder pagefolder = (PageFolder)super.clone();
            pagefolder.contents = (Vector)contents.clone();
            pagefolder.setUserMenu(null);
            int i = pagefolder.contents.size();
            for(int j = 0; j < i; j++)
            {
                HotListEntry hotlistentry = (HotListEntry)pagefolder.contents.elementAt(j);
                if(hotlistentry instanceof PageFolder)
                {
                    PageFolder pagefolder1 = (PageFolder)hotlistentry;
                    PageFolder pagefolder2 = (PageFolder)pagefolder1.clone();
                    pagefolder.contents.setElementAt(pagefolder2, j);
                }
            }

            return pagefolder;
        }
        catch(Exception exception)
        {
            System.out.println("Exception " + exception);
            exception.printStackTrace();
            return null;
        }
    }

    public int getItemPos(HotListEntry hotlistentry)
    {
        for(int i = 0; i < contents.size(); i++)
        {
            HotListEntry hotlistentry1 = (HotListEntry)contents.elementAt(i);
            if(hotlistentry1 == hotlistentry)
                return i;
        }

        return -1;
    }

    public String toString()
    {
        return "PageFolder[title=" + super.title + " visit_count = " + super.visit_count + " last_visit = " + super.last_visit + "]";
    }

    UserMenu pageFolderMenu;
    Vector contents;
    boolean closed;
    int sortType;
    boolean reverseSort;
}
