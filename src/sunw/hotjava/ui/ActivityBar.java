// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ProgressDialog.java

package sunw.hotjava.ui;

import java.awt.*;

// Referenced classes of package sunw.hotjava.ui:
//            BarCanvas, BPSIndicator, BytesTransferredInd, ProgressDialog, 
//            ValueBar, ValueUpdateable

class ActivityBar extends BarCanvas
    implements ValueUpdateable
{

    ActivityBar(int i, int j, int k)
    {
        super(i, j);
    }

    public void updateValue(long l)
    {
        section = (section + 1) % 10;
        Graphics g = getGraphics();
        try
        {
            paint(g);
        }
        finally
        {
            g.dispose();
        }
    }

    public void paint(Graphics g)
    {
        if(g == null)
        {
            return;
        } else
        {
            drawWell(g);
            g.setColor(Color.blue);
            paintSections(g, section, section);
            g.setColor(Color.white);
            paintSections(g, 0, section - 1);
            paintSections(g, section + 1, 10);
            return;
        }
    }

    private void paintSections(Graphics g, int i, int j)
    {
        if(j < 0 || i >= 10)
            return;
        Dimension dimension = getPreferredSize();
        int k = dimension.height - 6;
        int l = dimension.width - 6;
        int i1 = (j + 1) - i;
        if(orientation == 1)
        {
            int j1 = k / 10;
            int l1 = 3 + i * j1;
            int j2 = j1 * i1;
            if(j == 10)
                j2 = (k - l1) + 3;
            g.fillRect(3, l1, l, j2);
            return;
        }
        int k1 = l / 10;
        int i2 = 3 + i * k1;
        int k2 = k1 * i1;
        if(j == 10)
            k2 = (l - i2) + 3;
        g.fillRect(i2, 3, k2, k);
    }

    private static final int totalSections = 10;
    private int section;
    private int orientation;
}
