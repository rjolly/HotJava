// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ResizeTracker.java

package sunw.hotjava.tags;

import java.awt.*;

// Referenced classes of package sunw.hotjava.tags:
//            ResizeTracker

class RuleWindow extends Window
{

    RuleWindow(Frame frame)
    {
        super(frame);
        setCursor(Cursor.getPredefinedCursor(12));
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    public void paint(Graphics g)
    {
        Dimension dimension = getSize();
        g.drawRect(0, 0, dimension.width, dimension.height);
    }
}
