// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   RaisedPanel.java

package sunw.hotjava.ui;

import java.awt.*;

// Referenced classes of package sunw.hotjava.ui:
//            HJColor

public class RaisedPanel extends Panel
{

    public Insets insets()
    {
        return new Insets(2, 2, 2, 2);
    }

    public void paint(Graphics g)
    {
        Dimension dimension = size();
        g.setColor(getVisible3DColor(getBackground()));
        g.draw3DRect(0, 0, dimension.width - 1, dimension.height - 1, true);
        g.draw3DRect(1, 1, dimension.width - 3, dimension.height - 3, true);
        g.fillRect(2, 2, dimension.width - 4, dimension.height - 4);
    }

    private static Color getVisible3DColor(Color color)
    {
        HJColor hjcolor = new HJColor(color);
        HJColor hjcolor1 = (HJColor)hjcolor.brighter();
        if(hjcolor1.similar(hjcolor))
            return hjcolor.brighter(-0.4F);
        HJColor hjcolor2 = (HJColor)hjcolor.darker();
        if(hjcolor2.similar(hjcolor))
            return hjcolor.brighter(0.4F);
        else
            return hjcolor;
    }

    public RaisedPanel()
    {
    }
}
