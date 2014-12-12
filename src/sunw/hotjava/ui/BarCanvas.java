// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ProgressDialog.java

package sunw.hotjava.ui;

import java.awt.*;

// Referenced classes of package sunw.hotjava.ui:
//            ActivityBar, BPSIndicator, BytesTransferredInd, ProgressDialog, 
//            ValueBar, ValueUpdateable

class BarCanvas extends Canvas
{

    BarCanvas(int i, int j)
    {
        setSize(i, j);
        width = i;
        height = j;
    }

    public Dimension getPreferredSize()
    {
        return new Dimension(width, height);
    }

    public Dimension getMinimumSize()
    {
        return new Dimension(width, height);
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    protected void drawWell(Graphics g)
    {
        g.setColor(Color.gray);
        for(int i = 0; i < 3; i++)
            g.draw3DRect(i, i, width - 2 * i - 1, height - 2 * i - 1, false);

    }

    static final int HORIZONTAL = 0;
    static final int VERTICAL = 1;
    protected static final int border = 3;
    protected int orientation;
    private int width;
    private int height;
}
