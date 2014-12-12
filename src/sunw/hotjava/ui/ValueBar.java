// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ProgressDialog.java

package sunw.hotjava.ui;

import java.awt.*;

// Referenced classes of package sunw.hotjava.ui:
//            BarCanvas, ActivityBar, BPSIndicator, BytesTransferredInd, 
//            ProgressDialog, ValueUpdateable

class ValueBar extends BarCanvas
    implements ValueUpdateable
{

    ValueBar(int i, int j, int k)
    {
        super(i, j);
        maxValue = 1L;
        fullColor = Color.blue;
        emptyColor = Color.white;
        super.orientation = k;
    }

    public void updateValue(long l)
    {
        value = l;
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

    void setMaxValue(long l)
    {
        maxValue = l;
        repaint(10L);
    }

    void setMinValue(long l)
    {
        minValue = l;
        repaint(10L);
    }

    Color getFullColor()
    {
        return fullColor;
    }

    void setFullColor(Color color)
    {
        fullColor = color;
        repaint(10L);
    }

    Color getEmptyColor()
    {
        return emptyColor;
    }

    void setEmptyColor(Color color)
    {
        emptyColor = color;
        repaint(10L);
    }

    public void paint(Graphics g)
    {
        if(maxValue <= minValue || g == null)
            return;
        drawWell(g);
        double d = (double)(value - minValue) / (double)(maxValue - minValue);
        Dimension dimension = getPreferredSize();
        int i = dimension.height - 6;
        int j = dimension.width - 6;
        if(super.orientation == 1)
        {
            int k = i - (int)(d * (double)i);
            if(k - 1 > 0)
            {
                g.setColor(emptyColor);
                g.fillRect(3, 3, j, k - 1);
            }
            if(k < i)
            {
                g.setColor(fullColor);
                g.fillRect(3, 3 + k, j, i - k);
                return;
            }
        } else
        {
            int l = (int)(d * (double)j);
            g.setColor(fullColor);
            g.fillRect(3, 3, l, i);
            if(l + 1 < j)
            {
                g.setColor(emptyColor);
                g.fillRect(3 + l + 1, 3, j - l - 1, i);
            }
        }
    }

    private long maxValue;
    private long minValue;
    private long value;
    private Color fullColor;
    private Color emptyColor;
}
