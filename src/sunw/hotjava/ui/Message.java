// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   MessageLine.java

package sunw.hotjava.ui;

import java.awt.*;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.ui:
//            MessageLine

class Message extends Canvas
{

    public Message()
    {
        msg = "";
        fm = getToolkit().getFontMetrics(font);
        int i = fm.getHeight();
        setSize(1, i + 4);
    }

    void setText(String s)
    {
        msg = s != null ? s : "";
        repaint();
    }

    String getText()
    {
        return msg;
    }

    public Dimension getPreferredSize()
    {
        if(size == null)
            size = super.getPreferredSize();
        return size;
    }

    public void paint(Graphics g)
    {
        byte byte0 = 2;
        int i = 5 + byte0;
        g.setColor(getBackground());
        int j = getSize().height;
        int k = getSize().width;
        int l = getPreferredSize().height;
        k--;
        l--;
        g.draw3DRect(0, 0, k, j - 1, false);
        g.setColor(Color.black);
        g.setFont(font);
        FontMetrics fontmetrics = g.getFontMetrics();
        g.drawString(msg, i, (j + fontmetrics.getAscent()) / 2);
    }

    String msg;
    private static final boolean raised = false;
    private static final int borderWidth = 1;
    private static final int depth = 1;
    private static final int verticalOffset = 5;
    private static final int horizontalOffset = 5;
    private static HJBProperties properties;
    private static FontMetrics fm;
    private static Font font;
    private static final int vMargin = 4;
    private Dimension size;

    static 
    {
        properties = HJBProperties.getHJBProperties("hjbrowser");
        String s = properties.getProperty("hotjava.statusbar.font", "SansSerif-plain-10");
        font = Font.decode(s);
    }
}
