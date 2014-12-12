// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   NPanel.java

package sunw.hotjava.ui;

import java.awt.*;

public class NPanel extends Panel
{

    public NPanel(int i)
    {
        where = i;
    }

    public void paint(Graphics g)
    {
        Dimension dimension = size();
        g.setColor(Color.black);
        switch(where)
        {
        case 0: // '\0'
            g.drawLine(2, 2, dimension.width - 2, 2);
            return;

        case 1: // '\001'
            g.drawLine(2, dimension.height - 2, dimension.width - 2, dimension.height - 2);
            return;

        case 2: // '\002'
            g.drawLine(2, 2, 2, dimension.height - 2);
            return;

        case 3: // '\003'
            g.drawLine(dimension.width - 2, 2, dimension.width - 2, dimension.height - 2);
            return;
        }
    }

    public static final int TOP = 0;
    public static final int BOTTOM = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    public static final int NO = -1;
    private int where;
}
