// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Webmark.java

package sunw.hotjava.ui;


// Referenced classes of package sunw.hotjava.ui:
//            HotListEntry, PageFolder

public class Webmark extends HotListEntry
    implements Cloneable
{

    public Webmark(PageFolder pagefolder, String s, String s1)
    {
        this(pagefolder, s, s1, 0, ((String) (null)));
    }

    public Webmark(PageFolder pagefolder, String s, String s1, int i, String s2)
    {
        super(pagefolder, s, s1);
        super.last_visit = i;
        last_modified = s2;
    }

    public Webmark(PageFolder pagefolder, String s, String s1, int i, int j)
    {
        super(pagefolder, s, s1, i, j);
    }

    public Webmark(PageFolder pagefolder, String s)
    {
        this(pagefolder, s, s, 0, ((String) (null)));
    }

    public Webmark(PageFolder pagefolder)
    {
        this(pagefolder, null, null, 0, ((String) (null)));
    }

    public String getURL()
    {
        return super.url;
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
        return "Webmark[url=" + super.url + " title=" + super.title + " last_visit =" + super.last_visit + " last_modified =" + last_modified + " visit_count = " + super.visit_count + " last_visit = " + super.last_visit + "]";
    }

    public String last_modified;
    PageFolder parent;
}
