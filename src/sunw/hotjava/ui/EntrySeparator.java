// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   EntrySeparator.java

package sunw.hotjava.ui;

import java.awt.*;

// Referenced classes of package sunw.hotjava.ui:
//            ListItemSeparator, HotListEntry, ListItem, Folder, 
//            Separator

public class EntrySeparator extends ListItemSeparator
{

    public EntrySeparator(Folder folder, Separator separator, Image image)
    {
        super(folder, ((HotListEntry) (separator)).title, image);
        entry = separator;
    }

    public HotListEntry getHotListEntry()
    {
        return entry;
    }

    public void drawItem(Graphics g)
    {
        java.awt.Container container = super.canvas.getParent().getParent();
        if(container instanceof ScrollPane)
        {
            ScrollPane scrollpane = (ScrollPane)container;
            Dimension dimension = scrollpane.getViewportSize();
            setWidth(dimension.width);
        }
        super.drawItem(g);
    }

    HotListEntry entry;
}
