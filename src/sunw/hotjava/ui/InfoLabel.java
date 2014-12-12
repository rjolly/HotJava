// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Locator.java

package sunw.hotjava.ui;

import java.awt.*;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.ui:
//            InputField, Locator

class InfoLabel extends Panel
{

    public InfoLabel(Font font)
    {
        setFont(font);
    }

    public Dimension getPreferredSize()
    {
        Dimension dimension = super.getPreferredSize();
        dimension.width = Math.max(dimension.width, getFontMetrics(getFont()).stringWidth(label));
        dimension.height = Math.max(dimension.height, getFontMetrics(getFont()).getHeight());
        return dimension;
    }

    public Dimension getMinimumSize()
    {
        Dimension dimension = super.getMinimumSize();
        dimension.width = Math.max(dimension.width, getFontMetrics(getFont()).stringWidth(label));
        dimension.height = Math.max(dimension.height, getFontMetrics(getFont()).getHeight());
        return dimension;
    }

    public void paint(Graphics g)
    {
        g.drawString(label, 0, 19);
    }

    static final String label = HJBProperties.getHJBProperties("hjbrowser").getProperty("hotjava.uri.text");

}
