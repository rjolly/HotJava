// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Separator.java

package sunw.hotjava.ui;


// Referenced classes of package sunw.hotjava.ui:
//            HotListEntry, PageFolder

public class Separator extends HotListEntry
{

    public Separator(PageFolder pagefolder, String s, String s1)
    {
        super(pagefolder, s, s1);
        parent = pagefolder;
    }

    public String toString()
    {
        return "Separator[Parent = " + parent + " url =" + super.url + " title = " + super.title + "]";
    }

    public PageFolder getParent()
    {
        return parent;
    }

    PageFolder parent;
}
