// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ListElement.java

package sunw.hotjava.ui;

import java.awt.*;

// Referenced classes of package sunw.hotjava.ui:
//            ListItem, ListContainer

public class ListElement extends ListItem
{

    public ListElement(ListContainer listcontainer, String s, Image image)
    {
        super(listcontainer, s);
        img = image;
        if(image == null)
        {
            return;
        } else
        {
            super.icon_width = image.getWidth(super.canvas);
            super.icon_height = image.getHeight(super.canvas);
            return;
        }
    }

    public void drawItem(Graphics g)
    {
        Point point = super.location;
        int i = point.x;
        int j = point.y;
        setBoundingBox(g);
        showBackground(g);
        if(img != null)
            g.drawImage(img, i, j, super.canvas);
        g.setColor(Color.black);
        g.drawString(super.text, i + super.icon_width, j + super.icon_height);
    }

    public String toString()
    {
        return "ListElement [text=" + super.text + ", boundingBox = " + super.boundingBox + "]";
    }

    Image img;
}
