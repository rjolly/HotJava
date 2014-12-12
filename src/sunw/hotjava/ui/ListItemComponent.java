// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ListItemComponent.java

package sunw.hotjava.ui;

import java.awt.*;

// Referenced classes of package sunw.hotjava.ui:
//            ListItem

public class ListItemComponent extends Canvas
{

    ListItemComponent(ListItem listitem, ListItem listitem1)
    {
        listItem = listitem;
        baseListItem = listitem1;
    }

    ListItem getItem()
    {
        return listItem;
    }

    public ListItem getBaseItem()
    {
        return baseListItem;
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    public void paint(Graphics g)
    {
        listItem.drawItem(g);
    }

    public Dimension getPreferredSize()
    {
        return preferredSize();
    }

    public Dimension getMinimumSize()
    {
        return minimumSize();
    }

    public Dimension preferredSize()
    {
        return getItemSize();
    }

    public Dimension minimumSize()
    {
        return getItemSize();
    }

    public Dimension getItemSize()
    {
        Graphics g = getGraphics();
        listItem.setBoundingBox(g);
        g.dispose();
        Rectangle rectangle = listItem.getBoundingBox();
        return new Dimension(rectangle.width, rectangle.height);
    }

    private ListItem listItem;
    private ListItem baseListItem;
}
