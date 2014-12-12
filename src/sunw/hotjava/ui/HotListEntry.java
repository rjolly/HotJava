// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HotListEntry.java

package sunw.hotjava.ui;

import java.util.Vector;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.ui:
//            PageFolder

public class HotListEntry
    implements Cloneable
{

    public HotListEntry(PageFolder pagefolder, String s, String s1)
    {
        parent = pagefolder;
        url = s;
        title = s1;
    }

    public HotListEntry(PageFolder pagefolder, String s, String s1, int i, int j)
    {
        parent = pagefolder;
        url = s;
        title = s1;
        visit_count = i;
        last_visit = j;
    }

    public HotListEntry(PageFolder pagefolder, String s)
    {
        this(pagefolder, s, s);
    }

    public HotListEntry(PageFolder pagefolder)
    {
        this(pagefolder, null, null);
    }

    public void removeMyself()
    {
        parent.getContents().removeElement(this);
    }

    public void setParentListContainer(PageFolder pagefolder)
    {
        parent = pagefolder;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String s)
    {
        title = s;
    }

    public void setURL(String s)
    {
        url = s;
    }

    public void setVisitCount(int i)
    {
        visit_count = i;
    }

    public int getVisitCount()
    {
        return visit_count;
    }

    public void setLastVisit(int i)
    {
        last_visit = i;
    }

    public int getLastVisit()
    {
        return last_visit;
    }

    public PageFolder getParent()
    {
        return parent;
    }

    public PageFolder getGrandParent()
    {
        if(parent != null)
            return parent.getParent();
        else
            return null;
    }

    public Object clone()
    {
        try
        {
            return super.clone();
        }
        catch(Exception _ex)
        {
            return null;
        }
    }

    public String toString()
    {
        return "HotListEntry[url=" + url + " title=" + title + "]";
    }

    public boolean isInMenuPlacesFolder()
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("hjbrowser");
        String s = hjbproperties.getProperty("hotlist.menuplaces", "");
        for(PageFolder pagefolder = parent; pagefolder != null; pagefolder = pagefolder.getParent())
            if(s.equals(pagefolder.getTitle()))
                return true;

        return false;
    }

    public String url;
    public String title;
    public String add_date;
    PageFolder parent;
    public int visit_count;
    public int last_visit;
}
