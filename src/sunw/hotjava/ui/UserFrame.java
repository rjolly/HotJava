// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   UserFrame.java

package sunw.hotjava.ui;

import java.awt.*;
import sunw.hotjava.misc.HJBProperties;

public class UserFrame extends Frame
{

    public UserFrame(String s)
    {
        name = s;
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("hjbrowser");
        setName(s);
        java.awt.Font font = hjbproperties.getFont(s + ".font");
        if(font != null)
            setFont(font);
        java.awt.Color color = hjbproperties.getColor(s + ".background", null);
        if(color != null)
            setBackground(color);
        color = hjbproperties.getColor(s + ".foreground", null);
        if(color != null)
            setForeground(color);
        String s1 = getDimName();
        int i = hjbproperties.getInteger(hjbproperties.getProperty(s1 + ".x") != null ? s1 + ".x" : s, 20);
        int j = hjbproperties.getInteger(hjbproperties.getProperty(s1 + ".y") != null ? s1 + ".y" : s, 20);
        int k = hjbproperties.getInteger(hjbproperties.getProperty(s1 + ".width") != null ? s1 + ".width" : s, 600);
        int l = hjbproperties.getInteger(hjbproperties.getProperty(s1 + ".height") != null ? s1 + ".height" : s, 400);
        setBounds(i, j, k, l);
        String s2 = hjbproperties.getProperty(s + ".title");
        if(s2 != null)
            setTitle(s2);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        try
        {
            java.net.URL url = ClassLoader.getSystemResource("lib/images/hotjava.icon.gif");
            java.awt.Image image = toolkit.getImage(url);
            setIconImage(image);
            return;
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }

    private String getDimName()
    {
        Dimension dimension = getToolkit().getScreenSize();
        return name + "." + dimension.width + "x" + dimension.height;
    }

    public void saveStateToProperties()
    {
        Dimension dimension = getSize();
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("hjbrowser");
        String s = getDimName();
        if(hjbproperties != null)
        {
            if(dimension.width > 100 && dimension.height > 100)
            {
                hjbproperties.put(s + ".width", String.valueOf(dimension.width));
                hjbproperties.put(s + ".height", String.valueOf(dimension.height));
            }
            if(isShowing())
            {
                Point point = getLocation();
                Dimension dimension1 = getToolkit().getScreenSize();
                if(point.x < 0 || point.x > dimension1.width)
                    point.x = 0;
                hjbproperties.put(s + ".x", String.valueOf(point.x));
                if(point.y < 0 || point.y > dimension1.height)
                    point.y = 0;
                hjbproperties.put(s + ".y", String.valueOf(point.y));
            }
            hjbproperties.save();
        }
    }

    public void centerMe()
    {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension dimension1 = getSize();
        setLocation((dimension.width - dimension1.width) / 2, (dimension.height - dimension1.height) / 2);
    }

    private String name;
}
