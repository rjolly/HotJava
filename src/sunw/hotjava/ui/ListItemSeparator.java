// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ListItemSeparator.java

package sunw.hotjava.ui;

import java.awt.*;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.ui:
//            ListElement, ListItem, ListContainer

public class ListItemSeparator extends ListElement
{

    public ListItemSeparator(ListContainer listcontainer, String s, Image image)
    {
        super(listcontainer, s, image);
        height = 2;
    }

    public void drawItem(Graphics g)
    {
        Point point = super.location;
        int i = point.x;
        int j = point.y;
        if(width == 0)
            width = super.canvas.getPreferredSize().width;
        setBoundingBox(g);
        if(super.selected || grabbed())
        {
            HJBProperties hjbproperties = HJBProperties.getHJBProperties("hjbrowser");
            g.setColor(hjbproperties.getColor("hotlistframe.selection.color", Color.yellow));
            g.fillRect(super.boundingBox.x, super.boundingBox.y, super.boundingBox.width + 1, super.boundingBox.height - 1);
        }
        g.setColor(Color.black);
        g.fillRect(i + super.icon_width, j + super.icon_height, width - (i + 10), height);
    }

    public void setBoundingBox(Graphics g)
    {
        Point point = super.location;
        int i = point.x;
        int j = point.y;
        super.boundingBox = new Rectangle(i, j + 4 * height, width - (i + 10), super.icon_height);
    }

    public void setWidth(int i)
    {
        width = i;
    }

    public String toString()
    {
        return "ListItemSeparator [parent = " + super.myContainer + " location = " + super.location + "]";
    }

    int width;
    int height;
}
