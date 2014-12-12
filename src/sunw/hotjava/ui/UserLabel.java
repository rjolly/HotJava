// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   UserLabel.java

package sunw.hotjava.ui;

import java.awt.*;
import sunw.hotjava.misc.HJBProperties;

public class UserLabel extends Label
{

    public UserLabel(String s)
    {
        setName(s);
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("hjbrowser");
        java.awt.Font font = hjbproperties.getFont(s + ".font");
        if(font != null)
            setFont(font);
        java.awt.Color color = hjbproperties.getColor(s + ".background", null);
        if(color != null)
            setBackground(color);
        color = hjbproperties.getColor(s + ".foreground", null);
        if(color != null)
            setForeground(color);
        String s1 = hjbproperties.getProperty(s + ".text");
        if(s1 != null)
            setText(s1);
        chars = hjbproperties.getInteger(s + ".chars", 0);
        setAlignment(getAlign(s + ".align"));
    }

    int getAlign(String s)
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("hjbrowser");
        String s1 = hjbproperties.getProperty(s);
        if("right".equalsIgnoreCase(s1))
            return 2;
        return !"center".equalsIgnoreCase(s1) ? 0 : 1;
    }

    public Dimension minimumSize()
    {
        Dimension dimension = super.minimumSize();
        if(chars == 0)
        {
            return dimension;
        } else
        {
            FontMetrics fontmetrics = getFontMetrics(getFont());
            dimension.width = fontmetrics.stringWidth("0") * chars;
            return dimension;
        }
    }

    public Dimension preferredSize()
    {
        if(chars == 0)
            return super.preferredSize();
        else
            return minimumSize();
    }

    int chars;
}
