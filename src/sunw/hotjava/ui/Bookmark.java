// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Bookmark.java

package sunw.hotjava.ui;

import java.awt.Image;

// Referenced classes of package sunw.hotjava.ui:
//            ListElement, HotListEntry, Webmark, Folder

public class Bookmark extends ListElement
{

    public Bookmark(Folder folder, Webmark webmark, Image image)
    {
        super(folder, ((HotListEntry) (webmark)).title, image);
        entry = webmark;
    }

    public Webmark getWebmark()
    {
        return (Webmark)entry;
    }

    public HotListEntry getHotListEntry()
    {
        return entry;
    }

    HotListEntry entry;
}
