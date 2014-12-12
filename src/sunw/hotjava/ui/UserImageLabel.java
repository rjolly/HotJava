// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   UserImageLabel.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.image.ImageObserver;
import sunw.hotjava.misc.HJBProperties;

public class UserImageLabel extends Canvas
{

    public UserImageLabel(String s, HJBProperties hjbproperties)
    {
        properties = hjbproperties;
        setImage(s);
    }

    public Dimension minimumSize()
    {
        return size();
    }

    public Dimension preferredSize()
    {
        return size();
    }

    public void paint(Graphics g)
    {
        update(g);
    }

    public void update(Graphics g)
    {
        try
        {
            int i = image.getWidth(this);
            if(i < 0)
                return;
            int j = image.getHeight(this);
            if(j < 0)
            {
                return;
            } else
            {
                Dimension dimension = size();
                g.drawImage(image, (dimension.width - i) / 2, (dimension.height - j) / 2, getBackground(), this);
                return;
            }
        }
        catch(NullPointerException _ex)
        {
            return;
        }
    }

    public void setImage(String s)
    {
        if(s == null)
            return;
        setName(s);
        image = properties.getImage(properties.getProperty("imagelabel." + s));
        int i = 20;
        int j = 20;
        try
        {
            String s1 = properties.getProperty("imagelabel." + s + ".width");
            if(s1 != null)
                i = Integer.parseInt(s1);
        }
        catch(NumberFormatException _ex) { }
        try
        {
            String s2 = properties.getProperty("imagelabel." + s + ".height");
            if(s2 != null)
                j = Integer.parseInt(s2);
        }
        catch(NumberFormatException _ex) { }
        resize(i, j);
        java.awt.Color color = properties.getColor(s + ".background", null);
        if(color != null)
            setBackground(color);
        prepareImage(image, this);
        repaint();
    }

    public boolean imageUpdate(Image image1, int i, int j, int k, int l, int i1)
    {
        if(image1 == image && (i & 0x28) != 0)
            repaint((i & 0x20) != 0 ? 0 : 100);
        return (i & 0xa0) == 0;
    }

    public void addNotify()
    {
        super.addNotify();
        if(image != null)
            prepareImage(image, this);
    }

    Image image;
    private HJBProperties properties;
}
